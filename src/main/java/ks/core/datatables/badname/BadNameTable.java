package ks.core.datatables.badname;

import ks.util.common.SqlUtils;

public class BadNameTable {
    private static final BadNameTable instance = new BadNameTable();

    public static BadNameTable getInstance() {
        return instance;
    }

    public int nextId() {
        return SqlUtils.selectInteger("select ifnull(max(id),0)+1 id from character_badname");
    }

    public void insert(String charName, int charId) {
        SqlUtils.update("insert into character_badname (charName,charId) values (?,?)", charName, charId);
    }
}
