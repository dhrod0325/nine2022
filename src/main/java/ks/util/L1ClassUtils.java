package ks.util;

import ks.model.pc.L1PcInstance;

public class L1ClassUtils {
    public static final int CLASSID_PRINCE = 0;
    public static final int CLASSID_PRINCESS = 1;
    public static final int CLASSID_KNIGHT_MALE = 61;
    public static final int CLASSID_KNIGHT_FEMALE = 48;
    public static final int CLASSID_ELF_MALE = 138;
    public static final int CLASSID_ELF_FEMALE = 37;
    public static final int CLASSID_WIZARD_MALE = 734;
    public static final int CLASSID_WIZARD_FEMALE = 1186;
    public static final int CLASSID_DARK_ELF_MALE = 2786;
    public static final int CLASSID_DARK_ELF_FEMALE = 2796;
    public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
    public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;
    public static final int CLASSID_ILLUSIONIST_MALE = 6671;
    public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;

    public static boolean isCrown(int classId) {
        return (classId == CLASSID_PRINCE || classId == CLASSID_PRINCESS);
    }

    public static boolean isKnight(int classId) {
        return (classId == CLASSID_KNIGHT_MALE || classId == CLASSID_KNIGHT_FEMALE);
    }

    public static boolean isElf(int classId) {
        return (classId == CLASSID_ELF_MALE || classId == CLASSID_ELF_FEMALE);
    }

    public static boolean isWizard(int classId) {
        return (classId == CLASSID_WIZARD_MALE || classId == CLASSID_WIZARD_FEMALE);
    }

    public static boolean isDarkelf(int classId) {
        return (classId == CLASSID_DARK_ELF_MALE || classId == CLASSID_DARK_ELF_FEMALE);
    }

    public static boolean isDragonknight(int classId) {
        return (classId == CLASSID_DRAGON_KNIGHT_MALE || classId == CLASSID_DRAGON_KNIGHT_FEMALE);
    }

    public static boolean isIllusionist(int classId) {
        return (classId == CLASSID_ILLUSIONIST_MALE || classId == CLASSID_ILLUSIONIST_FEMALE);
    }

    public static String className(L1PcInstance pc) {
        if (L1ClassUtils.isCrown(pc.getClassId())) {
            return "prince";
        } else if (L1ClassUtils.isKnight(pc.getClassId())) {
            return "knight";
        } else if (L1ClassUtils.isElf(pc.getClassId())) {
            return "elf";
        } else if (L1ClassUtils.isDarkelf(pc.getClassId())) {
            return "darkelf";
        } else if (L1ClassUtils.isWizard(pc.getClassId())) {
            return "wizard";
        }

        return "";
    }
}
