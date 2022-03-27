package ks.model.attack.utils;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1Attrs;
import ks.constants.L1Types;
import ks.core.datatables.WeaponDamageTable;
import ks.core.datatables.weaponSkill.ItemSkillTable;
import ks.model.L1Character;
import ks.model.L1WeaponAddDamage;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.pc.L1PcInstance;
import ks.model.poison.L1DamagePoison;
import ks.util.common.NumberUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1WeaponUtils {
    private static final Logger logger = LogManager.getLogger();

    public static double stingDamage(L1PcInstance pc) {
        L1ItemInstance sting = pc.getInventory().getSting();

        if (sting == null)
            return 0;

        int dmg = 0;

        int add_dmg = sting.getItem().getDmgSmall();
        if (add_dmg == 0) {
            add_dmg = 1;
        }

        dmg += RandomUtils.nextInt(add_dmg) + 1;

        return dmg;
    }

    public static double arrowDamage(L1PcInstance pc) {
        if (pc.getWeapon() == null)
            return 0;

        int dmg = 0;

        if (pc.getWeapon().getItem().getType1() == 20) {
            int weaponId = pc.getWeapon().getItemId();

            if (pc.getInventory().getArrow() != null) {
                int addDmg = pc.getInventory().getArrow().getItem().getDmgSmall();

                if (addDmg == 0) {
                    addDmg = 1;
                }

                dmg += RandomUtils.nextInt(addDmg) + 1;
            } else if (weaponId == 190 || (weaponId >= 11011 && weaponId <= 11013)) {
                dmg += RandomUtils.nextInt(15) + 1;
            }
        }

        //logger.debug("화살대미지 : {} ", dmg);

        return dmg;
    }

    public static double weaponSkillDamage(L1PcInstance attacker, L1Character target) {
        L1ItemInstance weapon = attacker.getWeapon();

        if (weapon == null)
            return 0;

        int weaponId = weapon.getItemId();
        int weaponEnchant = weapon.getEnchantLevel();

        int dmg = 0;

        int dmgBySkill = ItemSkillTable.getInstance().findDmg(attacker.getWeapon(), attacker, target);

        if (dmgBySkill > 0) {
            dmg += dmgBySkill;
        } else {
            switch (weaponId) {
                case 2://악운의단검
                case 200002:
                    if (target instanceof L1PcInstance) {
                        dmg += L1WeaponSkills.악운의단검(attacker, (L1PcInstance) target, weapon);
                    }

                    break;
                case 13:
                case 44:
                    if (RandomUtils.isWinning(100, weaponEnchant * 2)) {
                        L1DamagePoison.doInfection(attacker, target, 1000 * 2, weaponEnchant * 3, 7);
                    }
                    break;
                case 126:
                case 127:
                case 134:
                case 45000623:
                    int maxDrainMp;

                    if (target instanceof L1PcInstance) {
                        maxDrainMp = 2;
                    } else if (target instanceof L1MonsterInstance) {
                        L1MonsterInstance mi = (L1MonsterInstance) target;

                        boolean isBoss = mi.isBoss();

                        if (weaponId == 134) {
                            maxDrainMp = 8;
                        } else if (isBoss) {
                            maxDrainMp = 2;
                        } else {
                            maxDrainMp = weaponEnchant / 2;
                        }
                    } else {
                        maxDrainMp = 2;
                    }

                    if (maxDrainMp < 0) {
                        maxDrainMp = 0;
                    }

                    if (maxDrainMp > 0) {
                        int drainMana = RandomUtils.nextInt(maxDrainMp) + 1;
                        attacker.setDrainMp(drainMana);
                        L1LogUtils.gmLog(attacker, "drainMp : {}", drainMana);
                    }

                    if (attacker.getDrainMp() > CodeConfig.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
                        attacker.setDrainMp(CodeConfig.MANA_DRAIN_LIMIT_PER_SOM_ATTACK);
                    }

                    if (weaponId == 134) {
                        dmg += L1WeaponSkills.crystalStaff(attacker, target, weapon);
                    }

                    break;
                case 203://앨리스8
                    dmg += L1WeaponSkills.alice(attacker, target);
                    break;
                case 413105:
                case 413104:
                case 413103:
                case 413102:
                case 413101:
                case 412003:
                    dmg += L1WeaponSkills.disease(attacker, target, weaponEnchant);
                    break;
                case 415010:
                case 415011:
                case 415012:
                case 415013:
                    L1WeaponSkills.테베무기(attacker, target, weapon, 6985);
                    break;
                case 415015://쿠쿨칸의 건틀렛
                case 415016:
                    L1WeaponSkills.테베무기(attacker, target, weapon, 7179);
                    break;
            }
        }

        if (dmg > 0) {
            logger.debug("무기스킬 대미지 : {}", dmg);
        }

        return dmg;
    }

    public static int calcWeaponTypeDamage(L1PcInstance pc) {
        int weaponType = pc.getWeaponInfo().getWeaponType();

        int dmg = 0;

        if (weaponType == L1Types.TYPE1_WEAPON_TYPE_BOW) {
            dmg += L1WeaponUtils.arrowDamage(pc);
        } else if (weaponType == L1Types.TYPE1_WEAPON_TYPE_GAUNTLET) {
            dmg += L1WeaponUtils.stingDamage(pc);
        }

        return dmg;
    }

    public static double gradeAndEnchantDamage(L1PcInstance pc) {
        double result;

        int weaponId = pc.getWeaponInfo().getWeaponId();
        int weaponEnchant = pc.getWeaponInfo().getWeaponEnchant();

        L1WeaponAddDamage detail = WeaponDamageTable.getInstance().getDetail(weaponId, weaponEnchant);

        if (detail != null) {
            result = detail.getValue();
        } else {
            result = WeaponDamageTable.getInstance().getNormalValue(pc, pc.getWeapon());
        }

        return result;
    }

    public static String getWeakAttrString(int weakAttr) {
        if (weakAttr == L1Attrs.ATTR_WIND) {
            return "바람";
        } else if (weakAttr == L1Attrs.ATTR_FIRE) {
            return "불";
        } else if (weakAttr == L1Attrs.ATTR_EARTH) {
            return "땅";
        } else if (weakAttr == L1Attrs.ATTR_WATER) {
            return "물";
        }

        return "없음";
    }

    public static int getWeaponAttr(int attrLevel) {
        if (attrLevel >= 1 && attrLevel <= 5) {
            return L1Attrs.ATTR_FIRE;
        } else if (attrLevel >= 6 && attrLevel <= 10) {
            return L1Attrs.ATTR_WATER;
        } else if (attrLevel >= 11 && attrLevel <= 15) {
            return L1Attrs.ATTR_WIND;
        } else if (attrLevel >= 16 && attrLevel <= 20) {
            return L1Attrs.ATTR_EARTH;
        }

        return 0;
    }

    public static int getWeaponAttrLevelGrade(int attrLevel) {
        if (NumberUtils.contains(attrLevel, 1, 6, 11, 16)) {
            return 1;
        } else if (NumberUtils.contains(attrLevel, 2, 7, 12, 17)) {
            return 2;
        } else if (NumberUtils.contains(attrLevel, 3, 8, 13, 18)) {
            return 3;
        } else if (NumberUtils.contains(attrLevel, 4, 9, 14, 19)) {
            return 4;
        } else if (NumberUtils.contains(attrLevel, 5, 10, 15, 20)) {
            return 5;
        }

        return 0;
    }

    public static int getWeaponAttrDamage(int attrLevel) {
        switch (getWeaponAttrLevelGrade(attrLevel)) {
            case 1:
                return 1;
            case 2:
                return 3;
            case 3:
                return 5;
            case 4:
                return 7;
            case 5:
                return 9;
        }

        return 0;
    }
}
