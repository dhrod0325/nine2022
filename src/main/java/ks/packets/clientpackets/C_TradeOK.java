package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1Trade;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class C_TradeOK extends ClientBasePacket {
    public C_TradeOK(byte[] data, L1Client client) {
        super(data);

        L1PcInstance player = client.getActiveChar();

        if (player == null) {
            return;
        }

        L1Object tradingPartner = L1World.getInstance().findObject(player.getTradeID());

        if (tradingPartner != null) {
            if (tradingPartner instanceof L1PcInstance) {
                L1PcInstance tradepc = (L1PcInstance) tradingPartner;

                if (player.getTradeID() == 0) {
                    return;
                }

                player.setTradeOk(true);

                if (player.getTradeOk() && tradepc.getTradeOk()) {
                    if (player.getInventory().getSize() < (180 - 16) && tradepc.getInventory().getSize() < (180 - 16)) {
                        L1Trade.trade(player);
                    } else {
                        player.sendPackets(new S_ServerMessage(263));
                        tradepc.sendPackets(new S_ServerMessage(263));
                    }
                }
            }
        }
    }
}
