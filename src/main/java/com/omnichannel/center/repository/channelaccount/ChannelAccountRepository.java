package com.omnichannel.center.repository.channelaccount;

import com.omnichannel.center.config.ChannelCode;
import com.omnichannel.center.domain.channelaccount.ChannelAccount;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChannelAccountRepository {
    private final ConcurrentHashMap<UUID, ChannelAccount> storage = new ConcurrentHashMap<>();

    public ChannelAccount upsert(ChannelAccount input) {
        Optional<ChannelAccount> existing = storage.values().stream()
                .filter(account -> account.getTenantId().equals(input.getTenantId()) && account.getChannel() == input.getChannel())
                .findFirst();

        if (existing.isPresent()) {
            ChannelAccount current = existing.get();
            current.setAccessToken(input.getAccessToken());
            current.setRefreshToken(input.getRefreshToken());
            current.setAccessTokenExpiresAt(input.getAccessTokenExpiresAt());
            current.setChannelShopId(input.getChannelShopId());
            storage.put(current.getId(), current);
            return current;
        }

        input.setId(UUID.randomUUID());
        input.setConnectedAt(Instant.now());
        storage.put(input.getId(), input);
        return input;
    }

    public List<ChannelAccount> findByTenant(String tenantId) {
        List<ChannelAccount> result = new ArrayList<>();
        for (ChannelAccount account : storage.values()) {
            if (tenantId.equals(account.getTenantId())) {
                result.add(account);
            }
        }
        return result;
    }

    public Optional<ChannelAccount> findByTenantAndChannel(String tenantId, ChannelCode channelCode) {
        return storage.values().stream()
                .filter(account -> account.getTenantId().equals(tenantId) && account.getChannel() == channelCode)
                .findFirst();
    }
}
