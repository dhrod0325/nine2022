package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillBuffCoin extends L1SkillAdapter {

    public L1SkillBuffCoin(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getAC().addAc(-3 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
        //request.getTargetCharacter().sendPackets(new S_PacketBox(L1PacketBoxType.EFFECT_ICON, 11031, request.getDuration()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
        //cha.sendPackets(new S_PacketBox(L1PacketBoxType.EFFECT_ICON, 11031, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        //cha.sendPackets(new S_PacketBox(L1PacketBoxType.EFFECT_ICON, 11031, duration));
    }
}
