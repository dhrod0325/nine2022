package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconAura;

public class L1SkillGlowingWeapon extends L1SkillAdapter {
    private static final int ICON_NUMBER = 113;

    public L1SkillGlowingWeapon(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.addHitUp(5 * type);
        cha.addDmgUp(5 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();
        statUp(cha, 1);
        sendIcon(cha, request.getDuration());
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        statUp(targetCharacter, -1);
        targetCharacter.sendPackets(new S_SkillIconAura(ICON_NUMBER, 0));
    }

    @Override
    public void sendIcon(L1Character targetCharacter, int duration) {
        targetCharacter.sendPackets(new S_SkillIconAura(ICON_NUMBER, duration));
    }
}
