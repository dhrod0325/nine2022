package ks.system.chat;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;

import java.util.concurrent.ScheduledFuture;

public abstract class L1ChatEventHandler {
    private L1PcInstance pc;

    private String chatMessage = "Y";

    private long waitTimeSecond;

    private ScheduledFuture<?> sc;

    private long startTime;

    public void handle() {
        if (sc != null)
            return;

        startTime = System.currentTimeMillis();

        sc = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(new Runnable() {
            int cnt = 0;

            @Override
            public void run() {
                if (waitTimeSecond - cnt == 0) {
                    fail();
                    stopHandle();
                }

                cnt++;
            }
        }, 1000);
    }

    public void stopHandle() {
        if (sc != null) {
            sc.cancel(true);
            sc = null;
        }
    }

    public L1PcInstance getPc() {
        return pc;
    }

    public void setPc(L1PcInstance pc) {
        this.pc = pc;
    }

    public abstract void process();

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public long getWaitTimeSecond() {
        return waitTimeSecond;
    }

    public void setWaitTimeSecond(long waitTimeSecond) {
        this.waitTimeSecond = waitTimeSecond;
    }

    public long getStartTime() {
        return startTime;
    }

    public void fail() {
    }
}
