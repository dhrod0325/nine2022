package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1NpcInstance;

public class S_NpcChatPacket extends ServerBasePacket {
    public S_NpcChatPacket(L1NpcInstance npc, String chat, int type) {
        buildPacket(npc, chat, type);
    }

    private void buildPacket(L1NpcInstance npc, String chat, int type) {
        int sTemp = L1Opcodes.S_OPCODE_NPCSHOUT;
        switch (type) {
            case 0:
                writeC(sTemp);
                writeC(type);
                writeD(npc.getId());
                writeS(npc.getName() + ": " + chat);
                break;
            case 2:
                writeC(sTemp);
                writeC(type);
                writeD(npc.getId());
                if (npc.getTemplate().getNpcId() == 70518 || npc.getTemplate().getNpcId() == 70506) {
                    writeS(npc.getName() + ": " + chat);
                } else {
                    writeS("<" + npc.getName() + "> " + chat);
                }
                break;
            case 3: // world chat
                writeC(L1Opcodes.S_OPCODE_NPCSHOUT);
                writeC(type);
                writeD(npc.getId());
                writeS("[" + npc.getName() + "] " + chat);
                break;

            default:
                break;
        }
    }
}
