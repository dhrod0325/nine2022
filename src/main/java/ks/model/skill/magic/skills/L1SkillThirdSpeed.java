package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillBrave;

public class L1SkillThirdSpeed extends L1SkillAdapter {
    public L1SkillThirdSpeed(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        logger.debug("runSkill");

        L1Character cha = request.getTargetCharacter();
        cha.getMoveState().setBraveSpeed(4);
    }

    @Override
    public void stopSkill(L1Character cha) {
        cha.getMoveState().setBraveSpeed(0);
        cha.sendPackets(new S_SkillBrave(cha.getId(), 0, 0));
        Broadcaster.broadcastPacket(cha, new S_SkillBrave(cha.getId(), 0, 0));
    }

    @Override
    public void sendGrfx(L1SkillRequest request, boolean isSkillAction) {
        logger.debug("sendGrfx");

        if (!isSkillAction) {
            return;
        }

        L1Character cha = request.getTargetCharacter();
        int duration = request.getDuration();

        cha.sendPackets(new S_SkillBrave(cha.getId(), 4, duration));
        Broadcaster.broadcastPacket(cha, new S_SkillBrave(cha.getId(), 4, duration));
    }
}
