package ks.commands.gm.command.executor;

import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Invis;
import ks.packets.serverpackets.S_OtherCharPacks;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Visible implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Visible.class.getName());

    private L1Visible() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Visible();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            pc.setGmInvis(false);
            pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY);
            pc.sendPackets(new S_Invis(pc.getId(), 0));
            Broadcaster.broadcastPacket(pc, new S_OtherCharPacks(pc));
            pc.sendPackets(new S_SystemMessage("투명상태를 해제했습니다. "));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 커멘드 에러"));
        }
    }
}
