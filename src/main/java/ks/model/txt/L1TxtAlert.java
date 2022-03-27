package ks.model.txt;

import ks.app.config.prop.CodeConfig;
import ks.util.common.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class L1TxtAlert {
    private static final L1TxtAlert instance = new L1TxtAlert();
    private final List<String> list = new ArrayList<>();

    public L1TxtAlert() {
        load();
    }

    public static L1TxtAlert getInstance() {
        return instance;
    }

    public void load() {
        list.clear();
        list.add("레벨정보 - [경험치지급단]:" + CodeConfig.EXP_GIVE_MAX_LEVEL + " [만렙]:" + CodeConfig.MAX_LEVEL);
        list.addAll(FileUtils.readLineTextList("data/alert.txt"));
    }

    public String getRandom() {
        if (list.isEmpty()) {
            return null;
        }

        Collections.shuffle(list);

        return list.get(0);
    }
}