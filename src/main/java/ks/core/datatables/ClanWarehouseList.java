package ks.core.datatables;

import ks.util.common.SqlUtils;

public class ClanWarehouseList {
    private static final ClanWarehouseList instance = new ClanWarehouseList();

    public static ClanWarehouseList getInstance() {
        return instance;
    }

    public void addList(int id, String text, String d) {
        SqlUtils.update("INSERT INTO clan_warehouse_list SET clanid=?, list=?, date=?", id, text, d);
    }
}
