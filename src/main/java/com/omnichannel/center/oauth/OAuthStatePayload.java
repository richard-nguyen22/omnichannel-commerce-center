package com.omnichannel.center.oauth;

import com.omnichannel.center.config.ChannelCode;

public class OAuthStatePayload {
    private String tenantId;
    private ChannelCode channel;
    private String redirectUri;
    private String nonce;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public ChannelCode getChannel() {
        return channel;
    }

    public void setChannel(ChannelCode channel) {
        this.channel = channel;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
