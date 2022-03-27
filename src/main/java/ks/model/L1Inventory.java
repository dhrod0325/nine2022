package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.FurnitureSpawnTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.next_items.CharacterNextReqTable;
import ks.core.datatables.pet.PetTable;
import ks.model.instance.L1FurnitureInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.Warehouse;
import ks.packets.serverpackets.S_EquipmentWindow;
import ks.system.dogFight.L1DogFight;
import ks.system.dogFight.L1DogFightTicketTable;
import ks.system.race.L1RaceManager;
import ks.system.race.datatable.L1RaceTicketTable;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1Inventory extends L1Object {
    private final Logger logger = LogManager.getLogger();

    public static final int MAX_AMOUNT = 2000000000; // 2G
    public static final int MAX_WEIGHT = 1500;
    public static final int OK = 0;
    public static final int SIZE_OVER = 1;
    public static final int WEIGHT_OVER = 2;
    public static final int AMOUNT_OVER = 3;

    private final L1CurrentItem currentItem = new L1CurrentItem();

    // 아이템패킷추가
    public int[] slotRing = new int[4];
    public int[] slotRune = new int[3];
    protected List<L1ItemInstance> items = new CopyOnWriteArrayList<>();
    private int arrowId = 0;
    private int stingId = 0;

    public L1Inventory() {
        Arrays.fill(slotRing, 0);
        Arrays.fill(slotRune, 0);
    }

    public L1CurrentItem getCurrentItem() {
        return currentItem;
    }

    public int getTypeAndItemIdEquipped(int type2, int type, int itemId) {
        int equipCount = 0;

        for (L1ItemInstance item : items) {
            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.getItem().getItemId() == itemId && item.isEquipped()) {
                equipCount++;
            }
        }
        return equipCount;
    }

    public void toSlotPacket(L1PcInstance pc, L1ItemInstance item) {
        toSlotPacket(pc, item, S_EquipmentWindow.TYPE_EQUIP_ACTION);
    }

    public void toSlotPacket(L1PcInstance pc, L1ItemInstance item, int type) {
        try {
            int selectIdx = -1;
            int idx = 0;

            if (item.getItem().getType2() == 2) {
                switch (item.getItem().getType()) {
                    case 1:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_HEML;
                        currentItem.setHelm(item);
                        break;
                    case 2:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR;
                        currentItem.setArmor(item);
                        break;
                    case 3:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_T;
                        currentItem.setShirt(item);
                        break;
                    case 4:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK;
                        currentItem.setCloak(item);
                        break;
                    case 5:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE;
                        currentItem.setGlove(item);
                        break;
                    case 6:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS;
                        currentItem.setBoots(item);
                        break;
                    case 7:
                    case 13:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
                        currentItem.setShield(item);
                        break;
                    case 8:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_NECKLACE;
                        currentItem.setNecklace(item);
                        break;
                    case 9:
                    case 11:
                        for (int i = 0; i < slotRing.length; ++i) {
                            if (slotRing[i] == item.getId()) {
                                selectIdx = i;
                            }
                        }

                        if (item.isEquipped() && selectIdx == -1) {
                            for (int i = 0; i < slotRing.length; i++) {
                                if (slotRing[i] == 0) {
                                    slotRing[i] = item.getId();
                                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_RING1 + i;
                                    currentItem.setRing(i, item);
                                    break;
                                }
                            }
                        }

                        if (!item.isEquipped() && selectIdx != -1) {
                            slotRing[selectIdx] = 0;
                            idx = S_EquipmentWindow.EQUIPMENT_INDEX_RING1 + selectIdx;
                            currentItem.removeRing(selectIdx);
                        }
                        break;
                    case 10:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_BELT;
                        currentItem.setBelt(item);
                        break;
                    case 12:
                        idx = S_EquipmentWindow.EQUIPMENT_INDEX_EARRING;
                        currentItem.setEarRing(item);
                        break;
                    case 14:
                        for (int i = 0; i < slotRune.length; ++i) {
                            if (slotRune[i] == item.getId()) {
                                selectIdx = i;
                            }
                        }

                        //아이템 착용 패킷이고 착용한 링이 없는 경우
                        if (item.isEquipped() && selectIdx == -1) {
                            for (int i = 0; i < slotRune.length; ++i) {
                                if (slotRune[i] == 0) {
                                    slotRune[i] = item.getId();
                                    idx = S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1 + i;
                                    currentItem.addRune(selectIdx, item);
                                    break;
                                }
                            }
                        }

                        //착용해제이고 착용한 링이 있는경우
                        if (!item.isEquipped() && selectIdx != -1) {
                            slotRune[selectIdx] = 0;
                            idx = S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1 + selectIdx;
                            currentItem.removeRune(selectIdx);
                        }

                        break;
                }
            } else {
                idx = S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON;
                currentItem.setWeapon(item);
            }

            if (idx != 0) {
                pc.sendPackets(new S_EquipmentWindow(item.getId(), idx, item.isEquipped(), type));
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public int getSize() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<L1ItemInstance> getItems() {
        return items;
    }

    public void setItems(List<L1ItemInstance> items) {
        this.items = items;
    }

    public int getWeight() {
        int weight = 0;

        for (L1ItemInstance item : items) {
            weight += item.getWeight();
        }

        weight /= CodeConfig.RATE_WEIGHT_LIMIT;

        return weight;
    }

    public int checkAddItem(L1ItemInstance item, int count) {
        if (item == null) {
            return -1;
        }

        if (item.getCount() <= 0 || count <= 0 || item.getCount() < count) {
            return -1;
        }

        boolean check1 = getSize() > CodeConfig.MAX_NPC_ITEM;
        boolean check2 = (getSize() == CodeConfig.MAX_NPC_ITEM && (!item.isStackable() || !checkItem(item.getItem().getItemId())));

        if (check1 || check2) { // 용량 확인
            return SIZE_OVER;
        }

        int weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;

        if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
            return WEIGHT_OVER;
        }

        if (weight > (MAX_WEIGHT * CodeConfig.RATE_WEIGHT_LIMIT_PET)) {
            return WEIGHT_OVER;
        }

        L1ItemInstance itemExist = findItemId(item.getItemId());
        if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
            return AMOUNT_OVER;
        }

        return OK;
    }

    public synchronized L1ItemInstance storeItem(int itemId, int count, int enchant, boolean identified) {
        if (count <= 0) {
            return null;
        }

        L1Item temp = ItemTable.getInstance().getTemplate(itemId);

        if (temp == null) {
            return null;
        }

        if (temp.isStackable()) {
            L1ItemInstance item = ItemTable.getInstance().functionItem(temp);
            item.setCount(count);

            if (findItemId(itemId) == null) {
                item.setId(ObjectIdFactory.getInstance().nextId());
                L1World.getInstance().storeObject(item);
            }

            return storeItem(item);
        }

        L1ItemInstance item = null;

        for (int i = 0; i < count; i++) {
            item = ItemTable.getInstance().functionItem(temp);
            item.setId(ObjectIdFactory.getInstance().nextId());
            item.setEnchantLevel(enchant);
            item.setIdentified(identified);
            L1World.getInstance().storeObject(item);
            storeItem(item);
        }

        return item;
    }

    public synchronized L1ItemInstance storeItem(int id, int count, int enchant) {
        return storeItem(id, count, enchant, false);
    }

    public synchronized L1ItemInstance storeItem(int id, int count) {
        return storeItem(id, count, 0);
    }

    public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
        if (item.getCount() <= 0) {
            return null;
        }

        int itemId = item.getItem().getItemId();

        if (item.isStackable()) {
            L1ItemInstance findItem;

            if (itemId == L1RaceManager.SHOP_ITEM_ID || itemId == L1DogFight.SHOP_ITEM_ID) {
                findItem = findItemNameId(item.getItem().getNameId());
            } else {
                findItem = findItemId(itemId);
            }

            if (findItem != null) {
                findItem.setCount(findItem.getCount() + item.getCount());
                updateItem(findItem);
                return findItem;
            }
        }

        if (itemId == L1RaceManager.SHOP_ITEM_ID) {
            L1RaceManager.getInstance().storeItem(item);
        } else if (itemId == L1DogFight.SHOP_ITEM_ID) {
            L1DogFight.getInstance().storeItem(item);
        }

        item.setX(getX());
        item.setY(getY());
        item.setMap(getMapId());

        int chargeCount = item.getItem().getMaxChargeCount();

        switch (itemId) {
            case 20383:
                chargeCount = 50;
                break;
            case 40006:
            case 40007:
            case 46091:
            case 40008:
            case 45464:
            case 41401:
            case 140006:
            case 140008:
                L1ItemInstance findItem = findItemId(itemId);

                if (findItem != null) {
                    chargeCount -= RandomUtils.nextInt(5);
                    findItem.setChargeCount(findItem.getChargeCount() + chargeCount);
                    updateItem(findItem);
                    return findItem;
                }
                break;
            case 5000121:
                chargeCount -= RandomUtils.nextInt(5);
                break;
            case 40903:
            case 40904:
            case 40905:
                chargeCount = itemId - 40902;
                break;
            case 40906:
                chargeCount = 5;
                break;
            case 40907:
            case 40908:
                chargeCount = 20;
                break;
        }

        item.setChargeCount(chargeCount);

        if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light
            item.setRemainingTime(item.getItem().getLightFuel());
        } else {
            item.setRemainingTime(item.getItem().getMaxUseTime());
        }

        if (item.getBless() == 1) {
            item.setBless(item.getItem().getBless());
        }

        if (item.getItem().getDeleteSecond() > 0) {
            Timestamp deleteTime = new Timestamp(System.currentTimeMillis() + (1000L * item.getItem().getDeleteSecond()));
            item.setEndTime(deleteTime);
        }

        if (!item.isIdentified()) {
            item.setIdentified(false);
        }

        items.add(item);

        insertItem(item);

        return item;
    }

    public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
        int itemId = item.getItem().getItemId();

        if (item.isStackable()) {
            L1ItemInstance findItem = findItemId(item.getItem().getItemId());
            if (findItem != null) {
                findItem.setCount(findItem.getCount() + item.getCount());
                updateItem(findItem);
                return findItem;
            }
        }

        switch (itemId) {
            case 40006:
            case 40007:
            case 40008:
            case 41401:
            case 140006:
            case 140008:
            case 46091:
                L1ItemInstance findItem = findItemId(itemId);

                if (findItem != null) {
                    int chargeCount = item.getChargeCount();
                    findItem.setChargeCount(findItem.getChargeCount() + chargeCount);
                    updateItem(findItem);

                    return findItem;
                }

                break;
        }

        item.setX(getX());
        item.setY(getY());
        item.setMap(getMapId());
        items.add(item);
        insertItem(item);

        return item;
    }

    public boolean consumeItem(int itemid, int count) {
        if (count <= 0) {
            return false;
        }

        if (ItemTable.getInstance().getTemplate(itemid).isStackable()) {
            L1ItemInstance item = findItemId(itemid);
            if (item != null && item.getCount() >= count) {
                removeItem(item, count);
                return true;
            }
        } else {
            List<L1ItemInstance> itemList = findItemsId(itemid);
            if (itemList.size() == count) {
                for (int i = 0; i < count; i++) {
                    removeItem(itemList.get(i), 1);
                }

                return true;
            } else if (itemList.size() > count) {
                DataComparator dc = new DataComparator();
                extracted(itemList, dc);

                for (int i = 0; i < count; i++) {
                    removeItem(itemList.get(i), 1);
                }
                return true;
            }
        }
        return false;
    }

    public boolean consumeItem(int itemId) {
        L1ItemInstance item = findItemId(itemId);
        if (item != null) {
            removeItem(item, item.getCount());
            return true;
        }
        return false;
    }

    private void extracted(List<L1ItemInstance> itemList, DataComparator dc) {
        itemList.sort(dc);
    }

    public int removeItem(int objectId, int count) {
        L1ItemInstance item = getItem(objectId);
        return removeItem(item, count);
    }

    public void removeItemById(int itemId, int count) {
        L1ItemInstance item = findItemId(itemId);

        removeItem(item, count);
    }

    public int removeItem(L1ItemInstance item) {
        return removeItem(item, item.getCount());
    }

    public int removeItem(L1ItemInstance item, int count) {
        if (item == null) {
            return 0;
        }

        if (item.getCount() <= 0 || count <= 0) {
            return 0;
        }

        if (item.getCount() < count) {
            count = item.getCount();
        }

        if (item.getCount() == count) {
            int itemId = item.getItem().getItemId();

            if (itemId == 40314 || itemId == 40316) {
                PetTable.getInstance().deletePet(item.getId());
            } else if (itemId >= 41383 && itemId <= 41400) {
                Collection<L1Object> ob = L1World.getInstance().getAllObject();

                for (L1Object l1object : ob) {
                    if (l1object == null)
                        continue;

                    if (l1object instanceof L1FurnitureInstance) {
                        L1FurnitureInstance obj = (L1FurnitureInstance) l1object;
                        if (obj.getItemObjId() == item.getId()) {
                            FurnitureSpawnTable.getInstance().deleteFurniture(obj);
                        }
                    }
                }
            } else if (item.getItemId() == L1RaceManager.SHOP_ITEM_ID) {
                L1RaceTicketTable.getInstance().deleteTicket(item.getId());
            } else if (item.getItemId() == L1DogFight.SHOP_ITEM_ID) {
                L1DogFightTicketTable.getInstance().deleteTicket(item.getId());
            }

            deleteItem(item);
            L1World.getInstance().removeObject(item);
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
        }

        return count;
    }

    public void deleteItem(L1ItemInstance item) {
        items.remove(item);
    }

    public synchronized L1ItemInstance tradeItem(int objectId, int count,
                                                 Warehouse inventory) {
        L1ItemInstance item = getItem(objectId);

        return tradeItem(item, count, inventory);
    }

    public synchronized L1ItemInstance tradeItem(int objectId, int count,
                                                 L1Inventory inventory) {
        L1ItemInstance item = getItem(objectId);
        return tradeItem(item, count, inventory);
    }

    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, Warehouse inventory) {
        if (item == null) {
            return null;
        }

        if (item.getCount() <= 0 || count <= 0) {
            return null;
        }

        if (item.isEquipped()) {
            return null;
        }

        if (!checkItem(item.getItem().getItemId(), count)) {
            return null;
        }

        L1ItemInstance carryItem;

        if (item.getCount() <= count) {
            deleteItem(item);
            carryItem = item;
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
            carryItem = ItemTable.getInstance().createItem(item.getItem().getItemId());
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.setDurability(item.getDurability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getItem().getBless());
            carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
            carryItem.setProtection(item.getProtection());//추가장비보호
            carryItem.setSecondId(item.getSecondId());
            carryItem.setRoundId(item.getRoundId());
            carryItem.setTicketId(item.getTicketId());
            carryItem.setOptionGrade(item.getOptionGrade());
        }

        return inventory.storeTradeItem(carryItem);
    }

    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, L1Inventory inventory) {
        if (item == null) {
            return null;
        }

        if (item.getCount() <= 0 || count <= 0) {
            return null;
        }

        if (item.isEquipped()) {
            return null;
        }

        if (!checkItem(item.getItem().getItemId(), count)) {
            return null;
        }

        if (item.getNextReq() == 1) {
            item.setNextReq(0);
            CharacterNextReqTable.getInstance().deleteByItemObjId(item.getId());
        }

        L1ItemInstance carryItem;

        if (item.getCount() <= count) {
            deleteItem(item);
            carryItem = item;
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);

            carryItem = ItemTable.getInstance().createItem(item.getItem().getItemId());
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.setDurability(item.getDurability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getItem().getBless());
            carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
            carryItem.setProtection(item.getProtection()); //추가장비 보호
            carryItem.setOptionGrade(item.getOptionGrade());
        }

        return inventory.storeTradeItem(carryItem);
    }

    public L1ItemInstance receiveDamage(int objectId) {
        L1ItemInstance item = getItem(objectId);
        return receiveDamage(item);
    }

    public L1ItemInstance receiveDamage(L1ItemInstance item) {
        return receiveDamage(item, 1);
    }

    public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
        int itemType = item.getItem().getType2();
        int currentDurability = item.getDurability();

        if ((currentDurability == 0 && itemType == 0) || currentDurability < 0) {
            item.setDurability(0);
            return null;
        }

        if (itemType == 0) {
            int minDurability = (item.getEnchantLevel() + 5) * -1;
            int durability = currentDurability - count;

            if (durability < minDurability) {
                durability = minDurability;
            }
            if (currentDurability > durability) {
                item.setDurability(durability);
            }
        } else {
            int maxDurability = item.getEnchantLevel() + 5;
            int durability = currentDurability + count;

            if (durability > maxDurability) {
                durability = maxDurability;
            }

            if (currentDurability < durability) {
                item.setDurability(durability);
            }
        }

        updateItem(item, L1PcInventory.COL_DURABILITY);

        return item;
    }

    public void recoveryDamage(L1ItemInstance item) {
        int itemType = item.getItem().getType2();
        int durability = item.getDurability();

        if ((durability == 0 && itemType != 0) || durability < 0) {
            item.setDurability(0);
            return;
        }

        if (itemType == 0) {
            item.setDurability(durability + 1);
        } else {
            item.setDurability(durability - 1);
        }

        updateItem(item, L1PcInventory.COL_DURABILITY);
    }

    public L1ItemInstance findEquippedItemId(int id) {
        for (L1ItemInstance item : items) {
            if (item == null)
                continue;

            if ((item.getItem().getItemId() == id) && item.isEquipped()) {
                return item;
            }
        }
        return null;
    }

    public L1ItemInstance findItemObjId(int id) {
        for (L1ItemInstance item : items) {
            if (item == null)
                continue;

            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public L1ItemInstance findItemId(int itemId) {
        for (L1ItemInstance item : items) {
            if (item == null)
                continue;

            if (item.getItemId() == itemId) {
                return item;
            }
        }

        return null;
    }

    public L1ItemInstance findItemNameId(String nameId) {
        for (L1ItemInstance item : items) {
            if (nameId.equals(item.getItem().getNameId())) {
                return item;
            }
        }
        return null;
    }

    public List<L1ItemInstance> findItemsId(int id) {
        List<L1ItemInstance> itemList = new ArrayList<>();

        for (L1ItemInstance item : items) {
            if (item == null)
                continue;

            if (item.getItemId() == id) {
                itemList.add(item);
            }
        }

        return itemList;
    }

    public List<L1ItemInstance> findItemsIdNotEquipped(int id, int enchant, int bless) {
        List<L1ItemInstance> itemList = new ArrayList<>();

        for (L1ItemInstance item : items) {
            if (item == null)
                continue;
            if (item.getItemId() == id && item.getEnchantLevel() == enchant) {
                if (!item.isEquipped()) {
                    if (bless != 0) {
                        if (item.getBless() == bless) {
                            itemList.add(item);
                        }
                    } else {
                        itemList.add(item);
                    }
                }
            }
        }

        return itemList;
    }

    public List<L1ItemInstance> findItemsIdNotEquipped(int id) {
        List<L1ItemInstance> itemList = new ArrayList<>();

        for (L1ItemInstance item : items) {
            if (item == null)
                continue;
            if (item.getItemId() == id) {
                if (!item.isEquipped()) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    public L1ItemInstance getItem(int objectId) {
        for (L1ItemInstance item : items) {
            if (item == null) {
                continue;
            }

            if (item.getId() == objectId) {
                return item;
            }
        }

        return null;
    }

    public L1ItemInstance getAdena() {
        for (L1ItemInstance item : items) {
            if (item == null) {
                continue;
            }
            if (item.getItemId() == L1ItemId.ADENA) {
                return item;
            }
        }

        return null;
    }

    public int getAdenaCount() {
        L1ItemInstance adena = getAdena();

        if (adena == null) {
            return 0;
        } else {
            return adena.getCount();
        }
    }

    public void makeDeleteEnchant(int itemid, int enchantLevel) {
        L1ItemInstance item = findItemId(itemid);

        if (item != null && item.getEnchantLevel() == enchantLevel) {
            removeItem(item, 1);
        }
    }

    public boolean makeCheckEnchant(int id, int enchantLevel) {
        L1ItemInstance item = findItemId(id);

        if (item == null) {
            return false;
        }

        if (item.getCount() <= 0) {
            return false;
        }

        return item.getEnchantLevel() == enchantLevel && item.getCount() >= 1;
    }

    public int makeCheckEnchantAndNotEqCount(int id, int enchantLevel) {
        int searchCount = 0;

        for (L1ItemInstance item : items) {
            if (item.isEquipped()) {
                continue;
            }

            if (item.getCount() <= 0) {
                continue;
            }

            if (item.getItemId() == id && item.getEnchantLevel() == enchantLevel) {
                searchCount++;
            }
        }

        return searchCount;
    }

    public boolean checkItem(int itemId, int count, int enchant) {
        return checkItem(itemId, count, enchant, 1);
    }

    public boolean checkItem(int itemId, int count, int enchant, int bless) {
        if (count == 0) {
            return true;
        }

        int checkCount = 0;

        for (L1ItemInstance item : items) {
            if (item.isEquipped())
                continue;

            if (item.getItemId() == itemId && enchant == item.getEnchantLevel() && item.getBless() == bless) {
                checkCount++;
            }

            if (checkCount == count)
                return true;
        }

        return false;
    }

    public List<L1ItemInstance> getItems(int itemId, int count, int enchant) {
        return getItems(itemId, count, enchant, 1);
    }

    public List<L1ItemInstance> getItems(int itemId, int count, int enchant, int bless) {
        if (count == 0) {
            return Collections.emptyList();
        }

        List<L1ItemInstance> result = new ArrayList<>();

        int checkCount = 0;

        for (L1ItemInstance item : items) {
            if (item.isEquipped())
                continue;


            if (item.getItemId() == itemId && enchant == item.getEnchantLevel() && item.getBless() == bless) {
                checkCount++;
                result.add(item);
            }

            if (checkCount == count)
                break;
        }

        return result;
    }

    public boolean checkItem(int id) {
        return checkItem(id, 1);
    }

    public boolean checkItem(int id, int count) {
        if (count == 0) {
            return true;
        }

        L1Item tem = ItemTable.getInstance().getTemplate(id);

        if (tem == null) {
            return false;
        }

        if (tem.isStackable()) {
            L1ItemInstance item = findItemId(id);
            return item != null && item.getCount() >= count;
        } else {
            List<L1ItemInstance> itemList = findItemsId(id);
            return itemList.size() >= count;
        }
    }

    public boolean checkItemNotEquipped(int id, int count) {
        if (count == 0) {
            return true;
        }
        return count <= countItems(id);
    }

    public boolean checkItem(int[] ids) {
        int len = ids.length;
        int[] counts = new int[len];

        for (int i = 0; i < len; i++) {
            counts[i] = 1;
        }

        return checkItem(ids, counts);
    }

    public boolean checkItem(int[] ids, int[] counts) {
        for (int i = 0; i < ids.length; i++) {
            if (checkItem(ids[i], counts[i])) {
                return true;
            }
        }

        return false;
    }

    public int countItems(int id) {
        if (ItemTable.getInstance().getTemplate(id).isStackable()) {
            L1ItemInstance item = findItemId(id);
            if (item != null) {
                return item.getCount();
            }
        } else {
            List<L1ItemInstance> itemList = findItemsIdNotEquipped(id);
            return itemList.size();
        }
        return 0;
    }

    public void shuffle() {
        Collections.shuffle(items);
    }

    public void clearItems() {
        for (L1ItemInstance item : items) {
            if (item == null)
                continue;

            L1World.getInstance().removeObject(item);
        }

        items.clear();
    }

    public void loadItems() {
    }

    public void insertItem(L1ItemInstance item) {
    }

    public void updateItem(L1ItemInstance item) {
    }

    public void updateItem(L1ItemInstance item, int colmn) {
    }

    public L1ItemInstance getItemOne(int[] ids) {
        for (int id : ids) {
            for (L1ItemInstance item : items) {
                if (item == null) {
                    continue;
                }

                if (item.getItemId() == id) {
                    return item;
                }
            }
        }

        return null;
    }

    public L1ItemInstance getArrow() {
        return getBullet(0);
    }

    public L1ItemInstance getSting() {
        return getBullet(15);
    }

    public List<L1ItemInstance> getArrowList(int type) {
        List<L1ItemInstance> results = new ArrayList<>();

        for (L1ItemInstance item : items) {
            if (item.getItem().getType() == type && item.getItem().getType1() == 0) {
                if (type == 0) {
                    results.add(item);
                }

                if (type == 15) {
                    results.add(item);
                }
            }
        }

        return results;
    }

    public int getArrowId() {
        return arrowId;
    }

    public void setArrowId(int id) {
        this.arrowId = id;
    }

    public void setStingId(int id) {
        this.stingId = id;
    }

    public L1ItemInstance getBullet(int type) {
        int itemId = 0;

        if (type == 0) {
            if (arrowId == 0) {
                return null;
            }

            itemId = arrowId;
        }

        if (type == 15) {
            if (stingId == 0) {
                return null;
            }

            itemId = stingId;
        }

        if (itemId > 0) {
            return findItemId(itemId);
        }

        return null;
    }

    public static class DataComparator implements Comparator<L1ItemInstance> {
        @Override
        public int compare(L1ItemInstance item1, L1ItemInstance item2) {
            return item1.getEnchantLevel() - item2.getEnchantLevel();
        }
    }
}
