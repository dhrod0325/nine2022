package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.random.RandomUtils;

public class L1SkillWeaponBreak extends L1SkillAdapter {
    public L1SkillWeaponBreak(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1ItemInstance weapon = targetCharacter.getWeapon();

            if (weapon != null) {
                int weaponDamage = RandomUtils.nextInt(skillUseCharacter.getAbility().getTotalInt() / 3) + 1;
                targetCharacter.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                targetCharacter.getInventory().receiveDamage(weapon, weaponDamage);
            }
        } else if (targetCharacter instanceof L1NpcInstance) {
            ((L1NpcInstance) targetCharacter).setWeaponBreaking(true);
        }
    }
}
