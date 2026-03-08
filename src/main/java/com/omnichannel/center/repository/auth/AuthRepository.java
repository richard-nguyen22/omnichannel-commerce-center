package com.omnichannel.center.repository.auth;

import com.omnichannel.center.domain.auth.AuthRefreshToken;
import com.omnichannel.center.domain.auth.AuthUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthUser createUser(AuthUser input) {
        String sql = """
                INSERT INTO app_user (id, email, password_hash, full_name, is_active)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                input.getId(),
                input.getEmail(),
                input.getPasswordHash(),
                input.getFullName(),
                input.isActive());

        return findUserById(input.getId()).orElseThrow();
    }

    public Optional<AuthUser> findUserByEmail(String email) {
        String sql = """
                SELECT id, email, password_hash, full_name, is_active, created_at, updated_at
                FROM app_user
                WHERE email = ?
                """;

        List<AuthUser> users = jdbcTemplate.query(sql, userRowMapper(), email);
        return users.stream().findFirst();
    }

    public Optional<AuthUser> findUserById(UUID id) {
        String sql = """
                SELECT id, email, password_hash, full_name, is_active, created_at, updated_at
                FROM app_user
                WHERE id = ?
                """;
        List<AuthUser> users = jdbcTemplate.query(sql, userRowMapper(), id);
        return users.stream().findFirst();
    }

    public AuthRefreshToken createRefreshToken(AuthRefreshToken input) {
        String sql = """
                INSERT INTO auth_refresh_token (id, user_id, token_hash, expires_at)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                input.getId(),
                input.getUserId(),
                input.getTokenHash(),
                Timestamp.from(input.getExpiresAt()));

        return findActiveRefreshTokenByHash(input.getTokenHash()).orElseThrow();
    }

    public Optional<AuthRefreshToken> findActiveRefreshTokenByHash(String tokenHash) {
        String sql = """
                SELECT id, user_id, token_hash, expires_at, revoked_at, created_at
                FROM auth_refresh_token
                WHERE token_hash = ?
                  AND revoked_at IS NULL
                """;

        List<AuthRefreshToken> tokens = jdbcTemplate.query(sql, refreshTokenRowMapper(), tokenHash);
        return tokens.stream().findFirst();
    }

    public void revokeRefreshToken(UUID tokenId) {
        String sql = """
                UPDATE auth_refresh_token
                SET revoked_at = NOW()
                WHERE id = ?
                  AND revoked_at IS NULL
                """;

        jdbcTemplate.update(sql, tokenId);
    }

    private RowMapper<AuthUser> userRowMapper() {
        return (rs, rowNum) -> {
            AuthUser user = new AuthUser();
            user.setId((UUID) rs.getObject("id"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setFullName(rs.getString("full_name"));
            user.setActive(rs.getBoolean("is_active"));
            user.setCreatedAt(timestampToInstant(rs, "created_at"));
            user.setUpdatedAt(timestampToInstant(rs, "updated_at"));
            return user;
        };
    }

    private RowMapper<AuthRefreshToken> refreshTokenRowMapper() {
        return (rs, rowNum) -> {
            AuthRefreshToken token = new AuthRefreshToken();
            token.setId((UUID) rs.getObject("id"));
            token.setUserId((UUID) rs.getObject("user_id"));
            token.setTokenHash(rs.getString("token_hash"));
            token.setExpiresAt(timestampToInstant(rs, "expires_at"));
            token.setRevokedAt(timestampToInstant(rs, "revoked_at"));
            token.setCreatedAt(timestampToInstant(rs, "created_at"));
            return token;
        };
    }

    private Instant timestampToInstant(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts == null ? null : ts.toInstant();
    }
}
