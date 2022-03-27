package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class ActionEnt extends L1AbstractNpcAction {
    public ActionEnt(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 4206000) {
            if (pc.getLevel() != pc.getHighLevel()) {
                pc.sendPackets(new S_NpcChatPacket(npc, "레벨이 다운된 케릭터 입니다. 렙업후 이용해주세요.", 0));
            } else if (!pc.getInventory().checkItem(L1ItemId.REMINISCING_CANDLE)) {
                pc.sendPackets(new S_NpcChatPacket(npc, "스테이터스 초기화에 필요한 아이템이 없습니다.", 0));
            } else {
                if (pc.getLevel() > 50)
                    if (pc.getInventory().checkItem(L1ItemId.REMINISCING_CANDLE)) {
                        pc.getInventory().consumeItem(L1ItemId.REMINISCING_CANDLE, 1);
                        L1Teleport.teleport(pc, 32723 + RandomUtils.nextInt(10), 32851 + RandomUtils.nextInt(10), (short) 5166, 5, true);
                        L1CommonUtils.statInit(pc);
                    } else {
                        pc.sendPackets(new S_NpcChatPacket(npc, "스테이터스 초기화에 필요한 아이템이 없습니다.", 0));
                    }
                else {
                    pc.sendPackets(new S_NpcChatPacket(npc, "회상의 촛불은 레벨51부터 사용하실수 있습니다.", 0));
                }
            }
        }
    }
}
