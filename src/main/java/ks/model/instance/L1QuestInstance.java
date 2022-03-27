package ks.model.instance;

import ks.core.datatables.npc.NpcTable;
import ks.model.Broadcaster;
import ks.model.L1Npc;
import ks.model.L1Quest;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChangeHeading;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.scheduler.npc.NpcRestScheduler;

@SuppressWarnings("unused")
public class L1QuestInstance extends L1NpcInstance {
    private static final long REST_MILLISEC = 10000;

    public L1QuestInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onNpcAI() {
        int npcId = getTemplate().getNpcId();
        if (isAiRunning()) {
            return;
        }
        if (npcId == 71075 || npcId == 70957 || npcId == 81209) {
        } else {
            setActivated(false);
            startAI();
        }
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
        attack.commit();
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        int pcX = pc.getX();
        int pcY = pc.getY();
        int npcX = getX();
        int npcY = getY();

        int heading = 0;
        if (pcX == npcX && pcY < npcY) {
        } else if (pcX > npcX && pcY < npcY)
            heading = 1;
        else if (pcX > npcX && pcY == npcY)
            heading = 2;
        else if (pcX > npcX)
            heading = 3;
        else if (pcX == npcX && pcY > npcY)
            heading = 4;
        else if (pcX < npcX && pcY > npcY)
            heading = 5;
        else if (pcX < npcX && pcY == npcY)
            heading = 6;
        else if (pcX < npcX)
            heading = 7;

        setHeading(heading);
        Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));

        int npcId = getTemplate().getNpcId();

        switch (npcId) {
            case 71092:
            case 71093:
                if (pc.isKnight() && pc.getQuest().getStep(3) == 4) {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "searcherk1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "searcherk4"));
                }
                break;
            case 71094:
                if (pc.isDarkElf() && pc.getQuest().getStep(4) == 1) {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "endiaq1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "endiaq4"));
                }
                break;
            case 71062:
                if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == 2) {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "kamit1b"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
                }
                break;
            case 71075:
                if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 1) {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "llizard1b"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
                }
                break;
            case 70957:
            case 81209:
                if (pc.getQuest().getStep(L1Quest.QUEST_ROI) != 1) {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "roi1"));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), "roi2"));
                }
                break;
        }

        synchronized (this) {
            if (isRest()) {
                restTime = System.currentTimeMillis() + REST_MILLISEC;
            } else {
                setRest(true);
                restTime = System.currentTimeMillis() + REST_MILLISEC;
                NpcRestScheduler.getInstance().addNpc(this);
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance pc, String action) {
        if (action.equalsIgnoreCase("start")) {
            int npcId = getTemplate().getNpcId();

            if ((npcId == 71092 || npcId == 71093) && pc.isKnight() && pc.getQuest().getStep(3) == 4) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(71093);
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            } else if (npcId == 71094 && pc.isDarkElf() && pc.getQuest().getStep(4) == 1) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(71094);
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            } else if (npcId == 71062 && pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == 2) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(71062);
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            } else if (npcId == 71075 && pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 1) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(71075);
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            } else if (npcId == 70957 || npcId == 81209) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(70957);
                pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
            }
        }
    }
}
