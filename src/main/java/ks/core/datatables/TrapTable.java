package ks.core.datatables;

import ks.core.storage.TrapStorage;
import ks.model.trap.*;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrapTable {
    private static final Logger logger = LogManager.getLogger(TrapTable.class);

    private static final TrapTable instance = new TrapTable();
    private final Map<Integer, L1Trap> traps = new HashMap<>();

    public static TrapTable getInstance() {
        return instance;
    }

    private L1Trap createTrapInstance(String name, SqlTrapStorage storage) {
        switch (name) {
            case "L1DamageTrap":
                return new L1DamageTrap(storage);
            case "L1PoisonTrap":
                return new L1PoisonTrap(storage);
            case "L1MonsterTrap":
                return new L1MonsterTrap(storage);
            case "L1TeleportTrap":
                return new L1TeleportTrap(storage);
            case "L1HealingTrap":
                return new L1HealingTrap(storage);
            case "L1SkillTrap":
                return new L1SkillTrap(storage);
            case "L1PolyTrap":
                return new L1PolyTrap(storage);
        }

        return null;
    }

    public void load() {
        traps.clear();

        List<L1Trap> list = selectList();

        for (L1Trap trap : list) {
            traps.put(trap.getId(), trap);
        }
    }

    public L1Trap getTemplate(int id) {
        return traps.get(id);
    }

    public List<L1Trap> selectList() {
        return SqlUtils.query("select * from trap", (rs, i) -> {
            try {
                String typeName = rs.getString("type");
                return createTrapInstance(typeName, new SqlTrapStorage(rs));
            } catch (Exception e) {
                logger.error(e);
            }

            return null;
        });
    }

    private static class SqlTrapStorage implements TrapStorage {
        private final ResultSet rs;

        public SqlTrapStorage(ResultSet rs) {
            this.rs = rs;
        }

        public String getString(String name) {
            try {
                return rs.getString(name);
            } catch (SQLException e) {
                logger.error("오류", e);
            }
            return "";
        }

        public int getInt(String name) {
            try {
                return rs.getInt(name);
            } catch (SQLException e) {
                logger.error("오류", e);
            }
            return 0;
        }

        public boolean getBoolean(String name) {
            try {
                return rs.getBoolean(name);
            } catch (SQLException e) {
                logger.error("오류", e);
            }

            return false;
        }
    }
}
