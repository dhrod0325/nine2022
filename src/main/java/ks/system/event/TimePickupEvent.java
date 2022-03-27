package ks.system.event;

import ks.app.LineageAppContext;
import ks.core.datatables.item.ItemTable;
import ks.model.L1GroundInventory;
import ks.model.L1Location;
import ks.model.L1PolyMorph;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.item.function.L1DropItemList;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.model.trap.L1WorldTraps;
import ks.model.warehouse.WarehouseManager;
import ks.util.L1CommonUtils;
import ks.util.L1TeleportUtils;
import ks.util.common.SqlUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public class TimePickupEvent implements Runnable {
    public static final short MAP_ID = 612;
    private static final String EVENT_NAME = "단풍나무숲";
    private static final String EVENT_NAME_XML = "timePickupEvent";
    private static final int ITEM_ID = 60001235;

    private static final int WAIT_TIME = 30;

    private static final int DURATION = 60 * 2;

    private static final int TOTAL_DURATION = DURATION + (WAIT_TIME + 10);
    private final List<L1GroundInventory> dropInventorys = new CopyOnWriteArrayList<>();
    private final L1Map eventMap;
    private final List<L1PcInstance> itemUsers = new ArrayList<>();
    private ScheduledFuture<?> future;
    private int second = 0;
    private boolean deletedEventItems = false;
    private ScheduledFuture<?> dropItemFuture;
    private int dropItemState = 0;

    public TimePickupEvent() {
        eventMap = L1WorldMap.getInstance().getMap(MAP_ID);
    }

    public void start() {
        future = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(this, 1000);
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;

            itemUsers.clear();
            dropInventorys.clear();
        }

        if (dropItemFuture != null) {
            dropItemFuture.cancel(true);
            dropItemFuture = null;
        }
    }

    public int dropItemCount() {
        int count = 0;

        for (L1GroundInventory inv : dropInventorys) {
            count += inv.getSize();
        }

        return count;
    }

    @Override
    public void run() {
        second++;

        if (second == 1) {
            eventWaitStart();
            return;
        } else if (second < WAIT_TIME) {
            eventWait();
            return;
        } else if (second == WAIT_TIME) {
            eventStart();
            return;
        }

        if (second == TOTAL_DURATION) {
            eventEnd();
        } else if (second > TOTAL_DURATION - 10) {
            eventEndWait();
        } else {
            eventIng();
        }
    }

    private void sendAllMessage(String msg) {
        L1World.getInstance().broadcastPacketGreenMessage(msg);
        L1World.getInstance().broadcastServerMessage(msg);
    }

    private void eventWaitStart() {
        sendAllMessage(EVENT_NAME + " 이벤트 입장이 가능합니다");

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            L1ItemInstance item = ItemTable.getInstance().createItem(ITEM_ID);
            pc.getInventory().storeItem(item);
            itemUsers.add(pc);
        });

        L1WorldTraps.getInstance().stopTraps(MAP_ID);
    }

    private void eventEndWait() {
        sendAllMessage((TOTAL_DURATION - second) + "초 후에 " + EVENT_NAME + " 이벤트가 종료됩니다");
    }

    private void eventWait() {
        int t = WAIT_TIME - second;

        if (t <= 10) {
            sendAllMessage(t + "초 후에 " + EVENT_NAME + " 입장이 마감 됩니다");
        }
    }

    public void expendDrop() {
        L1Location location = new L1Location(eventMap.getX(), eventMap.getY(), MAP_ID);

        for (int i = 0; i <= 1000; i++) {
            L1Location loc = location.randomLocation(eventMap.getWidth());

            L1GroundInventory inv = L1World.getInstance().getInventory(loc);

            L1DropItemList dropItemList = L1DropItemList.get(EVENT_NAME_XML);
            L1DropItemList.DropItem item = dropItemList.getRandomItem();

            L1ItemInstance createItem = item.createItem();
            createItem.setDropMobId(1);

            inv.storeItem(createItem);

            dropInventorys.add(inv);
        }
    }

    private void eventStart() {
        Collection<L1PcInstance> users = getUsers();

        L1WorldTraps.getInstance().resetAllTraps(MAP_ID);

        sendAllMessage(EVENT_NAME + " 이벤트가 시작되었습니다");

        users.forEach(pc -> {
            L1CommonUtils.unEquipTurbun(pc);

            pc.removeHasteSkillEffect();
            pc.removeBraveSkillEffect();
            pc.removeFastMove();

            L1PolyMorph.doPoly(pc, 1154, TOTAL_DURATION, L1PolyMorph.MORPH_BY_GM);
        });

        deleteEventMoveItem();
    }

    private void eventIng() {
        if (dropItemCount() < 20 && dropItemState == 0) {
            sendAllMessage("잠시후 " + EVENT_NAME + "에 선물이 도착합니다");
            dropItemState = 1;

            if (dropItemFuture != null) {
                dropItemFuture.cancel(true);
                dropItemFuture = null;
            }

            dropItemFuture = LineageAppContext.commonTaskScheduler().schedule(this::expendDrop, Instant.now().plusMillis(1000 * 5));
        }
    }

    public void eventEnd() {
        Collection<L1PcInstance> users = getUsers();

        sendAllMessage(EVENT_NAME + " 이벤트가 종료되었습니다");

        users.forEach(pc -> {
            L1PolyMorph.undoPoly(pc);
            L1TeleportUtils.teleportToGiran(pc);
        });

        clearDropItems();

        if (!deletedEventItems) {
            deleteEventMoveItem();
        }

        stop();
    }

    public void clearDropItems() {
        for (L1GroundInventory inv : dropInventorys) {
            for (L1ItemInstance item : inv.getItems()) {
                inv.deleteItem(item);
            }
        }
    }

    public void setDropItemState(int dropItemState) {
        this.dropItemState = dropItemState;
    }

    public Collection<L1PcInstance> getUsers() {
        return L1World.getInstance().getAllPlayersByMap(MAP_ID);
    }

    public void deleteEventMoveItem() {
        deletedEventItems = true;

        SqlUtils.update("delete from character_elf_warehouse where item_id=?", ITEM_ID);
        SqlUtils.update("delete from character_warehouse where item_id=?", ITEM_ID);
        SqlUtils.update("delete from character_extra_warehouse where item_id=?", ITEM_ID);
        SqlUtils.update("delete from character_items where item_id=?", ITEM_ID);

        itemUsers.forEach(pc -> {
            if (pc != null) {
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    if (item.getItemId() == ITEM_ID) {
                        pc.getInventory().removeItem(item);
                    }
                }

                WarehouseManager.getInstance().reloadAll(pc.getAccountName());
            }
        });
    }
}
