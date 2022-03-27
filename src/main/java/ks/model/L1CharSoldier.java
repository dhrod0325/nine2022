package ks.model;

public class L1CharSoldier {

    private int _charId;
    private int _npcid;
    private int _count;
    private int _castleid;
    private int _time;

    public L1CharSoldier(int charId) {
        _charId = charId;
    }

    public int getCharId() {
        return _charId;
    }

    public void setCharId(int i) {
        _charId = i;
    }

    public int getSoldierNpc() {
        return _npcid;
    }

    public void setSoldierNpc(int i) {
        _npcid = i;
    }

    public int getSoldierCount() {
        return _count;
    }

    public void setSoldierCount(int i) {
        _count = i;
    }

    public int getSoldierCastleId() {
        return _castleid;
    }

    public void setSoldierCastleId(int i) {
        _castleid = i;
    }

    public int getSoldierTime() {
        return _time;
    }

    public void setSoldierTime(int i) {
        _time = i;
    }
}