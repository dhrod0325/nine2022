package ks.packets.serverpackets;

import ks.core.datatables.CastleTable;
import ks.core.datatables.SoldierTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Castle;
import ks.model.L1Soldier;

public class S_SoldierBuyList extends ServerBasePacket {
    public S_SoldierBuyList(int objId, int castle_id) {
        L1Soldier soldier = SoldierTable.getInstance().getSoldierTable(castle_id);

        if (soldier == null)
            return;

        L1Castle castle = CastleTable.getInstance().getCastleTable(castle_id);
        writeC(L1Opcodes.S_OPCODE_SOLDIERBUYLIST);
        writeD(objId);
        writeD(castle.getPublicMoney());
        writeH(castle_id);
        writeH(0);
        writeS(soldier.getSoldier1Name());
        writeH(10000);
        writeH(1);
        writeS(soldier.getSoldier2Name());
        writeH(10000);
        writeH(2);
        writeS(soldier.getSoldier3Name());
        writeH(10000);
        writeH(3);
        writeS(soldier.getSoldier4Name());
        writeH(15000);
    }
}
