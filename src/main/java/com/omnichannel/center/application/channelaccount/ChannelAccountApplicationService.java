package com.omnichannel.center.application.channelaccount;

import com.omnichannel.center.domain.channelaccount.ChannelAccount;
import com.omnichannel.center.repository.channelaccount.ChannelAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelAccountApplicationService {
    private final ChannelAccountRepository channelAccountRepository;

    public ChannelAccountApplicationService(ChannelAccountRepository channelAccountRepository) {
        this.channelAccountRepository = channelAccountRepository;
    }

    public List<ChannelAccount> listByTenant(String tenantId) {
        return channelAccountRepository.findByTenant(tenantId);
    }
}
