package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconShield;

public class L1SkillEarthSkin extends L1SkillAdapter {

    public L1SkillEarthSkin(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getAC().addAc(-6 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
        request.getTargetCharacter().sendPackets(new S_SkillIconShield(S_SkillIconShield.EARTH_SKIN, request.getDuration()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
        cha.sendPackets(new S_SkillIconShield(S_SkillIconShield.EARTH_SKIN, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_SkillIconShield(S_SkillIconShield.EARTH_SKIN, duration));
    }
}
