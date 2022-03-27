package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.instance.L1MonsterInstance;
import ks.model.skill.magic.L1Skill;
import ks.model.skill.magic.L1SkillRequest;
import ks.system.boss.L1BossSpawnManager;

import static ks.constants.L1SkillId.CHILL_TOUCH;

public class L1SkillHpDrain extends L1SkillAdapter implements L1Skill {
    public L1SkillHpDrain(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        int skillId = request.getSkillId();
        int dmg = request.getMagic().getMagicParam().getDamage();

        int heal;

        if (skillId == CHILL_TOUCH) {
            heal = dmg / 4;
        } else {
            if (targetCharacter instanceof L1MonsterInstance) {
                if (L1BossSpawnManager.getInstance().isSpawned((L1MonsterInstance) targetCharacter)) {
                    heal = 0;
                } else {
                    heal = dmg / 2;
                }
            } else {
                heal = dmg / 2;
            }
        }

        if (heal > 0) {
            if ((heal + skillUseCharacter.getCurrentHp()) > skillUseCharacter.getMaxHp()) {
                skillUseCharacter.setCurrentHp(skillUseCharacter.getMaxHp());
            } else {
                skillUseCharacter.setCurrentHp(heal + skillUseCharacter.getCurrentHp());
            }
        }
    }
}
