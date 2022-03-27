package ks.model.action.xml;

import ks.model.L1Object;
import ks.model.pc.L1PcInstance;
import org.w3c.dom.Element;

public class L1NpcSetQuestAction extends L1NpcXmlAction {
    private final int id;
    private final int step;

    public L1NpcSetQuestAction(Element element) {
        super(element);

        id = L1NpcXmlParser.parseQuestId(element.getAttribute("Id"));
        step = L1NpcXmlParser.parseQuestStep(element.getAttribute("Step"));

        if (id == -1 || step == -1) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args) {
        pc.getQuest().setStep(id, step);
        return null;
    }

}
