package ks.listener;

import ks.app.LineageAppContext;
import ks.core.datatables.LightSpawnTable;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1FieldObjectInstance;
import ks.scheduler.timer.BaseTime;
import ks.scheduler.timer.gametime.GameTimeScheduler;
import org.springframework.stereotype.Component;

@Component
public class LightTimeListener extends TimeListenerAdapter {
    private static boolean isSpawn = true;

    public static LightTimeListener getInstance() {
        return LineageAppContext.getBean(LightTimeListener.class);
    }

    private void changeLight(BaseTime gameTime) {
        if (gameTime.isNight()) {
            turnOn();
        } else {
            turnOff();
        }
    }

    private void turnOn() {
        if (!isSpawn) {
            isSpawn = true;
            LightSpawnTable.getInstance().load();
        }
    }

    private void turnOff() {
        if (isSpawn) {
            isSpawn = false;

            for (L1Object object : L1World.getInstance().getAllObject()) {
                if (object instanceof L1FieldObjectInstance) {
                    L1FieldObjectInstance npc = (L1FieldObjectInstance) object;

                    // 81177, 81178, 81179, 81180, 81181
                    if (((npc.getTemplate().getNpcId() >= 81177
                            && npc.getTemplate().getNpcId() <= 81181)
                            || npc.getTemplate().getNpcId() == 4500000
                            || npc.getTemplate().getNpcId() == 4500002
                            || npc.getTemplate().getNpcId() == 81160)
                            && (npc.getMapId() == 0 || npc.getMapId() == 4)
                    ) {
                        npc.deleteMe();
                    }
                }
            }
        }
    }

    @Override
    public void onHourChanged(BaseTime time) {
        changeLight(time);
    }

    public void load() {
        isSpawn = !GameTimeScheduler.getInstance().getTime().isNight();
        changeLight(GameTimeScheduler.getInstance().getTime());
    }
}
