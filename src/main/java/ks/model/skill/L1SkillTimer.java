package ks.model.skill;

public interface L1SkillTimer {
    int getRemainingTime();

    void begin();

    void end();

    void kill();
}

