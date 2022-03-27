package ks.model;

import ks.model.instance.L1ItemInstance;
import ks.util.L1CommonUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.*;

import static ks.constants.L1ItemId.룸티스붉은빛귀걸이;
import static ks.constants.L1ItemId.축룸티스붉은빛귀걸이;

public class L1CurrentItem {
    private final Map<Integer, L1ItemInstance> rings = new HashMap<>(4);
    private final Map<Integer, L1ItemInstance> runes = new HashMap<>(3);

    private L1ItemInstance helm;
    private L1ItemInstance armor;
    private L1ItemInstance shirt;
    private L1ItemInstance cloak;
    private L1ItemInstance glove;
    private L1ItemInstance boots;
    private L1ItemInstance shield;
    private L1ItemInstance necklace;
    private L1ItemInstance belt;
    private L1ItemInstance earRing;
    private L1ItemInstance weapon;

    public void setRing(int idx, L1ItemInstance item) {
        rings.remove(idx);
        rings.put(idx, item);
    }

    public void removeRing(int idx) {
        rings.remove(idx);
    }

    public boolean isArmor(int itemId) {
        if (armor == null) {
            return false;
        }

        return armor.getItemId() == itemId;
    }

    public void addRune(int idx, L1ItemInstance item) {
        runes.put(idx, item);
    }

    public void removeRune(int idx) {
        runes.remove(idx);
    }

    public L1ItemInstance getHelm() {
        return helm;
    }

    public void setHelm(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.helm = item;
        } else {
            this.helm = null;
        }
    }

    public L1ItemInstance getArmor() {
        return armor;
    }

    public void setArmor(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.armor = item;
        } else {
            this.armor = null;
        }
    }

    public L1ItemInstance getShirt() {
        return shirt;
    }

    public void setShirt(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.shirt = item;
        } else {
            this.shirt = null;
        }
    }

    public L1ItemInstance getCloak() {
        return cloak;
    }

    public void setCloak(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.cloak = item;
        } else {
            this.cloak = null;
        }
    }

    public L1ItemInstance getGlove() {
        return glove;
    }

    public void setGlove(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.glove = item;
        } else {
            this.glove = null;
        }
    }

    public L1ItemInstance getBoots() {
        return boots;
    }

    public void setBoots(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.boots = item;
        } else {
            this.boots = null;
        }
    }

    public L1ItemInstance getShield() {
        return shield;
    }

    public void setShield(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.shield = item;
        } else {
            this.shield = null;
        }
    }

    public L1ItemInstance getNecklace() {
        return necklace;
    }

    public void setNecklace(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.necklace = item;
        } else {
            this.necklace = null;
        }
    }

    public L1ItemInstance getBelt() {
        return belt;
    }

    public void setBelt(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.belt = item;
        } else {
            this.belt = null;
        }
    }

    public L1ItemInstance getEarRing() {
        return earRing;
    }

    public void setEarRing(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.earRing = item;
        } else {
            this.earRing = null;
        }
    }

    public L1ItemInstance getWeapon() {
        return weapon;
    }

    public void setWeapon(L1ItemInstance item) {
        if (item.isEquipped()) {
            this.weapon = item;
        } else {
            this.weapon = null;
        }
    }

    public L1ItemInstance getRing(int idx) {
        return rings.get(idx);
    }

    public Collection<L1ItemInstance> getRings() {
        return rings.values();
    }

    public L1ItemInstance getRune(int idx) {
        return runes.get(idx);
    }

    public Collection<L1ItemInstance> getRunes() {
        return runes.values();
    }

    public boolean isRindArmor() {
        if (armor == null)
            return false;

        return L1CommonUtils.isRindArmor(armor.getItemId());
    }

    public boolean isAntaArmor() {
        if (armor == null)
            return false;
        return L1CommonUtils.isAntaArmor(armor.getItemId());
    }

    public boolean isValaArmor() {
        if (armor == null)
            return false;

        return L1CommonUtils.isValaArmor(armor.getItemId());
    }

    public boolean isPapooArmor() {
        if (armor == null)
            return false;

        return L1CommonUtils.isPapooArmor(armor.getItemId());
    }

    public boolean isRedEarRing() {
        if (earRing == null) {
            return false;
        }

        return earRing.getItemId() == 룸티스붉은빛귀걸이 || earRing.getItemId() == 축룸티스붉은빛귀걸이;
    }

    public List<L1ItemInstance> getItems() {
        List<L1ItemInstance> items = new ArrayList<>();

        items.addAll(rings.values());
        items.addAll(runes.values());
        items.add(helm);
        items.add(armor);
        items.add(shirt);
        items.add(cloak);
        items.add(glove);
        items.add(boots);
        items.add(shield);
        items.add(necklace);
        items.add(belt);
        items.add(earRing);
        items.add(weapon);

        return items;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
