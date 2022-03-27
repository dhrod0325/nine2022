package ks.model.instance;

import ks.constants.L1NpcConstants;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1Object;
import ks.model.L1Quest;
import ks.model.L1World;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_FollowerPack;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.util.L1CommonUtils;
import ks.util.L1InstanceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class L1FollowerInstance extends L1NpcInstance {
    private final Logger logger = LogManager.getLogger();

    public L1FollowerInstance(L1Npc template, L1QuestInstance target, L1PcInstance master) {
        super(template);

        this.master = master;
        setId(ObjectIdFactory.getInstance().nextId());
        setMaster(master);
        setX(target.getX());
        setY(target.getY());
        setMap(target.getMapId());
        setHeading(target.getHeading());
        setLightSize(target.getLightSize());
        target.setParalyzed(true);
        target.deleteMe();
        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc != null)
                onPerceive(pc);
        }

        startAI();
        master.addFollower(this);
    }

    @Override
    public boolean noTarget() {
        L1PcInstance pc;
        List<L1Object> list = L1World.getInstance().getVisibleObjects(this);

        for (L1Object object : list) {
            if (object == null)
                continue;
            if (object instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) object;
                switch (npc.getTemplate().getNpcId()) {
                    case 70740:
                    case 71093:
                        setParalyzed(true);
                        pc = master;
                        if (!pc.getInventory().checkItem(40593)) {
                            L1CommonUtils.createNewItem(pc, 40593, 1);
                        }
                        deleteMe();
                        return true;
                    case 70811:
                    case 71094:
                        setParalyzed(true);
                        pc = master;
                        if (!pc.getInventory().checkItem(40582)) {
                            L1CommonUtils.createNewItem(pc, 40582, 1);
                        }
                        deleteMe();
                        return true;
                    case 71061:
                    case 71062:
                        if (getLocation()
                                .getTileLineDistance(master.getLocation()) < 3) {
                            pc = master;
                            if ((pc.getX() >= 32448 && pc.getX() <= 32452) // 모퉁이
                                    // 모스 주변
                                    // 좌표
                                    && (pc.getY() >= 33048 && pc.getY() <= 33052)
                                    && (pc.getMapId() == 440)) {
                                setParalyzed(true);
                                if (!pc.getInventory().checkItem(40711)) {
                                    L1CommonUtils.createNewItem(pc, 40711, 1);
                                    pc.getQuest().setStep(L1Quest.QUEST_CADMUS, 3);
                                }
                                deleteMe();
                                return true;
                            }
                        }
                        break;
                    case 71074:
                    case 71075:
                        if (getLocation()
                                .getTileLineDistance(master.getLocation()) < 3) {
                            pc = master;
                            if ((pc.getX() >= 32731 && pc.getX() <= 32735) // 리자드만
                                    // 장로 주변
                                    // 좌표
                                    && (pc.getY() >= 32854 && pc.getY() <= 32858)
                                    && (pc.getMapId() == 480)) {
                                setParalyzed(true);
                                if (!pc.getInventory().checkItem(40633)) {
                                    L1CommonUtils.createNewItem(pc, 40633, 1);
                                    pc.getQuest().setStep(L1Quest.QUEST_LIZARD, 2);
                                }
                                deleteMe();
                                return true;
                            }
                        }
                        break;
                    case 70964:
                    case 70957:
                        if (getLocation().getTileLineDistance(master.getLocation()) < 3) {
                            pc = master;
                            if ((pc.getX() >= 32917 && pc.getX() <= 32921) && (pc.getY() >= 32974 && pc.getY() <= 32978) && (pc.getMapId() == 410)) {
                                setParalyzed(true);
                                L1CommonUtils.createNewItem(pc, 41003, 1);
                                pc.getQuest().setStep(L1Quest.QUEST_ROI, 0);
                                deleteMe();
                                return true;
                            }
                        }
                        break;
                }
            }
        }

        if (master.isDead()
                || getLocation().getTileLineDistance(master.getLocation()) > 10) {
            setParalyzed(true);
            spawn(getTemplate().getNpcId(), getX(), getY(), getMoveState()
                    .getHeading(), getMapId());
            deleteMe();
            return true;
        } else if (master != null && master.getMapId() == getMapId()) {
            if (getLocation().getTileLineDistance(master.getLocation()) > 2) {
                toMoveDirection(moveDirection(master.getX(), master.getY()));
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            }
        }
        return false;
    }

    @Override
    public synchronized void deleteMe() {
        master.getFollowerList().remove(getId());
        getMap().setPassable(getLocation(), true);
        super.deleteMe();
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
        attack.commit();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        if (isDead()) {
            return;
        }
        switch (getTemplate().getNpcId()) {
            case 71093:
                if (master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk4"));
                }
                break;
            case 71094:
                if (master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq4"));
                }
                break;
            case 71062:
                if (master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "kamit2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
                }
                break;
            case 71075:
                if (master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "llizard2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
                }
                break;
            case 70957:
                if (master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "roi2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "roi2"));
                }
                break;
        }

    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_FollowerPack(this, perceivedFrom));
    }

    public void spawn(int npcId, int X, int Y, int H, short Map) {
        L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);

        try {
            L1NpcInstance mob = L1InstanceFactory.createInstance(l1npc);

            mob.setId(ObjectIdFactory.getInstance().nextId());
            mob.setX(X);
            mob.setY(Y);
            mob.setHomeX(X);
            mob.setHomeY(Y);
            mob.setMap(Map);
            mob.setHeading(H);
            L1World.getInstance().storeObject(mob);
            L1World.getInstance().addVisibleObject(mob);
            L1Object object = L1World.getInstance().findObject(mob.getId());
            L1QuestInstance newnpc = (L1QuestInstance) object;
            newnpc.onNpcAI();
            newnpc.getLight().turnOnOffLight();
            newnpc.startChat(L1NpcConstants.CHAT_TIMING_APPEARANCE); // 채팅 개시
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

}
