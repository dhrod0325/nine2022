package ks.model.inventory;

import ks.core.ObjectIdFactory;
import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

public class S_InventoryInfo extends ServerPacket {
    private final L1PcInstance pc;
    private InventoryInfoMessenger messenger = new InventoryInfoMessengerAdapter();
    private Integer maxItemCount;

    public S_InventoryInfo(L1PcInstance pc) {
        this.pc = pc;
    }

    public void setMaxItemCount(Integer maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    public void setMessenger(InventoryInfoMessenger messenger) {
        this.messenger = messenger;
    }

    public void build() {
        build(pc.getInventory().getItems());
    }

    public void build(List<L1ItemInstance> list) {
        int handleId = ObjectIdFactory.getInstance().nextId();

        if (messenger != null) {
            messenger.setPc(pc);
            messenger.setHandleId(handleId);
            InventoryInfoHandler.getInstance().register(messenger);
        }

        writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
        writeD(handleId);

        List<L1ItemInstance> tempSellList = new ArrayList<>();

        for (L1ItemInstance item : list) {
            if (messenger.isValidItem(item)) {
                tempSellList.add(item);
            }
        }

        writeH(tempSellList.size());
        writeC(10);

        for (L1ItemInstance item : tempSellList) {
            writeD(item.getId());
            writeC(item.getItem().getType2());
            writeH(item.getGfxId());
            writeC(item.getBless());

            if (maxItemCount != null) {
                writeD(maxItemCount);
            } else {
                writeD(item.getCount());
            }

            writeC(item.isIdentified() ? 1 : 0);
            writeS(item.getViewName());
        }

        writeD(30);
        writeD(0x00000000);
        writeH(0x00);
    }
}
