package ks.packets.serverpackets;

import ks.core.datatables.NPCTalkDataTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1NpcTalkData;
import ks.model.instance.L1FieldObjectInstance;
import ks.model.instance.L1NpcInstance;


public class S_NPCPack extends ServerBasePacket {
    private static final int STATUS_POISON = 1;

    private static final int STATUS_PC = 4;

    private static final int STATUS_FREEZE = 8;

    private static final int STATUS_BRAVE = 16;

    public S_NPCPack(L1NpcInstance npc) {
        int status = 0;

        if (npc.getPoison() != null) {
            if (npc.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }

        if (npc.getMoveState().getBraveSpeed() == 1) {
            status |= STATUS_BRAVE;
        }

        if (npc.getTemplate().isDoppel()) {
            if (npc.getTemplate().getNpcId() != 81069) {
                status |= STATUS_PC;
            }
        }

        if (npc.isParalyzed()) {
            status |= STATUS_FREEZE;
        }

        writeC(L1Opcodes.S_OPCODE_SHOWOBJ);
        writeH(npc.getX());
        writeH(npc.getY());
        writeD(npc.getId());

        int gfxId;

        if (npc.getGfxId().getTempCharGfx() == 0) {
            gfxId = npc.getGfxId().getGfxId();
        } else {
            gfxId = npc.getGfxId().getTempCharGfx();
        }

        writeH(gfxId);

        if ((npc.getTemplate().isDoppel() && npc.getGfxId().getGfxId() != 31)
                || npc.getGfxId().getGfxId() == 6632 || npc.getGfxId().getGfxId() == 6634
                || npc.getGfxId().getGfxId() == 6636 || npc.getGfxId().getGfxId() == 6638) {
            writeC(4); // 장검
        } else if (npc.getGfxId().getGfxId() == 51 || npc.getNpcId() == 60519) { // 창 경비병 , 청상어단
            writeC(24);
        } else if (npc.getGfxId().getGfxId() == 816) { // 오성 오크스카우트
            writeC(20);
        } else {
            writeC(npc.getActionStatus());
        }

        writeC(npc.getMoveState().getHeading());
        writeC(npc.getLight().getChaLightSize());
        writeC(npc.getMoveState().getMoveSpeed());
        writeD(1);// npc.getExp 지만 이제 보내지 않는다.
        writeH(npc.getTempLawful());
        writeS(npc.getNameId());

//        if (npc.getNameId().trim().isEmpty()) {
//            writeS("");
//        } else {
//            writeS(npc.getName());
//        }

        if (npc instanceof L1FieldObjectInstance) {
            L1NpcTalkData talkData = NPCTalkDataTable.getInstance().getTemplate(npc.getTemplate().getNpcId());
            if (talkData != null) {
                writeS(talkData.getNormalAction());
            } else {
                writeS(null);
            }
        } else {
            writeS(npc.getTitle());
        }

        writeC(status);
        writeD(0); // 0이외에 하면(자) C_27이 난다
        writeS(null);
        writeS(null); // 마스터명?
        writeC(0);
        writeC(0xFF); // HP
        writeC(0);
        writeC(npc.getLevel());
        writeC(0);
        writeC(0xFF);
        writeC(0xFF);
    }
}
