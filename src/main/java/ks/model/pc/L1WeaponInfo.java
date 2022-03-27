package ks.model.pc;

import ks.model.instance.L1ItemInstance;

public class L1WeaponInfo {
    private L1ItemInstance weapon;
    private int weaponId;
    private int weaponType;
    private int weaponGrade;
    private int weaponEnchant;
    private int weaponMaterial;
    private int weaponAttrEnchantLevel;
    private boolean bow;
    private boolean guntlet;
    private boolean longAttack;

    public L1WeaponInfo(L1ItemInstance weapon) {
        this.weapon = weapon;

        if (weapon != null) {
            this.weaponId = weapon.getItem().getItemId();
            this.weaponType = weapon.getItem().getType1();
            this.weaponGrade = weapon.getItem().getItemGrade();
            this.weaponEnchant = weapon.getEnchantLevel();
            this.weaponMaterial = weapon.getItem().getMaterial();
            this.bow = weapon.isBow();
            this.guntlet = weapon.isGuntlet();
            this.longAttack = bow || guntlet;
            this.weaponAttrEnchantLevel = weapon.getAttrEnchantLevel();
        }
    }

    public boolean isLongAttack() {
        return longAttack;
    }

    public boolean isGuntlet() {
        return guntlet;
    }

    public boolean isBow() {
        return bow;
    }

    public L1ItemInstance getWeapon() {
        return weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        this.weapon = weapon;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public int getWeaponType() {
        return weaponType;
    }

    public int getWeaponGrade() {
        return weaponGrade;
    }

    public int getWeaponEnchant() {
        return weaponEnchant;
    }

    public int getWeaponMaterial() {
        return weaponMaterial;
    }

    public int getAttrEnchantLevel() {
        return weaponAttrEnchantLevel;
    }
}
