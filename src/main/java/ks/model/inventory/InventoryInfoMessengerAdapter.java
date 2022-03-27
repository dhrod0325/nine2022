package ks.model.inventory;

import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;

public class InventoryInfoMessengerAdapter implements InventoryInfoMessenger {
    private int handleId;

    private L1PcInstance pc;

    @Override
    public boolean isValidItem(L1ItemInstance item) {
        return true;
    }

    @Override
    public int getHandleId() {
        return handleId;
    }

    public void setHandleId(int handleId) {
        this.handleId = handleId;
    }

    @Override
    public void action(int handleId, int size, C_ShopAndWarehouse packet) {
    }

    @Override
    public L1PcInstance getPc() {
        return pc;
    }

    @Override
    public void setPc(L1PcInstance pc) {
        this.pc = pc;
    }

    @Override
    public String key() {
        return "InventoryInfoMessengerAdapter";
    }

    public String getKey() {
        return key() + "_" + pc.getId() + "_" + getHandleId();
    }
}
