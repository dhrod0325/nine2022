package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1EffectSpawn;
import ks.model.attack.magic.L1MagicRun;
import ks.model.attack.magic.impl.action.vo.L1MagicActionVo;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_Poison;
import ks.util.log.L1LogUtils;

public class L1SkillFreezeIce extends L1SkillAdapter {
    private boolean success;

    public L1SkillFreezeIce(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();

        logger.debug("runSkill hasIce");

        L1Character targetCharacter = request.getTargetCharacter();
        L1MagicRun magic = request.getMagic();
        int duration = request.getDuration();

        boolean hasIce = targetCharacter.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS);

        if (!hasIce) {
            this.success = magic.calcProbabilityMagic(skillId);
            int dmg = magic.calcMagicDamage(skillId);
            magic.commit(new L1MagicActionVo(dmg, 0));

            L1LogUtils.gmLog(skillUseCharacter, "{}, 확률 : {}", getSkill().getName(), magic.getMagicParam().getProbability());

            if (success) {
                if (!targetCharacter.isDead()) {
                    L1EffectSpawn.getInstance().spawnEffect(81168, duration * 1000L, targetCharacter.getX(), targetCharacter.getY(), targetCharacter.getMapId());

                    if (targetCharacter instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) targetCharacter;

                        pc.sendPackets(new S_Poison(pc.getId(), 2));
                        Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 2));

                        pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                    } else if (targetCharacter instanceof L1MonsterInstance
                            || targetCharacter instanceof L1SummonInstance
                            || targetCharacter instanceof L1PetInstance) {
                        L1NpcInstance npc = (L1NpcInstance) targetCharacter;

                        if (npc.getMaxHp() < 4300) {
                            Broadcaster.broadcastPacket(npc, new S_Poison(npc.getId(), 2));
                            npc.setParalyzed(true);
                            npc.setParalysisTime(duration);
                        }
                    }

                    targetCharacter.getSkillEffectTimerSet().setSkillEffect(skillId, getSkill().getBuffDuration() * 1000);
                }
            }
        }

        setRunSkillState(STATUS_CONTINUE);
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        L1MagicUtils.unFreeze(targetCharacter);
        logger.debug("freeze stop");
    }
}
