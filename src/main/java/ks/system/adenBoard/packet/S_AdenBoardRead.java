package ks.system.adenBoard.packet;

import ks.core.network.opcode.L1Opcodes;
import ks.packets.serverpackets.ServerPacket;
import ks.system.adenBoard.database.AdenBankAccountTable;
import ks.system.adenBoard.model.AdenBoard;
import ks.system.adenBoard.model.AdenBuy;

public class S_AdenBoardRead extends ServerPacket {
    public S_AdenBoardRead(int number) {
        buildPacket(number);
    }

    public S_AdenBoardRead buildPacket(int number) {
        AdenBankAccountTable dao = AdenBankAccountTable.getInstance();

        AdenBoard v = AdenBoard.create(dao.getAdenData(number));

        if (v != null) {
            writeC(L1Opcodes.S_OPCODE_BOARDREAD);
            writeD(number);
            writeS(v.getName());
            writeS(v.getSubject());
            writeS(v.toStringDays());

            String memo = v.getMemo();

            AdenBuy buyer = dao.getAdenBuy(v.getId());

            if (buyer != null) {
                memo += "\r\n";
                memo += "구매자 : " + buyer.getBuyer_name();
            }

            writeS(memo);
        }

        return this;
    }

}
