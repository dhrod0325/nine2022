package ks.model.action.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

public class L1NpcActionFactory {
    private static final Logger logger = LogManager.getLogger(L1NpcActionFactory.class.getName());

    public static L1NpcAction newAction(Element element) {
        try {
            switch (element.getNodeName()) {
                case "Action":
                    return new L1NpcListedAction(element);
                case "MakeItem":
                    return new L1NpcMakeItemAction(element);
                case "ShowHtml":
                    return new L1NpcShowHtmlAction(element);
                case "SetQuest":
                    return new L1NpcSetQuestAction(element);
                case "Teleport":
                    return new L1NpcTeleportAction(element);
            }
        } catch (NullPointerException e) {
            logger.warn(element.getNodeName() + " 미정도리의 NPC 액션입니다");
        } catch (Exception e) {
            logger.error("NpcAction의 클래스 로드에 실패", e);
        }
        return null;
    }
}
