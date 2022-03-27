package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconAura;

public class L1SkillWindShot extends L1SkillAdapter {

    public L1SkillWindShot(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.addBowHitup(6 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
        request.getTargetCharacter().sendPackets(new S_SkillIconAura(148, request.getDuration()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
        cha.sendPackets(new S_SkillIconAura(148, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_SkillIconAura(148, duration));
    }
}
