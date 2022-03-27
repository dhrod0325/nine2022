package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

public class C_TaxRate extends ClientBasePacket {
    public C_TaxRate(byte[] bytes, L1Client clientthread) {
        super(bytes);
        int i = readD();
        int j = readC();

        L1PcInstance player = clientthread.getActiveChar();
        if (player == null) {
            return;
        }
        if (i == player.getId()) {
            L1Clan clan = L1World.getInstance().getClan(player.getClanName());
            if (clan != null) {
                int castle_id = clan.getCastleId();
                if (castle_id != 0) {
                    player.sendPackets(new S_SystemMessage("세금 조정을 할수없습니다."));
                }
            }
        }
    }
}
