package ks.scheduler.npc;

import ks.app.LineageAppContext;
import ks.constants.L1NpcConstants;
import ks.model.instance.L1NpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NpcAIScheduler {
    private static final Logger logger = LogManager.getLogger();

    private final Map<Integer, L1NpcInstance> list = new ConcurrentHashMap<>();

    public static NpcAIScheduler getInstance() {
        return LineageAppContext.getBean(NpcAIScheduler.class);
    }

    @Scheduled(fixedDelay = 100)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        list.values()
                .stream()
                .filter(Objects::nonNull)
                .forEach((npc) -> {
                    LineageAppContext.npcAiScheduler().execute(() -> runAi(npc));
                });
    }

    public void runAi(L1NpcInstance npc) {
        boolean skip = false;

        try {
            if (!npc.isAiCheck()) {
                npc.setAiRunning(true);

                if (!npc.destroyed && !npc.isDead() && npc.getCurrentHp() > 0 && npc.getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_NONE) {
                    if (npc.getAiSleepTime() <= System.currentTimeMillis()) {
                        if (npc.isParalyzed() || npc.isSleeped()) {
                            skip = true;
                        } else {
                            if (npc.toAi()) {
                                npc.setAiCheck(true);
                            } else {
                                npc.setAiSleepTime(npc.getSleepTime() + System.currentTimeMillis());
                                skip = true;
                            }
                        }
                    } else {
                        skip = true;
                    }
                }
            } else {
                if (npc.getAiSleepTime() > System.currentTimeMillis()) {
                    skip = true;
                } else {
                    npc.getMobSkill().resetAllSkillUseCount();

                    if (npc.isDeathProcessing()) {
                        npc.setAiSleepTime(npc.getSleepTime() + System.currentTimeMillis());
                        skip = true;
                    }
                }
            }

            if (skip) {
                return;
            }

            npc.setAiCheck(false);
            npc.allTargetClear();
            npc.setAiRunning(false);
            removeNpc(npc);
        } catch (Exception e) {
            logger.error("ai npcId : {},location:{}", npc.getNpcId(), npc.getLocation());
        }
    }

    public void addNpc(L1NpcInstance npc) {
        if (!list.containsKey(npc.getId()))
            list.put(npc.getId(), npc);
    }

    public void removeNpc(L1NpcInstance npc) {
        list.remove(npc.getId());
    }
}
