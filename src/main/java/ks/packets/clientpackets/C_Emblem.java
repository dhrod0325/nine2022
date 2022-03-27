package ks.packets.clientpackets;

import ks.core.ObjectIdFactory;
import ks.core.datatables.clan.ClanTable;
import ks.core.network.L1Client;
import ks.model.Broadcaster;
import ks.model.L1Clan;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ReturnedStat;

import java.io.File;
import java.io.FileOutputStream;

public class C_Emblem extends ClientBasePacket {
    public C_Emblem(byte[] data, L1Client client) throws Exception {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (pc.getClanRank() != 4 && pc.getClanRank() != 10) {
            return;
        }

        if (pc.getClanId() != 0) {
            String emblemDir = "data/emblem/";

            File dirCheck = new File(emblemDir);

            if (!dirCheck.isDirectory()) {
                dirCheck.mkdirs();
            }

            L1Clan clan = pc.getClan();

            int newEmblemdId = ObjectIdFactory.getInstance().nextId();
            String emblemFile = String.valueOf(newEmblemdId);

            try (FileOutputStream fos = new FileOutputStream(emblemDir + emblemFile)) {
                for (short cnt = 0; cnt < 384; cnt++) {
                    fos.write(readC());
                }
            } catch (Exception e) {
                logger.error(e);
                throw e;
            }

            clan.setEmblemId(newEmblemdId);
            ClanTable.getInstance().updateClan(clan);

            for (L1PcInstance member : clan.getOnlineClanMember()) {
                member.sendPackets(new S_ReturnedStat(member.getId(), newEmblemdId));
                Broadcaster.broadcastPacket(member, new S_ReturnedStat(member.getId(), newEmblemdId));
            }
        }
    }
}
