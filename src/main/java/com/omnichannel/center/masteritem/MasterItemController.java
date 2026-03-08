package com.omnichannel.center.masteritem;

import com.omnichannel.center.common.ApiException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/master-items")
public class MasterItemController {
    private final MasterItemRepository repository;

    public MasterItemController(MasterItemRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public MasterItem create(@Valid @RequestBody CreateMasterItemRequest request) {
        return repository.create(request);
    }

    @GetMapping
    public List<MasterItem> listByTenant(@RequestParam String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new ApiException(400, "tenantId query is required");
        }
        return repository.findByTenant(tenantId);
    }

    @GetMapping("/{id}")
    public MasterItem getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ApiException(404, "Master item not found"));
    }

    @PutMapping("/{id}")
    public MasterItem update(@PathVariable UUID id, @RequestBody UpdateMasterItemRequest request) {
        return repository.update(id, request).orElseThrow(() -> new ApiException(404, "Master item not found"));
    }
}
