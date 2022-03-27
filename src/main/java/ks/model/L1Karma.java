package ks.model;

import ks.util.common.IntRange;

public class L1Karma {
    private static final int[] KARMA_POINT = {10000, 20000, 100000, 500000, 1500000, 3000000, 5000000, 10000000, 15500000};

    private static final IntRange KARMA_RANGE = new IntRange(-15500000, 15500000);

    private int karma = 0;

    public int get() {
        return karma;
    }

    public void set(int i) {
        karma = KARMA_RANGE.ensure(i);
    }

    public void add(int i) {
        set(karma + i);
    }

    public int getLevel() {
        boolean isMinus = false;
        int karmaLevel = 0;

        int karma = get();

        if (karma < 0) {
            isMinus = true;
            karma *= -1;
        }

        for (int point : KARMA_POINT) {
            if (karma >= point) {
                karmaLevel++;
                if (karmaLevel >= 8) {
                    break;
                }
            } else {
                break;
            }
        }

        if (isMinus) {
            karmaLevel *= -1;
        }

        return karmaLevel;
    }

    public int getPercent() {
        int karma = get();
        int karmaLevel = getLevel();
        if (karmaLevel == 0 || karmaLevel == 8) {
            return 0;
        }

        if (karma < 0) {
            karma *= -1;
            karmaLevel *= -1;
        }

        return 100 * (karma - KARMA_POINT[karmaLevel - 1]) / (KARMA_POINT[karmaLevel] - KARMA_POINT[karmaLevel - 1]);
    }
}
