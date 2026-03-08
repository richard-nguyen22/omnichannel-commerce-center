package com.omnichannel.center.oauth.providers;

import com.omnichannel.center.config.AppProperties;
import com.omnichannel.center.config.ChannelCode;
import com.omnichannel.center.oauth.OAuthProvider;
import com.omnichannel.center.oauth.OAuthTokenResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ShopeeOAuthProvider extends BaseOAuthProvider implements OAuthProvider {
    public ShopeeOAuthProvider(AppProperties appProperties, RestClient.Builder restClientBuilder) {
        super(appProperties.getOauth().getShopee(), restClientBuilder);
    }

    @Override
    public ChannelCode channel() {
        return ChannelCode.SHOPEE;
    }

    @Override
    public String buildAuthorizeUrl(String state, String redirectUri) {
        return defaultAuthorizeUrl(state, redirectUri);
    }

    @Override
    public OAuthTokenResult exchangeCode(String code, String redirectUri) {
        return defaultExchangeCode(code, redirectUri, "Shopee");
    }
}
