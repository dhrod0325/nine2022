package ks.core.network.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import ks.constants.L1Options;
import ks.core.network.L1Client;
import ks.core.network.util.L1NetworkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class L1NettyDecoder extends ByteToMessageDecoder {
    private final Logger logger = LogManager.getLogger();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        L1Client client = (L1Client) ctx.channel().attr(AttributeKey.valueOf(L1Options.CLIENT_KEY)).get();

        if (client == null) {
            return;
        }

        if (client.isPacketAttack()) {
            return;
        }

        int readable = msg.readableBytes();

        if (readable < 2) {
            return;
        }

        try {
            while (readable > 2) {
                msg.markReaderIndex();

                int size = msg.readUnsignedShortLE() - 2;

                readable -= 2;

                if (readable < size) {
                    msg.resetReaderIndex();
                    break;
                }

                if (size < 0) {
                    break;
                }

                byte[] data = new byte[size];
                msg.readBytes(data, 0, size);

                byte[] buffer = L1NetworkUtils.buffer(data, data.length + 2);
                byte[] dt = client.decrypt(buffer);

                out.add(dt);

                readable -= size;
            }
        } catch (NegativeArraySizeException e) {
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}