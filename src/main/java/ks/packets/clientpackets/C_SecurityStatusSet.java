package ks.packets.clientpackets;

import ks.core.datatables.CastleTable;
import ks.core.network.L1Client;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CloseList;

public class C_SecurityStatusSet extends ClientBasePacket {
    public C_SecurityStatusSet(byte[] data, L1Client client) {
        super(data);

        int objid = readD();
        int type = readC();
        int unknow = readD();// ????

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        L1Castle castle = CastleTable.getInstance().getCastleTable(clan.getCastleId());
        int money = castle.getPublicMoney();

        if (castle.getCastleSecurity() == type)
            return;

        if (money < 100000)
            return;

        if (type == 1)
            castle.setPublicMoney(money - 100000);

        castle.setCastleSecurity(type);

        CastleTable.getInstance().updateCastle(castle);

        pc.sendPackets(new S_CloseList(objid));
    }
}
