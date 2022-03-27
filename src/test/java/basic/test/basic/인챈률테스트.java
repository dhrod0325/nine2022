//package ks.test;
//
//import ks.model.L1CalcStat;
//import ks.util.L1CommonUtils;
//
//public class 인챈률테스트 extends BaseTest {
//    public static void main(String[] args) {
////        int safe = 0;
////        for (int enchant = safe; enchant <= 11; enchant++) {
////            int chance = L1CommonUtils.calcEnchantPer(safe, enchant, false, 210, 5);
////            System.out.println("안전 : " + safe + ", 인챈트 : " + enchant + ", 인챈률 : " + chance);
////        }
//    }
//
//    public static void checkHp(int type) {
//        int con25 = 0;
//        int con23 = 0;
//        int con21 = 0;
//        int con18 = 0;
//        int con16 = 0;
//        int con14 = 0;
//
//        con25 += _checkHp(type, 25);
//        con23 += _checkHp(type, 23);
//        con21 += _checkHp(type, 21);
//        con18 += _checkHp(type, 18);
//        con16 += _checkHp(type, 16);
//        con14 += _checkHp(type, 14);
//
//        System.out.println("" + con25);
//        System.out.println("" + con23);
//        System.out.println("" + con21);
//        System.out.println("" + con18);
//        System.out.println("" + con16);
//        System.out.println("" + con14);
//    }
//
//
//    public static int _checkHp(int type, int con) {
//        int result = 0;
//
//        for (int i = 1; i <= 60; i++) {
//            int hp = L1CalcStat.calcStatHp(type, 16, (byte) con);
//            result += hp;
//        }
//
//        return result;
//    }
//
//    public static int _checkMp(int type, int wis) {
//        int result = 0;
//
//        for (int i = 1; i <= 70; i++) {
//            int mp = L1CalcStat.calcStatMp(type, 16, (byte) wis);
//            result += mp;
//        }
//
//        return result;
//    }
//
//    public static void checkMp(int type) {
//        int con25 = 0;
//        int con23 = 0;
//        int con21 = 0;
//        int con18 = 0;
//        int con16 = 0;
//        int con14 = 0;
//
//        con25 += _checkMp(type, 25);
//        con23 += _checkMp(type, 23);
//        con21 += _checkMp(type, 21);
//        con18 += _checkMp(type, 18);
//        con14 += _checkMp(type, 12);
//
//        System.out.println("" + con25);
//        System.out.println("" + con23);
//        System.out.println("" + con21);
//        System.out.println("" + con18);
//        System.out.println("" + con14);
//    }
//
//    public static int calcChance(int enchantLevel, int safeEnchant) {
//        int enchantLevelTmp;
//
//        if (safeEnchant == 0) {
//            enchantLevelTmp = 2;
//        } else {
//            enchantLevelTmp = 1;
//        }
//
//        int calcChance = 80 / ((enchantLevel - safeEnchant + 1) * 2) / (enchantLevel / 8 != 0 ? 2 : 1);
//        int enchantChanceWeapon = calcChance / enchantLevelTmp + 5;
//
//        if (enchantLevel >= 9) {
//            enchantChanceWeapon = 2;
//        }
//
//
//        return enchantChanceWeapon;
//    }
//}
