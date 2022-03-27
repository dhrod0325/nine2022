package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Npc;
import ks.model.instance.L1MonsterInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillWeakElemental extends L1SkillAdapter {
    public L1SkillWeakElemental(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1MonsterInstance) {
            L1Npc npcTemp = ((L1MonsterInstance) targetCharacter).getTemplate();
            int weakAttr = npcTemp.getWeakAttr();

            if ((weakAttr & 1) == 1) {
                Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 2169));
            }
            if ((weakAttr & 2) == 2) {
                Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 2167));
            }
            if ((weakAttr & 4) == 4) {
                Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 2166));
            }
            if ((weakAttr & 8) == 8) {
                Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 2168));
            }
        }

    }
}
