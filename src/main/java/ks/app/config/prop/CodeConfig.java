package ks.app.config.prop;

import ks.core.datatables.MapsTable;
import ks.core.datatables.commonCode.CommonCodeTable;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CodeConfig {
    public static final String MAP_DIR = "./data/mapsPlay/";

    public static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    public static double BALANCE_DMG;

    public static int MIN_MASTER_ARMOR_ENCHANT;
    public static int MIN_MASTER_WEAPON_ENCHANT;

    public static int ENCHANT_MENT_ACC;
    public static int ENCHANT_MENT_WEAPON;
    public static int ENCHANT_MENT_ARMOR;

    public static int RATE_CRAFT;

    public static int ASTAR_CHECK_COUNT;

    private static final Logger logger = LogManager.getLogger(CodeConfig.class);

    public static Boolean USE_ITEM_COLOR_BY_GRADE;
    public static int SPR_KNIGHT;
    public static int SPR_KNIGHT_FEMALE;
    public static double MAGIC_DMG_REDUCE_BY_MR;
    public static int SPR_PRINCE;
    public static int SPR_PRINCESS;
    public static int MAX_TRADE_PRICE;
    public static boolean AUTO_LOOT_ADENA;
    public static boolean AUTO_LOOT_ITEM;
    public static boolean AUTO_CHECK_USE;
    public static int MAX_ACC_ENCHANT;
    public static double EXP_BONUS_AIN;
    public static double EXP_BONUS_EME;
    public static int TELEPORT_SLEEP;
    public static int DIRTY_CHAT_TYPE;
    public static int DIRTY_CHAT_TIME;
    public static String ADEN_BOARD_CLICK_MSG;
    public static int RANK_POLY_ALL_RANK;
    public static int PACKET_ATTACK_INTERVAL;
    public static int PACKET_ATTACK_COUNT;
    public static int MAGIC_MAX_DMG;
    public static double MAGIC_DMG_SP;
    public static double MAGIC_DMG_INT;
    public static boolean ASTAR_USE;
    public static double MAGIC_DMG_VALANCE;
    public static double AUTO_ATTACK_DELAY;

    public static int TELL_WAIT_TIME;

    public static int FORCE_DISCONNECT_QUIT;

    public static int ASTAR_LOOP_COUNT;
    public static int ASTAR_SEARCH_TAIL_COUNT;
    public static int ASTAR_NODE_CHECK_COUNT;

    public static int FIELD_ITEM_DELETE_MINUTE;

    public static int AUTO_SAVE_CHAR_INTERVAL;
    public static int AUTO_SAVE_CHAR_INV_INTERVAL;

    public static double DRAGON_ANTA_DMG_REDUCE;

    public static int MAX_ARMOR_ENCHANT = 0;
    public static double MAX_LEVEL_PANALTY = 0;
    public static int MONSTER_MAX_MOVE_DISTANCE_FROM_HOMEPOINT;
    public static int MOB_ESCAPE_DISTANCE;
    public static int MON_TARGET_CHANGE_DISTANCE;
    public static int BOSS_MAX_MOVE_DISTANCE_FROM_HOMEPOINT;
    public static int CLASSIC_BOX_SOUND;
    public static int CASTLE_WAR_MIN_MEMBER_CNT;

    public static int ASTAR_CHECK_RANGE;

    public static int BOSS_HANIP;
    public static int RANK_BUFF_MIN_LEVEL;
    public static double CHECK_HASTE_RATE;
    public static double CHECK_BRAVE_RATE;
    public static double CHECK_WAFFLE_RATE;
    public static double CHECK_DRGON_PER_RATE;
    public static double CHECK_MOVE_RATE;
    public static double CHECK_NO_POLY_RATE;
    public static int CASTLE_WAR_DEFENSE_MAX_MINUTE;
    public static int ER_RANDOM_VALUE;
    public static int DB_BACKUP_MININUTE;
    public static int DB_BACKUP_FILE_COUNT;
    public static boolean DB_BACKUP_USE;

    public static int EXP_GIVE_MAX_LEVEL;
    public static int SERVER_STATUS;
    public static boolean GM_IGNORE_TELEPORT_ABLE;
    public static String DB_BACKUP_DIR;
    public static boolean USE_BOSS_SYSTEM;
    public static int MAX_SELF_BUFF_LEVEL;
    public static int RESOLVENT_FAIL_PER;
    public static int MAX_WEAPON_ENCHANT;
    public static boolean STANDBY_SERVER;
    public static int DELETE_MIN_LEVEL;
    public static int START_LEVEL;
    public static int ADEN_SELL_MIN;

    public static int SERVER_GAHO_LEVEL;

    public static int PET_EXP_RATE;
    public static int RANK_POLY_RANK;
    public static boolean USE_BRAVE_AVATAR;
    public static int FISHING_EXP;
    public static double TRIPLE_BALANCE;
    public static int MAX_LEVEL;
    public static int FEATHER_NUMBER;
    public static double FEATHER_CLAN_BONUS;
    public static double FEATHER_CASTLE_BONUS;
    public static int ADEN_CANCEL_WAIT_TIME;
    public static int CRACKER_MAX_LEVEL;
    public static int ADEN_BOARD_NPC_ID;
    public static int CASTLE_WAR_WINNER_ADENA;
    public static int ADEN_SELL_MIN_LEVEL;
    public static int ADEN_BUY_MIN_LEVEL;

    public static int BLESS_ADD_DMG;
    public static int BLESS_ADD_REDUC;
    public static int BLESS_ADD_HP;

    public static int DEATH_KILL_UP_MIN_LEVEL;
    public static int AUTO_SHOP_MAX_ITEM_COUNT;

    public static int SPEED_HACK_PRISON;
    public static boolean SPEED_CHECK_MOVE_INTERVAL;
    public static boolean SPEED_CHECK_ATTACK_INTERVAL;
    public static boolean SPEED_CHECK_SPELL_INTERVAL;
    public static int SPEED_JUSTICE_COUNT;
    public static int SPEED_CHECK_STRICTNESS;
    public static boolean SPEED_HACK_CHECK;
    public static boolean SPEED_HACK_STUN;
    public static int SPEED_HACK_STUN_TIME;

    public static boolean SAFE_MODE;
    public static boolean WAR_MARK_ALL;
    public static int ABANDONED_LAND_LEV;
    public static int RND_RIPER;
    public static boolean ENCHANT_MENT;
    public static int MAX_CLIENT_COUNT;
    public static String NON_DROP_LIST;
    public static String MAP_NORMAL_CHANGE;
    public static int DRAGON_PAPOO_MIN;
    public static int DRAGON_PAPOO_MAX;
    public static int DRAGON_VALA_MIN;
    public static int DRAGON_VALA_MAX;
    public static double DRAGON_VALA_ELF;
    public static double REDUCE_PC_DMG_BY_AC;
    public static double BOSS_HP_VALANCE1;
    public static double BOSS_HP_VALANCE2;
    public static double BOSS_HP_VALANCE3;
    public static String CLASSIC_DROP;
    public static int CLAN_MEMBER_MAX_COUNT;
    public static int RATE_XP;
    public static int RATE_DROP_ADENA;
    public static int RATE_DROP_ITEMS;
    public static int RATE_CRISTAL;
    public static int RATE_LAWFUL;
    public static int RATE_KARMA;
    public static double RATE_WEIGHT_LIMIT;
    public static double RATE_WEIGHT_LIMIT_PET;

    public static boolean GRANG_KIN_ANGER_SYSTEM_USE;
    public static int ER_AQUA;
    public static int ER_EVASION;
    public static int ER_SOLID;
    public static int DG_AC_130;
    public static int DG_AC_125;
    public static int DG_AC_120;
    public static int DG_AC_115;
    public static int DG_AC_110;
    public static int DG_AC_105;
    public static int DG_AC_100;
    public static int DG_AC_95;
    public static int DG_AC_90;
    public static int DG_AC_85;
    public static int DG_AC_80;
    public static int DG_AC_75;
    public static int DG_AC_70;
    public static int DG_AC_65;
    public static int DG_AC_60;
    public static int DG_AC_55;
    public static int DG_AC_50;
    public static int DG_AC_45;
    public static int DG_AC_40;
    public static int DG_AC_35;
    public static int DG_AC_30;
    public static int DG_AC_25;
    public static int GIRAN_PORTAL_NUMBER;
    public static double MAGIC_IMMUNE_TO_HARM_REDUCE;
    public static double MAGIC_IMMUNE_TO_HARM_REDUCE_NPC;
    public static String ADENA_CUSTOM_DROP_MAPID;
    public static String ADENA_CUSTOM_NON_DROP_MAPID;
    public static String STACK_OPEN_BOX_ID;
    public static int AUTO_UPDATE_INTERVAL;
    public static int CASTLE_WAR_BOMB_DMG_MIN;
    public static int CASTLE_WAR_BOMB_DMG_MAX;
    public static int CASTLE_WAR_BOMB_ADENA;
    public static int CASTLE_WAR_MIN_CROWN_LEVEL;
    public static int CASTLE_WAR_TIME;
    public static int ALT_WAR_TIME_UNIT;
    public static int CASTLE_WAR_TIME_INTERVAL;
    public static int ALT_WAR_INTERVAL_UNIT;
    public static int CREATE_CLAN_PRICE;
    public static boolean USE_SPR_CHECK;

    public static int PC_RECOGNIZE_RANGE;

    public static String EARTH_BIND_DURATION;

    public static int NEXT_REQ_SERVER_STATE;

    public static boolean NEXT_REQ_START;
    public static int BLESS_ADD_SP;

    public static int MAX_HP_PRINCE;
    public static int MAX_MP_PRINCE;
    public static int MAX_HP_KNIGHT;
    public static int MAX_MP_KNIGHT;
    public static int MAX_HP_ELF;
    public static int MAX_MP_ELF;
    public static int MAX_HP_WIZARD;
    public static int MAX_MP_WIZARD;
    public static int MAX_HP_DARKELF;
    public static int MAX_MP_DARKELF;
    public static int MAX_HP_DRAGON_KNIGHT;
    public static int MAX_MP_DRAGON_KNIGHT;
    public static int MAX_HP_ILLUSIONIST;
    public static int MAX_MP_ILLUSIONIST;

    public static boolean AUTO_CREATE_ACCOUNTS;
    public static int DROP_LEVEL_LIMIT;
    public static int SKILL_TIMER_IMPL_TYPE;
    public static boolean CHARACTER_CONFIG_IN_SERVER_SIDE;
    public static int LEVEL_DOWN_RANGE;
    public static int GLOBAL_CHAT_LEVEL;
    public static int WHISPER_CHAT_LEVEL;
    public static boolean CHANGE_TITLE_BY_ONESELF;
    public static boolean CLAN_ALLIANCE;
    public static int MAX_PARTY_NUMBER;
    public static int MAX_CHAT_PARTY_NUMBER;
    public static boolean WAR_PENALTY;
    public static String ALT_ITEM_DELETION_TYPE;
    public static boolean SPAWN_HOME_POINT;
    public static int SPAWN_HOME_POINT_RANGE;
    public static int SPAWN_HOME_POINT_COUNT;
    public static int SPAWN_HOME_POINT_DELAY;
    public static int MAX_DOLL_COUNT;
    public static boolean RETURN_TO_NATURE;
    public static int MAX_NPC_ITEM;
    public static int MAX_PERSONAL_WAREHOUSE_ITEM;
    public static int MAX_CLAN_WAREHOUSE_ITEM;
    public static boolean DELETE_CHARACTER_AFTER_7DAYS;
    public static int GM_CODE;
    public static boolean IS_GM_CHAT = true;

    public static boolean CACHE_MAP_FILES;

    public static int MANA_DRAIN_LIMIT_PER_SOM_ATTACK;

    public static boolean isHuntMap(int mapId) {
        return MapsTable.getInstance().huntMapList().contains(mapId);
    }

    public static void store(String key, String value) {
        CommonCodeTable.getInstance().update(key, value);
    }

    public static void load() {
        try {
            CommonCodeTable codeTable = CommonCodeTable.getInstance();

            String strWar = codeTable.getString("CASTLE_WAR_TIME", "1h");

            if (strWar.contains("d")) {
                ALT_WAR_TIME_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.contains("h")) {
                ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.contains("m")) {
                ALT_WAR_TIME_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }

            CASTLE_WAR_TIME = Integer.parseInt(strWar);

            strWar = codeTable.getString("CASTLE_WAR_TIME_INTERVAL", "2d");

            if (strWar.contains("d")) {
                ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.contains("h")) {
                ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.contains("m")) {
                ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }

            CASTLE_WAR_TIME_INTERVAL = Integer.parseInt(strWar);

            USE_ITEM_COLOR_BY_GRADE = codeTable.getBoolean("USE_ITEM_COLOR_BY_GRADE", false);

            MIN_MASTER_ARMOR_ENCHANT = codeTable.getInteger("MIN_MASTER_ARMOR_ENCHANT", 8);
            MIN_MASTER_WEAPON_ENCHANT = codeTable.getInteger("MIN_MASTER_WEAPON_ENCHANT", 9);

            ENCHANT_MENT_ACC = codeTable.getInteger("ENCHANT_MENT_ACC", 7);
            ENCHANT_MENT_WEAPON = codeTable.getInteger("ENCHANT_MENT_WEAPON", 9);
            ENCHANT_MENT_ARMOR = codeTable.getInteger("ENCHANT_MENT_ARMOR", 8);
            BALANCE_DMG = codeTable.getDouble("BALANCE_DMG", 1.35);
            SPR_PRINCE = codeTable.getInteger("SPR_PRINCE", 0);
            SPR_PRINCESS = codeTable.getInteger("SPR_PRINCESS", 0);
            SPR_KNIGHT = codeTable.getInteger("SPR_KNIGHT", 0);
            SPR_KNIGHT_FEMALE = codeTable.getInteger("SPR_KNIGHT_FEMALE", 0);
            SERVER_GAHO_LEVEL = codeTable.getInteger("SERVER_GAHO_LEVEL", 75);
            SERVER_STATUS = codeTable.getInteger("SERVER_STATUS", 0);
            AUTO_CHECK_USE = codeTable.getBoolean("AUTO_CHECK_USE", true);
            MAX_TRADE_PRICE = codeTable.getInteger("MAX_TRADE_PRICE", 100000000);
            MAGIC_MAX_DMG = codeTable.getInteger("MAGIC_MAX_DMG", 1000);
            TELEPORT_SLEEP = codeTable.getInteger("TELEPORT_SLEEP", 180);
            DIRTY_CHAT_TYPE = codeTable.getInteger("DIRTY_CHAT_TYPE", 0);
            DIRTY_CHAT_TIME = codeTable.getInteger("DIRTY_CHAT_TIME", 1);
            RATE_CRAFT = codeTable.getInteger("RATE_CRAFT", 1);
            AUTO_SAVE_CHAR_INTERVAL = codeTable.getInteger("AUTO_SAVE_CHAR_INTERVAL", 1000);
            AUTO_SAVE_CHAR_INV_INTERVAL = codeTable.getInteger("AUTO_SAVE_CHAR_INV_INTERVAL", 1000);

            PACKET_ATTACK_INTERVAL = codeTable.getInteger("PACKET_ATTACK_INTERVAL", 1000);
            PACKET_ATTACK_COUNT = codeTable.getInteger("PACKET_ATTACK_COUNT", 1000);

            AUTO_LOOT_ADENA = codeTable.getBoolean("AUTO_LOOT_ADENA", true);
            AUTO_LOOT_ITEM = codeTable.getBoolean("AUTO_LOOT_ITEM", true);

            FEATHER_CLAN_BONUS = codeTable.getDouble("FEATHER_CLAN_BONUS", 0.2);
            FEATHER_CASTLE_BONUS = codeTable.getDouble("FEATHER_CASTLE_BONUS", 0.5);

            ASTAR_USE = codeTable.getBoolean("ASTAR_USE", false);
            ASTAR_LOOP_COUNT = codeTable.getInteger("ASTAR_LOOP_COUNT", 50);
            ASTAR_CHECK_COUNT = codeTable.getInteger("ASTAR_CHECK_COUNT", 20);
            TELL_WAIT_TIME = codeTable.getInteger("TELL_WAIT_TIME", 3000);

            DRAGON_VALA_ELF = codeTable.getDouble("DRAGON_VALA_ELF", 0.7);
            DRAGON_ANTA_DMG_REDUCE = codeTable.getDouble("DRAGON_ANTA_DMG_REDUCE", 1.5);

            FEATHER_NUMBER = codeTable.getInteger("FEATHER_NUMBER", 5);

            FORCE_DISCONNECT_QUIT = codeTable.getInteger("FORCE_DISCONNECT_QUIT", 5);

            SPEED_HACK_PRISON = codeTable.getInteger("SPEED_HACK_PRISON", 10);
            SPEED_HACK_CHECK = codeTable.getBoolean("SPEED_HACK_CHECK", true);
            SPEED_HACK_STUN = codeTable.getBoolean("SPEED_HACK_STUN", true);
            SPEED_HACK_STUN_TIME = codeTable.getInteger("SPEED_HACK_STUN_TIME", 300);

            SPEED_CHECK_MOVE_INTERVAL = codeTable.getBoolean("SPEED_CHECK_MOVE_INTERVAL", true);
            SPEED_CHECK_ATTACK_INTERVAL = codeTable.getBoolean("SPEED_CHECK_ATTACK_INTERVAL", true);
            SPEED_CHECK_SPELL_INTERVAL = codeTable.getBoolean("SPEED_CHECK_SPELL_INTERVAL", true);

            SPEED_JUSTICE_COUNT = codeTable.getInteger("SPEED_JUSTICE_COUNT", 5);
            SPEED_CHECK_STRICTNESS = codeTable.getInteger("SPEED_CHECK_STRICTNESS", 160);

            FIELD_ITEM_DELETE_MINUTE = codeTable.getInteger("FIELD_ITEM_DELETE_MINUTE", 35);

            RANK_BUFF_MIN_LEVEL = codeTable.getInteger("RANK_BUFF_MIN_LEVEL", 55);

            DB_BACKUP_DIR = codeTable.getString("DB_BACKUP_DIR", "");

            DB_BACKUP_USE = codeTable.getBoolean("DB_BACKUP_USE", true);
            DB_BACKUP_MININUTE = codeTable.getInteger("DB_BACKUP_MININUTE", 30);
            DB_BACKUP_FILE_COUNT = codeTable.getInteger("DB_BACKUP_FILE_COUNT", 5);

            CASTLE_WAR_MIN_CROWN_LEVEL = codeTable.getInteger("CASTLE_WAR_MIN_CROWN_LEVEL", 55);
            CASTLE_WAR_MIN_MEMBER_CNT = codeTable.getInteger("CASTLE_WAR_MIN_MEMBER_CNT", 0);
            CASTLE_WAR_BOMB_DMG_MIN = codeTable.getInteger("CASTLE_WAR_BOMB_DMG_MIN", 200);
            CASTLE_WAR_BOMB_DMG_MAX = codeTable.getInteger("CASTLE_WAR_BOMB_DMG_MAX", 300);
            CASTLE_WAR_BOMB_ADENA = codeTable.getInteger("CASTLE_WAR_BOMB_ADENA", 100000);
            CASTLE_WAR_DEFENSE_MAX_MINUTE = codeTable.getInteger("CASTLE_WAR_DEFENSE_MAX_MINUTE", 10);
            CASTLE_WAR_WINNER_ADENA = codeTable.getInteger("CASTLE_WAR_WINNER_ADENA", 50000000);

            GIRAN_PORTAL_NUMBER = codeTable.getInteger("GIRAN_PORTAL_NUMBER", 1);
            RANK_POLY_RANK = codeTable.getInteger("RANK_POLY_RANK", 3);
            RANK_POLY_ALL_RANK = codeTable.getInteger("RANK_POLY_ALL_RANK", 10);
            BOSS_HANIP = codeTable.getInteger("BOSS_HANIP", 52);

            DG_AC_130 = codeTable.getInteger("DG_AC_130", 0);
            DG_AC_125 = codeTable.getInteger("DG_AC_125", 0);
            DG_AC_120 = codeTable.getInteger("DG_AC_120", 0);
            DG_AC_115 = codeTable.getInteger("DG_AC_115", 0);
            DG_AC_110 = codeTable.getInteger("DG_AC_110", 0);
            DG_AC_105 = codeTable.getInteger("DG_AC_105", 0);
            DG_AC_100 = codeTable.getInteger("DG_AC_100", 0);
            DG_AC_95 = codeTable.getInteger("DG_AC_95", 0);
            DG_AC_90 = codeTable.getInteger("DG_AC_90", 0);
            DG_AC_85 = codeTable.getInteger("DG_AC_85", 0);
            DG_AC_80 = codeTable.getInteger("DG_AC_80", 0);
            DG_AC_75 = codeTable.getInteger("DG_AC_75", 0);
            DG_AC_70 = codeTable.getInteger("DG_AC_70", 0);
            DG_AC_65 = codeTable.getInteger("DG_AC_65", 0);
            DG_AC_60 = codeTable.getInteger("DG_AC_60", 0);
            DG_AC_55 = codeTable.getInteger("DG_AC_55", 0);
            DG_AC_50 = codeTable.getInteger("DG_AC_50", 0);
            DG_AC_45 = codeTable.getInteger("DG_AC_45", 0);
            DG_AC_40 = codeTable.getInteger("DG_AC_40", 0);
            DG_AC_35 = codeTable.getInteger("DG_AC_35", 0);
            DG_AC_30 = codeTable.getInteger("DG_AC_30", 0);
            DG_AC_25 = codeTable.getInteger("DG_AC_25", 0);

            ASTAR_CHECK_RANGE = codeTable.getInteger("ASTAR_CHECK_RANGE", 0);

            ER_AQUA = codeTable.getInteger("ER_AQUA", 0);
            ER_EVASION = codeTable.getInteger("ER_EVASION", 0);
            ER_SOLID = codeTable.getInteger("ER_SOLID", 0);
            ER_RANDOM_VALUE = codeTable.getInteger("ER_RANDOM_VALUE", 0);

            AUTO_UPDATE_INTERVAL = codeTable.getInteger("AUTO_UPDATE_INTERVAL", 300);

            MON_TARGET_CHANGE_DISTANCE = codeTable.getInteger("MON_TARGET_CHANGE_DISTANCE", 8);

            BOSS_MAX_MOVE_DISTANCE_FROM_HOMEPOINT = codeTable.getInteger("BOSS_MAX_MOVE_DISTANCE_FROM_HOMEPOINT", 0);
            MONSTER_MAX_MOVE_DISTANCE_FROM_HOMEPOINT = codeTable.getInteger("MONSTER_MAX_MOVE_DISTANCE_FROM_HOMEPOINT", 300);

            REDUCE_PC_DMG_BY_AC = codeTable.getDouble("REDUCE_PC_DMG_BY_AC", 0.049);

            USE_SPR_CHECK = codeTable.getBoolean("USE_SPR_CHECK", true);

            CLASSIC_BOX_SOUND = codeTable.getInteger("CLASSIC_BOX_SOUND", 6875);

            EXP_GIVE_MAX_LEVEL = codeTable.getInteger("EXP_GIVE_MAX_LEVEL", 52);

            MAGIC_IMMUNE_TO_HARM_REDUCE = codeTable.getDouble("MAGIC_IMMUNE_TO_HARM_REDUCE", 1.3);
            MAGIC_IMMUNE_TO_HARM_REDUCE_NPC = codeTable.getDouble("MAGIC_IMMUNE_TO_HARM_REDUCE_NPC", 1.3);

            MAGIC_DMG_SP = codeTable.getDouble("MAGIC_DMG_SP", 0.15);
            MAGIC_DMG_INT = codeTable.getDouble("MAGIC_DMG_INT", 0.2);

            GM_IGNORE_TELEPORT_ABLE = codeTable.getBoolean("GM_IGNORE_TELEPORT_ABLE", true);

            DELETE_MIN_LEVEL = codeTable.getInteger("DELETE_MIN_LEVEL", 76);

            START_LEVEL = codeTable.getInteger("START_LEVEL", 76);

            CREATE_CLAN_PRICE = codeTable.getInteger("CREATE_CLAN_PRICE", 76);

            MAX_WEAPON_ENCHANT = codeTable.getInteger("MAX_WEAPON_ENCHANT", 11);
            MAX_ARMOR_ENCHANT = codeTable.getInteger("MAX_ARMOR_ENCHANT", 10);
            MAX_ACC_ENCHANT = codeTable.getInteger("MAX_ACC_ENCHANT", 10);

            AUTO_SHOP_MAX_ITEM_COUNT = codeTable.getInteger("AUTO_SHOP_MAX_ITEM_COUNT", 0);
            DEATH_KILL_UP_MIN_LEVEL = codeTable.getInteger("DEATH_KILL_UP_MIN_LEVEL", 0);
            ADEN_SELL_MIN_LEVEL = codeTable.getInteger("ADEN_SELL_MIN_LEVEL", 0);
            ADEN_BUY_MIN_LEVEL = codeTable.getInteger("ADEN_BUY_MIN_LEVEL", 0);

            PET_EXP_RATE = codeTable.getInteger("PET_EXP_RATE", 0);
            USE_BOSS_SYSTEM = codeTable.getBoolean("USE_BOSS_SYSTEM", true);
            USE_BRAVE_AVATAR = codeTable.getBoolean("USE_BRAVE_AVATAR", true);
            STANDBY_SERVER = codeTable.getBoolean("STANDBY_SERVER", true);

            RESOLVENT_FAIL_PER = codeTable.getInteger("RESOLVENT_FAIL_PER", 10);
            ADEN_CANCEL_WAIT_TIME = codeTable.getInteger("ADEN_CANCEL_WAIT_TIME", 20);
            CRACKER_MAX_LEVEL = codeTable.getInteger("CRACKER_MAX_LEVEL", 80);
            ADEN_BOARD_NPC_ID = codeTable.getInteger("ADEN_BOARD_NPC_ID", 460000058);
            MAX_SELF_BUFF_LEVEL = codeTable.getInteger("MAX_SELF_BUFF_LEVEL", 75);

            NON_DROP_LIST = codeTable.getString("NON_DROP_LIST", "");
            EARTH_BIND_DURATION = codeTable.getString("EARTH_BIND_DURATION", "");

            TRIPLE_BALANCE = codeTable.getDouble("TRIPLE_BALANCE", 1.0);
            MAX_LEVEL_PANALTY = codeTable.getDouble("MAX_LEVEL_PANALTY", 0.005);

            MAX_LEVEL = codeTable.getInteger("MAX_LEVEL", 0);
            FISHING_EXP = codeTable.getInteger("FISHING_EXP", 0);

            ABANDONED_LAND_LEV = codeTable.getInteger("ABANDONED_LAND_LEV", 0);

            BLESS_ADD_DMG = codeTable.getInteger("BLESS_ADD_DMG", 0);
            BLESS_ADD_REDUC = codeTable.getInteger("BLESS_ADD_REDUC", 0);
            BLESS_ADD_HP = codeTable.getInteger("BLESS_ADD_HP", 0);
            BLESS_ADD_SP = codeTable.getInteger("BLESS_ADD_SP", 0);

            DRAGON_PAPOO_MIN = codeTable.getInteger("DRAGON_PAPOO_MIN", 0);
            DRAGON_PAPOO_MAX = codeTable.getInteger("DRAGON_PAPOO_MAX", 0);

            DRAGON_VALA_MIN = codeTable.getInteger("DRAGON_VALA_MIN", 0);
            DRAGON_VALA_MAX = codeTable.getInteger("DRAGON_VALA_MAX", 0);

            SAFE_MODE = codeTable.getBoolean("SAFE_MODE", false);
            WAR_MARK_ALL = codeTable.getBoolean("WAR_MARK_ALL", false);
            MAX_CLIENT_COUNT = codeTable.getInteger("MAX_CLIENT_COUNT", 300);
            ENCHANT_MENT = codeTable.getBoolean("ENCHANT_MENT", false);

            EXP_BONUS_AIN = codeTable.getDouble("EXP_BONUS_AIN", 1.33);
            EXP_BONUS_EME = codeTable.getDouble("EXP_BONUS_EME", 0.77);

            MAP_NORMAL_CHANGE = codeTable.getString("MAP_NORMAL_CHANGE", "");

            CLASSIC_DROP = codeTable.getString("CLASSIC_DROP", "");

            BOSS_HP_VALANCE1 = codeTable.getDouble("BOSS_HP_VALANCE1", 1.0);
            BOSS_HP_VALANCE2 = codeTable.getDouble("BOSS_HP_VALANCE2", 1.0);
            BOSS_HP_VALANCE3 = codeTable.getDouble("BOSS_HP_VALANCE3", 1.0);

            GRANG_KIN_ANGER_SYSTEM_USE = codeTable.getBoolean("GRANG_KIN_ANGER_SYSTEM_USE", false);

            RND_RIPER = codeTable.getInteger("RND_RIPER", 10);

            ADENA_CUSTOM_DROP_MAPID = codeTable.getString("ADENA_CUSTOM_DROP_MAPID", "");
            ADENA_CUSTOM_NON_DROP_MAPID = codeTable.getString("ADENA_CUSTOM_NON_DROP_MAPID", "");

            STACK_OPEN_BOX_ID = codeTable.getString("STACK_OPEN_BOX_ID", "");

            MOB_ESCAPE_DISTANCE = codeTable.getInteger("MOB_ESCAPE_DISTANCE", 25);

            ADEN_SELL_MIN = codeTable.getInteger("ADEN_SELL_MIN", 5000000);

            RATE_XP = codeTable.getInteger("RATE_XP", 1000);
            RATE_DROP_ADENA = codeTable.getInteger("RATE_DROP_ADENA", 20);
            RATE_DROP_ITEMS = codeTable.getInteger("RATE_DROP_ITEMS", 1);

            RATE_CRISTAL = codeTable.getInteger("RATE_CRISTAL", 1);
            RATE_LAWFUL = codeTable.getInteger("RATE_LAWFUL", 1);
            RATE_KARMA = codeTable.getInteger("RATE_KARMA", 1);

            RATE_WEIGHT_LIMIT = codeTable.getDouble("RATE_WEIGHT_LIMIT", 1.5);
            RATE_WEIGHT_LIMIT_PET = codeTable.getDouble("RATE_WEIGHT_LIMIT_PET", 1.5);
            CLAN_MEMBER_MAX_COUNT = codeTable.getInteger("CLAN_MEMBER_MAX_COUNT", 20);

            NEXT_REQ_START = codeTable.getBoolean("NEXT_REQ_START", false);

            CHECK_HASTE_RATE = codeTable.getDouble("CHECK_HASTE_RATE", 0.0);
            CHECK_BRAVE_RATE = codeTable.getDouble("CHECK_BRAVE_RATE", 0.0);
            CHECK_WAFFLE_RATE = codeTable.getDouble("CHECK_WAFFLE_RATE", 0.0);
            CHECK_DRGON_PER_RATE = codeTable.getDouble("CHECK_DRGON_PER_RATE", 0.0);
            CHECK_MOVE_RATE = codeTable.getDouble("CHECK_MOVE_RATE", 0.0);
            CHECK_NO_POLY_RATE = codeTable.getDouble("CHECK_NO_POLY_RATE", 0.0);

            PC_RECOGNIZE_RANGE = codeTable.getInteger("PC_RECOGNIZE_RANGE", -1);

            ADEN_BOARD_CLICK_MSG = codeTable.getString("ADEN_BOARD_CLICK_MSG", "");
            NEXT_REQ_SERVER_STATE = codeTable.getInteger("NEXT_REQ_SERVER_STATE", 0);

            MAX_HP_PRINCE = codeTable.getInteger("MAX_HP_PRINCE", 0);
            MAX_MP_PRINCE = codeTable.getInteger("MAX_MP_PRINCE", 0);

            MAX_HP_KNIGHT = codeTable.getInteger("MAX_HP_KNIGHT", 0);
            MAX_MP_KNIGHT = codeTable.getInteger("MAX_MP_KNIGHT", 0);

            MAX_HP_ELF = codeTable.getInteger("MAX_HP_ELF", 0);
            MAX_MP_ELF = codeTable.getInteger("MAX_MP_ELF", 0);

            MAX_HP_WIZARD = codeTable.getInteger("MAX_HP_WIZARD", 0);
            MAX_MP_WIZARD = codeTable.getInteger("MAX_MP_WIZARD", 0);

            MAX_HP_DARKELF = codeTable.getInteger("MAX_HP_DARKELF", 0);
            MAX_MP_DARKELF = codeTable.getInteger("MAX_MP_DARKELF", 0);

            MAX_HP_DRAGON_KNIGHT = codeTable.getInteger("MAX_HP_DRAGON_KNIGHT", 0);
            MAX_MP_DRAGON_KNIGHT = codeTable.getInteger("MAX_MP_DRAGON_KNIGHT", 0);

            MAX_HP_ILLUSIONIST = codeTable.getInteger("MAX_HP_ILLUSIONIST", 0);
            MAX_MP_ILLUSIONIST = codeTable.getInteger("MAX_MP_ILLUSIONIST", 0);

            AUTO_CREATE_ACCOUNTS = codeTable.getBoolean("AUTO_CREATE_ACCOUNTS", true);

            SKILL_TIMER_IMPL_TYPE = codeTable.getInteger("SKILL_TIMER_IMPL_TYPE", 1);
            CHARACTER_CONFIG_IN_SERVER_SIDE = codeTable.getBoolean("CHARACTER_CONFIG_IN_SERVER_SIDE", true);
            LEVEL_DOWN_RANGE = codeTable.getInteger("LEVEL_DOWN_RANGE", 0);

            CACHE_MAP_FILES = codeTable.getBoolean("CACHE_MAP_FILES", true);

            GLOBAL_CHAT_LEVEL = codeTable.getInteger("GLOBAL_CHAT_LEVEL", 1);
            WHISPER_CHAT_LEVEL = codeTable.getInteger("WHISPER_CHAT_LEVEL", 1);

            DROP_LEVEL_LIMIT = codeTable.getInteger("DROP_LEVEL_LIMIT", 1);

            CHANGE_TITLE_BY_ONESELF = codeTable.getBoolean("CHANGE_TITLE_BY_ONESELF", true);
            CLAN_ALLIANCE = codeTable.getBoolean("CLAN_ALLIANCE", true);

            MAX_PARTY_NUMBER = codeTable.getInteger("MAX_PARTY_NUMBER", 8);
            MAX_CHAT_PARTY_NUMBER = codeTable.getInteger("MAX_CHAT_PARTY_NUMBER", 8);

            WAR_PENALTY = codeTable.getBoolean("WAR_PENALTY", true);

            ALT_ITEM_DELETION_TYPE = codeTable.getString("ALT_ITEM_DELETION_TYPE", "std");

            SPAWN_HOME_POINT = codeTable.getBoolean("SPAWN_HOME_POINT", true);
            SPAWN_HOME_POINT_COUNT = codeTable.getInteger("SPAWN_HOME_POINT_COUNT", 8);
            SPAWN_HOME_POINT_DELAY = codeTable.getInteger("SPAWN_HOME_POINT_DELAY", 2);
            SPAWN_HOME_POINT_RANGE = codeTable.getInteger("SPAWN_HOME_POINT_RANGE", 5);

            MAX_DOLL_COUNT = codeTable.getInteger("MAX_DOLL_COUNT", 8);
            RETURN_TO_NATURE = codeTable.getBoolean("RETURN_TO_NATURE", true);
            MAX_NPC_ITEM = codeTable.getInteger("MAX_NPC_ITEM", 8);

            MAX_PERSONAL_WAREHOUSE_ITEM = codeTable.getInteger("MAX_PERSONAL_WAREHOUSE_ITEM", 8);
            MAX_CLAN_WAREHOUSE_ITEM = codeTable.getInteger("MAX_CLAN_WAREHOUSE_ITEM", 8);
            DELETE_CHARACTER_AFTER_7DAYS = codeTable.getBoolean("DELETE_CHARACTER_AFTER_7DAYS", true);
            GM_CODE = codeTable.getInteger("GM_CODE", 9999);

            MANA_DRAIN_LIMIT_PER_SOM_ATTACK = codeTable.getInteger("MANA_DRAIN_LIMIT_PER_SOM_ATTACK", 8);

            MAGIC_DMG_VALANCE = codeTable.getDouble("MAGIC_DMG_VALANCE", 1.0);
            MAGIC_DMG_REDUCE_BY_MR = codeTable.getDouble("MAGIC_DMG_REDUCE_BY_MR", 1.0);

            AUTO_ATTACK_DELAY = codeTable.getDouble("AUTO_ATTACK_DELAY", 1.0);
            ASTAR_SEARCH_TAIL_COUNT = codeTable.getInteger("ASTAR_SEARCH_TAIL_COUNT", 20);
            ASTAR_NODE_CHECK_COUNT = codeTable.getInteger("ASTAR_NODE_CHECK_COUNT", 20);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public static List<Short> getMapNormalChangeList() {
        List<Short> result = new ArrayList<>();

        String[] s = MAP_NORMAL_CHANGE.split(",");

        for (String o : s) {
            result.add(Short.valueOf(o));
        }

        return result;
    }

    public static List<Integer> CLASSIC_DROP_LIST() {
        List<Integer> result = new ArrayList<>();
        String[] s = CLASSIC_DROP.split(",");

        for (String o : s) {
            result.add(Integer.valueOf(o));
        }

        return result;
    }

    public static List<Integer> earthBindDuration() {
        return getIntegers(EARTH_BIND_DURATION);
    }

    private static List<Integer> getIntegers(String str) {
        List<Integer> result = new ArrayList<>();

        if (str.isEmpty()) {
            return result;
        }

        String[] s = str.split(",");

        for (String o : s) {
            result.add(Integer.valueOf(o));
        }

        return result;
    }

    public static List<Integer> adenaCustomDropMapIds() {
        return getIntegers(ADENA_CUSTOM_DROP_MAPID);
    }

    public static List<Integer> adenaCustomNonDropMapIds() {
        return getIntegers(ADENA_CUSTOM_NON_DROP_MAPID);
    }

    public static String STACK_OPEN_BOX_ID_LIST_STR() {
        List<String> list = new ArrayList<>();
        for (int k : STACK_OPEN_BOX_ID_LIST()) {
            L1Item item = ItemTable.getInstance().findItem(k);
            list.add(item.getName());
        }

        return StringUtils.join(list, ",");
    }

    public static List<Integer> STACK_OPEN_BOX_ID_LIST() {
        return getIntegers(STACK_OPEN_BOX_ID);
    }

    public static List<Integer> nonDropList() {
        String[] strings = NON_DROP_LIST.split(",");

        List<Integer> itemIdList = new ArrayList<>();

        for (String s : strings) {
            try {
                itemIdList.add(Integer.valueOf(s));
            } catch (Exception e) {
                logger.error(e);
            }
        }

        return itemIdList;
    }

    public static String getAdenaClickMent() {
        int k = ADEN_SELL_MIN / 10000;

        return String.format("등록랩제 : %d 아데나 %s만 이상 등록 [%s]", CodeConfig.ADEN_SELL_MIN_LEVEL, NumberFormat.getInstance().format(k), CodeConfig.ADEN_BOARD_CLICK_MSG);
    }
}
