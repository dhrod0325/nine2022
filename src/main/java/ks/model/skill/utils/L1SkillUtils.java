package ks.model.skill.utils;

import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.core.datatables.PolyTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.model.trap.L1WorldTraps;
import ks.packets.serverpackets.*;
import ks.util.L1CharPosUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ks.constants.L1SkillId.*;

public class L1SkillUtils {
    private static final List<Integer> CAST_WITH_INVIS = Arrays.asList(
            HEAL, LIGHT, SHIELD, TELEPORT, HOLY_WEAPON, CURE_POISON, ENCHANT_WEAPON, DETECTION,
            DECREASE_WEIGHT, EXTRA_HEAL, BLESSED_ARMOR, PHYSICAL_ENCHANT_DEX, COUNTER_MAGIC, MEDITATION,
            GREATER_HEAL, REMOVE_CURSE, PHYSICAL_ENCHANT_STR, HASTE, CANCELLATION, BLESS_WEAPON, HEAL_ALL, HOLY_WALK,
            54, 55, 57, 60, 61, 63, 67, 68, MASS_TELEPORT, 72, 73, 75, 78, ADVANCE_SPIRIT, REDUCTION_ARMOR,
            BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
            101, 102, 104, DOUBLE_BRAKE, 106, 107, 109, 110, DRESS_EVASION, 113, 114, 115, 116,
            117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
            150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
            171, 181, SOUL_OF_FLAME, ADDITIONAL_FIRE);

    private static final List<Integer> EXCEPT_COUNTER_MAGIC = Arrays.asList(
            1, 2, 3, 5, 8, 9, 12,
            13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, CANCELLATION, 48, 49, 52, 54, 55,
            57, 60, 61, 63, 67, 68, MASS_TELEPORT, 72, 73, 75, 78, ADVANCE_SPIRIT, SHOCK_STUN, EMPIRE,
            REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
            97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113,
            114, 115, 116, 117, 118, 129, 130, 131, 132, TRIPLE_ARROW, 134, 137, 138, 146,
            147, 148, 149, 150, 151, 155, 156, 158, 159, 163, 164, 165,
            166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN,
            FOU_SLAYER, SCALES_EARTH_DRAGON, SCALES_FIRE_DRAGON,
            SCALES_WATER_DRAGON, MIRROR_IMAGE, IllUSION_OGRE, PATIENCE,
            IllUSION_DIAMOND_GOLEM, IllUSION_LICH, IllUSION_AVATAR, INSIGHT,
            10026, 10027, 10029, 30060, 30000, 30078, 30079, 30011,
            30081, 30082, 30083, 30080, 30084, 30010, 30002, 30086, AREA_OF_SILENCE
    );

    private static final int[][] REPEATED_SKILLS = {
            {STATUS_ELFBRAVE, DANCING_BLADES},
            {FIRE_WEAPON, WIND_SHOT, EYE_OF_STORM, BURNING_WEAPON, STORM_SHOT},
            {SHIELD, SHADOW_ARMOR, EARTH_SKIN, IRON_SKIN},
            {HOLY_WALK, BLOOD_LUST, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, STATUS_ELFBRAVE},
            {HASTE, GREATER_HASTE, STATUS_HASTE},
            {PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY},
            {PHYSICAL_ENCHANT_STR, DRESS_MIGHTY},
            {GLOWING_WEAPON, SHINING_SHILELD, SHINING_SHILELD_SINGLE, GLOWING_WEAPON_SINGLE},
            {BRAVE_MENTAL, BRAVE_MENTAL_SINGLE},
            {FAFU_MAAN, ANTA_MAAN, LIND_MAAN, VALA_MAAN, LIFE_MAAN, BIRTH_MAAN, SHAPE_MAAN},
            {STATUS_COMA_3, STATUS_COMA_5},
            {STATUS_LUCK_A, STATUS_LUCK_B, STATUS_LUCK_C, STATUS_LUCK_D},
            {STATUS_CASHSCROLL1, STATUS_CASHSCROLL2, STATUS_CASHSCROLL3},
            {EXP_POTION1, EXP_POTION2, EXP_POTION3, EXP_POTION4},
            {SCALES_WATER_DRAGON, SCALES_FIRE_DRAGON, SCALES_EARTH_DRAGON},
            {FAFU_MAAN, ANTA_MAAN, LIND_MAAN, VALA_MAAN, LIFE_MAAN, BIRTH_MAAN, SHAPE_MAAN},// 마안
            {RANK_BUFF_1, RANK_BUFF_2, RANK_BUFF_3},
            {CLAN_BUFF1, CLAN_BUFF2, CLAN_BUFF3, CLAN_BUFF4, CLAN_BUFF5},
            {DECREASE_WEIGHT, REDUCE_WEIGHT}
    };

    public static final List<Integer> STUN_SKILLS = Arrays.asList(
            EMPIRE, SHOCK_STUN, BONE_BREAK, STATUS_SHOCK_STUN,
            MOB_STUN_1, MOB_RANGESTUN_30, MOB_RANGESTUN_19, MOB_RANGESTUN_18
    );

    public static final List<Integer> SLEEP_SKILLS = Arrays.asList(
            FOG_OF_SLEEPING,
            PHANTASM
    );

    public static final List<Integer> CURSE_SKILLS = Arrays.asList(
            STATUS_CURSE_PARALYZED,
            CURSE_PARALYZE2,
            STATUS_CURSE_PARALYZING,
            MOB_CURSEPARALYZ_18,
            MOB_CURSEPARALYZ_19
    );

    public static final List<Integer> ICE_SKILLS = Arrays.asList(
            ICE_LANCE,
            FREEZING_BLIZZARD,
            MOB_SKILL_FREEZE,
            MOB_BASILL,
            MOB_COCA
    );

    public static final List<Integer> NOT_ABLE_CANCEL_SKILLS = new ArrayList<>(Arrays.asList(
            ENCHANT_WEAPON, ABSOLUTE_BARRIER, BLESSED_ARMOR, ADVANCE_SPIRIT, SHADOW_FANG, REDUCTION_ARMOR, SOLID_CARRIAGE, COUNTER_BARRIER,
            ANTA_MAAN, FAFU_MAAN, LIND_MAAN, VALA_MAAN, BIRTH_MAAN, SHAPE_MAAN, LIFE_MAAN,
            STATUS_CASHSCROLL1, STATUS_CASHSCROLL2, STATUS_CASHSCROLL3,
            EXP_POTION1, EXP_POTION2, EXP_POTION3, EXP_POTION4,
            STATUS_DRAGON_EMERALD_YES, STATUS_HUNT,
            STATUS_LUCK_A, STATUS_LUCK_B, STATUS_LUCK_C, STATUS_LUCK_D,
            STATUS_BRAVE_AVATAR_1ST, STATUS_BRAVE_AVATAR_2ND, STATUS_BRAVE_AVATAR_3RD,
            RANK_BUFF_1, RANK_BUFF_2, RANK_BUFF_3,
            CLAN_BUFF1, CLAN_BUFF2, CLAN_BUFF3, CLAN_BUFF4, CLAN_BUFF5,
            SCALES_EARTH_DRAGON, SCALES_FIRE_DRAGON, SCALES_WATER_DRAGON
    ));

    static {
        NOT_ABLE_CANCEL_SKILLS.addAll(STUN_SKILLS);
    }

    public static void skillByType(L1PcInstance pc, int skillId, int skillTime, int skillType) {
        L1SkillUse skillUse = new L1SkillUse(pc, skillId, pc.getId(), pc.getX(), pc.getY(), skillTime, skillType);
        skillUse.run();
    }

    public static void skillByGm(L1PcInstance pc, int skillId, int skillTime) {
        skillByType(pc, skillId, skillTime, L1SkillUse.TYPE_GM_BUFF);
    }

    public static void skillByGm(L1PcInstance pc, int skillId) {
        skillByGm(pc, skillId, 0);
    }

    public static void skillByGm(L1PcInstance pc, Integer... skills) {
        for (int skillId : skills) {
            skillByGm(pc, skillId);
        }
    }

    public static void skillByLogin(L1PcInstance pc, int skillId, int skillTime) {
        skillByType(pc, skillId, skillTime, L1SkillUse.TYPE_LOGIN);
    }

    public static void skillByLogin(L1PcInstance pc, int skillId) {
        skillByLogin(pc, skillId, 0);
    }

    public static boolean isSpellScrollUsable(L1Character user, int skillId) {
        if (user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) user;

            if (pc.isParalyzed()) {
                return false;
            }

            return (!pc.isInvisible() && !pc.isInvisDelay()) || isInvisUsableSkill(skillId);
        }

        return false;
    }

    public static boolean isInvisUsableSkill(int skillId) {
        return CAST_WITH_INVIS.contains(skillId);
    }

    public static boolean isExceptCounterMagic(int skillId) {
        return EXCEPT_COUNTER_MAGIC.contains(skillId);
    }

    public static boolean isCounterMagic(L1Character cha, int skillId) {
        if (cha.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
            if (!isExceptCounterMagic(skillId)) {
                cha.getSkillEffectTimerSet().removeSkillEffect(COUNTER_MAGIC);
                return true;
            }
        }

        return false;
    }

    public static void deleteRepeatedSkills(L1Character cha, int skillId) {
        for (int[] skills : REPEATED_SKILLS) {
            for (int id : skills) {
                if (id == skillId) {
                    stopSkillList(cha, skills, skillId);
                }
            }
        }
    }

    public static void stopSkillList(L1Character cha, int[] repeatSkill, int skillId) {
        for (int sid : repeatSkill) {
            if (sid != skillId) {
                cha.getSkillEffectTimerSet().removeSkillEffect(sid);
            }
        }
    }

    public static void detection(L1Character character) {
        if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;

            if (!pc.isGmInvis() && pc.isInvisible()) {
                pc.delInvis();
                pc.beginInvisTimer();
            }

            List<L1PcInstance> list = L1World.getInstance().getVisiblePlayer(pc);

            for (L1PcInstance target : list) {
                if (!target.isGmInvis() && target.isInvisible()) {
                    target.delInvis();
                    target.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_INVISI_OFF);
                }
            }

            L1WorldTraps.getInstance().onDetection(pc);
        }
    }

    public static boolean isNotCancelable(int skillNum) {
        return NOT_ABLE_CANCEL_SKILLS.contains(skillNum);
    }

    public static void summonMonster(L1PcInstance pc, int level, int order) {
        int[] summonidList = null;
        int summoncost = 8;
        int levelRange = level * 4;

        switch (levelRange) {
            case 28:
                summonidList = new int[]{81210, 81211, 81212};
                break;
            case 32:
                summonidList = new int[]{81213, 81214, 81215};
                break;
            case 36:
                summonidList = new int[]{81216, 81217, 81218};
                break;
            case 40:
                summonidList = new int[]{81219, 81220, 81221};
                break;
            case 44:
                summonidList = new int[]{81222, 81223, 81224};
                break;
            case 48:
                summonidList = new int[]{81225, 81226, 81227};
                break;
            case 52:
                summonidList = new int[]{81228, 81229, 81230};
                break;
            case 56:
                summonidList = new int[]{81231, 81232, 81233};
                break;
            case 60:
                summonidList = new int[]{81234, 81235, 81236};
                break;
            case 64:
                summonidList = new int[]{81237};
                summoncost = 14;
                break;
            case 68:
                summonidList = new int[]{81238};
                summoncost = 42;
                break;
            case 72:
                summonidList = new int[]{81239, 81240};

                if (order == 0)
                    summoncost = 42;
                else
                    summoncost = 50;
                break;
        }

        if (summonidList == null)
            return;

        int summonId = summonidList[order];

        summon(pc, levelRange, summonId, summoncost);
    }

    public static void summon(L1PcInstance pc, int levelRange, int summonid, int summoncost) {
        if (pc.getLevel() < levelRange) {
            pc.sendPackets(new S_ServerMessage(743));
            return;
        }

        int petcost = 0;

        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (L1NpcInstance pet : petList) {
            petcost += pet.getPetCost();
        }

        if ((summonid == 81238 || summonid == 81239 || summonid == 81240) && petcost != 0) {
            pc.sendPackets(new S_CloseList(pc.getId()));
            return;
        }

        int charisma = pc.getAbility().getTotalCha() + 6 - petcost;
        int summoncount;

        if (levelRange <= 52) {
            summoncount = charisma / summoncost;
        } else if (levelRange == 56) {
            summoncount = charisma / (summoncost + 2);
        } else if (levelRange == 60) {
            summoncount = charisma / (summoncost + 4);
        } else if (levelRange == 64) {
            summoncount = charisma / (summoncost + 6);
        } else {
            summoncount = charisma / summoncost;
        }

        if (levelRange <= 52 && summoncount > 5) {
            summoncount = 5;
        } else if (levelRange == 56 && summoncount > 4) {
            summoncount = 4;
        } else if (levelRange == 60 && summoncount > 3) {
            summoncount = 3;
        } else if (levelRange == 64 && summoncount > 2) {
            summoncount = 2;
        }

        L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);

        for (int cnt = 0; cnt < summoncount; cnt++) {
            L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);

            if (summonid == 81238 || summonid == 81239 || summonid == 81240) {
                summon.setPetCost(pc.getAbility().getTotalCha() + 7);
            } else {
                if (levelRange <= 52)
                    summon.setPetCost(summoncost);
                else if (levelRange == 56)
                    summon.setPetCost(summoncost + 2);
                else if (levelRange == 60)
                    summon.setPetCost(summoncost + 4);
                else if (levelRange == 64)
                    summon.setPetCost(summoncost + 6);
                else
                    summoncount = charisma / summoncost;
            }
        }
    }

    public static boolean targetCheck(L1Character cha, int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (pc.isGmInvis()) {
                return false;
            }
        }

        if (cha instanceof L1DoorInstance) {
            if (cha.getMaxHp() == 0 || cha.getMaxHp() == 1) {
                return false;
            }
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)) {
            return false;
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
            if (skillId != CANCELLATION && skill.getType() != L1Skills.TYPE_CHANGE) {
                return false;
            }
        }

        if (cha.isInvisible()) {
            if (skillId == CANCELLATION) {
                return false;
            }
        }

        if (!(cha instanceof L1MonsterInstance) && (skillId == TAMING_MONSTER || skillId == CREATE_ZOMBIE)) {
            return false;
        }

        if (cha.isDead() && (skillId != CREATE_ZOMBIE && skillId != RESURRECTION && skillId != GREATER_RESURRECTION && skillId != CALL_OF_NATURE)) {
            return false;
        }

        if (!(cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) && !cha.isDead() && (skillId == CREATE_ZOMBIE || skillId == RESURRECTION || skillId == GREATER_RESURRECTION || skillId == CALL_OF_NATURE)) {
            return false;
        }

        if (cha instanceof L1NpcInstance) {
            int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
            if (hiddenStatus == L1NpcConstants.HIDDEN_STATUS_SINK) {
                return skillId == DETECTION || skillId == COUNTER_DETECTION;
            } else if (hiddenStatus == L1NpcConstants.HIDDEN_STATUS_FLY) {
                return false;
            }
        }

        return !skill.getTarget().equals("none")
                || skill.getType() != L1Skills.TYPE_ATTACK
                || (!(cha instanceof L1AuctionBoardInstance)
                && !(cha instanceof L1BoardInstance)
                && !(cha instanceof L1CrownInstance)
                && !(cha instanceof L1DwarfInstance)
                && !(cha instanceof L1EffectInstance)
                && !(cha instanceof L1FieldObjectInstance)
                && !(cha instanceof L1FurnitureInstance)
                && !(cha instanceof L1HousekeeperInstance)
                && !(cha instanceof L1MerchantInstance)
                && !(cha instanceof L1TeleporterInstance));
    }

    public static boolean isNormalSkillUsable(L1Character user, int skillId) {
        if (user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) user;

            if (pc.isParalyzed()) {
                return false;
            }

            if ((pc.isInvisible() || pc.isInvisDelay()) && !isInvisUsableSkill(skillId)) {
                return false;
            }

            if (pc.getInventory().isOverWeight82()) {
                pc.sendPackets(new S_ServerMessage(316));
                return false;
            }

            int castleId = L1CastleLocation.getCastleIdByArea(pc);

            if (castleId != 0) {
                if (skillId == MASS_TELEPORT
                        || skillId == EARTH_BIND
                        || skillId == FIRE_WALL
                        || skillId == ICE_LANCE
                        || skillId == BLIZZARD
                        || skillId == FREEZING_BLIZZARD
                        || skillId == AREA_OF_SILENCE
                        || skillId == ABSOLUTE_BARRIER) {
                    pc.sendPackets(new S_ChatPacket(pc, "공성존에서는 사용 할 수 없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                    return false;
                }
            }

            if (pc.getMap().isSafetyZone(pc.getLocation())) {
                if (skillId == CONFUSION || skillId == SMASH_ENERGY
                        || skillId == MIND_BREAK || skillId == BONE_BREAK
                        || skillId == CUBE_IGNITION || skillId == CUBE_QUAKE
                        || skillId == CUBE_SHOCK || skillId == CUBE_BALANCE
                        || skillId == PHANTASM) {
                    pc.sendPackets(new S_SystemMessage("이 곳에선 사용 할 수 없습니다."));
                    return false;
                }
            }

            if (pc.getMapId() == 1005 || pc.getMapId() == 1011) {
                if (skillId == MASS_TELEPORT || skillId == 116 || skillId == 118) {
                    pc.sendPackets(new S_SystemMessage("해당마법은 시전이 불가능합니다."));
                    return false;
                }
            }

            if (skillId == DANCING_BLADES) {
                if (pc.getWeapon() == null) {
                    pc.sendPackets(new S_SystemMessage("한손검을 착용해야 사용할 수 있습니다."));
                    return false;
                }

                if (pc.getWeapon().getItem().getType1() != 4 && pc.getWeapon().getItem().getType1() != 46) {
                    pc.sendPackets(new S_SystemMessage("한손검을 착용해야 사용할 수 있습니다."));
                    return false;
                }
            }

            if (skillId == FINAL_BURN) {
                if (pc.getCurrentHp() <= 100) {
                    return false;
                }
            }

            if (!isAttrAgrees(user, skillId)) {
                return false;
            }

            if (skillId == SHOCK_STUN) {
                if (pc.getWeapon().getItem().getType() != 3) {
                    return false;
                }
            }

            if (skillId == EMPIRE) {
                boolean isEqShield = false;
                for (L1ItemInstance item : pc.getEquipSlot().getEquipedItems()) {
                    if (item.getItem().getType() == 7) {
                        isEqShield = true;
                        break;
                    }
                }

                if (!isEqShield) {
                    pc.sendPackets("방패를 착용해야 사용할 수 있습니다");
                    return false;
                }
            }

            if (skillId == ELEMENTAL_PROTECTION && pc.getElfAttr() == 0) {
                pc.sendPackets(new S_ServerMessage(280));
                return false;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(SILENCE)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(AREA_OF_SILENCE)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_POISON_SILENCE)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(CONFUSION)) {
                if (skillId != EMPIRE && (skillId < SHOCK_STUN || skillId > COUNTER_BARRIER)) {
                    pc.sendPackets(new S_ServerMessage(285));
                    return false;
                }
            }

            if (pc.isInvisible() && skillId == CANCELLATION) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }

            int polyId = pc.getGfxId().getTempCharGfx();
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);

            if (poly != null && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285));
                return false;
            }
        } else if (user instanceof L1NpcInstance) {
            if (user.getSkillEffectTimerSet().hasSkillEffect(SILENCE)) {
                user.getSkillEffectTimerSet().killSkillEffectTimer(SILENCE);
                return false;
            }
        }

        return true;
    }

    public static boolean isAttrAgrees(L1Character user, int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
        int magicattr = skill.getAttr();

        if (user instanceof L1NpcInstance) {
            return true;
        }

        if (user instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) user;

            return (skill.getSkillLevel() < 17 || skill.getSkillLevel() > 22 || magicattr == 0) || (magicattr == pc.getElfAttr() || pc.isGm());
        }

        return false;
    }

    public static int calcProbability(int diffLevel, int diffProbability) {
        return calcProbability(diffLevel, diffProbability, 0);
    }

    public static int calcProbability(int diffLevel, int diffProbability, int oneLevelDiffProbability) {
        int result = 0;
        int levelBonus;

        int bonus1 = diffLevel * diffProbability + oneLevelDiffProbability;

        if (diffLevel <= 0 && oneLevelDiffProbability != 0) {
            if (diffLevel == 0) {
                levelBonus = bonus1;
            } else {
                levelBonus = diffLevel * diffProbability;
            }
        } else {
            levelBonus = diffLevel * diffProbability;

            if (levelBonus > 0) {
                if (bonus1 > levelBonus) {
                    levelBonus = bonus1 - diffProbability;
                }
            }
        }

        return result + levelBonus;
    }

    public static boolean isTargetCalc(L1Character user, L1Character target, int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        if (skill.getTarget().equals("attack") && skillId != TURN_UNDEAD) {
            if (isPcSummonPet(user, target)) {
                if (user instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) user;
                    if (pc.checkNonPvP()) {
                        return false;
                    }
                }

                if (L1CharPosUtils.isSafeZone(user) || L1CharPosUtils.isSafeZone(target)) {
                    return false;
                }
            }
        }

        if (skillId == FOG_OF_SLEEPING && user.getId() == target.getId()) {
            return false;
        }

        if (skillId == GRATE_SLOW) {
            if (user.getId() == target.getId()) {
                return false;
            }

            if (isPetMaster(user, target)) {
                return false;
            }
        }

        if (skillId == MASS_TELEPORT) {
            return user.getId() == target.getId();
        }

        return true;
    }

    public static boolean isPcSummonPet(L1Character cha, L1Character target) {
        if (cha instanceof L1PcInstance && target instanceof L1PcInstance) {
            return true;
        }

        if (cha instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) cha;

            if (summon.isExsistMaster()) {
                return true;
            }
        }

        return cha instanceof L1PetInstance;
    }


    public static void pinkName(L1Character user, L1Character target, L1Skills skill) {
        if (user instanceof L1PcInstance && target instanceof L1PcInstance) {

            if (skill.getTarget().equals("buff")
                    && L1CharPosUtils.isNormalZone(user)
                    && !L1CharPosUtils.isSafeZone(target)
            ) {
                if (skill.getType() == L1Skills.TYPE_HEAL || skill.getType() == L1Skills.TYPE_CHANGE || skill.getType() == L1Skills.TYPE_PROBABILITY) {
                    L1PcInstance pc = (L1PcInstance) target;
                    if (pc.isPinkName()) {
                        L1PinkName.onAction(pc, user);
                    }
                }
            }
        }
    }

    public static boolean isPetMaster(L1Character character, L1Character target) {
        if (target instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) target;
            return character.getId() == summon.getMaster().getId();
        } else if (target instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) target;
            return character.getId() == pet.getMaster().getId();
        }

        return false;
    }

    public static boolean isEraseMagic(int skillId) {
        List<Integer> checks = new ArrayList<>(Arrays.asList(ERASE_MAGIC, MOB_RANGE_ERASE_MAGIC));
        return checks.contains(skillId);
    }

    public static boolean hasEraseMagic(L1Character character) {
        return character.getSkillEffectTimerSet().hasSkillEffect(ERASE_MAGIC, MOB_RANGE_ERASE_MAGIC);
    }

    public static void removeEraseMagic(L1Character character) {
        if (hasEraseMagic(character)) {
            character.getSkillEffectTimerSet().removeSkillEffect(ERASE_MAGIC, MOB_RANGE_ERASE_MAGIC);
        }
    }

    public static void removeSleep(L1Character character) {
        if (character.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
            character.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
        }

        if (character.getSkillEffectTimerSet().hasSkillEffect(PHANTASM)) {
            character.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.PHANTASM);
        }
    }

    public static void removeCounterMagic(L1Character target) {
        if (target.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
            target.getSkillEffectTimerSet().removeSkillEffect(COUNTER_MAGIC);

            Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 10702));
            target.sendPackets(new S_SkillSound(target.getId(), 10702));
        }
    }
}
