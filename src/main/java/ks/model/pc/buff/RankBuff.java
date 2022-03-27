package ks.model.pc.buff;

import ks.app.config.prop.CodeConfig;
import ks.model.pc.L1PcInstance;
import ks.model.rank.L1RankChecker;
import ks.packets.serverpackets.S_PacketBox;

import static ks.constants.L1PacketBoxType.UNLIMITED_ICON1;
import static ks.constants.L1SkillId.*;

public class RankBuff {
    private final L1PcInstance pc;

    private int oldRank;

    public RankBuff(L1PcInstance pc) {
        this.pc = pc;
    }

    public void stop(int skillId) {
        if (pc.getHighLevel() >= CodeConfig.RANK_BUFF_MIN_LEVEL) {
            int icon;

            if (skillId == RANK_BUFF_3) {
                icon = 499;
            } else if (skillId == RANK_BUFF_2) {
                icon = 500;
                pc.getAC().addAc(1);
            } else {
                icon = 501;
                pc.getAC().addAc(1);
                pc.addDamageReductionByArmor(-1);
            }

            pc.sendPackets(new S_PacketBox(UNLIMITED_ICON1, icon, false));
        }
    }

    public void startBuff() {
        int rank = L1RankChecker.getInstance().getClassRank(pc);

        if (oldRank != rank) {
            stop(RANK_BUFF_1);
            stop(RANK_BUFF_2);
            stop(RANK_BUFF_3);

            int buff = 0;

            if (rank == 3) {
                buff = RANK_BUFF_3;
            } else if (rank == 2) {
                buff = RANK_BUFF_2;
            } else if (rank == 1) {
                buff = RANK_BUFF_1;
            }

            if (buff > 0) {
                if (pc.getHighLevel() >= CodeConfig.RANK_BUFF_MIN_LEVEL) {
                    int icon38;

                    if (buff == RANK_BUFF_3) {
                        icon38 = 499;
                    } else if (buff == RANK_BUFF_2) {
                        icon38 = 500;
                        pc.getAC().addAc(-1);
                    } else {
                        icon38 = 501;
                        pc.getAC().addAc(-1);
                        pc.addDamageReductionByArmor(1);
                    }

                    pc.sendPackets(new S_PacketBox(UNLIMITED_ICON1, icon38, true));
                }
            }
        }

        oldRank = rank;
    }

    public void reload() {
        oldRank = 0;
        startBuff();
    }
}
