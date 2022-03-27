package ks.core.network.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.IpSubnetFilter;
import ks.core.network.util.L1ConnectDelay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class L1NettyIpFilter extends IpSubnetFilter {
    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {
        String ip = remoteAddress.getHostString();

        boolean manyApplyConnection = L1ConnectDelay.getInstance().isManyApplyConnection(ip);

        if (manyApplyConnection) {
            logger.warn("공격의심 차단아이피 : {}", ip);
            return false;
        }

        return super.accept(ctx, remoteAddress);
    }
}
