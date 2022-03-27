package ks.core.datatables.weaponSkill;

public class ItemSkill {
    public static final int SKILL_TYPE_NORMAL = 0;
    public static final int SKILL_TYPE_ATTACK_SKILL = 1;
    public static final int SKILL_TYPE_MAGIC = 2;

    private int itemId;
    private String itemName;
    private int effectId;
    private int probability;

    private int enchantProbability;
    private int enchantProbabilityStart;

    private String dmgType;
    private int dmgMin;
    private int dmgMax;
    private int skillId;
    private int skillTime;
    private int skillType;
    private int skillStartEnchant;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public int getDmgMin() {
        return dmgMin;
    }

    public void setDmgMin(int dmgMin) {
        this.dmgMin = dmgMin;
    }

    public int getDmgMax() {
        return dmgMax;
    }

    public void setDmgMax(int dmgMax) {
        this.dmgMax = dmgMax;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getSkillTime() {
        return skillTime;
    }

    public void setSkillTime(int skillTime) {
        this.skillTime = skillTime;
    }

    public int getSkillType() {
        return skillType;
    }

    public void setSkillType(int skillType) {
        this.skillType = skillType;
    }

    public int getEnchantProbability() {
        return enchantProbability;
    }

    public void setEnchantProbability(int enchantProbability) {
        this.enchantProbability = enchantProbability;
    }

    public int getEnchantProbabilityStart() {
        return enchantProbabilityStart;
    }

    public void setEnchantProbabilityStart(int enchantProbabilityStart) {
        this.enchantProbabilityStart = enchantProbabilityStart;
    }

    public String getDmgType() {
        return dmgType;
    }

    public void setDmgType(String dmgType) {
        this.dmgType = dmgType;
    }

    public int getSkillStartEnchant() {
        return skillStartEnchant;
    }

    public void setSkillStartEnchant(int skillStartEnchant) {
        this.skillStartEnchant = skillStartEnchant;
    }
}
