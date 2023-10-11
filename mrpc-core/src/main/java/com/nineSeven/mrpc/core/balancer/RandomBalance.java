package com.nineSeven.mrpc.core.balancer;

import com.nineSeven.mrpc.core.common.ServiceInfo;

import java.util.List;
import java.util.Random;

public class RandomBalance implements BalancePolicy{

    private static Random random = new Random();

    @Override
    public ServiceInfo getService(List<ServiceInfo> services) {
        return services.get(random.nextInt(services.size()));
    }
}
