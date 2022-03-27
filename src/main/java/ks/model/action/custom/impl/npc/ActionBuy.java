package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.core.datatables.shopInfo.NpcShopInfo;
import ks.core.datatables.shopInfo.NpcShopInfoTable;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PremiumShopSellList;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_ShopSellList;

public class ActionBuy extends L1AbstractNpcAction {
    public ActionBuy(String actionName, L1PcInstance pc, L1Object obj) {
        super(actionName, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getInventory().isFullWeightOrFullCount()) {
            pc.sendPackets(new S_ServerMessage(270));
            return;
        }

        if (isNpcSellOnly(npc)) {
            return;
        }

        NpcShopInfo shopInfo = NpcShopInfoTable.getInstance().selectByNpcId(npc.getNpcId());

        if (shopInfo != null) {
            if (shopInfo.getTargetItemId() != L1ItemId.ADENA) {
                pc.sendPackets(new S_PremiumShopSellList(npc.getId()));
            }
        } else {
            if (npcId == 4220000 || npcId == 4220001 || npcId == 4220002 || npcId == 4220003) {
                pc.sendPackets(new S_PremiumShopSellList(npc.getId()));
            } else {
                pc.sendPackets(new S_ShopSellList(npc.getId()));
            }
        }
    }

    public boolean isNpcSellOnly(L1NpcInstance npc) {
        String npcName = npc.getTemplate().getName();

        return npc.getNpcId() == 70027 || "아덴상단".equals(npcName);
    }
}
