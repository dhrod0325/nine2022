package ks.util;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1World;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.ServerBasePacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ks.constants.L1SkillId.ABSOLUTE_BARRIER;
import static ks.constants.L1SkillId.EARTH_BIND;

public class L1MotionUtils {
    private static final List<Integer> runPolyList = new ArrayList<>(Arrays.asList(18255, 20150));

    public static void motionForRangeSkillPacket(ServerBasePacket packet, L1Character targetCharacter) {
        if (runPolyList.contains(targetCharacter.getGfxId().getTempCharGfx())) {
            packet.writeH(0);
            damagedAction(targetCharacter);
        } else {
            packet.writeH(0x20);
        }
    }

    public static void motionForAttackSkillPacket(ServerBasePacket packet, int targetId, int spellGfx, int actionId) {
        L1PcInstance target = L1World.getInstance().getPlayer(targetId);

        if (spellGfx == 10 && actionId == 17) { //흑단막대
            if (target != null) {
                if (target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER, EARTH_BIND)
                        || target.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)) {
                    packet.writeD(0);
                    L1AttackUtils.missAttack(target);
                } else {
                    packet.writeD(targetId);
                }
            } else {
                packet.writeD(targetId);
            }
        } else {
            if (target != null) {
                if (target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER, EARTH_BIND)
                        || target.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)) {
                    packet.writeD(0);
                    L1AttackUtils.missAttack(target);
                } else {
                    if (runPolyList.contains(target.getGfxId().getTempCharGfx())) {
                        packet.writeD(0);
                        damagedAction(target);
                    } else {
                        packet.writeD(targetId);
                    }
                }
            } else {
                packet.writeD(targetId);
            }
        }
    }

    public static void damagedAction(L1Character targetCharacter) {
        if (runPolyList.contains(targetCharacter.getGfxId().getTempCharGfx())) {
            if (targetCharacter instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) targetCharacter;

                if (!pc.getTimer().isTimeOver("damagedAction")) {
                    return;
                }

                pc.getTimer().setWaitTime("damagedAction", 300);
            }

            targetCharacter.sendPackets(new S_DoActionGFX(targetCharacter.getId(), L1ActionCodes.ACTION_Damage));
            Broadcaster.broadcastPacket(targetCharacter, new S_DoActionGFX(targetCharacter.getId(), L1ActionCodes.ACTION_Damage));
        }
    }
}
