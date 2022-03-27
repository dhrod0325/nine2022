package ks.app.event;

import ks.core.datatables.account.Account;
import org.springframework.context.ApplicationEvent;

public class L1AccountEvent {
    public static class OnLoad extends ApplicationEvent {
        public OnLoad(Account source) {
            super(source);
        }

        @Override
        public Account getSource() {
            return (Account) super.getSource();
        }
    }
}
