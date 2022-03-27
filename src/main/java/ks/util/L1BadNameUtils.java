package ks.util;

import ks.util.common.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class L1BadNameUtils {
    private static final L1BadNameUtils instance = new L1BadNameUtils();
    private final List<String> nameList = new ArrayList<>();

    private L1BadNameUtils() {
        nameList.addAll(FileUtils.readLineTextList("data/badnames.txt"));
    }

    public static L1BadNameUtils getInstance() {
        return instance;
    }

    public boolean isBadName(String name) {
        for (String badName : nameList) {
            if (name.equalsIgnoreCase(badName)) {
                return true;
            }

            if (name.toLowerCase().startsWith(badName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
