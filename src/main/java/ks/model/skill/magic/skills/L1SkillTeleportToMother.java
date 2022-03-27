package ks.model.skill.magic.skills;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_Paralysis;

public class L1SkillTeleportToMother extends L1SkillAdapter {

    public L1SkillTeleportToMother(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;

            if (pc.isEscapable()) {
                L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5, true);
            } else {
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                pc.sendPackets(new S_ChatPacket(pc, "주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            }
        }
    }
}
