package com.omnichannel.center.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl = "http://localhost:8888";
    private Auth auth = new Auth();
    private OAuth oauth = new OAuth();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {
        private String jwtSecret;
        private long accessTokenMinutes = 15;
        private long refreshTokenDays = 14;

        public String getJwtSecret() {
            return jwtSecret;
        }

        public void setJwtSecret(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }

        public long getAccessTokenMinutes() {
            return accessTokenMinutes;
        }

        public void setAccessTokenMinutes(long accessTokenMinutes) {
            this.accessTokenMinutes = accessTokenMinutes;
        }

        public long getRefreshTokenDays() {
            return refreshTokenDays;
        }

        public void setRefreshTokenDays(long refreshTokenDays) {
            this.refreshTokenDays = refreshTokenDays;
        }
    }

    public static class OAuth {
        private OAuthChannelConfig lazada = new OAuthChannelConfig();
        private OAuthChannelConfig shopee = new OAuthChannelConfig();
        private OAuthChannelConfig tiktok = new OAuthChannelConfig();

        public OAuthChannelConfig getLazada() {
            return lazada;
        }

        public void setLazada(OAuthChannelConfig lazada) {
            this.lazada = lazada;
        }

        public OAuthChannelConfig getShopee() {
            return shopee;
        }

        public void setShopee(OAuthChannelConfig shopee) {
            this.shopee = shopee;
        }

        public OAuthChannelConfig getTiktok() {
            return tiktok;
        }

        public void setTiktok(OAuthChannelConfig tiktok) {
            this.tiktok = tiktok;
        }
    }
}
