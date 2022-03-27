package ks.model;

import ks.model.pc.L1PcInstance;

public interface L1ArmorSetEffect {
    void giveEffect(L1PcInstance pc);

    void cancelEffect(L1PcInstance pc);
}
