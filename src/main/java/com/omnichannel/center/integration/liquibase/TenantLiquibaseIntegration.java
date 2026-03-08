package com.omnichannel.center.integration.liquibase;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.repository.clienttenant.client_tenant_hdr;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;

@Component
public class TenantLiquibaseIntegration {
    private static final String MAIN_CHANGELOG = "liquibase/liquibase-changelog-001.sql";

    public LiquibaseExecutionResult runTenantMigration(client_tenant_hdr tenant) {
        try (Connection connection = open(tenant)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            runChangeLog(database, MAIN_CHANGELOG);
            return new LiquibaseExecutionResult(tenant.getCode(), true, "Liquibase executed (liquibase-changelog-001.sql)");
        } catch (Exception ex) {
            throw new ApiException(500, "Liquibase run failed for tenant " + tenant.getCode() + ": " + ex.getMessage());
        }
    }

    public LiquibaseStatusResult readLatestStatus(client_tenant_hdr tenant) {
        try (Connection connection = open(tenant);
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery("""
                    SELECT id, author, dateexecuted, description
                    FROM databasechangelog
                    ORDER BY dateexecuted DESC, orderexecuted DESC
                    LIMIT 1
                    """);

            if (!rs.next()) {
                return new LiquibaseStatusResult(tenant.getCode(), null, null, "No changelog executed");
            }

            String id = rs.getString("id");
            String author = rs.getString("author");
            Instant executedAt = rs.getTimestamp("dateexecuted").toInstant();
            String description = rs.getString("description");

            return new LiquibaseStatusResult(tenant.getCode(), author + ":" + id, executedAt, description);
        } catch (Exception ex) {
            throw new ApiException(500, "Liquibase status failed for tenant " + tenant.getCode() + ": " + ex.getMessage());
        }
    }

    public LiquibaseExecutionResult clearCheckSums(client_tenant_hdr tenant) {
        try (Connection connection = open(tenant)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            clearChecksum(database, MAIN_CHANGELOG);
            return new LiquibaseExecutionResult(tenant.getCode(), true, "Liquibase checksum cleared");
        } catch (Exception ex) {
            throw new ApiException(500, "Liquibase clear checksum failed for tenant " + tenant.getCode() + ": " + ex.getMessage());
        }
    }

    private void runChangeLog(Database database, String changeLogPath) throws Exception {
        Liquibase liquibase = new Liquibase(
                changeLogPath,
                new ClassLoaderResourceAccessor(),
                database
        );
        liquibase.update(new Contexts(), new LabelExpression());
    }

    private void clearChecksum(Database database, String changeLogPath) throws Exception {
        Liquibase liquibase = new Liquibase(
                changeLogPath,
                new ClassLoaderResourceAccessor(),
                database
        );
        liquibase.clearCheckSums();
    }

    private Connection open(client_tenant_hdr tenant) {
        try {
            return DriverManager.getConnection(tenant.getJdbcUrl(), tenant.getDbUsername(), tenant.getDbPassword());
        } catch (Exception ex) {
            throw new ApiException(500, "Cannot connect tenant database " + tenant.getCode() + ": " + ex.getMessage());
        }
    }
}
