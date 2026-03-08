package com.omnichannel.center.repository.auth;

import com.omnichannel.center.domain.auth.auth_refresh_token;
import com.omnichannel.center.domain.user.UserStatus;
import com.omnichannel.center.domain.user.user_login;
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

    public user_login createUser(user_login input) {
        String sql = """
                INSERT INTO user_login (guid, user_email, user_name, password, password_hash, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                input.getGuid(),
                input.getUserEmail(),
                input.getUserName(),
                input.getPassword(),
                input.getPasswordHash(),
                input.getStatus().name());

        return findUserByGuid(input.getGuid()).orElseThrow();
    }

    public Optional<user_login> findUserByEmail(String email) {
        String sql = """
                SELECT guid, user_email, user_name, password, password_hash, status, created_date, updated_date
                FROM user_login
                WHERE user_email = ?
                """;

        List<user_login> users = jdbcTemplate.query(sql, userRowMapper(), email);
        return users.stream().findFirst();
    }

    public Optional<user_login> findUserByGuid(UUID guid) {
        String sql = """
                SELECT guid, user_email, user_name, password, password_hash, status, created_date, updated_date
                FROM user_login
                WHERE guid = ?
                """;
        List<user_login> users = jdbcTemplate.query(sql, userRowMapper(), guid);
        return users.stream().findFirst();
    }

    public auth_refresh_token createRefreshToken(auth_refresh_token input) {
        String sql = """
                INSERT INTO auth_refresh_token (id, user_guid, token_hash, expires_at)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                input.getId(),
                input.getUserGuid(),
                input.getTokenHash(),
                Timestamp.from(input.getExpiresAt()));

        return findActiveRefreshTokenByHash(input.getTokenHash()).orElseThrow();
    }

    public Optional<auth_refresh_token> findActiveRefreshTokenByHash(String tokenHash) {
        String sql = """
                SELECT id, user_guid, token_hash, expires_at, revoked_at, created_at
                FROM auth_refresh_token
                WHERE token_hash = ?
                  AND revoked_at IS NULL
                """;

        List<auth_refresh_token> tokens = jdbcTemplate.query(sql, refreshTokenRowMapper(), tokenHash);
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

    private RowMapper<user_login> userRowMapper() {
        return (rs, rowNum) -> {
            user_login user = new user_login();
            user.setGuid((UUID) rs.getObject("guid"));
            user.setUserEmail(rs.getString("user_email"));
            user.setUserName(rs.getString("user_name"));
            user.setPassword(rs.getString("password"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setStatus(UserStatus.valueOf(rs.getString("status")));
            user.setCreatedDate(timestampToInstant(rs, "created_date"));
            user.setUpdatedDate(timestampToInstant(rs, "updated_date"));
            return user;
        };
    }

    private RowMapper<auth_refresh_token> refreshTokenRowMapper() {
        return (rs, rowNum) -> {
            auth_refresh_token token = new auth_refresh_token();
            token.setId((UUID) rs.getObject("id"));
            token.setUserGuid((UUID) rs.getObject("user_guid"));
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
