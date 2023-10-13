package com.nineSeven.mrpc.client.handler;

import com.nineSeven.mrpc.client.cache.LocalRpcResponseCache;
import com.nineSeven.mrpc.client.transport.RpcFuture;
import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class RpcResponseHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcResponse> response) throws Exception {
        LocalRpcResponseCache.fillResponse(response.getHeader().getRequestId(), response);
    }
}
