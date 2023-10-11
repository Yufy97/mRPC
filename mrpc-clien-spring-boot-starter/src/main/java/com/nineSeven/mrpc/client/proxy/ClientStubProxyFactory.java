package com.nineSeven.mrpc.client.proxy;

import com.nineSeven.mrpc.client.config.RpcClientProperties;
import com.nineSeven.mrpc.client.handler.ClientStubInvocationHandler;
import com.nineSeven.mrpc.core.discover.DiscoveryService;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ClientStubProxyFactory {

    private Map<Class<?>, Object> objectCache = new HashMap<>();

    public <T> T getProxy(Class<T> clazz, String version, DiscoveryService discoveryService, RpcClientProperties properties) {
        return (T) objectCache.computeIfAbsent(clazz, clz -> Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clazz},
                new ClientStubInvocationHandler(discoveryService, properties, clazz, version)));
    }
}
