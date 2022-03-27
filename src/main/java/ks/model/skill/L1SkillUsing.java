package ks.model.skill;

public interface L1SkillUsing {
    int getSkillIcon();

    void sendIcon(int buffIconDuration);

    void runSkill(int buffIconDuration);

    void stopSkill();

}
