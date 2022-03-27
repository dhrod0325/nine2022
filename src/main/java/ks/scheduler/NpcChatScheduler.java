package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.ServerConfig;
import ks.constants.L1NpcConstants;
import ks.core.datatables.NpcChatTable;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@Component
public class NpcChatScheduler {
    private static final Logger logger = LogManager.getLogger(NpcChatScheduler.class);

    private static Calendar getRealTime() {
        TimeZone _tz = TimeZone.getTimeZone(ServerConfig.SERVER_TIME_ZONE);
        return Calendar.getInstance(_tz);
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        checkNpcChatTime(); // 채팅 개시 시간을 체크
    }

    private void checkNpcChatTime() {
        NpcChatTable.getInstance().getAllGameTime().forEach(npcChat -> {
            try {
                if (isChatTime(npcChat.getGameTime())) {
                    int npcId = npcChat.getNpcId();

                    L1World.getInstance().getAllObject().forEach(temp -> {
                        if (temp instanceof L1NpcInstance) {
                            L1NpcInstance obj = (L1NpcInstance) temp;

                            if (obj.getTemplate().getNpcId() == npcId) {
                                obj.startChat(L1NpcConstants.CHAT_TIMING_GAME_TIME);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    private boolean isChatTime(int chatTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        Calendar realTime = getRealTime();
        int nowTime = Integer.parseInt(sdf.format(realTime.getTime()));
        return (nowTime == chatTime);
    }
}
