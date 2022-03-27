package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class C_CheckPK extends ClientBasePacket {
    public C_CheckPK(byte[] data, L1Client client) {
        super(data);

        L1PcInstance player = client.getActiveChar();

        if (player == null) {
            return;
        }

        player.sendPackets(new S_ServerMessage(562, String.valueOf(player.getPkCount())));
    }
}
