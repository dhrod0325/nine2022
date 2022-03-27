package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.datatables.item.ItemTable;
import ks.model.L1CalcExp;
import ks.model.L1Inventory;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class FishingTimeScheduler {
    private static final Logger logger = LogManager.getLogger(FishingTimeScheduler.class);

    private final List<L1PcInstance> fishingList = new CopyOnWriteArrayList<>();

    public static FishingTimeScheduler getInstance() {
        return LineageAppContext.getBean(FishingTimeScheduler.class);
    }

    //1시간을 돌리려면 미끼가 120개 필요함
    //5시간을 돌리려면 미끼가 600개 필요함
    //5시간하면 파대 하나 나오도록 용결 20개
    @Scheduled(fixedDelay = 1000 * 30)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        fishing();
    }

    public void addMember(L1PcInstance pc) {
        if (fishingList.contains(pc)) {
            return;
        }

        fishingList.add(pc);
    }

    public void removeMember(L1PcInstance pc) {
        if (!fishingList.contains(pc)) {
            return;
        }

        fishingList.remove(pc);
    }

    private void fishing() {
        fishingList.stream().filter(Objects::nonNull).forEach(pc -> {
            try {
                if (pc.getMapId() != L1Map.MAP_FISHING)
                    return;

                if (pc.isFishing()) {
                    if (L1CommonUtils.isStandByServer(pc)) {
                        pc.endFishing();
                        return;
                    }

                    if (pc.getInventory().consumeItem(41295, 1)) {
                        L1CalcExp.addExp(pc, CodeConfig.FISHING_EXP, 0);

                        int chance = RandomUtils.nextInt(100);

                        if (chance <= 50) {
                            successFishing(pc);
                        }
                    } else {
                        pc.endFishing();
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    private void successFishing(L1PcInstance pc) {
        L1ItemInstance item = ItemTable.getInstance().createItem(6000057);
        item.setCount(1);

        if (pc.getInventory().checkAddItem(item, 1) != L1Inventory.OK) {
            pc.sendPackets(new S_ServerMessage(263));
            return;
        }

        pc.getInventory().storeItem(item);
        pc.sendPackets(new S_SystemMessage("낚시에 성공하여 낚시상자를 획득하였습니다."));
    }
}
