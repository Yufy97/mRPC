package com.nineSeven.mrpc.server.transport;

import com.nineSeven.mrpc.core.codec.RpcDecoder;
import com.nineSeven.mrpc.core.codec.RpcEncoder;
import com.nineSeven.mrpc.server.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public class NettyRpcServer implements RpcServer {
    @Override
    public void start(int port) {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap().group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcRequestHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String serverAddress = InetAddress.getLocalHost().getHostAddress();
            ChannelFuture channelFuture = bootstrap.bind(serverAddress, port).sync();

            channelFuture.channel().closeFuture().sync();

            log.info("server addr {} started on port {}", serverAddress, port);
        } catch (Exception e) {
            log.error("{}", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
