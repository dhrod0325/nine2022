package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.instance.L1MonsterInstance;
import ks.model.skill.magic.L1SkillRequest;

public class L1SkillTurnUndead extends L1SkillAdapter {
    public L1SkillTurnUndead(int skillId) {
        super(skillId);
    }

    @Override
    public int interceptDamage(L1SkillRequest request, int dmg) {
        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1MonsterInstance) {
            int undeadType = ((L1MonsterInstance) targetCharacter).getTemplate().getUndead();

            if (undeadType == 1 || undeadType == 3) {
                dmg = targetCharacter.getCurrentHp();
            }
        }

        return dmg;
    }
}
