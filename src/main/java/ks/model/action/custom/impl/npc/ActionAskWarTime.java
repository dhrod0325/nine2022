package ks.model.action.custom.impl.npc;

import ks.core.datatables.CastleTable;
import ks.model.L1Castle;
import ks.model.L1CastleLocation;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

import java.util.Calendar;

public class ActionAskWarTime extends L1AbstractNpcAction {
    public ActionAskWarTime(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String[] data = null;
        String html = null;

        switch (npcId) {
            case 60514:
                data = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
                html = "ktguard7";
                break;
            case 60560:
                data = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
                html = "orcguard7";
                break;
            case 60552:
                data = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
                html = "wdguard7";
                break;
            case 60524:
            case 60525:
            case 60529:
                data = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
                html = "grguard7";
                break;
            case 70857:
                data = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
                html = "heguard7";
                break;
            case 60530:
            case 60531:
                data = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
                html = "dcguard7";
                break;
            case 60533:
            case 60534:
                data = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
                html = "adguard7";
                break;
            case 81156:
                data = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
                html = "dfguard3";
                break;
        }

        if (html != null)
            pc.sendPackets(new S_NPCTalkReturn(objId, html, data));
    }

    public String[] makeWarTimeStrings(int castleId) {
        L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
        if (castle == null) {
            return null;
        }
        Calendar warTime = castle.getWarTime();

        int year = warTime.get(Calendar.YEAR);
        int month = warTime.get(Calendar.MONTH) + 1;
        int day = warTime.get(Calendar.DATE);
        int hour = warTime.get(Calendar.HOUR_OF_DAY);
        int minute = warTime.get(Calendar.MINUTE);
        String[] result;

        if (castleId == L1CastleLocation.OT_CASTLE_ID) {
            result = new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute)};
        } else {
            result = new String[]{"", String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute)};
        }

        return result;
    }
}
