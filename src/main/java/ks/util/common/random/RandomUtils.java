package ks.util.common.random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Logger logger = LogManager.getLogger();

    public static int nextInt(int whole) {
        return new Random().nextInt(whole);
    }

    public static int nextInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static boolean isWinning(int whole, int dice) {
        return new Random().nextInt(whole - 1) + 1 <= dice;
    }

    public static boolean isBoolean() {
        return new Random().nextBoolean();
    }

    public static double nextDouble() {
        return new Random().nextDouble();
    }

    public static double nextDouble(double min, double max) {
        double val = nextInt((int) min * 100, (int) max * 100);
        return val * 0.01;
    }

    public static <T extends ChanceAble> int getChanceIdx(List<T> perList) {
        int sum = 0;

        for (ChanceAble chance : perList) {
            sum += chance.getPer() * 100;
        }

        if (sum != 10000) {
            logger.warn("합계가 100%가 아님 , {}", sum);
            return -1;
        }

        List<Integer> randomNoList = new ArrayList<>();

        int idx = 0;

        for (ChanceAble chance : perList) {
            int cnt = (int) (chance.getPer() * 100);

            for (int i = 0; i < cnt; i++) {
                randomNoList.add(idx);
            }

            idx++;
        }

        Collections.shuffle(randomNoList);

        int resultIndex = randomNoList.get(0);

        randomNoList.clear();

        return resultIndex;
    }
}
