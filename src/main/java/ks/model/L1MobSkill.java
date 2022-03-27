package ks.model;

public class L1MobSkill implements Cloneable {
    public static final int TYPE_NONE = 0;

    public static final int TYPE_PHYSICAL_ATTACK = 1;

    public static final int TYPE_MAGIC_ATTACK = 2;

    public static final int TYPE_SUMMON = 3;

    public static final int TYPE_POLY = 4;

    public static final int CHANGE_TARGET_ME = 2;

    public static final int CHANGE_TARGET_RANDOM = 3;

    private final int skillSize;
    private final int[] type;
    private final int[] triRnd;
    int[] triHp;
    int[] triCompanionHp;
    int[] triRange;
    int[] triCount;
    int[] changeTarget;
    int[] range;
    int[] areaWidth;
    int[] areaHeight;
    int[] leverage;
    int[] skillId;
    int[] gfxId;
    int[] actId;
    int[] summon;
    int[] summonMin;
    int[] summonMax;
    int[] polyId;

    int[] skillUseCount;
    private int mobId;
    private String mobName;

    public L1MobSkill(int sSize) {
        skillSize = sSize;

        type = new int[skillSize];
        triRnd = new int[skillSize];
        triHp = new int[skillSize];
        triCompanionHp = new int[skillSize];
        triRange = new int[skillSize];
        triCount = new int[skillSize];
        changeTarget = new int[skillSize];
        range = new int[skillSize];
        areaWidth = new int[skillSize];
        areaHeight = new int[skillSize];
        leverage = new int[skillSize];
        skillId = new int[skillSize];
        gfxId = new int[skillSize];
        actId = new int[skillSize];
        summon = new int[skillSize];
        summonMin = new int[skillSize];
        summonMax = new int[skillSize];
        polyId = new int[skillSize];
        skillUseCount = new int[skillSize];
    }

    public int getSkillUseCount(int idx) {
        return skillUseCount[idx];
    }

    public void skillUseCountUp(int idx) {
        skillUseCount[idx]++;
    }

    public void setSkillUseCount(int idx, int value) {
        skillUseCount[idx] = value;
    }

    @Override
    public L1MobSkill clone() {
        try {
            return (L1MobSkill) (super.clone());
        } catch (CloneNotSupportedException e) {
            throw (new InternalError(e.getMessage()));
        }
    }

    public int getSkillSize() {
        return skillSize;
    }

    public int getType(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return type[idx];
    }

    public void setType(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        type[idx] = i;
    }

    public int getTriggerRandom(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return triRnd[idx];
    }

    public void setTriggerRandom(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        triRnd[idx] = i;
    }

    public int getTriggerHp(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return triHp[idx];
    }

    public void setTriggerHp(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        triHp[idx] = i;
    }

    public int getTriggerCompanionHp(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return triCompanionHp[idx];
    }

    public void setTriggerCompanionHp(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        triCompanionHp[idx] = i;
    }

    public int getTriggerRange(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }

        return triRange[idx];
    }

    public void setTriggerRange(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        triRange[idx] = i;
    }

    public boolean isTriggerDistance(int idx, int distance) {
        int triggerRange = getTriggerRange(idx);

        return (triggerRange < 0 && distance <= Math.abs(triggerRange)) || (triggerRange > 0 && distance >= triggerRange);
    }

    public int getTriggerCount(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return triCount[idx];
    }

    public void setTriggerCount(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        triCount[idx] = i;
    }

    public int getChangeTarget(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }

        return changeTarget[idx];
    }

    public void setChangeTarget(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        changeTarget[idx] = i;
    }

    public int getRange(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return range[idx];
    }

    public void setRange(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        range[idx] = i;
    }

    public int getAreaWidth(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return areaWidth[idx];
    }

    public void setAreaWidth(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        areaWidth[idx] = i;
    }

    public int getAreaHeight(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return areaHeight[idx];
    }

    public void setAreaHeight(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        areaHeight[idx] = i;
    }

    public int getLeverage(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return leverage[idx];
    }

    public void setLeverage(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        leverage[idx] = i;
    }

    public int getSkillId(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }

        return skillId[idx];
    }

    public void setSkillId(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        skillId[idx] = i;
    }

    public int getGfxid(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return gfxId[idx];
    }

    public void setGfxid(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        gfxId[idx] = i;
    }

    public int getActid(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return actId[idx];
    }

    public void setActid(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        actId[idx] = i;
    }

    public int getSummon(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return summon[idx];
    }

    public void setSummon(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        summon[idx] = i;
    }

    public int getSummonMin(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return summonMin[idx];
    }

    public void setSummonMin(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        summonMin[idx] = i;
    }

    public int getSummonMax(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return summonMax[idx];
    }

    public void setSummonMax(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        summonMax[idx] = i;
    }

    public int getPolyId(int idx) {
        if (idx < 0 || idx >= getSkillSize()) {
            return 0;
        }
        return polyId[idx];
    }

    public void setPolyId(int idx, int i) {
        if (idx < 0 || idx >= getSkillSize()) {
            return;
        }
        polyId[idx] = i;
    }

    public int getMobId() {
        return mobId;
    }

    public void setMobId(int mobId) {
        this.mobId = mobId;
    }

    public String getMobName() {
        return mobName;
    }

    public void setMobName(String mobName) {
        this.mobName = mobName;
    }
}
