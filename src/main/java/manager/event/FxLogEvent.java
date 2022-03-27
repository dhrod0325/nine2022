package manager.event;

import org.springframework.context.ApplicationEvent;

import java.util.Queue;

public class FxLogEvent extends ApplicationEvent {
    public FxLogEvent(Queue<FxLog> source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Queue<FxLog> getSource() {
        return (Queue<FxLog>) super.getSource();
    }

    public static class FxLog {
        public String name;
        public long timeMillis;
        public String message;

        public FxLog(String name, long timeMillis, String message) {
            this.name = name;
            this.timeMillis = timeMillis;
            this.message = message;
        }

        @Override
        public String toString() {
            return "FxLogEvent{" +
                    "name='" + name + '\'' +
                    ", timeMillis=" + timeMillis +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
