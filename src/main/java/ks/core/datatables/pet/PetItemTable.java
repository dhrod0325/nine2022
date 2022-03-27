package ks.core.datatables.pet;

import ks.model.L1PetItem;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetItemTable {
    private static final PetItemTable instance = new PetItemTable();

    private final Map<Integer, L1PetItem> petItemIdIndex = new HashMap<>();

    public static PetItemTable getInstance() {
        return instance;
    }

    public void load() {
        List<L1PetItem> list = selectList();

        for (L1PetItem item : list) {
            petItemIdIndex.put(item.getItemId(), item);
        }
    }

    public List<L1PetItem> selectList() {
        return SqlUtils.query("SELECT * FROM petitem", (rs, i) -> {
            L1PetItem petItem = new L1PetItem();
            petItem.setItemId(rs.getInt("item_id"));
            petItem.setHitModifier(rs.getInt("hitmodifier"));
            petItem.setDamageModifier(rs.getInt("dmgmodifier"));
            petItem.setAddAc(rs.getInt("ac"));
            petItem.setAddStr(rs.getInt("add_str"));
            petItem.setAddCon(rs.getInt("add_con"));
            petItem.setAddDex(rs.getInt("add_dex"));
            petItem.setAddInt(rs.getInt("add_int"));
            petItem.setAddWis(rs.getInt("add_wis"));
            petItem.setAddHp(rs.getInt("add_hp"));
            petItem.setAddMp(rs.getInt("add_mp"));
            petItem.setAddSp(rs.getInt("add_sp"));
            petItem.setAddMr(rs.getInt("m_def"));
            return petItem;
        });
    }

    public L1PetItem getTemplate(int itemId) {
        return petItemIdIndex.get(itemId);
    }
}
