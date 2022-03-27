package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillBuy;

public class C_SkillBuy extends ClientBasePacket {
    public C_SkillBuy(byte[] data, L1Client client) {
        super(data);

        int i = readD();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }

        pc.sendPackets(new S_SkillBuy(pc));
    }
}
