package com.omnichannel.center.integration.liquibase;

public class LiquibaseExecutionResult {
    private final String tenantCode;
    private final boolean success;
    private final String message;

    public LiquibaseExecutionResult(String tenantCode, boolean success, String message) {
        this.tenantCode = tenantCode;
        this.success = success;
        this.message = message;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
