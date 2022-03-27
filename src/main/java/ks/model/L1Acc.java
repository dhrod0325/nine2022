package ks.model;

import ks.model.instance.L1ItemInstance;

public class L1Acc {
    public static boolean isNormalAcc(L1ItemInstance item) {
        int itemId = item.getItemId();

        if (!item.getItem().isAccessorie()) {
            return false;
        }

        if (L1Ring.is스냅퍼반지(itemId)) {
            return false;
        }

        if (L1EarRing.is룸티스(itemId)) {
            return false;
        }

        return true;
    }

    public static int calcAddHp(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        int result = 0;

        if (item.getItem().getType() == 10) {
            switch (enchantLevel) {
                case 6:
                    result += 20;
                    break;
                case 7:
                    result += 30;
                    break;
                case 8:
                    result += 40;
                    break;
                case 9:
                    result += 50;
                    break;
                case 10:
                    result += 100;
                    break;
            }
        } else {
            switch (enchantLevel) {
                case 1:
                    result += 5;
                    break;
                case 2:
                    result += 10;
                    break;
                case 3:
                    result += 20;
                    break;
                case 4:
                    result += 30;
                    break;
                case 5:
                case 6:
                    result += 40;
                    break;
                case 7:
                case 8:
                    result += 50;
                    break;
                case 9:
                    result += 60;
                    break;
                case 10:
                    result += 70;
                    break;
            }
        }


        return result;
    }

    public static int calcAc(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (!(item.getItem().getType() == 12 && item.getItem().getType() == 8)) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 5:
                result += 1;
                break;
            case 6:
                result += 2;
                break;
            case 7:
                result += 3;
                break;
            case 8:
                result += 4;
                break;
            case 9:
                result += 5;
                break;
            case 10:
                result += 6;
                break;
        }

        return result;
    }

    public static int calcAddDmgUpAndBowDmgUp(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 9) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 5:
                result += 1;
                break;
            case 6:
                result += 2;
                break;
            case 7:
                result += 3;
                break;
            case 8:
                result += 4;
                break;
            case 9:
                result += 5;
                break;
            case 10:
                result += 6;
                break;
        }

        return result;
    }


    public static int calcPvpDamage(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 9) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 6:
                result += 1;
                break;
            case 7:
                result += 2;
                break;
            case 8:
                result += 3;
                break;
            case 9:
                result += 5;
                break;
            case 10:
                result += 7;
                break;
        }

        return result;
    }

    public static int calcAddSp(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 9) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 7:
                result += 1;
                break;
            case 8:
                result += 2;
                break;
            case 9:
                result += 3;
                break;
            case 10:
                result += 4;
                break;
        }
        return result;
    }

    public static int calcMr(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 9) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 6:
                result += 1;
                break;
            case 7:
                result += 3;
                break;
            case 8:
                result += 5;
                break;
            case 9:
                result += 7;
                break;
            case 10:
                result += 9;
                break;
        }

        return result;
    }

    public static int calcRegistStun(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (!(item.getItem().getType() == 12 && item.getItem().getType() == 8)) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 7:
                result += 2;
                break;
            case 8:
                result += 3;
                break;
            case 9:
                result += 4;
                break;
            case 10:
                result += 5;
                break;
        }

        return result;
    }

    public static int calcAddReduction(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 10) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 5:
                result += 1;
                break;
            case 6:
                result += 2;
                break;
            case 7:
                result += 3;
                break;
            case 8:
                result += 4;
                break;
            case 9:
                result += 5;
                break;
            case 10:
                result += 6;
                break;
        }

        return result;
    }

    public static int calcAddMp(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 10) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 1:
                result += 5;
                break;
            case 2:
                result += 10;
                break;
            case 3:
                result += 20;
                break;
            case 4:
                result += 30;
                break;
            case 5:
            case 6:
                result += 40;
                break;
            case 7:
            case 8:
                result += 50;
                break;
            case 9:
                result += 60;
                break;
            case 10:
                result += 70;
                break;
        }

        return result;
    }

    public static int calcPvpReduction(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 10) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 6:
                result += 1;
                break;
            case 7:
                result += 3;
                break;
            case 8:
                result += 5;
                break;
            case 9:
                result += 7;
                break;
            case 10:
                result += 9;
                break;
        }

        return result;
    }

    public static double calcPotionPer(L1ItemInstance item, int enchantLevel) {
        if (!isNormalAcc(item)) {
            return 0;
        }

        if (item.getItem().getType() != 12) {
            return 0;
        }

        int result = 0;

        switch (enchantLevel) {
            case 5:
                result += 2;
                break;
            case 6:
                result += 4;
                break;
            case 7:
                result += 6;
                break;
            case 8:
                result += 8;
                break;
            case 9:
                result += 10;
                break;
            case 10:
                result += 12;
                break;
        }

        return result;
    }
}
