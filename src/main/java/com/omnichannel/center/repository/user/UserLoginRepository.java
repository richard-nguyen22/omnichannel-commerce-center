package com.omnichannel.center.repository.user;

import com.omnichannel.center.domain.user.UserLogin;
import com.omnichannel.center.domain.user.UserStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserLoginRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserLoginRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserLogin insert(UserLogin input) {
        String sql = """
                INSERT INTO user_login
                (guid, user_email, user_name, password, password_hash, status, created_date, updated_date)
                VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())
                """;

        jdbcTemplate.update(sql,
                input.getGuid(),
                input.getUserEmail(),
                input.getUserName(),
                input.getPassword(),
                input.getPasswordHash(),
                input.getStatus().name());

        return findByGuid(input.getGuid()).orElseThrow();
    }

    public Optional<UserLogin> findByGuid(UUID guid) {
        String sql = """
                SELECT guid, user_email, user_name, password, password_hash, status, created_date, updated_date
                FROM user_login
                WHERE guid = ?
                """;

        List<UserLogin> result = jdbcTemplate.query(sql, rowMapper(), guid);
        return result.stream().findFirst();
    }

    public Optional<UserLogin> findActiveByEmail(String userEmail) {
        String sql = """
                SELECT guid, user_email, user_name, password, password_hash, status, created_date, updated_date
                FROM user_login
                WHERE user_email = ?
                  AND status = 'ACTIVE'
                """;

        List<UserLogin> result = jdbcTemplate.query(sql, rowMapper(), userEmail);
        return result.stream().findFirst();
    }

    public List<UserLogin> findByStatus(UserStatus status) {
        String sql = """
                SELECT guid, user_email, user_name, password, password_hash, status, created_date, updated_date
                FROM user_login
                WHERE status = ?
                ORDER BY created_date DESC
                """;

        return jdbcTemplate.query(sql, rowMapper(), status.name());
    }

    private RowMapper<UserLogin> rowMapper() {
        return (rs, rowNum) -> {
            UserLogin user = new UserLogin();
            user.setGuid((UUID) rs.getObject("guid"));
            user.setUserEmail(rs.getString("user_email"));
            user.setUserName(rs.getString("user_name"));
            user.setPassword(rs.getString("password"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setStatus(UserStatus.valueOf(rs.getString("status")));
            user.setCreatedDate(toInstant(rs.getTimestamp("created_date")));
            user.setUpdatedDate(toInstant(rs.getTimestamp("updated_date")));
            return user;
        };
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
