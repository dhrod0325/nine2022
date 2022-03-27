package ks.commands.gm.command;

import ks.model.L1Command;
import ks.util.common.SqlUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class L1Commands {
    private static L1Command fromResultSet(ResultSet rs) throws SQLException {
        return new L1Command(rs.getString("name"), rs.getInt("access_level"), rs.getString("class_name"));
    }

    public static L1Command get(String name) {
        return SqlUtils.select("SELECT * FROM commands WHERE name=?", (rs, i) -> fromResultSet(rs), name);
    }

    public static List<L1Command> availableCommandList(int accessLevel) {
        List<L1Command> result = new ArrayList<>();

        SqlUtils.query("SELECT * FROM commands WHERE access_level <= ?", (rs, i) -> {
            result.add(fromResultSet(rs));
            return null;
        }, accessLevel);

        return result;
    }
}
