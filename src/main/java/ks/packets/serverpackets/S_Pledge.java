package ks.packets.serverpackets;

import ks.constants.L1PacketBoxType;
import ks.core.network.opcode.L1Opcodes;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class S_Pledge extends ServerBasePacket {
    public S_Pledge(String htmlid, int objid, String clanname, String olmembers) {
        buildPacket(htmlid, objid, 1, clanname, olmembers, "");
    }

    public S_Pledge(String htmlid, int objid, String clanname, String olmembers, String allmembers) {
        buildPacket(htmlid, objid, 2, clanname, olmembers, allmembers);
    }

    public S_Pledge(String name, String notes) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(L1PacketBoxType.HTML_PLEDGE_WRITE_NOTES);
        writeS(name);

        byte[] text = new byte[62];
        Arrays.fill(text, (byte) 0);

        if (notes.length() != 0) {
            int i = 0;
            try {
                for (byte b : notes.getBytes("EUC-KR")) {
                    text[i++] = b;
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("오류", e);
            }
        }
        writeByte(text);
    }

    private void buildPacket(String htmlid, int objid, int type, String clanname, String olmembers, String allmembers) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objid);
        writeS(htmlid);
        writeH(type);
        writeH(0x03);
        writeS(clanname); // clanname
        writeS(olmembers); // clanmember with a space in the end
        writeS(allmembers); // all clan members names with a space in the
        // end
        // example: "player1 player2 player3 "
    }
}