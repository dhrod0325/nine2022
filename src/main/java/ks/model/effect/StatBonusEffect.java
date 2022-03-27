package ks.model.effect;

import ks.model.L1ArmorSetEffect;
import ks.model.pc.L1PcInstance;

public class StatBonusEffect implements L1ArmorSetEffect {
    private final int str;

    private final int dex;

    private final int con;

    private final int wis;

    private final int cha;

    private final int intl;

    public StatBonusEffect(int str, int dex, int con, int wis, int cha, int intl) {
        this.str = str;
        this.dex = dex;
        this.con = con;
        this.wis = wis;
        this.cha = cha;
        this.intl = intl;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.getAbility().addAddedStr((byte) str);
        pc.getAbility().addAddedDex((byte) dex);
        pc.getAbility().addAddedCon((byte) con);
        pc.getAbility().addAddedWis((byte) wis);
        pc.getAbility().addAddedCha((byte) cha);
        pc.getAbility().addAddedInt((byte) intl);
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.getAbility().addAddedStr((byte) -str);
        pc.getAbility().addAddedDex((byte) -dex);
        pc.getAbility().addAddedCon((byte) -con);
        pc.getAbility().addAddedWis((byte) -wis);
        pc.getAbility().addAddedCha((byte) -cha);
        pc.getAbility().addAddedInt((byte) -intl);
    }
}
