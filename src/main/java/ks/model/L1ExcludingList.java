package ks.model;

import java.util.ArrayList;
import java.util.List;

public class L1ExcludingList {
    private final List<String> nameList = new ArrayList<>();

    public void add(String name) {
        nameList.add(name);
    }

    public String remove(String name) {
        for (String each : nameList) {
            if (each.equalsIgnoreCase(name)) {
                nameList.remove(each);
                return each;
            }
        }
        return null;
    }

    public boolean contains(String name) {
        for (String each : nameList) {
            if (each.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public boolean isFull() {
        return nameList.size() >= 50;
    }
}
