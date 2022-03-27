package ks.scheduler.timer.gametime;

import ks.app.LineageAppContext;
import ks.model.L1World;
import ks.packets.serverpackets.S_GameTime;
import ks.scheduler.timer.TimeScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameTimeScheduler extends TimeScheduler<GameTime> {
    public static GameTimeScheduler getInstance() {
        return LineageAppContext.getBean(GameTimeScheduler.class);
    }

    @Scheduled(fixedDelay = 500)
    public void scheduled() {
        super.run();

        int serverTime = getTime().getSeconds();

        if (serverTime % 60 == 0) {
            L1World.getInstance().broadcastPacketToAll(new S_GameTime(serverTime));
        }
    }
}
