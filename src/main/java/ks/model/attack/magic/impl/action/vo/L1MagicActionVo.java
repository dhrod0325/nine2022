package ks.model.attack.magic.impl.action.vo;

public class L1MagicActionVo {
    private int damage;
    private int drainMana;

    public L1MagicActionVo(int damage, int drainMana) {
        this.damage = damage;
        this.drainMana = drainMana;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDrainMana() {
        return drainMana;
    }

    public void setDrainMana(int drainMana) {
        this.drainMana = drainMana;
    }
}
