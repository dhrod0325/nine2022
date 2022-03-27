package ks.model;

import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;

import java.util.ArrayList;
import java.util.List;

public class L1ArmorSetImpl extends L1ArmorSet {
    private final int[] ids;

    private final List<L1ArmorSetEffect> effects = new ArrayList<>();

    public L1ArmorSetImpl(int[] ids) {
        this.ids = ids;
    }

    public void addEffect(L1ArmorSetEffect effect) {
        effects.add(effect);
    }

    public void removeEffect(L1ArmorSetEffect effect) {
        effects.remove(effect);
    }

    @Override
    public void cancelEffect(L1PcInstance pc) {
        for (L1ArmorSetEffect effect : effects) {
            effect.cancelEffect(pc);
        }
    }

    @Override
    public void giveEffect(L1PcInstance pc) {
        for (L1ArmorSetEffect effect : effects) {
            effect.giveEffect(pc);
        }
    }

    @Override
    public final boolean isValid(L1PcInstance pc) {
        return pc.getInventory().checkEquipped(ids);
    }

    @Override
    public boolean isPartOfSet(int id) {
        for (int i : ids) {
            if (id == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEquippedRingOfArmorSet(L1PcInstance pc) {
        L1PcInventory pcInventory = pc.getInventory();
        L1ItemInstance armor = null;
        boolean isSetContainRing = false;

        for (int id : ids) {
            armor = pcInventory.findItemId(id);
            if (armor.getItem().getType2() == 2 && armor.getItem().getType() == 9) { // ring
                isSetContainRing = true;
                break;
            }
        }

        if (armor != null && isSetContainRing) {
            int itemId = armor.getItem().getItemId();
            if (pcInventory.getTypeEquipped(2, 9) == 2) {
                L1ItemInstance[] ring = pcInventory.getRingEquipped();
                return ring[0].getItem().getItemId() == itemId && ring[1].getItem().getItemId() == itemId;
            }
        }

        return false;
    }

}
