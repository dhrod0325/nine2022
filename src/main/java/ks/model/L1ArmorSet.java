package ks.model;

import ks.model.pc.L1PcInstance;

public abstract class L1ArmorSet {
    public abstract void giveEffect(L1PcInstance pc);

    public abstract void cancelEffect(L1PcInstance pc);

    public abstract boolean isValid(L1PcInstance pc);

    public abstract boolean isPartOfSet(int id);

    public abstract boolean isEquippedRingOfArmorSet(L1PcInstance pc);
}

