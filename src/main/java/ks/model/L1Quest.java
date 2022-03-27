package ks.model;

import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.Map;

public class L1Quest {
    public static final int QUEST_TUTOR = 3005;//초보자도우미
    public static final int QUEST_LEVEL15 = 1;
    public static final int QUEST_LEVEL30 = 2;
    public static final int QUEST_LEVEL45 = 3;
    public static final int QUEST_LEVEL50 = 4;
    public static final int QUEST_LEVEL70 = 5;
    public static final int QUEST_FIRSTQUEST = 40; // ## A70 말하는 두루마리 퀘스트 추가
    public static final int QUEST_LYRA = 10;
    public static final int QUEST_OILSKINMANT = 11;
    public static final int QUEST_RUBA = 21;
    public static final int QUEST_LUKEIN1 = 23;
    public static final int QUEST_TBOX1 = 24;
    public static final int QUEST_TBOX2 = 25;
    public static final int QUEST_TBOX3 = 26;
    public static final int QUEST_RESTA = 30;
    public static final int QUEST_CADMUS = 31;
    public static final int QUEST_KAMYLA = 32;
    public static final int QUEST_LIZARD = 34;
    public static final int QUEST_DESIRE = 36;
    public static final int QUEST_SHADOWS = 37;
    public static final int QUEST_ROI = 38;
    public static final int QUEST_MOONBOW = 39;
    public static final int QUEST_KARIF = 41;
    public static final int QUEST_ICEQUEENRING = 42;
    public static final int QUEST_SNAP_RARING = 64;
    public static final int QUEST_END = 255;
    private final L1PcInstance pc;

    private Map<Integer, Integer> quest = null;

    public L1Quest(L1PcInstance pc) {
        this.pc = pc;
    }

    public L1PcInstance getPc() {
        return pc;
    }

    public int getStep(int questId) {
        if (quest == null) {
            quest = new HashMap<>();

            SqlUtils.query("SELECT * FROM character_quests WHERE char_id=?", (rs, i) -> {
                quest.put(rs.getInt(2), rs.getInt(3));
                return null;
            }, pc.getId());
        }

        Integer step = quest.get(questId);

        if (step == null) {
            return 0;
        } else {
            return step;
        }
    }

    public void setStep(int quest_id, int step) {
        if (quest.get(quest_id) == null) {
            SqlUtils.update("INSERT INTO character_quests SET char_id = ?, quest_id = ?, quest_step = ?",
                    pc.getId(),
                    quest_id,
                    step
            );
        } else {
            SqlUtils.update("UPDATE character_quests SET quest_step = ? WHERE char_id = ? AND quest_id = ?",
                    step,
                    pc.getId(),
                    quest_id

            );
        }

        quest.put(quest_id, step);
    }

    public void setEnd(int quest_id) {
        setStep(quest_id, QUEST_END);
    }

    public boolean isEnd(int quest_id) {
        return getStep(quest_id) == QUEST_END;
    }

    public void checkQuest() {
        int lv15_step = getStep(L1Quest.QUEST_LEVEL15);

        int level = pc.getLevel();
        int type = pc.getType();

        if (level >= 15 && lv15_step != L1Quest.QUEST_END) {
            switch (type) {
                case 0:
                case 4:
                case 3:
                case 2:
                case 1:
                    setEnd(L1Quest.QUEST_LEVEL15);
                    break;
            }
        }

        int lv30_step = getStep(L1Quest.QUEST_LEVEL30);

        if (level >= 30 && lv30_step != L1Quest.QUEST_END) {
            switch (type) {
                case 0:
                case 4:
                case 3:
                case 2:
                case 1:
                    setEnd(L1Quest.QUEST_LEVEL30);
                    break;
            }
        }

        int lv45_step = getStep(L1Quest.QUEST_LEVEL45);

        if (level >= 45 && lv45_step != L1Quest.QUEST_END) {
            switch (type) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    setEnd(L1Quest.QUEST_LEVEL45);
                    break;
            }
        }

        int lv50_step = getStep(L1Quest.QUEST_LEVEL50);
        if (level >= 50 && lv50_step != L1Quest.QUEST_END) {
            switch (type) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    setEnd(L1Quest.QUEST_LEVEL50);
                    break;
            }
        }
    }
}
