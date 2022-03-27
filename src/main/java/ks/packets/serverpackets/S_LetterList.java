package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class S_LetterList extends ServerBasePacket {
    public S_LetterList(L1PcInstance pc, int type, int count) {
        buildPacket(pc, type, count);
    }

    private void buildPacket(L1PcInstance pc, int type, int count) {
        int cnt = SqlUtils.selectInteger("SELECT count(*) as cnt FROM letter WHERE receiver=? AND template_id = ? order by date limit ?", pc.getName(), type, count);

        writeC(L1Opcodes.S_OPCODE_LETTER);
        writeC(type);
        writeH(cnt);

        SqlUtils.query("SELECT * FROM letter WHERE receiver=? AND template_id = ? order by date limit ?  ", (rs, i) -> {
            writeD(rs.getInt(1));
            writeC(rs.getInt(9));
            try {
                Date date = new SimpleDateFormat("yyyyMMdd").parse(rs.getString(5));
                String dateString = new SimpleDateFormat("yy/MM/dd").format(date);
                String[] str = dateString.split("/");

                writeC(Integer.parseInt(str[0]));//년
                writeC(Integer.parseInt(str[1]));//월
                writeC(Integer.parseInt(str[2]));//일
                writeS(rs.getString(3));
                writeSS(rs.getString(7));
            } catch (Exception e) {
                logger.error("오류", e);
            }

            return null;
        }, pc.getName(), type, count);
    }
}
