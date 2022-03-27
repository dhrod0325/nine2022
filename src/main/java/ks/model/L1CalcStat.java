package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1Types;
import ks.core.datatables.characterStat.CharacterStatTable;
import ks.model.pc.L1PcInstance;
import ks.util.common.random.RandomUtils;

public class L1CalcStat {
    private final static int RANDOM_HP_PLUS = 1;
    private final static int RANDOM_MP_PLUS = 1;

    public static short calcStatHp(int charType, int baseMaxHp, byte baseCon) {
        short resultHp = 0;
        int addHp;
        int calCon;

        int randomHpPlus = RandomUtils.nextInt(0, RANDOM_HP_PLUS);

        if (charType == L1Types.TYPE_PRINCE) { // 군주
            calCon = 10;

            switch (baseCon - calCon) {
                case 0://10
                case 1://11
                    addHp = randomHpPlus + 11;
                    break;
                case 2://12
                case 3://13
                    addHp = randomHpPlus + 12;
                    break;
                case 4://14
                case 5://15
                    addHp = randomHpPlus + 13;
                    break;
                case 6://16
                    addHp = randomHpPlus + 15;
                    break;
                case 7://17
                    addHp = randomHpPlus + 16;
                    break;
                case 8://18
                    addHp = randomHpPlus + 17;
                    break;
                case 9://19
                    addHp = randomHpPlus + 18;
                    break;
                case 10://20
                    addHp = randomHpPlus + 19;
                    break;
                case 11://21
                    addHp = randomHpPlus + 20;
                    break;
                case 12://22
                    addHp = randomHpPlus + 21;
                    break;
                case 13://23
                    addHp = randomHpPlus + 22;
                    break;
                case 14://24
                    addHp = randomHpPlus + 23;
                    break;
                default://25
                    addHp = randomHpPlus + 24;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_PRINCE) {
                resultHp = (short) (CodeConfig.MAX_HP_PRINCE - baseMaxHp);
            }
        } else if (charType == L1Types.TYPE_KNIGHT) { // 기사
            calCon = 14;

            switch (baseCon - calCon) {
                case 0://14
                    addHp = randomHpPlus + 17;
                    break;
                case 1://15
                    addHp = randomHpPlus + 18;
                    break;
                case 2://16
                    addHp = randomHpPlus + 19;
                    break;
                case 3://17
                    addHp = randomHpPlus + 22;
                    break;
                case 4://18
                    addHp = randomHpPlus + 23;
                    break;
                case 5://19
                    addHp = randomHpPlus + 24;
                    break;
                case 6://20
                    addHp = randomHpPlus + 25;
                    break;
                case 7://21
                    addHp = randomHpPlus + 26;
                    break;
                case 8://22
                    addHp = randomHpPlus + 27;
                    break;
                case 9://23
                    addHp = randomHpPlus + 28;
                    break;
                case 10://24
                    addHp = randomHpPlus + 29;
                    break;
                default://25
                    addHp = randomHpPlus + 30;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_KNIGHT) {
                resultHp = (short) (CodeConfig.MAX_HP_KNIGHT - baseMaxHp);
            }
        } else if (charType == L1Types.TYPE_ELF) { // 요정
            calCon = 12;

            switch (baseCon - calCon) {
                case 0://12
                    addHp = randomHpPlus + 10;
                    break;
                case 1://13
                case 2://14
                case 3://15
                    addHp = randomHpPlus + 11;
                    break;
                case 4://16
                    addHp = randomHpPlus + 12;
                    break;
                case 5://17
                    addHp = randomHpPlus + 13;
                    break;
                case 6://18
                    addHp = randomHpPlus + 15;
                    break;
                case 7://19
                    addHp = randomHpPlus + 16;
                    break;
                case 8://20
                    addHp = randomHpPlus + 17;
                    break;
                case 9://21
                    addHp = randomHpPlus + 18;
                    break;
                case 10://22
                    addHp = randomHpPlus + 19;
                    break;
                case 11://23
                    addHp = randomHpPlus + 20;
                    break;
                case 12://24
                    addHp = randomHpPlus + 21;
                    break;
                default://25
                    addHp = randomHpPlus + 22;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_ELF) {
                resultHp = (short) (CodeConfig.MAX_HP_ELF - baseMaxHp);
            }
        } else if (charType == L1Types.TYPE_WIZARD) { // 마법사
            calCon = 12;

            switch (baseCon - calCon) {
                case 0://12
                case 1://13
                    addHp = randomHpPlus + 7;
                    break;
                case 2://14
                case 3://15
                    addHp = randomHpPlus + 8;
                    break;
                case 4://16
                    addHp = randomHpPlus + 10;
                    break;
                case 5://17
                    addHp = randomHpPlus + 11;
                    break;
                case 6://18
                    addHp = randomHpPlus + 12;
                    break;
                case 7://19
                    addHp = randomHpPlus + 13;
                    break;
                case 8://20
                    addHp = randomHpPlus + 14;
                    break;
                case 9://21
                    addHp = randomHpPlus + 15;
                    break;
                case 10://22
                    addHp = randomHpPlus + 16;
                    break;
                case 11://23
                    addHp = randomHpPlus + 17;
                    break;
                case 12://24
                    addHp = randomHpPlus + 18;
                    break;
                default://25
                    addHp = randomHpPlus + 19;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_WIZARD) {
                resultHp = (short) (CodeConfig.MAX_HP_WIZARD - baseMaxHp);
            }
        } else if (charType == L1Types.TYPE_DARK_ELF) { // 다크엘프
            calCon = 8;

            switch (baseCon - calCon) {
                case 0://8
                case 1://9
                    addHp = randomHpPlus + 10;
                    break;
                case 2://10
                case 3://11
                    addHp = randomHpPlus + 11;
                    break;
                case 4://12
                case 5://13
                case 6://14
                case 7://15
                    addHp = randomHpPlus + 12;
                    break;
                case 8://16
                    addHp = randomHpPlus + 13;
                    break;
                case 9://17
                    addHp = randomHpPlus + 14;
                    break;
                case 10://18
                    addHp = randomHpPlus + 15;
                    break;
                case 11://19
                    addHp = randomHpPlus + 16;
                    break;
                case 12://20
                    addHp = randomHpPlus + 17;
                    break;
                case 13://21
                    addHp = randomHpPlus + 18;
                    break;
                case 14://22
                    addHp = randomHpPlus + 19;
                    break;
                case 15://23
                    addHp = randomHpPlus + 20;
                    break;
                case 16://24
                    addHp = randomHpPlus + 21;
                    break;
                default://25
                    addHp = randomHpPlus + 22;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_DARKELF) {
                resultHp = (short) (CodeConfig.MAX_HP_DARKELF - baseMaxHp);
            }
        } else if (charType == L1Types.TYPE_DRAGON_KNIGHT) {
            calCon = 14;

            switch (baseCon - calCon) {
                case 0://14
                    addHp = randomHpPlus + 13;
                    break;
                case 1://15
                    addHp = randomHpPlus + 14;
                    break;
                case 2://16
                    addHp = randomHpPlus + 15;
                    break;
                case 3://17
                    addHp = randomHpPlus + 18;
                    break;
                case 4://18
                    addHp = randomHpPlus + 19;
                    break;
                case 5://19
                    addHp = randomHpPlus + 20;
                    break;
                case 6://20
                    addHp = randomHpPlus + 21;
                    break;
                case 7://21
                    addHp = randomHpPlus + 22;
                    break;
                case 8://22
                    addHp = randomHpPlus + 23;
                    break;
                case 9://23
                    addHp = randomHpPlus + 24;
                    break;
                case 10://24
                    addHp = randomHpPlus + 25;
                    break;
                default://25
                    addHp = randomHpPlus + 26;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_DRAGON_KNIGHT) {
                resultHp = (short) (CodeConfig.MAX_HP_DRAGON_KNIGHT - baseMaxHp);
            }
        } else if (charType == L1Types.TYPE_ILLUSIONIST) {
            calCon = 12;

            switch (baseCon - calCon) {
                case 0://12
                    addHp = randomHpPlus + 9;
                    break;
                case 1://13
                case 2://14
                    addHp = randomHpPlus + 10;
                    break;
                case 3://15
                    addHp = randomHpPlus + 11;
                    break;
                case 4://16
                    addHp = randomHpPlus + 12;
                    break;
                case 5://17
                    addHp = randomHpPlus + 13;
                    break;
                case 6://18
                    addHp = randomHpPlus + 14;
                    break;
                case 7://19
                    addHp = randomHpPlus + 15;
                    break;
                case 8://20
                    addHp = randomHpPlus + 16;
                    break;
                case 9://21
                    addHp = randomHpPlus + 17;
                    break;
                case 10://22
                    addHp = randomHpPlus + 18;
                    break;
                case 11://23
                    addHp = randomHpPlus + 19;
                    break;
                case 12://24
                    addHp = randomHpPlus + 20;
                    break;
                default://25
                    addHp = randomHpPlus + 21;
                    break;
            }

            resultHp += addHp;

            if (baseMaxHp + resultHp > CodeConfig.MAX_HP_ILLUSIONIST) {
                resultHp = (short) (CodeConfig.MAX_HP_ILLUSIONIST - baseMaxHp);
            }
        }

        if (resultHp < 0) {
            resultHp = 0;
        }

        return resultHp;

    }

    public static short calcStatMp(int charType, int baseMaxMp, byte baseWis) {
        int addMp;
        int resultMp = 1;

        int randomMpPlus = RandomUtils.nextInt(0, RANDOM_MP_PLUS);

        if (charType == L1Types.TYPE_PRINCE) { // 프린스
            int calWis = 11;

            switch (baseWis - calWis) {
                case 0://11
                    addMp = randomMpPlus + 2;
                    break;
                case 1://12
                case 2://13
                case 3://14
                case 4://15
                    addMp = randomMpPlus + 3;
                    break;
                default:
                    addMp = randomMpPlus + 4;
                    break;
            }

            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_PRINCE) {
                resultMp = CodeConfig.MAX_MP_PRINCE - baseMaxMp;
            }
        } else if (charType == L1Types.TYPE_KNIGHT) {
            int calWis = 9;

            switch (baseWis - calWis) {
                case 0://9
                case 1://10
                case 2://11
                    addMp = randomMpPlus + 1;
                    break;
                default:
                    addMp = randomMpPlus + 2;
                    break;
            }

            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_KNIGHT) {
                resultMp = CodeConfig.MAX_MP_KNIGHT - baseMaxMp;
            }
        } else if (charType == L1Types.TYPE_ELF) { // 에르프
            int calWis = 12;

            switch (baseWis - calWis) {
                case 0://12
                case 1://13
                    addMp = randomMpPlus + 4;
                    break;
                case 2://14
                    addMp = randomMpPlus + 5;
                    break;
                case 3://15
                case 4://16
                    addMp = randomMpPlus + 6;
                    break;
                case 5://17
                    addMp = randomMpPlus + 7;
                    break;
                case 6://18
                case 7://19
                case 8://20
                    addMp = randomMpPlus + 8;
                    break;
                case 9://21
                case 10://22
                case 11://23
                    addMp = randomMpPlus + 10;
                    break;
                default://25
                    addMp = randomMpPlus + 11;
                    break;
            }

            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_ELF) {
                resultMp = CodeConfig.MAX_MP_ELF - baseMaxMp;
            }
        } else if (charType == L1Types.TYPE_WIZARD) { // 위저드
            int calWis = 12;
            switch (baseWis - calWis) {
                case 0://12
                    addMp = randomMpPlus + 6;
                    break;
                case 1://13
                case 2://14
                    addMp = randomMpPlus + 7;
                    break;
                case 3://15
                case 4://16
                    addMp = randomMpPlus + 9;
                    break;
                case 5://17
                    addMp = randomMpPlus + 10;
                    break;
                case 6://18
                case 7://19
                case 8://20
                    addMp = randomMpPlus + 11;
                    break;
                case 9://21
                case 10://22
                case 11://23
                    addMp = randomMpPlus + 13;
                    break;
                case 12://23
                    addMp = randomMpPlus + 14;
                    break;
                default://25
                    addMp = randomMpPlus + 15;
                    break;
            }

            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_WIZARD) {
                resultMp = CodeConfig.MAX_MP_WIZARD - baseMaxMp;
            }
        } else if (charType == L1Types.TYPE_DARK_ELF) { // 다크 에르프
            int calWis = 10;

            switch (baseWis - calWis) {
                case 0://10
                case 1://11
                    addMp = randomMpPlus + 3;
                    break;
                case 2://12
                case 3://13
                case 4://14
                    addMp = randomMpPlus + 5;
                    break;
                case 5://15
                case 6://16
                case 7://17
                    addMp = randomMpPlus + 6;
                    break;
                case 8://18
                case 9://19
                case 10://20
                    addMp = randomMpPlus + 7;
                    break;
                case 11://21
                case 12://22
                case 13://23
                    addMp = randomMpPlus + 9;
                    break;
                default://25
                    addMp = randomMpPlus + 10;
                    break;
            }
            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_DARKELF) {
                resultMp = CodeConfig.MAX_MP_DARKELF - baseMaxMp;
            }
        } else if (charType == L1Types.TYPE_DRAGON_KNIGHT) {
            int calWis = 12;
            switch (baseWis - calWis) {
                case 0://12
                case 1://13
                    addMp = randomMpPlus + 1;
                    break;
                case 2://14
                    addMp = randomMpPlus + 2;
                    break;
                case 3://15
                case 4://16
                case 5://17
                    addMp = randomMpPlus + 3;
                    break;
                case 6://18
                case 7://19
                case 8://20
                    addMp = randomMpPlus + 4;
                    break;
                case 9://21
                case 10://22
                case 11://23
                case 12://23
                    addMp = randomMpPlus + 5;
                    break;
                default://25
                    addMp = randomMpPlus + 6;
                    break;
            }

            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_DRAGON_KNIGHT) {
                resultMp = CodeConfig.MAX_MP_DRAGON_KNIGHT - baseMaxMp;
            }
        } else if (charType == L1Types.TYPE_ILLUSIONIST) { // 위저드
            int calWis = 12;

            switch (baseWis - calWis) {
                case 0://12
                case 1://13
                case 2://14
                    addMp = randomMpPlus + 4;
                    break;
                case 3://15
                case 4://16
                case 5://17
                    addMp = randomMpPlus + 5;
                    break;
                case 6://18
                case 7://19
                case 8://20
                    addMp = randomMpPlus + 6;
                    break;
                case 9://21
                case 10://22
                case 11://23
                    addMp = randomMpPlus + 7;
                    break;
                case 12://23
                    addMp = randomMpPlus + 8;
                    break;
                default://25
                    addMp = randomMpPlus + 9;
                    break;
            }

            resultMp += addMp;

            if (baseMaxMp + resultMp > CodeConfig.MAX_MP_ILLUSIONIST) {
                resultMp = CodeConfig.MAX_MP_ILLUSIONIST - baseMaxMp;
            }
        }

        if (resultMp < 0) {
            resultMp = 1;
        }

        return (short) resultMp;
    }

    public static int getMaxWeight(int str, int con) {
        int total = str + con;

        if (total > 150)
            total = 150;

        if (total % 2 != 0)
            total -= 1;

        return 1000 + (total * 100);
    }

    public static int calcLevelHitUp(int chartype, int level) {
        int levelHitUp = 0;

        switch (chartype) {
            case L1Types.TYPE_PRINCE: //군주
            case L1Types.TYPE_KNIGHT: //기사
            case L1Types.TYPE_WIZARD: //법사
            case L1Types.TYPE_DRAGON_KNIGHT: //법사
            case L1Types.TYPE_ILLUSIONIST: //법사
                levelHitUp = level / 10;
                break;
            case L1Types.TYPE_DARK_ELF: //다엘
                levelHitUp = level / 4;
                break;
            case L1Types.TYPE_ELF: //요정
                break;
            default:
                break;
        }

        return levelHitUp;
    }

    public static int calcLevelBowHitUp(int charType, int level) {
        int levelHitUp = 0;

        if (charType == L1Types.TYPE_ELF) {
            levelHitUp = level / 8;
        }

        return levelHitUp;
    }

    public static int calcLevelDmgUp(int charType, int level) {
        int dmgUp = 0;
        int dmg = level / 10;

        switch (charType) {
            case L1Types.TYPE_PRINCE:
            case L1Types.TYPE_KNIGHT:
            case L1Types.TYPE_DARK_ELF:
            case L1Types.TYPE_DRAGON_KNIGHT:
            case L1Types.TYPE_ILLUSIONIST:
                dmgUp = dmg;
                break;
            default:
                break;
        }

        return dmgUp;
    }

    public static int calcLevelBowDmgUp(int charType, int level) {
        int dmgUp = 0;
        int dmg = level / 10;

        if (charType == L1Types.TYPE_ELF) {
            dmgUp = dmg;
        }

        return dmgUp;
    }

    public static int calcStatBowDmgUp(int dex) {
        return getStatBonus("bowDmgUp", dex);
    }

    public static int calcLevelMagic(L1PcInstance pc) {
        int level = pc.getLevel();

        if (pc.isCrown()) {
            return Math.min(2, level / 10);
        } else if (pc.isKnight()) {
            return level / 50;
        } else if (pc.isElf()) {
            return Math.min(6, level / 8);
        } else if (pc.isWizard()) {
            return Math.min(11, level / 6);
        } else if (pc.isDarkElf()) {
            return Math.min(2, level / 12);
        } else if (pc.isDragonKnight()) {
            return Math.min(4, level / 9);
        } else if (pc.isIllusionist()) {
            return Math.min(10, level / 6);
        }

        return 0;
    }

    public static short calcInitHp(L1PcInstance pc) {
        short initHp = 0;

        if (pc.isCrown()) { // CROWN
            initHp = 14;
        } else if (pc.isKnight()) {
            initHp = 16;
        } else if (pc.isElf()) { // ELF
            initHp = 15;
        } else if (pc.isWizard()) { // WIZ
            initHp = 12;
        } else if (pc.isDarkElf()) { // DE
            initHp = 12;
        } else if (pc.isDragonKnight()) { // 용기사
            initHp = 16;
        } else if (pc.isIllusionist()) { // 환술사
            initHp = 14;
        }

        return initHp;
    }

    public static short calcInitMp(L1PcInstance pc) {
        short initMp = 0;

        if (pc.isCrown()) { // CROWN
            switch (pc.getAbility().getBaseWis()) {
                case 12:
                case 13:
                case 14:
                case 15:
                    initMp = 3;
                    break;
                case 16:
                case 17:
                case 18:
                    initMp = 4;
                    break;
                default:
                    initMp = 2;
                    break;
            }
        } else if (pc.isKnight()) {
            switch (pc.getAbility().getBaseWis()) {
                case 12:
                case 13:
                    initMp = 2;
                    break;
                default:
                    initMp = 1;
                    break;
            }
        } else if (pc.isElf()) { // ELF
            switch (pc.getAbility().getBaseWis()) {
                case 16:
                case 17:
                case 18:
                    initMp = 6;
                    break;
                default:
                    initMp = 4;
                    break;
            }
        } else if (pc.isWizard()) { // WIZ
            switch (pc.getAbility().getBaseWis()) {
                case 16:
                case 17:
                case 18:
                    initMp = 8;
                    break;
                default:
                    initMp = 6;
                    break;
            }
        } else if (pc.isDarkElf()) { // DE
            switch (pc.getAbility().getBaseWis()) {
                case 12:
                case 13:
                case 14:
                case 15:
                    initMp = 4;
                    break;
                case 16:
                case 17:
                case 18:
                    initMp = 6;
                    break;
                default:
                    initMp = 3;
                    break;
            }
        } else if (pc.isDragonKnight()) { // 용기사
            initMp = 2;
        } else if (pc.isIllusionist()) { // 환술사
            switch (pc.getAbility().getBaseWis()) {
                case 16:
                case 17:
                case 18:
                    initMp = 6;
                    break;
                default:
                    initMp = 5;
                    break;
            }
        }

        return initMp;
    }

    public static int calcStatBowHitUp(int dex) {
        return getStatBonus("bowHitUp", dex);
    }

    public static int calcStatHitUp(int str) {
        return getStatBonus("hitUp", str);
    }

    public static int calcStatDmg(int str) {
        return getStatBonus("dmgUp", str);
    }

    public static int calcStatSp(int inter) {
        return getStatBonus("spUp", inter);
    }

    public static int calcAc(int dex) {
        return 10 - getStatBonus("acUp", dex);
    }

    public static int calcStatMagicCritical(int inter) {
        return getStatBonus("magicCritical", inter);
    }

    public static int calcStatMagicHitUp(int inter) {
        return getStatBonus("magicHitUp", inter);
    }

    public static int calcStatMr(int wis) {
        return getStatBonus("mr", wis);
    }

    public static int calcStatEr(int dex) {
        return getStatBonus("er", dex);
    }

    public static int calcStatBowCritical(int dex) {
        return getStatBonus("bowCritical", dex);
    }

    public static int calcStatCritical(int str) {
        return getStatBonus("critical", str);
    }

    private static int getStatBonus(String type, int value) {
        return CharacterStatTable.getInstance().findBonus(type, value);
    }

    public static int calcBaseStatHitUp(L1PcInstance pc) {
        int baseStr = pc.getAbility().getBaseStr();

        if (pc.isCrown()) { // CROWN
            return getStatBonus("baseHitUpCrown", baseStr);
        } else if (pc.isKnight()) {
            return getStatBonus("baseHitUpKnight", baseStr);
        } else if (pc.isElf()) { // ELF
            return getStatBonus("baseHitUpElf", baseStr);
        } else if (pc.isWizard()) { // WIZ
            return getStatBonus("baseHitUpMage", baseStr);
        } else if (pc.isDarkElf()) { // DE
            return getStatBonus("baseHitUpDarkElf", baseStr);
        } else if (pc.isDragonKnight()) {
            return getStatBonus("baseHitUpDragonKnight", baseStr);
        } else if (pc.isIllusionist()) {
            return getStatBonus("baseHitUpIllusionist", baseStr);
        }

        return 0;
    }

    public static int calcBaseStatDmgUp(L1PcInstance pc) {
        int baseStr = pc.getAbility().getBaseStr();

        if (pc.isCrown()) { // CROWN
            return getStatBonus("baseDmgUpCrown", baseStr);
        } else if (pc.isKnight()) {
            return getStatBonus("baseDmgUpKnight", baseStr);
        } else if (pc.isElf()) { // ELF
            return getStatBonus("baseDmgUpElf", baseStr);
        } else if (pc.isWizard()) { // WIZ
            return getStatBonus("baseDmgUpMage", baseStr);
        } else if (pc.isDarkElf()) { // DE
            return getStatBonus("baseDmgUpDarkElf", baseStr);
        } else if (pc.isDragonKnight()) {
            return getStatBonus("baseDmgUpDragonKnight", baseStr);
        } else if (pc.isIllusionist()) {
            return getStatBonus("baseDmgUpIllusionist", baseStr);
        }

        return 0;
    }

    public static int calcBaseStatBowDmgUp(L1PcInstance pc) {
        int baseDex = pc.getAbility().getBaseDex();

        if (pc.isCrown()) { // CROWN
            return getStatBonus("baseBowDmgUpCrown", baseDex);
        } else if (pc.isKnight()) {
            return getStatBonus("baseBowDmgUpKnight", baseDex);
        } else if (pc.isElf()) { // ELF
            return getStatBonus("baseBowDmgUpElf", baseDex);
        } else if (pc.isDarkElf()) { // DE
            return getStatBonus("baseBowDmgUpDarkElf", baseDex);
        } else if (pc.isDragonKnight()) { // DE
            return getStatBonus("baseBowDmgUpDragonKnight", baseDex);
        } else if (pc.isIllusionist()) { // DE
            return getStatBonus("baseBowDmgUpIllusionist", baseDex);
        }

        return 0;
    }

    public static int calcBaseStatBowHitUp(L1PcInstance pc) {
        int baseDex = pc.getAbility().getBaseDex();

        if (pc.isCrown()) { // CROWN
            return getStatBonus("baseBowHitUpCrown", baseDex);
        } else if (pc.isKnight()) {
            return getStatBonus("baseBowHitUpKnight", baseDex);
        } else if (pc.isElf()) { // ELF
            return getStatBonus("baseBowHitUpElf", baseDex);
        } else if (pc.isDarkElf()) { // DE
            return getStatBonus("baseBowHitUpDarkElf", baseDex);
        }

        return 0;
    }

    public static int calcBaseStatAc(L1PcInstance pc) {
        int baseDex = pc.getAbility().getBaseDex();

        if (pc.isCrown()) { // CROWN
            return getStatBonus("baseAcUpCrown", baseDex);
        } else if (pc.isKnight()) {
            return getStatBonus("baseAcUpKnight", baseDex);
        } else if (pc.isElf()) { // ELF
            return getStatBonus("baseAcUpElf", baseDex);
        } else if (pc.isDarkElf()) { // DE
            return getStatBonus("baseAcUpDarkElf", baseDex);
        } else if (pc.isDragonKnight()) { // DE
            return getStatBonus("baseAcUpDragonKnight", baseDex);
        } else if (pc.isIllusionist()) { // DE
            return getStatBonus("baseAcUpIllusionist", baseDex);
        }

        return 0;
    }

    public static int calcInitMr(L1PcInstance pc) {
        int newMr = 0;

        if (pc.isCrown())
            newMr = 10;
        else if (pc.isElf())
            newMr = 22;
        else if (pc.isWizard())
            newMr = 15;
        else if (pc.isDarkElf())
            newMr = 10;
        else if (pc.isDragonKnight())
            newMr = 18;
        else if (pc.isIllusionist())
            newMr = 20;

        return newMr;
    }

    public static int calcBaseStatMagicHitUp(L1PcInstance pc) {
        int baseInt = pc.getAbility().getBaseInt();

        if (pc.isCrown()) { // CROWN
            return getStatBonus("baseMagicHitUpCrown", baseInt);
        } else if (pc.isKnight()) {
            return getStatBonus("baseMagicHitUpKnight", baseInt);
        } else if (pc.isElf()) { // ELF
            return getStatBonus("baseMagicHitUpElf", baseInt);
        } else if (pc.isWizard()) { // DE
            return getStatBonus("baseMagicHitUpMage", baseInt);
        } else if (pc.isDarkElf()) { // DE
            return getStatBonus("baseMagicHitUpDarkElf", baseInt);
        }

        return 0;
    }

    public static int calcBaseStatMagicCriticalPer(L1PcInstance pc) {
        int baseInt = pc.getAbility().getBaseInt();

        if (pc.isCrown()) {
            return getStatBonus("baseMagicCriticalCrown", baseInt);
        } else if (pc.isKnight()) {
            return getStatBonus("baseMagicCriticalKnight", baseInt);
        } else if (pc.isElf()) {
            return getStatBonus("baseMagicCriticalElf", baseInt);
        } else if (pc.isWizard()) {
            return getStatBonus("baseMagicCriticalMage", baseInt);
        } else if (pc.isDarkElf()) {
            return getStatBonus("baseMagicCriticalDarkElf", baseInt);
        } else if (pc.isDragonKnight()) {
            return getStatBonus("baseMagicCriticalDragonKnight", baseInt);
        } else if (pc.isIllusionist()) {
            return getStatBonus("baseMagicCriticalIllusionist", baseInt);
        }

        return 0;
    }

    public static int calcBaseStatMagicDamage(L1PcInstance pc) {
        int baseInt = pc.getAbility().getBaseInt();

        if (pc.isWizard()) {
            if (baseInt >= 13) {
                return 1;
            }
        }

        return 0;
    }
}
