package ks.core.network.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ks.app.config.prop.ServerConfig;
import ks.core.network.L1Server;

public class L1NettyServer implements L1Server {
    private final NioEventLoopGroup bsGroup = new NioEventLoopGroup(1);
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup();
    private ChannelFuture bindFuture;

    @Override
    public void start(int port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bsGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, ServerConfig.SERVER_MAX_USERS)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                .childOption(ChannelOption.SO_REUSEADDR, false)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_RCVBUF, 4096)
                .childOption(ChannelOption.SO_SNDBUF, 4096)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addFirst(L1NettyIpFilter.class.getName(), new L1NettyIpFilter());
                        pipeline.addLast(L1NettyDecoder.class.getName(), new L1NettyDecoder());
                        pipeline.addLast(L1NettyEncoder.class.getName(), new L1NettyEncoder());
                        pipeline.addLast(L1NettyHandler.class.getName(), new L1NettyHandler());

                    }
                });

        this.bindFuture = bootstrap.bind(port);
    }

    @Override
    public void shutDown() {
        if (bindFuture != null) {
            bindFuture.channel().close().awaitUninterruptibly();
            bindFuture = null;
        }

        bsGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
