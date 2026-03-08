package com.omnichannel.center.oauth;

import com.omnichannel.center.channelaccount.ChannelAccount;
import com.omnichannel.center.channelaccount.ChannelAccountRepository;
import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.config.AppProperties;
import com.omnichannel.center.config.ChannelCode;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuthService {
    private final Map<ChannelCode, OAuthProvider> providerMap = new EnumMap<>(ChannelCode.class);
    private final OAuthStateService stateService;
    private final ChannelAccountRepository channelAccountRepository;
    private final AppProperties appProperties;

    public OAuthService(List<OAuthProvider> providers,
                        OAuthStateService stateService,
                        ChannelAccountRepository channelAccountRepository,
                        AppProperties appProperties) {
        for (OAuthProvider provider : providers) {
            providerMap.put(provider.channel(), provider);
        }
        this.stateService = stateService;
        this.channelAccountRepository = channelAccountRepository;
        this.appProperties = appProperties;
    }

    public String buildAuthorizeUrl(ChannelCode channel, String tenantId, String callbackPath) {
        OAuthProvider provider = providerOf(channel);

        String finalCallbackPath = (callbackPath == null || callbackPath.isBlank())
                ? "/oauth/" + channel.name().toLowerCase() + "/callback"
                : callbackPath;

        String redirectUri = appProperties.getBaseUrl() + finalCallbackPath;

        OAuthStatePayload statePayload = new OAuthStatePayload();
        statePayload.setTenantId(tenantId);
        statePayload.setChannel(channel);
        statePayload.setRedirectUri(redirectUri);
        statePayload.setNonce(UUID.randomUUID().toString());

        String state = stateService.serialize(statePayload);
        return provider.buildAuthorizeUrl(state, redirectUri);
    }

    public ChannelAccount handleCallback(ChannelCode channel, String code, String state) {
        OAuthStatePayload parsedState = stateService.deserialize(state);
        if (parsedState.getChannel() != channel) {
            throw new ApiException(400, "OAuth state does not match callback channel");
        }

        OAuthProvider provider = providerOf(channel);
        OAuthTokenResult token = provider.exchangeCode(code, parsedState.getRedirectUri());

        ChannelAccount account = new ChannelAccount();
        account.setTenantId(parsedState.getTenantId());
        account.setChannel(channel);
        account.setAccessToken(token.getAccessToken());
        account.setRefreshToken(token.getRefreshToken());
        account.setChannelShopId(token.getChannelShopId());

        if (token.getExpiresIn() != null) {
            account.setAccessTokenExpiresAt(Instant.now().plusSeconds(token.getExpiresIn()));
        }

        return channelAccountRepository.upsert(account);
    }

    private OAuthProvider providerOf(ChannelCode channel) {
        OAuthProvider provider = providerMap.get(channel);
        if (provider == null) {
            throw new ApiException(400, "Unsupported channel: " + channel);
        }
        return provider;
    }
}
