package ks.core.datatables.badMsg;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BadMsgTable {
    private final List<BadMsg> list = new ArrayList<>();

    public static BadMsgTable getInstance() {
        return LineageAppContext.getBean(BadMsgTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<BadMsg> selectList() {
        return SqlUtils.query("select * from bad_msg", new BeanPropertyRowMapper<>(BadMsg.class));
    }

    public void insert(String msg) {
        SqlUtils.update("insert into bad_msg (chat) values (?)", msg);
    }

    public boolean isContainsBadMsg(String chat) {
        for (BadMsg badMsg : list) {
            if (chat.contains(badMsg.getChat())) {
                return true;
            }
        }

        return false;
    }
}
