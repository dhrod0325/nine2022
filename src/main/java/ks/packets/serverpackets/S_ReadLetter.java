package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.util.common.SqlUtils;

public class S_ReadLetter extends ServerBasePacket {
    public S_ReadLetter(int type, int id) {
        buildPacket(type, id);
    }

    private void buildPacket(int type, int id) {
        writeC(L1Opcodes.S_OPCODE_LETTER);
        writeC(type); // 16:메일함 17:혈맹메일

        SqlUtils.select("SELECT * FROM letter WHERE item_object_id = ?", (rs, i) -> {
            writeD(rs.getInt(1)); // 게시글 넘버
            writeSS(rs.getString(8)); // 내용
            writeC(id); // 일
            writeS(rs.getString(3)); // 보낸사람
            writeSS(rs.getString(7)); // 제목

            return null;
        }, id);
    }
}
