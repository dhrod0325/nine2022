package manager.controller.popup.user;

import javafx.scene.control.Label;
import ks.model.pc.L1PcInstance;

public class UserLabel extends Label {
    private final L1PcInstance pc;

    public UserLabel(L1PcInstance pc) {
        super(pc.getName());
        this.pc = pc;
    }

    public L1PcInstance getPc() {
        return pc;
    }
}
