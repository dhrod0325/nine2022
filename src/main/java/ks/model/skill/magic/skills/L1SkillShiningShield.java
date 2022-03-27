package ks.model.skill.magic.skills;

import ks.app.LineageAppContext;
import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconAura;

import java.time.Instant;

public class L1SkillShiningShield extends L1SkillAdapter {
    private static final int ICON_NUMBER = 114;

    public L1SkillShiningShield(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getAC().addAc(-8 * type);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();
        statUp(cha, 1);

        logger.debug("du1:{}", request.getDuration());
        sendIcon(cha, request.getDuration());
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        statUp(targetCharacter, -1);
        targetCharacter.sendPackets(new S_SkillIconAura(ICON_NUMBER, 0));
    }

    @Override
    public void sendIcon(L1Character targetCharacter, int duration) {
        logger.debug("du2:{}", duration);
        LineageAppContext.commonTaskScheduler().schedule(() -> targetCharacter.sendPackets(new S_SkillIconAura(ICON_NUMBER, duration)), Instant.now().plusMillis(100));
    }
}
