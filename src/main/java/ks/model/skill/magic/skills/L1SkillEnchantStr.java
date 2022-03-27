package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_StrUp;

public class L1SkillEnchantStr extends L1SkillAdapter {

    public L1SkillEnchantStr(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getAbility().addAddedStr(5 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
        request.getTargetCharacter().sendPackets(new S_StrUp(request.getTargetCharacter(), 5, request.getDuration()));
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
        cha.sendPackets(new S_StrUp(cha, 1, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_StrUp(cha, 5, duration));
    }
}
