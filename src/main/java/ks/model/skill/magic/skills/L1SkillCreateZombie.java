package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;

public class L1SkillCreateZombie extends L1SkillAdapter {

    public L1SkillCreateZombie(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            if (targetCharacter instanceof L1MonsterInstance) {
                L1MonsterInstance mon = (L1MonsterInstance) targetCharacter;

                int charisma = L1MagicUtils.getTamingCharisma((L1PcInstance) skillUseCharacter);

                if (charisma >= 6) {
                    new L1SummonInstance(mon, (L1PcInstance) skillUseCharacter, true);
                } else {
                    pc.sendPackets(new S_ServerMessage(319));
                }
            }
        }

    }
}
