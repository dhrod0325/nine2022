package ks.model.attack.physics.impl.damage.common;

import ks.app.config.prop.CodeConfig;
import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.L1AttackDamageDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.util.log.L1LogUtils;

import static ks.constants.L1SkillId.IMMUNE_TO_HARM;

public class ImmuneReduceDamage extends L1AttackDamageDecorator {
    public ImmuneReduceDamage(L1AttackDamage attackDamage) {
        super(attackDamage);
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        int dmg = super.calcDamage(attackParam);

        if (dmg == 0) {
            return 0;
        }

        if (getTarget().getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
            if (getTarget() instanceof L1PcInstance) {
                dmg /= CodeConfig.MAGIC_IMMUNE_TO_HARM_REDUCE;
            } else if (getTarget() instanceof L1NpcInstance) {
                dmg /= CodeConfig.MAGIC_IMMUNE_TO_HARM_REDUCE_NPC;
            }

            L1LogUtils.damageLog("이뮨으로 감소한 대미지 : {}", dmg);
        }

        return Math.max(dmg, 0);
    }
}
