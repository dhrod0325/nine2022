package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.constants.L1ItemTypes;
import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1WeaponAddDamage;
import ks.model.instance.L1ItemInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeaponDamageTable {
    private static Logger logger = LogManager.getLogger();

    private final List<L1WeaponAddDamage> normalList = new ArrayList<>();
    private final List<L1WeaponAddDamage> detailList = new ArrayList<>();

    public static WeaponDamageTable getInstance() {
        return LineageAppContext.getBean(WeaponDamageTable.class);
    }

    public void load() {
        normalList.clear();
        normalList.addAll(selectListNoraml());

        detailList.clear();
        detailList.addAll(selectListDetail());
    }

    public List<L1WeaponAddDamage> selectListNoraml() {
        String sql = "SELECT * FROM weapon_add_damage_normal";
        List<L1WeaponAddDamage> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1WeaponAddDamage.class));

        for (L1WeaponAddDamage o : list) {
            o.setWeaponType(L1ItemTypes.weaponTypes.get(o.getType()));
        }

        return list;
    }

    public List<L1WeaponAddDamage> selectListDetail() {
        String sql = "SELECT * FROM weapon_add_damage_detail";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1WeaponAddDamage.class));
    }

    public L1WeaponAddDamage getDetail(int weaponId, int enchantLevel) {
        for (L1WeaponAddDamage o : detailList) {
            if (o.getWeaponId() == weaponId && o.getEnchant() == enchantLevel) {
                return o;
            }
        }

        return null;
    }

    public int getNormalValue(L1Character character, L1ItemInstance item) {
        int result = 0;

        L1WeaponAddDamage d = getNormal(item);

        if (d != null) {
            if (character.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SOUL_OF_FLAME)) {
                result = d.getMax();
            } else {
                result = d.getValue();
            }
        }

        logger.debug("lormalValue : {}", result);

        return result;
    }

    public L1WeaponAddDamage getNormal(L1ItemInstance is) {
        if (is == null) {
            return null;
        }

        L1Item item = is.getItem();

        int grade = item.getGrade();
        int enchantLevel = is.getEnchantLevel();
        int type = item.getType();

        int dmg = calcDmg(grade, enchantLevel, type);

        if (dmg == 0) {
            return null;
        }

        int min = dmg / 2;
        int max = dmg * 2;

        L1WeaponAddDamage addDamage = new L1WeaponAddDamage();
        addDamage.setMin(min);
        addDamage.setMax(max);
        addDamage.setWeaponType(type);
        addDamage.setGrade(grade);

        return addDamage;
        /*
        for (L1WeaponAddDamage o : normalList) {
            if (is == null) {
                continue;
            }

            if (item.getSafeEnchant() == 0) {
                if (enchantLevel >= 11) {
                    enchantLevel = 11;
                } else if (enchantLevel >= 9) {
                    enchantLevel = 10;
                } else if (enchantLevel >= 7) {
                    enchantLevel = 9;
                } else if (enchantLevel >= 5) {
                    enchantLevel = 8;
                } else if (enchantLevel >= 3) {
                    enchantLevel = 7;
                }
            }

            if (item.getType() == 11) {
                type = 12;
            } else if (item.getType() == 13) {
                type = 4;
            }

            if (o.getGrade() == grade
                    && o.getEnchant() == enchantLevel
                    && o.getWeaponType() == type) {

                return o;
            }
        }
         */
//        return null;
    }

    private int calcDmg(int grade, int enchant, int weaponType) {
        //기준 대미지 양손검
        int dmg = 0;

        switch (grade) {
            case 10001:
                switch (enchant) {
                    case 7:
                        dmg = 2;
                        break;
                    case 8:
                        dmg = 3;
                        break;
                    case 9:
                        dmg = 4;
                        break;
                    case 10:
                        dmg = 5;
                        break;
                    case 11:
                        dmg = 6;
                        break;
                }
                break;
            case 10002:
                switch (enchant) {
                    case 7:
                        dmg = 3;
                        break;
                    case 8:
                        dmg = 5;
                        break;
                    case 9:
                        dmg = 7;
                        break;
                    case 10:
                        dmg = 9;
                        break;
                    case 11:
                        dmg = 11;
                        break;
                }
                break;
            case 10003:
                switch (enchant) {
                    case 7:
                        dmg = 5;
                        break;
                    case 8:
                        dmg = 8;
                        break;
                    case 9:
                        dmg = 11;
                        break;
                    case 10:
                        dmg = 14;
                        break;
                    case 11:
                        dmg = 17;
                        break;
                }
                break;
            case 10004:
                switch (enchant) {
                    case 7:
                        dmg = 7;
                        break;
                    case 8:
                        dmg = 11;
                        break;
                    case 9:
                        dmg = 15;
                        break;
                    case 10:
                        dmg = 19;
                        break;
                    case 11:
                        dmg = 23;
                        break;
                }
                break;
            case 10005:
                switch (enchant) {
                    case 7:
                        dmg = 10;
                        break;
                    case 8:
                        dmg = 15;
                        break;
                    case 9:
                        dmg = 20;
                        break;
                    case 10:
                        dmg = 25;
                        break;
                    case 11:
                        dmg = 30;
                        break;
                }

                break;
        }

        switch (weaponType) {
            case 3://양손검
            case 12:
                dmg *= 1.5;
                break;
            case 1://한손검
            case 4://활
                dmg *= 1;
                break;
        }

        return dmg;
    }
}
