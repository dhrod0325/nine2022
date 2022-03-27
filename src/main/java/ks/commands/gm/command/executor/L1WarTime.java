package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.WarTimeScheduler;

import java.util.Calendar;
import java.util.StringTokenizer;

public class L1WarTime implements L1CommandExecutor {
    private L1WarTime() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1WarTime();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String name = tok.nextToken();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);

            WarTimeScheduler.getInstance().setWarStartTime(name, cal);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".공성종료 [켄트,오크,윈다,기란,하이,지저,아덴,디아]"));
        }
    }
}