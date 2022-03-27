package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;


public class S_Paralysis extends ServerBasePacket {
    public static final int TYPE_PARALYSIS = 1;
    public static final int TYPE_PARALYSIS2 = 2;
    public static final int TYPE_SLEEP = 3;
    public static final int TYPE_FREEZE = 4;
    public static final int TYPE_STUN = 5;
    public static final int TYPE_BIND = 6;
    public static final int TYPE_TELEPORT_UNLOCK = 7;

    private int paralysisType;
    private boolean paralysisFlag;

    public S_Paralysis(int type, boolean flag) {
        setParalysisType(type);
        setParalysisFlag(flag);
        writeC(L1Opcodes.S_OPCODE_PARALYSIS);

        if (type == TYPE_PARALYSIS) {
            if (flag) {
                writeC(2);
            } else {
                writeC(3);
            }
        } else if (type == TYPE_PARALYSIS2) {
            if (flag) {
                writeC(4);
            } else {
                writeC(5);
            }
        } else if (type == TYPE_TELEPORT_UNLOCK) {
            writeC(7);
        } else if (type == TYPE_SLEEP) {
            if (flag) {
                writeC(10);
            } else {
                writeC(11);
            }
        } else if (type == TYPE_FREEZE) {
            if (flag) {
                writeC(12);
            } else {
                writeC(13);
            }
        } else if (type == TYPE_STUN) {
            if (flag) {
                writeC(22);
            } else {
                writeC(23);
            }
        } else if (type == TYPE_BIND) {
            if (flag) {
                writeC(24);
            } else {
                writeC(25);
            }
        }
    }

    public boolean isParalysisFlag() {
        return paralysisFlag;
    }

    public void setParalysisFlag(boolean paralysisFlag) {
        this.paralysisFlag = paralysisFlag;
    }

    public int getParalysisType() {
        return paralysisType;
    }

    public void setParalysisType(int paralysisType) {
        this.paralysisType = paralysisType;
    }
}
