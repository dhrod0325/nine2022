package ks.constants;

public class L1Options {
    public static final String CLIENT_KEY = "CLIENT";

    public static final int 옵션_HP10 = 1;
    public static final int 옵션_HP20 = 2;
    public static final int 옵션_HP30 = 3;

    public static final int 옵션_MP10 = 4;
    public static final int 옵션_MP20 = 5;
    public static final int 옵션_MP30 = 6;

    public static final int 옵션_AC1 = 7;
    public static final int 옵션_AC2 = 8;

    public static final int 옵션_스턴적중1 = 9;
    public static final int 옵션_스턴내성1 = 10;

    public static final int 옵션_추가대미지1 = 11;
    public static final int 옵션_SP1 = 12;
    public static final int 옵션_리덕션1 = 13;
    public static final int 옵션_명중1 = 14;

    public static final int 무기옵션_추가대미지1 = 1;
    public static final int 무기옵션_추가대미지2 = 2;
    public static final int 무기옵션_추가대미지3 = 3;

    public static final int 무기옵션_명중1 = 4;
    public static final int 무기옵션_명중2 = 5;
    public static final int 무기옵션_명중3 = 6;

    public static final int 무기옵션_sp1 = 7;
    public static final int 무기옵션_sp2 = 8;
    public static final int 무기옵션_sp3 = 9;

    public static String optionMsgWeapon(int option) {
        String result = "옵션 : ";

        switch (option) {
            case 무기옵션_추가대미지1:
                result += "추가대미지 +1";
                break;
            case 무기옵션_추가대미지2:
                result += "추가대미지 +2";
                break;
            case 무기옵션_추가대미지3:
                result += "추가대미지 +3";
                break;
            case 무기옵션_명중1:
                result += "명중 +1";
                break;
            case 무기옵션_명중2:
                result += "명중 +2";
                break;
            case 무기옵션_명중3:
                result += "명중 +3";
                break;
            case 무기옵션_sp1:
                result += "SP +1";
                break;
            case 무기옵션_sp2:
                result += "SP +2";
                break;
            case 무기옵션_sp3:
                result += "SP +3";
                break;
        }

        return result;
    }

    public static String optionMsgArmor(int option) {
        String result = "옵션 : ";

        switch (option) {
            case 옵션_HP10:
                result += "HP +10";
                break;
            case 옵션_HP20:
                result += "HP +20";
                break;
            case 옵션_HP30:
                result += "HP +30";
                break;
            case 옵션_MP10:
                result += "MP +10";
                break;
            case 옵션_MP20:
                result += "MP +20";
                break;
            case 옵션_MP30:
                result += "MP +30";
                break;
            case 옵션_AC1:
                result += "AC +1";
                break;
            case 옵션_AC2:
                result += "AC +2";
                break;
            case 옵션_스턴적중1:
                result += "스턴적중 +1";
                break;
            case 옵션_스턴내성1:
                result += "스턴내성 +1";
                break;
            case 옵션_추가대미지1:
                result += "추가대미지 +1";
                break;
            case 옵션_명중1:
                result += "명중 +1";
                break;
            case 옵션_SP1:
                result += "SP +1";
                break;
            case 옵션_리덕션1:
                result += "리덕션 +1";
                break;
        }

        return result;
    }
}
