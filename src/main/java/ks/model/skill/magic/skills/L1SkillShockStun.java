package ks.model.skill.magic.skills;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1EffectSpawn;
import ks.model.attack.physics.L1AttackRun;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ks.constants.L1SkillId.*;

public class L1SkillShockStun extends L1SkillAdapter {
    public L1SkillShockStun(int skillId) {
        super(skillId);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        L1MagicUtils.stopStun(targetCharacter);
    }

    private void sendGrfx(L1Character skillUseCharacter, L1Character targetCharacter) {
        int gfxId;

        if (skillId == EMPIRE) {
            gfxId = 17569;
        } else {
            gfxId = 4434;
        }

        logger.debug("sendGrfx - target : {}, user : ", targetCharacter.getName());

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_SkillSound(pc.getId(), gfxId));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxId));
        } else if (targetCharacter instanceof L1NpcInstance) {
            Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), gfxId));
        }
    }

    @Override
    public void completeSkill(L1SkillRequest request) {
        super.completeSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        if (skillUseCharacter instanceof L1NpcInstance) {
            Broadcaster.broadcastPacket(skillUseCharacter, new S_DoActionGFX(skillUseCharacter.getId(), getSkill().getActionId()));
            Broadcaster.broadcastPacketExceptTargetSight(targetCharacter, new S_DoActionGFX(targetCharacter.getId(), L1ActionCodes.ACTION_Damage), skillUseCharacter);
        }
    }

    @Override
    public int interceptorDuration(L1SkillRequest request, int duration) {
        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        int stunEffectNpcId;

        if (skillId == SHOCK_STUN) {
            stunEffectNpcId = 460000070;
        } else if (skillId == EMPIRE) {
            stunEffectNpcId = 460000202;
        } else if (skillId == BONE_BREAK) {
            stunEffectNpcId = 460000314;
        } else {
            stunEffectNpcId = 81162;
        }

        int diffLevel = skillUseCharacter.getLevel() - targetCharacter.getLevel();

        List<Integer> stunTimeList;

        if (diffLevel >= 8) {
            stunTimeList = new ArrayList<>(Arrays.asList(2700, 3200, 3700, 4200, 4700, 5200, 5700));
        } else if (diffLevel >= 6) {
            stunTimeList = new ArrayList<>(Arrays.asList(2400, 2900, 3400, 3900, 4400, 4900, 5400));
        } else if (diffLevel >= 4) {
            stunTimeList = new ArrayList<>(Arrays.asList(2100, 2600, 3100, 3600, 4100, 4600, 5100));
        } else if (diffLevel >= 2) {
            stunTimeList = new ArrayList<>(Arrays.asList(1800, 2300, 2800, 3300, 3800, 4300, 4800, 5000));
        }

        //여기까지 시전자가 렙이 높은경우
        else if (diffLevel >= 0) {
            stunTimeList = new ArrayList<>(Arrays.asList(1500, 2000, 2200, 2500, 3000, 3500, 4000, 4500));
        } else if (diffLevel >= -2) {
            stunTimeList = new ArrayList<>(Arrays.asList(1200, 1200, 1500, 1500, 1700, 2000, 2200, 2200, 2700, 3200, 3700, 4200));
        } else if (diffLevel >= -4) {
            stunTimeList = new ArrayList<>(Arrays.asList(900, 1000, 1200, 1200, 1400, 1900, 2000, 2200, 2400, 2900, 3400, 3900));
        } else {
            stunTimeList = new ArrayList<>(Arrays.asList(600, 1000, 1000, 1100, 1600, 1700, 1700, 2100, 2500, 3100, 3400));
        }

        int shockStunDuration = stunTimeList.get(RandomUtils.nextInt(stunTimeList.size()));

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
        } else if (targetCharacter instanceof L1MonsterInstance || targetCharacter instanceof L1SummonInstance || targetCharacter instanceof L1PetInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            npc.setParalyzed(true);
            npc.setParalysisTime(shockStunDuration);
        }

        if (skillId == EMPIRE) {
            shockStunDuration *= 0.6;
        }

        if (skillId == BONE_BREAK) {
            shockStunDuration = 2000;
        }

        L1EffectSpawn.getInstance().spawnEffect(stunEffectNpcId, shockStunDuration, targetCharacter.getX(), targetCharacter.getY(), targetCharacter.getMapId());

        return shockStunDuration;
    }

    @Override
    public boolean interceptProbability(L1SkillRequest request, boolean success) {
        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        if (L1AttackUtils.isNotAttackAbleByTargetStatus(targetCharacter)) {
            return false;
        } else if (skillUseCharacter instanceof L1PcInstance) {
            L1AttackRun motion = new L1AttackRun(skillUseCharacter, targetCharacter);
            motion.getAttackParam().setHitUp(success);
            motion.action();

            if (!success) {
                sendGrfx(skillUseCharacter, targetCharacter);
            }
        }

        return success;
    }
}
