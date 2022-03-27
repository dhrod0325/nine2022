package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.constants.L1ItemId;
import ks.core.datatables.drop.DropTable;
import ks.model.instance.*;
import ks.model.instance.extend.event.L1DeathEvent;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static ks.constants.L1NpcConstants.CHAT_TIMING_DEAD;

public class L1MonsterDeath {
    private static final Logger logger = LogManager.getLogger(L1MonsterDeath.class);
    private final L1MonsterInstance monster;
    private L1Character attacker;

    public L1MonsterDeath(L1MonsterInstance monster) {
        this.monster = monster;
    }

    public L1MonsterDeath(L1MonsterInstance monster, L1Character attacker) {
        setAttacker(attacker);
        this.monster = monster;
    }

    public L1Character getAttacker() {
        return attacker;
    }

    public void setAttacker(L1Character a) {
        attacker = a;
    }

    public String getLastAttackerName() {
        if (attacker != null) {
            return attacker.getName();
        }

        return null;
    }

    public void run() {
        die(attacker);
    }

    public void die(L1Character attacker) {
        try {
            setAttacker(attacker);

            monster.getLight().turnOnOffLight();
            monster.setDeathProcessing(true);
            monster.setCurrentHp(0);
            monster.setDead(true);
            monster.setActionStatus(L1ActionCodes.ACTION_Die);

            List<L1Object> o = L1World.getInstance().getVisibleObjects(monster, 0);
            List<L1Object> checkList = new ArrayList<>();

            for (L1Object check : o) {
                if (check instanceof L1Character) {
                    L1Character character = (L1Character) check;

                    if (character.isDead()) {
                        continue;
                    }

                    if (character instanceof L1DollInstance) {
                        continue;
                    }

                    checkList.add(check);
                }
            }

            if (checkList.isEmpty()) {
                monster.getMap().setPassable(monster.getLocation(), true);
            }

            List<L1Object> visibleObjects = L1World.getInstance().getVisibleObjects(monster, -1);

            for (L1Object visibleObject : visibleObjects) {
                if (visibleObject instanceof L1DeathEvent) {
                    if (visibleObject.equals(monster)) {
                        continue;
                    }

                    L1DeathEvent event = (L1DeathEvent) visibleObject;
                    event.onAroundDeath(attacker, monster);
                }
            }

            distributeExpDropKarma(attacker);

            monster.startChat(CHAT_TIMING_DEAD);
            monster.setDeathProcessing(false);
            monster.setExp(0);
            monster.setKarma(0);
            monster.setLawful(0);
            monster.allTargetClear();
            Broadcaster.broadcastPacket(monster, new S_DoActionGFX(monster.getId(), L1ActionCodes.ACTION_Die));
        } catch (Exception e) {
            logger.error(e);
        }

        try {
            monster.startDeleteTimer();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void distributeExpDropKarma(L1Character lastAttacker) {
        try {
            if (lastAttacker == null) {
                return;
            }

            L1PcInstance pc = L1CommonUtils.getAttackerToPc(lastAttacker);

            if (pc != null) {
                pc.setMonsterKill(pc.getMonsterKill() + 1);
                L1CalcExp.calcExp(pc, monster);

                if (monster.isDead()) {
                    distributeDrop(pc);
                    giveKarma(pc);
                }
            } else if (lastAttacker instanceof L1EffectInstance) {
                List<L1Character> targetList = monster.getHateList().toTargetList();
                List<Integer> hateList = monster.getHateList().toHateList();

                if (hateList.size() != 0) {
                    int maxHate = 0;
                    for (int i = hateList.size() - 1; i >= 0; i--) {
                        if (maxHate < hateList.get(i)) {
                            maxHate = (hateList.get(i));
                            lastAttacker = targetList.get(i);
                        }
                    }

                    if (lastAttacker instanceof L1PcInstance) {
                        pc = (L1PcInstance) lastAttacker;
                    } else if (lastAttacker instanceof L1PetInstance) {
                        pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
                    } else if (lastAttacker instanceof L1SummonInstance) {
                        pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
                    }

                    L1CalcExp.calcExp(pc, monster);

                    if (monster.isDead()) {
                        distributeDrop(pc);
                        giveKarma(pc);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    //땅에떨어지는건 여기를 확인할것
    private void distributeDrop(L1PcInstance pc) {
        try {
            int npcId = monster.getTemplate().getNpcId();

            if (pc.getInventory().checkItem(L1ItemId.SERVER_GAHO)) {
                List<L1Drop> dropList = DropTable.getInstance().findDropList(npcId);

                for (L1Drop drop : dropList) {
                    if (drop.getItemId() == 60001314) {
                        monster.setDrop(pc);
                        break;
                    }
                }
            }

            if (npcId != 45640 || monster.getGfxId().getTempCharGfx() == 2332) {
                DropTable.getInstance().dropShare(pc, monster);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private void giveKarma(L1PcInstance pc) {
        int karma = monster.getKarma();

        if (karma != 0) {
            int karmaSign = Integer.signum(karma);
            int pcKarmaLevel = pc.getKarmaLevel();
            int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
            if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
                karma *= 5;
            }

            pc.addKarma(karma * CodeConfig.RATE_KARMA);
        }
    }
}
