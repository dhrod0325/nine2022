package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1NpcTalkData;
import ks.model.action.xml.L1NpcHtml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S_NPCTalkReturn extends ServerBasePacket {
    private static final Logger logger = LogManager.getLogger(S_NPCTalkReturn.class);

    public S_NPCTalkReturn(L1NpcTalkData npc, int objId, int action, String[] data) {
        if (npc == null) {
            logger.error("action:{}, objId:{}", action, objId);
            return;
        }

        String html;

        if (action == 1) {
            html = npc.getNormalAction();
        } else if (action == 2) {
            html = npc.getCaoticAction();
        } else {
            throw new IllegalArgumentException();
        }

        logger.debug("objId : {} , htmlId : {}", objId, html);

        buildPacket(objId, html, data);
    }

    public S_NPCTalkReturn(L1NpcTalkData npc, int objId, int action) {
        this(npc, objId, action, null);
    }

    public S_NPCTalkReturn(int objid, String htmlid, String[] data) {
        buildPacket(objid, htmlid, data);
    }

    public S_NPCTalkReturn(int objid, String htmlid) {
        buildPacket(objid, htmlid, null);
    }

    public S_NPCTalkReturn(int objid, L1NpcHtml html) {
        buildPacket(objid, html.getName(), html.getArgs());
    }

    public S_NPCTalkReturn(int objid, L1NpcHtml html, String[] data) {
        buildPacket(objid, html.getName(), data);
    }

    private void buildPacket(int objId, String htmlId, String[] data) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objId);
        writeS(htmlId);

        if (data != null && 1 <= data.length) {
            writeH(0x01);
            writeH(data.length);

            for (String datum : data) {
                writeS(datum);
            }
        } else {
            writeH(0x00);
            writeH(0x00);
        }
    }
}
