package ks.model.skill.magic.skills;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;

public class L1SkillBlessedArmor extends L1SkillAdapter {
    public L1SkillBlessedArmor(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance item = pc.getInventory().getItem(request.getTargetItemId());

            if (item != null && item.getItem().getType2() == 2 && item.getItem().getType() == 2) {
                pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
                item.setSkillArmorEnchant(pc, request.getDuration() * 1000);
            } else {
                pc.sendPackets(new S_ChatPacket(pc, "아무일도 일어나지 않았습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            }
        }
    }
}
