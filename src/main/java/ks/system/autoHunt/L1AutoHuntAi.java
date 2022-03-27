package ks.system.autoHunt;

import ks.model.L1Character;

public interface L1AutoHuntAi {
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

    boolean isAi(long time);
}