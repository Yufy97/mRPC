package com.nineSeven.mrpc.server.handler;

import com.nineSeven.mrpc.core.common.RpcRequest;
import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.protocol.MessageHeader;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import com.nineSeven.mrpc.core.protocol.MsgStatus;
import com.nineSeven.mrpc.core.protocol.MsgType;
import com.nineSeven.mrpc.server.store.LocalServerCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcRequest>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcRequest> rpcRequestMessageProtocol) throws Exception {
        MessageProtocol<RpcResponse> responseMessageProtocol = new MessageProtocol<>();
        MessageHeader header = rpcRequestMessageProtocol.getHeader();
        header.setMsgType(MsgType.RESPONSE.getType());
        try {
            header.setStatus(MsgStatus.SUCCESS.getStatus());
            responseMessageProtocol.setHeader(header);
            responseMessageProtocol.setBody(process(rpcRequestMessageProtocol.getBody()));
        } catch (Throwable e) {
            header.setStatus(MsgStatus.FAIL.getStatus());
            responseMessageProtocol.setBody(null);
            log.error("process request {} error", header.getRequestId(), e);
        }

        channelHandlerContext.writeAndFlush(responseMessageProtocol);
    }

    private RpcResponse process(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Object service = LocalServerCache.get(rpcRequest.getServiceName());
            if(service == null) {
                throw new RuntimeException(String.format("service not exist: %s !", rpcRequest.getServiceName()));
            }

            Method method = service.getClass().getMethod(rpcRequest.getMethod(), rpcRequest.getParameterTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            rpcResponse.setData(result);
            rpcResponse.setMessage("服务调用成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rpcResponse;
    }
}
