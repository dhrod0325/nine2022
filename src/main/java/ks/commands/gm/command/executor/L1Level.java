package ks.commands.gm.command.executor;

import ks.core.datatables.exp.ExpTable;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.common.IntRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1Level implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Level.class.getName());

    private L1Level() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Level();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            int level = Integer.parseInt(tok.nextToken());
            if (level == pc.getLevel()) {
                return;
            }
            if (!IntRange.includes(level, 1, 99)) {
                pc.sendPackets(new S_SystemMessage("1-99의 범위에서 지정해 주세요"));
                return;
            }
            pc.setExp(ExpTable.getInstance().getExpByLevel(level));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + "lv 라고 입력해 주세요"));
        }
    }
}
