package com.omnichannel.center.application.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.domain.oauth.OAuthStatePayload;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class OAuthStateService {
    private final ObjectMapper objectMapper;

    public OAuthStateService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(OAuthStatePayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new ApiException(500, "Failed to serialize OAuth state");
        }
    }

    public OAuthStatePayload deserialize(String rawState) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(rawState);
            OAuthStatePayload payload = objectMapper.readValue(decoded, OAuthStatePayload.class);
            if (payload.getTenantId() == null || payload.getTenantId().isBlank() || payload.getChannel() == null
                    || payload.getRedirectUri() == null || payload.getRedirectUri().isBlank()) {
                throw new ApiException(400, "Invalid OAuth state");
            }
            return payload;
        } catch (IllegalArgumentException | JsonProcessingException ex) {
            throw new ApiException(400, "Invalid OAuth state");
        } catch (Exception ex) {
            throw new RuntimeException("Invalid OAuth state", ex);
        }
    }
}
