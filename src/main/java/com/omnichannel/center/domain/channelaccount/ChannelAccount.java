package com.omnichannel.center.domain.channelaccount;

import com.omnichannel.center.config.ChannelCode;

import java.time.Instant;
import java.util.UUID;

public class ChannelAccount {
    private UUID id;
    private String tenantId;
    private ChannelCode channel;
    private String channelShopId;
    private String accessToken;
    private String refreshToken;
    private Instant accessTokenExpiresAt;
    private Instant connectedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getChannelShopId() {
        return channelShopId;
    }

    public void setChannelShopId(String channelShopId) {
        this.channelShopId = channelShopId;
    }

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

    public Instant getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(Instant accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public Instant getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(Instant connectedAt) {
        this.connectedAt = connectedAt;
    }
}
