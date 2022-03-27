package ks.scheduler.npc;

import ks.app.LineageAppContext;
import ks.model.instance.L1NpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class NpcDeleteScheduler {
    private static final Logger logger = LogManager.getLogger(NpcDeleteScheduler.class);

    private final List<L1NpcInstance> list = new CopyOnWriteArrayList<>();

    public static NpcDeleteScheduler getInstance() {
        return LineageAppContext.getBean(NpcDeleteScheduler.class);
    }

    @Scheduled(fixedDelay = 300)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        list.stream().filter(Objects::nonNull).forEach(npc -> {
            try {
                if (npc.getDeleteTime() < System.currentTimeMillis()) {
                    List<NpcDeleteCallBack> callBackList = npc.getDeleteCallBackList();

                    if (callBackList != null) {
                        for (NpcDeleteCallBack callBack : callBackList) {
                            try {
                                if (callBack != null) {
                                    callBack.onDelete(npc);
                                }
                            } catch (Exception e) {
                                logger.error(e);
                            }
                        }
                    }

                    npc.setDeleteTime(0);
                    npc.deleteMe();

                    removeNpcDelete(npc);
                }
            } catch (Exception e) {
                logger.error(e);
                removeNpcDelete(npc);
            }
        });
    }

    public void addNpcDelete(L1NpcInstance npc, long delaySecond) {
        addNpcDelete(npc, delaySecond, null);
    }

    public void addNpcDelete(L1NpcInstance npc, long delay, NpcDeleteCallBack callBack) {
        npc.setDeleteTime(System.currentTimeMillis() + delay);

        if (callBack != null) {
            npc.addDeleteCallBack(callBack);
        }

        if (!list.contains(npc))
            list.add(npc);

        logger.trace("ADD DELETE TIMER " + npc.getName() + ",DELAY:" + delay + ",DELAY MINUTE :" + TimeUnit.MILLISECONDS.toMinutes(delay) + ",callBack : " + callBack + ",npcId:" + npc.getNpcId());
    }

    public void removeNpcDelete(L1NpcInstance npc) {
        list.remove(npc);
    }

    public interface NpcDeleteCallBack {
        void onDelete(L1NpcInstance npc);
    }
}
