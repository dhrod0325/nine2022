package ks.model.trap;

import ks.util.common.random.RandomUtils;

public class L1Dice {
    private final int faces;

    public L1Dice(int faces) {
        this.faces = faces;
    }

    public int getFaces() {
        return faces;
    }

    public int roll() {
        return RandomUtils.nextInt(faces) + 1;
    }

    public int roll(int count) {
        int n = 0;
        for (int i = 0; i < count; i++) {
            n += roll();
        }
        return n;
    }
}
