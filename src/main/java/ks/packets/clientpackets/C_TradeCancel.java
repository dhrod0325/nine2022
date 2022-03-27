package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Trade;
import ks.model.pc.L1PcInstance;

public class C_TradeCancel extends ClientBasePacket {
    public C_TradeCancel(byte[] data, L1Client client) {
        super(data);

        L1PcInstance player = client.getActiveChar();

        if (player == null) {
            return;
        }

        L1Trade.cancel(player);
    }
}
