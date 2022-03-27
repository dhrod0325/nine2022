package ks.model.instance.extend.event;

import ks.model.L1Character;

public interface L1DeathEvent {
    //캐릭터가 죽었을때 주변 캐릭에 공격자와 죽은 캐릭터를 알림
    void onAroundDeath(L1Character attacker, L1Character deathCharacter);
}
