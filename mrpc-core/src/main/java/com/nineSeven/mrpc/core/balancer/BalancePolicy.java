package com.nineSeven.mrpc.core.balancer;

import com.nineSeven.mrpc.core.common.ServiceInfo;

import java.util.List;

public interface BalancePolicy {
    ServiceInfo getService(List<ServiceInfo> services);
}
