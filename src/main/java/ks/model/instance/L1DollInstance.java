package ks.model.instance;

import ks.app.LineageAppContext;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillIcon;
import ks.constants.L1SkillId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.dollBonus.DollBonus;
import ks.core.datatables.dollBonus.DollBonusTable;
import ks.model.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.scheduler.DollDeleteScheduler;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class L1DollInstance extends L1NpcInstance {
    private static final Logger logger = LogManager.getLogger(L1DollInstance.class);
    private final DollActionThread dollAction = new DollActionThread();
    public long dollTime;
    private MagicDollItemInstance item;

    public L1DollInstance(L1Npc template, L1PcInstance master, MagicDollItemInstance item, int dollTime) {
        super(template);

        setId(ObjectIdFactory.getInstance().nextId());
        setItem(item);

        this.dollTime = System.currentTimeMillis() + dollTime;

        DollDeleteScheduler.getInstance().addDollDelete(this);

        setMaster(master);
        setX(master.getX());
        setY(master.getY());
        setMap(master.getMapId());
        setHeading(5);
        setLightSize(template.getLightSize());

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);

        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            onPerceive(pc);
        }

        master.addDoll(this);

        if (!isAiRunning()) {
            startAI();
        }

        startDollBuff();
    }

    @Override
    public boolean noTarget() {
        if (master == null)
            return true;

        if (master.isDead()
                || master.isInvisible()
                || master.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)
                || master.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
            if (master != null) {
                master.sendPackets(new S_SkillIconGFX(L1SkillIcon.인형, 0));
                master.sendPackets(new S_OwnCharStatus(master));
                deleteDoll();
            }

            return true;
        } else if (master != null && master.getMapId() == getMapId()) {
            L1Location location = getLocation();
            L1Location masterLocation = master.getLocation();

            if (location.getTileLineDistance(masterLocation) > 2) {
                int dir = moveDirection(master.getX(), master.getY());

                if (dir == -1) {
                    teleport(master.getX(), master.getY(), getHeading(), false);
                    toMoveDirection(dir);
                    setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                } else {
                    toMoveDirection(dir);
                    setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
                    setHeading(dir);
                }
            }

            if (location.getTileLineDistance(masterLocation) > 8) {
                teleport(master.getX(), master.getY(), getHeading(), false);
            }
        } else {
            deleteDoll();
            return true;
        }

        return false;
    }

    @Override
    protected int calcSleepTime(int sleepTime, int type) {
        if (master == null)
            return 0;

        switch (master.getMoveState().getMoveSpeed()) {
            case 0:
                break;
            case 1:
                sleepTime -= (sleepTime * 0.25);
                break;
            case 2:
                sleepTime *= 2;
                break;
        }

        if (master.getMoveState().getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }

        return sleepTime;
    }

    public void startDollBuff() {
        L1PcInstance pc = master;

        pc.setCurrentDollId(item.getId());
        pc.setUsingDoll(true);

        if (pc.getCurrentDollItem() != null) {
            if (getAbMpr() > 0) {
                master.startMpRegenerationByDoll();
            }

            if (getAbHpr() > 0) {
                master.startHpRegenerationByDoll();
            }

            buff(pc, 1);

            item.setAppear(true);
            pc.sendPackets(new S_ItemName(item));
        }

        dollAction.start();
    }

    public int getAbHpr() {
        DollBonus e = DollBonusTable.getInstance().getEnchantBonus(item);

        if (e != null) {
            return e.getAbHpr();
        }

        return 0;
    }

    public int getAbHprTime() {
        DollBonus e = DollBonusTable.getInstance().getEnchantBonus(item);

        if (e != null) {
            return e.getAbHprTime();
        }

        return 0;
    }

    public int getAbMpr() {
        DollBonus e = DollBonusTable.getInstance().getEnchantBonus(item);

        if (e != null) {
            return e.getAbMpr();
        }

        return 0;
    }

    public int getAbMprTime() {
        DollBonus e = DollBonusTable.getInstance().getEnchantBonus(item);

        if (e != null) {
            return e.getAbMprTime();
        }

        return 0;
    }

    public void buff(L1PcInstance pc, int type) {
        DollBonus e = DollBonusTable.getInstance().getEnchantBonus(item);

        if (e != null) {
            if (e.getReduction() > 0) {
                pc.getAbility().addAddedReduction(e.getReduction() * type);
            }

            if (e.getWeight() > 0) {
                pc.addWeightReduction(e.getWeight() * type);
            }

            if (e.getAc() < 0) {
                pc.getAC().addAc(e.getAc() * type);
            }

            if (e.getAddHp() > 0) {
                pc.addMaxHp(e.getAddHp() * type);
            }
            
            if (e.getAddMp() > 0) {
                pc.addMaxHp(e.getAddMp() * type);
            }

            if (e.getAddBowDmg() > 0) {
                pc.addBowDmgupByArmor(e.getAddBowDmg() * type);
            }

            if (e.getAddBowHitUp() > 0) {
                pc.addBowHitupByArmor(e.getAddBowHitUp() * type);
            }

            if (e.getAddDmg() > 0) {
                pc.addDmgUpByArmor(e.getAddDmg() * type);
            }

            if (e.getAddHitUp() > 0) {
                pc.addHitUpByArmor(e.getAddHitUp() * type);
            }

            if (e.getAddExp() > 0) {
                pc.addExpBonus(e.getAddExp() * type);
            }

            if (e.getAddSp() > 0) {
                pc.getAbility().addSp(e.getAddSp() * type);
            }

            if (e.getRegistStun() > 0) {
                pc.getResistance().addStun(e.getRegistStun() * type);
            }

            if (e.getStunHitUp() > 0) {
                pc.addAddStunHit(e.getStunHitUp() * type);
            }

            if (e.getPvpDmg() > 0) {
                pc.addAddPvpDmgUp(e.getPvpDmg() * type);
            }

            if (e.getPvpReduction() > 0) {
                pc.addAddPvpReudction(e.getPvpReduction() * type);
            }

            if (e.getMagicHit() > 0) {
                pc.addAddMagicHitUp(e.getMagicHit() * type);
            }
        }
    }

    public void stopDollBuff() {
        L1PcInstance pc = master;

        buff(pc, -1);

        if (pc.getCurrentDollItem() != null) {
            if (getAbMpr() > 0) {
                master.stopMpRegenerationByDoll();
            }

            if (getAbHpr() > 0) {
                master.stopHpRegenerationByDoll();
            }

            item.setAppear(false);
            pc.sendPackets(new S_ItemName(item));
        }

        pc.setCurrentDollId(0);
        pc.setUsingDoll(false);
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_SPMR(pc));

        dollAction.stop();
    }

    public void deleteDoll() {
        deleteDoll(true);
    }

    public void deleteDoll(boolean withEffect) {
        try {
            stopDollBuff();

            if (master != null) {
                if (!master.getDollList().isEmpty()) {
                    master.getDollList().remove(getId());
                }

                if (withEffect) {
                    master.sendPackets(new S_SkillSound(getId(), 5936));
                    master.sendPackets(new S_SkillSound(getId(), 3940));
                    master.sendPackets(new S_SkillIconGFX(L1SkillIcon.인형, 0));

                    if (master.getCurrentDollItem() != null) {
                        master.sendPackets(new S_ItemName(master.getCurrentDollItem()));
                    }
                }
            }

            DollDeleteScheduler.getInstance().removeDollDelete(this);

            deleteMe();

            setMaster(null);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_DollPack(this));
    }

    @Override
    public void onItemUse() {
        if (!isActivated()) {
            useItem(L1NpcConstants.USEITEM_HASTE, 100);
        }
    }

    @Override
    public void onGetItem(L1ItemInstance item) {
        if (getTemplate().getDigestItem() > 0) {
            setDigestItem(item);
        }
    }

    public MagicDollItemInstance getItem() {
        return item;
    }

    public void setItem(MagicDollItemInstance item) {
        this.item = item;
    }

    public double getAttackDamage(L1PcInstance pc, L1Character targetCharacter) {
        int dmg = 0;

        if (pc.isUsingDoll()) {
            DollBonus e = DollBonusTable.getInstance().getEnchantBonus(pc.getCurrentDollItem());

            if (e != null) {
                if (e.getPerDmgPer() > 0) {
                    if (RandomUtils.isWinning(100, e.getPerDmgPer())) {
                        dmg += e.getPerDmg();

                        if (e.getPerDmgEffect() > 0) {
                            if (e.getPerDmgTarget() == 0) {
                                pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), e.getPerDmgEffect()));
                                Broadcaster.broadcastPacket(pc, new S_EffectLocation(pc.getX(), pc.getY(), e.getPerDmgEffect()));
                            } else if (e.getPerDmgTarget() == 1) {
                                pc.sendPackets(new S_EffectLocation(targetCharacter.getX(), targetCharacter.getY(), e.getPerDmgEffect()));
                                Broadcaster.broadcastPacket(pc, new S_EffectLocation(targetCharacter.getX(), targetCharacter.getY(), e.getPerDmgEffect()));
                            } else if (e.getPerDmgTarget() == 2) {
                                pc.sendPackets(new S_EffectLocation(getX(), getY(), e.getPerDmgEffect()));
                                Broadcaster.broadcastPacket(pc, new S_EffectLocation(getX(), getY(), e.getPerDmgEffect()));
                            }
                        }
                    }
                }
            }
        }

        return dmg;
    }

    public int getItemObjId() {
        if (item != null)
            return item.getId();

        return 0;
    }

    private class DollActionThread implements Runnable {
        private ScheduledFuture<?> dollActionFuture;

        @Override
        public void run() {
            L1Character cha = getMaster();

            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;

                if (!L1CharPosUtils.isSafeZone(pc))
                    return;

                if (RandomUtils.isWinning(100, 50))
                    return;

                int dis = getLocation().getTileDistance(pc.getLocation());

                if (dis > 4)
                    return;

                int rndAction = RandomUtils.nextInt(100);

                int actioncode = 67;

                if (rndAction <= 30) {
                    actioncode = 66;
                }

                pc.sendPackets(new S_DoActionGFX(getId(), actioncode));
                Broadcaster.broadcastPacket(pc, new S_DoActionGFX(getId(), actioncode));
            }
        }

        public void start() {
            long repeatTime = 1000 * 14;

            dollActionFuture = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(this, Instant.now().plusMillis(repeatTime), Duration.ofMillis(repeatTime));
        }

        public void stop() {
            if (dollActionFuture != null) {
                dollActionFuture.cancel(true);
                dollActionFuture = null;
            }
        }
    }
}
