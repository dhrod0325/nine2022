package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;

@SuppressWarnings("unused")
public class L1DeleteGroundItem implements L1CommandExecutor {
    private L1DeleteGroundItem() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1DeleteGroundItem();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        L1CommonUtils.deleteGroundItems();
    }
}
