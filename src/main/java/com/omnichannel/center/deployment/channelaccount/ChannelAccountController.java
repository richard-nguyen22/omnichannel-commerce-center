package com.omnichannel.center.deployment.channelaccount;

import com.omnichannel.center.application.channelaccount.ChannelAccountApplicationService;
import com.omnichannel.center.common.ApiException;
import com.omnichannel.center.domain.channelaccount.ChannelAccount;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/channel-accounts")
public class ChannelAccountController {
    private final ChannelAccountApplicationService applicationService;

    public ChannelAccountController(ChannelAccountApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<ChannelAccount> listByTenant(@RequestParam String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new ApiException(400, "tenantId query is required");
        }
        return applicationService.listByTenant(tenantId);
    }
}
