package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.util.L1ClanUtils;

public class C_LeaveClan extends ClientBasePacket {
    public C_LeaveClan(byte[] data, L1Client client) throws Exception {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        int clanId = pc.getClanId();

        if (clanId == 0)
            return;

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan == null)
            return;

        if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
            L1ClanUtils.leaveClanBoss(clan, pc);
        } else {
            L1ClanUtils.leaveClanMember(clan, pc);
        }
    }
}

