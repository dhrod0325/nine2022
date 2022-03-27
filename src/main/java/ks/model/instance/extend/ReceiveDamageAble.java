package ks.model.instance.extend;

import ks.model.L1Character;

public interface ReceiveDamageAble {
    void receiveManaDamage(L1Character attacker, int damageMp);

    void receiveDamage(L1Character attacker, int damage);
}