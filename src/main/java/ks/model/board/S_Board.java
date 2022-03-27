package ks.model.board;

import ks.core.datatables.BoardTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.ServerBasePacket;

import java.util.List;
import java.util.Map;

public class S_Board extends ServerBasePacket {
    public S_Board(L1NpcInstance board, L1PcInstance pc, int number) {
        buildPacket(board, pc, number);
    }

    private void buildPacket(L1NpcInstance board, L1PcInstance pc, int number) {
        List<Map<String, Object>> list = BoardTable.getInstance().selectListByBoardId(board.getNpcId(), number);

        writeC(L1Opcodes.S_OPCODE_BOARD);
        writeC(0);
        writeD(board.getId());

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

        for (Map<String, Object> o : list) {
            writeD((Integer) o.get("id"));

            String name = o.get("name").toString();

//            if (pc != null) {
//                if (pc.getName().equalsIgnoreCase(name)) {
//                    writeS(pc.getHuntName());
//                } else {
//                    writeS(name);
//                }
//            } else {
            writeS(name);
//            }

            writeS(o.get("date").toString());
            writeS(o.get("title").toString());
        }
    }
}
