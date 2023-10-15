package com.nineSeven.mrpc.client.cache;

import com.nineSeven.mrpc.client.transport.RpcFuture;
import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRpcResponseCache {

    private static Map<String, RpcFuture<MessageProtocol<RpcResponse>>> responseCache = new ConcurrentHashMap<>();

    public static void add(String reqId, RpcFuture<MessageProtocol<RpcResponse>> future) {
        responseCache.put(reqId, future);
    }

    public static void fillResponse(String reqId, MessageProtocol<RpcResponse> messageProtocol) {
        RpcFuture<MessageProtocol<RpcResponse>> future = responseCache.get(reqId);

        future.setResponse(messageProtocol);
        future.countDown();

        responseCache.remove(reqId);
    }
}
