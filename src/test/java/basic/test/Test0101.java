package basic.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test0101 {
    public static void main(String[] args) {
        List<Double> perList = new ArrayList<>(Arrays.asList(90d, 5d, 5d));
        List<Integer> randomNoList = new ArrayList<>();

        int idx = 0;

        for (Double d : perList) {
            int cnt = (int) (d * 1000);

            for (int i = 0; i < cnt; i++) {
                randomNoList.add(idx);
            }

            idx++;
        }

        Collections.shuffle(randomNoList);

        int resultIdx = randomNoList.get(0);
    }
}
