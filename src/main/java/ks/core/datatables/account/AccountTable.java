package ks.core.datatables.account;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class AccountTable {
    private static final Logger logger = LogManager.getLogger();

    public static AccountTable getInstance() {
        return LineageAppContext.getBean(AccountTable.class);
    }

    public void insert(final String name, String rawPassword, String ip, String host) {
        SqlUtils.update("INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?,charslot=?,quize=?,gamepassword=?,point_time=?,Point_time_ready=?",
                name,
                rawPassword,
                new Date(),
                0,
                ip,
                host,
                0,
                6,
                null,
                0,
                0,
                0
        );

        logger.info("created new account for " + name);
    }

    public Account load(String name) {
        Account account = selectByName(name);
        account.getGrangKain().initWithAccount(account);
        return account;
    }

    public Account selectByName(String name) {
        return SqlUtils.select("SELECT * FROM accounts WHERE login=? LIMIT 1", new AccountMapper(), name);
    }

    public void updateLastActive(String account) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        SqlUtils.update("UPDATE accounts SET lastactive=? WHERE login = ?", ts, account);
    }

    public void ban(String account) {
        SqlUtils.update("UPDATE accounts SET banned=1 WHERE login=?", account);
    }

    public boolean checkLoginIp(String ip) {
        int cnt = SqlUtils.selectInteger("SELECT count(ip) as cnt FROM accounts WHERE ip=? AND access_level != ?", ip, CodeConfig.GM_CODE);
        return cnt >= 2;
    }

    public void setGamePassword(Account account, int gamePassword) {
        account.setGamePassword(gamePassword);
        SqlUtils.update("UPDATE accounts SET gamepassword=? WHERE login =?", gamePassword, account.getName());
    }

    public void updateQuiz(Account vo) {
        SqlUtils.update("UPDATE accounts SET quize=? WHERE login=?", vo.getQuiz(), vo.getName());
    }

    public void updateLastLogOut(Account vo) {
        SqlUtils.update("UPDATE accounts SET last_log_out=? WHERE login = ?", new Date(), vo.getName());
    }

    public int countCharacters(Account vo) {
        return SqlUtils.selectInteger("SELECT count(*) as cnt FROM characters WHERE account_name=? ", vo.getName());
    }

    public String selectAccountNameByCharName(String charName) {
        return SqlUtils.select("select account_name from characters where char_name = ?", String.class, charName);
    }

    public void updatePassword(String accountName, String passwd) {
        SqlUtils.update("UPDATE accounts SET password=? WHERE login = ?", passwd, accountName);
    }


}
