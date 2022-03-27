package ks.app.event;

import ks.model.pc.L1PcInstance;
import org.springframework.context.ApplicationEvent;

public class L1QuitGameEvent extends ApplicationEvent {
    public L1QuitGameEvent(L1PcInstance source) {
        super(source);
    }

    @Override
    public L1PcInstance getSource() {
        return (L1PcInstance) super.getSource();
    }
}
