package ks.model.effect;

import ks.model.L1ArmorSetEffect;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SPMR;

public class DamageEffect implements L1ArmorSetEffect {
    private final int sp;

    private final int shortHitup;

    private final int shortDmgup;

    private final int longHitup;

    private final int longDmgup;

    public DamageEffect(int sp, int shortHitup, int shortDmgup, int longHitup,
                        int longDmgup) {
        this.sp = sp;
        this.shortHitup = shortHitup;
        this.shortDmgup = shortDmgup;
        this.longHitup = longHitup;
        this.longDmgup = longDmgup;
    }

    public void giveEffect(L1PcInstance pc) {
        pc.getAbility().addSp(sp);
        pc.addHitUp(shortHitup);
        pc.addDmgUp(shortDmgup);
        pc.addBowHitup(longHitup);
        pc.addBowDmgUp(longDmgup);
        pc.sendPackets(new S_SPMR(pc));
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.getAbility().addSp(-sp);
        pc.addHitUp(-shortHitup);
        pc.addDmgUp(-shortDmgup);
        pc.addBowHitup(-longHitup);
        pc.addBowDmgUp(-longDmgup);
        pc.sendPackets(new S_SPMR(pc));
    }
}
