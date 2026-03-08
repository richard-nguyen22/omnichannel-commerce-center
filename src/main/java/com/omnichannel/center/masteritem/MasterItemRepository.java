package com.omnichannel.center.masteritem;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MasterItemRepository {
    private final ConcurrentHashMap<UUID, MasterItem> storage = new ConcurrentHashMap<>();

    public MasterItem create(CreateMasterItemRequest request) {
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

        storage.put(item.getId(), item);
        return item;
    }

    public List<MasterItem> findByTenant(String tenantId) {
        List<MasterItem> result = new ArrayList<>();
        for (MasterItem item : storage.values()) {
            if (tenantId.equals(item.getTenantId())) {
                result.add(item);
            }
        }
        return result;
    }

    public Optional<MasterItem> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<MasterItem> update(UUID id, UpdateMasterItemRequest request) {
        MasterItem item = storage.get(id);
        if (item == null) {
            return Optional.empty();
        }

        if (request.getSku() != null) item.setSku(request.getSku());
        if (request.getTitle() != null) item.setTitle(request.getTitle());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getBrand() != null) item.setBrand(request.getBrand());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getAttributes() != null) item.setAttributes(request.getAttributes());
        if (request.getListings() != null) item.setListings(request.getListings());
        item.setUpdatedAt(Instant.now());

        storage.put(item.getId(), item);
        return Optional.of(item);
    }
}
