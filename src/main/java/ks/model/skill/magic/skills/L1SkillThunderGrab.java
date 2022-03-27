package ks.model.skill.magic.skills;

import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillThunderGrab extends L1SkillAdapter {
    public L1SkillThunderGrab(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        int time = request.getDuration() * 1000;

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_FREEZE, time);

            pc.sendPackets(new S_SkillSound(pc.getId(), 4184));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 4184));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
        } else if (cha instanceof L1MonsterInstance
                || cha instanceof L1SummonInstance
                || cha instanceof L1PetInstance) {

            L1NpcInstance npc = (L1NpcInstance) cha;

            npc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_FREEZE, time);

            Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(), 4184));
            npc.setParalyzed(true);
        }
    }
}
