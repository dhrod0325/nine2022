package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillHaste;

import static ks.constants.L1SkillId.*;

public class L1SkillHaste extends L1SkillAdapter {
    public L1SkillHaste(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();
        int duration = request.getDuration();

        if (cha.getMoveState().getMoveSpeed() != 2) {
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;

                if (pc.getHasteItemEquipped() > 0) {
                    setRunSkillState(STATUS_CONTINUE);
                    return;
                }

                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, duration));
            }

            Broadcaster.broadcastPacket(cha, new S_SkillHaste(cha.getId(), 1, 0));

            cha.getMoveState().setMoveSpeed(1);
        } else {
            int skillNum = 0;

            if (cha.getSkillEffectTimerSet().hasSkillEffect(SLOW)) {
                skillNum = SLOW;
            } else if (cha.getSkillEffectTimerSet().hasSkillEffect(GRATE_SLOW)) {
                skillNum = GRATE_SLOW;
            } else if (cha.getSkillEffectTimerSet().hasSkillEffect(ENTANGLE)) {
                skillNum = ENTANGLE;
            } else if (cha.getSkillEffectTimerSet().hasSkillEffect(MOB_SLOW_1)) {
                skillNum = MOB_SLOW_1;
            } else if (cha.getSkillEffectTimerSet().hasSkillEffect(MOB_SLOW_18)) {
                skillNum = MOB_SLOW_18;
            }

            if (skillNum != 0) {
                cha.getSkillEffectTimerSet().removeSkillEffect(skillNum);
                cha.getSkillEffectTimerSet().removeSkillEffect(HASTE);
                cha.getMoveState().setMoveSpeed(0);

                setRunSkillState(STATUS_CONTINUE);
            }
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        super.stopSkill(targetCharacter);

        targetCharacter.getMoveState().setMoveSpeed(0);

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
        }
    }

    @Override
    public void sendIcon(L1Character targetCharacter, int duration) {
        super.sendIcon(targetCharacter, duration);

        targetCharacter.sendPackets(new S_SkillHaste(targetCharacter.getId(), 1, duration));
        Broadcaster.broadcastPacket(targetCharacter, new S_SkillHaste(targetCharacter.getId(), 1, 0));
    }
}
