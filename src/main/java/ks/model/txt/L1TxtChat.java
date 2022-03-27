package ks.model.txt;

import ks.util.common.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class L1TxtChat {
    private static final L1TxtChat instance = new L1TxtChat();
    private final List<String> dirtyChatList = new ArrayList<>();

    public L1TxtChat() {
        load();
    }

    public static L1TxtChat getInstance() {
        return instance;
    }

    public void load() {
        dirtyChatList.clear();
        dirtyChatList.addAll(FileUtils.readLineTextList("data/dirtyChat.txt"));
    }

    public List<String> getDirtyChatList() {
        return dirtyChatList;
    }

    public boolean containsDirtyChat(String text) {
        for (String filter : dirtyChatList) {
            if (text.contains(filter)) {
                return true;
            }
        }

        return false;
    }

    public String cleanChat(String text) {
        for (String filter : dirtyChatList) {
            text = text.replace(filter, " ");
        }

        return text;
    }
}