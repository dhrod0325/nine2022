package ks.model.skill.magic.skills;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1CastleLocation;
import ks.model.L1Character;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_Paralysis;

public class L1SkillRunClan extends L1SkillAdapter {
    public L1SkillRunClan(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(request.getTargetId());
            if (clanPc != null) {
                if (pc.isEscapable()) {
                    boolean castleArea = L1CastleLocation.checkInAllWarArea(clanPc.getX(), clanPc.getY(), clanPc.getMapId());

                    if ((clanPc.getMapId() == 0 || clanPc.getMapId() == 4 || clanPc.getMapId() == 304) && !castleArea) {
                        L1Teleport.teleport(pc, clanPc.getX(), clanPc.getY(), clanPc.getMapId(), 5, true);
                    } else {
                        pc.sendPackets(new S_ChatPacket(pc, "당신의 파트너는 지금 당신이 갈 수 없는 곳에서 플레이를 하고 있습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                    }
                } else {
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                    pc.sendPackets(new S_ChatPacket(pc, "주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                }
            }
        }
    }
}
