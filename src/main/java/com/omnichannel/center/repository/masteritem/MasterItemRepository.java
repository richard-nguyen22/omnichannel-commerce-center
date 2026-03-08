package com.omnichannel.center.repository.masteritem;

import com.omnichannel.center.domain.masteritem.MasterItem;
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

    public MasterItem create(MasterItem item) {
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

    public Optional<MasterItem> update(UUID id, MasterItem patch) {
        MasterItem item = storage.get(id);
        if (item == null) {
            return Optional.empty();
        }

        if (patch.getSku() != null) item.setSku(patch.getSku());
        if (patch.getTitle() != null) item.setTitle(patch.getTitle());
        if (patch.getDescription() != null) item.setDescription(patch.getDescription());
        if (patch.getBrand() != null) item.setBrand(patch.getBrand());
        if (patch.getCategory() != null) item.setCategory(patch.getCategory());
        if (patch.getAttributes() != null) item.setAttributes(patch.getAttributes());
        if (patch.getListings() != null) item.setListings(patch.getListings());
        item.setUpdatedAt(Instant.now());

        storage.put(item.getId(), item);
        return Optional.of(item);
    }
}
