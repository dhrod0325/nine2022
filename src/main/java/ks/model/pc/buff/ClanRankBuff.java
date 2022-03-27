package ks.model.pc.buff;

import ks.model.L1Clan;
import ks.model.pc.L1PcInstance;

import static ks.constants.L1SkillId.*;

public class ClanRankBuff {
    private int oldClanRank = 0;

    private final L1PcInstance pc;

    public ClanRankBuff(L1PcInstance pc) {
        this.pc = pc;
    }

    public void startBuff() {
        int level;

        L1Clan clan = pc.getClan();

        if (clan == null) {
            level = 0;
        } else {
            level = clan.getClanLevel();
        }

        if (oldClanRank != level) {
            pc.getSkillEffectTimerSet().removeSkillEffect(CLAN_BUFF1, CLAN_BUFF2, CLAN_BUFF3, CLAN_BUFF4, CLAN_BUFF5);

            if (level > 0) {
                int skill = 600000 + (level - 1);
                pc.getSkillEffectTimerSet().setSkillEffect(skill, 0);
            }
        }

        oldClanRank = level;
    }
}
