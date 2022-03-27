package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_FishCancel extends ClientBasePacket {
    public C_FishCancel(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        pc.endFishing();
    }
}
