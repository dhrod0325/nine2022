package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_Attack extends ClientBasePacket {
    public C_Attack(byte[] decrypt, L1Client client) {
        super(decrypt);

        int targetId = readD();
        int x = readH();
        int y = readH();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null)
            return;

        pc.getAttack().toAttack(x, y, targetId);
    }
}

