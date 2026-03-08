package com.omnichannel.center.integration.oauth.providers;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.config.OAuthChannelConfig;
import com.omnichannel.center.domain.oauth.OAuthTokenResult;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public abstract class BaseOAuthProvider {
    protected final OAuthChannelConfig config;
    protected final RestClient restClient;

    protected BaseOAuthProvider(OAuthChannelConfig config, RestClient.Builder restClientBuilder) {
        this.config = config;
        this.restClient = restClientBuilder.build();
    }

    protected String defaultAuthorizeUrl(String state, String redirectUri) {
        assertConfigured();

        StringJoiner query = new StringJoiner("&");
        query.add("client_id=" + encode(config.getClientId()));
        query.add("response_type=code");
        query.add("redirect_uri=" + encode(redirectUri));
        query.add("state=" + encode(state));
        if (config.getScopes() != null && !config.getScopes().isEmpty()) {
            query.add("scope=" + encode(String.join(" ", config.getScopes())));
        }

        return config.getAuthUrl() + "?" + query;
    }

    protected OAuthTokenResult defaultExchangeCode(String code, String redirectUri, String channelName) {
        assertConfigured();

        Map<String, Object> payload = new HashMap<>();
        payload.put("client_id", config.getClientId());
        payload.put("client_secret", config.getClientSecret());
        payload.put("code", code);
        payload.put("redirect_uri", redirectUri);
        payload.put("grant_type", "authorization_code");

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri(config.getTokenUrl())
                .body(payload)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new ApiException(502, channelName + " token exchange returned empty response");
        }

        String accessToken = stringValue(response.get("access_token"));
        if (accessToken == null) {
            accessToken = stringValue(response.get("accessToken"));
        }

        if (accessToken == null) {
            throw new ApiException(502, channelName + " token response missing access token");
        }

        OAuthTokenResult result = new OAuthTokenResult();
        result.setAccessToken(accessToken);
        result.setRefreshToken(firstNonNull(stringValue(response.get("refresh_token")), stringValue(response.get("refreshToken"))));
        result.setExpiresIn(longValue(firstNonNull(response.get("expires_in"), response.get("expiresIn"))));
        return result;
    }

    protected void assertConfigured() {
        if (isBlank(config.getClientId()) || isBlank(config.getClientSecret()) || isBlank(config.getAuthUrl()) || isBlank(config.getTokenUrl())) {
            throw new ApiException(500, "OAuth config is incomplete");
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Long longValue(Object value) {
        if (value == null) return null;
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static <T> T firstNonNull(T first, T second) {
        return first != null ? first : second;
    }
}
