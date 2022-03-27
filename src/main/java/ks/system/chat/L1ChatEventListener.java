package ks.system.chat;

import ks.model.pc.L1PcInstance;

import java.util.HashMap;
import java.util.Map;

public class L1ChatEventListener {
    private static final L1ChatEventListener instance = new L1ChatEventListener();

    private final Map<L1PcInstance, L1ChatEventHandler> handlers = new HashMap<>();

    public static L1ChatEventListener getInstance() {
        return instance;
    }

    public void add(L1PcInstance pc, L1ChatEventHandler handler) {
        handlers.put(pc, handler);
    }

    public void remove(L1PcInstance pc) {
        handlers.remove(pc);
    }

    public L1ChatEventHandler get(L1PcInstance pc) {
        return handlers.get(pc);
    }
}