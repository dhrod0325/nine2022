package ks.model.action.custom.impl.npc;

import ks.core.datatables.CastleTable;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class ActionInex extends L1AbstractNpcAction {
    public ActionInex(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = null;
        String[] data;

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan != null) {
            int castleId = clan.getCastleId();

            if (castleId != 0) {
                if (castleId == 4)
                    html = "orville2";
                else if (castleId == 6)
                    html = "potempin2";

                L1Castle l1castle = CastleTable.getInstance().getCastleTable(castleId);
                int money = l1castle.getShowMoney();// 결산
                int a = money / 2 * 3;// 소비액
                int b = money + a; // 총액
                int pm = l1castle.getPublicMoney();
                data = new String[]{b + "", a + "", money + "", pm + ""};

                if (html != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objId, html, data));
                }
            }
        }
    }
}
