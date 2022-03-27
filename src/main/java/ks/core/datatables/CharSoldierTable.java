package ks.core.datatables;

import ks.model.L1CharSoldier;
import ks.util.common.SqlUtils;

import java.util.ArrayList;
import java.util.List;

public class CharSoldierTable {
    private static final CharSoldierTable instance = new CharSoldierTable();

    private final List<L1CharSoldier> soldiers = new ArrayList<>();

    public static CharSoldierTable getInstance() {
        return instance;
    }

    public void load() {
        soldiers.clear();
        soldiers.addAll(selectList());
    }

    public List<L1CharSoldier> selectList() {
        return SqlUtils.query("SELECT * FROM character_soldier", (rs, i) -> {
            L1CharSoldier soldier = new L1CharSoldier(rs.getInt(1));
            soldier.setSoldierNpc(rs.getInt(2));
            soldier.setSoldierCount(rs.getInt(3));
            soldier.setSoldierCastleId(rs.getInt(4));
            soldier.setSoldierTime(rs.getInt(5));

            return soldier;
        });
    }

    public void addCharSoldier(L1CharSoldier newCharSoldier) {
        soldiers.add(newCharSoldier);
    }

    public void storeCharSoldier(L1CharSoldier soldier) {
        SqlUtils.update("INSERT INTO character_soldier SET char_id=?, npc_id=?, count=?, castle_id=?, time=?",
                soldier.getCharId(),
                soldier.getSoldierNpc(),
                soldier.getSoldierCount(),
                soldier.getSoldierCastleId(),
                soldier.getSoldierTime()
        );

        addCharSoldier(soldier);
    }

    public List<L1CharSoldier> getSoldiers() {
        return soldiers;
    }
}