package ks.packets.clientpackets;

import ks.core.datatables.CastleTable;
import ks.core.network.L1Client;
import ks.model.L1Castle;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_WarTime;

import java.util.Calendar;

public class C_WarTimeList extends ClientBasePacket {
    public C_WarTimeList(byte[] data, L1Client clientthread) throws Exception {
        super(data);
        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            int castle_id = clan.getCastleId();
            if (castle_id != 0) { // 성주 클랜 아이디
                L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
                if (l1castle.getWarBaseTime() == 0) {
                    pc.sendPackets(new S_ServerMessage(305));// \f1지금은 전쟁 시간을
                    return;
                }

                Calendar warTime = l1castle.getWarTime();
                int year = warTime.get(Calendar.YEAR);
                int month = warTime.get(Calendar.MONTH);
                int day = warTime.get(Calendar.DATE);

                Calendar warBase = Calendar.getInstance();
                warBase.set(year, month, day, 12, 0);// 4 23

                Calendar base_cal = Calendar.getInstance();
                base_cal.set(1997, Calendar.JANUARY, 1, 17, 0);// 1997/01/01 17:00(을)를 기점으로 하고
                // 있다
                long base_millis = base_cal.getTimeInMillis();
                long millis = warBase.getTimeInMillis();
                long diff = millis - base_millis;
                diff -= 1200 * 60 * 1000; // 오차수정
                diff = diff / 60000; // 분 이하 잘라버림
                // time는 1을 더하면 3:02(182분 ) 진행된다
                int time = (int) (diff / 182);

                pc.sendPackets(new S_WarTime(time));
                pc.sendPackets(new S_ServerMessage(300));// 다음 공성전을 위한 시간을
                // 지정해 주십시오.
            }
        }
    }

}
