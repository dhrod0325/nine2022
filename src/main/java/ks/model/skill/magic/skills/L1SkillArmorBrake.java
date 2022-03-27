package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1PinkName;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconAura;

public class L1SkillArmorBrake extends L1SkillAdapter {
    public L1SkillArmorBrake(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();
        int duration = request.getDuration();

        targetCharacter.sendPackets(new S_SkillIconAura(119, duration));

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PinkName.onAction(targetCharacter, skillUseCharacter);
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        targetCharacter.sendPackets(new S_SkillIconAura(119, 0));
    }
}
