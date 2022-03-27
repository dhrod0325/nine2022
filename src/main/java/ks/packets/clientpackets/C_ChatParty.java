package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1ChatParty;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Party;
import ks.packets.serverpackets.S_ServerMessage;

public class C_ChatParty extends ClientBasePacket {
    public C_ChatParty(byte[] data, L1Client clientthread) {
        super(data);

        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }
        int type = readC();
        if (type == 0) {
            String name = readS();

            if (!pc.isInChatParty()) {
                pc.sendPackets(new S_ServerMessage(425));
                return;
            }
            if (!pc.getChatParty().isLeader(pc)) {
                pc.sendPackets(new S_ServerMessage(427));
                return;
            }

            L1PcInstance targetPc = L1World.getInstance().getPlayer(name);

            if (targetPc == null) {
                pc.sendPackets(new S_ServerMessage(109));
                return;
            }

            if (pc.getId() == targetPc.getId()) {
                return;
            }

            for (L1PcInstance member : pc.getChatParty().getMembers()) {
                if (member.getName().equalsIgnoreCase(name)) {
                    pc.getChatParty().kickMember(member);
                    return;
                }
            }
            pc.sendPackets(new S_ServerMessage(426, name));
        } else if (type == 1) {
            if (pc.isInChatParty()) {
                pc.getChatParty().leaveMember(pc);
            }
        } else if (type == 2) {
            L1ChatParty chatParty = pc.getChatParty();
            if (pc.isInChatParty()) {
                pc.sendPackets(new S_Party("party", pc.getId(), chatParty.getLeader().getName(), chatParty.getMembersNameList()));
            } else {
                pc.sendPackets(new S_ServerMessage(425));
            }
        }
    }
}
