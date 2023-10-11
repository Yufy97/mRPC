package com.nineSeven.mrpc.core.balancer;

import com.nineSeven.mrpc.core.common.ServiceInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FullRoundBalance implements BalancePolicy{

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public ServiceInfo getService(List<ServiceInfo> services) {
        return services.get(index.getAndIncrement() % services.size());
    }
}
