package ks.model;

import ks.constants.L1ItemId;

public class L1EarRing {
    public static int calcAddMagicHitup(int itemId, int enchantLevel, int bless) {
        int result = 0;
        if (itemId == L1ItemId.룸티스보랏빛귀걸이 || itemId == L1ItemId.축룸티스보랏빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 6:
                        result += 1;
                        break;
                    case 7:
                        result += 3;
                        break;
                    case 8:
                        result += 7;
                        break;
                    case 9:
                        result += 12;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 7:
                        result += 1;
                        break;
                    case 8:
                        result += 5;
                        break;
                    case 9:
                        result += 9;
                        break;
                }
            }
        }
        return result;
    }

    public static int calcAc(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스검은빛귀걸이 || itemId == L1ItemId.축룸티스검은빛귀걸이) {
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
                        result += 7;
                        break;
                    case 6:
                        result += 8;
                        break;
                    case 7:
                        result += 9;
                        break;
                    case 8:
                        result += 10;
                        break;
                    case 9:
                        result += 11;
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
                        result += 5;
                        break;
                    case 5:
                        result += 6;
                        break;
                    case 6:
                        result += 7;
                        break;
                    case 7:
                        result += 8;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 10;
                        break;
                }
            }
        } else if (itemId == L1ItemId.룸티스보랏빛귀걸이 || itemId == L1ItemId.축룸티스보랏빛귀걸이) {
            if (isBless(bless)) {
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
                        result += 4;
                        break;
                }
            } else if (isNormal(bless)) {
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
                        result += 3;
                        break;
                }
            }
        } else if (itemId == L1ItemId.룸티스붉은빛귀걸이 || itemId == L1ItemId.축룸티스붉은빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
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
                        result += 10;
                        break;
                    case 9:
                        result += 11;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 6:
                        result += 7;
                        break;
                    case 7:
                        result += 8;
                        break;
                    case 8:
                        result += 9;
                        break;
                    case 9:
                        result += 10;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcAddDmgUp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스검은빛귀걸이 || itemId == L1ItemId.축룸티스검은빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 1;
                        break;
                    case 4:
                        result += 2;
                        break;
                    case 5:
                        result += 3;
                        break;
                    case 6:
                        result += 4;
                        break;
                    case 7:
                        result += 5;
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
                    case 3:
                        result += 1;
                        break;
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
            }
        }

        return result;
    }

    public static int calcAddHitUp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스붉은빛귀걸이 || itemId == L1ItemId.축룸티스붉은빛귀걸이) {
            if (isBless(bless)) {
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

    public static int calcAddHp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스붉은빛귀걸이 || itemId == L1ItemId.축룸티스붉은빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 10;
                        break;
                    case 1:
                        result += 30;
                        break;
                    case 2:
                        result += 40;
                        break;
                    case 3:
                        result += 60;
                        break;
                    case 4:
                        result += 70;
                        break;
                    case 5:
                        result += 80;
                        break;
                    case 6:
                        result += 90;
                        break;
                    case 7:
                        result += 100;
                        break;
                    case 8:
                        result += 150;
                        break;
                    case 9:
                        result += 160;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 10;
                        break;
                    case 1:
                        result += 30;
                        break;
                    case 2:
                        result += 40;
                        break;
                    case 3:
                        result += 50;
                        break;
                    case 4:
                        result += 60;
                        break;
                    case 5:
                        result += 70;
                        break;
                    case 6:
                        result += 80;
                        break;
                    case 7:
                        result += 90;
                        break;
                    case 8:
                        result += 100;
                        break;
                    case 9:
                        result += 110;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcAddSp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스보랏빛귀걸이 || itemId == L1ItemId.축룸티스보랏빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 1;
                        break;
                    case 4:
                        result += 2;
                        break;
                    case 5:
                        result += 3;
                        break;
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
                        result += 11;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 1;
                        break;
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

    public static int calcAddMp(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스보랏빛귀걸이 || itemId == L1ItemId.축룸티스보랏빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 5;
                        break;
                    case 1:
                        result += 15;
                        break;
                    case 2:
                        result += 20;
                        break;
                    case 3:
                        result += 40;
                        break;
                    case 4:
                        result += 55;
                        break;
                    case 5:
                        result += 60;
                        break;
                    case 6:
                        result += 75;
                        break;
                    case 7:
                        result += 100;
                        break;
                    case 8:
                        result += 130;
                        break;
                    case 9:
                        result += 160;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 5;
                        break;
                    case 1:
                        result += 15;
                        break;
                    case 2:
                        result += 20;
                        break;
                    case 3:
                        result += 35;
                        break;
                    case 4:
                        result += 40;
                        break;
                    case 5:
                        result += 55;
                        break;
                    case 6:
                        result += 60;
                        break;
                    case 7:
                        result += 75;
                        break;
                    case 8:
                        result += 100;
                        break;
                    case 9:
                        result += 130;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcMpr(int itemId, int enchantLevel, int bless) {
        int result = 0;

        return result;
    }

    public static int calcHpr(int itemId, int enchantLevel, int bless) {
        int result = 0;

        return result;
    }

    public static int calcMr(int itemId, int enchantLevel, int bless) {
        int result = 0;

        if (itemId == L1ItemId.룸티스보랏빛귀걸이 || itemId == L1ItemId.축룸티스보랏빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 2;
                        break;
                    case 1:
                        result += 5;
                        break;
                    case 2:
                        result += 6;
                        break;
                    case 3:
                        result += 8;
                        break;
                    case 4:
                        result += 9;
                        break;
                    case 5:
                        result += 10;
                        break;
                    case 6:
                        result += 12;
                        break;
                    case 7:
                        result += 15;
                        break;
                    case 8:
                        result += 20;
                        break;
                    case 9:
                        result += 23;
                        break;
                }
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 0:
                        result += 2;
                        break;
                    case 1:
                        result += 5;
                        break;
                    case 2:
                        result += 6;
                        break;
                    case 3:
                        result += 7;
                        break;
                    case 4:
                        result += 8;
                        break;
                    case 5:
                        result += 9;
                        break;
                    case 6:
                        result += 10;
                        break;
                    case 7:
                        result += 12;
                        break;
                    case 8:
                        result += 17;
                        break;
                    case 9:
                        result += 20;
                        break;
                }
            }
        }

        return result;
    }

    public static int calcAddReduction(int itemId, int enchantLevel, int bless) {
        int result = 0;
        if (itemId == L1ItemId.룸티스붉은빛귀걸이 || itemId == L1ItemId.축룸티스붉은빛귀걸이) {
            if (isBless(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 1;
                        break;
                    case 4:
                        result += 2;
                        break;
                    case 5:
                        result += 3;
                        break;
                    case 6:
                        result += 4;
                        break;
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
            } else if (isNormal(bless)) {
                switch (enchantLevel) {
                    case 3:
                        result += 1;
                        break;
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
                        result += 7;
                        break;
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


    public static boolean is룸티스(int itemId) {
        return itemId == L1ItemId.룸티스보랏빛귀걸이
                || itemId == L1ItemId.축룸티스보랏빛귀걸이
                || itemId == L1ItemId.룸티스붉은빛귀걸이
                || itemId == L1ItemId.축룸티스붉은빛귀걸이
                || itemId == L1ItemId.룸티스검은빛귀걸이
                || itemId == L1ItemId.축룸티스검은빛귀걸이
                || itemId == L1ItemId.룸티스푸른빛귀걸이
                || itemId == L1ItemId.축룸티스푸른빛귀걸이;
    }
}
