package ks.model.instance;

import ks.core.datatables.NPCTalkDataTable;
import ks.model.Broadcaster;
import ks.model.L1Npc;
import ks.model.L1NpcTalkData;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChangeHeading;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.scheduler.npc.NpcRestScheduler;

@SuppressWarnings("unused")
public class L1PeopleInstance extends L1NpcInstance {
    private static final long REST_MILLISEC = 10000;

    public L1PeopleInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (getCurrentHp() > 0 && !isDead()) {
            L1AttackRun attack = new L1AttackRun(pc, this);
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActivated(false);
        startAI();
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        int npcid = getTemplate().getNpcId();
        String htmlid = null;
        String[] htmldata = null;

        int pcX = pc.getX();
        int pcY = pc.getY();
        int npcX = getX();
        int npcY = getY();

        if (getTemplate().getChangeHead()) {
            int heading = 0;

            if (pcX > npcX && pcY < npcY)
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

        if (talking != null) {
            switch (npcid) {
                case 70839: // 도에트
                    if (pc.isCrown() || pc.isKnight() || pc.isWizard()) {
                        htmlid = "doettM1";
                    } else if (pc.isDarkElf()) {
                        htmlid = "doettM2";
                    } else if (pc.isDragonKnight()) {
                        htmlid = "doettM3";
                    } else if (pc.isIllusionist()) {
                        htmlid = "doettM4";
                    }
                    break;
                case 70854: // 후린달렌
                    if (pc.isCrown() || pc.isKnight() || pc.isWizard()) {
                        htmlid = "hurinM1";
                    } else if (pc.isDarkElf()) {
                        htmlid = "hurinE3";
                    } else if (pc.isDragonKnight()) {
                        htmlid = "hurinE4";
                    } else if (pc.isIllusionist()) {
                        htmlid = "hurinE5";
                    }
                    break;
                case 70843: // 모리엔
                    if (pc.isCrown() || pc.isKnight() || pc.isWizard()) {
                        htmlid = "morienM1";
                    } else if (pc.isDarkElf()) {
                        htmlid = "morienM2";
                    } else if (pc.isDragonKnight()) {
                        htmlid = "morienM3";
                    } else if (pc.isIllusionist()) {
                        htmlid = "morienM4";
                    }
                    break;
                case 70849: // 테오도르
                    if (pc.isCrown() || pc.isKnight() || pc.isWizard()) {
                        htmlid = "theodorM1";
                    } else if (pc.isDarkElf()) {
                        htmlid = "theodorM2";
                    } else if (pc.isDragonKnight()) {
                        htmlid = "theodorM3";
                    } else if (pc.isIllusionist()) {
                        htmlid = "theodorM4";
                    }
                    break;
                default:
                    break;
            }

            if (htmlid != null) {
                pc.sendPackets(new S_NPCTalkReturn(getId(), htmlid));
            } else {
                if (pc.getLawful() < -1000) {
                    pc.sendPackets(new S_NPCTalkReturn(talking, getId(), 2));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(talking, getId(), 1));
                }
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
    }
}
