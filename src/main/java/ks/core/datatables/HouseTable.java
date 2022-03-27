package ks.core.datatables;

import ks.model.L1House;
import ks.util.common.DateUtils;
import ks.util.common.SqlUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HouseTable {
    private static final HouseTable instance = new HouseTable();

    private final Map<Integer, L1House> houseMap = new ConcurrentHashMap<>();

    public static HouseTable getInstance() {
        return instance;
    }

    public static List<Integer> getHouseIdList() {
        return SqlUtils.queryForList("SELECT house_id FROM house ORDER BY house_id", Integer.class);
    }

    public void load() {
        houseMap.clear();

        List<L1House> list = selectList();

        for (L1House o : list) {
            houseMap.put(o.getHouseId(), o);
        }
    }

    public List<L1House> selectList() {
        return SqlUtils.query("SELECT * FROM house ORDER BY house_id", (rs, i) -> {
            L1House house = new L1House();
            house.setHouseId(rs.getInt(1));
            house.setHouseName(rs.getString(2));
            house.setHouseArea(rs.getInt(3));
            house.setLocation(rs.getString(4));
            house.setKeeperId(rs.getInt(5));
            house.setOnSale(rs.getInt(6) == 1);
            house.setPurchaseBasement(rs.getInt(7) == 1);
            house.setTaxDeadline(DateUtils.timestampToCalendar((Timestamp) rs.getObject(8)));

            return house;
        });
    }

    public Collection<L1House> getHouseTableList() {
        return houseMap.values();
    }

    public L1House getHouseTable(int houseId) {
        return houseMap.get(houseId);
    }

    public void updateHouse(L1House house) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fm = formatter.format(house.getTaxDeadline().getTime());

        SqlUtils.update("UPDATE house SET house_name=?, house_area=?, location=?, keeper_id=?, is_on_sale=?, is_purchase_basement=?, tax_deadline=? WHERE house_id=?",
                house.getHouseName(),
                house.getHouseArea(),
                house.getLocation(),
                house.getKeeperId(),
                house.isOnSale() ? 1 : 0,
                house.isPurchaseBasement() ? 1 : 0,
                fm,
                house.getHouseId()
        );
    }
}
