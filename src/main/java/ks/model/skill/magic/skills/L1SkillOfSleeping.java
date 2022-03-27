package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_NPCPack;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_ServerMessage;

public class L1SkillOfSleeping extends L1SkillAdapter {
    public L1SkillOfSleeping(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;

            if (!pc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
                pc.sendPackets(new S_ServerMessage(297));
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
                pc.setPoisonEffect(2);
                targetCharacter.setSleeped(true);
            }
        } else if (targetCharacter instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;

            if (!npc.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
                npc.setPoisonEffect(2);
                npc.setSleeped(true);
            }
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
            pc.setPoisonEffect(0);
            targetCharacter.setSleeped(false);
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else if (targetCharacter instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            npc.setPoisonEffect(0);
            npc.setSleeped(false);

            Broadcaster.broadcastPacket(npc, new S_NPCPack(npc));
        }
    }
}
