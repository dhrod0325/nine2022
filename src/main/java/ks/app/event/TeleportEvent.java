package ks.app.event;

import ks.model.pc.L1PcInstance;
import org.springframework.context.ApplicationEvent;

public class TeleportEvent extends ApplicationEvent {
    public TeleportEvent(L1PcInstance source) {
        super(source);
    }

    @Override
    public L1PcInstance getSource() {
        return (L1PcInstance) super.getSource();
    }
}
