package ks.core.datatables.buff;

import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

import static ks.constants.L1SkillId.*;

public class CharBuffTable {
    public static final int[] BUFF_SKILL = {
            STATUS_WISDOM_POTION,
            STATUS_DRAGON_PERL,
            2,
            67,
            3,
            99,
            151,
            159,
            168,
            43,
            54,
            1000,
            1001,
            STATUS_ELFBRAVE,
            52,
            101,
            150,
            26,
            42,
            109,
            110,
            114,
            115,
            117,
            148,
            155,
            163,
            149,
            156,
            166,
            1002,
            STATUS_CHAT_PROHIBITED,
            DECREASE_WEIGHT, DECAY_POTION, SILENCE, VENOM_RESIST, WEAKNESS, DISEASE, DRESS_EVASION, BERSERKERS,
            NATURES_TOUCH, WIND_SHACKLE, ERASE_MAGIC, ADDITIONAL_FIRE, ELEMENTAL_FALL_DOWN, ELEMENTAL_FIRE,
            STRIKER_GALE, SOUL_OF_FLAME, POLLUTE_WATER,
            RESIST_MAGIC, CLEAR_MIND, RESIST_ELEMENTAL, ELEMENTAL_PROTECTION,
            STATUS_BLUE_POTION2,
            STATUS_CASHSCROLL1, STATUS_CASHSCROLL2, STATUS_CASHSCROLL3,
            STATUS_COMA_3, STATUS_COMA_5,
            EXP_POTION1, EXP_POTION2, EXP_POTION3,

            CONCENTRATION, INSIGHT, PANIC, MORTAL_BODY, HORROR_OF_DEATH, FEAR,
            PATIENCE, GUARD_BREAK, DRAGON_SKIN, BLOOD_LUST,

            SPECIAL_COOKING,
            DRAGON_EMERALD_NO, STATUS_DRAGON_EMERALD_YES,
            STATUS_BLUE_POTION3,
            STATUS_LUCK_A,
            STATUS_LUCK_B,
            STATUS_LUCK_C,
            STATUS_LUCK_D,

            COOKING_1_0_N, COOKING_1_0_S,
            COOKING_1_1_N, COOKING_1_1_S,
            COOKING_1_2_N, COOKING_1_2_S,
            COOKING_1_3_N, COOKING_1_3_S,
            COOKING_1_4_N, COOKING_1_4_S,
            COOKING_1_5_N, COOKING_1_5_S,
            COOKING_1_6_N, COOKING_1_6_S,
            COOKING_1_7_N, COOKING_1_7_S,
            COOKING_1_8_N, COOKING_1_8_S,
            COOKING_1_9_N, COOKING_1_9_S,
            COOKING_1_10_N, COOKING_1_10_S,
            COOKING_1_11_N, COOKING_1_11_S,
            COOKING_1_12_N, COOKING_1_12_S,
            COOKING_1_13_N, COOKING_1_13_S,
            COOKING_1_14_N, COOKING_1_14_S,
            COOKING_1_15_N, COOKING_1_15_S,
            COOKING_1_16_N, COOKING_1_16_S,
            COOKING_1_17_N, COOKING_1_17_S,
            COOKING_1_18_N, COOKING_1_18_S,
            COOKING_1_19_N, COOKING_1_19_S,
            COOKING_1_20_N, COOKING_1_20_S,
            COOKING_1_21_N, COOKING_1_21_S,
            COOKING_1_22_N, COOKING_1_22_S,
            COOKING_1_23_N, COOKING_1_23_S,

            COOKING_NEW_1, COOKING_NEW_2, COOKING_NEW_3, COOKING_NEW_4,
            ANTA_MAAN, FAFU_MAAN, LIND_MAAN,
            VALA_MAAN, BIRTH_MAAN, SHAPE_MAAN,
            LIFE_MAAN,

            MOB_RANGE_ERASE_MAGIC,

            5000063,
            5000062,
            5000061
    };

    private static void store(int objId, int skillId, int time, int polyId) {
        SqlUtils.update("INSERT INTO character_buff SET char_obj_id=?, skill_id=?, remaining_time=?, poly_id=? ON DUPLICATE KEY UPDATE remaining_time=?, poly_id=?", objId, skillId, time, polyId, time, polyId);
    }

    public static void delete(L1PcInstance pc) {
        SqlUtils.update("DELETE FROM character_buff WHERE char_obj_id=?", pc.getId());
    }

    public static void save(L1PcInstance pc) {
        for (int skillId : BUFF_SKILL) {
            int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(skillId);

            if (0 < timeSec) {
                int polyId = 0;

                if (skillId == SHAPE_CHANGE) {
                    polyId = pc.getGfxId().getTempCharGfx();
                }

                store(pc.getId(), skillId, timeSec, polyId);
            }
        }
    }

    public static List<CharBuff> selectList(int charId) {
        return SqlUtils.query("SELECT * FROM character_buff WHERE char_obj_id=?", new BeanPropertyRowMapper<>(CharBuff.class), charId);
    }
}
