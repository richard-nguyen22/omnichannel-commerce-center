package com.omnichannel.center.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl = "http://localhost:8080";
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
