package ks.model.skill.magic.skills;

import ks.app.LineageAppContext;
import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1PolyMorph;
import ks.model.attack.magic.impl.prob.L1MagicProbNpcToPc;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.cooking.L1CookingUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.util.common.random.RandomUtils;

import java.time.Instant;

import static ks.constants.L1SkillId.*;

public class L1SkillCancellation extends L1SkillAdapter {
    public L1SkillCancellation(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        if (skillUseCharacter instanceof L1PcInstance) {
            cancellation(skillUseCharacter, targetCharacter);
        } else if (skillUseCharacter instanceof L1NpcInstance) {
            if (targetCharacter instanceof L1PcInstance) {
                L1MagicProbNpcToPc check = new L1MagicProbNpcToPc((L1NpcInstance) skillUseCharacter, (L1PcInstance) targetCharacter);
                boolean isSuccess = RandomUtils.isWinning(100, check.calcProbability(skillId));

                if (isSuccess) {
                    cancellation(skillUseCharacter, targetCharacter);
                }
            }
        }
    }

    public void cancellation(L1Character skillUseCharacter, L1Character targetCharacter) {
        if (targetCharacter instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            int npcId = npc.getTemplate().getNpcId();

            if (npcId == 71092) {
                if (npc.getGfxId().getGfxId() == npc.getGfxId().getTempCharGfx()) {
                    npc.getGfxId().setTempCharGfx(1314);
                    Broadcaster.broadcastPacket(npc, new S_ChangeShape(npc.getId(), 1314));
                }

                setRunSkillState(STATUS_RETURN);
                return;
            } else if (npcId == 45640) {// 유니콘
                if (npc.getGfxId().getGfxId() == npc.getGfxId().getTempCharGfx()) {
                    npc.setCurrentHp(npc.getMaxHp());
                    npc.setName("$2103");
                    npc.setNameId("$2103");

                    Broadcaster.broadcastPacket(npc, new S_ChangeName(npc.getId(), "$2103"));
                    npc.getGfxId().setTempCharGfx(2332);
                    Broadcaster.broadcastPacket(npc, new S_ChangeShape(npc.getId(), 2332));
                    setRunSkillState(STATUS_RETURN);
                    return;
                } else if (npc.getGfxId().getTempCharGfx() == 2332) {
                    npc.setCurrentHp(npc.getMaxHp());
                    npc.setName("$2488");
                    npc.setNameId("$2488");
                    Broadcaster.broadcastPacket(npc, new S_ChangeName(npc.getId(), "$2488"));
                    npc.getGfxId().setTempCharGfx(2755);
                    Broadcaster.broadcastPacket(npc, new S_ChangeShape(npc.getId(), 2755));
                    setRunSkillState(STATUS_RETURN);
                    return;
                }
            } else if (npcId == 81209) {
                if (npc.getGfxId().getGfxId() == npc.getGfxId().getTempCharGfx()) {
                    npc.getGfxId().setTempCharGfx(4310);
                    Broadcaster.broadcastPacket(npc, new S_ChangeShape(npc.getId(), 4310));
                }

                setRunSkillState(STATUS_RETURN);
                return;
            }
        }

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            if (pc.isInvisible()) {
                pc.delInvis();
            }
        }

        if (targetCharacter instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            npc.getMoveState().setMoveSpeed(0);
            npc.getMoveState().setBraveSpeed(0);
            Broadcaster.broadcastPacket(npc, new S_SkillHaste(targetCharacter.getId(), 0, 0));
            Broadcaster.broadcastPacket(npc, new S_SkillBrave(targetCharacter.getId(), 0, 0));
            npc.setWeaponBreaking(false);
            npc.setParalyzed(false);
            npc.setParalysisTime(0);
        }

        for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
            if (L1SkillUtils.isNotCancelable(skillNum) && !targetCharacter.isDead()) {
                continue;
            }

            if (targetCharacter instanceof L1PcInstance && skillNum == SHAPE_CHANGE) {
                L1PcInstance pc = (L1PcInstance) targetCharacter;

                if (pc.getEquipSlot().isEquipedContain(
                        421003,
                        421004,
                        421005,
                        421006,
                        421007,
                        421008
                )) {
                    continue;
                }
            }

            targetCharacter.getSkillEffectTimerSet().removeSkillEffect(skillNum);
        }

        targetCharacter.curePoison();
        targetCharacter.cureParalaysis();

        for (int skillNum = CHAR_BUFF_START; skillNum <= CHAR_BUFF_END; skillNum++) {
            if (L1SkillUtils.isNotCancelable(skillNum)) {
                continue;
            }
            targetCharacter.getSkillEffectTimerSet().removeSkillEffect(skillNum);
        }

        for (int skillNum = STATUS_CANCLE_START; skillNum <= STATUS_CANCLE_END; skillNum++) {
            if (L1SkillUtils.isNotCancelable(skillNum)) {
                continue;
            }

            targetCharacter.getSkillEffectTimerSet().removeSkillEffect(skillNum);
        }

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;

            if (!pc.getEquipSlot().isEquipedContain(
                    421003,
                    421004,
                    421005,
                    421006,
                    421007,
                    421008
            )) {
                L1PolyMorph.undoPoly(pc);
            }

            pc.sendPackets(new S_CharVisualUpdate(pc));
            Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));

            if (pc.getHasteItemEquipped() > 0) {
                pc.getMoveState().setMoveSpeed(0);
                pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
            }

            if (pc.isInvisible()) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
                    pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY);
                    pc.sendPackets(new S_Invis(pc.getId(), 0));
                    Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                    pc.sendPackets(new S_Sound(147));
                }
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                    pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
                    pc.sendPackets(new S_Invis(pc.getId(), 0));
                    Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
                }
            }
        }

        targetCharacter.getSkillEffectTimerSet().removeSkillEffect(L1SkillUtils.ICE_SKILLS);

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_CharVisualUpdate(pc));
            Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
        }

        LineageAppContext.commonTaskScheduler().schedule(() -> {
            for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
                if (targetCharacter instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) targetCharacter;

                    if (targetCharacter.getSkillEffectTimerSet().hasSkillEffect(skillNum)) {
                        int time = targetCharacter.getSkillEffectTimerSet().getSkillEffectTimeSec(skillNum);

                        logger.debug("쿠킹 패킷 : {} 시간 : {}", skillNum, time);
                        L1CookingUtils.sendPacket(pc, skillNum, time);
                    }
                }
            }
        }, Instant.now().plusMillis(10));
    }

    @Override
    public boolean interceptProbability(L1SkillRequest request, boolean success) {
        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        if (L1MagicUtils.isTargetMeAndPartyAndClan(skillUseCharacter, targetCharacter)) {
            return true;
        }

        return success;
    }
}
