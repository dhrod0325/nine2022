//package ks.test;
//
//import ks.model.L1BringStonePer;
//import ks.util.L1CommonUtils;
//import ks.util.common.RandomUtils;
//import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
//
//public class 브링스톤테스트 {
//    public static void main(String[] args) {
////        test(67, 300);
//    }
//
//    public static void test(int level, int 흑마석숫자) {
//        int wis = level - 32;
//
//        L1BringStonePer k = L1CommonUtils.calcBringStonePer(level, wis);
//
//        int darkCount = 흑마석숫자;
//
//        int 흑요석 = 0;
//
//        for (int i = 0; i < darkCount; i++) {
//            if (RandomUtils.isWinning(100, k.getDark())) {
//                흑요석++;
//            }
//        }
//
//        int 강암석 = 0;
//
//        for (int i = 0; i < 흑요석; i++) {
//            if (RandomUtils.isWinning(100, k.getBrave())) {
//                강암석++;
//            }
//        }
//
//        int 현암석 = 0;
//
//        for (int i = 0; i < 강암석; i++) {
//            if (RandomUtils.isWinning(100, k.getWise())) {
//                현암석++;
//            }
//        }
//
//        int 암황석 = 0;
//
//        for (int i = 0; i < 현암석; i++) {
//            if (RandomUtils.isWinning(100, k.getKayser())) {
//                암황석++;
//            }
//        }
//
//        System.out.println("레벨 : " + level);
//        System.out.println("위즈 : " + wis);
//        System.out.println("흑요석 : " + 흑요석);
//        System.out.println("강암석 : " + 강암석);
//        System.out.println("현암석 : " + 현암석);
//        System.out.println("암황석 : " + 암황석);
//
//        System.out.println(ReflectionToStringBuilder.toString(k));
//    }
//
//}
