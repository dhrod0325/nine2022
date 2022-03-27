package ks.model.pc;

import ks.model.bookMark.L1BookMark;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1PcBookMark {
    private final List<L1BookMark> bookMarks = new CopyOnWriteArrayList<>();
    private final List<L1BookMark> speedBookmarks = new CopyOnWriteArrayList<>();

    public L1BookMark findByName(String name) {
        for (L1BookMark bookmark : bookMarks) {
            if (bookmark.getName().equalsIgnoreCase(name)) {
                return bookmark;
            }
        }

        return null;
    }

    public L1BookMark findById(int id) {
        for (L1BookMark bookmark : bookMarks) {
            if (bookmark.getId() == id) {
                return bookmark;
            }
        }
        return null;
    }

    public void addBookMark(L1BookMark book) {
        bookMarks.add(book);
    }

    public L1BookMark findByLocation(int x, int y, int map) {
        for (L1BookMark bookmark : bookMarks) {
            if (x == bookmark.getLocX() && y == bookmark.getLocY() && map == bookmark.getMapId()) {
                return bookmark;
            }
        }

        return null;
    }

    public void clear() {
        bookMarks.clear();
        speedBookmarks.clear();
    }

    public List<L1BookMark> getBookMarkList() {
        return bookMarks;
    }

    public List<L1BookMark> getSpeedBookmarkList() {
        return speedBookmarks;
    }

    public void addSpeedBookMark(L1BookMark bookMark) {
        speedBookmarks.add(bookMark);
    }

    public L1BookMark findByTempId(int tempId) {
        for (L1BookMark o : bookMarks) {
            if (o.getTempId() == tempId) {
                return o;
            }
        }

        return null;

    }
}
