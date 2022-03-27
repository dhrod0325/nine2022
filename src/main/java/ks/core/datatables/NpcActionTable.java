package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1Object;
import ks.model.action.xml.L1NpcAction;
import ks.model.action.xml.L1NpcXmlParser;
import ks.model.pc.L1PcInstance;
import ks.util.common.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NpcActionTable {
    private static final Logger logger = LogManager.getLogger(NpcActionTable.class);

    private final List<L1NpcAction> actions = new ArrayList<>();
    private final List<L1NpcAction> talkActions = new ArrayList<>();

    public static NpcActionTable getInstance() {
        return LineageAppContext.getBean(NpcActionTable.class);
    }

    @LogTime
    public void load() {
        try {
            actions.clear();
            talkActions.clear();

            File usersDir = new File("./data/xml/NpcActions/users/");

            if (usersDir.exists()) {
                loadDirectoryActions(usersDir);
            }

            loadDirectoryActions(new File("./data/xml/NpcActions/"));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private List<L1NpcAction> loadAction(File file, String nodeName) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(file);

        if (!doc.getDocumentElement().getNodeName().equalsIgnoreCase(nodeName)) {
            return new ArrayList<>();
        }

        return L1NpcXmlParser.listActions(doc.getDocumentElement());
    }

    private void loadAction(File file) throws Exception {
        actions.addAll(loadAction(file, "NpcActionList"));
    }

    private void loadTalkAction(File file) throws Exception {
        talkActions.addAll(loadAction(file, "NpcTalkActionList"));
    }

    private void loadDirectoryActions(File dir) throws Exception {
        for (String fileName : dir.list()) {
            File file = new File(dir, fileName);
            if (FileUtils.getExtension(file).equalsIgnoreCase("xml")) {
                loadAction(file);
                loadTalkAction(file);
            }
        }
    }

    public L1NpcAction get(String actionName, L1PcInstance pc, L1Object obj) {
        for (L1NpcAction action : actions) {
            if (action.acceptsRequest(actionName, pc, obj)) {
                return action;
            }
        }

        return null;
    }

    public L1NpcAction get(L1PcInstance pc, L1Object obj) {
        for (L1NpcAction action : talkActions) {
            if (action.acceptsRequest("", pc, obj)) {
                return action;
            }
        }

        return null;
    }
}
