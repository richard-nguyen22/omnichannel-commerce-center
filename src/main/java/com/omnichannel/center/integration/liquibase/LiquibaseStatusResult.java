package com.omnichannel.center.integration.liquibase;

import java.time.Instant;

public class LiquibaseStatusResult {
    private final String tenantCode;
    private final String latestChangeSet;
    private final Instant executedAt;
    private final String description;

    public LiquibaseStatusResult(String tenantCode, String latestChangeSet, Instant executedAt, String description) {
        this.tenantCode = tenantCode;
        this.latestChangeSet = latestChangeSet;
        this.executedAt = executedAt;
        this.description = description;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public String getLatestChangeSet() {
        return latestChangeSet;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public String getDescription() {
        return description;
    }
}
