package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Trade;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CloseList;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;
import ks.util.L1FaceUtils;

public class C_Trade extends ClientBasePacket {
    public C_Trade(byte[] data, L1Client client) {
        super(data);

        L1PcInstance player = client.getActiveChar();

        if (player == null) {
            return;
        }

        if (L1CommonUtils.isTwoLogin(player))
            return;

        if (player.getOnlineStatus() == 0) {
            client.disconnect();
            return;
        }

        if (player.getOnlineStatus() != 1) {
            client.disconnect();
            return;
        }

        if (player.isInvisible()) {
            player.sendPackets(new S_ServerMessage(334));
            return;
        }

        L1PcInstance target = L1FaceUtils.faceToFace(player);

        if (target != null) {
            if (!L1Trade.checkTradeAble(player, target)) {
                return;
            }

            player.sendPackets(new S_CloseList(player.getId()));
            target.sendPackets(new S_CloseList(target.getId()));

            if (player.getLevel() > 4 && target.getLevel() > 4) {
                if (!target.isParalyzed()) {
                    player.setTradeID(target.getId());
                    target.setTradeID(player.getId());
                    target.sendPackets(new S_Message_YN(252, player.getName()));
                }
            }
        }
    }
}
