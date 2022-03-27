package ks.model;

import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CloseList;
import ks.util.L1CommonUtils;

public class L1DragonArmorChange {
    private final L1PcInstance pc;
    private L1ItemInstance useItem;
    private L1ItemInstance targetItem;

    public L1DragonArmorChange(L1PcInstance pc) {
        this.pc = pc;
    }

    public L1ItemInstance getUseItem() {
        return useItem;
    }

    public void setUseItem(L1ItemInstance useItem) {
        this.useItem = useItem;
    }

    public L1ItemInstance getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(L1ItemInstance targetItem) {
        this.targetItem = targetItem;
    }

    public void clear() {
        setTargetItem(null);
        setUseItem(null);
    }

    public void changeItem(Integer newItemId) {
        if (targetItem == null) {
            pc.sendPackets("실패 : 다시 시도하세요");
        } else {
            L1ItemInstance newItem = ItemTable.getInstance().createItem(newItemId);
            newItem.setBless(targetItem.getBless());
            newItem.setCount(1);
            newItem.setEnchantLevel(targetItem.getEnchantLevel());
            newItem.setIdentified(true);
            newItem.setAttrEnchantLevel(targetItem.getAttrEnchantLevel());

            if (pc.getInventory().checkAddItem(newItem, 1) == L1Inventory.OK) {
                pc.sendPackets("변경 전 : " + targetItem.getViewName());
                pc.sendPackets("변경 후 : " + newItem.getViewName());

                L1CommonUtils.locationEffect(pc, 7321);

                pc.getInventory().storeItem(newItem);
                pc.getInventory().removeItem(targetItem, 1);
                pc.getInventory().removeItem(useItem, 1);
            } else {
                pc.sendPackets("인벤토리를 확인하세요. 용갑옷 교환에 실패하였습니다");
            }
        }

        pc.sendPackets(new S_CloseList(pc.getId()));
        pc.getDragonArmorChange().clear();
    }
}
