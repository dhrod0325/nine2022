package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_NpcChatPacket;

public class L1SkillDragonChat extends L1SkillAdapter {

    public L1SkillDragonChat(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (skillUseCharacter instanceof L1NpcInstance) {
            Broadcaster.broadcastPacket(skillUseCharacter, new S_NpcChatPacket((L1NpcInstance) skillUseCharacter, "$3717", 0));
        } else {
            Broadcaster.broadcastPacket(skillUseCharacter, new S_ChatPacket((L1PcInstance) skillUseCharacter, "$3717", 0, 0));
        }

    }
}
