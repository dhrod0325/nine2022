package ks.model;

public class L1Delay {
    private long startTime = 0;

    public void start() {
        start(0);
    }

    public void start(long delayTime) {
        if (startTime <= System.currentTimeMillis()) {
            this.startTime = System.currentTimeMillis() + delayTime;
        }
    }

    public boolean isDelay() {
        return this.startTime >= System.currentTimeMillis();
    }
}
