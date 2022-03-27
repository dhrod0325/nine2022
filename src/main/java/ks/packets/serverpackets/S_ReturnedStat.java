package ks.packets.serverpackets;

import ks.core.datatables.exp.ExpTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1CalcStat;
import ks.model.pc.L1PcInstance;

public class S_ReturnedStat extends ServerBasePacket {
    public static final int START = 1;
    public static final int LEVELUP = 2;
    public static final int END = 3;
    public static final int LOGIN = 4;
    public static final int PET_PARTY = 12;

    public S_ReturnedStat(L1PcInstance pc, int type) {
        buildPacket(pc, type);
    }

    public S_ReturnedStat(int type, int count, int id, boolean ck) {
        writeC(L1Opcodes.S_OPCODE_RETURNEDSTAT);
        writeC(type);

        if (type == PET_PARTY) {
            if (ck) {
                writeC(count);
                writeC(0x00);
                writeD(0x00);
            } else {
                writeC(count);
                writeC(0x00);
                writeC(0x01);
                writeC(0x00);
                writeC(0x00);
                writeC(0x00);
            }
            writeD(id);
        }
    }

    public S_ReturnedStat(int pcObjId, int emblemId) {
        writeC(L1Opcodes.S_OPCODE_RETURNEDSTAT);
        writeC(0x3c);
        writeD(pcObjId);
        writeD(emblemId);
    }

    private void buildPacket(L1PcInstance pc, int type) {
        writeC(L1Opcodes.S_OPCODE_RETURNEDSTAT);
        writeC(type);

        switch (type) {
            case START:
                short initHp = L1CalcStat.calcInitHp(pc);
                short initMp = L1CalcStat.calcInitMp(pc);

                writeH(initHp);
                writeH(initMp);

                writeC(0x0a);
                writeC(ExpTable.getInstance().getLevelByExp(pc.getReturnStat()));
                break;
            case LEVELUP:
                writeC(pc.getLevel());
                writeC(ExpTable.getInstance().getLevelByExp(pc.getReturnStat()));
                writeH(pc.getBaseMaxHp());
                writeH(pc.getBaseMaxMp());
                writeH(pc.getBaseAc());
                writeC(pc.getAbility().getStr());
                writeC(pc.getAbility().getInt());
                writeC(pc.getAbility().getWis());
                writeC(pc.getAbility().getDex());
                writeC(pc.getAbility().getCon());
                writeC(pc.getAbility().getCha());
                break;
            case END:
                writeC(pc.getAbility().getElixirCount());
                break;
            case LOGIN:
                int[] minStat = pc.getAbility().getMinStat(pc.getClassId());
                int first = minStat[0] + minStat[5] * 16;
                int second = minStat[3] + minStat[1] * 16;
                int third = minStat[2] + minStat[4] * 16;
                writeC(first);
                writeC(second);
                writeC(third);
                writeC(0x00);
                break;
        }
    }
}