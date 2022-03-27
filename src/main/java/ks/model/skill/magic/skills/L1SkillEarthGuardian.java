package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconShield;

public class L1SkillEarthGuardian extends L1SkillAdapter {

    public L1SkillEarthGuardian(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        request.getTargetCharacter().sendPackets(new S_SkillIconShield(S_SkillIconShield.EARTH_GUARDIAN, request.getDuration()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        cha.sendPackets(new S_SkillIconShield(S_SkillIconShield.EARTH_GUARDIAN, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_SkillIconShield(S_SkillIconShield.EARTH_GUARDIAN, duration));
    }
}
