package com.nineSeven.mrpc.client.config;

import com.nineSeven.mrpc.client.processor.RpcClientProcessor;
import com.nineSeven.mrpc.client.proxy.ClientStubProxyFactory;
import com.nineSeven.mrpc.core.balancer.BalancePolicy;
import com.nineSeven.mrpc.core.balancer.FullRoundBalance;
import com.nineSeven.mrpc.core.balancer.RandomBalance;
import com.nineSeven.mrpc.core.discover.DiscoverConfig;
import com.nineSeven.mrpc.core.discover.DiscoveryService;
import com.nineSeven.mrpc.core.discover.ZkDiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcClientAutoConfiguration {

    @Autowired
    RpcClientProperties properties;

    @Primary
    @Bean(name = "balancePolicy")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "fullRound", matchIfMissing = true)
    public BalancePolicy fullRoundBalance() {
        return new FullRoundBalance();
    }

    @Bean(name = "balancePolicy")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "balance", havingValue = "random")
    public BalancePolicy randomBalance() {
        return new RandomBalance();
    }

    @Bean
    @ConditionalOnMissingBean
    public DiscoveryService discoveryService(@Autowired BalancePolicy balance) {
        DiscoverConfig config = DiscoverConfig.builder()
                .baseSleepTime(properties.getBaseSleepTime())
                .discoverAddr(properties.getDiscoveryAddr())
                .maxRetries(properties.getMaxRetries())
                .basePath(properties.getBasePath())
                .build();
        return new ZkDiscoverService(config, balance);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientStubProxyFactory clientStubProxyFactory() {
        return new ClientStubProxyFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientProcessor rpcClientProcessor(@Autowired DiscoveryService discoveryService, @Autowired ClientStubProxyFactory proxyFactory,
                                                 @Autowired ApplicationContext context) {
        return new RpcClientProcessor(discoveryService, proxyFactory, properties, context);
    }
}
