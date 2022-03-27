package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Invis;
import ks.packets.serverpackets.S_OtherCharPacks;

public class L1SkillInvisibility extends L1SkillAdapter {
    public L1SkillInvisibility(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1MagicUtils.startInvisible(request.getTargetCharacter());
    }

    @Override
    public void stopSkill(L1Character cha) {
        L1MagicUtils.stopInvisible(cha);

        cha.sendPackets(new S_Invis(cha.getId(), 0));

        if (cha instanceof L1PcInstance) {
            Broadcaster.broadcastPacket(cha, new S_OtherCharPacks((L1PcInstance) cha));
        }
    }
}
