package ks.model.pc;

import java.util.HashMap;

public class L1PcStateMap extends HashMap<String, Boolean> {

    private final String KEY_AUTO_CLAN = "autoClan";

    public boolean isState(String key) {
        return get(key) != null && get(key);
    }

    public boolean isAutoClan() {
        return isState(KEY_AUTO_CLAN);
    }

    public void setAutoClan(boolean value) {
        put(KEY_AUTO_CLAN, value);
    }
}