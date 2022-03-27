package ks.core.datatables.getback;

import ks.model.L1Location;
import ks.model.L1Party;
import ks.model.L1TownLocation;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetBackTable {
    private static final Map<Integer, List<GetBack>> dataMap = new HashMap<>();

    private static final GetBackTable instance = new GetBackTable();

    public static GetBackTable getInstance() {
        return instance;
    }

    public void load() {
        dataMap.clear();

        List<GetBack> list = selectList();

        for (GetBack getBack : list) {
            List<GetBack> getbackList = dataMap.computeIfAbsent(getBack.areaMapId, k -> new ArrayList<>());
            getbackList.add(getBack);
        }
    }

    public List<GetBack> selectList() {
        return SqlUtils.query("SELECT * FROM getback ORDER BY area_mapid,area_x1 DESC", (rs, i) -> {
            GetBack getback = new GetBack();
            getback.areaX1 = rs.getInt("area_x1");
            getback.areaY1 = rs.getInt("area_y1");
            getback.areaX2 = rs.getInt("area_x2");
            getback.areaY2 = rs.getInt("area_y2");
            getback.areaMapId = rs.getInt("area_mapid");

            getback.getbackX1 = rs.getInt("getback_x1");
            getback.getbackY1 = rs.getInt("getback_y1");
            getback.getbackX2 = rs.getInt("getback_x2");
            getback.getbackY2 = rs.getInt("getback_y2");
            getback.getbackX3 = rs.getInt("getback_x3");
            getback.getbackY3 = rs.getInt("getback_y3");

            getback.getbackMapId = rs.getInt("getback_mapid");
            getback.getbackTownId = rs.getInt("getback_townid");
            getback.getbackTownIdForElf = rs.getInt("getback_townid_elf");
            getback.getbackTownIdForDarkelf = rs.getInt("getback_townid_darkelf");

            return getback;
        });
    }

    public L1Location getBackLocationObject(L1PcInstance pc) {
        int[] backLocation = getBackLocation(pc);

        return new L1Location(backLocation[0], backLocation[1], (short) backLocation[2]);
    }

    public int[] getBackLocation(L1PcInstance pc) {
        int[] loc = new int[3];

        int nPosition = RandomUtils.nextInt(3);

        int pcLocX = pc.getX();
        int pcLocY = pc.getY();
        int pcMapId = pc.getMapId();

        List<GetBack> getBackList = dataMap.get(pcMapId);

        if (pc.isInParty()) {//파티추가
            if (pc.isDead()) {
                L1Party party = pc.getParty();

                if (party != null) {
                    party.refresh(pc);
                }
            }
        }

        if (getBackList != null) {
            GetBack getback = null;

            for (GetBack gb : getBackList) {
                if (gb.isSpecifyArea()) {
                    if (gb.areaX1 <= pcLocX && pcLocX <= gb.areaX2 && gb.areaY1 <= pcLocY && pcLocY <= gb.areaY2) {
                        getback = gb;
                        break;
                    }
                } else {
                    getback = gb;
                    break;
                }
            }

            if (getback == null) {
                loc[0] = 33089;
                loc[1] = 33397;
                loc[2] = 4;
            } else {
                loc = readGetBackInfo(getback, nPosition);

                if (pc.isElf() && getback.getbackTownIdForElf > 0) {
                    loc = L1TownLocation.getGetBackLoc(getback.getbackTownIdForElf);
                } else if (pc.isDarkElf() && getback.getbackTownIdForDarkelf > 0) {
                    loc = L1TownLocation.getGetBackLoc(getback.getbackTownIdForDarkelf);
                } else if (getback.getbackTownId > 0) {
                    loc = L1TownLocation.getGetBackLoc(getback.getbackTownId);
                }
            }
        } else {
            loc[0] = 33089;
            loc[1] = 33397;
            loc[2] = 4;
        }
        if (loc[0] == 0 || loc[1] == 0) {
            loc[0] = 33089;
            loc[1] = 33397;
            loc[2] = 4;
        }

        return loc;
    }

    private int[] readGetBackInfo(GetBack getback, int nPosition) {
        int[] loc = new int[3];
        switch (nPosition) {
            case 0:
                loc[0] = getback.getbackX1;
                loc[1] = getback.getbackY1;
                break;
            case 1:
                loc[0] = getback.getbackX2;
                loc[1] = getback.getbackY2;
                break;
            case 2:
                loc[0] = getback.getbackX3;
                loc[1] = getback.getbackY3;
                break;
        }

        loc[2] = getback.getbackMapId;

        return loc;
    }
}
