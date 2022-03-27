package ks.model;

import ks.model.pc.L1PcInstance;

import java.util.HashMap;
import java.util.Map;

public class L1Buddy {
    private final int charId;

    private final Map<Integer, String> buddys = new HashMap<>();

    public L1Buddy(int charId) {
        this.charId = charId;
    }

    public int getCharId() {
        return charId;
    }

    public boolean add(int objId, String name) {
        if (buddys.containsKey(objId)) {
            return false;
        }
        buddys.put(objId, name);
        return true;
    }

    public boolean remove(int objId) {
        String result = buddys.remove(objId);
        return (result != null);
    }

    public boolean remove(String name) {
        int id = 0;
        for (Map.Entry<Integer, String> buddy : buddys.entrySet()) {
            if (name.equalsIgnoreCase(buddy.getValue())) {
                id = buddy.getKey();
                break;
            }
        }
        if (id == 0) {
            return false;
        }
        buddys.remove(id);
        return true;
    }

    public String getOnlineBuddyListString() {
        StringBuilder result = new StringBuilder();
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (buddys.containsKey(pc.getId())) {
                result.append(pc.getName()).append(" ");
            }
        }
        return result.toString();
    }

    public String getBuddyListString() {
        StringBuilder result = new StringBuilder();
        for (String name : buddys.values()) {
            result.append(name).append(" ");
        }
        return result.toString();
    }

    public boolean contains(String name) {
        for (String buddyName : buddys.values()) {
            if (name.equalsIgnoreCase(buddyName)) {
                return true;
            }
        }

        return false;
    }
}
