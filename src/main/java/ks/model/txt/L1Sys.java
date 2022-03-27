package ks.model.txt;

import ks.util.common.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class L1Sys {
    private static final L1Sys instance = new L1Sys();
    private final List<String> list = new ArrayList<>();

    public L1Sys() {
        load();
    }

    public static L1Sys getInstance() {
        return instance;
    }

    public void load() {
        list.clear();
        list.addAll(FileUtils.readLineTextList("data/sys.txt"));
    }

    public List<String> getList() {
        return list;
    }
}
