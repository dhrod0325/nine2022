package ks.model.board;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;
import ks.packets.serverpackets.ServerBasePacket;
import ks.util.common.SqlUtils;

import java.util.Map;

public class S_BoardRead extends ServerBasePacket {
    public S_BoardRead(L1NpcInstance board, int number) {
        buildPacket(board, number);
    }

    private void buildPacket(L1NpcInstance board, int number) {
        Map<String, Object> data = SqlUtils.queryForMap("SELECT * FROM board WHERE id=? AND board_id=?", number, board.getNpcId());

        if (data != null) {
            writeC(L1Opcodes.S_OPCODE_BOARDREAD);
            writeD(number);
            writeS(data.get("name").toString());
            writeS(data.get("title").toString());
            writeS(data.get("date").toString());
            writeS(data.get("content").toString());
        }
    }
}
