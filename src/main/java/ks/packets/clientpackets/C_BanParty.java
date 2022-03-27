package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class C_BanParty extends ClientBasePacket {
    public C_BanParty(byte[] decrypt, L1Client client) {
        super(decrypt);

        String s = readS();

        L1PcInstance player = client.getActiveChar();

        if (player == null) {
            return;
        }

        if (player.getParty() == null) {
            return;
        }

        if (!player.getParty().isLeader(player)) {
            player.sendPackets(new S_ServerMessage(427));
            return;
        }

        for (L1PcInstance member : player.getParty().getMembers()) {
            if (member.getName().equalsIgnoreCase(s)) {
                player.getParty().kickMember(member);
                return;
            }
        }
        player.sendPackets(new S_ServerMessage(426, s));
    }
}
