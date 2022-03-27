package ks.core.datatables.item.mapper;

import ks.constants.L1ItemTypes;
import ks.model.L1Armor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArmorMapper implements RowMapper<L1Armor> {
    @Override
    public L1Armor mapRow(ResultSet rs, int i) throws SQLException {
        L1Armor armor = new L1Armor();
        armor.setItemId(rs.getInt("item_id"));
        armor.setName(rs.getString("name"));
        armor.setNameId(rs.getString("name_id"));
        armor.setType(L1ItemTypes.armorTypes.get(rs.getString("type")));
        armor.setType2(2);
        armor.setUseType(L1ItemTypes.useTypes.get(rs.getString("type")));
        armor.setGrade(rs.getInt("grade"));
        armor.setMaterial(L1ItemTypes.materialTypes.get(rs.getString("material")));
        armor.setWeight(rs.getInt("weight"));
        armor.setGfxId(rs.getInt("invgfx"));
        armor.setGroundGfxId(rs.getInt("grdgfx"));
        armor.setItemDescId(rs.getInt("itemdesc_id"));
        armor.setAc(rs.getInt("ac"));
        armor.set_safeenchant(rs.getInt("safenchant"));
        armor.setUseRoyal(rs.getInt("use_royal") != 0);
        armor.setUseKnight(rs.getInt("use_knight") != 0);
        armor.setUseElf(rs.getInt("use_elf") != 0);
        armor.setUseMage(rs.getInt("use_mage") != 0);
        armor.setUseDarkElf(rs.getInt("use_darkelf") != 0);
        armor.setUseDragonKnight(rs.getInt("use_dragonknight") != 0);
        armor.setUseBlackWizard(rs.getInt("use_blackwizard") != 0);
        armor.setUseHighPet(rs.getInt("use_HighPet") != 0);
        armor.setAddStr(rs.getByte("add_str"));
        armor.setAddCon(rs.getByte("add_con"));
        armor.setAddDex(rs.getByte("add_dex"));
        armor.setAddInt(rs.getByte("add_int"));
        armor.setAddWis(rs.getByte("add_wis"));
        armor.setAddCha(rs.getByte("add_cha"));
        armor.setAddHp(rs.getInt("add_hp"));
        armor.setAddMp(rs.getInt("add_mp"));
        armor.setAddHpr(rs.getInt("add_hpr"));
        armor.setAddMpr(rs.getInt("add_mpr"));
        armor.setAddSp(rs.getInt("add_sp"));
        armor.setMinLevel(rs.getInt("min_lvl"));
        armor.setMaxLevel(rs.getInt("max_lvl"));
        armor.setmDef(rs.getInt("m_def"));
        armor.setDamageReduction(rs.getInt("damage_reduction"));
        armor.setWeightReduction(rs.getInt("weight_reduction"));
        armor.setHitup(rs.getInt("hit_rate")); // 공성
        armor.setDmgup(rs.getInt("dmg_rate")); // 추타
        armor.setBowHitup(rs.getInt("bow_hit_rate"));// 활명중
        armor.setBowDmgup(rs.getInt("bow_dmg_rate"));// 활타격치
        armor.setHasteItem(rs.getInt("haste_item") != 0);
        armor.setBless(rs.getInt("bless"));
        armor.setTradAble(rs.getInt("trade") == 1);
        armor.setDeleteAble(rs.getInt("deleteAble") == 1);
        armor.setDefenseEarth(rs.getInt("defense_earth"));
        armor.setDefenseWater(rs.getInt("defense_water"));
        armor.setDefenseWind(rs.getInt("defense_wind"));
        armor.setDefenseFire(rs.getInt("defense_fire"));
        armor.setRegistStun(rs.getInt("regist_stun"));
        armor.setRegistStone(rs.getInt("regist_stone"));
        armor.setRegistSleep(rs.getInt("regist_sleep"));
        armor.setRegistFreeze(rs.getInt("regist_freeze"));
        armor.setRegistSustain(rs.getInt("regist_sustain"));
        armor.setRegistBlind(rs.getInt("regist_blind"));
        armor.setRegistElf(rs.getInt("regist_elf"));
        armor.setMaxUseTime(rs.getInt("max_use_time"));
        armor.setRevival(rs.getInt("revival"));
        armor.setRevivalPer(rs.getInt("revival_per"));
        armor.setRevivalMent(rs.getInt("revival_ment"));
        armor.setWarehouse(rs.getInt("warehouse") == 1);
        armor.setPickupMent(rs.getInt("pickup_ment"));
        armor.setMagicHitUp(rs.getInt("magic_hitup"));
        armor.setEtc1(rs.getString("etc1"));
        armor.setEtc2(rs.getString("etc2"));
        armor.setIgnoreReduction(rs.getInt("ignore_reduction"));

        armor.setPvpDamage(rs.getInt("pvp_damage"));
        armor.setPvpReduction(rs.getInt("pvp_reduction"));
        armor.setAddStun(rs.getInt("add_stun"));
        armor.setAddEr(rs.getInt("add_er"));
        armor.setCriticalPer(rs.getInt("critical_per"));
        armor.setBowCriticalPer(rs.getInt("bowCritical_per"));
        armor.setDeleteSecond(rs.getInt("delete_second"));
        armor.setStatusMsg(rs.getString("status_msg"));
        armor.setColor(rs.getString("color"));
        armor.setPurchaseAble(rs.getInt("purchase_able") == 1);
        armor.setDropSound(rs.getInt("drop_sound"));


        return armor;
    }
}
