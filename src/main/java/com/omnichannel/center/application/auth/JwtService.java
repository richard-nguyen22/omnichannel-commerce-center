package com.omnichannel.center.application.auth;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.config.AppProperties;
import com.omnichannel.center.domain.auth.AuthUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;

    public JwtService(AppProperties appProperties) {
        String secret = appProperties.getAuth().getJwtSecret();
        if (secret == null || secret.length() < 32) {
            throw new ApiException(500, "app.auth.jwt-secret must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(AuthUser user, long accessTokenMinutes) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTokenMinutes * 60);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }
}
