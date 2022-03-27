package ks.util.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NumberUtils {
    public static int randomRound(double number) {
        double percentage = (number - Math.floor(number)) * 100;

        if (percentage == 0) {
            return ((int) number);
        } else {
            int r = new Random(System.nanoTime()).nextInt(100);
            if (r < percentage) {
                return ((int) number + 1);
            } else {
                return ((int) number);
            }
        }
    }

    public static ArrayList<Integer> getNumList(Integer... numList) {
        return new ArrayList<>(Arrays.asList(numList));
    }

    public static boolean contains(int check, Integer... numList) {
        return getNumList(numList).contains(check);
    }
}
