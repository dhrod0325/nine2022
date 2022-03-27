package ks.packets.clientpackets;

import ks.constants.L1ItemId;
import ks.core.datatables.CastleTable;
import ks.core.datatables.item.ItemTable;
import ks.core.network.L1Client;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1Inventory;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.WarTimeScheduler;

public class C_Drawal extends ClientBasePacket {
    public C_Drawal(byte[] data, L1Client client) {
        super(data);

        int i = readD();
        int j = readD();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            int castleId = clan.getCastleId();

            if (castleId != 0) {
                if (WarTimeScheduler.getInstance().isNowWar(clan.getCastleId())) {
                    pc.sendPackets(new S_SystemMessage("공성중에는 세금을 찾으실수 없습니다."));
                    return;
                }

                if (pc.getClanRank() != 10 || !pc.isCrown() || pc.getId() != pc.getClan().getLeaderId())
                    return;

                L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);

                int money = castle.getPublicMoney();
                long _money = money;

                if (_money <= 0 || money < j) {
                    return;
                }

                money -= j;
                L1ItemInstance item = ItemTable.getInstance().createItem(L1ItemId.ADENA);
                if (item != null) {
                    castle.setPublicMoney(money);
                    CastleTable.getInstance().updateCastle(castle);
                    if (pc.getInventory().checkAddItem(item, j) == L1Inventory.OK) {
                        pc.getInventory().storeItem(L1ItemId.ADENA, j);
                    } else {
                        L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(L1ItemId.ADENA, j);
                    }
                    pc.sendPackets(new S_ServerMessage(143, "$457", "$4" + " (" + j + ")"));
                }
            }
        }
    }
}
