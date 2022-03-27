package ks.model.attack.magic.impl.damage.pc;

import ks.app.config.prop.CodeConfig;
import ks.model.Broadcaster;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

public class L1MagicDamagePc extends L1MagicDamageDecorator {
    public L1MagicDamagePc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int damage = super.calcDamage(magicParam);

        L1PcInstance attacker = getAttacker();

        damage += attacker.getMagicDamage();

        if (attacker.getInventory().getCurrentItem().isArmor(420115)) {
            int per = attacker.getInventory().getCurrentItem().getArmor().getMagicPercent();

            if (RandomUtils.isWinning(100, per)) {
                attacker.sendPackets(new S_SkillSound(attacker.getId(), 15841));
                Broadcaster.broadcastPacket(attacker, new S_SkillSound(attacker.getId(), 15841));
            }
        }

        int powerSp = attacker.getAbility().getSp();
        int powerInt = attacker.getAbility().getTotalInt();

        double coefficient = 1.0 + (powerSp + 1) * CodeConfig.MAGIC_DMG_SP + (powerInt - 9) * CodeConfig.MAGIC_DMG_INT;

        damage = (int) (damage * coefficient);

        return (int) (damage * CodeConfig.MAGIC_DMG_VALANCE);
    }

    @Override
    public L1PcInstance getAttacker() {
        return (L1PcInstance) super.getAttacker();
    }
}
