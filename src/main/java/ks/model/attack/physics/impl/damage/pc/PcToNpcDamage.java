package ks.model.attack.physics.impl.damage.pc;

import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.L1AttackDamageDecorator;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1WeaponUtils;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.system.boss.L1BossSpawnManager;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;

public class PcToNpcDamage extends L1AttackDamageDecorator {
    public PcToNpcDamage(L1AttackDamage attackDamage) {
        super(attackDamage);
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        int damage = super.calcDamage(attackParam);

        if (getAttacker() instanceof L1PcInstance) {
            L1PcInstance attacker = (L1PcInstance) getAttacker();

            if (getTarget() instanceof L1NpcInstance) {
                L1NpcInstance target = (L1NpcInstance) getTarget();

                //은 타입 대미지
                damage += calcBlessDmg(attacker, target);
                L1LogUtils.damageLog("은타입 증가 대미지 : {}", damage);

                //속성대미지
                damage += weakAttrDamage(attacker, target);
                L1LogUtils.damageLog("속성 증가 대미지 : {}", damage);


                if (L1BossSpawnManager.getInstance().isSpawned(target)) {
                    if (!attacker.isHunt()) {
                        damage /= 4;
                        attacker.sendPackets("수배를 걸지않으면 대미지의 1/4만 적용됩니다");
                    }
                }

                damage -= target.getTemplate().getDamageReduction();
                L1LogUtils.damageLog("타겟 리덕션 감소 대미지 : {}", damage);
            }
        }


        return damage;
    }

    private int weakAttrDamage(L1PcInstance attacker, L1NpcInstance target) {
        int damage = 0;

        if (attacker.getWeapon() != null) {
            int attrLevel = attacker.getWeapon().getAttrEnchantLevel();
            int weakAttr = target.getTemplate().getWeakAttr();
            int attackerAttr = L1WeaponUtils.getWeaponAttr(attrLevel);

            if (attackerAttr == weakAttr) {
                damage += L1WeaponUtils.getWeaponAttrDamage(attrLevel);
            }
        }

        return damage;
    }

    private static int calcBlessDmg(L1PcInstance pc, L1NpcInstance target) {
        L1ItemInstance weapon = pc.getWeapon();
        int weaponMaterial = pc.getWeaponInfo().getWeaponMaterial();

        if (weapon == null)
            return 0;

        int weaponBless = weapon.getBless();
        int weaponType = pc.getWeaponInfo().getWeaponType();

        int damage = 0;
        int undead = target.getTemplate().getUndead();

        if ((weaponMaterial == 14 || weaponMaterial == 17 || weaponMaterial == 22) && (undead == 1 || undead == 3 || undead == 5)) {
            damage += RandomUtils.nextInt(20) + 1;
        }

        if ((weaponMaterial == 17 || weaponMaterial == 22) && undead == 2) {
            damage += RandomUtils.nextInt(3) + 1;
        }

        if (weaponBless == 0 && (undead == 1 || undead == 2 || undead == 3)) {
            damage += RandomUtils.nextInt(4) + 1;
        }

        if (pc.getWeapon() != null && weaponType != 20 && weaponType != 62 && weapon.getHolyDmgByMagic() != 0 && (undead == 1 || undead == 3)) {
            damage += weapon.getHolyDmgByMagic();
        }

        return damage;
    }
}
