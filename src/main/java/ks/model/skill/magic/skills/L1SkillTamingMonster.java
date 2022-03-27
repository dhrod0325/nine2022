package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;

import static ks.model.attack.utils.L1MagicUtils.getTamingCharisma;

public class L1SkillTamingMonster extends L1SkillAdapter {

    public L1SkillTamingMonster(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);
        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (targetCharacter instanceof L1MonsterInstance) {
            if (skillUseCharacter instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) skillUseCharacter;
                L1MonsterInstance mon = (L1MonsterInstance) targetCharacter;

                if (mon.getTemplate().isTamable()) {
                    int charisma = getTamingCharisma((L1PcInstance) skillUseCharacter);

                    if (charisma >= 6) {
                        new L1SummonInstance(mon, pc, false);
                    } else {
                        pc.sendPackets(new S_ServerMessage(319));
                    }
                }
            }
        }

    }
}
