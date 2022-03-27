package ks.model.pc;

import java.util.HashMap;

public class L1PcSpeedHackCheck {
    private final HashMap<Integer, PcSpeedHackCount> counter = new HashMap<>();

    public void addSpeedHackCount(int gfxId) {
        addSpeedHackCount(gfxId, 1);
    }

    public void addSpeedHackCount(int gfxId, int speedHackCount) {
        saveSpeedHackCount(gfxId, getSpeedHackCount(gfxId).getCount() + speedHackCount);
    }

    public void saveSpeedHackCount(int gfxId, int speedHackCount) {
        PcSpeedHackCount count = getSpeedHackCount(gfxId);
        count.setGfxId(gfxId);
        count.setCount(speedHackCount);

        counter.put(gfxId, count);
    }

    public PcSpeedHackCount getSpeedHackCount(int gfxId) {
        return counter.getOrDefault(gfxId, new PcSpeedHackCount());
    }

    public static class PcSpeedHackCount {
        private int gfxId;
        private int count;

        public int getGfxId() {
            return gfxId;
        }

        public void setGfxId(int gfxId) {
            this.gfxId = gfxId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}