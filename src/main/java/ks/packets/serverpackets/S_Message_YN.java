package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;

import java.util.HashMap;

public class S_Message_YN extends ServerBasePacket {
    public static HashMap<Integer, MessageYnWrapper> idxMap = new HashMap<>();

    private static int YNCount = 0;

    public S_Message_YN(int type) {
        buildPacket(type, "", null, null, 1);
    }

    public S_Message_YN(int type, String msg1) {
        buildPacket(type, msg1, null, null, 1);
    }

    public S_Message_YN(int type, String msg1, String msg2) {
        buildPacket(type, msg1, msg2, null, 2);
    }

    public S_Message_YN(int type, String msg1, String msg2, String msg3) {
        buildPacket(type, msg1, msg2, msg3, 3);
    }

    public S_Message_YN(int type, int idx, String msg) {
        this(type, idx, msg, null);
    }

    public S_Message_YN(int type, int idx, String msg, L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_YES_NO);
        writeH(0);
        writeD(idx);
        writeH(type);
        writeS(msg);

        idxMap.put(type, new MessageYnWrapper(idx, pc));
    }

    private void buildPacket(int type, String msg1, String msg2, String msg3, int check) {
        writeC(L1Opcodes.S_OPCODE_YES_NO);
        writeH(0x00);
        writeD(++YNCount);
        writeH(type);

        if (check == 1) {
            writeS(msg1);
        } else if (check == 2) {
            writeS(msg1);
            writeS(msg2);
        } else if (check == 3) {
            writeS(msg1);
            writeS(msg2);
            writeS(msg3);
        }
    }

    public static class MessageYnWrapper {
        private Integer idx;
        private L1PcInstance pc;

        public MessageYnWrapper(Integer idx, L1PcInstance pc) {
            this.idx = idx;
            this.pc = pc;
        }

        public Integer getIdx() {
            return idx;
        }

        public void setIdx(Integer idx) {
            this.idx = idx;
        }

        public L1PcInstance getPc() {
            return pc;
        }

        public void setPc(L1PcInstance pc) {
            this.pc = pc;
        }
    }
}
