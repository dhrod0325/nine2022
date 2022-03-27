package ks.model.instance.extend;

import ks.model.L1Character;

public interface DrainHpMpAble {
    int getDrainMp();

    void setDrainMp(int mp);

    int getDrainHp();

    void setDrainHp(int hp);

    int drainMana(L1Character target);

    int drainHp(L1Character target);
}
