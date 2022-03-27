package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.PrivateWarehouse;
import ks.model.warehouse.WarehouseManager;

public class S_RetrieveList extends ServerBasePacket {
    public S_RetrieveList(int objid, L1PcInstance pc) {
        if (pc.getInventory().getSize() < 180) {
            PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
            int size = warehouse.getSize();

            if (size > 0) {
                writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
                writeD(objid);
                writeH(size);
                writeC(3);

                for (L1ItemInstance item : warehouse.getItems()) {
                    writeD(item.getId());
                    writeC(item.getItem().getType2());//탬타입..돌려주기 응헉
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
