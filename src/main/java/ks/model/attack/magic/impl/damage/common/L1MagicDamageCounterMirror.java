package ks.model.attack.magic.impl.damage.common;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.instance.extend.ReceiveDamageAble;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.COUNTER_MIRROR;

public class L1MagicDamageCounterMirror extends L1MagicDamageDecorator {
    public L1MagicDamageCounterMirror(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        L1Character attacker = getAttacker();
        L1Character target = getTarget();

        int damage = super.calcDamage(magicParam);

        if (target.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MIRROR)) {
            if (target.getAbility().getTotalWis() >= RandomUtils.nextInt(100)) {
                attacker.sendPackets(new S_DoActionGFX(attacker.getId(), L1ActionCodes.ACTION_Damage));
                Broadcaster.broadcastPacket(attacker, new S_DoActionGFX(attacker.getId(), L1ActionCodes.ACTION_Damage));
                target.sendPackets(new S_SkillSound(target.getId(), 4395));
                Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 4395));

                if (attacker instanceof ReceiveDamageAble) {
                    ((ReceiveDamageAble) attacker).receiveDamage(target, damage / 2);
                }

                target.getSkillEffectTimerSet().killSkillEffectTimer(COUNTER_MIRROR);

                return 0;
            }
        }

        return damage;
    }
}
