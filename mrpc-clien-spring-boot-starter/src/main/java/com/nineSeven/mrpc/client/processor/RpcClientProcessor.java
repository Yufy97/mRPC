package com.nineSeven.mrpc.client.processor;

import com.nineSeven.mrpc.client.annotation.RpcAutowired;
import com.nineSeven.mrpc.client.config.RpcClientProperties;
import com.nineSeven.mrpc.client.proxy.ClientStubProxyFactory;
import com.nineSeven.mrpc.core.discover.DiscoveryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private DiscoveryService discoveryService;

    private ClientStubProxyFactory clientStubProxyFactory;

    private RpcClientProperties properties;

    private ApplicationContext applicationContext;

    public RpcClientProcessor(DiscoveryService discoveryService, ClientStubProxyFactory clientStubProxyFactory,
                              RpcClientProperties properties) {
        this.discoveryService = discoveryService;
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.properties = properties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //获取所有bean的beanDefinition
        for(String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if(beanClassName != null) {
                //获取bean的class对象
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, beanDefinition.getClass().getClassLoader());
                //该bean的字段如果有RpcAutoWired则把bean替换为代理对象
                ReflectionUtils.doWithFields(clazz, field -> {
                    RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if(rpcAutowired != null) {
                        Object bean = applicationContext.getBean(clazz);
                        field.setAccessible(true);
                        // 修改为代理对象
                        ReflectionUtils.setField(field, bean, clientStubProxyFactory.getProxy(field.getType(),
                                rpcAutowired.version(), discoveryService, properties));
                    }
                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.setApplicationContext(applicationContext);
    }
}
