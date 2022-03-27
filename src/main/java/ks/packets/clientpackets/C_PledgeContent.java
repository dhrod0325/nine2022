package ks.packets.clientpackets;

import ks.constants.L1PacketBoxType;
import ks.core.datatables.clan.ClanTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_RetrieveExtraList;
import ks.util.L1ClanUtils;

public class C_PledgeContent extends ClientBasePacket {
    public C_PledgeContent(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        int code = readC();

        if (code == 15) {
            if (pc.getClanId() == 0) {
                return;
            }

            String announce = readS();
            L1Clan clan = ClanTable.getInstance().getTemplate(pc.getClanId());
            clan.setAnnouncement(announce);
            L1ClanUtils.updateClan(clan);
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.HTML_PLEDGE_REALEASE_ANNOUNCE, announce));
        } else if (code == 6) {
            pc.sendPackets(new S_RetrieveExtraList(pc.getId(), pc));
        }
    }
}
