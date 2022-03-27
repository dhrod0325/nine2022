package ks.model.action.xml;

import ks.constants.L1ItemId;
import ks.model.L1Location;
import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import org.w3c.dom.Element;

public class L1NpcTeleportAction extends L1NpcXmlAction {
    private final L1Location loc;

    private final int heading;

    private final int price;

    private final boolean effect;

    public L1NpcTeleportAction(Element element) {
        super(element);

        int x = L1NpcXmlParser.getIntAttribute(element, "X", -1);
        int y = L1NpcXmlParser.getIntAttribute(element, "Y", -1);
        int mapId = L1NpcXmlParser.getIntAttribute(element, "Map", -1);

        loc = new L1Location(x, y, mapId);

        heading = L1NpcXmlParser.getIntAttribute(element, "Heading", 5);
        price = L1NpcXmlParser.getIntAttribute(element, "Price", 0);
        effect = L1NpcXmlParser.getBoolAttribute(element, "Effect", true);
    }

    @Override
    public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args) {
        if ((loc.getMapId() == 68 || loc.getMapId() == 2005) && pc.getLevel() >= 13) {
            return L1NpcHtml.HTML_CLOSE;
        }

        if ((pc.getLevel() < 45 || pc.getLevel() > 51) && (loc.getMapId() == 777 || loc.getMapId() == 778 || loc.getMapId() == 779)) {
            return L1NpcHtml.HTML_CLOSE;
        }

        if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
            pc.sendPackets(new S_ServerMessage(337, "$4"));
            return L1NpcHtml.HTML_CLOSE;
        }

        L1Map map = loc.getMap();

        L1Location loc = L1Location.randomLocation(this.loc.getX(), this.loc.getY(), map, (short) this.loc.getMapId(), 1, 3, false);

        pc.getInventory().consumeItem(L1ItemId.ADENA, price);
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) this.loc.getMapId(), heading, effect);

        return null;
    }
}
