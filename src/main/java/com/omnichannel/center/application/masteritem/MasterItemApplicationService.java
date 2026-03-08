package com.omnichannel.center.application.masteritem;

import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.domain.masteritem.MasterItem;
import com.omnichannel.center.repository.masteritem.MasterItemRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MasterItemApplicationService {
    private final MasterItemRepository masterItemRepository;

    public MasterItemApplicationService(MasterItemRepository masterItemRepository) {
        this.masterItemRepository = masterItemRepository;
    }

    public MasterItem create(CreateMasterItemCommand request) {
        MasterItem item = new MasterItem();
        item.setId(UUID.randomUUID());
        item.setTenantId(request.getTenantId());
        item.setSku(request.getSku());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setBrand(request.getBrand());
        item.setCategory(request.getCategory());
        item.setAttributes(request.getAttributes());
        item.setListings(request.getListings());
        item.setCreatedAt(Instant.now());
        item.setUpdatedAt(Instant.now());
        return masterItemRepository.create(item);
    }

    public List<MasterItem> listByTenant(String tenantId) {
        return masterItemRepository.findByTenant(tenantId);
    }

    public MasterItem getById(UUID id) {
        return masterItemRepository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Master item not found"));
    }

    public MasterItem update(UUID id, UpdateMasterItemCommand request) {
        MasterItem patch = new MasterItem();
        patch.setSku(request.getSku());
        patch.setTitle(request.getTitle());
        patch.setDescription(request.getDescription());
        patch.setBrand(request.getBrand());
        patch.setCategory(request.getCategory());
        patch.setAttributes(request.getAttributes());
        patch.setListings(request.getListings());

        return masterItemRepository.update(id, patch)
                .orElseThrow(() -> new ApiException(404, "Master item not found"));
    }
}
