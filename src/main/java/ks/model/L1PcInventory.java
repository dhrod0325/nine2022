package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.pc.CharacterItemTable;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.system.dogFight.L1DogFight;
import ks.system.race.L1RaceManager;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

public class L1PcInventory extends L1Inventory {
    public static final int COL_ATTRENCHANTLVL = 1024;
    public static final int COL_BLESS = 512;
    public static final int COL_REMAINING_TIME = 256;
    public static final int COL_CHARGE_COUNT = 128;
    public static final int COL_ITEMID = 64;
    public static final int COL_DELAY_EFFECT = 32;
    public static final int COL_COUNT = 16;
    public static final int COL_EQUIPPED = 8;
    public static final int COL_ENCHANTLVL = 4;
    public static final int COL_IS_ID = 2;
    public static final int COL_DURABILITY = 1;

    private static final Logger logger = LogManager.getLogger(L1PcInventory.class);
    private static final int MAX_SIZE = 180;
    public static int COL_NEXT_REQ = 2048 * 32;
    public static int COL_OPTION = 2048 * 16;
    public static int COL_PANDORA = 2048 * 8;
    public static int COL_PROTEC = 2048 * 4;
    public static int COL_INNA = 2048 * 2;
    public static int COL_CLOCK = 2048; //
    private final L1PcInstance owner;


    public L1PcInventory(L1PcInstance owner) {
        this.owner = owner;
    }

    public L1PcInstance getOwner() {
        return owner;
    }

    public int getWeight240() {
        return calcWeight240(getWeight());
    }

    public int calcWeight240(int weight) {
        int weight240 = 0;

        if (CodeConfig.RATE_WEIGHT_LIMIT != 0) {
            double maxWeight = owner.getMaxWeight();

            if (weight > maxWeight) {
                weight240 = 240;
            } else {
                double wpTemp = (weight * 100 / maxWeight) * 240.00 / 100.00;
                DecimalFormat df = new DecimalFormat("00.##");
                df.format(wpTemp);
                wpTemp = Math.round(wpTemp);
                weight240 = (int) (wpTemp);
            }
        }

        return weight240;
    }

    @Override
    public int checkAddItem(L1ItemInstance item, int count) {
        return checkAddItem(item, count, true);
    }

    public int checkAddItem(L1ItemInstance item, int count, boolean message) {
        if (item == null) {
            return -1;
        }

        if (getSize() >= MAX_SIZE || (getSize() == MAX_SIZE && (!item.isStackable() || !checkItem(item.getItem().getItemId())))) {
            if (message) {
                sendOverMessage(263);
            }

            return SIZE_OVER;
        }

        int weight3 = 0;

        if (item.getCount() <= 1 && count > 0) {
            for (int i = 0; i <= count; i++) {
                weight3 += item.getWeight();
            }
        }

        int weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
        int weight2 = item.getItem().getWeight() * count / 1000;

        if (weight < 0 || weight2 < 0) {
            if (message) {
                sendOverMessage(82);
            }

            return WEIGHT_OVER;
        }

        if (weight3 + getWeight() > owner.getMaxWeight()) {
            if (message) {
                sendOverMessage(82);
            }

            return WEIGHT_OVER;
        }

        if (isOverWeightFull()) {
            if (message) {
                sendOverMessage(82);
            }
            return WEIGHT_OVER;
        }

        if (item.getWeight() > owner.getMaxWeight()) {
            if (message) {
                sendOverMessage(82);
            }

            return WEIGHT_OVER;
        }

        L1ItemInstance itemExist = findItemId(item.getItemId());

        if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
            if (message) {
                getOwner().sendPackets(new S_ServerMessage(166, "소지하고 있는 아데나", "2,000,000,000을 초과하고 있습니다.")); // \f1%0이%4%1%3%2
            }
            return AMOUNT_OVER;
        }

        return OK;
    }

    public void sendOverMessage(int message_id) {
        owner.sendPackets(new S_ServerMessage(message_id));
    }

    @Override
    public void loadItems() {
        loadItems(true);
    }

    public void loadItems(boolean storeWorld) {
        try {
            List<L1ItemInstance> loadItems = CharacterItemTable.getInstance().loadItems(owner.getId());

            for (L1ItemInstance item : loadItems) {
                items.add(item);

                if (item.isEquipped()) {
                    item.setEquipped(false);
                    setEquipped(item, true, true, false);
                }

                if (item.getItemId() == L1RaceManager.SHOP_ITEM_ID) {
                    L1RaceManager.getInstance().loadItems(item);
                } else if (item.getItemId() == L1DogFight.SHOP_ITEM_ID) {
                    L1DogFight.getInstance().loadItems(item);
                }

                if (storeWorld) {
                    L1World.getInstance().storeObject(item);
                }
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    @Override
    public synchronized void insertItem(L1ItemInstance item) {
        owner.sendPackets(new S_AddItem(item));

        if (item.getItem().getWeight() != 0) {
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.WEIGHT, getWeight240()));
        }

        try {
            CharacterItemTable.getInstance().storeItem(owner.getId(), item);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void updateItem(L1ItemInstance item) {
        updateItem(item, COL_COUNT);

        if (item.getItem().isToBeSavedAtOnce()) {
            saveItem(item, COL_COUNT);
        }
    }

    /**
     * 목록내의 아이템 상태를 갱신한다.
     *
     * @param item   -
     *               갱신 대상의 아이템
     * @param column -
     *               갱신하는 스테이터스의 종류
     */
    @Override
    public void updateItem(L1ItemInstance item, int column) {
        if (column >= COL_OPTION) { // 엔챤트
            owner.sendPackets(new S_ItemStatus(item));
            column -= COL_OPTION;
        }

        if (column >= COL_PANDORA) { // 엔챤트
            owner.sendPackets(new S_ItemStatus(item));
            column -= COL_PANDORA;
        }
        if (column >= COL_PROTEC) { // 엔챤트
            owner.sendPackets(new S_ItemStatus(item));
            column -= COL_PROTEC;
        }
        if (column >= COL_INNA) { // 엔챤트
            owner.sendPackets(new S_ItemStatus(item));
            column -= COL_INNA;
        }
        if (column >= COL_ATTRENCHANTLVL) {
            owner.sendPackets(new S_ItemName(item));
            column -= COL_ATTRENCHANTLVL;
        }
        if (column >= COL_BLESS) {
            owner.sendPackets(new S_ItemColor(item));
            column -= COL_BLESS;
        }
        if (column >= COL_REMAINING_TIME) { // 사용 가능한 남은 시간
            owner.sendPackets(new S_ItemName(item));
            column -= COL_REMAINING_TIME;
        }
        if (column >= COL_CLOCK) {
            owner.sendPackets(new S_ItemName(item));
            column -= COL_CLOCK;
        }  //추가
        if (column >= COL_CHARGE_COUNT) { // 사용 가능한 횟수
            owner.sendPackets(new S_ItemName(item));
            column -= COL_CHARGE_COUNT;
        }
        if (column >= COL_ITEMID) { // 다른 아이템이 되는 경우(편지지를 개봉했을 때 등)
            owner.sendPackets(new S_ItemStatus(item));
            owner.sendPackets(new S_ItemColor(item));
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.WEIGHT, getWeight240()));
            column -= COL_ITEMID;
        }
        if (column >= COL_DELAY_EFFECT) {
            column -= COL_DELAY_EFFECT;
        }
        if (column >= COL_COUNT) {// 카운트
            int weight = item.getWeight();
            if (weight != item.getLastWeight()) {
                item.setLastWeight(weight);
            } else {
                owner.sendPackets(new S_ItemName(item));
            }
            owner.sendPackets(new S_ItemStatus(item));
            if (item.getItem().getWeight() != 0) {
                // 무게가 변하지 않았을 경우 그냥 보내도 된다.
                owner.sendPackets(new S_PacketBox(L1PacketBoxType.WEIGHT, getWeight240()));
            }
            column -= COL_COUNT;
        }
        if (column >= COL_EQUIPPED) { // 장비 상태
            owner.sendPackets(new S_ItemName(item));
            column -= COL_EQUIPPED;
        }
        if (column >= COL_ENCHANTLVL) { // 엔챤트
            owner.sendPackets(new S_ItemStatus(item));
            column -= COL_ENCHANTLVL;
        }
        if (column >= COL_IS_ID) { // 확인장태
            owner.sendPackets(new S_ItemStatus(item));
            owner.sendPackets(new S_ItemColor(item));
            column -= COL_IS_ID;
        }
        if (column >= COL_DURABILITY) { // 확인장태
            owner.sendPackets(new S_ItemStatus(item));
            column -= COL_DURABILITY;
        }
    }

    /**
     * 목록내의 아이템 상태를 DB에 보존한다.
     *
     * @param item   -
     *               갱신 대상의 아이템
     * @param column -
     *               갱신하는 스테이터스의 종류
     */
    public void saveItem(L1ItemInstance item, int column) {
        if (column == 0) {
            return;
        }

        try {
            CharacterItemTable storage = CharacterItemTable.getInstance();

            if (column >= COL_OPTION) {
                storage.updateItemOptionGrade(item);
                column -= COL_OPTION;
            }

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
            logger.error(e);
        }
    }

    // DB의 character_items로부터 삭제
    @Override
    public void deleteItem(L1ItemInstance item) {
        try {
            CharacterItemTable.getInstance().deleteItem(item);
        } catch (Exception e) {
            logger.error(e);
        }

        if (item.isEquipped()) {
            setEquipped(item, false);
        }

        owner.sendPackets(new S_DeleteInventoryItem(item));

        if (owner.getCurrentDollItem() != null) {
            if (owner.getCurrentDollItem().equals(item)) {
                owner.getCurrentDoll().deleteDoll();
            }
        }

        items.remove(item);

        if (item.getItem().getWeight() != 0) {
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.WEIGHT, getWeight240()));
        }
    }

    public void setEquipped(L1ItemInstance item, boolean equipped) {
        setEquipped(item, equipped, false, false);
    }

    public int getTypeAndItemIdEquipped(int type2, int type, int itemId) {
        int equipCount = 0;

        for (L1ItemInstance itemObject : items) {
            if (itemObject.getItem().getType2() == type2
                    && itemObject.getItem().getType() == type
                    && itemObject.getItem().getItemId() == itemId
                    && itemObject.isEquipped()) {
                equipCount++;
            }
        }
        return equipCount;
    }

    public int getTypeAndGradeEquipped(int type2, int type, int Grade) {
        int equipCount = 0;

        for (L1ItemInstance item : items) {
            if (item.getItem().getType2() == type2
                    && item.getItem().getType() == type
                    && item.getItem().getGrade() == Grade
                    && item.isEquipped()) { //착용
                equipCount++;
            }
        }
        return equipCount;

    }

    public void setEquipped(L1ItemInstance item, boolean equipped, boolean loaded, boolean changeWeapon) {
        try {
            if (item.isEquipped() != equipped) {
                L1Item temp = item.getItem();

                int itemId = item.getItemId();

                if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
                    if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_INVISI_OFF)) {
                        return;
                    }
                }

                item.setEquipped(equipped);

                if (equipped) {
                    owner.getEquipSlot().set(item);
                } else {
                    owner.getEquipSlot().remove(item);
                }

                if (!loaded) {
                    owner.setCurrentHp(owner.getCurrentHp());
                    owner.setCurrentMp(owner.getCurrentMp());

                    updateItem(item, COL_EQUIPPED);

                    owner.sendPackets(new S_OwnCharStatus(owner));

                    if (temp.getType2() == 1 && !changeWeapon) {
                        owner.sendPackets(new S_CharVisualUpdate(owner));
                        Broadcaster.broadcastPacket(owner, new S_CharVisualUpdate(owner));
                    }

                    owner.getInventory().toSlotPacket(owner, item);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public boolean checkEquipped(int id) {
        for (L1ItemInstance item : new ArrayList<>(items)) {
            if (item.getItem().getItemId() == id && item.isEquipped()) {
                return true;
            }
        }

        return false;
    }

    public boolean checkEquipped(int[] ids) {
        for (int id : ids) {
            if (!checkEquipped(id)) {
                return false;
            }
        }
        return true;
    }

    public int getTypeEquipped(int type2, int type) {
        int equipCount = 0;

        for (Object itemObject : items) {
            L1ItemInstance item = (L1ItemInstance) itemObject;

            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                equipCount++;
            }
        }
        return equipCount;
    }

    public int getGarderEquipped(int type2, int type, int gd) {
        int equipCount = 0;

        for (L1ItemInstance item : items) {
            if (item.getItem().getType2() == type2
                    && item.getItem().getType() == type
                    && item.getItem().getUseType() != gd && item.isEquipped()) {
                equipCount++;
            }
        }
        return equipCount;
    }

    public L1ItemInstance getItemEquipped(int type2, int type) {
        L1ItemInstance equipeitem = null;

        for (L1ItemInstance item : items) {
            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                equipeitem = item;
                break;
            }
        }

        return equipeitem;
    }

    public L1ItemInstance[] getRingEquipped() {
        L1ItemInstance[] equipItem = new L1ItemInstance[2];

        int equipeCount = 0;

        for (L1ItemInstance item : items) {
            if (item.getItem().getType2() == 2 && item.getItem().getType() == 9 && item.isEquipped()) {
                equipItem[equipeCount] = item;
                equipeCount++;

                if (equipeCount == 2) {
                    break;
                }
            }
        }
        return equipItem;
    }

    public void takeoffEquip(int polyid) {
        takeoffWeapon(polyid);
        takeoffArmor(polyid);
    }

    private void takeoffWeapon(int polyid) {
        if (owner.getWeapon() == null) {
            return;
        }

        int weapon_type = owner.getWeapon().getItem().getType();
        boolean takeoff = !L1PolyMorph.isEquipAbleWeapon(polyid, weapon_type);

        if (takeoff) {
            setEquipped(owner.getWeapon(), false, false, false);
        }
    }

    private void takeoffArmor(int polyId) {
        L1ItemInstance armor;

        for (int type = 0; type <= 13; type++) {
            if (getTypeEquipped(2, type) != 0 && !L1PolyMorph.isEquipableArmor(polyId, type)) {
                if (type == 9) {
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }

                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                } else {
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                }
            }
        }
    }


    public void initArrow() {
        L1ItemInstance weapon = owner.getWeapon();

        if (weapon != null) {
            boolean isBow = owner.getWeaponInfo().isBow();
            boolean isGun = owner.getWeaponInfo().isGuntlet();

            if (isBow || isGun) {
                int type;

                if (isBow) {
                    if (weapon.getItemId() == 190) {
                        arrowClose();
                        return;
                    }

                    type = 0;
                } else {
                    type = 15;
                }

                for (L1ItemInstance item : items) {
                    if (item.getItem().getType() == type && item.getItem().getType1() == 0) {
                        if (type == 0) {
                            setArrowId(item.getItemId());
                        }

                        if (type == 15) {
                            setStingId(item.getItemId());
                        }

                        for (L1ItemInstance oo : getArrowList(0)) {
                            oo.setPc(owner);
                            owner.sendPackets(new S_ItemName(oo));
                        }

                        for (L1ItemInstance oo : getArrowList(15)) {
                            oo.setPc(owner);
                            owner.sendPackets(new S_ItemName(oo));
                        }

                        return;
                    }
                }
            } else {
                arrowClose();
            }
        } else {
            arrowClose();
        }
    }

    public void arrowClose() {
        if (getArrowId() != 0) {
            L1ItemInstance oo = getArrow();

            setArrowId(0);

            if (oo != null) {
                oo.setPc(owner);

                owner.sendPackets(new S_ItemName(oo));
            }
        }
    }

    public int hpRegenPerTick() {
        int hpr = 0;
        for (L1ItemInstance item : items) {
            if (item.isEquipped()) {
                hpr += item.getAddHpr();
            }
        }
        return hpr;
    }

    public int mpRegenPerTick() {
        int mpr = 0;

        for (L1ItemInstance item : items) {
            if (item.isEquipped()) {
                mpr += item.getAddMpr();
            }
        }

        return mpr;
    }

    public L1ItemInstance caoPenaltyDropItem() {
        try {
            if (items.isEmpty()) {
                return null;
            }

            int rnd = RandomUtils.nextInt(items.size());

            L1ItemInstance penaltyItem = items.get(rnd);

            if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA || !penaltyItem.getItem().isTradeAble()) {
                return null;
            }

            if (penaltyItem.getItem().getName().startsWith("마법인형 :")) {
                return null;
            }

            Collection<L1NpcInstance> petList = owner.getPetList().values();

            for (Object petObject : petList) {
                if (petObject instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) petObject;
                    if (penaltyItem.getId() == pet.getItemObjId()) {
                        return null;
                    }
                }
            }

            setEquipped(penaltyItem, false);

            return penaltyItem;
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    /**
     * 수량성 템 복사 방지
     **/
    public void checkCloneItem() {
        int count = 0;

        Map<Integer, L1ItemInstance> stackableItems = new HashMap<>();

        for (L1ItemInstance item : items) {
            if (item.getItem().isStackable()) {
                stackableItems.put(count, item);
                count++;
            }
        }

        for (L1ItemInstance item : items) {
            if (item.getItemId() == L1RaceManager.SHOP_ITEM_ID || item.getItemId() == L1DogFight.SHOP_ITEM_ID) {
                continue;
            }

            if (item.getItem().isStackable()) {
                for (int i = 0; i < stackableItems.size(); i++) {
                    if (item.getItem().getItemId() == stackableItems.get(i).getItem().getItemId()) {
                        L1ItemInstance alreadyItem = stackableItems.get(i);

                        if (item.getId() != alreadyItem.getId()) {
                            int plusCount = item.getCount();
                            int itemId = item.getItemId();

                            removeItem(item);

                            storeItem(itemId, plusCount);
                        }

                        break;
                    }
                }
            }
        }

        stackableItems.clear();
    }

    public void makeDeleteEnchant(int itemId, int enchantLevel) {
        L1ItemInstance item = findItemId(itemId);
        if (item != null && item.getEnchantLevel() == enchantLevel) {
            removeItem(item, 1);
        }
    }

    public boolean isOverWeightFull() {
        return getWeight240() >= 240;
    }

    public boolean isOverWeight82() {
        return getWeight240() >= 200;
    }

    public boolean isOverWeight48() {
        return getWeight240() >= 120;
    }

    public boolean isFullCount() {
        return getSize() >= 180;
    }

    public boolean isFullWeightOrFullCount() {
        return isOverWeightFull() || isFullCount();
    }
}
