package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;

public class C_Shop extends ClientBasePacket {
    public C_Shop(byte[] data, L1Client lineageClient) {
        super(data);

        L1PcInstance pc = lineageClient.getActiveChar();

        if (!L1CommonUtils.isValidShopOpen(pc)) {
            return;
        }

        pc.sendPackets("개인상점은 .상점 명령어를 이용해 주세요.");
    }
}
