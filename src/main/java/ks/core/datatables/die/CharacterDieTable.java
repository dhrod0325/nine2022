package ks.core.datatables.die;

import ks.util.common.SqlUtils;

import java.util.Date;
import java.util.List;

public class CharacterDieTable {
    private static final CharacterDieTable instance = new CharacterDieTable();

    public static CharacterDieTable getInstance() {
        return instance;
    }

    public List<CharacterDie> selectListByCharId(int charId) {
        return SqlUtils.queryForList("select * from character_die where char_id=?", CharacterDie.class, charId);
    }

    public void insert(int charId, int res) {
        SqlUtils.update("insert into character_die (char_id,res,regDate) values (?,?,?)", charId, res, new Date());
    }

    public void update(int id, int res) {
        SqlUtils.update("update character_die set res=? where id = ?", res, id);
    }
}
