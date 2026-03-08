package com.omnichannel.center.deployment.masteritem;

import com.omnichannel.center.application.masteritem.MasterItemApplicationService;
import com.omnichannel.center.application.masteritem.CreateMasterItemCommand;
import com.omnichannel.center.application.masteritem.UpdateMasterItemCommand;
import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.domain.masteritem.MasterItem;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/master-items")
public class MasterItemController {
    private final MasterItemApplicationService applicationService;

    public MasterItemController(MasterItemApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public MasterItem create(@Valid @RequestBody CreateMasterItemRequest request) {
        CreateMasterItemCommand command = new CreateMasterItemCommand();
        command.setTenantId(request.getTenantId());
        command.setSku(request.getSku());
        command.setTitle(request.getTitle());
        command.setDescription(request.getDescription());
        command.setBrand(request.getBrand());
        command.setCategory(request.getCategory());
        command.setAttributes(request.getAttributes());
        command.setListings(request.getListings());
        return applicationService.create(command);
    }

    @GetMapping
    public List<MasterItem> listByTenant(@RequestParam String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new ApiException(400, "tenantId query is required");
        }
        return applicationService.listByTenant(tenantId);
    }

    @GetMapping("/{id}")
    public MasterItem getById(@PathVariable UUID id) {
        return applicationService.getById(id);
    }

    @PutMapping("/{id}")
    public MasterItem update(@PathVariable UUID id, @RequestBody UpdateMasterItemRequest request) {
        UpdateMasterItemCommand command = new UpdateMasterItemCommand();
        command.setSku(request.getSku());
        command.setTitle(request.getTitle());
        command.setDescription(request.getDescription());
        command.setBrand(request.getBrand());
        command.setCategory(request.getCategory());
        command.setAttributes(request.getAttributes());
        command.setListings(request.getListings());
        return applicationService.update(id, command);
    }
}
