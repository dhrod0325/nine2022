package ks.core.datatables;

import ks.util.common.SqlUtils;

public class LetterTable {
    private static final LetterTable instance = new LetterTable();

    public static LetterTable getInstance() {
        return instance;
    }

    public int getLetterCount(String name, int type) {
        return SqlUtils.selectInteger("SELECT count(*) as cnt FROM letter WHERE receiver=? AND template_id = ?", name, type);
    }

    public void deleteOldMail(String name, int type) {
        String sql = "DELETE FROM letter WHERE receiver=? AND template_id = ?  order by item_object_id limit 1";
        SqlUtils.update(sql, name, type);
    }

    public void writeLetter(int code, String dTime, String sender, String receiver, int templateId, String subject, String content) {
        Integer itemObjectId = SqlUtils.selectInteger("SELECT ifnull(max(item_object_id),0)+1 as cnt FROM letter ORDER BY item_object_id");

        SqlUtils.update("INSERT INTO letter SET item_object_id=?, code=?, sender=?, receiver=?, date=?, template_id=?, subject=?, content=?, isCheck=?",
                itemObjectId,
                code,
                sender,
                receiver,
                dTime,
                templateId,
                subject,
                content,
                0
        );
    }

    public void deleteLetter(int itemObjectId) {
        SqlUtils.update("DELETE FROM letter WHERE item_object_id=?", itemObjectId);
    }

    public void saveLetter(int id, int letterType) {
        SqlUtils.update("UPDATE letter SET template_id = ? WHERE item_object_id=?", letterType, id);
    }

    public void checkLetter(int id) {
        SqlUtils.update("UPDATE letter SET isCheck = 1 WHERE item_object_id=?", id);
    }
}