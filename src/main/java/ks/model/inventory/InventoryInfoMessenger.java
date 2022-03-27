package ks.model.inventory;

import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;

public interface InventoryInfoMessenger {
    L1PcInstance getPc();

    void setPc(L1PcInstance pc);

    boolean isValidItem(L1ItemInstance item);

    int getHandleId();

    void setHandleId(int handleId);

    void action(int handleId, int size, C_ShopAndWarehouse packet);

    String key();

    String getKey();
}
