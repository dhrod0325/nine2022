package ks.util;

import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_SkillBrave;
import ks.packets.serverpackets.S_SkillHaste;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.ABSOLUTE_BARRIER;
import static ks.constants.L1SkillId.EARTH_BIND;

public class L1StatusUtils {
    public static void haste(L1PcInstance pc, int timeMillis) {
        pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_HASTE, timeMillis);

        int objId = pc.getId();

        pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
        Broadcaster.broadcastPacket(pc, new S_SkillHaste(objId, 1, 0));

        pc.sendPackets(new S_SkillSound(objId, 191));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(objId, 191));

        pc.getMoveState().setMoveSpeed(1);
    }

    public static void shockStun(L1Character target, int timeMillis) {
        target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_SHOCK_STUN, timeMillis);
        target.sendPackets(new S_SkillSound(target.getId(), 4434));
        Broadcaster.broadcastPacket(target, new S_SkillSound(target.getId(), 4434));
        target.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
        L1MagicUtils.startAbsoluteBarrier(target, timeMillis);
    }

    public static void brave(L1PcInstance pc, int timeMillis) {
        pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_BRAVE, timeMillis);

        int objId = pc.getId();

        pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
        Broadcaster.broadcastPacket(pc, new S_SkillBrave(objId, 1, 0));

        pc.sendPackets(new S_SkillSound(objId, 751));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(objId, 751));

        pc.getMoveState().setBraveSpeed(1);
    }

    public static boolean isStatusLock(L1Character character) {
        return character.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
                || character.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
                || character.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)
                || character.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.STUN_SKILLS);
    }
}
