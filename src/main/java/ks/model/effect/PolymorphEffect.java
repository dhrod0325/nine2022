package ks.model.effect;

import ks.model.L1ArmorSetEffect;
import ks.model.L1PolyMorph;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;

public class PolymorphEffect implements L1ArmorSetEffect {
    private int gfxId;

    public PolymorphEffect(int gfxId) {
        this.gfxId = gfxId;
    }

    public void giveEffect(L1PcInstance pc) {
        if (gfxId == 6080 || gfxId == 6094) {
            if (pc.getSex() == 0) {
                gfxId = 6094;
            } else {
                gfxId = 6080;
            }

            if (!isRemainderOfCharge(pc)) {
                return;
            }
        }

        L1PolyMorph.doPoly(pc, gfxId, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
    }

    public void cancelEffect(L1PcInstance pc) {
        if (gfxId == 6080) {
            if (pc.getSex() == 0) {
                gfxId = 6094;
            }
        }
        if (pc.getGfxId().getTempCharGfx() != gfxId) {
            return;
        }

        L1PolyMorph.undoPoly(pc);
    }

    private boolean isRemainderOfCharge(L1PcInstance pc) {
        if (pc.getInventory().checkItem(20383, 1)) {
            L1ItemInstance item = pc.getInventory().findItemId(20383);

            if (item != null) {
                return item.getChargeCount() != 0;
            }
        }
        return false;
    }
}
