package ks.model.item.function.option;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class L1OptionScroll {
    private static final Logger logger = LogManager.getLogger(L1OptionScroll.class);

    private static final String PATH = "./data/xml/Item/Option.xml";

    private static final L1OptionScroll instance = new L1OptionScroll();

    private final Map<String, L1OptionItem> dataMap = new HashMap<>();

    public L1OptionScroll() {
        load();
    }

    public static L1OptionScroll getInstance() {
        return instance;
    }

    public void load() {
        try {
            dataMap.clear();

            JAXBContext context = JAXBContext.newInstance(L1OptionList.class);

            Unmarshaller um = context.createUnmarshaller();

            File file = new File(PATH);
            L1OptionList list = (L1OptionList) um.unmarshal(file);

            for (L1OptionItem each : list) {
                each.init();
                dataMap.put(each.getType(), each);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public L1OptionItem getOption(String type) {
        return dataMap.get(type);
    }
}