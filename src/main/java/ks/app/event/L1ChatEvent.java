package ks.app.event;

import org.springframework.context.ApplicationEvent;

public class L1ChatEvent extends ApplicationEvent {
    public L1ChatEvent(L1ChatEventSource source) {
        super(source);
    }

    @Override
    public L1ChatEventSource getSource() {
        return (L1ChatEventSource) super.getSource();
    }

    public static class L1ChatEventSource {
        public final String name;
        public final String type;
        public final String message;
        public final String targetName;
        public final long timeMillis;

        public L1ChatEventSource(String name, String type, String message) {
            this(name, type, message, null);
        }

        public L1ChatEventSource(String name, String type, String message, String targetName) {
            this.name = name;
            this.type = type;
            this.message = message;
            this.targetName = targetName;
            this.timeMillis = System.currentTimeMillis();
        }
    }
}
