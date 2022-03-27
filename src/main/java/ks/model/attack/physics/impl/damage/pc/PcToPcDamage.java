package ks.model.attack.physics.impl.damage.pc;

import ks.app.config.prop.CodeConfig;
import ks.model.L1World;
import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.L1AttackDamageDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.pc.L1PcInstance;
import ks.util.log.L1LogUtils;

import static ks.constants.L1SkillId.TRUE_TARGET;

public class PcToPcDamage extends L1AttackDamageDecorator {
    public PcToPcDamage(L1AttackDamage attackDamage) {
        super(attackDamage);
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        int dmg = super.calcDamage(attackParam);

        if (getAttacker() != null) {
            L1PcInstance attacker = getAttacker();

            if (getTarget() != null) {
                L1PcInstance target = getTarget();

                int reduction = target.getTotalReduction();

                if (attacker.getTotalIgnoreReduction() > 0) {
                    reduction -= attacker.getTotalIgnoreReduction();

                    L1LogUtils.damageLog("리덕션 무시로 감소한 리덕션 대미지 : {}", attacker.getTotalIgnoreReduction());

                    if (reduction < 0) {
                        reduction = 0;
                    }
                }

                dmg -= reduction;

                if (target.getSkillEffectTimerSet().hasSkillEffect(TRUE_TARGET)) {
                    L1PcInstance trueTargetPlayer = L1World.getInstance().getPlayer(target.getTrueTargetLeaderId());

                    if (trueTargetPlayer != null) {
                        if (trueTargetPlayer.getClanId() != 0 && trueTargetPlayer.getClanId() == getAttacker().getClanId()) {
                            double per = 1.01;
                            per += trueTargetPlayer.getLevel() * 1.0 / 15 / 100;
                            dmg *= per;
                        }
                    }
                }
            }
        }

        L1LogUtils.damageLog("대미지 : {}", dmg);

        return (int) (dmg * CodeConfig.BALANCE_DMG);
    }

    @Override
    public L1PcInstance getTarget() {
        return (L1PcInstance) super.getTarget();
    }

    @Override
    public L1PcInstance getAttacker() {
        return (L1PcInstance) super.getAttacker();
    }
}
