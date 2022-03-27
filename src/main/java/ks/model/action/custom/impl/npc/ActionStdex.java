package ks.model.action.custom.impl.npc;

import ks.core.datatables.CastleTable;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class ActionStdex extends L1AbstractNpcAction {
    public ActionStdex(String action, L1PcInstance pc, L1Object obj) {
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
                    html = "orville3";
                else if (castleId == 6)
                    html = "potempin3";

                L1Castle l1castle = CastleTable.getInstance().getCastleTable(castleId);
                int i = l1castle.getShowMoney();// 계산 금액

                int a = (i + i / 2 * 3) / 100 * 25;// 25%
                int b = (i + i / 2 * 3) / 100 * 10;// 10%
                int c = (i + i / 2 * 3) / 100 * 5;// 5%
                data = new String[]{"" + a + "", "" + b + "", "" + c + "", "" + c + "", "" + b + "", "" + c + ""};

                if (html != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objId, html, data));
                }

            }
        }
    }
}
