package ks.system.grangKin;

import ks.app.LineageAppContext;
import ks.core.datatables.account.Account;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

@Component
public class GrangKainTable {
    public static GrangKainTable getInstance() {
        return LineageAppContext.getBean(GrangKainTable.class);
    }

    public void updateTime(String accountId, int second) {
        SqlUtils.update("insert into characters_grang_kain (accountId,second) values (?,?) ON DUPLICATE KEY UPDATE second=?", accountId, second, second);
    }

    public void updateTime(Account vo) {
        updateTime(vo.getName(), vo.getGrangKain().getSecond());
    }

    public int selectTime(String accountId) {
        return SqlUtils.selectInteger("select second from characters_grang_kain where accountId=?", accountId);
    }
}