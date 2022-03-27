package ks.model.action.custom.impl.npc;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_SystemMessage;

import static ks.constants.L1SkillId.*;

public class ActionZendor extends L1AbstractNpcAction {
    public ActionZendor(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 777835) {
            if (action.equalsIgnoreCase("a")) { //근거리버프
                if (pc.getInventory().checkItem(40308, 30000)) {
                    if (pc.getLevel() >= 5) {
                        pc.getInventory().consumeItem(40308, 100000);
                        L1SkillUtils.skillByGm(pc, PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON);
                    } else {
                        pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분치않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                }
            }

            if (action.equalsIgnoreCase("1")
                    || action.equalsIgnoreCase("2")
                    || action.equalsIgnoreCase("3")
                    || action.equalsIgnoreCase("4")
                    || action.equalsIgnoreCase("5")) {
                pc.sendPackets(new S_NPCTalkReturn(objId, "bs_m4"));
            }
        }
    }
}
