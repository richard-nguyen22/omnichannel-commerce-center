package com.omnichannel.center.deployment.oauth;

import com.omnichannel.center.application.oauth.OAuthApplicationService;
import com.omnichannel.center.domain.channelaccount.ChannelAccount;
import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.config.ChannelCode;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
@Validated
public class OAuthController {
    private final OAuthApplicationService oAuthService;

    public OAuthController(OAuthApplicationService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/{channel}/authorize")
    public Map<String, Object> authorize(@PathVariable String channel,
                                         @RequestParam @NotBlank String tenantId,
                                         @RequestParam(required = false) String callbackPath) {
        ChannelCode channelCode = parseChannel(channel);
        String authorizeUrl = oAuthService.buildAuthorizeUrl(channelCode, tenantId, callbackPath);
        return Map.of("authorizeUrl", authorizeUrl);
    }

    @GetMapping("/{channel}/callback")
    public Map<String, Object> callback(@PathVariable String channel,
                                        @RequestParam @NotBlank String code,
                                        @RequestParam @NotBlank String state) {
        ChannelCode channelCode = parseChannel(channel);
        ChannelAccount account = oAuthService.handleCallback(channelCode, code, state);
        return Map.of(
                "message", channel + " connected",
                "account", account
        );
    }

    private ChannelCode parseChannel(String raw) {
        try {
            return ChannelCode.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException(400, "Unsupported channel: " + raw);
        }
    }
}
