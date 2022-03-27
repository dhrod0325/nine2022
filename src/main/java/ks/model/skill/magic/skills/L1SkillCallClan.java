package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Message_YN;

public class L1SkillCallClan extends L1SkillAdapter {
    public L1SkillCallClan(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(request.getTargetId());

            if (clanPc != null) {
                clanPc.setTempID(pc.getId());
                clanPc.sendPackets(new S_Message_YN(729, ""));
            }
        }
    }
}
