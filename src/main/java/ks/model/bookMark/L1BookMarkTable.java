package ks.model.bookMark;

import ks.core.ObjectIdFactory;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Bookmarks;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class L1BookMarkTable {
    private static final Logger logger = LogManager.getLogger(L1BookMark.class);

    public static void load(L1PcInstance pc) {
        pc.getBookMark().getBookMarkList().clear();
        pc.getBookMark().getBookMarkList().addAll(selectList(pc));

        pc.getBookMark().getSpeedBookmarkList().clear();
        pc.getBookMark().getSpeedBookmarkList().addAll(selectSpeedList(pc));
    }

    public static List<L1BookMark> selectList(L1PcInstance pc) {
        return SqlUtils.query("SELECT * FROM character_teleport WHERE char_id=? ORDER BY id", (rs, i) -> {
            L1BookMark bookmark = new L1BookMark();
            bookmark.setCharId(rs.getInt("char_id"));
            bookmark.setId(rs.getInt("id"));
            bookmark.setNumId(i);
            bookmark.setSpeedId(rs.getInt("speed_id"));
            bookmark.setName(rs.getString("name"));
            bookmark.setLocX(rs.getInt("locx"));
            bookmark.setLocY(rs.getInt("locy"));
            bookmark.setMapId(rs.getShort("mapid"));

            return bookmark;
        }, pc.getId());
    }

    public static List<L1BookMark> selectSpeedList(L1PcInstance pc) {
        return SqlUtils.query("SELECT * FROM character_teleport WHERE char_id=? AND speed_id > -1 ORDER BY speed_id",
                (rs, i) -> pc.getBookMark().findById(rs.getInt("id")), pc.getId());
    }

    public static void delete(L1PcInstance pc, String bookName) {
        try {
            L1BookMark book = pc.getBookMark().findByName(bookName);

            if (book != null) {
                SqlUtils.update("DELETE FROM character_teleport WHERE id=? AND char_id=?", book.getId(), pc.getId());

                int delId = book.getNumId();

                for (L1BookMark books : pc.getBookMark().getBookMarkList()) {
                    if (books.getNumId() > delId) {
                        books.setNumId(books.getNumId() - 1);
                    }
                }

                if (pc.getBookMark().getSpeedBookmarkList().contains(book)) {
                    delId = book.getSpeedId();

                    for (L1BookMark books : pc.getBookMark().getSpeedBookmarkList()) {
                        if (books.getSpeedId() > delId) {
                            books.setSpeedId(books.getSpeedId() - 1);
                        }
                    }
                }

                update(pc);

                load(pc);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static synchronized void addBookmark(L1PcInstance pc, String bookName) {
        if (pc.getBookMark().getBookMarkList().size() >= pc.getMarkCount()) {
            pc.sendPackets(new S_ServerMessage(676));
            return;
        }

        if (pc.getBookMark().findByName(bookName) == null) {
            L1BookMark bookmark = new L1BookMark();
            bookmark.setId(ObjectIdFactory.getInstance().nextId());

            int numId = SqlUtils.selectInteger("SELECT ifnull(max(num_id),0) as newid FROM character_teleport WHERE char_id=?", pc.getId());

            bookmark.setNumId(numId);
            bookmark.setSpeedId(-1);
            bookmark.setCharId(pc.getId());

            bookmark.setName(bookName);

            bookmark.setLocX(pc.getX());
            bookmark.setLocY(pc.getY());
            bookmark.setMapId(pc.getMapId());

            SqlUtils.update("INSERT INTO character_teleport SET id=?,num_id=?,speed_id=?, char_id=?, name=?, locx=?, locy=?, mapid=?",
                    bookmark.getId(),
                    bookmark.getNumId(),
                    bookmark.getSpeedId(),
                    bookmark.getCharId(),
                    bookmark.getName(),
                    bookmark.getLocX(),
                    bookmark.getLocY(),
                    bookmark.getMapId()
            );

            pc.getBookMark().addBookMark(bookmark);
            pc.sendPackets(new S_Bookmarks(bookName, bookmark.getMapId(), bookmark.getId()));
        } else {
            pc.sendPackets(new S_ServerMessage(1655));
        }
    }

    public static void update(L1PcInstance pc) {
        List<L1BookMark> list = pc.getBookMark().getBookMarkList();

        SqlUtils.update("UPDATE character_teleport SET speed_id=-1  WHERE char_id=?", pc.getId());

        for (L1BookMark bookMark : list) {
            if (pc.getBookMark().getSpeedBookmarkList().contains(bookMark)) {
                SqlUtils.update("UPDATE character_teleport SET speed_id=?  WHERE id=? and char_id=?", bookMark.getSpeedId(), bookMark.getId(), pc.getId());
            }
        }
    }

    public static void deleteByCharId(int charId) {
        SqlUtils.update("DELETE FROM character_teleport WHERE char_id=?", charId);
    }
}
