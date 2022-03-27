package ks.system.huntCheck.vo;

import ks.constants.L1ItemId;
import ks.core.datatables.MapsTable;
import ks.core.datatables.ResolventTable;
import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Hunt {
    private int mapId;
    private Date startTime;
    private Date endTime;
    private List<HuntCheck> huntCheckList = new ArrayList<>();

    public Hunt(int mapId) {
        startTime = new Date();
        this.mapId = mapId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<HuntCheck> getHuntCheckList() {
        return huntCheckList;
    }

    public void setHuntCheckList(List<HuntCheck> huntCheckList) {
        this.huntCheckList = huntCheckList;
    }

    public void addHuntCheck(HuntCheck vo) {
        huntCheckList.add(vo);
    }

    public long huntTimeMill() {
        return endTime.getTime() - startTime.getTime();
    }

    public void printInfo(int mapId, L1PcInstance pc) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (!huntCheckList.isEmpty()) {
            pc.sendPackets("맵 : " + MapsTable.getInstance().getMapName(mapId));
            pc.sendPackets("시작 : " + format.format(startTime));
            if (endTime != null) {
                pc.sendPackets("종료 : " + format.format(endTime));
                pc.sendPackets("사냥시간 : " + TimeUnit.MILLISECONDS.toMinutes(huntTimeMill()) + "분");
            }
        }

        int adenaSum = 0;
        int itemSum = 0;

        for (HuntCheck huntCheck : huntCheckList) {
            for (HuntCheckItem item : huntCheck.getHuntCheckItemList()) {
                if (item.getItemId() == L1ItemId.ADENA) {
                    adenaSum += item.getCount();
                }
            }
        }

        Map<Integer, L1ItemInstance> itemMap = new HashMap<>();

        for (HuntCheck huntCheck : huntCheckList) {
            for (HuntCheckItem item : huntCheck.getHuntCheckItemList()) {
                if (item.getItemId() != L1ItemId.ADENA) {
                    L1ItemInstance dropItem = ItemTable.getInstance().createItem(item.getItemId());
                    dropItem.setCount(0);
                    L1ItemInstance is = itemMap.getOrDefault(item.getItemId(), dropItem);

                    is.setCount(is.getCount() + item.getCount());
                    itemMap.put(item.getItemId(), is);

                    itemSum += ResolventTable.getInstance().getCrystalCount(item.getItemId());
                }
            }
        }

        pc.sendPackets("- 아덴 : " + NumberFormat.getInstance().format(adenaSum));
        pc.sendPackets("- 용해 : " + NumberFormat.getInstance().format(itemSum));
        pc.sendPackets("- 총합 : " + NumberFormat.getInstance().format(adenaSum + itemSum));

        for (L1ItemInstance item : itemMap.values()) {
            StringBuilder name = new StringBuilder();

            if (item.getBless() == 0) {
                name.append("[축] ");
            } else if (item.getBless() == 2) {
                name.append("[저] ");
            }

            name.append(item.getLogName());

            pc.sendPackets("- " + name + "");
        }
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
