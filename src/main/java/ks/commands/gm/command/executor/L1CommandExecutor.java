package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;

public interface L1CommandExecutor {
    void execute(L1PcInstance pc, String cmdName, String arg);
}
