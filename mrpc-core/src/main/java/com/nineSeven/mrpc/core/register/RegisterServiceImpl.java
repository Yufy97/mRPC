package com.nineSeven.mrpc.core.register;

import com.nineSeven.mrpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;

@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    public RegisterServiceImpl(RegisterConfig registerConfig) {
        try {
            CuratorFramework client = CuratorFrameworkFactory.newClient(registerConfig.getRegistryAddr(),
                    new ExponentialBackoffRetry(registerConfig.getBaseSleepTime(), registerConfig.getMaxRetries()));
            client.start();

            this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(new JsonInstanceSerializer<>(ServiceInfo.class))
                    .basePath(registerConfig.getBasePath())
                    .build();
            this.serviceDiscovery.start();
        } catch (Exception e) {
            log.error("serviceDiscovery start error :{}", e);
        }
    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        serviceDiscovery.registerService(ServiceInstance.<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .address(serviceInfo.getAddress())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build());
    }

    @Override
    public void unRegister(ServiceInfo serviceInfo) throws Exception {
        serviceDiscovery.unregisterService(ServiceInstance.<ServiceInfo>builder()
                .name(serviceInfo.getServiceName())
                .address(serviceInfo.getAddress())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build());
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
