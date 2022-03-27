package ks.listener;

import ks.app.LineageAppContext;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.getback.GetBackTable;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.scheduler.timer.BaseTime;
import ks.system.timeDungeon.L1TimeDungeonData;
import org.springframework.stereotype.Component;

@Component
public class L1TimeDungeonListener extends TimeListenerAdapter {
    public static L1TimeDungeonListener getInstance() {
        return LineageAppContext.getBean(L1TimeDungeonListener.class);
    }

    @Override
    public void onSecondChanged(BaseTime time) {
        if (!LineageAppContext.isRun()) {
            return;
        }

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getTimeDungeon().isTimeDungeon(pc.getMapId())) {
                L1TimeDungeonData data = pc.getTimeDungeon().getTimeDungeonData(pc.getMapId());

                if (data == null) {
                    continue;
                }

                if (data.isTimeOver()) {
                    L1Location backLocation = GetBackTable.getInstance().getBackLocationObject(pc);
                    L1Teleport.teleport(pc, backLocation, pc.getHeading(), true);
                    pc.sendPackets("던전 이용시간이 종료되었습니다");
                } else {
                    data.setUseSecond(data.getUseSecond() + 1);
                    pc.getTimeDungeon().saveTimeDungeonData();

                    if (time.getSeconds() / 3 == 0) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.MAP_TIMER, data.getRemainingMinute() * 60));
                    }

                    if (time.getSeconds() / (60 * 10) == 0) {
                        pc.sendPackets("남은 던전 시간은 " + data.getRemainingMinute() + "분 입니다");
                    }
                }
            }
        }
    }

    @Override
    public void onDayChanged(BaseTime time) {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            pc.getTimeDungeon().loadTimeDungeon();
        }
    }
}
