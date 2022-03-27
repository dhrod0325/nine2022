package ks.model;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.datatables.bugCheck.BugCheckTable;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DropItem;
import ks.packets.serverpackets.S_RemoveObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class L1GroundInventory extends L1Inventory {
    private static final Logger logger = LogManager.getLogger(L1GroundInventory.class);

    private final Map<Integer, DeleteTimer> reservedTimers = new HashMap<>();

    public L1GroundInventory(int objectId, int x, int y, short map) {
        setId(objectId);

        setX(x);
        setY(y);
        setMap(map);

        L1World.getInstance().addVisibleObject(this);
    }

    private void setTimer(L1ItemInstance item) {
        if (!CodeConfig.ALT_ITEM_DELETION_TYPE.equalsIgnoreCase("std")) {
            return;
        }

        if (item.getItemId() == 40515) { // 정령의 돌
            return;
        }

        DeleteTimer deleteTimer = new DeleteTimer(item);
        deleteTimer.start();

        reservedTimers.put(item.getId(), deleteTimer);
    }

    private void cancelTimer(L1ItemInstance item) {
        DeleteTimer timer = reservedTimers.get(item.getId());

        if (timer == null) {
            return;
        }

        timer.cancel();
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        List<L1ItemInstance> list = getItems();

        for (L1ItemInstance item : new ArrayList<>(list)) {
            if (!perceivedFrom.getNearObjects().knownsObject(item)) {
                perceivedFrom.getNearObjects().addKnownObject(item);
                perceivedFrom.sendPackets(new S_DropItem(item));
            }
        }
    }

    // 인식 범위내에 있는 플레이어에 오브젝트 송신
    @Override
    public void insertItem(L1ItemInstance item) {
        setTimer(item);

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(item);

        for (L1PcInstance pc : list) {
            pc.sendPackets(new S_DropItem(item));
            pc.getNearObjects().addKnownObject(item);
        }

        BugCheckTable.getInstance().insertOrUpdate(item, item.getCount());
    }

    @Override
    public void updateItem(L1ItemInstance item) {
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(item);

        for (L1PcInstance pc : list) {
            pc.sendPackets(new S_DropItem(item));
        }

        BugCheckTable.getInstance().insertOrUpdate(item, item.getCount());
    }

    @Override
    public void deleteItem(L1ItemInstance item) {
        cancelTimer(item);

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(item);

        for (L1PcInstance pc : list) {
            pc.sendPackets(new S_RemoveObject(item));
            pc.getNearObjects().removeKnownObject(item);
        }

        items.remove(item);

        if (items.size() == 0) {
            L1World.getInstance().removeVisibleObject(this);
        }

        BugCheckTable.getInstance().delete(item, item.getCount());
    }

    private class DeleteTimer implements Runnable {
        private final L1ItemInstance item;

        private ScheduledFuture<?> deleteFuture;

        public DeleteTimer(L1ItemInstance item) {
            this.item = item;
        }

        public void start() {
            deleteFuture = LineageAppContext.commonTaskScheduler().schedule(this, Instant.now().plusMillis(1000 * 60 * 3));
        }

        @Override
        public void run() {
            try {
                if (!items.contains(item)) {
                    return;
                }

                removeItem(item);
            } catch (Throwable e) {
                logger.error(e);
            }
        }

        public void cancel() {
            if (deleteFuture != null) {
                deleteFuture.cancel(true);
                deleteFuture = null;
            }
        }
    }
}
