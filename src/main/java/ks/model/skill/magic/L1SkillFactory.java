package ks.model.skill.magic;

import ks.model.skill.magic.skills.*;

import static ks.constants.L1SkillId.*;

public class L1SkillFactory {
    public static L1Skill create(int skillId) {
        L1Skill runSkill = null;

        switch (skillId) {
            case LIGHT:
                runSkill = new L1SkillLight(skillId);
                break;
            case HASTE:
            case GREATER_HASTE: {
                runSkill = new L1SkillHaste(skillId);
                break;
            }
            case CURE_POISON: {
                runSkill = new L1SkillCurePoison(skillId);
                break;
            }
            case REMOVE_CURSE: {
                runSkill = new L1SkillRemoveCure(skillId);
                break;
            }
            case RESURRECTION:
            case GREATER_RESURRECTION: {
                runSkill = new L1SkillResurrection(skillId);
                break;
            }
            case CALL_OF_NATURE: {
                runSkill = new L1SkillCallOfNature(skillId);
                break;
            }
            case DETECTION: {
                runSkill = new L1SkillDetection(skillId);
                break;
            }
            case UNCANNY_DODGE: {
                runSkill = new L1SkillUnCannyDodge(skillId);
                break;
            }
            case COUNTER_DETECTION: {
                runSkill = new L1SkillCounterDetection(skillId);
                break;
            }
            case TRUE_TARGET: {
                runSkill = new L1SkillTrueTarget(skillId);
                break;
            }
            case ELEMENTAL_FALL_DOWN: {
                runSkill = new L1SkillElementalFallDown(skillId);
                break;
            }
            case CHILL_TOUCH: // 칠터치
            case VAMPIRIC_TOUCH: {// 뱀파
                runSkill = new L1SkillHpDrain(skillId);
                break;
            }
            case TRIPLE_ARROW: { // 트리플애로우
                runSkill = new L1SkillTripleArrow(skillId);
                break;
            }
            case 10026:
            case 10027:
            case 10028:
            case 10029: {
                runSkill = new L1SkillDragonChat(skillId);
                break;
            }

            case 10057: {
                runSkill = new L1SkillRecall(skillId);
                break;
            }
            case DRAKE_MASSTELEPORT: {
                runSkill = new L1SkillDrakeMassTeleport(skillId);
                break;
            }
            case SLOW: //슬로우
            case GRATE_SLOW://매스슬로우
            case ENTANGLE://인탱글
            case MOB_SLOW_1://몹 슬로우
            case MOB_SLOW_18: {
                runSkill = new L1SkillSlow(skillId);
                break;
            }
            case CURSE_BLIND: //커스블라인드
            case DARKNESS: { //다크니스
                runSkill = new L1SkillDarkNess(skillId);
                break;
            }
            case CURSE_POISON: { //커스포이즌
                runSkill = new L1SkillCursePoison(skillId);
                break;
            }

            case CURSE_PARALYZE2:
            case MOB_CURSEPARALYZ_18:
            case MOB_CURSEPARALYZ_19: {
                runSkill = new L1SkillNpcCurseParalyze(skillId);
                break;
            }

            case WEAKNESS:
            case MOB_WEAKNESS_1: {
                runSkill = new L1SkillWeakNess(skillId);
                break;
            }

            case DISEASE:
            case MOB_DISEASE_30: {
                runSkill = new L1SkillDisease(skillId);
                break;
            }

            case ICE_LANCE:
            case FREEZING_BLIZZARD: {
                runSkill = new L1SkillFreezeIce(skillId);
                break;
            }
            case SILENCE:
            case AREA_OF_SILENCE:
                runSkill = new L1SkillSilence(skillId);
                break;
            case THUNDER_GRAB:
                runSkill = new L1SkillThunderGrab(skillId);
                break;
            case EARTH_BIND:
            case MOB_BASILL:
            case MOB_COCA: {
                runSkill = new L1SkillFreeze(skillId);
                break;
            }
            case BONE_BREAK:
            case EMPIRE:
            case SHOCK_STUN:
            case MOB_RANGESTUN_30:
            case MOB_RANGESTUN_19:
            case MOB_RANGESTUN_18:
            case MOB_STUN_1: {
                runSkill = new L1SkillShockStun(skillId);
                break;
            }
            case PHANTASM:
            case FOG_OF_SLEEPING: {
                runSkill = new L1SkillOfSleeping(skillId);
                break;
            }
            case DRAKE_WIND_SHACKLE:
            case WIND_SHACKLE:
            case P_WIND_SHACKLE: {
                runSkill = new L1SkillWindShackle(skillId);
                break;
            }
            case ARMOR_BRAKE:
                runSkill = new L1SkillArmorBrake(skillId);
                break;
            case MOB_RANGE_ERASE_MAGIC:
            case MOB_RANGE_GRIM_RIPER1:
            case MOB_RANGE_GRIM_RIPER2:
                runSkill = new L1SkillMobRange(skillId);
                break;
            case MOB_RANGE_CANCELLATION:
            case CANCELLATION: {
                runSkill = new L1SkillCancellation(skillId);
                break;
            }
            case TURN_UNDEAD: {
                runSkill = new L1SkillTurnUndead(skillId);
                break;
            }
            case MANA_DRAIN: {
                runSkill = new L1SkillManaDrain(skillId);
                break;
            }
            case WEAPON_BREAK: {
                runSkill = new L1SkillWeaponBreak(skillId);
                break;
            }
            case COUNTER_MAGIC: {
                runSkill = new L1SkillCounterMagic(skillId);
                break;
            }
            case TELEPORT:
            case MASS_TELEPORT: {
                runSkill = new L1SkillTeleport(skillId);
                break;
            }
            case TELEPORT_TO_MOTHER: {
                runSkill = new L1SkillTeleportToMother(skillId);
                break;
            }
            case CALL_CLAN: {
                runSkill = new L1SkillCallClan(skillId);
                break;
            }
            case RUN_CLAN: {
                runSkill = new L1SkillRunClan(skillId);
                break;
            }
            case CREATE_MAGICAL_WEAPON: {
                runSkill = new L1SkillCreateMagicalWeapon(skillId);
                break;
            }
            case BRING_STONE: {
                runSkill = new L1SkillBringStone(skillId);
                break;
            }
            case SUMMON_LESSER_ELEMENTAL:
            case GREATER_ELEMENTAL: {
                runSkill = new L1SkillSummonElemental(skillId);
            }
            break;
            case SUMMON_MONSTER: {
                runSkill = new L1SkillSummonMonster(skillId);
                break;
            }
            case ABSOLUTE_BARRIER: {
                runSkill = new L1SkillAbsoluteBarrier(skillId);
                break;
            }
            case GLOWING_WEAPON:
            case GLOWING_WEAPON_SINGLE: {
                runSkill = new L1SkillGlowingWeapon(skillId);
                break;
            }

            case DRESS_DEXTERITY: {
                runSkill = new L1SkillDressDexterity(skillId);
                break;
            }

            case SHINING_SHILELD:
            case SHINING_SHILELD_SINGLE: {
                runSkill = new L1SkillShiningShield(skillId);
                break;
            }
            case BRAVE_MENTAL:
            case BRAVE_MENTAL_SINGLE: {
                runSkill = new L1SkillBraveMental(skillId);
                break;
            }
            case SHIELD: {
                runSkill = new L1SkillShield(skillId);
                break;
            }
            case SHADOW_ARMOR: {
                runSkill = new L1SkillShadowArmor(skillId);
                break;
            }
            case BUFF_COIN: {
                runSkill = new L1SkillBuffCoin(skillId);
                break;
            }
            case DRESS_MIGHTY: {
                runSkill = new L1SkillDressMight(skillId);
                break;
            }
            case SHADOW_FANG:
            case ENCHANT_WEAPON: {
                runSkill = new L1SkillEnchantWeapon(skillId);
                break;
            }
            case HOLY_WEAPON:
            case BLESS_WEAPON: {
                runSkill = new L1SkillBlessWeapon(skillId);
                break;
            }
            case BLESSED_ARMOR: {
                runSkill = new L1SkillBlessedArmor(skillId);
                break;
            }
            case EARTH_GUARDIAN: {
                runSkill = new L1SkillEarthGuardian(skillId);
                break;
            }
            case RESIST_MAGIC: {
                runSkill = new L1SkillResistMagic(skillId);
                break;
            }
            case CLEAR_MIND: {
                runSkill = new L1SkillClearMind(skillId);
                break;
            }
            case RESIST_ELEMENTAL: {
                runSkill = new L1SkillResistElemental(skillId);
                break;
            }
            case BODY_TO_MIND: {
                runSkill = new L1SkillBodyToMind(skillId);
                break;
            }
            case BLOODY_SOUL: {
                runSkill = new L1SkillBloodSoul(skillId);
                break;
            }
            case ELEMENTAL_PROTECTION: {
                runSkill = new L1SkillElementalProtection(skillId);
                break;
            }
            case INVISIBILITY:
            case BLIND_HIDING: {
                runSkill = new L1SkillInvisibility(skillId);
                break;
            }
            case IRON_SKIN: {
                runSkill = new L1SkillIronSkin(skillId);
                break;
            }
            case EARTH_SKIN: {
                runSkill = new L1SkillEarthSkin(skillId);
                break;
            }
            case PHYSICAL_ENCHANT_STR: {
                runSkill = new L1SkillEnchantStr(skillId);
                break;
            }
            case PHYSICAL_ENCHANT_DEX: {
                runSkill = new L1SkillEnchantDex(skillId);
                break;
            }
            case FIRE_WEAPON: {
                runSkill = new L1SkillFireWeapon(skillId);
                break;
            }
            case DANCING_BLADES: {
                runSkill = new L1SkillDancingBlade(skillId);
                break;
            }
            case BURNING_WEAPON: {
                runSkill = new L1SkillBurningWeapon(skillId);
                break;
            }
            case WIND_SHOT: {
                runSkill = new L1SkillWindShot(skillId);
                break;
            }
            case EYE_OF_STORM: {
                runSkill = new L1SkillEyeOfStorm(skillId);
                break;
            }
            case STORM_SHOT: {
                runSkill = new L1SkillStormShot(skillId);
                break;
            }
            case BERSERKERS: {
                runSkill = new L1SkillBerserkers(skillId);
                break;
            }

            case STATUS_COMA_3:
            case STATUS_COMA_5: {
                runSkill = new L1SkillStatusComa(skillId);
                break;
            }
            case LIND_MAAN: // 마안풍룡
            case FAFU_MAAN: // 마안수룡
            case ANTA_MAAN: // 마안지룡
            case VALA_MAAN: // 마안화룡
            case BIRTH_MAAN: // 마안탄생
            case SHAPE_MAAN: // 마안형상
            case LIFE_MAAN: {// 마안생명
                runSkill = new L1SkillMaan(skillId);
                break;
            }
            case IllUSION_OGRE:
                runSkill = new L1SkillIllusionOrge(skillId);
                break;
            case MIND_BREAK:
                runSkill = new L1SkillMindBreak(skillId);

                break;

            case STATUS_CASHSCROLL1:
            case STATUS_CASHSCROLL2:
            case STATUS_CASHSCROLL3: {
                runSkill = new L1SkillCashScroll(skillId);
                break;
            }

            case BOUNCE_ATTACK: { // 바운스어택
                runSkill = new L1SkillBounceAttack(skillId);
                break;
            }
            case SANGA: {
                runSkill = new L1SkillSanga(skillId);
                break;
            }
            case SANGA_BUFF: {
                runSkill = new L1SkillSangaBuff(skillId);
                break;
            }
            case BUFF_SAEL:
            case BUFF_CRAY: {
                runSkill = new L1SkillBuffCray(skillId);
                break;
            }
            case SHAPE_CHANGE: {
                runSkill = new L1SkillShapeChange(skillId);
                break;
            }
            case ADVANCE_SPIRIT: {
                runSkill = new L1SkillAdvanceSpirit(skillId);
                break;
            }
            case HOLY_WALK:
            case MOVING_ACCELERATION:
            case WIND_WALK: {
                runSkill = new L1SkillThirdSpeed(skillId);
                break;
            }
            case STATUS_LUCK_A:
            case STATUS_LUCK_B:
            case STATUS_LUCK_C:
            case STATUS_LUCK_D: {
                runSkill = new L1SkillLuck(skillId);
                break;
            }
            case STATUS_HUNT: {
                runSkill = new L1SkillStatusHunt(skillId);
                break;
            }
            case TAMING_MONSTER: {
                runSkill = new L1SkillTamingMonster(skillId);
                break;
            }
            case CREATE_ZOMBIE: {
                runSkill = new L1SkillCreateZombie(skillId);
                break;
            }
            case WEAK_ELEMENTAL: {
                runSkill = new L1SkillWeakElemental(skillId);
                break;
            }
            case RETURN_TO_NATURE: {
                runSkill = new L1SkillReturnToNature(skillId);
                break;
            }
            case GUARD_BREAK: {
                runSkill = new L1SkillGuardBreak(skillId);
                break;
            }
            case SCALES_EARTH_DRAGON: {
                runSkill = new L1SkillScalesEarthDragon(skillId);
                break;
            }
            case SCALES_WATER_DRAGON: {
                runSkill = new L1SkillScalesWaterDragon(skillId);
                break;
            }
            case BLOOD_LUST: {
                runSkill = new L1SkillBloodLust(skillId);
                break;
            }
            case FOU_SLAYER: {
                runSkill = new L1SkillFouSlayer(skillId);
                break;
            }
            case FEAR: {
                runSkill = new L1SkillFear(skillId);
                break;
            }
            case PANIC: {
                runSkill = new L1SkillPanic(skillId);
                break;
            }
            case HORROR_OF_DEATH: {
                runSkill = new L1SkillHorrorOfDeath(skillId);
                break;
            }
            case SCALES_FIRE_DRAGON: {
                runSkill = new L1SkillScalesFireDragon(skillId);
                break;
            }
            case CONCENTRATION:
                runSkill = new L1SkillConcentration(skillId);
                break;
            case IllUSION_LICH:
                runSkill = new L1SkillIllusionLich(skillId);
                break;
            case MIRROR_IMAGE:
                runSkill = new L1SkillMirrorImage(skillId);
                break;
            case CONFUSION:
                break;
            case IllUSION_AVATAR:
                runSkill = new L1SkillIllusionAvatar(skillId);
                break;
            case IllUSION_DIAMOND_GOLEM:
                runSkill = new L1SkillDiamondGolem(skillId);
                break;
        }

        return runSkill;
    }
}
