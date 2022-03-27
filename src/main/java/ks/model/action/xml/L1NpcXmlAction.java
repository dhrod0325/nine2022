package ks.model.action.xml;

import ks.model.L1Object;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.IntRange;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class L1NpcXmlAction implements L1NpcAction {
    private final static Map<Character, Integer> charTypes = new HashMap<>();

    static {
        charTypes.put('P', 0);
        charTypes.put('K', 1);
        charTypes.put('E', 2);
        charTypes.put('W', 3);
        charTypes.put('D', 4);
        charTypes.put('T', 5);
        charTypes.put('B', 6);
    }

    private final int[] npcIds;
    private final IntRange level;
    private final int questId;
    private final int questStep;
    private final int[] classes;
    private String name;

    public L1NpcXmlAction(Element element) {
        name = element.getAttribute("Name");
        name = name.equals("") ? null : name;
        npcIds = parseNpcIds(element.getAttribute("NpcId"));
        level = parseLevel(element);
        questId = L1NpcXmlParser.parseQuestId(element.getAttribute("QuestId"));
        questStep = L1NpcXmlParser.parseQuestStep(element.getAttribute("QuestStep"));
        classes = parseClasses(element);
    }

    private int[] parseClasses(Element element) {
        String classes = element.getAttribute("Class").toUpperCase();
        int[] result = new int[classes.length()];
        int idx = 0;

        for (Character cha : classes.toCharArray()) {
            result[idx++] = charTypes.get(cha);
        }

        Arrays.sort(result);

        return result;
    }

    private IntRange parseLevel(Element element) {
        int level = L1NpcXmlParser.getIntAttribute(element, "Level", 0);
        int min = L1NpcXmlParser.getIntAttribute(element, "LevelMin", 1);
        int max = L1NpcXmlParser.getIntAttribute(element, "LevelMax", 99);

        return level == 0 ? new IntRange(min, max) : new IntRange(level, level);
    }

    private int[] parseNpcIds(String npcIds) {
        StringTokenizer tok = new StringTokenizer(npcIds.replace(" ", ""), ",");
        int[] result = new int[tok.countTokens()];

        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(tok.nextToken());
        }

        Arrays.sort(result);

        return result;
    }

    private boolean acceptsNpcId(L1Object obj) {
        if (npcIds.length > 0) {
            if (!(obj instanceof L1NpcInstance)) {
                return false;
            }

            int npcId = ((L1NpcInstance) obj).getTemplate().getNpcId();

            return Arrays.binarySearch(npcIds, npcId) >= 0;
        }

        return true;
    }

    private boolean acceptsLevel(int level) {
        return this.level.includes(level);
    }

    private boolean acceptsCharType(int type) {
        if (0 < classes.length) {
            return Arrays.binarySearch(classes, type) >= 0;
        }
        return true;
    }

    private boolean acceptsActionName(String name) {
        if (this.name == null) {
            return true;
        }

        return name.equals(this.name);
    }

    private boolean acceptsQuest(L1PcInstance pc) {
        if (questId == -1) {
            return true;
        }
        if (questStep == -1) {
            return 0 < pc.getQuest().getStep(questId);
        }
        return pc.getQuest().getStep(questId) == questStep;
    }

    public boolean acceptsRequest(String actionName, L1PcInstance pc, L1Object obj) {
        if (!acceptsNpcId(obj)) {
            return false;
        }

        if (!acceptsLevel(pc.getLevel())) {
            return false;
        }

        if (!acceptsQuest(pc)) {
            return false;
        }

        if (!acceptsCharType(pc.getType())) {
            return false;
        }

        return acceptsActionName(actionName);
    }

    public abstract L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args);

    public L1NpcHtml executeWithAmount(String actionName, L1PcInstance pc, L1Object obj, int amount) {
        return null;
    }
}
