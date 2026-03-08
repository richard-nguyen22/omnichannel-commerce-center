package com.omnichannel.center.application.auth;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.config.AppProperties;
import com.omnichannel.center.domain.auth.auth_refresh_token;
import com.omnichannel.center.domain.user.UserStatus;
import com.omnichannel.center.domain.user.user_login;
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

        user_login user = new user_login();
        user.setGuid(UUID.randomUUID());
        user.setUserEmail(email);
        user.setUserName(rawFullName.trim());
        user.setPassword(rawPassword);
        user.setPasswordHash(passwordService.hash(rawPassword));
        user.setStatus(UserStatus.ACTIVE);

        try {
            authRepository.createUser(user);
        } catch (DuplicateKeyException ex) {
            throw new ApiException(409, "Email already exists");
        }

        return createTokenResponse(user);
    }

    public AuthResult login(String rawEmail, String rawPassword) {
        String email = rawEmail.trim().toLowerCase(Locale.ROOT);
        user_login user = authRepository.findUserByEmail(email)
                .orElseThrow(() -> new ApiException(401, "Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
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

        auth_refresh_token token = authRepository.findActiveRefreshTokenByHash(refreshTokenHash)
                .orElseThrow(() -> new ApiException(401, "Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            authRepository.revokeRefreshToken(token.getId());
            throw new ApiException(401, "Refresh token expired");
        }

        user_login user = authRepository.findUserByGuid(token.getUserGuid())
                .orElseThrow(() -> new ApiException(401, "Invalid refresh token"));

        authRepository.revokeRefreshToken(token.getId());
        return createTokenResponse(user);
    }

    public void logout(String rawRefreshToken) {
        String refreshTokenHash = refreshTokenCodec.sha256Hex(rawRefreshToken);
        authRepository.findActiveRefreshTokenByHash(refreshTokenHash)
                .ifPresent(token -> authRepository.revokeRefreshToken(token.getId()));
    }

    private AuthResult createTokenResponse(user_login user) {
        long accessTokenMinutes = appProperties.getAuth().getAccessTokenMinutes();
        long refreshTokenDays = appProperties.getAuth().getRefreshTokenDays();

        String accessToken = jwtService.createAccessToken(user, accessTokenMinutes);

        String rawRefreshToken = refreshTokenCodec.generateRawToken();
        String refreshTokenHash = refreshTokenCodec.sha256Hex(rawRefreshToken);

        auth_refresh_token refreshToken = new auth_refresh_token();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUserGuid(user.getGuid());
        refreshToken.setTokenHash(refreshTokenHash);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenDays * 24 * 60 * 60));
        authRepository.createRefreshToken(refreshToken);

        AuthResult response = new AuthResult();
        response.setAccessToken(accessToken);
        response.setAccessTokenExpiresInSeconds(accessTokenMinutes * 60);
        response.setRefreshToken(rawRefreshToken);

        AuthResult.UserProfile profile = new AuthResult.UserProfile();
        profile.setId(user.getGuid().toString());
        profile.setEmail(user.getUserEmail());
        profile.setFullName(user.getUserName());
        response.setUser(profile);

        return response;
    }
}
