package ks.model.instance;

import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;

public class L1FurnitureInstance extends L1NpcInstance {
    private int itemObjId;

    public L1FurnitureInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
    }

    @Override
    public void deleteMe() {
        super.deleteSpawn();
    }

    public int getItemObjId() {
        return itemObjId;
    }

    public void setItemObjId(int i) {
        itemObjId = i;
    }
}
