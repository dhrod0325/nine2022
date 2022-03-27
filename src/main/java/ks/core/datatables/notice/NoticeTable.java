package ks.core.datatables.notice;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoticeTable {
    public static NoticeTable getInstance() {
        return LineageAppContext.getBean(NoticeTable.class);
    }

    public List<Notice> selectList(int id) {
        String sql = "SELECT * FROM notice where id > ?";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(Notice.class), id);
    }

    public Notice selectFirstNoticeById(int id) {
        String sql = "SELECT * FROM notice where id > ? limit 1";
        return SqlUtils.select(sql, new BeanPropertyRowMapper<>(Notice.class), id);
    }

    public int noticeCount(String account) {
        return SqlUtils.selectInteger("select count(id) as cnt from notice where id > (select notice from accounts where login=?)", account);
    }

    public boolean hasReadRequireNotice(String account) {
        return noticeCount(account) > 0;
    }

    public int findLastNoticeId(String accountName) {
        String sql = "SELECT notice FROM accounts where login=?";
        return SqlUtils.selectInteger(sql, accountName);
    }

    public void update(String account, int id) {
        SqlUtils.update("update accounts set notice=? where login=?", id, account);
    }
}
