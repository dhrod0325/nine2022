package ks.system.portalsystem.model;

import ks.system.portalsystem.L1AbstractPortalSystem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class L1PortalBoss {
    private final L1AbstractPortalSystem portalSystem;
    private final List<L1PortalBossInfo> portalBossInfoList = new ArrayList<>();
    private int bossSpawnMinute;
    private boolean bossSpawn;
    private String bossSpawnMsg;

    public L1PortalBoss(L1AbstractPortalSystem portalSystem) {
        this.portalSystem = portalSystem;
    }

    public Calendar getBossSpawnTime() {
        Calendar result = Calendar.getInstance();
        result.setTime(portalSystem.getStartTime());
        result.add(Calendar.MINUTE, bossSpawnMinute);

        return result;
    }

    public int getBossSpawnMinute() {
        return bossSpawnMinute;
    }

    public void setBossSpawnMinute(int bossSpawnMinute) {
        this.bossSpawnMinute = bossSpawnMinute;
    }

    public boolean isBossSpawn() {
        return bossSpawn;
    }

    public void setBossSpawn(boolean bossSpawn) {
        this.bossSpawn = bossSpawn;
    }

    public List<L1PortalBossInfo> getPortalBossInfoList() {
        return portalBossInfoList;
    }

    public void addBossInfo(L1PortalBossInfo portalBossInfo) {
        if (!portalBossInfoList.contains(portalBossInfo)) {
            portalBossInfoList.add(portalBossInfo);
        }
    }

    public String getBossSpawnMsg() {
        return bossSpawnMsg;
    }

    public void setBossSpawnMsg(String bossSpawnMsg) {
        this.bossSpawnMsg = bossSpawnMsg;
    }
}
