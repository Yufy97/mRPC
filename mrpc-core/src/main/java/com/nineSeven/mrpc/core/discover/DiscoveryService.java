package com.nineSeven.mrpc.core.discover;

import com.nineSeven.mrpc.core.common.ServiceInfo;

public interface DiscoveryService {

    ServiceInfo discovery(String serviceName) throws Exception;
}
