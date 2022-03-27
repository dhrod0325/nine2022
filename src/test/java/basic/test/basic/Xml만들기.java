package basic.test.basic;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.attack.utils.L1AttackUtils;
import basic.test.BaseTest;
import ks.util.common.random.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Xml만들기 extends BaseTest {
    //악몽의장궁 13541


    public static void main(String[] args) {


        List<Integer> list = new ArrayList<>(Arrays.asList(
                450014,
                45000618,
                45000619,
                45000616,
                58,
                54
        ));

        /* 상급 방어구
        List<Integer> list = new ArrayList<>(Arrays.asList(
                55000095,
                55000096,
                55000097,
                55000098,
                55000084,
                55000054,
                500010,
                55000085,
                20050,
                20049,
                20079,
                500042,
                76795,
                20190,
                55000069,
                500011,
                55000105,
                55000070,
                55000071,
                55000072,
                20218,
                76796
        ));
 */
              /* 최상급 방어구
        List<Integer> list = new ArrayList<>(Arrays.asList(
                155000101,
                155000102,
                155000103,
                155000104,
                420103,
                420102,
                420101,
                420100,
                420107,
                420106,
                420105,
                420104,
                420111,
                420110,
                420109,
                420108
        ));
        */

        for (Integer i : list) {
            double p = 100d / list.size();

            ItemTable.getInstance().load();

            L1Item item = ItemTable.getInstance().findItem(i);

            String out = String.format("<Item ItemId=\"%d\" Count=\"1\" Chance=\"%02.02f\"/><!-- %s -->", i, p, item.getName());
            System.out.println(out);
        }
        System.out.println("size : " + list.size());

        System.exit(0);
    }

    public static int calcHit(int attackerHitRate, int attackerLevel, int targetLevel, int targetAc) {
        if (targetAc >= 0) {
            targetAc = targetAc - 10;
        }

        int ac = -targetAc;

        if (ac < 1) {
            ac = 1;
        }

        int levDiffRate = (attackerLevel - targetLevel);
        int defRate = (ac / 2) - levDiffRate;

        int attackRate = attackerHitRate / 2;
        defRate -= attackRate;

        return 100 - defRate;
    }


    public static boolean test(int attackerLevel, int attackerHitUp, int targetLevel, int targetAc) {
        int hitRate = calcHit(attackerLevel, attackerHitUp, targetLevel, targetAc);

        if (hitRate < 2)
            hitRate = 2;// 최소치

        if (hitRate > 100)
            hitRate = 100;// 최대치

        hitRate -= L1AttackUtils.missByUncannyDodge(-targetAc);

        return RandomUtils.isWinning(100, hitRate);
    }

    public static int 명중횟수(int 공격횟수) {
        int sum = 0;

        for (int i = 0; i < 공격횟수; i++) {
            if (test(70, 70, 80, -90)) {
                sum++;
            }
        }

        return sum;
    }
}
