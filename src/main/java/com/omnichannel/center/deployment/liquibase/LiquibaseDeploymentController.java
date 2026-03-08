package com.omnichannel.center.deployment.liquibase;

import com.omnichannel.center.application.liquibase.LiquibaseDeploymentApplicationService;
import com.omnichannel.center.integration.liquibase.LiquibaseExecutionResult;
import com.omnichannel.center.integration.liquibase.LiquibaseStatusResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/deployment/liquibase"})
public class LiquibaseDeploymentController {
    private final LiquibaseDeploymentApplicationService applicationService;

    public LiquibaseDeploymentController(LiquibaseDeploymentApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/run")
    public List<LiquibaseExecutionResult> runAllActiveTenants() {
        return applicationService.runForAllActiveTenants();
    }

    @PostMapping("/run/{tenant_code}")
    public LiquibaseExecutionResult runSingleTenant(@PathVariable("tenant_code") String tenantCode) {
        return applicationService.runForTenant(tenantCode);
    }

    @GetMapping("/status")
    public List<LiquibaseStatusResult> statusAllActiveTenants() {
        return applicationService.statusForAllActiveTenants();
    }

    @GetMapping("/status/{tenant_code}")
    public LiquibaseStatusResult statusSingleTenant(@PathVariable("tenant_code") String tenantCode) {
        return applicationService.statusForTenant(tenantCode);
    }

    @PostMapping("/clear-checksum/{tenant_code}")
    public Map<String, Object> clearChecksum(@PathVariable("tenant_code") String tenantCode) {
        LiquibaseExecutionResult result = applicationService.clearChecksum(tenantCode);
        return Map.of(
                "tenantCode", result.getTenantCode(),
                "success", result.isSuccess(),
                "message", result.getMessage()
        );
    }
}
