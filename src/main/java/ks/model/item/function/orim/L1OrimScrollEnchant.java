package ks.model.item.function.orim;

import ks.core.datatables.item.ItemTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("ALL")
@XmlAccessorType(XmlAccessType.FIELD)
public class L1OrimScrollEnchant {
    private static final Logger logger = LogManager.getLogger(L1OrimScrollEnchant.class);
    private static final String path = "./data/xml/Item/OrimScrollEnchant.xml";

    private static HashMap<Integer, L1OrimScrollEnchant> _dataMap = new HashMap<>();

    @XmlAttribute(name = "ItemId")
    private int _antiqueBookId;

    @XmlAttribute(name = "TargetItemId")
    private String _targetItemIds;

    @XmlAttribute(name = "TargetType")
    private int _targetType;

    @XmlElement(name = "Effect")
    private CopyOnWriteArrayList<L1OrimScrollEffect> _L1OrimScroll_effects;

    public static L1OrimScrollEnchant get(int id) {
        return _dataMap.get(id);
    }

    public static void loadXml(HashMap<Integer, L1OrimScrollEnchant> dataMap) {
        try {
            JAXBContext context = JAXBContext.newInstance(L1OrimScrollItemEffectList.class);

            Unmarshaller um = context.createUnmarshaller();

            File file = new File(path);
            L1OrimScrollItemEffectList list = (L1OrimScrollItemEffectList) um.unmarshal(file);

            for (L1OrimScrollEnchant each : list)
                if (each.init())
                    dataMap.put(each.getAntiqueBookId(), each);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void load() {
        loadXml(_dataMap);
    }

    public static void reload() {
        HashMap<Integer, L1OrimScrollEnchant> dataMap = new HashMap<>();
        loadXml(dataMap);
        _dataMap = dataMap;
    }

    private int getAntiqueBookId() {
        return this._antiqueBookId;
    }

    public String getTargetItemIds() {
        return this._targetItemIds;
    }

    public int getTargetType() {
        return this._targetType;
    }

    public List<L1OrimScrollEffect> getEffects() {
        return this._L1OrimScroll_effects;
    }

    private boolean init() {
        if (ItemTable.getInstance().getTemplate(getAntiqueBookId()) == null) {
            logger.warn("존재하지 않는 아이템번호입니다. " + getAntiqueBookId());
            return false;
        }
        String[] itemIdArray;
        if (getTargetItemIds() != null) {
            itemIdArray = getTargetItemIds().split(",");

            for (String s : itemIdArray) {
                if (ItemTable.getInstance().getTemplate(Integer.parseInt(s)) == null) {
                    logger.warn("존재하지 않는 아이템번호입니다. " + s);
                    return false;
                }
            }
        }
        for (L1OrimScrollEffect each : getEffects()) {
            int totalChance = 0;
            String[] probArray = each.getProbs().split(",");

            for (String s : probArray) {
                totalChance += Integer.parseInt(s);
            }
            if (totalChance != 100) {
                logger.warn("(ID:" + getAntiqueBookId() + " / Enchant:" + each.getEnchantLevel() + ") 확률 오류: 합계가 " + totalChance + "%입니다. 100%로 설정해 주세요.");
            }
        }

        return true;
    }


}