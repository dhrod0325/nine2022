package ks.packets.clientpackets;

import ks.core.datatables.CastleTable;
import ks.core.network.L1Client;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_WarTime;

import java.util.Calendar;

public class C_ChangeWarTime extends ClientBasePacket {
    public C_ChangeWarTime(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan != null) {
            int castle_id = clan.getCastleId();
            if (castle_id != 0) {
                L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
                Calendar cal = l1castle.getWarTime();
                Calendar base_cal = Calendar.getInstance();
                base_cal.set(1997, Calendar.JANUARY, 1, 17, 0);
                long base_millis = base_cal.getTimeInMillis();
                long millis = cal.getTimeInMillis();
                long diff = millis - base_millis;
                diff -= 1200 * 60 * 1000;
                diff = diff / 60000;
                int time = (int) (diff / 182);

                pc.sendPackets(new S_WarTime(time));
            }
        }
    }
}