package ks.packets.clientpackets;

import ks.constants.L1ItemId;
import ks.core.datatables.CastleTable;
import ks.core.network.L1Client;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

public class C_Deposit extends ClientBasePacket {
    public C_Deposit(byte[] data, L1Client client) {
        super(data);
        int i = readD();
        int j = readD();

        L1PcInstance player = client.getActiveChar();

        if (player == null) {
            return;
        }

        if (player.getOnlineStatus() != 1) {
            player.disconnect(player.getName() + " 온라인 상태가 아님");
            return;
        }

        if (i == player.getId()) {
            L1Clan clan = L1World.getInstance().getClan(player.getClanName());
            if (clan != null) {
                int castleId = clan.getCastleId();

                if (!player.isCrown()) {
                    if (castleId == 0) {
                        return;
                    }
                }

                if (castleId != 0) { // 성주 크란
                    L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
                    L1ItemInstance aden = player.getInventory().findItemId(L1ItemId.ADENA);
                    if (j <= 0 || aden.getCount() < j || aden.getCount() <= 0 || j > 2000000000) {
                        player.sendPackets(new S_SystemMessage("(" + j + ")아데나는 정상적인 입금액이 아닙니다."));
                        return;
                    }
                    if (aden.getCount() < 0 || aden.getCount() > 2000000000
                            || (aden.getCount() - j <= 0)
                            || (aden.getCount() - j > 2000000000)) {
                        player.sendPackets(new S_SystemMessage("(" + j + ")아데나는 정상적인 입금액이 아닙니다."));
                        return;
                    }
                    /* 성세금 버그 */
                    synchronized (castle) {
                        int money = castle.getPublicMoney();

                        if (!player.getInventory().checkItem(L1ItemId.ADENA, j))
                            return;

                        if (player.getInventory().consumeItem(L1ItemId.ADENA, j)) {
                            money += j;
                            if (money > 2000000000 || money <= 0) {
                                money = 0;
                            }
                            castle.setPublicMoney(money);
                            CastleTable.getInstance().updateCastle(castle);
                            player.sendPackets(new S_SystemMessage("공금 " + j + " 아데나를 입금하였습니다."));
                        }
                    }
                }
            }
        }
    }
}
