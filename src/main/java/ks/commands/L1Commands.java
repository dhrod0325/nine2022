package ks.commands;

import ks.model.pc.L1PcInstance;

import java.util.StringTokenizer;

public abstract class L1Commands {
    public abstract String name();

    public abstract boolean execute(L1PcInstance pc, StringTokenizer nt);
}