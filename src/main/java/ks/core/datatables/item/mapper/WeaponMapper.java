package ks.core.datatables.item.mapper;

import ks.constants.L1ItemTypes;
import ks.model.L1Weapon;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WeaponMapper implements RowMapper<L1Weapon> {
    @Override
    public L1Weapon mapRow(ResultSet rs, int i) throws SQLException {
        L1Weapon weapon = new L1Weapon();
        weapon.setItemId(rs.getInt("item_id"));
        weapon.setName(rs.getString("name"));
        weapon.setNameId(rs.getString("name_id"));
        weapon.setType(L1ItemTypes.weaponTypes.get(rs.getString("type")));
        weapon.setType1(L1ItemTypes.weaponId.get(rs.getString("type")));
        weapon.setType2(1);
        weapon.setUseType(1);
        weapon.setMaterial(L1ItemTypes.materialTypes.get(rs.getString("material")));
        weapon.setWeight(rs.getInt("weight"));
        weapon.setGfxId(rs.getInt("invgfx"));
        weapon.setGroundGfxId(rs.getInt("grdgfx"));
        weapon.setItemDescId(rs.getInt("itemdesc_id"));
        weapon.setDmgSmall(rs.getInt("dmg_small"));
        weapon.setDmgLarge(rs.getInt("dmg_large"));
        weapon.setRange(rs.getInt("range"));
        weapon.set_safeenchant(rs.getInt("safenchant"));
        weapon.setUseRoyal(rs.getInt("use_royal") != 0);
        weapon.setUseKnight(rs.getInt("use_knight") != 0);
        weapon.setUseElf(rs.getInt("use_elf") != 0);
        weapon.setUseMage(rs.getInt("use_mage") != 0);
        weapon.setUseDarkElf(rs.getInt("use_darkelf") != 0);
        weapon.setUseDragonKnight(rs.getInt("use_dragonknight") != 0);
        weapon.setUseBlackWizard(rs.getInt("use_blackwizard") != 0);
        weapon.setHitModifier(rs.getInt("hitmodifier"));
        weapon.setDmgModifier(rs.getInt("dmgmodifier"));
        weapon.setAddStr(rs.getByte("add_str"));
        weapon.setAddDex(rs.getByte("add_dex"));
        weapon.setAddCon(rs.getByte("add_con"));
        weapon.setAddInt(rs.getByte("add_int"));
        weapon.setAddWis(rs.getByte("add_wis"));
        weapon.setAddCha(rs.getByte("add_cha"));
        weapon.setAddHp(rs.getInt("add_hp"));
        weapon.setAddMp(rs.getInt("add_mp"));
        weapon.setAddHpr(rs.getInt("add_hpr"));
        weapon.setAddMpr(rs.getInt("add_mpr"));
        weapon.setAddSp(rs.getInt("add_sp"));
        weapon.setmDef(rs.getInt("m_def"));
        weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
        weapon.setMagicDmgModifier(rs.getInt("magicdmgmodifier"));
        weapon.set_canbedmg(rs.getInt("canbedmg"));
        weapon.setMinLevel(rs.getInt("min_lvl"));
        weapon.setMaxLevel(rs.getInt("max_lvl"));
        weapon.setBless(rs.getInt("bless"));
        weapon.setTradAble(rs.getInt("trade") == 1);
        weapon.setDeleteAble(rs.getInt("deleteAble") == 1);
        weapon.setHasteItem(rs.getInt("haste_item") != 0);
        weapon.setMaxUseTime(rs.getInt("max_use_time"));
        weapon.set_addDmg(rs.getInt("addDmg"));
        weapon.setItemGrade(rs.getInt("grade"));
        weapon.setCantDurability(rs.getInt("cant_durability") == 1);
        weapon.setRevival(rs.getInt("revival"));
        weapon.setRevivalPer(rs.getInt("revival_per"));
        weapon.setRevivalMent(rs.getInt("revival_ment"));
        weapon.setWarehouse(rs.getInt("warehouse") == 1);
        weapon.setPickupMent(rs.getInt("pickup_ment"));
        weapon.setMagicHitUp(rs.getInt("magic_hitup"));
        weapon.setEtc1(rs.getString("etc1"));
        weapon.setEtc2(rs.getString("etc2"));
        weapon.setAddStun(rs.getInt("add_stun"));
        weapon.setCriticalPer(rs.getInt("critical_per"));
        weapon.setBowCriticalPer(rs.getInt("bowCritical_per"));
        weapon.setDeleteSecond(rs.getInt("delete_second"));
        weapon.setStatusMsg(rs.getString("status_msg"));
        weapon.setColor(rs.getString("color"));
        weapon.setGrade(rs.getInt("grade"));
        weapon.setPurchaseAble(rs.getInt("purchase_able") == 1);
        weapon.setDropSound(rs.getInt("drop_sound"));

        return weapon;
    }
}
