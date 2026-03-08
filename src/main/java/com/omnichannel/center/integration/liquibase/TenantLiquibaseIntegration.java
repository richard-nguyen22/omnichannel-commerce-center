package com.omnichannel.center.integration.liquibase;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.repository.clienttenant.ClientTenantHeader;
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
    private static final String TENANT_CHANGELOG = "liquibase/tenant-user-bootstrap.sql";

    public LiquibaseExecutionResult runTenantMigration(ClientTenantHeader tenant) {
        try (Connection connection = open(tenant)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    TENANT_CHANGELOG,
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts(), new LabelExpression());
            return new LiquibaseExecutionResult(tenant.getCode(), true, "Liquibase executed");
        } catch (Exception ex) {
            return new LiquibaseExecutionResult(tenant.getCode(), false, ex.getMessage());
        }
    }

    public LiquibaseStatusResult readLatestStatus(ClientTenantHeader tenant) {
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
            return new LiquibaseStatusResult(tenant.getCode(), null, null, "Error: " + ex.getMessage());
        }
    }

    public LiquibaseExecutionResult clearCheckSums(ClientTenantHeader tenant) {
        try (Connection connection = open(tenant)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    TENANT_CHANGELOG,
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.clearCheckSums();
            return new LiquibaseExecutionResult(tenant.getCode(), true, "Liquibase checksum cleared");
        } catch (Exception ex) {
            return new LiquibaseExecutionResult(tenant.getCode(), false, ex.getMessage());
        }
    }

    private Connection open(ClientTenantHeader tenant) {
        try {
            return DriverManager.getConnection(tenant.getJdbcUrl(), tenant.getDbUsername(), tenant.getDbPassword());
        } catch (Exception ex) {
            throw new ApiException(500, "Cannot connect tenant database " + tenant.getCode() + ": " + ex.getMessage());
        }
    }
}
