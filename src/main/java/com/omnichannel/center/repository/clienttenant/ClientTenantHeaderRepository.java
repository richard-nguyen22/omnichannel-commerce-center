package com.omnichannel.center.repository.clienttenant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClientTenantHeaderRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClientTenantHeaderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<client_tenant_hdr> findAllActive() {
        String sql = """
                SELECT guid, code, name, jdbc_url, db_username, db_password, status, created_date, updated_date
                FROM client_tenant_hdr
                WHERE status = 'ACTIVE'
                ORDER BY code
                """;

        return jdbcTemplate.query(sql, rowMapper());
    }

    public Optional<client_tenant_hdr> findActiveByCode(String code) {
        String sql = """
                SELECT guid, code, name, jdbc_url, db_username, db_password, status, created_date, updated_date
                FROM client_tenant_hdr
                WHERE code = ?
                  AND status = 'ACTIVE'
                """;

        List<client_tenant_hdr> result = jdbcTemplate.query(sql, rowMapper(), code);
        return result.stream().findFirst();
    }

    private RowMapper<client_tenant_hdr> rowMapper() {
        return (rs, rowNum) -> {
            client_tenant_hdr header = new client_tenant_hdr();
            header.setGuid((UUID) rs.getObject("guid"));
            header.setCode(rs.getString("code"));
            header.setName(rs.getString("name"));
            header.setJdbcUrl(rs.getString("jdbc_url"));
            header.setDbUsername(rs.getString("db_username"));
            header.setDbPassword(rs.getString("db_password"));
            header.setStatus(ClientTenantStatus.valueOf(rs.getString("status")));
            header.setCreatedDate(toInstant(rs.getTimestamp("created_date")));
            header.setUpdatedDate(toInstant(rs.getTimestamp("updated_date")));
            return header;
        };
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
