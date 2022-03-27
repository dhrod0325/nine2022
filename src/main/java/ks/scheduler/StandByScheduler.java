package ks.scheduler;

import ks.app.LineageAppContext;
import ks.core.datatables.item.ItemTable;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.L1CommonUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StandByScheduler {
    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void run() {
        if (!LineageAppContext.isRun())
            return;

        if (!L1CommonUtils.isStandByServer()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            L1ItemInstance item = ItemTable.getInstance().createItem(6000083);
            item.setCount(1);
            item.setIdentified(true);

            pc.getInventory().storeItem(item);

            pc.sendPackets("오픈대기보상지급 : " + item.getLogName());
            pc.sendPackets(new S_SkillSound(pc.getId(), 1091));
        });
    }
}
