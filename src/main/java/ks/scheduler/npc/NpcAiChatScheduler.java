package ks.scheduler.npc;

import ks.app.LineageAppContext;
import ks.constants.L1NpcConstants;
import ks.model.Broadcaster;
import ks.model.instance.L1NpcInstance;
import ks.packets.serverpackets.S_NpcChatPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NpcAiChatScheduler {
    private static final Logger logger = LogManager.getLogger(NpcAiChatScheduler.class);

    private final List<L1NpcInstance> list = new CopyOnWriteArrayList<>();

    public static NpcAiChatScheduler getInstance() {
        return LineageAppContext.getBean(NpcAiChatScheduler.class);
    }

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        try {
            list.stream().filter(Objects::nonNull).forEach(npc -> {
                if (npc.npcChat == null) {
                    return;
                }

                if (npc.getHiddenStatus() != L1NpcConstants.HIDDEN_STATUS_NONE || npc.destroyed) {
                    remove(npc);
                    return;
                }

                int chatTiming = npc.npcChat.getChatTiming();
                int chatInterval = npc.npcChat.getChatInterval();
                boolean isShout = npc.npcChat.isShout();

                String chatId1 = npc.npcChat.getChatId1();
                String chatId2 = npc.npcChat.getChatId2();
                String chatId3 = npc.npcChat.getChatId3();
                String chatId4 = npc.npcChat.getChatId4();
                String chatId5 = npc.npcChat.getChatId5();

                int repeatInterval = npc.npcChat.getRepeatInterval();

                if (!chatId1.equals("")) {
                    if (npc.npcChatTime == 0 && npc.npcChatType == 0) {
                        chat(npc, chatTiming, chatId1, isShout);
                        npc.npcChatType = 1;
                    } else if (npc.npcChatType == 5) {
                        if (npc.npcChatTime <= System.currentTimeMillis()) {
                            chat(npc, chatTiming, chatId1, isShout);
                            npc.npcChatTime = System.currentTimeMillis() + repeatInterval;
                        } else
                            return;
                    }
                }
                if (chatId2.equals("") && !npc.npcChat.isRepeat()) {
                    remove(npc);
                    return;
                } else if (npc.npcChat.isRepeat()) {
                    if (npc.npcChatTime == 0) {
                        npc.npcChatType = 5;
                        npc.npcChatTime = System.currentTimeMillis() + repeatInterval;
                    }
                    return;
                }
                if (!chatId2.equals("") && npc.npcChatType == 1) {
                    if (npc.npcChatTime == 0) {
                        npc.npcChatTime = System.currentTimeMillis()
                                + chatInterval;
                        return;
                    }
                    if (npc.npcChatTime <= System.currentTimeMillis()) {
                        chat(npc, chatTiming, chatId2, isShout);
                        npc.npcChatTime = System.currentTimeMillis() + chatInterval;
                        npc.npcChatType = 2;
                    } else
                        return;
                } else if (chatId2.equals("")) {
                    remove(npc);
                    return;
                }
                if (!chatId3.equals("") && npc.npcChatType == 2) {
                    if (npc.npcChatTime <= System.currentTimeMillis()) {
                        chat(npc, chatTiming, chatId3, isShout);
                        npc.npcChatTime = System.currentTimeMillis() + chatInterval;
                        npc.npcChatType = 3;
                    } else
                        return;
                } else if (chatId3.equals("")) {
                    remove(npc);
                    return;
                }

                if (!chatId4.equals("") && npc.npcChatType == 3) {
                    if (npc.npcChatTime <= System.currentTimeMillis()) {
                        chat(npc, chatTiming, chatId4, isShout);
                        npc.npcChatTime = System.currentTimeMillis() + chatInterval;
                        npc.npcChatType = 4;
                    } else {
                        return;
                    }
                } else if (chatId4.equals("")) {
                    remove(npc);
                    return;
                }

                if (!chatId5.equals("") && npc.npcChatType == 4) {
                    if (npc.npcChatTime <= System.currentTimeMillis()) {
                        chat(npc, chatTiming, chatId5, isShout);
                        remove(npc);
                    }
                } else if (chatId5.equals("")) {
                    remove(npc);
                }
            });
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void add(L1NpcInstance npc) {
        if (!list.contains(npc))
            list.add(npc);
    }

    public void remove(L1NpcInstance npc) {
        if (list.contains(npc)) {
            list.remove(npc);

            if (npc != null) {
                npc.npcChatTime = 0;
                npc.npcChatType = 0;
            }
        }
    }

    private void chat(L1NpcInstance npc, int chatTiming, String chatId, boolean isShout) {
        if (chatTiming == L1NpcConstants.CHAT_TIMING_APPEARANCE && npc.isDead()) {
            return;
        }
        if (chatTiming == L1NpcConstants.CHAT_TIMING_DEAD && !npc.isDead()) {
            return;
        }
        if (chatTiming == L1NpcConstants.CHAT_TIMING_HIDE && npc.isDead()) {
            return;
        }

        if (!isShout) {
            Broadcaster.broadcastPacket(npc, new S_NpcChatPacket(npc, chatId, 0));
        } else {
            Broadcaster.wideBroadcastPacket(npc, new S_NpcChatPacket(npc, chatId, 2));
        }
    }
}
