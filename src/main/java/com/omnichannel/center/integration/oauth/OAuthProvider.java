package com.omnichannel.center.integration.oauth;

import com.omnichannel.center.config.ChannelCode;
import com.omnichannel.center.domain.oauth.OAuthTokenResult;

public interface OAuthProvider {
    ChannelCode channel();

    String buildAuthorizeUrl(String state, String redirectUri);

    OAuthTokenResult exchangeCode(String code, String redirectUri);
}
