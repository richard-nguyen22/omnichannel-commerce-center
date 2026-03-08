package com.omnichannel.center.application.auth;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.config.AppProperties;
import com.omnichannel.center.domain.auth.AuthRefreshToken;
import com.omnichannel.center.domain.auth.AuthUser;
import com.omnichannel.center.repository.auth.AuthRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthApplicationService {
    private final AuthRepository authRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final RefreshTokenCodec refreshTokenCodec;
    private final AppProperties appProperties;

    public AuthApplicationService(AuthRepository authRepository,
                                  PasswordService passwordService,
                                  JwtService jwtService,
                                  RefreshTokenCodec refreshTokenCodec,
                                  AppProperties appProperties) {
        this.authRepository = authRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.refreshTokenCodec = refreshTokenCodec;
        this.appProperties = appProperties;
    }

    public AuthResult register(String rawEmail, String rawPassword, String rawFullName) {
        String email = rawEmail.trim().toLowerCase(Locale.ROOT);

        AuthUser user = new AuthUser();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordService.hash(rawPassword));
        user.setFullName(rawFullName.trim());
        user.setActive(true);

        try {
            authRepository.createUser(user);
        } catch (DuplicateKeyException ex) {
            throw new ApiException(409, "Email already exists");
        }

        return createTokenResponse(user);
    }

    public AuthResult login(String rawEmail, String rawPassword) {
        String email = rawEmail.trim().toLowerCase(Locale.ROOT);
        AuthUser user = authRepository.findUserByEmail(email)
                .orElseThrow(() -> new ApiException(401, "Invalid email or password"));

        if (!user.isActive()) {
            throw new ApiException(403, "User is inactive");
        }

        boolean matched = passwordService.matches(rawPassword, user.getPasswordHash());
        if (!matched) {
            throw new ApiException(401, "Invalid email or password");
        }

        return createTokenResponse(user);
    }

    public AuthResult refresh(String rawRefreshToken) {
        String refreshTokenHash = refreshTokenCodec.sha256Hex(rawRefreshToken);

        AuthRefreshToken token = authRepository.findActiveRefreshTokenByHash(refreshTokenHash)
                .orElseThrow(() -> new ApiException(401, "Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            authRepository.revokeRefreshToken(token.getId());
            throw new ApiException(401, "Refresh token expired");
        }

        AuthUser user = authRepository.findUserById(token.getUserId())
                .orElseThrow(() -> new ApiException(401, "Invalid refresh token"));

        authRepository.revokeRefreshToken(token.getId());
        return createTokenResponse(user);
    }

    public void logout(String rawRefreshToken) {
        String refreshTokenHash = refreshTokenCodec.sha256Hex(rawRefreshToken);
        authRepository.findActiveRefreshTokenByHash(refreshTokenHash)
                .ifPresent(token -> authRepository.revokeRefreshToken(token.getId()));
    }

    private AuthResult createTokenResponse(AuthUser user) {
        long accessTokenMinutes = appProperties.getAuth().getAccessTokenMinutes();
        long refreshTokenDays = appProperties.getAuth().getRefreshTokenDays();

        String accessToken = jwtService.createAccessToken(user, accessTokenMinutes);

        String rawRefreshToken = refreshTokenCodec.generateRawToken();
        String refreshTokenHash = refreshTokenCodec.sha256Hex(rawRefreshToken);

        AuthRefreshToken refreshToken = new AuthRefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUserId(user.getId());
        refreshToken.setTokenHash(refreshTokenHash);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenDays * 24 * 60 * 60));
        authRepository.createRefreshToken(refreshToken);

        AuthResult response = new AuthResult();
        response.setAccessToken(accessToken);
        response.setAccessTokenExpiresInSeconds(accessTokenMinutes * 60);
        response.setRefreshToken(rawRefreshToken);

        AuthResult.UserProfile profile = new AuthResult.UserProfile();
        profile.setId(user.getId().toString());
        profile.setEmail(user.getEmail());
        profile.setFullName(user.getFullName());
        response.setUser(profile);

        return response;
    }
}
