package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class S_PetCtrlMenu extends ServerBasePacket {
    public S_PetCtrlMenu(L1PcInstance pc, L1NpcInstance npc, boolean open) {
        buildPacket(pc, npc, open);
    }

    private void buildPacket(L1PcInstance pc, L1NpcInstance npc, boolean open) {

        writeC(L1Opcodes.S_OPCODE_RETURNEDSTAT);
        writeC(12);
        if (open) {
            writeH(pc.getPetList().size() * 3);
            writeD(0);
            writeD(npc.getId());
            writeD(npc.getMapId());
            writeH(npc.getX());
            writeH(npc.getY());
            writeS(npc.getName());
            writeH(pc.getPetList().size() * 3);
            writeD(0x00000000);
            writeD(npc.getId());
            writeH(npc.getMapId());
            writeH(0x0000);
            writeH(npc.getX());
            writeH(npc.getY());
            writeS(npc.getName());
        } else {
            writeH(pc.getPetList().size() * 3 - 3);
            writeD(1);
            writeD(npc.getId());
            writeD(npc.getMapId());
            writeH(npc.getX());
            writeH(npc.getY());
            writeS(npc.getName());
            writeH(pc.getPetList().size() * 3 - 3);
            writeD(0x00000001);
            writeD(npc.getId());
        }
    }
}