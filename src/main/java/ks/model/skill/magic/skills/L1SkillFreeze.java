package ks.model.skill.magic.skills;

import ks.app.config.prop.CodeConfig;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_Poison;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.common.random.RandomUtils;

public class L1SkillFreeze extends L1SkillAdapter {
    public L1SkillFreeze(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Poison(pc.getId(), 2));
            Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 2));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        L1MagicUtils.unFreeze(targetCharacter);
    }

    @Override
    public int interceptorDuration(L1SkillRequest request, int duration) {
        return RandomUtils.nextInt(CodeConfig.earthBindDuration().get(0), CodeConfig.earthBindDuration().get(1)) * 1000;
    }

    @Override
    public boolean interceptProbability(L1SkillRequest request, boolean success) {
        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1MonsterInstance
                || targetCharacter instanceof L1SummonInstance
                || targetCharacter instanceof L1PetInstance) {

            request.getSkillUseCharacter().sendPackets(new S_SystemMessage("몬스터에게는 사용 불가능 합니다"));

            return false;
        }

        return success;
    }

}
