package web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.config.WebServerConfig;
import web.http.handler.L1HttpHandler;

public class L1WebServer implements Runnable {
    private final Logger logger = LogManager.getLogger();

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        if (port == 0) {
            port = WebServerConfig.WEB_SERVER_PORT;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("httpServerCodec", new HttpServerCodec());
                            pipeline.addLast("httpHandler", new L1HttpHandler());
                        }
                    });

            logger.info("웹 서버가 포트번호 : {} 으로 기동되었습니다", port);

            ChannelFuture f = bootStrap.bind("127.0.0.1", port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("오류", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        start();
    }
}
