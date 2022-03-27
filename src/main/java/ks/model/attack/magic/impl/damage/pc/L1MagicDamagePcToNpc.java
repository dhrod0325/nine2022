package ks.model.attack.magic.impl.damage.pc;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.attack.magic.impl.L1MagicDamage;
import ks.model.attack.magic.impl.L1MagicDamageDecorator;
import ks.model.attack.magic.impl.L1MagicParam;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.system.boss.table.L1BossSpawnListHotTable;

import static ks.constants.L1SkillId.*;

public class L1MagicDamagePcToNpc extends L1MagicDamageDecorator {
    public L1MagicDamagePcToNpc(L1MagicDamage magicDamage) {
        super(magicDamage);
    }

    @Override
    public int calcDamage(L1MagicParam magicParam) {
        int damage = super.calcDamage(magicParam);

        L1PcInstance attacker = (L1PcInstance) getAttacker();
        L1NpcInstance target = (L1NpcInstance) getTarget();

        int skillId = magicParam.getSkillId();

        if (target.getNpcId() == 45640) {
            damage /= 2;
        }

        int weakAttr = target.getTemplate().getWeakAttr();
        L1Skills skills = SkillsTable.getInstance().getTemplate(skillId);

        if (skills.getAttr() == weakAttr) {
            damage *= 1.2;
        }

        damage -= damage * L1MagicUtils.reduceDamageByMr(target.getResistance().getEffectedMrBySkill());

        boolean isBoss = L1BossSpawnListHotTable.getInstance().isBoss(target.getNpcId());

        if (isBoss) {
            if (!attacker.isHunt()) {
                damage /= 4;
                attacker.sendPackets("수배를 걸지않으면 대미지의 1/4만 적용됩니다");
            }
        }

        if (skillId == CALL_LIGHTNING || skillId == ERUPTION || skillId == SUNBURST) {
            if (attacker.isElf()) {
                damage /= 2;
            }
        }

        if (damage > target.getCurrentHp()) {
            damage = target.getCurrentHp() + 1;
        }

        if (attacker.isGm()) {
            attacker.sendPackets("매직대미지 : " + damage);
        }

        return damage;
    }
}
