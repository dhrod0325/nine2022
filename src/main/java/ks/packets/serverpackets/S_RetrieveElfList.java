package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.ElfWarehouse;
import ks.model.warehouse.WarehouseManager;

public class S_RetrieveElfList extends ServerBasePacket {
    public S_RetrieveElfList(int objid, L1PcInstance pc) {
        if (pc.getInventory().getSize() < 180) {
            ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
            int size = elfwarehouse.getSize();
            if (size > 0) {
                writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
                writeD(objid);
                writeH(size);
                writeC(9);

                for (L1ItemInstance itemObject : elfwarehouse.getItems()) {
                    writeD(itemObject.getId());
                    writeC(itemObject.getItem().getType2());//탬타입..돌려주기 응헉
                    writeH(itemObject.getGfxId());
                    writeC(itemObject.getBless());
                    writeD(itemObject.getCount());
                    writeC(itemObject.isIdentified() ? 1 : 0);
                    writeS(itemObject.getViewName());
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
