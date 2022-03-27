package ks.model.action.custom.impl.npc;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.exp.ExpTable;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class ActionExpGive extends L1AbstractNpcAction {
    public ActionExpGive(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String htmlid = null;
        String[] htmldata = null;

        if (action.equalsIgnoreCase("0")) {
            if (ExpTable.getInstance().getLevelByExp(pc.getExp()) >= CodeConfig.EXP_GIVE_MAX_LEVEL) {
                htmlid = "expgive3";
                htmldata = new String[]{CodeConfig.EXP_GIVE_MAX_LEVEL + ""};
            } else {
                if (pc.getLevel() < CodeConfig.EXP_GIVE_MAX_LEVEL) {
                    pc.addExp(ExpTable.getInstance().getExpByLevel(pc.getLevel() + 1) - pc.getExp());
                    pc.setCurrentHp(pc.getMaxHp());
                    pc.setCurrentMp(pc.getMaxMp());

                    htmlid = "expgive1";
                }
            }

            if (htmlid != null)
                pc.sendPackets(new S_NPCTalkReturn(objId, htmlid, htmldata));
        }
    }
}
