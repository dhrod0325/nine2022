package ks.constants;

import static ks.constants.L1ItemId.*;

public class L1NpcConstants {
    public static final int MOVE_SPEED = 0;
    public static final int ATTACK_SPEED = 1;
    public static final int MAGIC_SPEED = 2;
    public static final int HIDDEN_STATUS_NONE = 0;
    public static final int HIDDEN_STATUS_SINK = 1;
    public static final int HIDDEN_STATUS_FLY = 2;
    public static final int CHAT_TIMING_APPEARANCE = 0;
    public static final int CHAT_TIMING_DEAD = 1;
    public static final int CHAT_TIMING_HIDE = 2;
    public static final int CHAT_TIMING_GAME_TIME = 3;
    public static final int USEITEM_HEAL = 0;
    public static final int USEITEM_HASTE = 1;
    public static final long DEFAULT_DELETE_TIME = 1000 * 10;

    public static int[][] classGfxId = {
            {0, 1}, {48, 61}, {37, 138}, {734, 1186}, {2786, 2796}, {6658, 6661}, {6671, 6650}
    };

    public static int[] HEAL_POTIONS = {
            POTION_OF_GREATER_HEALING,
            POTION_OF_EXTRA_HEALING,
            POTION_OF_HEALING
    };

    public static int[] HASTE_POTIONS = {
            B_POTION_OF_GREATER_HASTE_SELF,
            POTION_OF_GREATER_HASTE_SELF,
            B_POTION_OF_HASTE_SELF,
            POTION_OF_HASTE_SELF
    };
}
