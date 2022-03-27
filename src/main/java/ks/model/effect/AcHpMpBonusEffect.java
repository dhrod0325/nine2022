package ks.model.effect;

import ks.model.L1ArmorSetEffect;
import ks.model.pc.L1PcInstance;

public class AcHpMpBonusEffect implements L1ArmorSetEffect {
    private final int ac;

    private final int addHp;

    private final int addMp;

    private final int regenHp;

    private final int regenMp;

    private final int addMr;

    public AcHpMpBonusEffect(int ac, int addHp, int addMp, int regenHp, int regenMp, int addMr) {
        this.ac = ac;
        this.addHp = addHp;
        this.addMp = addMp;
        this.regenHp = regenHp;
        this.regenMp = regenMp;
        this.addMr = addMr;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.getAC().addAc(ac);
        pc.addMaxHp(addHp);
        pc.addMaxMp(addMp);
        pc.addHpr(regenHp);
        pc.addMpr(regenMp);
        pc.getResistance().addMr(addMr);
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.getAC().addAc(-ac);
        pc.addMaxHp(-addHp);
        pc.addMaxMp(-addMp);
        pc.addHpr(-regenHp);
        pc.addMpr(-regenMp);
        pc.getResistance().addMr(-addMr);
    }
}
