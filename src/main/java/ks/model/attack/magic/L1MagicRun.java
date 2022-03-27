package ks.model.attack.magic;

import ks.app.config.prop.CodeConfig;
import ks.model.L1Character;
import ks.model.attack.magic.impl.L1MagicAttack;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.attack.magic.impl.action.vo.L1MagicActionVo;
import ks.model.attack.magic.npc.L1NpcToNpcMagic;
import ks.model.attack.magic.npc.L1NpcToPcMagic;
import ks.model.attack.magic.pc.L1PcToNpcMagic;
import ks.model.attack.magic.pc.L1PcToPcMagic;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1ScarecrowInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.NumberUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1MagicRun {
    private static final Logger logger = LogManager.getLogger();

    private final L1Character target;

    private final L1Character attacker;

    private final L1MagicParam magicParam = new L1MagicParam();
    private int leverage = 1;

    private L1MagicAttack magicAttack;

    public L1MagicRun(L1Character attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;

        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;

            if (target instanceof L1PcInstance) {
                magicAttack = new L1PcToPcMagic(pc, (L1PcInstance) target);
            } else {
                magicAttack = new L1PcToNpcMagic(pc, (L1NpcInstance) target);
            }
        } else if (attacker instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) attacker;

            if (target instanceof L1PcInstance) {
                leverage *= 15;
                magicAttack = new L1NpcToPcMagic(npc, (L1PcInstance) target);
            } else {
                magicAttack = new L1NpcToNpcMagic(npc, (L1NpcInstance) target);
            }
        }

        if (magicAttack == null) {
            logger.error("오류 발생 : attacker : {} target : {}", attacker, target);
        }
    }

    public void setLeverage(int i) {
        leverage = i;
    }

    public boolean calcProbabilityMagic(int skillId) {
        magicParam.setSkillId(skillId);

        int prob = magicAttack.getProbability().calcProbability(skillId);

        prob = prob * leverage / 10;

        boolean result = RandomUtils.isWinning(100, prob);

        if (target instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) target;

            if (result) {
                if (NumberUtils.contains(skillId,
                        ENTANGLE,
                        SLOW,
                        WIND_SHACKLE
                )) {
                    pc.getAutoPotion().stop();
                }
            }
        }

        magicParam.setProbability(prob);
        magicParam.setSuccess(result);

        return result;
    }

    public int calcMagicDamage(int skillId) {
        magicParam.setSkillId(skillId);

        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;

            boolean isCritical = RandomUtils.isWinning(100, pc.getMagicCriticalPer() * 2);

            magicParam.setCritical(isCritical);
        }

        int damage = magicAttack.getDamage().calcDamage(magicParam);

        if (skillId == DISINTEGRATE) {
            if (target.getSkillEffectTimerSet().hasSkillEffect(STATUS_DISINTEGRATE)) {
                damage *= 0.3;
            } else {
                target.getSkillEffectTimerSet().setSkillEffect(STATUS_DISINTEGRATE, 2000);
            }
        }

        if (magicParam.isCritical()) {
            damage *= 1.5;
        }

        if (damage > CodeConfig.MAGIC_MAX_DMG) {
            damage = CodeConfig.MAGIC_MAX_DMG;
        }

        if (damage < 0) {
            damage = 0;
        }

        if (target.getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
            damage /= CodeConfig.MAGIC_IMMUNE_TO_HARM_REDUCE;
        }

        magicParam.setDamage(damage);

        return damage;
    }

    public void commit(L1MagicActionVo vo) {
        magicAttack.getAction().commit(vo);

        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;

            if (target instanceof L1ScarecrowInstance) {
                L1ScarecrowInstance scarecrow = (L1ScarecrowInstance) target;
                int dmg = magicAttack.getDamage().calcDamage(magicParam);
                scarecrow.magicDamageCheck(pc, magicParam.getSkillId(), dmg);
            }
        }
    }

    public L1MagicAttack getMagicAttack() {
        return magicAttack;
    }

    public L1MagicParam getMagicParam() {
        return magicParam;
    }
}
