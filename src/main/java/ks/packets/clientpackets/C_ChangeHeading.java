package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.Broadcaster;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChangeHeading;

public class C_ChangeHeading extends ClientBasePacket {
    public C_ChangeHeading(byte[] decrypt, L1Client client) {
        super(decrypt);

        int heading = readC();

        if (heading < 0 || heading > 7)
            return;

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        pc.setHeading(heading);

        if (!pc.isGmInvis() && !pc.isInvisible()) {
            Broadcaster.broadcastPacket(pc, new S_ChangeHeading(pc));
        }
    }
}