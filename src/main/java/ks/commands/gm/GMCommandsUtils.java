package ks.commands.gm;

import ks.constants.L1DataMapKey;
import ks.model.L1ItemSetItem;
import ks.model.L1Location;
import ks.model.pc.L1PcInstance;
import ks.util.common.IterableElementList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class GMCommandsUtils {
    private static final Logger logger = LogManager.getLogger(GMCommandsUtils.class);
    private static final Map<String, ConfigLoader> loaders = new HashMap<>();
    public static Map<String, L1Location> ROOMS = new LinkedHashMap<>();
    public static Map<String, List<L1ItemSetItem>> ITEM_SETS = new HashMap<>();

    static {
        loaders.put("roomlist", new RoomLoader());
        loaders.put("itemsetlist", new ItemSetLoader());
    }

    public static boolean isDebug(L1PcInstance pc) {
        if (pc != null && pc.isGm()) {
            return "on".equalsIgnoreCase(pc.getDataMap().get(L1DataMapKey.GM_DEBUG));
        }

        return false;
    }

    private static Document loadXml() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse("./data/xml/GmCommands/GMCommands.xml");
    }

    public static void load() {
        try {
            Document doc = loadXml();
            NodeList nodes = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                ConfigLoader loader = loaders.get(nodes.item(i).getNodeName().toLowerCase());

                if (loader != null) {
                    loader.load((Element) nodes.item(i));
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private interface ConfigLoader {
        void load(Element element);
    }

    private abstract static class ListLoaderAdapter implements ConfigLoader {
        private final String _listName;

        public ListLoaderAdapter(String listName) {
            _listName = listName;
        }

        public final void load(Element element) {
            NodeList nodes = element.getChildNodes();
            for (Element elem : new IterableElementList(nodes)) {
                if (elem.getNodeName().equalsIgnoreCase(_listName)) {
                    loadElement(elem);
                }
            }
        }

        public abstract void loadElement(Element element);
    }

    private static class RoomLoader extends ListLoaderAdapter {
        public RoomLoader() {
            super("Room");
        }

        @Override
        public void loadElement(Element element) {
            String name = element.getAttribute("Name");
            int locX = Integer.parseInt(element.getAttribute("LocX"));
            int locY = Integer.parseInt(element.getAttribute("LocY"));
            int mapId = Integer.parseInt(element.getAttribute("MapId"));
            ROOMS.put(name.toLowerCase(), new L1Location(locX, locY, mapId));
        }
    }

    private static class ItemSetLoader extends ListLoaderAdapter {
        public ItemSetLoader() {
            super("ItemSet");
        }

        public L1ItemSetItem loadItem(Element element) {
            int id = Integer.parseInt(element.getAttribute("Id"));
            int amount = Integer.parseInt(element.getAttribute("Amount"));
            int enchant = Integer.parseInt(element.getAttribute("Enchant"));

            return new L1ItemSetItem(id, amount, enchant);
        }

        @Override
        public void loadElement(Element element) {
            List<L1ItemSetItem> list = new ArrayList<>();
            NodeList nodes = element.getChildNodes();
            for (Element elem : new IterableElementList(nodes)) {
                if (elem.getNodeName().equalsIgnoreCase("Item")) {
                    list.add(loadItem(elem));
                }
            }
            String name = element.getAttribute("Name");
            ITEM_SETS.put(name.toLowerCase(), list);
        }
    }
}
