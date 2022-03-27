package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconAura;

public class L1SkillBraveMental extends L1SkillAdapter {
    public L1SkillBraveMental(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        request.getTargetCharacter().sendPackets(new S_SkillIconAura(116, request.getDuration()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        cha.sendPackets(new S_SkillIconAura(116, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_SkillIconAura(116, duration));
    }
}
