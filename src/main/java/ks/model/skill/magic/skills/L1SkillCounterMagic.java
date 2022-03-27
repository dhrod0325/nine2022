package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillCounterMagic extends L1SkillAdapter {
    public L1SkillCounterMagic(int skillId) {
        super(skillId);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_SkillSound(pc.getId(), 10702));
            Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 10702));
            pc.sendPackets("카운터 매직의 마력이 사라집니다");
        }
    }
}
