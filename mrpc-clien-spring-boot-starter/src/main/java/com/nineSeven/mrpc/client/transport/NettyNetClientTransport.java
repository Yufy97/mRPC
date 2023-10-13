package com.nineSeven.mrpc.client.transport;

import com.nineSeven.mrpc.client.cache.LocalRpcResponseCache;
import com.nineSeven.mrpc.client.handler.RpcResponseHandler;
import com.nineSeven.mrpc.core.codec.RpcDecoder;
import com.nineSeven.mrpc.core.codec.RpcEncoder;
import com.nineSeven.mrpc.core.common.RpcRequest;
import com.nineSeven.mrpc.core.common.RpcResponse;
import com.nineSeven.mrpc.core.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyNetClientTransport implements NetClientTransport {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public NettyNetClientTransport() {
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup(4);

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new RpcDecoder())
                                .addLast(new RpcEncoder<>())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    @Override
    public MessageProtocol<RpcResponse> sendRequest(RequestMetaData metaData) throws Exception {
        MessageProtocol<RpcRequest> protocol = metaData.getProtocol();
        RpcFuture<MessageProtocol<RpcResponse>> future = new RpcFuture<>();
        LocalRpcResponseCache.add(protocol.getHeader().getRequestId(), future);

        ChannelFuture channelFuture = bootstrap.connect(metaData.getAddress(), metaData.getPort()).sync();
        channelFuture.addListener(cf -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {} on port {} success", metaData.getAddress(), metaData.getPort());
            } else {
                log.error("connect rpc server {} on port {} success", metaData.getAddress(), metaData.getPort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });

        channelFuture.channel().writeAndFlush(protocol);
        //todo 用一个CountDownLatch实现
        return metaData.getTimeout() != null ? future.get(metaData.getTimeout(), TimeUnit.MILLISECONDS) : future.get();
    }
}
