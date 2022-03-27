package ks.model.item.function.option;

import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.concurrent.CopyOnWriteArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1OptionItem {
    public static final Logger logger = LogManager.getLogger(L1OptionItem.class);
    @XmlAttribute(name = "Type")
    private String type;

    private int totalChance;

    @XmlElement(name = "Option")
    private CopyOnWriteArrayList<L1Option> items;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void init() {
        for (L1Option each : items) {
            totalChance += each.getChance();
        }

        if (totalChance != 0 && totalChance != 1000000) {
            logger.warn("확률의 합계가 100%가 되지 않습니다.");
        }
    }

    public int getTotalChance() {
        return totalChance;
    }

    public L1Option open() {
        int chance = 0;

        int r = RandomUtils.nextInt(getTotalChance());

        for (L1Option item : items) {
            chance += item.getChance();

            if (r < chance) {
                return item;
            }
        }

        return null;
    }
}
