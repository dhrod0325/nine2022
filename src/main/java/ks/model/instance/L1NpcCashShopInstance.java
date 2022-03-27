package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionShop;
import ks.packets.serverpackets.S_NPCPack;

@SuppressWarnings("unused")
public class L1NpcCashShopInstance extends L1NpcInstance {
    private int state = 0;
    private String shopName;

    public L1NpcCashShopInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));

        if (state == 1)
            perceivedFrom.sendPackets(new S_DoActionShop(getId(), L1ActionCodes.ACTION_Shop, getShopName().getBytes()));
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
    }


    public int getState() {
        return state;
    }

    public void setState(int i) {
        state = i;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String name) {
        shopName = name;
    }

}
