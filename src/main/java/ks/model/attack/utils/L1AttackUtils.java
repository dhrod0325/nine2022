package ks.model.attack.utils;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.poison.L1DamagePoison;
import ks.model.poison.L1ParalysisPoison;
import ks.model.poison.L1SilencePoison;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_SkillSound;
import ks.scheduler.timer.gametime.GameTimeScheduler;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1AttackUtils {
    private static final Logger logger = LogManager.getLogger();

    public static int getWeaponMaxDamage(L1PcInstance pc, L1Character target) {
        if (pc.getWeapon() == null) {
            return 5;
        }

        if (target instanceof L1NpcInstance) {
            L1NpcInstance c = (L1NpcInstance) target;
            if ("large".equals(c.getTemplate().getSize())) {
                return pc.getWeapon().getItem().getDmgLarge();
            } else {
                return pc.getWeapon().getItem().getDmgSmall();
            }
        } else {
            return pc.getWeapon().getItem().getDmgSmall();
        }
    }

    //ER에 따른 회피율
    public static boolean isMissByEr(L1Character target) {
        int er = target.getEr();
        return !RandomUtils.isWinning(CodeConfig.ER_RANDOM_VALUE, er);
    }

    //AC에 따른 대미지 감소
    public static int reduceDamageByAc(L1Character target) {
        int ac = -target.getAC().getAc();
        return (int) (ac * CodeConfig.REDUCE_PC_DMG_BY_AC);
    }

    public static int missByUncannyDodge(int ac) {
        int dodChance = 0;

        if (ac >= 130) {
            dodChance = CodeConfig.DG_AC_130;
        } else if (ac >= 125) {
            dodChance = CodeConfig.DG_AC_125;
        } else if (ac >= 120) {
            dodChance = CodeConfig.DG_AC_120;
        } else if (ac >= 115) {
            dodChance = CodeConfig.DG_AC_115;
        } else if (ac >= 110) {
            dodChance = CodeConfig.DG_AC_110;
        } else if (ac >= 105) {
            dodChance = CodeConfig.DG_AC_105;
        } else if (ac >= 100) {
            dodChance = CodeConfig.DG_AC_100;
        } else if (ac >= 95) {
            dodChance = CodeConfig.DG_AC_95;
        } else if (ac >= 90) {
            dodChance = CodeConfig.DG_AC_90;
        } else if (ac >= 85) {
            dodChance = CodeConfig.DG_AC_85;
        } else if (ac >= 80) {
            dodChance = CodeConfig.DG_AC_80;
        } else if (ac >= 75) {
            dodChance = CodeConfig.DG_AC_75;
        } else if (ac >= 70) {
            dodChance = CodeConfig.DG_AC_70;
        } else if (ac >= 65) {
            dodChance = CodeConfig.DG_AC_65;
        } else if (ac >= 60) {
            dodChance = CodeConfig.DG_AC_60;
        } else if (ac >= 55) {
            dodChance = CodeConfig.DG_AC_55;
        } else if (ac >= 50) {
            dodChance = CodeConfig.DG_AC_50;
        } else if (ac >= 45) {
            dodChance = CodeConfig.DG_AC_45;
        } else if (ac >= 40) {
            dodChance = CodeConfig.DG_AC_40;
        } else if (ac >= 35) {
            dodChance = CodeConfig.DG_AC_35;
        } else if (ac >= 30) {
            dodChance = CodeConfig.DG_AC_30;
        } else if (ac >= 25) {
            dodChance = CodeConfig.DG_AC_25;
        }

        return dodChance;
    }

    public static int missByUncannyDodge(L1Character character) {
        if (character.getSkillEffectTimerSet().hasSkillEffect(UNCANNY_DODGE)) {
            int ac = -character.getAC().getAc();
            return missByUncannyDodge(ac);
        } else {
            return 0;
        }
    }

    public static boolean isNotAttackAbleByTargetStatus(L1Character target) {
        if (target == null) {
            return false;
        }

        return target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER, EARTH_BIND)
                || target.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS);
    }

    public static void poisonAttack(L1Character attacker, L1Character target) {
        if (target == null)
            return;

        int chance = RandomUtils.nextInt(100) + 1;

        L1ItemInstance weapon = attacker.getWeapon();

        if (weapon == null)
            return;

        int weaponId = weapon.getItem().getItemId();

        if (weaponId == 0 || !attacker.getSkillEffectTimerSet().hasSkillEffect(ENCHANT_VENOM)) {
            return;
        }

        L1Skills skill = SkillsTable.getInstance().getTemplate(ENCHANT_VENOM);

        if (skill == null)
            return;

        int probability = skill.getProbabilityValue() + L1SkillUtils.calcProbability(attacker.getLevel() - target.getLevel(), 5);

        if (chance <= probability) {
            L1DamagePoison.doInfection(attacker, target, 1000 * 2, skill.getRandomDiceDamage(), 7);
        }
    }

    public static void addNpcPoisonAttack(L1NpcInstance npc, L1Character target) {
        if (npc.getTemplate().getPoisonAtk() != 0) {
            if (target instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) target;

                if (targetPc.getResistance().getMr() > 80) {
                    return;
                }
            }

            int per = npc.getLevel() / 4;

            if (per >= RandomUtils.nextInt(100) + 1) {
                if (npc.getTemplate().getPoisonAtk() == 1) {
                    L1DamagePoison.doInfection(npc, target, 3000, 5);
                } else if (npc.getTemplate().getPoisonAtk() == 2) {
                    L1SilencePoison.doInfection(target);
                } else if (npc.getTemplate().getPoisonAtk() == 4) { // 마비독
                    L1ParalysisPoison.doInfection(target, 1000 * 20, 1000 * 30);
                }
            }
        }
    }

    public static void missAttack(L1Character attacker, L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance target = (L1PcInstance) targetCharacter;

            if (attacker instanceof L1PcInstance) {
                if (!(L1CharPosUtils.isSafeZone(targetCharacter) || L1CharPosUtils.isSafeZone(target))) {
                    target.sendPackets(new S_SkillSound(target.getId(), 13418));
                }
            } else if (attacker instanceof L1NpcInstance) {
                target.sendPackets(new S_SkillSound(target.getId(), 13418));
            }
        }
    }

    public static void missAttack(L1Character character) {
        if (character instanceof L1PcInstance) {
            L1PcInstance target = (L1PcInstance) character;

            if (!(L1CharPosUtils.isSafeZone(character) || L1CharPosUtils.isSafeZone(target))) {
                target.sendPackets(new S_SkillSound(target.getId(), 13418));
            }
        }
    }

    public static boolean isNotHitAble(L1Character attacker, L1Character target) {
        if (attacker == null || target == null)
            return true;

        if (target.getAC() == null) {
            return true;
        }

        if (!attacker.getLocation().isInScreen(target.getLocation())) {
            return true;
        }

        if (isNotAttackAbleByTargetStatus(target)) {
            return true;
        }

        if (target instanceof L1SummonInstance || target instanceof L1PetInstance) {
            if (L1CharPosUtils.isSafeZone(target)) {
                return true;
            }
        }

        if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;

            int npcId = npc.getTemplate().getNpcId();

            if (npcId == 45941) {
                if (!attacker.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
                    return true;
                }
            } else if ((npcId == 45752 || npcId == 45753)) {
                if (!attacker.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_BARLOG)) {
                    return true;
                }
            } else if ((npcId == 45675 || npcId == 81082 || npcId == 45625 || npcId == 45674 || npcId == 45685)) {
                if (!attacker.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_YAHEE)) {
                    return true;
                }
            } else if (npcId == 81158) {
                boolean check1 = !L1CharPosUtils.glanceCheck(attacker, target.getX(), target.getY());
                boolean check2 = !L1CharPosUtils.glanceCheck(attacker, target.getX() + 1, target.getY());
                boolean check3 = !L1CharPosUtils.glanceCheck(attacker, target.getX() + 2, target.getY());
                boolean check4 = !L1CharPosUtils.glanceCheck(attacker, target.getX() + 3, target.getY());

                if (check1 && check2 && check3 && check4) {
                    return true;
                }
            }
        }

        if (attacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) attacker;
            int weaponType = pc.getWeaponInfo().getWeaponType();

            if (pc.getWeaponInfo().isLongAttack() && pc.getInventory().getArrow() == null) {
                return true;
            } else if (weaponType == 62 && pc.getInventory().getSting() == null) {
                return true;
            }
        }

        return !L1CharPosUtils.glanceCheck(attacker, target);
    }

    public static boolean isUndeadDamage(L1NpcInstance npc) {
        boolean flag = false;

        int undead = npc.getTemplate().getUndead();

        boolean isNight = GameTimeScheduler.getInstance().getTime().isNight();

        if (isNight && (undead == 1 || undead == 3 || undead == 4)) {
            flag = true;
        }

        return flag;
    }

    public static boolean isNotAttackAbleByPos(L1Character attacker, L1Character target) {
        return L1CharPosUtils.isSafeZone(attacker) || L1CharPosUtils.isSafeZone(target);
    }

    public static boolean isAttackAbleGhost(L1Character attacker, L1Character target) {
        if (attacker instanceof L1PcInstance) {
            if (target instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) target;

                int npcId = npc.getNpcId();

                if (npcId >= 45912 && npcId <= 45916) {
                    if (npcId == 45916) {
                        if (!attacker.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
                            return false;
                        }
                    } else {
                        if (!attacker.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HOLY_WATER)) {
                            return false;
                        }
                    }

                    if (!GameTimeScheduler.getInstance().getTime().isNight()) {
                        return false;
                    }

                    if (!(attacker.getX() > 32597 && attacker.getY() > 32650 && attacker.getX() < 32634 && attacker.getY() < 32689 && attacker.getMapId() == 4)) {
                        return false;
                    }
                }
            }
        }


        return true;
    }

}
