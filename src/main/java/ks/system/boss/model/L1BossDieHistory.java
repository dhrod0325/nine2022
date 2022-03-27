package ks.system.boss.model;

import java.util.Date;

public class L1BossDieHistory {
    private int id;
    private int bossSpawnId;
    private int npcId;
    private Date deathTime;
    private String attacker;
    private int portalId;
    private Date portalStartTime;

    public int getPortalId() {
        return portalId;
    }

    public void setPortalId(int portalId) {
        this.portalId = portalId;
    }

    public Date getPortalStartTime() {
        return portalStartTime;
    }

    public void setPortalStartTime(Date portalStartTime) {
        this.portalStartTime = portalStartTime;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public Date getDeathTime() {
        return deathTime;
    }

    public void setDeathTime(Date deathTime) {
        this.deathTime = deathTime;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBossSpawnId() {
        return bossSpawnId;
    }

    public void setBossSpawnId(int bossSpawnId) {
        this.bossSpawnId = bossSpawnId;
    }
}
