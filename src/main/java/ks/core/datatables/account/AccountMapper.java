package ks.core.datatables.account;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet rs, int i) throws SQLException {
        Account account = new Account();
        account.setName(rs.getString("login"));
        account.setPassword(rs.getString("password"));
        account.setLastActive(rs.getTimestamp("lastactive"));
        account.setAccessLevel(rs.getInt("access_level"));
        account.setIp(rs.getString("ip"));
        account.setHost(rs.getString("host"));
        account.setBanned(rs.getInt("banned") != 0);
        account.setCharSlot(rs.getInt("charslot"));
        account.setQuiz(rs.getString("quize"));
        account.setGamePassword(rs.getInt("gamepassword"));
        account.setLastLogout(rs.getTimestamp("last_log_out"));

        return account;
    }
}
