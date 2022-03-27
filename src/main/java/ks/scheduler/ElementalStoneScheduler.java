package ks.scheduler;

import ks.app.LineageAppContext;
import ks.core.datatables.item.ItemTable;
import ks.model.L1GroundInventory;
import ks.model.L1Location;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1CheckTimer;
import ks.model.types.Point;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElementalStoneScheduler {
    private final Logger logger = LogManager.getLogger();

    private final L1Object dummy = new L1Object();

    private static final int ELVEN_FOREST_MAP_ID = 4;

    private static final int INTERVAL = 3;

    private static final int MAX_COUNT = 300;

    private static final int FIRST_X = 32911;
    private static final int FIRST_Y = 32210;

    private static final int LAST_X = 33141;
    private static final int LAST_Y = 32500;

    private static final int ELEMENTAL_STONE_ID = 40515;

    private final List<L1GroundInventory> itemList = new ArrayList<>(MAX_COUNT);

    private final L1CheckTimer timer = new L1CheckTimer();

    private static final String TIMER_KEY = "elementalStoneScheduler";

    @Scheduled(fixedDelay = 300)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1Map map = L1WorldMap.getInstance().getMap((short) ELVEN_FOREST_MAP_ID);

        removeItemsPickedUp();

        if (timer.isTimeOver(TIMER_KEY)) {
            if (itemList.size() < MAX_COUNT) {
                L1Location loc = new L1Location(nextPoint(), map);

                if (!canPut(loc)) {
                    return;
                }

                putElementalStone(loc);

                timer.setWaitTime(TIMER_KEY, INTERVAL * 1000);

                logger.trace("정령의돌 생성 : {}", loc);
            }
        }
    }

    private boolean canPut(L1Location loc) {
        dummy.setMap(loc.getMap());
        dummy.setX(loc.getX());
        dummy.setY(loc.getY());

        return L1World.getInstance().getVisiblePlayer(dummy).size() <= 0;
    }

    private Point nextPoint() {
        int newX = RandomUtils.nextInt(LAST_X - FIRST_X) + FIRST_X;
        int newY = RandomUtils.nextInt(LAST_Y - FIRST_Y) + FIRST_Y;

        return new Point(newX, newY);
    }

    private void removeItemsPickedUp() {
        itemList.removeAll(itemList
                .stream()
                .filter(inv -> !inv.checkItem(ELEMENTAL_STONE_ID))
                .collect(Collectors.toList()));
    }

    private void putElementalStone(L1Location loc) {
        L1GroundInventory gInventory = L1World.getInstance().getInventory(loc);
        L1ItemInstance item = ItemTable.getInstance().createItem(ELEMENTAL_STONE_ID);
        item.setEnchantLevel(0);
        item.setCount(1);
        gInventory.storeItem(item);
        itemList.add(gInventory);
    }
}
