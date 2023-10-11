package com.nineSeven.mrpc.core.discover;

import com.nineSeven.mrpc.core.balancer.BalancePolicy;
import com.nineSeven.mrpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class ZkDiscoverService implements DiscoveryService{

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    private BalancePolicy balancePolicy;

    public ZkDiscoverService(DiscoverConfig discoverConfig, BalancePolicy balancePolicy) {
        this.balancePolicy = balancePolicy;

        try {
            CuratorFramework client = CuratorFrameworkFactory.newClient(discoverConfig.getDiscoverAddr(),
                    new ExponentialBackoffRetry(discoverConfig.getBaseSleepTime(), discoverConfig.getMaxRetries()));
            client.start();
            JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(serializer)
                    .basePath(discoverConfig.getBasePath())
                    .build();
            this.serviceDiscovery.start();
        } catch (Exception e) {
            log.error("serviceDiscovery start error :{}", e);
        }
    }

    @Override
    public ServiceInfo discovery(String serviceName) throws Exception {
        Collection<ServiceInstance<ServiceInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        return serviceInstances == null ? null : balancePolicy.getService(serviceInstances.stream()
                .map(ServiceInstance :: getPayload).collect(Collectors.toList()));
    }
}
