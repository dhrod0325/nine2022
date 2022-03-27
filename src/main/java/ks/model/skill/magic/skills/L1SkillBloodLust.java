package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillBrave;

public class L1SkillBloodLust extends L1SkillAdapter {
    public L1SkillBloodLust(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1Character target = request.getTargetCharacter();

        target.getMoveState().setBraveSpeed(1);
        target.sendPackets(new S_SkillBrave(target.getId(), 1, request.getDuration()));
        Broadcaster.broadcastPacket(target, new S_SkillBrave(target.getId(), 1, 0));
    }

    @Override
    public void stopSkill(L1Character target) {
        super.stopSkill(target);

        target.sendPackets(new S_SkillBrave(target.getId(), 0, 0));
        Broadcaster.broadcastPacket(target, new S_SkillBrave(target.getId(), 0, 0));
    }
}
