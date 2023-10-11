package com.nineSeven.mrpc.server.config;

import com.nineSeven.mrpc.server.RpcServerProvider;
import com.nineSeven.mrpc.server.transport.NettyRpcServer;
import com.nineSeven.mrpc.server.transport.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nineSeven.mrpc.core.register.RegisterConfig;
import com.nineSeven.mrpc.core.register.RegisterService;
import com.nineSeven.mrpc.core.register.ZkRegisterService;

@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {

    @Autowired
    RpcServerProperties serverProperties;

    @Bean
    @ConditionalOnMissingBean(RegisterService.class)
    public RegisterService registerService() {
        return new ZkRegisterService(RegisterConfig.builder()
                .basePath(serverProperties.getBasePath())
                .baseSleepTime(serverProperties.getBaseSleepTime())
                .maxRetries(serverProperties.getMaxRetries())
                .registryAddr(serverProperties.getRegistryAddr())
                .build());
    }

    @Bean
    @ConditionalOnMissingBean(RpcServer.class)
    public RpcServer rpcServer() {
        return new NettyRpcServer();
    }

    @Bean
    @ConditionalOnMissingBean(RpcServerProvider.class)
    public RpcServerProvider rpcServerProvider(@Autowired RegisterService registerService,
                                               @Autowired RpcServer rpcServer,
                                               @Autowired RpcServerProperties rpcServerProperties) {
        return new RpcServerProvider(registerService, rpcServer, rpcServerProperties);
    }
}
