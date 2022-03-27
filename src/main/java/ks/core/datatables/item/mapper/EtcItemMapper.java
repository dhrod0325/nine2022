package ks.core.datatables.item.mapper;

import ks.constants.L1ItemTypes;
import ks.model.L1EtcItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtcItemMapper implements RowMapper<L1EtcItem> {
    @Override
    public L1EtcItem mapRow(ResultSet rs, int i) throws SQLException {
        L1EtcItem item = new L1EtcItem();
        item.setItemId(rs.getInt("item_id"));
        item.setName(rs.getString("name"));
        item.setNameId(rs.getString("name_id"));
        item.setType(L1ItemTypes.etcItemTypes.get(rs.getString("item_type")));
        item.setUseType(L1ItemTypes.useTypes.get(rs.getString("use_type")));
        item.setType2(0);
        item.setMaterial(L1ItemTypes.materialTypes.get(rs.getString("material")));
        item.setWeight(rs.getInt("weight"));
        item.setGfxId(rs.getInt("invgfx"));
        item.setGroundGfxId(rs.getInt("grdgfx"));
        item.setItemDescId(rs.getInt("itemdesc_id"));
        item.setMinLevel(rs.getInt("min_lvl"));
        item.setMaxLevel(rs.getInt("max_lvl"));
        item.setBless(rs.getInt("bless"));
        item.setTradAble(rs.getInt("trade") == 1);
        item.setDeleteAble(rs.getInt("deleteAble") == 1);
        item.setDmgSmall(rs.getInt("dmg_small"));
        item.setDmgLarge(rs.getInt("dmg_large"));
        item.setStackable(rs.getInt("stackable") == 1);
        item.setMaxChargeCount(rs.getInt("max_charge_count"));
        item.setLocX(rs.getInt("locx"));
        item.setLocY(rs.getInt("locy"));
        item.setMapid(rs.getShort("mapid"));
        item.setDelayId(rs.getInt("delay_id"));
        item.setDdelayTime(rs.getInt("delay_time"));
        item.set_delayEffect(rs.getInt("delay_effect"));
        item.setFoodVolume(rs.getInt("food_volume"));
        item.setToBeSavedAtOnce(rs.getInt("save_at_once") == 1);
        item.setlogcheckitem(rs.getInt("checkLog"));
        item.setWarehouse(rs.getInt("warehouse") == 1);
        item.setRevival(rs.getInt("revival"));
        item.setRevivalPer(rs.getInt("revival_per"));
        item.setRevivalMent(rs.getInt("revival_ment"));
        item.setPickupMent(rs.getInt("pickup_ment"));
        item.setMagicHitUp(rs.getInt("magic_hitup"));
        item.setEtc1(rs.getString("etc1"));
        item.setEtc2(rs.getString("etc2"));
        item.setDeleteSecond(rs.getInt("delete_second"));
        item.setStatusMsg(rs.getString("status_msg"));
        item.setGrade(rs.getInt("grade"));
        item.setColor(rs.getString("color"));
        item.setPurchaseAble(rs.getInt("purchase_able") == 1);
        item.setDropSound(rs.getInt("drop_sound"));
        return item;
    }
}
