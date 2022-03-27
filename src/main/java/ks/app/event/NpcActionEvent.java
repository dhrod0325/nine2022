package ks.app.event;

import ks.model.L1Object;
import ks.model.pc.L1PcInstance;
import org.springframework.context.ApplicationEvent;

public class NpcActionEvent extends ApplicationEvent {
    public NpcActionEvent(NpcActionEventSource source) {
        super(source);
    }

    @Override
    public NpcActionEventSource getSource() {
        return (NpcActionEventSource) super.getSource();
    }

    public static class NpcActionEventSource {
        private String action;
        private L1PcInstance pc;
        private L1Object obj;
        private String param;

        public NpcActionEventSource(String action, L1PcInstance pc, L1Object obj, String param) {
            this.action = action;
            this.pc = pc;
            this.obj = obj;
            this.param = param;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public L1PcInstance getPc() {
            return pc;
        }

        public void setPc(L1PcInstance pc) {
            this.pc = pc;
        }

        public L1Object getObj() {
            return obj;
        }

        public void setObj(L1Object obj) {
            this.obj = obj;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }
    }
}
