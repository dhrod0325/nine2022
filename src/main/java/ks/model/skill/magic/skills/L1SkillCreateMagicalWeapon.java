package ks.model.skill.magic.skills;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;

public class L1SkillCreateMagicalWeapon extends L1SkillAdapter {

    public L1SkillCreateMagicalWeapon(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            L1ItemInstance item = pc.getInventory().getItem(request.getTargetId());

            if (item != null && item.getItem().getType2() == 1) {
                int itemType = item.getItem().getType2();
                int safeEnchant = item.getItem().getSafeEnchant();
                int enchantLevel = item.getEnchantLevel();

                String itemName = item.getName();

                if (safeEnchant < 0) {
                    sendNoneMsg(pc);
                } else if (safeEnchant == 0) {
                    sendNoneMsg(pc);
                } else if (itemType == 1 && enchantLevel == 0) {
                    if (!item.isIdentified()) {
                        pc.sendPackets(new S_ServerMessage(161, itemName, "$245", "$247"));
                    } else {
                        itemName = "+0 " + itemName;
                        pc.sendPackets(new S_ServerMessage(161, "+0 " + itemName, "$245", "$247"));
                    }
                    item.setEnchantLevel(1);
                    pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                } else {
                    sendNoneMsg(pc);
                }
            } else {
                sendNoneMsg(pc);
            }
        }
    }

    private void sendNoneMsg(L1PcInstance pc) {
        pc.sendPackets(new S_ChatPacket(pc, "아무일도 일어나지 않았습니다.", L1Opcodes.S_OPCODE_MSG, 20));
    }
}
