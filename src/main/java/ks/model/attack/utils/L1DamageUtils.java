package ks.model.attack.utils;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.WATER_LIFE;

public class L1DamageUtils {
    public static int targetReceiveDamage(L1Character target, int dmg) {
        if (target.getInventory().getCurrentItem().isPapooArmor()) {
            int per = target.getInventory().getCurrentItem().getArmor().getMagicPercent();

            if (RandomUtils.isWinning(100, per)) {
                int healHp = RandomUtils.nextInt(CodeConfig.DRAGON_PAPOO_MIN, CodeConfig.DRAGON_PAPOO_MAX);

                if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER)) {
                    healHp /= 2;
                }

                if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WATER_LIFE)) {
                    healHp *= 2;
                    target.getSkillEffectTimerSet().removeSkillEffect(WATER_LIFE);
                }

                target.healHp(healHp);

                target.sendPackets(new S_SkillSound(target.getId(), 2187));
                Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 2187));
            }
        }

        if (target.getInventory().getCurrentItem().isRindArmor()) {
            int per = target.getInventory().getCurrentItem().getArmor().getMagicPercent();

            if (RandomUtils.isWinning(100, per)) {
                if (dmg >= 25) {
                    target.setCurrentMp(target.getCurrentMp() + 20);
                    target.sendPackets(new S_SkillSound(target.getId(), 2188));
                    Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 2188));
                }
            }
        }

        if (target.getInventory().getCurrentItem().isAntaArmor()) {
            int per = target.getInventory().getCurrentItem().getArmor().getMagicPercent();

            if (RandomUtils.isWinning(100, per)) {
                dmg *= CodeConfig.DRAGON_ANTA_DMG_REDUCE;

                target.sendPackets(new S_SkillSound(target.getId(), 2183));
                Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 2183));
            }
        }

        if (target.getInventory().getCurrentItem().isRedEarRing()) {
            dmg -= L1ArmorUtils.damageReduceByRedEaring(target);
        }

        L1ItemInstance shield = target.getInventory().getCurrentItem().getShield();

        if (shield != null && shield.getItemId() == 500040) {
            if (RandomUtils.isWinning(100, shield.getEnchantLevel() * 2)) {
                dmg *= 0.5;
                target.sendPackets(new S_SkillSound(target.getId(), 12118));
            }
        }

        return dmg;
    }
}
