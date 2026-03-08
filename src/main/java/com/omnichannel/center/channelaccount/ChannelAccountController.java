package com.omnichannel.center.channelaccount;

import com.omnichannel.center.common.ApiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/channel-accounts")
public class ChannelAccountController {
    private final ChannelAccountRepository repository;

    public ChannelAccountController(ChannelAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ChannelAccount> listByTenant(@RequestParam String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new ApiException(400, "tenantId query is required");
        }
        return repository.findByTenant(tenantId);
    }
}
