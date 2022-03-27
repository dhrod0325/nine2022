package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.scheduler.SummonTimeScheduler;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class L1SummonInstance extends L1NpcInstance {
    private static final long SUMMON_TIME = 3600000L;
    public boolean tamed;
    public long sumTime;
    private int currentPetStatus;
    private boolean isReturnToNature = false;

    public L1SummonInstance(L1Npc template, L1PcInstance master) {
        super(template);
        setId(ObjectIdFactory.getInstance().nextId());

        sumTime = SUMMON_TIME + System.currentTimeMillis();
        SummonTimeScheduler.getInstance().addNpc(this);

        setMaster(master);
        setX(master.getX() + RandomUtils.nextInt(5) - 2);
        setY(master.getY() + RandomUtils.nextInt(5) - 2);
        setMap(master.getMapId());
        setHeading(5);
        setLightSize(template.getLightSize());

        currentPetStatus = 3;
        tamed = false;

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc != null)
                onPerceive(pc);
        }

        master.addPet(this);

        Collection<L1NpcInstance> petList = master.getPetList().values();
        master.sendPackets(new S_ReturnedStat(12, (petList.size() + 1) * 3, getId(), true));

        int HpRatio = 100 * getCurrentHp() / getMaxHp();

        master.sendPackets(new S_HPMeter(this.getId(), HpRatio));
    }

    public L1SummonInstance(L1NpcInstance target, L1PcInstance master, boolean isCreateZombie) {
        super(null);
        setId(ObjectIdFactory.getInstance().nextId());

        if (isCreateZombie) {
            int npcId = 45065;
            int level = master.getLevel();
            if (master.isWizard()) {
                if (level >= 24 && level <= 31) {
                    npcId = 81183;
                } else if (level >= 32 && level <= 39) {
                    npcId = 81184;
                } else if (level >= 40 && level <= 43) {
                    npcId = 81185;
                } else if (level >= 44 && level <= 47) {
                    npcId = 81186;
                } else if (level >= 48 && level <= 51) {
                    npcId = 81187;
                } else if (level >= 52) {
                    npcId = 81188;
                }
            } else if (master.isElf()) {
                if (level >= 48) {
                    npcId = 81183;
                }
            }

            L1Npc template = NpcTable.getInstance().getTemplate(npcId).clone();
            settingTemplate(template);
        } else {
            settingTemplate(target.getTemplate());
            setCurrentHp(target.getCurrentHp());
            setCurrentMp(target.getCurrentMp());
        }

        sumTime = SUMMON_TIME + System.currentTimeMillis();
        SummonTimeScheduler.getInstance().addNpc(this);

        setMaster(master);
        setX(target.getX());
        setY(target.getY());
        setMap(target.getMapId());
        setHeading(target.getHeading());
        setLightSize(target.getLightSize());
        setPetCost(6);
        setInventory(target.getInventory());
        target.setInventory(null);

        currentPetStatus = 3;
        tamed = true;

        for (L1NpcInstance each : master.getPetList().values()) {
            if (each != null) {
                each.targetRemove(target);
            }
        }

        target.deleteMe();

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc != null) {
                onPerceive(pc);
            }
        }

        master.addPet(this);
        Collection<L1NpcInstance> petList = master.getPetList().values();
        master.sendPackets(new S_ReturnedStat(12, (petList.size() + 1) * 3, getId(), true));
        int HpRatio = 100 * getCurrentHp() / getMaxHp();
        master.sendPackets(new S_HPMeter(this.getId(), HpRatio));
        inventory.clearItems();
    }

    @Override
    public boolean noTarget() {
        if (currentPetStatus == 3) {
            return true;
        } else if (currentPetStatus == 4) {
            if (master != null && master.getMapId() == getMapId() && getLocation().getTileLineDistance(master.getLocation()) < 5) {
                int dir = targetReverseDirection(master.getX() + RandomUtils.nextInt(-1, 1), master.getY() + RandomUtils.nextInt(-1, 1));

                dir = L1CharPosUtils.checkObject(getX(), getY(), getMapId(), dir);

                toMoveDirection(dir);

                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            } else {
                currentPetStatus = 3;
                return true;
            }
        } else if (currentPetStatus == 5) {
            if (Math.abs(getHomeX() - getX()) > 1 || Math.abs(getHomeY() - getY()) > 1) {
                int dir = moveDirection(getHomeX(), getHomeY());
                if (dir == -1) {
                    setHomeX(getX());
                    setHomeY(getY());
                } else {
                    toMoveDirection(dir);
                    setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                }
            }
        } else if (master != null && master.getMapId() == getMapId()) {
            if (getLocation().getTileLineDistance(master.getLocation()) > 2) {
                int dir = moveDirection(master.getX(), master.getY());
                if (dir == -1) {
                    currentPetStatus = 3;
                    return true;
                } else {
                    toMoveDirection(dir);
                    setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
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

                if (!isExsistMaster()) {
                    currentPetStatus = 1;
                    setSummonTarget(attacker);
                }
            }

            if (attacker instanceof L1PcInstance && damage > 0) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }

            int newHp = getCurrentHp() - damage;

            if (newHp <= 0) {
                Death();
            } else {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            Death();
        }
    }

    public synchronized void Death() {
        if (!isDead()) {
            setDead(true);
            setCurrentHp(0);
            setActionStatus(L1ActionCodes.ACTION_Die);

            remove();
        }
    }

    public synchronized void remove() {
        getMap().setPassable(getLocation(), true);
        L1Inventory targetInventory = master.getInventory();
        List<L1ItemInstance> items = inventory.getItems();

        for (L1ItemInstance item : items) {
            if (item == null)
                continue;

            if (master.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                inventory.tradeItem(item, item.getCount(), targetInventory);
                master.sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
            } else {
                targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
                inventory.tradeItem(item, item.getCount(), targetInventory);
            }
        }

        deleteMe();
    }

    public synchronized void returnToNature() {
        isReturnToNature = true;

        if (!tamed) {
            remove();
        } else {
            liberate();
        }
    }

    @Override
    public synchronized void deleteMe() {
        if (destroyed) {
            return;
        }

        if (!tamed && !isReturnToNature) {
            Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 169));
        }

        Collection<L1NpcInstance> petList = master.getPetList().values();

        int i = 0;

        for (L1NpcInstance pet : petList) {
            if (pet.equals(this)) {
                master.sendPackets(new S_ReturnedStat(12, i * 3, getId(), false));
            }

            i++;
        }

        master.getPetList().remove(getId());

        super.deleteMe();

        SummonTimeScheduler.getInstance().removeNpc(this);
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

        monster.setCurrentHp(getCurrentHp());
        monster.setCurrentMp(getCurrentMp());
        monster.setExp(0);

        deleteMe();

        L1World.getInstance().storeObject(monster);
        L1World.getInstance().addVisibleObject(monster);
    }

    public void setSummonTarget(L1Character target) {
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
    public void onAction(L1PcInstance attacker) {
        if (attacker == null) {
            return;
        }

        L1Character cha = this.getMaster();

        if (cha == null) {
            return;
        }

        L1PcInstance master = (L1PcInstance) cha;

        if (master.isTeleport()) {
            return;
        }

        if ((L1CharPosUtils.isSafeZone(this) || L1CharPosUtils.isSafeZone(attacker)) && isExsistMaster()) {
            L1AttackRun attack_mortion = new L1AttackRun(attacker, this);
            attack_mortion.action();
            return;
        }

        if (attacker.checkNonPvP()) {
            return;
        }

        L1AttackRun attack = new L1AttackRun(attacker, this);
        attack.action();
        attack.commit();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        if (isDead()) {
            return;
        }
        if (master.equals(player)) {
            player.sendPackets(new S_PetMenuPacket(this, 0));
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
        int status = ActionType(action);

        if (status == 0) {
            return;
        }
        if (status == 6) {
            if (tamed) {
                liberate();
            } else {
                Death();
            }
        } else {
            Collection<L1NpcInstance> petList = master.getPetList().values();

            for (L1NpcInstance petObject : petList) {
                if (petObject == null)
                    continue;

                if (petObject instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) petObject;
                    summon.setCurrentPetStatus(status);
                }
            }
        }
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_SummonPack(this, perceivedFrom));
    }

    @Override
    public void onItemUse() {
        if (!isActivated()) {
            useItem(L1NpcConstants.USEITEM_HASTE, 100);
        }
        if (getCurrentHp() * 100 / getMaxHp() < 40) {
            useItem(L1NpcConstants.USEITEM_HEAL, 100);
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
        } else if (Arrays
                .binarySearch(L1NpcConstants.HASTE_POTIONS, item.getItem().getItemId()) >= 0) {
            useItem(L1NpcConstants.USEITEM_HASTE, 100);
        }
    }

    private int ActionType(String action) {
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
        }
        return status;
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);

        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }

        if (master != null) {
            int HpRatio = 100 * getCurrentHp() / getMaxHp();
            L1PcInstance Master = master;
            Master.sendPackets(new S_HPMeter(getId(), HpRatio));
        }
    }

    @Override
    public void setCurrentMp(int i) {
        super.setCurrentMp(i);

        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
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

        if (currentPetStatus == 3) {
            allTargetClear();
        } else {
            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    public boolean isExsistMaster() {
        boolean isExsistMaster = true;

        if (this.getMaster() != null) {
            String masterName = this.getMaster().getName();
            if (L1World.getInstance().getPlayer(masterName) == null) {
                isExsistMaster = false;
            }
        }

        return isExsistMaster;
    }

    public void dropItem() {
        L1Inventory targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
        List<L1ItemInstance> items = inventory.getItems();

        int size = inventory.getSize();

        for (int i = 0; i < size; i++) {
            L1ItemInstance item = items.get(0);

            if (item == null)
                continue;

            item.setEquipped(false);
            inventory.tradeItem(item, item.getCount(), targetInventory);
        }
    }

}
