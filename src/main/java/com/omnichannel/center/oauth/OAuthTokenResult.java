package com.omnichannel.center.oauth;

public class OAuthTokenResult {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String channelShopId;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getChannelShopId() {
        return channelShopId;
    }

    public void setChannelShopId(String channelShopId) {
        this.channelShopId = channelShopId;
    }
}
