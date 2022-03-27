package ks.model.skill.magic;

import ks.model.L1Character;

public interface L1Skill {
    int STATUS_RETURN = 2;
    int STATUS_CONTINUE = 1;
    int STATUS_END = 0;

    void runSkill(L1SkillRequest request);

    void stopSkill(L1Character targetCharacter);

    void sendIcon(L1Character targetCharacter, int duration);

    void sendGrfx(L1SkillRequest request, boolean isSkillAction);

    int interceptorDuration(L1SkillRequest request, int duration);

    int interceptDamage(L1SkillRequest request, int dmg);

    boolean interceptProbability(L1SkillRequest request, boolean success);

    int getRunSkillState();

    void completeSkill(L1SkillRequest request);

    void preSkill(L1SkillRequest request);
}
