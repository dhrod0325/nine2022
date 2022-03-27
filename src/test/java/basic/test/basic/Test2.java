//package ks.test;
//
//import ks.core.datatables.SkillsTable;
//import ks.model.L1Skills;
//import ks.model.attack.utils.L1AttackUtils;
//import ks.model.attack.utils.L1MagicUtils;
//import ks.model.pc.L1PcInstance;
//import ks.util.common.IntRange;
//import ks.util.common.RandomUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import static ks.constants.L1SkillId.CANCELLATION;
//
//public class Test2 extends BaseTest {
//    public static void main(String[] args) throws InterruptedException {
//
//        L1PcInstance a = new L1PcInstance();
//        a.setName("a");
//
//        L1PcInstance b = new L1PcInstance();
//        b.setName("b");
//
//        L1PcInstance c = new L1PcInstance();
//        c.setName("c");
//
//        List<L1PcInstance> list = new ArrayList<>();
//        list.add(a);
//        list.add(b);
//        list.add(c);
//
//        a = null;
//
//        System.out.println(list);
//    }
//
//    public static int mp(int ii, int min) {
//        int up = 0;
//
//        int start = 18;
//
//        for (int i = 2; i <= 52; i++) {
//            up += upMp(start, ii, min);
//
//            if (i >= 52) {
//                start++;
//            }
//        }
//
//        return up;
//    }
//
//    public static int upMp(int baseWis, int n, int min) {
//        baseWis = IntRange.ensure(baseWis, 5, 25);
//        int up = baseWis - n;
//        return IntRange.ensure(up, min, 12) + RandomUtils.nextInt(2);
//    }
//
//    public static int hp(int ii) {
//        int hpUp = 0;
//        int start = 8;
//
//        for (int i = 2; i <= 52; i++) {
//            if (i >= 52 && start < 35) {
//                start++;
//            }
//
//            hpUp += upHp(start, ii);
//        }
//
//        return hpUp;
//    }
//
//    public static int upHp(int baseCon, int n) {
//        baseCon = IntRange.ensure(baseCon, 5, 35);
//        int upHp = baseCon - n;
//        return IntRange.ensure(upHp, 3, 35) + RandomUtils.nextInt(2);
//    }
//
//    public static void 확률마법테스트() {
//        int attackInt = 35;
//        int defenseMr = 100;
//        int cnt = 100;
//
//        int skillId = CANCELLATION;
//
//        double p = 0;
//        double sum = 0;
//
//        for (int i = 1; i <= cnt; i++) {
//            boolean perCheck = perCheck(skillId, attackInt, defenseMr);
//
//            if (perCheck) {
//                p++;
//            }
//
//            String s = perCheck ? "O" : "X";
//            s += "\t";
//            System.out.print(s);
//
//            if (i % 10 == 0) {
//                sum += p;
//                System.out.print(p / 10.0);
//                System.out.println();
//                p = 0;
//            }
//        }
//
//        System.out.println(sum);
//    }
//
//    public static boolean perCheck(int skillId, int attackInt, int defenseMr) {
//        if (attackInt >= 38) {
//            attackInt = 38;
//        }
//
//        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
//
//        double per = attackInt / 10.7;
//
//        int probability = Math.toIntExact(Math.round(attackInt * per));
//        probability += RandomUtils.nextInt(skill.getProbabilityValue());
//        probability -= defenseMr;
//
//        if (probability > 90) {
//            probability = 90;
//        }
//
//        int rnd = RandomUtils.nextInt(100);
//
//        return probability >= rnd;
//    }
//
//    public static boolean 명중테스트(int ac, int er) {
//        int hitRate = 62;
//        int bowhitup = 0;
//
//        hitRate += bowhitup;
//
//        int attackerDice = RandomUtils.nextInt(20) + 1 + hitRate - 10;
//        int defenderValue = ac * 2 * -1;
//
//        int defenderDice = RandomUtils.nextInt(defenderValue) - 10;
//
//        boolean cri = false;
//        int fumble = hitRate - 9;
//        int critical = hitRate + 10;
//
//        if (attackerDice <= fumble) {
//            hitRate = 0;
//            cri = true;
//        } else if (attackerDice >= critical) {
//            hitRate = 100;
//        } else {
//            if (attackerDice > defenderDice) {
//                hitRate = 100;
//            } else if (attackerDice <= defenderDice) {
//                hitRate = 0;
//            }
//        }
//
//        return RandomUtils.isWinning(100, hitRate);
//    }
//
//    public static void 명중() {
//        for (int x = 0; x < 10; x++) {
//            int missAvg = 0;
//            int count = 10;
//
//            for (int z = 0; z < count; z++) {
//                int er = 37;
//                int ac = 10;
//                int miss = 0;
//                int attack = 1;
//
//                for (int i = 0; i < attack; i++) {
//                    if (!명중테스트(ac, er)) {
//                        miss += 1;
//                    }
//                }
//
//                String s = miss == 1 ? "O" : "X";
//                s += "\t";
//
//                System.out.print(s);
//
//                missAvg += miss;
//            }
//
//            System.out.print((missAvg / 10.0));
//            System.out.println();
//        }
//    }
//
//    public static void test() {
//        int cnt = 10000;
//        double sum = 0;
//
//        for (int x = 0; x < cnt; x++) {
//            int missAvg = 0;
//            int count = 10;
//
//            for (int z = 0; z < count; z++) {
//                int ac = -110;
//                int miss = 0;
//                int attack = 1;
//
//                for (int i = 0; i < attack; i++) {
//                    if (!닷지테스트(ac)) {
//                        miss += 1;
//                        sum += miss;
//                    }
//                }
//
//                String s = miss == 1 ? "O" : "X";
//                s += "\t";
//                System.out.print(s);
//
//                missAvg += miss;
//            }
//
//            System.out.print((missAvg / 10.0));
//            System.out.println();
//        }
//
//        System.out.println(sum / cnt * 10);
//    }
//
//    public static boolean 닷지테스트(int ac) {
//        int hitRate = 69;
//        int bowhitup = 62;
//
//        hitRate += bowhitup / 1.4;
//
//        int attackerDice = RandomUtils.nextInt(20) + hitRate - 10;
//        int defenderValue = RandomUtils.nextInt(-ac, -ac * 2);
//        int defenderDice = RandomUtils.nextInt(defenderValue) - 10;
//
//        boolean cri = false;
//        int fumble = hitRate - 9;
//        int critical = hitRate + 10;
//
//        if (attackerDice <= fumble) {
//            hitRate = 0;
//            cri = true;
//        } else if (attackerDice >= critical) {
//            hitRate = 100;
//        } else {
//            if (attackerDice > defenderDice) {
//                hitRate = 100;
//            } else if (attackerDice <= defenderDice) {
//                hitRate = 0;
//            }
//        }
//
//        hitRate -= L1AttackUtils.missByUncannyDodge(ac);
//
////        int rnd = random.nextInt(100) + 1;
////
////        if (hitRate > rnd) {
////            int math = RandomUtils.next(90) + 1;
////            return math > er;
////        }
//
//        return RandomUtils.isWinning(100, hitRate);
//    }
//
//    public static void mrTest() {
//        int checkCnt = 80;
//
//        for (int mr = 50; mr <= 250; mr += 1) {
//            double sum = 0;
//
//            for (int i = 0; i < checkCnt; i++) {
//                double dmg = 1500;
//                dmg -= dmg * L1MagicUtils.reduceDamageByMr(mr);
//                sum += dmg;
//            }
//
//            System.out.println("MR : " + mr + " - 평균 : " + sum / checkCnt);
//        }
//    }
//}
