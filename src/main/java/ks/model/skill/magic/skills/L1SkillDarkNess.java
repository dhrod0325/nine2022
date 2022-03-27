package ks.model.skill.magic.skills;

import ks.constants.L1DataMapKey;
import ks.model.L1Character;
import ks.model.L1PinkName;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Ability;
import ks.packets.serverpackets.S_CurseBlind;

import static ks.constants.L1SkillId.STATUS_FLOATING_EYE;

public class L1SkillDarkNess extends L1SkillAdapter {
    public L1SkillDarkNess(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1PinkName.onAction(targetCharacter, skillUseCharacter);

        if (targetCharacter.getSkillEffectTimerSet().hasSkillEffect(STATUS_FLOATING_EYE)) {
            targetCharacter.sendPackets(new S_CurseBlind(2));
        } else {
            targetCharacter.sendPackets(new S_CurseBlind(1));
        }

        targetCharacter.sendPackets(new S_Ability(3, false));
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        targetCharacter.sendPackets(new S_CurseBlind(0));

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;

            if ("on".equalsIgnoreCase(pc.getDataMap().get(L1DataMapKey.MAP_HACK))) {
                targetCharacter.sendPackets(new S_Ability(3, true));
            }
        }

    }
}
