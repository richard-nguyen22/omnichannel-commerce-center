package com.omnichannel.center.oauth;

import com.omnichannel.center.config.ChannelCode;

public interface OAuthProvider {
    ChannelCode channel();

    String buildAuthorizeUrl(String state, String redirectUri);

    OAuthTokenResult exchangeCode(String code, String redirectUri);
}
