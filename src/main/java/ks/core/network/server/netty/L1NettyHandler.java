package ks.core.network.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ks.constants.L1Options;
import ks.core.network.L1Client;
import ks.core.network.L1ProtocolHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class L1NettyHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getLogger(L1NettyHandler.class);
    private final L1ProtocolHandler handler = new L1ProtocolHandler();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        handler.sessionCreated(new L1NettySession(ctx.channel()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handler.sessionOpened(new L1NettySession(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        print(ctx, "연결 종료[정상]");
        handler.sessionClosed(new L1NettySession(ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        L1NettySession session = new L1NettySession(ctx.channel());
        L1Client client = (L1Client) session.getAttribute(L1Options.CLIENT_KEY);

        if (client == null) {
            ctx.close();
        } else {
            client.receivePacket((byte[]) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        print(ctx, "연결 종료[비정상(팅김)].");
        handler.sessionClosed(new L1NettySession(ctx.channel()));
    }

    public void print(ChannelHandlerContext ctx, String message) {
        InetSocketAddress inetAddr = (InetSocketAddress) ctx.channel().remoteAddress();
        print(inetAddr.getAddress().getHostAddress(), inetAddr.getPort(), message);
    }

    public void print(String ip, int port, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logger.debug("[{}][{}:{}] {}", date, ip, port, message);
    }
}
