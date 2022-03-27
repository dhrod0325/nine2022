package ks.system.robot;

import ks.model.L1PcInventory;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class L1RobotInventory extends L1PcInventory {
    private static final Logger logger = LogManager.getLogger(L1RobotInventory.class);

    public L1RobotInventory(L1PcInstance owner) {
        super(owner);
    }

    @Override
    public synchronized void insertItem(L1ItemInstance item) {
    }

    @Override
    public void loadItems() {
        try {
            L1RobotItemStorage storage = L1RobotItemStorage.getInstance();

            List<L1ItemInstance> loadItems = storage.loadItems(getOwner().getId());

            for (L1ItemInstance item : loadItems) {
                items.add(item);

                if (item.isEquipped()) {
                    item.setEquipped(false);
                    setEquipped(item, true, true, false);
                }

                L1World.getInstance().storeObject(item);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }


    @Override
    public void saveItem(L1ItemInstance item, int column) {
        if (column == 0) {
            return;
        }

        try {
            L1RobotItemStorage storage = L1RobotItemStorage.getInstance();

            if (column >= COL_PANDORA) { // 엔챤트
                storage.updateItemEnchantLevel(item);
                storage.updateItemProtection(item); //추가장비 보호
                column -= COL_PANDORA;
            }

            if (column >= COL_PROTEC) { // 엔챤트
                storage.updateItemEnchantLevel(item);
                storage.updateItemProtection(item); //추가장비 보호
                column -= COL_PROTEC;
            }

            if (column >= COL_INNA) { // 엔챤트
                storage.updateItemEnchantLevel(item);
                storage.updateItemProtection(item); //추가장비 보호
                column -= COL_INNA;
            }

            if (column >= COL_ATTRENCHANTLVL) {
                storage.updateItemAttrEnchantLevel(item);
                column -= COL_ATTRENCHANTLVL;
            }

            if (column >= COL_BLESS) {
                storage.updateItemBless(item);
                column -= COL_BLESS;
            }

            if (column >= COL_REMAINING_TIME) {
                storage.updateItemRemainingTime(item);
                column -= COL_REMAINING_TIME;
            }

            if (column >= COL_CLOCK) {
                storage.updateClock(item);
                storage.updateEndTime(item);
                column -= COL_CLOCK;
            } //추가

            if (column >= COL_CHARGE_COUNT) {
                storage.updateItemChargeCount(item);
                column -= COL_CHARGE_COUNT;
            }

            if (column >= COL_ITEMID) {
                storage.updateItemId(item);
                column -= COL_ITEMID;
            }

            if (column >= COL_DELAY_EFFECT) {
                storage.updateItemDelayEffect(item);
                column -= COL_DELAY_EFFECT;
            }

            if (column >= COL_COUNT) {
                storage.updateItemCount(item);
                column -= COL_COUNT;
            }

            if (column >= COL_EQUIPPED) {
                storage.updateItemEquipped(item);
                column -= COL_EQUIPPED;
            }

            if (column >= COL_ENCHANTLVL) {
                storage.updateItemEnchantLevel(item);
                storage.updateItemProtection(item); //추가장비 보호
                column -= COL_ENCHANTLVL;
            }
            if (column >= COL_IS_ID) {
                storage.updateItemIdentified(item);
                column -= COL_IS_ID;
            }
            if (column >= COL_DURABILITY) {
                storage.updateItemDurability(item);
                column -= COL_DURABILITY;
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    @Override
    public void deleteItem(L1ItemInstance item) {
        try {
            L1RobotItemStorage storage = L1RobotItemStorage.getInstance();
            storage.deleteItem(item);
        } catch (Exception e) {
            logger.error(e);
        }

        if (item.isEquipped()) {
            setEquipped(item, false);
        }

        items.remove(item);
    }

    @Override
    public int checkAddItem(L1ItemInstance item, int count) {
        return OK;
    }

    @Override
    public int checkAddItem(L1ItemInstance item, int count, boolean message) {
        return OK;
    }

    @Override
    public int getWeight240() {
        return 0;
    }

    @Override
    public int getWeight() {
        return 0;
    }
}