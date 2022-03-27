package ks.packets.clientpackets;

import ks.constants.L1PacketBoxType;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_Pledge;
import ks.packets.serverpackets.S_ServerMessage;

public class C_Pledge extends ClientBasePacket {
    public C_Pledge(byte[] data, L1Client client) {
        super(data);
        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }

        if (pc.getClanId() > 0) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

            if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                if (pc.getLevel() < 45) {
                    pc.sendPackets(new S_Pledge("pledgeM", pc.getId(), clan.getClanName(), clan.getOnlineClanMemberString(), clan.getAllMembersString()));
                } else {
                    pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.PLEDGE_TWO));
                }
            } else {
                pc.sendPackets(new S_Pledge("pledge", pc.getId(), clan.getClanName(), clan.getOnlineClanMemberString()));
            }

        } else {
            pc.sendPackets(new S_ServerMessage(1064));
        }
    }
}
