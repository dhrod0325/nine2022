package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1PinkName;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillHaste;

import static ks.constants.L1SkillId.*;

public class L1SkillSlow extends L1SkillAdapter {
    public L1SkillSlow(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();
        int duration = request.getDuration();

        if (skillUseCharacter instanceof L1PcInstance) {  //보라 되게
            if (targetCharacter instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) targetCharacter;
                L1PinkName.onAction(pc, skillUseCharacter);
            }
        }

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            if (pc.getHasteItemEquipped() > 0) {
                setRunSkillState(STATUS_CONTINUE);
                return;
            }
        }

        if (targetCharacter.getMoveState().getMoveSpeed() == 0) {
            if (targetCharacter instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) targetCharacter;
                pc.sendPackets(new S_SkillHaste(pc.getId(), 2, duration));
            }

            Broadcaster.broadcastPacket(targetCharacter, new S_SkillHaste(targetCharacter.getId(), 2, duration));
            targetCharacter.getMoveState().setMoveSpeed(2);
        } else if (targetCharacter.getMoveState().getMoveSpeed() == 1) {
            int skillNum = 0;

            if (targetCharacter.getSkillEffectTimerSet().hasSkillEffect(HASTE)) {
                skillNum = HASTE;
            } else if (targetCharacter.getSkillEffectTimerSet().hasSkillEffect(GREATER_HASTE)) {
                skillNum = GREATER_HASTE;
            } else if (targetCharacter.getSkillEffectTimerSet().hasSkillEffect(STATUS_HASTE)) {
                skillNum = STATUS_HASTE;
            }

            if (skillNum != 0) {
                targetCharacter.getSkillEffectTimerSet().removeSkillEffect(skillNum);
                targetCharacter.getSkillEffectTimerSet().removeSkillEffect(skillId);
                targetCharacter.getMoveState().setMoveSpeed(0);

                setRunSkillState(STATUS_CONTINUE);
            }
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        L1MagicUtils.stopHaste(targetCharacter);
    }

    @Override
    public void sendIcon(L1Character targetCharacter, int duration) {
        targetCharacter.sendPackets(new S_SkillHaste(targetCharacter.getId(), 2, duration));
        Broadcaster.broadcastPacket(targetCharacter, new S_SkillHaste(targetCharacter.getId(), 2, 0));
    }
}
