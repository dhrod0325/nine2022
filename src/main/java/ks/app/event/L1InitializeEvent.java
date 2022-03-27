package ks.app.event;

import org.springframework.context.ApplicationEvent;

public class L1InitializeEvent {
    public static class Start extends ApplicationEvent {
        public Start(Object source) {
            super(source);
        }
    }

    public static class End extends ApplicationEvent {
        public End(Object source) {
            super(source);
        }
    }

    public static class OnLoadConfig extends ApplicationEvent {
        public OnLoadConfig(Object o) {
            super(o);
        }
    }

    public static class OnLoadMap extends ApplicationEvent {
        public OnLoadMap(Object o) {
            super(o);
        }
    }

    public static class OnLoadWorld extends ApplicationEvent {
        public OnLoadWorld(Object o) {
            super(o);
        }
    }
}
