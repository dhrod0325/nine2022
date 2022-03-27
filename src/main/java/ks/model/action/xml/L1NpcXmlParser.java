package ks.model.action.xml;

import ks.model.L1Quest;
import ks.util.common.IterableElementList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L1NpcXmlParser {
    private final static Map<String, Integer> questIds = new HashMap<>();

    static {
        questIds.put("tutor", L1Quest.QUEST_TUTOR);//초보자도우미
        questIds.put("firstquest", L1Quest.QUEST_FIRSTQUEST);
        questIds.put("level15", L1Quest.QUEST_LEVEL15);
        questIds.put("level30", L1Quest.QUEST_LEVEL30);
        questIds.put("level45", L1Quest.QUEST_LEVEL45);
        questIds.put("level50", L1Quest.QUEST_LEVEL50);
        questIds.put("level70", L1Quest.QUEST_LEVEL70);
        questIds.put("lyra", L1Quest.QUEST_LYRA);
        questIds.put("oilskinmant", L1Quest.QUEST_OILSKINMANT);
        questIds.put("ruba", L1Quest.QUEST_RUBA);
        questIds.put("lukein", L1Quest.QUEST_LUKEIN1);
        questIds.put("tbox1", L1Quest.QUEST_TBOX1);
        questIds.put("tbox2", L1Quest.QUEST_TBOX2);
        questIds.put("tbox3", L1Quest.QUEST_TBOX3);
        questIds.put("cadmus", L1Quest.QUEST_CADMUS);
        questIds.put("resta", L1Quest.QUEST_RESTA);
        questIds.put("kamyla", L1Quest.QUEST_KAMYLA);
        questIds.put("lizard", L1Quest.QUEST_LIZARD);
        questIds.put("desire", L1Quest.QUEST_DESIRE);
        questIds.put("shadows", L1Quest.QUEST_SHADOWS);
        questIds.put("karif", L1Quest.QUEST_KARIF);
        questIds.put("icequeenring", L1Quest.QUEST_ICEQUEENRING);
    }

    public static List<L1NpcAction> listActions(Element element) {
        List<L1NpcAction> result = new ArrayList<>();
        NodeList list = element.getChildNodes();

        for (Element e : new IterableElementList(list)) {
            L1NpcAction action = L1NpcActionFactory.newAction(e);
            if (action != null) {
                result.add(action);
            }
        }

        return result;
    }

    public static Element getFirstChildElementByTagName(Element element, String tagName) {
        IterableElementList list = new IterableElementList(element.getElementsByTagName(tagName));

        for (Element elem : list) {
            return elem;
        }

        return null;
    }

    public static int getIntAttribute(Element element, String name, int defaultValue) {
        int result = defaultValue;

        try {
            result = Integer.parseInt(element.getAttribute(name));
        } catch (NumberFormatException ignored) {
        }

        return result;
    }

    public static boolean getBoolAttribute(Element element, String name, boolean defaultValue) {
        boolean result = defaultValue;
        String value = element.getAttribute(name);

        if (!value.equals("")) {
            result = Boolean.parseBoolean(value);
        }

        return result;
    }

    public static int parseQuestId(String questId) {
        if (questId.equals("")) {
            return -1;
        }

        Integer result = questIds.get(questId.toLowerCase());

        if (result == null) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    public static int parseQuestStep(String questStep) {
        if (questStep.equals("")) {
            return -1;
        }

        if (questStep.equalsIgnoreCase("End")) {
            return L1Quest.QUEST_END;
        }

        return Integer.parseInt(questStep);
    }
}
