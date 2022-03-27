package ks.model;

import ks.constants.L1ItemId;

@SuppressWarnings("ALL")
public class L1Ring {
    public static int calcAc(int itemId, int enchantLevel, int bless) {
        int result = 0;
        if (itemId == L1ItemId.스냅퍼의지혜반지 || itemId == L1ItemId.축스냅퍼의지혜반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                        result += 2;
                        break;
                    case 2:
                        result += 3;
                        break;
                    case 3:
                        result += 4;
                        break;
                    case 4:
                    case 5:
                    case 6:
                        result += 5;
                        break;
                    case 7:
                        result += 7;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 12;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                        result += 1;
                        break;
                    case 2:
                        result += 2;
                        break;
                    case 3:
                        result += 3;
                        break;
                    case 4:
                    case 5:
                        result += 4;
                        break;
                    case 6:
                    case 7:
                        result += 5;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            }
        } else if (itemId == L1ItemId.스냅퍼의마법저항반지 || itemId == L1ItemId.축스냅퍼의마법저항반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                        result += 2;
                        break;
                    case 2:
                        result += 3;
                        break;
                    case 3:
                        result += 4;
                        break;
                    case 4:
                        result += 5;
                        break;
                    case 5:
                        result += 6;
                        break;
                    case 6:
                        result += 6;
                        break;
                    case 7:
                        result += 7;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 12;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                        result += 1;
                        break;
                    case 2:
                        result += 2;
                        break;
                    case 3:
                        result += 3;
                        break;
                    case 4:
                    case 5:
                        result += 4;
                        break;
                    case 6:
                    case 7:
                        result += 5;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            }
        } else if (itemId == L1ItemId.스냅퍼의용사반지 || itemId == L1ItemId.축스냅퍼의용사반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 2;
                        break;
                    case 1:
                        result += 3;
                        break;
                    case 2:
                        result += 4;
                        break;
                    case 3:
                        result += 5;
                        break;
                    case 4:
                        result += 6;
                        break;
                    case 5:
                        result += 6;
                        break;
                    case 6:
                        result += 6;
                        break;
                    case 7:
                        result += 7;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 12;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 1;
                        break;
                    case 1:
                        result += 2;
                        break;
                    case 2:
                        result += 3;
                        break;
                    case 3:
                        result += 4;
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        result += 5;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            }
        } else if (itemId == L1ItemId.스냅퍼의체력반지 || itemId == L1ItemId.축스냅퍼의체력반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                        result += 2;
                        break;
                    case 2:
                        result += 3;
                        break;
                    case 3:
                        result += 4;
                        break;
                    case 4:
                        result += 5;
                        break;
                    case 5:
                        result += 6;
                        break;
                    case 6:
                        result += 6;
                        break;
                    case 7:
                        result += 7;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 12;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                        result += 1;
                        break;
                    case 2:
                        result += 2;
                        break;
                    case 3:
                        result += 3;
                        break;
                    case 4:
                    case 5:
                        result += 4;
                        break;
                    case 6:
                    case 7:
                        result += 5;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcAddDmgUp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (is스냅퍼반지(itemId)) {
            if (itemId != L1ItemId.스냅퍼의지혜반지 && itemId != L1ItemId.축스냅퍼의지혜반지) {
                if (isBless(bless)) {
                    switch (enchantLevel) {
                        case 4:
                            result += 1;
                            break;
                        case 5:
                            result += 2;
                            break;
                        case 6:
                            result += 3;
                            break;
                        case 7:
                            result += 4;
                            break;
                        case 8:
                            result += 7;
                            break;
                        case 9:
                            result += 9;
                            break;
                    }
                } else if (isNormal(bless)) {
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
                            result += 5;
                            break;
                        case 9:
                            result += 7;
                            break;
                    }
                }
            }
        }

        return result;
    }

    private static boolean isNormal(int bless) {
        return bless == 1;
    }

    public static boolean isBless(int bless) {
        return bless == 0;
    }

    public static boolean is스냅퍼반지(int itemId) {
        return itemId == L1ItemId.스냅퍼의용사반지
                || itemId == L1ItemId.스냅퍼의지혜반지
                || itemId == L1ItemId.스냅퍼의마법저항반지
                || itemId == L1ItemId.스냅퍼의마나반지
                || itemId == L1ItemId.스냅퍼의체력반지
                || itemId == L1ItemId.스냅퍼의집중반지
                || itemId == L1ItemId.스냅퍼의회복반지
                || itemId == L1ItemId.축스냅퍼의용사반지
                || itemId == L1ItemId.축스냅퍼의지혜반지
                || itemId == L1ItemId.축스냅퍼의마법저항반지
                || itemId == L1ItemId.축스냅퍼의마나반지
                || itemId == L1ItemId.축스냅퍼의체력반지
                || itemId == L1ItemId.축스냅퍼의집중반지
                || itemId == L1ItemId.축스냅퍼의회복반지;
    }

    public static int calcAddHitUp(int itemId, int enchantLevel, int bless) {
        int result = 0;
        if (itemId == L1ItemId.스냅퍼의용사반지 || itemId == L1ItemId.축스냅퍼의용사반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 4:
                        result += 1;
                        break;
                    case 5:
                        result += 2;
                        break;
                    case 6:
                        result += 3;
                        break;
                    case 7:
                        result += 4;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            } else if (isNormal(bless)) {
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
                        result += 5;
                        break;
                    case 9:
                        result += 7;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcAddHp(int itemId, int enchantLevel, int bless) {
        int result = 0;
        if (itemId == L1ItemId.스냅퍼의지혜반지 || itemId == L1ItemId.축스냅퍼의지혜반지) {
            if (isBless(bless)) {
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
                        result += 25;
                        break;
                    case 5:
                        result += 30;
                        break;
                    case 6:
                        result += 35;
                        break;
                    case 7:
                        result += 40;
                        break;
                    case 8:
                        result += 50;
                        break;
                    case 9:
                        result += 60;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 1:
                        result += 5;
                        break;
                    case 2:
                        result += 10;
                        break;
                    case 3:
                        result += 15;
                        break;
                    case 4:
                        result += 20;
                        break;
                    case 5:
                        result += 25;
                        break;
                    case 6:
                        result += 30;
                        break;
                    case 7:
                        result += 35;
                        break;
                    case 8:
                        result += 40;
                        break;
                    case 9:
                        result += 50;
                        break;
                }
            }
        } else if (itemId == L1ItemId.스냅퍼의마법저항반지 || itemId == L1ItemId.축스냅퍼의마법저항반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 1:
                        result += 15;
                        break;
                    case 2:
                        result += 20;
                        break;
                    case 3:
                        result += 30;
                        break;
                    case 4:
                        result += 35;
                        break;
                    case 5:
                        result += 40;
                        break;
                    case 6:
                        result += 45;
                        break;
                    case 7:
                    case 8:
                        result += 50;
                        break;
                    case 9:
                        result += 60;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 1:
                        result += 15;
                        break;
                    case 2:
                        result += 20;
                        break;
                    case 3:
                        result += 25;
                        break;
                    case 4:
                        result += 30;
                        break;
                    case 5:
                        result += 35;
                        break;
                    case 6:
                        result += 40;
                        break;
                    case 7:
                        result += 45;
                        break;
                    case 8:
                        result += 50;
                        break;
                    case 9:
                        result += 60;
                        break;
                }
            }
        } else if (itemId == L1ItemId.스냅퍼의용사반지 || itemId == L1ItemId.축스냅퍼의용사반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 10;
                        break;
                    case 4:
                        result += 15;
                        break;
                    case 5:
                        result += 20;
                        break;
                    case 6:
                        result += 25;
                        break;
                    case 7:
                    case 8:
                        result += 30;
                        break;
                    case 9:
                        result += 40;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 5;
                        break;
                    case 4:
                        result += 10;
                        break;
                    case 5:
                        result += 15;
                        break;
                    case 6:
                        result += 20;
                        break;
                    case 7:
                        result += 25;
                        break;
                    case 8:
                        result += 30;
                        break;
                    case 9:
                        result += 40;
                        break;
                }
            }
        } else if (itemId == L1ItemId.스냅퍼의체력반지 || itemId == L1ItemId.축스냅퍼의체력반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 50;
                        break;
                    case 1:
                        result += 65;
                        break;
                    case 2:
                        result += 70;
                        break;
                    case 3:
                        result += 80;
                        break;
                    case 4:
                        result += 85;
                        break;
                    case 5:
                        result += 90;
                        break;
                    case 6:
                        result += 95;
                        break;
                    case 7:
                        result += 105;
                        break;
                    case 8:
                        result += 115;
                        break;
                    case 9:
                        result += 125;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 50;
                        break;
                    case 1:
                        result += 65;
                        break;
                    case 2:
                        result += 70;
                        break;
                    case 3:
                        result += 75;
                        break;
                    case 4:
                        result += 80;
                        break;
                    case 5:
                        result += 85;
                        break;
                    case 6:
                        result += 90;
                        break;
                    case 7:
                        result += 95;
                        break;
                    case 8:
                        result += 100;
                        break;
                    case 9:
                        result += 105;
                        break;
                }
            }
        }
        return result;
    }

    public static int calcAddSp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.스냅퍼의지혜반지 || itemId == L1ItemId.축스냅퍼의지혜반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 4:
                        result += 1;
                        break;
                    case 5:
                        result += 2;
                        break;
                    case 6:
                        result += 3;
                        break;
                    case 7:
                        result += 4;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            } else if (isNormal(bless)) {
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
                        result += 5;
                        break;
                    case 9:
                        result += 7;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcAddMp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.스냅퍼의지혜반지 || itemId == L1ItemId.축스냅퍼의지혜반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        result += 15;
                        break;
                    case 7:
                        result += 30;
                        break;
                    case 8:
                        result += 35;
                        break;
                    case 9:
                        result += 80;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        result += 15;
                        break;
                    case 8:
                        result += 30;
                        break;
                    case 9:
                        result += 70;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcMr(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.스냅퍼의마법저항반지 || itemId == L1ItemId.축스냅퍼의마법저항반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        result += 7;
                        break;
                    case 6:
                        result += 8;
                        break;
                    case 7:
                        result += 9;
                        break;
                    case 8:
                        result += 12;
                        break;
                    case 9:
                        result += 15;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        result += 7;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 12;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcMpr(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.스냅퍼의지혜반지 || itemId == L1ItemId.축스냅퍼의지혜반지) {
            result += 1;
        }

        return result;
    }

    public static int calcHpr(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.스냅퍼의용사반지 || itemId == L1ItemId.축스냅퍼의용사반지) {
            result += 2;
        }

        return result;
    }

    public static int calcRegistStun(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (is스냅퍼반지(itemId)) {
            switch (enchantLevel) {
                case 6:
                    result += 5;
                    break;
                case 7:
                    result += 7;
                    break;
                case 8:
                    result += 9;
                    break;
                case 9:
                    result += 9;
                    break;
            }
        }

        return result;
    }

    public static int calcPvpDamage(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (is스냅퍼반지(itemId)) {
            switch (enchantLevel) {
                case 7:
                    result += 1;
                    break;
                case 8:
                    result += 3;
                    break;
                case 9:
                    result += 7;
                    break;
            }
        }

        return result;
    }

    public static int calcMagicHitUp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.스냅퍼의지혜반지 || itemId == L1ItemId.축스냅퍼의지혜반지) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 7:
                        result += 2;
                        break;
                    case 8:
                        result += 5;
                        break;
                    case 9:
                        result += 7;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 7:
                        result += 1;
                        break;
                    case 8:
                        result += 3;
                        break;
                    case 9:
                        result += 5;
                        break;
                }
            }
        }

        return result;
    }
}
