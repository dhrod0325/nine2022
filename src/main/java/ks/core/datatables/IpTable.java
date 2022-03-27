package ks.core.datatables;

import ks.util.common.SqlUtils;

import java.util.ArrayList;
import java.util.List;

public class IpTable {
    private static final IpTable instance = new IpTable();
    private final List<String> banIpList = new ArrayList<>();

    public static IpTable getInstance() {
        return instance;
    }

    public void insert(String ip) {
        SqlUtils.update("INSERT INTO ban_ip SET ip=?", ip);
        banIpList.add(ip);
    }

    public boolean isBanned(String s) {
        for (String ip : banIpList) {
            if (s.matches(ip)) {
                return true;
            }
        }

        return banIpList.contains(s);
    }

    public void load() {
        banIpList.clear();
        banIpList.addAll(selectList());
    }

    public List<String> selectList() {
        return SqlUtils.queryForList("SELECT ip FROM ban_ip", String.class);
    }

    public boolean delete(String ip) {
        SqlUtils.update("DELETE FROM ban_ip WHERE ip=?", ip);
        banIpList.remove(ip);

        return true;
    }
}
