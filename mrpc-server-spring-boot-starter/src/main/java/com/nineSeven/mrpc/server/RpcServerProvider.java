package com.nineSeven.mrpc.server;

import com.nineSeven.mrpc.core.common.ServiceInfo;
import com.nineSeven.mrpc.core.common.ServiceUtil;
import com.nineSeven.mrpc.server.annotation.RpcService;
import com.nineSeven.mrpc.server.config.RpcServerProperties;
import com.nineSeven.mrpc.server.store.LocalServerCache;
import com.nineSeven.mrpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import com.nineSeven.mrpc.core.register.RegisterService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;

import java.io.IOException;
import java.net.InetAddress;

@Slf4j
public class RpcServerProvider implements BeanPostProcessor, CommandLineRunner {

    private RegisterService registerService;

    private RpcServer rpcServer;

    private RpcServerProperties rpcServerProperties;


    public RpcServerProvider(RegisterService registerService, RpcServer rpcServer, RpcServerProperties rpcServerProperties) {
        this.registerService = registerService;
        this.rpcServer = rpcServer;
        this.rpcServerProperties = rpcServerProperties;
    }


    /**
     * 被Spring管理的服务bean注册进注册中心
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcServer = bean.getClass().getAnnotation(RpcService.class);
        if(rpcServer != null) {
            try {
                String serviceName = rpcServer.value().getName();
                String version = rpcServer.version();
                LocalServerCache.store(ServiceUtil.serviceKey(serviceName, version), bean);

                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(serviceName);
                serviceInfo.setVersion(version);
                serviceInfo.setPort(rpcServerProperties.getPort());
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                serviceInfo.setAppName(rpcServerProperties.getAppName());

                registerService.register(serviceInfo);
            } catch (Exception e) {
                log.error("服务注册出错:{}", e);
            }
        }
        return bean;
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> rpcServer.start(rpcServerProperties.getPort())).start();
        log.info("rpc server {} start, port :{}",rpcServer, rpcServerProperties.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                registerService.destroy();
            } catch (IOException e) {
                log.error("{}", e);
            }
        }));
    }
}
