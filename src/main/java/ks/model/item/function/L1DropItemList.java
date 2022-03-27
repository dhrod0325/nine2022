package ks.model.item.function;

import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1DropItemList {
    private static final Logger logger = LogManager.getLogger(L1DropItemList.class);
    private static final String path = "./data/xml/Item/DropItem.xml";

    private static final Map<String, L1DropItemList> dataMap = new HashMap<>();

    @XmlAttribute(name = "key")
    public String key;

    @XmlElement(name = "item")
    public List<DropItem> dropItemList;

    private static void loadXml() {
        try {
            dataMap.clear();

            JAXBContext context = JAXBContext.newInstance(DropItemListRoot.class);

            Unmarshaller um = context.createUnmarshaller();

            File file = new File(path);
            DropItemListRoot list = (DropItemListRoot) um.unmarshal(file);

            for (L1DropItemList each : list)
                if (each.init()) {
                    dataMap.put(each.key, each);
                }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static L1DropItemList get(String key) {
        return dataMap.get(key);
    }

    public static void load() {
        loadXml();
    }

    public DropItem getRandomItem() {
        int totalChance = getTotalChance();
        int randomChance = RandomUtils.nextInt(totalChance);

        int chance = 0;

        for (DropItem item : dropItemList) {
            chance += item.getChance();

            if (randomChance < chance) {
                return item;
            }
        }

        return null;
    }

    public int getTotalChance() {
        int chance = 0;

        for (DropItem item : dropItemList) {
            chance += item.getChance();
        }

        return chance;
    }

    private boolean init() {
        int totalChance = 0;

        for (DropItem each : dropItemList) {
            if (ItemTable.getInstance().getTemplate(each.itemId) == null) {
                logger.warn("존재하지 않는 아이템번호입니다. " + each.itemId);
                return false;
            }

            totalChance += each.getChance();
        }

        if (totalChance != 1000000) {
            logger.warn("확률 오류: 합계가 " + totalChance + "%입니다. 100%로 설정해 주세요.");
        }

        return true;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "DropItemList")
    private static class DropItemListRoot implements Iterable<L1DropItemList> {
        @XmlElement(name = "items")
        public List<L1DropItemList> list;

        public Iterator<L1DropItemList> iterator() {
            return this.list.iterator();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DropItem {
        @XmlAttribute(name = "itemId")
        public int itemId;

        @XmlAttribute(name = "max")
        public int max;

        @XmlAttribute(name = "min")
        public int min;

        @XmlAttribute(name = "chance")
        public double chance;

        public double getChance() {
            return chance * 10000;
        }

        public L1ItemInstance createItem() {
            L1ItemInstance createItem = ItemTable.getInstance().createItem(itemId);
            createItem.setCount(RandomUtils.nextInt(min, max));

            return createItem;
        }
    }
}
