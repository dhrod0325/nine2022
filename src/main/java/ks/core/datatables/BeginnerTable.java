package ks.core.datatables;

import ks.core.ObjectIdFactory;
import ks.model.bookMark.L1BookMark;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.*;

public class BeginnerTable {
    private static final Logger logger = LogManager.getLogger(BeginnerTable.class.getName());

    private static final BeginnerTable instance = new BeginnerTable();

    private final List<L1BookMark> bookMarks = new ArrayList<>();
    private final Map<String, List<Map<String, Object>>> beginItems = new HashMap<>();

    public static BeginnerTable getInstance() {
        return instance;
    }

    public void load() {
        loadBeginTeleport();
        loadBeginItems();
    }

    public void loadBeginTeleport() {
        bookMarks.clear();
        bookMarks.addAll(selectBeginTeleportList());
    }

    public List<L1BookMark> selectBeginTeleportList() {
        return SqlUtils.query("SELECT * FROM beginner_teleport order by ord,name", new BeanPropertyRowMapper<>(L1BookMark.class));
    }


    public void addBeginBookMarks(L1PcInstance pc) {
        int i = 1;

        List<String> colors = Arrays.asList("R", "S", "T", "U", "V", "X", "Y");

        for (L1BookMark temp : bookMarks) {
            L1BookMark bookMark = new L1BookMark();

            bookMark.setId(ObjectIdFactory.getInstance().nextId());
            bookMark.setCharId(pc.getId());
            bookMark.setName(temp.getName());
            bookMark.setLocX(temp.getLocX());
            bookMark.setLocY(temp.getLocY());
            bookMark.setMapId(temp.getMapId());
            bookMark.setOrd(temp.getOrd());
            bookMark.setNumId(i);
            bookMark.setColor(temp.getColor());

            SqlUtils.update("INSERT INTO character_teleport SET id = ?, char_id = ?, name = ?, locx = ?, locy = ?, mapid = ?, num_id=?",
                    bookMark.getId(),
                    bookMark.getCharId(),
                    bookMark.getBuildName(i),
                    bookMark.getLocX(),
                    bookMark.getLocY(),
                    bookMark.getMapId(),
                    bookMark.getNumId()
            );

            i++;
        }

        pc.loadBookMarks();
    }

    public void loadBeginItems() {
        try {
            beginItems.clear();

            Map<String, List<Map<String, Object>>> result = new HashMap<>();

            String sql = "SELECT * FROM beginner WHERE activate IN(?)";
            String[] types = {"P", "K", "E", "W", "D", "T", "B", "A"};

            for (String type : types) {
                List<Map<String, Object>> data = result.getOrDefault(type, new ArrayList<>());
                List<Map<String, Object>> list = SqlUtils.queryForList(sql, type);
                data.addAll(list);
                result.put(type, data);
            }

            beginItems.putAll(result);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void giveItem(L1PcInstance pc) {
        String type = "";

        if (pc.isCrown()) {
            type = "P";
        } else if (pc.isKnight()) {
            type = "K";
        } else if (pc.isElf()) {
            type = "E";
        } else if (pc.isWizard()) {
            type = "W";
        } else if (pc.isDarkElf()) {
            type = "D";
        } else if (pc.isDragonKnight()) {
            type = "T";
        } else if (pc.isIllusionist()) {
            type = "B";
        }

        List<Map<String, Object>> allItems = beginItems.get("A");
        List<Map<String, Object>> classItems = beginItems.get(type);

        allItems.addAll(classItems);

        for (Map<String, Object> item : allItems) {
            SqlUtils.update("INSERT INTO character_items SET id=?, item_id=?, char_id=?, item_name=?, count=?, is_equipped=?, enchantlvl=?, is_id=?, durability=?, charge_count=?, remaining_time=?, last_used=?, bless=?, attr_enchantlvl=?",
                    ObjectIdFactory.getInstance().nextId(),
                    item.get("item_id"),
                    pc.getId(),
                    item.get("item_name"),
                    item.get("count"),
                    0,
                    item.get("enchantlvl"),
                    1,
                    0,
                    item.get("charge_count"),
                    0,
                    null,
                    1,
                    0
            );
        }
    }
}
