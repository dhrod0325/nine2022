package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.STATUS_TRIPLE_ARROW;

public class L1SkillTripleArrow extends L1SkillAdapter {
    public L1SkillTripleArrow(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            L1ItemInstance weapon = skillUseCharacter.getWeapon();

            if (weapon != null) {
                int weaponType = weapon.getItem().getType1();

                if (weaponType != 20) {
                    setRunSkillState(STATUS_RETURN);
                    return;
                }

                pc.setTripleAction(true);

                targetCharacter.getSkillEffectTimerSet().setSkillEffect(STATUS_TRIPLE_ARROW, 200);

                for (int i = 0; i < 3; i++) {
                    targetCharacter.onAction(pc);
                }

                pc.setTripleAction(false);

                targetCharacter.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_TRIPLE_ARROW);

                skillUseCharacter.sendPackets(new S_SkillSound(skillUseCharacter.getId(), 4394));
                Broadcaster.broadcastPacket(skillUseCharacter, new S_SkillSound(skillUseCharacter.getId(), 4394));
            }
        }
    }
}
