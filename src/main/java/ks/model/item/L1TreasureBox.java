package ks.model.item;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Inventory;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {
    private static final Logger logger = LogManager.getLogger(L1TreasureBox.class.getName());
    private static final String PATH = "./data/xml/Item/TreasureBox.xml";
    private static final Map<Integer, L1TreasureBox> _dataMap = new HashMap<>();

    @XmlAttribute(name = "ItemId")
    private int boxId;

    @XmlAttribute(name = "Type")
    private TYPE _type;

    @XmlAttribute(name = "ReqCount")
    private Integer reqCount;

    @XmlElement(name = "Item")
    private CopyOnWriteArrayList<Item> _items;

    private int _totalChance;

    public static L1TreasureBox get(int id) {
        return _dataMap.get(id);
    }

    public static void load() {
        try {
            JAXBContext context = JAXBContext.newInstance(L1TreasureBox.TreasureBoxList.class);

            Unmarshaller um = context.createUnmarshaller();

            File file = new File(PATH);

            TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

            for (L1TreasureBox each : list) {
                each.init();
                _dataMap.put(each.getBoxId(), each);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private static void storeItem(L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }

        L1Inventory inventory;

        if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
            inventory = pc.getInventory();
        } else {
            inventory = L1World.getInstance().getInventory(pc.getLocation());
        }

        inventory.storeItem(item);

        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
    }

    public Integer getReqCount() {
        if (reqCount == null) {
            return 1;
        }

        return reqCount;
    }

    public void setReqCount(Integer reqCount) {
        this.reqCount = reqCount;
    }

    public int getBoxId() {
        return boxId;
    }

    public TYPE getType() {
        return _type;
    }

    public List<Item> getItems() {
        return _items;
    }

    public int getTotalChance() {
        return _totalChance;
    }

    private void init() {
        List<Item> items = getItems();

        if (items == null) {
            logger.error("아이템목록이 널임 : " + getBoxId());

            return;
        }

        for (Item each : items) {
            _totalChance += each.getChance();
            if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
                getItems().remove(each);
                logger.warn("아이템 ID " + each.getItemId() + " 의 템플릿이 발견되지 않았습니다.");
            }
        }

        if (getTotalChance() != 0 && getTotalChance() != 1000000) {
            logger.warn("ID " + getBoxId() + "의 확률의 합계가 100%가 되지 않습니다.");
        }
    }

    public List<L1ItemInstance> open(L1PcInstance pc) {
        List<L1ItemInstance> result = new ArrayList<>();

        if (getType().equals(TYPE.SPECIFIC)) {
            for (Item each : getItems()) {
                if (each.isExclude(pc)) {
                    continue;
                }

                L1ItemInstance item = ItemTable.getInstance().createItem(each.getItemId());

                if (item != null && !isNotOpenAble(pc)) {
                    item.setCount(each.getCount());
                    item.setEnchantLevel(each.getEnchant());
                    item.setAttrEnchantLevel(each.getAttr());
                    item.setIdentified(each.getIdentified());

                    if (each.getRandomBless() != null) {
                        if (RandomUtils.isWinning(100, each.getRandomBless())) {
                            if (each.getBless() != null) {
                                item.setBless(each.getBless());
                            } else {
                                item.setBless(0);
                            }
                        }
                    }

                    storeItem(pc, item);

                    result.add(item);
                } else {
                    return null;
                }
            }
        } else if (getType().equals(TYPE.RANDOM)) {
            int chance = 0;

            int r = RandomUtils.nextInt(getTotalChance());

            for (Item each : getItems()) {
                if (each.isExclude(pc)) {
                    continue;
                }

                chance += each.getChance();

                if (r < chance) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(each.getItemId());

                    if (item != null && !isNotOpenAble(pc)) {
                        item.setCount(each.getCount());
                        item.setEnchantLevel(each.getEnchant());
                        item.setAttrEnchantLevel(each.getAttr());
                        item.setIdentified(each.getIdentified());

                        if (each.getBless() != null) {
                            item.setBless(each.getBless());
                        }

                        if (each.getRandomBless() != null) {
                            if (RandomUtils.isWinning(100, each.getRandomBless())) {
                                if (each.getBless() != null) {
                                    item.setBless(each.getBless());
                                } else {
                                    item.setBless(0);
                                }
                            }
                        }

                        if (each.getMent() != null) {
                            L1World.getInstance().broadcastPacketGreenMessage(each.getMent());
                        }

                        storeItem(pc, item);
                        result.add(item);
                    } else {
                        return null;
                    }
                    break;
                }
            }
        }

        return result;
    }

    public boolean isNotOpenAble(L1PcInstance pc) {
        boolean check = pc.getInventory().isFullWeightOrFullCount();

        if (check) {
            pc.sendPackets(new S_ServerMessage(82));
        }

        return check;
    }

    public enum TYPE {
        RANDOM, SPECIFIC
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "TreasureBoxList")
    private static class TreasureBoxList implements Iterable<L1TreasureBox> {
        @XmlElement(name = "TreasureBox")
        private List<L1TreasureBox> _list;

        public Iterator<L1TreasureBox> iterator() {
            return _list.iterator();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        @XmlAttribute(name = "ItemId")
        private int _itemId;

        @XmlAttribute(name = "Count")
        private int _count;

        @XmlAttribute(name = "Enchant")
        private int _enchant;

        @XmlAttribute(name = "Attr")
        private int _attr;

        @XmlAttribute(name = "Identi")
        private boolean _identified;

        @XmlAttribute(name = "exclude")
        private String exclude;

        @XmlAttribute(name = "randomCount")
        private String randomCount;

        @XmlAttribute(name = "bless")
        private Integer bless;

        @XmlAttribute(name = "RandomBless")
        private Integer randomBless;

        @XmlAttribute(name = "Ment")
        private String _ment;

        private int _chance;

        public String getMent() {
            return _ment;
        }

        public Integer getBless() {
            return bless;
        }

        public void setBless(Integer bless) {
            this.bless = bless;
        }

        public Integer getRandomBless() {
            return randomBless;
        }

        public void setRandomBless(Integer randomBless) {
            this.randomBless = randomBless;
        }

        public String getRandomCount() {
            return randomCount;
        }

        public void setRandomCount(String randomCount) {
            this.randomCount = randomCount;
        }

        public int random1() {
            if (!StringUtils.isEmpty(randomCount)) {
                return Integer.parseInt(randomCount.split(",")[0]);
            }

            return 0;
        }

        public boolean isRandom() {
            return !StringUtils.isEmpty(randomCount);
        }

        public int random2() {
            if (!StringUtils.isEmpty(randomCount)) {
                return Integer.parseInt(randomCount.split(",")[1]);
            }

            return 0;
        }

        public int randomValue() {
            return RandomUtils.nextInt(random1(), random2());
        }

        // 아이템 id
        public int getItemId() {
            return _itemId;
        }

        // 아이템 갯수
        public int getCount() {
            if (isRandom()) {
                return randomValue();
            } else {
                return _count;
            }
        }

        // 아이템 인첸트 레벨
        public int getEnchant() {
            return _enchant;
        }

        // 속성 인첸트 레벨
        public int getAttr() {
            return _attr;
        }

        // 확인 상태
        public boolean getIdentified() {
            return _identified;
        }

        // 챈스 확률
        public double getChance() {
            return _chance;
        }

        @XmlAttribute(name = "Chance")
        private void setChance(double chance) {
            _chance = (int) (chance * 10000);
        }

        public String getExclude() {
            return exclude;
        }

        public void setExclude(String exclude) {
            this.exclude = exclude;
        }

        public boolean isExclude(L1PcInstance pc) {
            if (getExcludeList().contains(0) && pc.isCrown()) {
                return true;
            }

            if (getExcludeList().contains(1) && pc.isKnight()) {
                return true;
            }

            if (getExcludeList().contains(2) && pc.isElf()) {
                return true;
            }

            if (getExcludeList().contains(3) && pc.isWizard()) {
                return true;
            }

            return getExcludeList().contains(4) && pc.isDarkElf();
        }

        public List<Integer> getExcludeList() {
            if (!StringUtils.isEmpty(exclude)) {
                List<Integer> result = new ArrayList<>();

                String[] lists = exclude.split(",");

                for (String o : lists) {
                    result.add(Integer.valueOf(o));
                }

                return result;
            }

            return Collections.emptyList();
        }
    }
}
