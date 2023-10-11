package com.nineSeven.mrpc.client.handler;

import com.nineSeven.mrpc.client.config.RpcClientProperties;
import com.nineSeven.mrpc.core.common.RpcRequest;
import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.common.ServiceInfo;
import com.nineSeven.mrpc.core.common.ServiceUtil;
import com.nineSeven.mrpc.core.discover.DiscoveryService;
import com.nineSeven.mrpc.core.exception.ResourceNotFoundException;
import com.nineSeven.mrpc.core.exception.RpcException;
import com.nineSeven.mrpc.core.protocol.MessageHeader;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import com.nineSeven.mrpc.core.protocol.MsgStatus;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {



    private DiscoveryService discoveryService;

    private RpcClientProperties properties;

    private Class<?> clazz;

    private String version;

    public ClientStubInvocationHandler(DiscoveryService discoveryService, RpcClientProperties properties, Class<?> clazz, String version) {
        super();
        this.discoveryService = discoveryService;
        this.properties = properties;
        this.clazz = clazz;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = ServiceUtil.serviceKey(clazz.getName(), version);
        ServiceInfo serviceInfo = discoveryService.discovery(serviceName);

        if(serviceInfo == null) {
            throw new ResourceNotFoundException("404");
        }

        MessageProtocol<RpcRequest> request = new MessageProtocol<>();

        request.setHeader(MessageHeader.build(properties.getSerialization()));

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethod(method.getName());
        rpcRequest.setParameters(method.getParameters());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setServiceName(serviceName);
        request.setBody(rpcRequest);

        //todo netty发送请求
        MessageProtocol<RpcResponse> response = null;

        if (response == null) {
            log.error("请求超时");
            throw new RpcException("rpc调用结果失败， 请求超时 timeout:" + properties.getTimeout());
        }

        if (!MsgStatus.success(response.getHeader().getStatus())) {
            log.error("rpc调用结果失败， message：{}", response.getBody().getMessage());
            throw new RpcException(response.getBody().getMessage());
        }
        return response.getBody().getData();
    }
}
