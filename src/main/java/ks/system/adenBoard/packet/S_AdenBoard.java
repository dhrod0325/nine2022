package ks.system.adenBoard.packet;

import ks.core.network.opcode.L1Opcodes;
import ks.packets.serverpackets.ServerPacket;
import ks.system.adenBoard.database.AdenBankAccountTable;
import ks.system.adenBoard.model.AdenBoard;

import java.util.List;
import java.util.Map;

public class S_AdenBoard extends ServerPacket {
    public S_AdenBoard(int objectId) {
        buildPacket(objectId, 0);
    }

    public S_AdenBoard(int objectId, int number) {
        buildPacket(objectId, number);
    }

    private void buildPacket(int objectId, int number) {
        AdenBankAccountTable dao = AdenBankAccountTable.getInstance();

        List<Map<String, Object>> list = dao.getAdenDataList(number, 8);

        writeC(L1Opcodes.S_OPCODE_BOARD);
        writeC(0);
        writeD(objectId);

        if (number == 0) {
            writeD(0x7fffffff);
        } else {
            writeD(number);
        }

        writeC(list.size());

        if (number == 0) {
            writeC(0);
            writeH(300);
        }

        for (Map<String, Object> data : list) {
            AdenBoard b = AdenBoard.create(data);

            if (b != null) {
                writeD(b.getId());
                writeS(b.getName());
                writeS(b.toStringDays());
                writeS(b.getSubject());
            }
        }
    }
}
