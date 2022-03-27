package ks.core.network.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import ks.constants.L1Options;
import ks.core.network.L1Client;
import ks.core.network.util.L1NetworkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1NettyEncoder extends MessageToByteEncoder<byte[]> {
    private final Logger logger = LogManager.getLogger();

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] bytes, ByteBuf byteBuf) {
        L1Client client = (L1Client) ctx.channel().attr(AttributeKey.valueOf(L1Options.CLIENT_KEY)).get();

        try {
            if (bytes.length == 0) {
                return;
            }

            if (client != null) {
                bytes = L1NetworkUtils.buffer(client.encrypt(bytes), bytes.length + 2);
            } else {
                bytes = L1NetworkUtils.buffer(bytes, bytes.length + 2);
            }

            byteBuf.writeBytes(bytes);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
