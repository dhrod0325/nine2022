package ks.model.inventory;

import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;

import java.util.HashMap;
import java.util.Map;

public class InventoryInfoHandler {
    private static final InventoryInfoHandler instance = new InventoryInfoHandler();
    private final Map<String, InventoryInfoMessenger> messengerMap = new HashMap<>();

    public static InventoryInfoHandler getInstance() {
        return instance;
    }

    public boolean handle(int handleId, int size, C_ShopAndWarehouse packet) {
        InventoryInfoMessenger messenger = find(handleId);

        if (messenger != null) {
            messenger.action(handleId, size, packet);
            return true;
        }

        return false;
    }

    public InventoryInfoMessenger find(int handleId) {
        for (InventoryInfoMessenger messenger : messengerMap.values()) {
            if (handleId == messenger.getHandleId()) {
                return messenger;
            }
        }

        return null;
    }

    public InventoryInfoMessenger find(L1PcInstance pc) {
        for (String key : messengerMap.keySet()) {
            InventoryInfoMessenger messenger = messengerMap.get(key);

            if (messenger.getPc().equals(pc)) {
                return messenger;
            }
        }

        return null;
    }

    public void register(InventoryInfoMessenger messenger) {
        messengerMap.put(messenger.getKey(), messenger);
    }

}