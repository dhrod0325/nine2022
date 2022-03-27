package ks.model.action.xml;

import ks.model.L1Object;
import ks.model.pc.L1PcInstance;
import ks.util.common.IterableElementList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class L1NpcShowHtmlAction extends L1NpcXmlAction {
    private final String htmlId;

    private final String[] args;

    public L1NpcShowHtmlAction(Element element) {
        super(element);

        htmlId = element.getAttribute("HtmlId");
        NodeList list = element.getChildNodes();
        List<String> dataList = new ArrayList<>();

        for (Element elem : new IterableElementList(list)) {
            if (elem.getNodeName().equalsIgnoreCase("Data")) {
                dataList.add(elem.getAttribute("Value"));
            }
        }

        args = dataList.toArray(new String[dataList.size()]);
    }

    @Override
    public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args) {
        return new L1NpcHtml(htmlId, this.args);
    }

}
