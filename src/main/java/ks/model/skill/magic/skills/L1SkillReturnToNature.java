package ks.model.skill.magic.skills;

import ks.app.config.prop.CodeConfig;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1SummonInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillReturnToNature extends L1SkillAdapter {
    public L1SkillReturnToNature(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (CodeConfig.RETURN_TO_NATURE && targetCharacter instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) targetCharacter;
            Broadcaster.broadcastPacket(summon, new S_SkillSound(summon.getId(), 2245));
            summon.returnToNature();
        } else {
            skillUseCharacter.sendPackets(new S_ServerMessage(79));
        }

    }
}
