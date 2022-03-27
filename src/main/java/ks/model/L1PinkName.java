package ks.model;

import ks.constants.L1SkillId;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PinkName;
import ks.util.L1CharPosUtils;

public class L1PinkName {
    public static void onAction(L1Character target, L1Character attacker) {
        if (target == null || attacker == null)
            return;

        if (!(attacker instanceof L1PcInstance))
            return;

        L1PcInstance pc = (L1PcInstance) attacker;

        if (target.getId() == pc.getId())
            return;

        if (pc.getFightId() == target.getId())
            return;

        boolean isNowWar = L1CastleLocation.isNowWarByArea(target);

        if (target.getLawful() >= 0 && !L1CharPosUtils.isSafeZone(target) && !L1CharPosUtils.isSafeZone(pc) && !isNowWar) {
            pc.setPinkName(true);
            pc.sendPackets(new S_PinkName(pc.getId(), 30));

            if (!pc.isGmInvis()) {
                Broadcaster.broadcastPacket(pc, new S_PinkName(pc.getId(), 30));
            }

            pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PINK_NAME, 30 * 1000);
        }
    }
}
