package ks.core.network.server.netty;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import ks.core.network.L1Session;
import ks.core.network.util.L1PacketAttack;
import ks.packets.serverpackets.ServerBasePacket;

import java.net.SocketAddress;

public class L1NettySession implements L1Session {
    private final L1PacketAttack attack = new L1PacketAttack();

    private final Channel channel;

    public L1NettySession(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void write(Object o) {
        if (o instanceof ServerBasePacket) {
            ServerBasePacket packet = (ServerBasePacket) o;

            byte[] data = packet.getContent();

            channel.writeAndFlush(data);
        }
    }

    @Override
    public void setAttribute(String key, Object o) {
        AttributeKey<Object> attributeKey = AttributeKey.valueOf(key);

        Attribute<Object> attr = channel.attr(attributeKey);

        if (attr.get() == null) {
            attr.set(o);
        }
    }

    @Override
    public Object getAttribute(String key) {
        return channel.attr(AttributeKey.valueOf(key)).get();
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public void close(boolean now) {
        channel.close();
    }

    @Override
    public boolean isClosed() {
        return !channel.isOpen();
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public String getRemoteAddress() {
        SocketAddress addr = channel.remoteAddress();

        if (addr == null) {
            return "";
        } else {
            return addr.toString();
        }
    }

    @Override
    public boolean isPacketAttack() {
        return attack.isPacketAttack();
    }
}
