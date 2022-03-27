package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_RestartWait extends ClientBasePacket {
    public C_RestartWait(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc != null) {
            pc.sendPackets("아직 리스타트가 불가능합니다. 다시 시도하세요");
        }
    }
}

