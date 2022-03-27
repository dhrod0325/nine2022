package ks.commands.gm.command.executor;

import ks.model.Broadcaster;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Ress implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1Ress.class.getName());

    private L1Ress() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Ress();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int objid = pc.getId();

            pc.sendPackets(new S_SkillSound(objid, 759));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(objid, 759));

            for (L1PcInstance tg : L1World.getInstance().getVisiblePlayer(pc)) {
                if (tg.getCurrentHp() == 0 && tg.isDead()) {
                    tg.sendPackets(new S_SystemMessage("GM이 부활을 해주었습니다. "));
                    Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 3944));
                    tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
                    tg.setTempID(objid);
                    tg.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까?
                } else {
                    tg.sendPackets(new S_SystemMessage("GM이 HP,MP를 회복해주었습니다."));
                    Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 832));
                    tg.sendPackets(new S_SkillSound(tg.getId(), 832));
                    tg.setCurrentHp(tg.getMaxHp());
                    tg.setCurrentMp(tg.getMaxMp());
                }
            }

            pc.setCurrentHp(pc.getMaxHp());
            pc.setCurrentMp(pc.getMaxMp());
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 커멘드 에러"));
        }
    }
}
