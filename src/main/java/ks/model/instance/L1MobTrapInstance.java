package ks.model.instance;


import ks.app.LineageAppContext;
import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCPack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledFuture;

@SuppressWarnings("unused")
public class L1MobTrapInstance extends L1NpcInstance {
    private static final Logger logger = LogManager.getLogger(L1MobTrapInstance.class.getName());
    private boolean aiStart = false;

    public L1MobTrapInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
        startTrapAI();
    }

    private synchronized void startTrapAI() {
        if (!aiStart) {
            aiStart = true;
        }
    }

    private boolean onTrapAi() {
        return false;
    }

    class TrapAIThreadImpl implements Runnable {
        ScheduledFuture<?> stopCheck;

        public void start() {
            stopCheck = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(this, 500);
        }

        public void run() {
            try {
                if (onTrapAi()) {
                    aiStart = false;
                    stopCheck.cancel(true);
                    stopCheck = null;
                }
            } catch (Exception e) {
                logger.error("TrapAI에 예외가 발생했습니다.", e);
            }
        }
    }
}
