package ks.core.datatables;

import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;

import java.util.List;
import java.util.Map;

public class BoardTable {
    private static final BoardTable instance = new BoardTable();

    public static BoardTable getInstance() {
        return instance;
    }

    public int selectTodayWriteSize(L1PcInstance pc, String date, int boardId) {
        return SqlUtils.selectInteger("SELECT count(*) FROM board where board_id=? and date=? and name=?", boardId, date, pc.getName());
    }

    public void writeTopic(L1PcInstance pc, String date, String title, String content, int boardId) {
        SqlUtils.update("INSERT INTO board SET name=?, date=?, title=?, content=?, board_id=?", pc.getName(), date, title, content, boardId);
    }

    public void deleteTopic(int boardId, int number) {
        SqlUtils.update("DELETE FROM board WHERE id=? AND board_id=?", number, boardId);
    }

    public List<Map<String, Object>> selectListByBoardId(int boardId, int number) {
        if (number > 0) {
            return SqlUtils.queryForList("SELECT * FROM board where board_id=? AND id < ? order by id desc limit 0,8", boardId, number);
        } else {
            return SqlUtils.queryForList("SELECT * FROM board where board_id=? order by id desc limit 0,8", boardId);
        }
    }
}
