package ks.model.attack.physics;

import ks.model.L1Character;
import ks.model.attack.physics.impl.L1Attack;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.physics.npc.L1AttackNpcToNpc;
import ks.model.attack.physics.npc.L1AttackNpcToPc;
import ks.model.attack.physics.pc.L1AttackPcToNpc;
import ks.model.attack.physics.pc.L1AttackPcToPc;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1ScarecrowInstance;
import ks.model.pc.L1PcInstance;
import ks.model.types.Point;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1AttackRun {
    private static final Logger logger = LogManager.getLogger();

    private L1Character attacker;
    private L1Character target;
    private final L1AttackParam attackParam = new L1AttackParam();
    private L1Attack attack;

    public L1AttackRun() {
    }

    public L1AttackRun(L1Character attacker, L1Character target) {
        build(attacker, target);
    }

    public void build(L1Character attacker, L1Character target) {
        try {
            this.attacker = attacker;
            this.target = target;

            if (attacker instanceof L1PcInstance) {
                if (target instanceof L1PcInstance) {
                    attack = new L1AttackPcToPc((L1PcInstance) attacker, (L1PcInstance) target);
                } else if (target instanceof L1NpcInstance) {
                    attack = new L1AttackPcToNpc((L1PcInstance) attacker, (L1NpcInstance) target);
                }
            } else if (attacker instanceof L1NpcInstance) {
                if (target instanceof L1PcInstance) {
                    attack = new L1AttackNpcToPc((L1NpcInstance) attacker, (L1PcInstance) target);
                } else if (target instanceof L1NpcInstance) {
                    attack = new L1AttackNpcToNpc((L1NpcInstance) attacker, (L1NpcInstance) target);
                }
            }

            if (attack == null)
                return;

            int hitRate = attack.getHitUp().calcHitUp(attackParam);

            boolean isHitUp = RandomUtils.isWinning(100, hitRate);

            if (isHitUp) {
                if (target instanceof L1PcInstance) {
                    isHitUp = missByErCheck(attacker, target);
                }

                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;

                    int criticalPer;

                    if (pc.isLongAttack()) {
                        criticalPer = pc.getBowCriticalPer();
                    } else {
                        criticalPer = pc.getCriticalPer();
                    }

                    if (RandomUtils.isWinning(100, criticalPer * 3)) {
                        attackParam.setCritical(true);
                    }
                }
            }

            attackParam.setHitUp(isHitUp);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private boolean missByErCheck(L1Character attacker, L1Character target) {
        boolean isHitUp = true;

        if (attacker instanceof L1NpcInstance) {
            L1NpcInstance attackerNpc = (L1NpcInstance) attacker;

            if (attackerNpc.getTemplate().getRanged() >= 6
                    && attackerNpc.getTemplate().getBowActId() != 0
                    && attacker.getLocation().getTileLineDistance(new Point(target.getX(), target.getY())) >= 3) {
                isHitUp = L1AttackUtils.isMissByEr(target);
            }
        } else if (attacker instanceof L1PcInstance) {
            if (attacker.isLongAttack()) {
                isHitUp = L1AttackUtils.isMissByEr(target);
            }
        }

        return isHitUp;
    }

    public void action() {
        attack.getAction().action(attackParam);

        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;

            if (target instanceof L1ScarecrowInstance) {
                L1ScarecrowInstance scarecrow = (L1ScarecrowInstance) target;
                int dmg = attack.getDamage().calcDamage(attackParam);
                scarecrow.attackDamageCheck(pc, dmg);
            }
        }
    }

    public void commit() {
        if (attackParam.isHitUp()) {
            int dmg = attack.getDamage().calcDamage(attackParam);
            attackParam.setDamage(dmg);
        }

        L1LogUtils.gmLog(attacker, "명중률 : {} , 대미지 : {}", attackParam.getHitRate(), attackParam.getDamage());

        attack.getCommit().commit(attackParam);
    }

    public L1Attack getAttack() {
        return attack;
    }

    public L1AttackParam getAttackParam() {
        return attackParam;
    }

    public void setAttacker(L1Character attacker) {
        this.attacker = attacker;
    }

    public void setTarget(L1Character target) {
        this.target = target;
    }
}
