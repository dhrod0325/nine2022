package ks.model;

public class ItemDelayTimer {
    private final L1Character cha;

    private final long time;

    public ItemDelayTimer(L1Character cha, int time) {
        this.cha = cha;
        this.time = time + System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public L1Character getCha() {
        return cha;
    }

    public boolean hasItemDelay() {
        return time > System.currentTimeMillis();
    }
}