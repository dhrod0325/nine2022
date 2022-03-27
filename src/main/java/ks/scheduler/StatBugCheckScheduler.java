package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.ServerConfig;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

@Component
public class StatBugCheckScheduler {
    private static final Logger logger = LogManager.getLogger(StatBugCheckScheduler.class);

    @Scheduled(fixedDelay = 1000 * 50)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        checkBaseStat();
    }

    private Calendar getRealTime() {
        TimeZone _tz = TimeZone.getTimeZone(ServerConfig.SERVER_TIME_ZONE);
        return Calendar.getInstance(_tz);
    }

    private void sendMessage(String msg) {
        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : players) {
            if (pc.isGm())
                pc.sendPackets(new S_ChatPacket(pc, msg, L1Opcodes.S_OPCODE_MSG, 18));
        }
    }

    private void checkBaseStat() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        int nowTime = Integer.parseInt(sdf.format(getRealTime().getTime()));
        int time2 = 30;

        if (nowTime % time2 == 0) {
            try {
                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                players.forEach((pc) -> {
                    if (pc.isGm()) {
                        return;
                    }

                    if (pc.getAbility().getBaseStr() >= 36) {
                        sendMessage("버그 시도: [" + pc.getName() + "]");
                        pc.disconnect();
                    } else if (pc.getAbility().getBaseCon() >= 36) {
                        sendMessage("버그 시도: [" + pc.getName() + "]");
                        pc.disconnect();
                    } else if (pc.getAbility().getBaseDex() >= 36) {
                        sendMessage("버그 시도: [" + pc.getName() + "]");
                        pc.disconnect();
                    } else if (pc.getAbility().getBaseInt() >= 36) {
                        sendMessage("버그 시도: [" + pc.getName() + "]");
                        pc.disconnect();
                    } else if (pc.getAbility().getBaseWis() >= 36) {
                        sendMessage("버그 시도: [" + pc.getName() + "]");
                        pc.disconnect();
                    }

                    if (pc.getAbility().getSp() > 100) {
                        sendMessage("stat 버그 시도: [" + pc.getName() + "]");
                    }

                    if (pc.getAC().getAc() < -150) {
                        sendMessage("ac 버그 의심: [" + pc.getName() + "ac : " + pc.getAC().getAc() + "]");
                    }

                    int totalDamage = pc.getTotalDmg() + pc.getAddDmg();
                    int totalHitUp = pc.getTotalHitUp() + pc.getTotalHitUp();

                    if (totalDamage > 100) {
                        sendMessage("대미지 버그 의심: [" + pc.getName() + "totalDamage : " + totalDamage + "]");
                    }

                    if (totalHitUp > 100) {
                        sendMessage("대미지 버그 의심: [" + pc.getName() + "totalHitUp : " + totalHitUp + "]");
                    }
                });
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }
}
