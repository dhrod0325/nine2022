package ks.model;

import ks.util.common.IntRange;

public class AC {
    private int ac = 0;
    private int baseAc = 0;

    public int getAc() {
        return ac;
    }

    public void setAc(int i) {
        baseAc = i;
        ac = IntRange.ensure(i, -255, 127);
    }

    public void addAc(int i) {
        setAc(baseAc + i);
    }
}
