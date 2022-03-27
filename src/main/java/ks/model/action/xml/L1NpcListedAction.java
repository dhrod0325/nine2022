package ks.model.action.xml;

import ks.model.L1Object;
import ks.model.pc.L1PcInstance;
import org.w3c.dom.Element;

import java.util.List;

public class L1NpcListedAction extends L1NpcXmlAction {
    private final List<L1NpcAction> actions;

    public L1NpcListedAction(Element element) {
        super(element);

        actions = L1NpcXmlParser.listActions(element);
    }

    @Override
    public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args) {
        L1NpcHtml result = null;

        for (L1NpcAction action : actions) {
            if (!action.acceptsRequest(actionName, pc, obj)) {
                continue;
            }

            L1NpcHtml html = action.execute(actionName, pc, obj, args);

            if (html != null) {
                result = html;
            }
        }

        return result;
    }

    @Override
    public L1NpcHtml executeWithAmount(String actionName, L1PcInstance pc, L1Object obj, int amount) {
        L1NpcHtml result = null;

        for (L1NpcAction action : actions) {
            if (!action.acceptsRequest(actionName, pc, obj)) {
                continue;
            }

            L1NpcHtml html = action.executeWithAmount(actionName, pc, obj, amount);

            if (html != null) {
                result = html;
            }
        }
        return result;
    }
}
