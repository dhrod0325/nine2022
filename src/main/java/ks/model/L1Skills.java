package ks.model;

import ks.constants.L1SkillId;
import ks.model.pc.L1PcInstance;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1Skills {
    private final Logger logger = LogManager.getLogger();

    public static final int TYPE_PROBABILITY = 1;

    public static final int TYPE_CHANGE = 2;

    public static final int TYPE_CURSE = 4;

    public static final int TYPE_DEATH = 8;

    public static final int TYPE_HEAL = 16;

    public static final int TYPE_RESTORE = 32;

    public static final int TYPE_ATTACK = 64;

    public static final int TYPE_OTHER = 128;

    public static final int TARGET_TO_ME = 0;

    public static final int TARGET_TO_PC = 1;

    public static final int TARGET_TO_NPC = 2;

    public static final int TARGET_TO_CLAN = 4;

    public static final int TARGET_TO_PARTY = 8;

    public static final int TARGET_TO_PET = 16;

    public static final int TARGET_TO_PLACE = 32;

    private String name;

    private String target;

    private String nameId;

    private boolean isThrough;

    private int skillId;

    private int skillLevel;

    private int skillNumber;

    private int damageValue;

    private int damageDice;

    private int damageDiceCount;

    private int probabilityValue;

    private int probabilityDice;

    private int attr;

    private int type;

    private int ranged;

    private int area;

    private int id;

    private int actionId;

    private int castGfx;

    private int castGfx2;

    private int sysmsgIdHappen;

    private int systemMsgIdStop;

    private int systemMsgIdFail;

    private int lawful;

    private int mpConsume;

    private int hpConsume;

    private int itemConsumeId;

    private int itemConsumeCount;

    private int reuseDelay;

    private int buffDuration;

    private int maxDmg;
    private int _targetTo; // 대상 0:자신 1:PC 2:NPC 4:혈맹 8:파티 16:펫 32:장소

    public int getMaxDmg() {
        return maxDmg;
    }

    public void setMaxDmg(int maxDmg) {
        this.maxDmg = maxDmg;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int i) {
        skillId = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int i) {
        skillLevel = i;
    }

    public int getSkillNumber() {
        return skillNumber;
    }

    public void setSkillNumber(int i) {
        skillNumber = i;
    }

    public int getMpConsume() {
        return mpConsume;
    }

    public void setMpConsume(int i) {
        mpConsume = i;
    }

    public int getHpConsume() {
        return hpConsume;
    }

    public void setHpConsume(int i) {
        hpConsume = i;
    }

    public int getItemConsumeId() {
        return itemConsumeId;
    }

    public void setItemConsumeId(int i) {
        itemConsumeId = i;
    }

    public int getItemConsumeCount() {
        return itemConsumeCount;
    }

    public void setItemConsumeCount(int i) {
        itemConsumeCount = i;
    }

    public int getReuseDelay() {
        return reuseDelay;
    }

    public void setReuseDelay(int i) {
        reuseDelay = i;
    }

    public int getBuffDuration() {
        return buffDuration;
    }

    public void setBuffDuration(int i) {
        buffDuration = i;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String s) {
        target = s;
    }

    public int getTargetTo() {
        return _targetTo;
    }

    public void setTargetTo(int i) {
        _targetTo = i;
    }

    public int getDamageValue() {
        return damageValue;
    }

    public void setDamageValue(int i) {
        damageValue = i;
    }

    public int getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(int i) {
        damageDice = i;
    }

    public int getDamageDiceCount() {
        return damageDiceCount;
    }

    public void setDamageDiceCount(int i) {
        damageDiceCount = i;
    }

    public int getProbabilityValue() {
        return probabilityValue;
    }

    public void setProbabilityValue(int i) {
        probabilityValue = i;
    }

    public int getProbabilityDice() {
        return probabilityDice;
    }

    public void setProbabilityDice(int i) {
        probabilityDice = i;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int i) {
        attr = i;
    }

    public int getType() {
        return type;
    }

    public void setType(int i) {
        type = i;
    }

    public int getLawful() {
        return lawful;
    }

    public void setLawful(int i) {
        lawful = i;
    }

    public int getRanged() {
        return ranged;
    }

    public void setRanged(int i) {
        ranged = i;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int i) {
        area = i;
    }

    public boolean isThrough() {
        return isThrough;
    }

    public void setThrough(boolean flag) {
        isThrough = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String s) {
        nameId = s;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int i) {
        actionId = i;
    }

    public int getCastGfx() {
        return castGfx;
    }

    public void setCastGfx(int i) {
        castGfx = i;
    }

    public int getCastGfx2() {
        return castGfx2;
    }

    public void setCastGfx2(int i) {
        castGfx2 = i;
    }

    public int getSysmsgIdHappen() {
        return sysmsgIdHappen;
    }

    public void setSysmsgIdHappen(int i) {
        sysmsgIdHappen = i;
    }

    public int getSysmsgIdStop() {
        return systemMsgIdStop;
    }

    public void setSysmsgIdStop(int i) {
        systemMsgIdStop = i;
    }

    public int getSystemMsgIdFail() {
        return systemMsgIdFail;
    }

    public void setSystemMsgIdFail(int i) {
        systemMsgIdFail = i;
    }

    public int getRandomDiceChance() {
        int value = 0;
        int diceCount = getProbabilityDice();
        int probability = getProbabilityValue();

        for (int i = 0; i < diceCount; i++) {
            value += probability;
        }

        return value;
    }

    public int getRandomDiceDamage() {
        try {
            int dice = getDamageDice();
            int diceCount = getDamageDiceCount();
            int value = getDamageValue();
            int magicDamage = 0;

            for (int i = 0; i < diceCount; i++) {
                magicDamage += RandomUtils.nextInt(dice) + 1;
            }

            magicDamage += value;

            return magicDamage;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return 0;
    }

    public int calcConsumeMp(L1Character character) {
        int currentMp = character.getCurrentMp();
        int mpConsume = getMpConsume();

        int totalInt = character.getAbility().getTotalInt();

        if (totalInt > 12 && (skillId > HOLY_WEAPON && skillId <= FREEZING_BLIZZARD)) {
            mpConsume--;
        }

        if (totalInt > 13 && skillId > STALAC && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 14 && skillId > WEAK_ELEMENTAL && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 15 && skillId > MEDITATION && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 16 && skillId > DARKNESS && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 17 && skillId > BLESS_WEAPON && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 18 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 19 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 20 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 21 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 22 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 23 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }
        if (totalInt > 24 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }

        if (totalInt > 25 && skillId > DISEASE && skillId <= FREEZING_BLIZZARD) {
            mpConsume--;
        }

        if (totalInt > 12 && skillId >= SHOCK_STUN && skillId <= COUNTER_BARRIER) {
            mpConsume -= (totalInt - 12);
        }

        if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;

            if (pc.getInventory().checkEquipped(20013)) {
                if (skillId == PHYSICAL_ENCHANT_DEX || skillId == HASTE) {
                    mpConsume /= 2;
                }
            }

            if (pc.getInventory().checkEquipped(20014)) {
                if (skillId == HEAL || skillId == EXTRA_HEAL) {
                    mpConsume /= 2;
                }
            }

            if (pc.getInventory().checkEquipped(20015)) {
                if (skillId == ENCHANT_WEAPON || skillId == DETECTION) {
                    mpConsume /= 2;
                }
            }

            if (pc.getInventory().checkEquipped(20023)) {
                if (skillId == GREATER_HASTE) {
                    mpConsume /= 2;
                }
            }

            if (skillId == HASTE) {
                if (pc.getInventory().checkEquipped(20008)) {
                    mpConsume /= 2;
                }
            }
        }

        if (0 < getMpConsume()) {
            mpConsume = Math.max(mpConsume, 1);
        }

        if (currentMp < mpConsume) {
            return -1;
        }

        return mpConsume;
    }

    public int calcConsumeHp(L1Character user) {
        int currentHp = user.getCurrentHp();
        int hpConsume = getHpConsume();

        if (currentHp < hpConsume + 1) {
            return -1;
        }

        return hpConsume;
    }

    public boolean isTargetAttackAndTypeAttack() {
        return isTargetAttack() || isAttack();
    }

    public boolean isTurnUndead() {
        return getSkillId() == L1SkillId.TURN_UNDEAD;
    }

    public boolean isTargetNone() {
        return getTarget().equals("none");
    }

    public boolean isTargetAttack() {
        return getTarget().equals("attack");
    }

    public boolean isAttack() {
        return getType() == TYPE_ATTACK;
    }

    public boolean isHeal() {
        return getType() == TYPE_HEAL;
    }

    public boolean isTypeCurse() {
        return getType() == TYPE_CURSE;
    }

    public boolean isTypeProbability() {
        return getType() == TYPE_PROBABILITY;
    }

    public boolean isProbability() {
        return isTypeCurse() || isTypeProbability();
    }
}
