package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.pet.PetItemTable;
import ks.core.datatables.pet.PetTable;
import ks.core.datatables.pet.PetTypeTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.item.function.potion.HealingPotion;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.scheduler.npc.NpcDeleteScheduler;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class L1PetInstance extends L1NpcInstance {
    private static final Logger logger = LogManager.getLogger(L1PetInstance.class);
    private final L1PcInstance petMaster;
    private int damageByWeapon;
    private int currentPetStatus;
    private int itemObjId;
    private L1PetType type;
    private int expPercent;
    private L1ItemInstance weapon;
    private L1ItemInstance armor;
    private int hitByWeapon;
    private int food = 0;
    private int foodTime = 0;

    public L1PetInstance(L1Npc template, L1PcInstance master, L1Pet l1pet) {
        super(template);

        petMaster = master;
        itemObjId = l1pet.getItemobjId();
        type = PetTypeTable.getInstance().get(template.getNpcId());

        setId(l1pet.getObjId());
        setName(l1pet.getName());
        setLevel(l1pet.getLevel());
        setMaxHp(l1pet.getHp());
        setCurrentHp(l1pet.getHp());
        setMaxMp(l1pet.getMp());
        setCurrentMp(l1pet.getMp());
        setExp(l1pet.getExp());
        setExpPercent(ExpTable.getInstance().getExpPercentage(l1pet.getLevel(), l1pet.getExp()));
        setLawful(l1pet.getLawful());
        setTempLawful(l1pet.getLawful());
        setFood(l1pet.getFood());
        setFoodTime(l1pet.getFoodTime());

        setMaster(master);
        setX(master.getX() + RandomUtils.nextInt(5) - 2);
        setY(master.getY() + RandomUtils.nextInt(5) - 2);
        setMap(master.getMapId());
        setHeading(5);
        setLightSize(template.getLightSize());

        L1PetInventory inv = new L1PetInventory(this);
        inv.loadItems();

        setInventory(inv);

        currentPetStatus = 3;

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc != null) {
                onPerceive(pc);
            }
        }

        master.addPet(this);
        master.sendPackets(new S_ReturnedStat(12, (master.getPetList().size() + 1) * 3, getId(), true));
    }

    public L1PetInstance(L1NpcInstance target, L1PcInstance master, int itemId) {
        super(null);

        petMaster = master;
        itemObjId = itemId;
        type = PetTypeTable.getInstance().get(target.getTemplate().getNpcId());

        setId(ObjectIdFactory.getInstance().nextId());
        settingTemplate(target.getTemplate());
        setCurrentHp(target.getCurrentHp());
        setCurrentMp(target.getCurrentMp());
        setExp(750);
        setExpPercent(0);
        setLawful(0);
        setTempLawful(0);
        setFood(0);

        setMaster(master);
        setX(target.getX());
        setY(target.getY());
        setMap(target.getMapId());
        setHeading(target.getHeading());
        setLightSize(target.getLightSize());
        setPetCost(6);

        L1PetInventory inv = new L1PetInventory(this, target.getInventory());
        inv.loadItems();

        setInventory(inv);
        target.setInventory(null);

        currentPetStatus = 3;

        target.deleteMe();

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc != null)
                onPerceive(pc);
        }

        master.addPet(this);

        master.sendPackets(new S_ReturnedStat(12, (master.getPetList().size() + 1) * 3, getId(), true));

        getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, 1200 * 1000);
        PetTable.getInstance().storeNewPet(target, getId(), itemId);
    }

    public void removeMe() {
        if (getArmor() != null) {
            removePetArmor(getArmor());
        }
        if (getWeapon() != null) {
            removePetWeapon(getWeapon());
        }

        collect();

        int time = getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PET_FOOD);
        PetTable.getInstance().storePetFoodTime(getId(), getFood(), time);
        getSkillEffectTimerSet().clearSkillEffectTimer();

        if (master != null) {
            master.getPetList().remove(getId());
        }

        deleteMe();
    }

    @Override
    public synchronized void deleteMe() {
        try {
            if (master == null) {
                return;
            }

            if (master.getPetList() == null) {
                return;
            }

            Collection<L1NpcInstance> petList = master.getPetList().values();

            int i = 0;

            for (L1NpcInstance pet : petList) {
                if (pet.equals(this)) {
                    master.removePet(this);
                    master.sendPackets(new S_ReturnedStat(12, i * 3, getId(), false));
                }

                i++;
            }

            super.deleteMe();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public boolean noTarget() {
        if (currentPetStatus == 3) {
            return true;
        } else if (currentPetStatus == 4) {
            if (petMaster != null && petMaster.getMapId() == getMapId() && getLocation().getTileLineDistance(petMaster.getLocation()) < 5) {
                int dir = targetReverseDirection(petMaster.getX(), petMaster.getY());
                dir = L1CharPosUtils.checkObject(getX(), getY(), getMapId(), dir);
                toMoveDirection(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            } else {
                currentPetStatus = 3;
                return true;
            }
        } else if (currentPetStatus == 5) {
            if (Math.abs(getHomeX() - getX()) > 1
                    || Math.abs(getHomeY() - getY()) > 1) {
                int dir = moveDirection(getHomeX(), getHomeY());
                if (dir == -1) {
                    setHomeX(getX());
                    setHomeY(getY());
                } else {
                    toMoveDirection(dir);
                    setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                }
            }
        } else if (currentPetStatus == 7) {
            if (petMaster != null && petMaster.getMapId() == getMapId() && getLocation().getTileLineDistance(petMaster.getLocation()) <= 1) {
                currentPetStatus = 3;
                return true;
            }

            if (petMaster != null) {
                int x = petMaster.getX() + RandomUtils.nextInt(1);
                int y = petMaster.getY() + RandomUtils.nextInt(1);
                int dir = moveDirection(x, y);

                if (dir == -1) {
                    currentPetStatus = 3;
                    return true;
                }

                toMoveDirection(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            }
        } else if (petMaster != null && petMaster.getMapId() == getMapId()) {
            if (getLocation().getTileLineDistance(petMaster.getLocation()) > 2) {
                int dir = moveDirection(petMaster.getX(), petMaster.getY());
                if (dir == -1) {
                    currentPetStatus = 3;
                    return true;
                }

                toMoveDirection(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));

                if (currentPetStatus == 8) {
                    collect();
                }
            }
        } else {
            currentPetStatus = 3;
            return true;
        }
        return false;
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0) {
            if (damage > 0) {
                setHate(attacker, 0);

                L1SkillUtils.removeSleep(this);
            }

            if (attacker instanceof L1PcInstance && damage > 0) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }

            int newHp = getCurrentHp() - damage;

            if (newHp <= 0) {
                death();
            } else {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            death();
        }
    }

    public synchronized void death() {
        if (!isDead()) {
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
            setCurrentHp(0);

            getMap().setPassable(getLocation(), true);
            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Die));

            //master.sendPackets("펫이 사망했습니다 15분 이내에 부활시키지 않으면 사라집니다.");

            NpcDeleteScheduler.getInstance().addNpcDelete(this, 1000 * 60 * 15, npc -> removeMe());
        }
    }

    public void evolvePet(int newItemObjId) {
        L1Pet pet = PetTable.getInstance().getTemplate(itemObjId);

        if (pet == null) {
            return;
        }

        String oldName = pet.getName();
        String tempName = type.getName();
        int newNpcId = type.getNpcIdForEvolving();
        int tmpMaxHp = getMaxHp();
        int tmpMaxMp = getMaxMp();

        transform(newNpcId);
        type = PetTypeTable.getInstance().get(newNpcId);

        setLevel(1);
        setMaxHp(tmpMaxHp / 2);
        setMaxMp(tmpMaxMp / 2);
        setCurrentHp(getMaxHp());
        setCurrentMp(getMaxMp());
        setExp(0);
        setExpPercent(0);

        getInventory().clearItems();

        PetTable.getInstance().deletePet(itemObjId);

        pet.setItemobjId(newItemObjId);
        pet.setNpcId(newNpcId);
        if (oldName.equals(tempName)) {
            pet.setName(getName());
        } else {
            pet.setName(oldName);
        }

        pet.setLevel(getLevel());
        pet.setHp(getMaxHp());
        pet.setMp(getMaxMp());
        pet.setExp(getExp());
        getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, 1200 * 1000);
        PetTable.getInstance().storeNewPet(this, getId(), newItemObjId);

        itemObjId = newItemObjId;
    }

    public void liberate() {
        L1MonsterInstance monster = new L1MonsterInstance(getTemplate());
        monster.setId(ObjectIdFactory.getInstance().nextId());

        monster.setX(getX());
        monster.setY(getY());
        monster.setMap(getMapId());
        monster.setHeading(getHeading());
        monster.setInventory(getInventory());
        setInventory(null);

        monster.setLevel(getLevel());
        monster.setMaxHp(getMaxHp());
        monster.setCurrentHp(getCurrentHp());
        monster.setMaxMp(getMaxMp());
        monster.setCurrentMp(getCurrentMp());

        petMaster.getPetList().remove(getId());
        deleteMe();
        petMaster.getInventory().removeItem(itemObjId, 1);
        PetTable.getInstance().deletePet(itemObjId);

        L1World.getInstance().storeObject(monster);
        L1World.getInstance().addVisibleObject(monster);

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(monster);

        for (L1PcInstance pc : list) {
            if (pc != null)
                onPerceive(pc);
        }
    }

    public void collect() {
        L1Inventory targetInventory = petMaster.getInventory();
        List<L1ItemInstance> items = inventory.getItems();
        int size = inventory.getSize();

        for (int i = 0; i < size; i++) {
            L1ItemInstance item = items.get(0);

            if (item.isEquipped()) {
                continue;
            }

            if (petMaster.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                inventory.tradeItem(item, item.getCount(), targetInventory);
                petMaster.sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
            } else {
                targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
                inventory.tradeItem(item, item.getCount(), targetInventory);
            }
        }
    }

    public void dropItem() {
        L1Inventory targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
        List<L1ItemInstance> items = inventory.getItems();

        int size = inventory.getSize();

        for (int i = 0; i < size; i++) {
            L1ItemInstance item = items.get(0);
            item.setEquipped(false);

            inventory.tradeItem(item, item.getCount(), targetInventory);
        }
    }

    public void call() {
        int id = type.getMessageId(L1PetType.getMessageNumber(getLevel()));
        int id2 = type.getDefyMessageId();

        if (getFood() < 5) {
            if (id != 0) {
                Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, "$" + id, 0));
            }
            setCurrentPetStatus(7);
        } else {
            if (id != 0) {
                Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, "$" + id2, 0));
            }

            setCurrentPetStatus(3);
        }
    }

    public void setTarget(L1Character target) {
        super.setTarget(target);

        if (target != null && (currentPetStatus == 1 || currentPetStatus == 2 || currentPetStatus == 5)) {
            setHate(target, 0);

            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    public void setMasterTarget(L1Character target) {
        if (target != null && (currentPetStatus == 1 || currentPetStatus == 5)) {
            setHate(target, 0);

            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_PetPack(this, perceivedFrom));

        if (isDead()) {
            perceivedFrom.sendPackets(new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Die));
        }
    }

    @Override
    public void onAction(L1PcInstance player) {
        L1Character cha = this.getMaster();
        L1PcInstance master = (L1PcInstance) cha;

        if (master.isTeleport()) {
            return;
        }
        if (L1CharPosUtils.isSafeZone(this)) {
            L1AttackRun attack_mortion = new L1AttackRun(player, this);
            attack_mortion.action();
            return;
        }

        if (player.checkNonPvP()) {
            return;
        }

        L1AttackRun attack = new L1AttackRun(player, this);
        attack.action();
        attack.commit();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        if (isDead()) {
            return;
        }
        if (petMaster.equals(player)) {
            player.sendPackets(new S_PetMenuPacket(this, getExpPercent()));

            L1Pet pet = PetTable.getInstance().getTemplate(itemObjId);

            if (pet != null) {
                pet.setExp(getExp());
                pet.setLevel(getLevel());
                pet.setHp(getMaxHp());
                pet.setMp(getMaxMp());

                PetTable.getInstance().storePet(pet);
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
        int status = actionType(action);

        if (status == 0) {
            return;
        }

        if (status == 6) {
            liberate();
        } else {
            Collection<L1NpcInstance> petList = petMaster.getPetList().values();

            for (L1NpcInstance petObject : petList) {
                if (petObject == null)
                    continue;

                if (petObject instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) petObject;

                    if (petMaster.getLevel() >= pet.getLevel() && pet.getFood() < 5) {
                        pet.setCurrentPetStatus(status);
                    } else {
                        L1PetType type = PetTypeTable.getInstance().get(pet.getTemplate().getNpcId());
                        int id = type.getDefyMessageId();
                        if (id != 0) {
                            Broadcaster.broadcastPacket(this, new S_NpcChatPacket(pet, "$" + id, 0));
                        }
                    }
                }
            }

            player.sendPackets(new S_PetMenuPacket(this, getExpPercent()));
        }
    }

    @Override
    public void onItemUse() {
        Arrays.sort(L1NpcConstants.HEAL_POTIONS);
        Arrays.sort(L1NpcConstants.HASTE_POTIONS);

        if (!isActivated()) {
            useItem(L1NpcConstants.USEITEM_HASTE, 100);
        }

        int per = getCurrentHp() * 100 / getMaxHp();

        if (per < 80) {
            if (getInventory().checkItem(L1NpcConstants.HEAL_POTIONS)) {
                L1ItemInstance item = getInventory().getItemOne(L1NpcConstants.HEAL_POTIONS);

                if ((item instanceof HealingPotion)) {
                    if (L1ItemDelay.hasItemDelay(this, item))
                        return;

                    useItem(L1NpcConstants.USEITEM_HEAL, 100);

                    L1ItemDelay.onItemUse(this, item);
                }
            }
        }
    }

    @Override
    public void onGetItem(L1ItemInstance item) {
        if (getTemplate().getDigestItem() > 0) {
            setDigestItem(item);
        }

        Arrays.sort(L1NpcConstants.HEAL_POTIONS);
        Arrays.sort(L1NpcConstants.HASTE_POTIONS);

        if (Arrays.binarySearch(L1NpcConstants.HEAL_POTIONS, item.getItem().getItemId()) >= 0) {
            if (getCurrentHp() != getMaxHp()) {
                useItem(L1NpcConstants.USEITEM_HEAL, 100);
            }
        } else if (Arrays.binarySearch(L1NpcConstants.HASTE_POTIONS, item.getItem().getItemId()) >= 0) {
            useItem(L1NpcConstants.USEITEM_HASTE, 100);
        }
    }

    private int actionType(String action) {
        int status = 0;
        if (action.equalsIgnoreCase("aggressive")) {
            status = 1;
        } else if (action.equalsIgnoreCase("defensive")) {
            status = 2;
        } else if (action.equalsIgnoreCase("stay")) {
            status = 3;
        } else if (action.equalsIgnoreCase("extend")) {
            status = 4;
        } else if (action.equalsIgnoreCase("alert")) {
            status = 5;
        } else if (action.equalsIgnoreCase("dismiss")) {
            status = 6;
        } else if (action.equalsIgnoreCase("getitem")) {
            status = 8;
            collection();
        }

        return status;
    }

    private void collection() {
        List<L1GroundInventory> gInventorys = new ArrayList<>();

        for (L1Object obj : L1World.getInstance().getVisibleObjects(this, 10)) {
            if (obj instanceof L1GroundInventory) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }

        for (L1GroundInventory inventory : gInventorys) {
            for (L1ItemInstance item : inventory.getItems()) {
                if (item == null)
                    continue;
                if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK && !item.getItem().isUseHighPet()) {
                    targetItem = item;
                    targetItemList.add(targetItem);
                }
            }
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);

        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }

        if (petMaster != null) {
            int hpRatio = 100 * getCurrentHp() / getMaxHp();
            petMaster.sendPackets(new S_HPMeter(getId(), hpRatio));
        }
    }

    @Override
    public void setCurrentMp(int i) {
        super.setCurrentMp(i);

        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
    }

    public void usePetWeapon(L1ItemInstance weapon) {
        if (getWeapon() == null) {
            setPetWeapon(weapon);
        } else { // 이미 무엇인가를 장비 하고 있는 경우, 전의 장비를 뗀다
            if (getWeapon().equals(weapon)) {
                removePetWeapon(getWeapon());
            } else {
                removePetWeapon(getWeapon());
                setPetWeapon(weapon);
            }
        }
    }

    public void usePetArmor(L1ItemInstance armor) {
        if (getArmor() == null) {
            setPetArmor(armor);
        } else { // 이미 무엇인가를 장비 하고 있는 경우, 전의 장비를 뗀다
            if (getArmor().equals(armor)) {
                removePetArmor(getArmor());
            } else {
                removePetArmor(getArmor());
                setPetArmor(armor);
            }
        }
    }

    private void setPetWeapon(L1ItemInstance weapon) {
        int itemId = weapon.getItem().getItemId();
        L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);

        if (petItem == null) {
            return;
        }

        setHitByWeapon(petItem.getHitModifier());
        setDamageByWeapon(petItem.getDamageModifier());
        getAbility().addAddedStr(petItem.getAddStr());
        getAbility().addAddedCon(petItem.getAddCon());
        getAbility().addAddedDex(petItem.getAddDex());
        getAbility().addAddedInt(petItem.getAddInt());
        getAbility().addAddedWis(petItem.getAddWis());
        addMaxHp(petItem.getAddHp());
        addMaxMp(petItem.getAddMp());
        getAbility().addSp(petItem.getAddSp());
        getResistance().addMr(petItem.getAddMr());

        setWeapon(weapon);
        weapon.setEquipped(true);
    }

    public void removePetWeapon(L1ItemInstance weapon) {
        int itemId = weapon.getItem().getItemId();
        L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);

        if (petItem == null) {
            return;
        }

        setHitByWeapon(0);
        setDamageByWeapon(0);
        getAbility().addAddedStr(-petItem.getAddStr());
        getAbility().addAddedCon(-petItem.getAddCon());
        getAbility().addAddedDex(-petItem.getAddDex());
        getAbility().addAddedInt(-petItem.getAddInt());
        getAbility().addAddedWis(-petItem.getAddWis());
        addMaxHp(-petItem.getAddHp());
        addMaxMp(-petItem.getAddMp());
        getAbility().addSp(-petItem.getAddSp());
        getResistance().addMr(-petItem.getAddMr());

        setWeapon(null);
        weapon.setEquipped(false);
    }

    public void setPetArmor(L1ItemInstance armor) {
        int itemId = armor.getItem().getItemId();
        L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);

        if (petItem == null) {
            return;
        }

        getAC().addAc(petItem.getAddAc());
        getAbility().addAddedStr(petItem.getAddStr());
        getAbility().addAddedCon(petItem.getAddCon());
        getAbility().addAddedDex(petItem.getAddDex());
        getAbility().addAddedInt(petItem.getAddInt());
        getAbility().addAddedWis(petItem.getAddWis());
        addMaxHp(petItem.getAddHp());
        addMaxMp(petItem.getAddMp());
        getAbility().addSp(petItem.getAddSp());
        getResistance().addMr(petItem.getAddMr());

        setArmor(armor);
        armor.setEquipped(true);
    }

    public void removePetArmor(L1ItemInstance armor) {
        int itemId = armor.getItem().getItemId();
        L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);

        if (petItem == null) {
            return;
        }

        getAC().addAc(-petItem.getAddAc());
        getAbility().addAddedStr(-petItem.getAddStr());
        getAbility().addAddedCon(-petItem.getAddCon());
        getAbility().addAddedDex(-petItem.getAddDex());
        getAbility().addAddedInt(-petItem.getAddInt());
        getAbility().addAddedWis(-petItem.getAddWis());
        addMaxHp(-petItem.getAddHp());
        addMaxMp(-petItem.getAddMp());
        getAbility().addSp(-petItem.getAddSp());
        getResistance().addMr(-petItem.getAddMr());

        setArmor(null);
        armor.setEquipped(false);
    }

    public int getCurrentPetStatus() {
        return currentPetStatus;
    }

    public void setCurrentPetStatus(int i) {
        currentPetStatus = i;

        if (currentPetStatus == 5) {
            setHomeX(getX());
            setHomeY(getY());
        }

        if (currentPetStatus == 7) {
            allTargetClear();
        }

        if (currentPetStatus == 3) {
            allTargetClear();
        } else {
            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    public int getItemObjId() {
        return itemObjId;
    }

    public int getExpPercent() {
        return expPercent;
    }

    public void setExpPercent(int expPercent) {
        this.expPercent = expPercent;
    }

    public L1ItemInstance getWeapon() {
        return weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        this.weapon = weapon;
    }

    public L1ItemInstance getArmor() {
        return armor;
    }

    public void setArmor(L1ItemInstance armor) {
        this.armor = armor;
    }

    public int getHitByWeapon() {
        return hitByWeapon;
    }

    public void setHitByWeapon(int i) {
        hitByWeapon = i;
    }

    public int getDamageByWeapon() {
        return damageByWeapon;
    }

    public void setDamageByWeapon(int i) {
        damageByWeapon = i;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int i) {
        food = i;
    }

    public int getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(int i) {
        foodTime = i;
    }

    public L1PetType getPetType() {
        return type;
    }

    @Override
    public double onAttack(L1Character target, double damage) {
        damage += (getLevel() / 8d);
        damage += getDamageByWeapon();

        return damage;
    }
}
