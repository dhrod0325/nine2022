package ks.packets.serverpackets;

public class S_CopyPacket extends ServerBasePacket {
    public S_CopyPacket(ServerBasePacket sb) {
        writeByte(sb.getContent());
    }
}
