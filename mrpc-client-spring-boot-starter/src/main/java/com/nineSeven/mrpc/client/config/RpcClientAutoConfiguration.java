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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class RpcClientAutoConfiguration {

    @Bean
    public RpcClientProperties rpcClientProperties(Environment environment) {
        return Binder.get(environment).bind("mrpc.client", RpcClientProperties.class).get();
    }

    @Primary
    @Bean(name = "balancePolicy")
    @ConditionalOnMissingBean(BalancePolicy.class)
    @ConditionalOnProperty(prefix = "mrpc.client", name = "balance", havingValue = "fullRound", matchIfMissing = true)
    public BalancePolicy fullRoundBalance() {
        return new FullRoundBalance();
    }

    @Bean(name = "balancePolicy")
    @ConditionalOnMissingBean(BalancePolicy.class)
    @ConditionalOnProperty(prefix = "mrpc.client", name = "balance", havingValue = "random")
    public BalancePolicy randomBalance() {
        return new RandomBalance();
    }


    @Bean
    @ConditionalOnMissingBean(ClientStubProxyFactory.class)
    public ClientStubProxyFactory clientStubProxyFactory() {
        return new ClientStubProxyFactory();
    }

    @Bean
    @ConditionalOnMissingBean(DiscoveryService.class)
    @ConditionalOnBean({BalancePolicy.class, RpcClientProperties.class})
    public DiscoveryService discoveryService(@Autowired BalancePolicy balancePolicy, @Autowired RpcClientProperties clientProperties) {
        DiscoverConfig config = DiscoverConfig.builder()
                .baseSleepTime(clientProperties.getBaseSleepTime())
                .discoverAddr(clientProperties.getDiscoveryAddr())
                .maxRetries(clientProperties.getMaxRetries())
                .basePath(clientProperties.getBasePath())
                .build();
        return new ZkDiscoverService(config, balancePolicy);
    }

    @Bean
    @ConditionalOnMissingBean(RpcClientProcessor.class)
    @ConditionalOnBean({DiscoveryService.class, ClientStubProxyFactory.class})
    public RpcClientProcessor rpcClientProcessor(@Autowired DiscoveryService discoveryService,
                                                 @Autowired ClientStubProxyFactory clientStubProxyFactory,
                                                 @Autowired RpcClientProperties clientProperties) {
        return new RpcClientProcessor(discoveryService, clientStubProxyFactory, clientProperties);
    }
}
