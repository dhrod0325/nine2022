package ks.system.race.util;

import ks.util.common.random.RandomUtils;

public class L1RaceUtils {
    public static double getRandomProbability() {
        return (RandomUtils.nextInt(10000) + 1) / 100D;
    }
}
