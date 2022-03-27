package ks.model;

public class L1Weapon extends L1Item {
    private int _range = 0;
    private int _hitModifier = 0;
    private int _dmgModifier = 0;
    private int _addDmg = 0;
    private int _doubleDmgChance;
    private int _magicDmgModifier = 0;
    private int _canbedmg = 0;

    public L1Weapon() {
    }

    @Override
    public int getRange() {
        return _range;
    }

    public void setRange(int i) {
        _range = i;
    }

    @Override
    public int getHitModifier() {
        return _hitModifier;
    }

    public void setHitModifier(int i) {
        _hitModifier = i;
    }

    @Override
    public int getHitUp() {
        return super.getHitUp();
    }

    @Override
    public int getDmgUp() {
        return super.getDmgUp();
    }

    @Override
    public int getBowDmgUp() {
        return super.getBowDmgUp();
    }

    @Override
    public int getBowHitUp() {
        return super.getBowHitUp();
    }

    @Override
    public int getDmgModifier() {
        return _dmgModifier;
    }

    public void setDmgModifier(int i) {
        _dmgModifier = i;
    }

    public int getaddDmg() {
        return _addDmg;
    }

    public void set_addDmg(int i) {
        _addDmg = i;
    }

    @Override
    public int getDoubleDmgChance() {
        return _doubleDmgChance;
    }

    public void setDoubleDmgChance(int i) {
        _doubleDmgChance = i;
    }

    @Override
    public int getMagicDmgModifier() {
        return _magicDmgModifier;
    }

    public void setMagicDmgModifier(int i) {
        _magicDmgModifier = i;
    }

    @Override
    public int getCanbeDmg() {
        return _canbedmg;
    }

    public void set_canbedmg(int i) {
        _canbedmg = i;
    }

    @Override
    public boolean isTwoHandedWeapon() {
        int weapon_type = getType();

        return (weapon_type == 3 || weapon_type == 4
                || weapon_type == 5 || weapon_type == 11 || weapon_type == 12
                || weapon_type == 15 || weapon_type == 16 || weapon_type == 18 || weapon_type == 19);
    }
}
