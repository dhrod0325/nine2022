package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.ClanWarehouse;
import ks.model.warehouse.WarehouseManager;

public class S_RetrievePledgeList extends ServerBasePacket {
    public S_RetrievePledgeList(int objid, L1PcInstance pc) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan == null) {
            return;
        }

        ClanWarehouse clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());

        if (!clanWarehouse.lock(pc.getId())) {
            pc.sendPackets(new S_ServerMessage(209));
            return;
        }

        if (pc.getInventory().getSize() < 180) {
            int size = clanWarehouse.getSize();

            if (size > 0) {
                writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
                writeD(objid);
                writeH(size);
                writeC(5);
                for (L1ItemInstance item : clanWarehouse.getItems()) {
                    writeD(item.getId());
                    writeC(item.getItem().getType2());
                    writeH(item.getGfxId());
                    writeC(item.getBless());
                    writeD(item.getCount());
                    writeC(item.isIdentified() ? 1 : 0);
                    writeS(item.getViewName());
                }
                writeD(30);
                writeD(0x00000000);
                writeH(0x00);
            } else {
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noitemret"));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(263));
        }
    }
}
