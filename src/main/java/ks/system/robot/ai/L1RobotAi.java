package ks.system.robot.ai;

import ks.model.L1Character;

public interface L1RobotAi {
    void toAiWalk();

    void toAiAttack();

    void toAiDead();

    void toAiCorpse();

    void toAiSpawn();

    void toAiEscape();

    void toAiPickup();

    void toAiShop();

    void toAiTargetMove();

    void toAiSetting();

    void receiveDamage(L1Character attacker, int damage);

    void toAI(long time);

    void setAiStatus(int aiStatus);

    boolean isAi(long time);
}