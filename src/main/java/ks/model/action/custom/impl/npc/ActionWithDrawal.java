package ks.model.action.custom.impl.npc;

import ks.core.datatables.CastleTable;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Drawal;

public class ActionWithDrawal extends L1AbstractNpcAction {
    public ActionWithDrawal(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (pc.getId() != pc.getClan().getLeaderId() || pc.getClanRank() != 10 || !pc.isCrown()) {
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            int castleId = clan.getCastleId();

            if (castleId != 0) {
                L1Castle l1castle = CastleTable.getInstance().getCastleTable(castleId);

                if (l1castle.getPublicMoney() <= 0) {
                    pc.sendPackets("인출 가능한 자금이 없습니다");
                    return;
                }

                pc.sendPackets(new S_Drawal(pc.getId(), l1castle.getPublicMoney()));
            }
        }
    }
}
