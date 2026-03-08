package com.omnichannel.center.application.liquibase;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.integration.liquibase.LiquibaseExecutionResult;
import com.omnichannel.center.integration.liquibase.LiquibaseStatusResult;
import com.omnichannel.center.integration.liquibase.TenantLiquibaseIntegration;
import com.omnichannel.center.repository.clienttenant.client_tenant_hdr;
import com.omnichannel.center.repository.clienttenant.ClientTenantHeaderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiquibaseDeploymentApplicationService {
    private final ClientTenantHeaderRepository tenantHeaderRepository;
    private final TenantLiquibaseIntegration tenantLiquibaseIntegration;

    public LiquibaseDeploymentApplicationService(ClientTenantHeaderRepository tenantHeaderRepository,
                                                 TenantLiquibaseIntegration tenantLiquibaseIntegration) {
        this.tenantHeaderRepository = tenantHeaderRepository;
        this.tenantLiquibaseIntegration = tenantLiquibaseIntegration;
    }

    public List<LiquibaseExecutionResult> runForAllActiveTenants() {
        List<client_tenant_hdr> tenants = activeTenants();
        return tenants.stream()
                .map(tenantLiquibaseIntegration::runTenantMigration)
                .toList();
    }

    public LiquibaseExecutionResult runForTenant(String tenantCode) {
        client_tenant_hdr tenant = activeTenant(tenantCode);
        return tenantLiquibaseIntegration.runTenantMigration(tenant);
    }

    public List<LiquibaseStatusResult> statusForAllActiveTenants() {
        List<client_tenant_hdr> tenants = activeTenants();
        return tenants.stream()
                .map(tenantLiquibaseIntegration::readLatestStatus)
                .toList();
    }

    public LiquibaseStatusResult statusForTenant(String tenantCode) {
        client_tenant_hdr tenant = activeTenant(tenantCode);
        return tenantLiquibaseIntegration.readLatestStatus(tenant);
    }

    public LiquibaseExecutionResult clearChecksum(String tenantCode) {
        client_tenant_hdr tenant = activeTenant(tenantCode);
        return tenantLiquibaseIntegration.clearCheckSums(tenant);
    }

    private client_tenant_hdr activeTenant(String tenantCode) {
        return tenantHeaderRepository.findActiveByCode(tenantCode)
                .orElseThrow(() -> new ApiException(404, "Active tenant not found: " + tenantCode));
    }

    private List<client_tenant_hdr> activeTenants() {
        List<client_tenant_hdr> tenants = tenantHeaderRepository.findAllActive();
        if (tenants.isEmpty()) {
            throw new ApiException(404, "No ACTIVE tenant found in client_tenant_hdr");
        }
        return tenants;
    }
}
