package manager.controller.popup.user;

import ks.model.pc.L1PcInstance;
import org.springframework.context.ApplicationEvent;

public class UserPopupEvent extends ApplicationEvent {
    public enum EventType {
        CHAR_DESC,
        CHAR_DROP,
        CHAR_INVENTORY_SEARCH
    }

    public UserPopupEvent(UserPopupEventSource source) {
        super(source);
    }

    @Override
    public UserPopupEventSource getSource() {
        return (UserPopupEventSource) super.getSource();
    }

    public static class UserPopupEventSource {
        public final L1PcInstance pc;

        public final EventType eventType;

        public UserPopupEventSource(L1PcInstance pc, EventType eventType) {
            this.pc = pc;
            this.eventType = eventType;
        }
    }
}
