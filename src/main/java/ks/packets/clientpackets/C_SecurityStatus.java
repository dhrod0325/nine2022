package ks.packets.clientpackets;

import ks.core.datatables.CastleTable;
import ks.core.network.L1Client;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class C_SecurityStatus extends ClientBasePacket {
    public C_SecurityStatus(byte[] bytes, L1Client client) {
        super(bytes);

        int objid = readD();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan == null || clan.getCastleId() == 0)
            return;

        int castle_id = clan.getCastleId();
        String npcName = null;
        String status = null;
        L1Castle castle = CastleTable.getInstance().getCastleTable(castle_id);

        switch (castle_id) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                npcName = "$1238";
                break;
            case 5:
                break;
            default:
                break;
        }

        if (castle.getCastleSecurity() == 0)
            status = "$1118";
        else
            status = "$1117";

        String[] htmldata = new String[]{npcName, status};
        pc.sendPackets(new S_NPCTalkReturn(objid, "CastleS", htmldata));
    }
}
