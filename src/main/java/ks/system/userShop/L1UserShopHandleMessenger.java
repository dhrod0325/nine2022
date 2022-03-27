package ks.system.userShop;

import ks.model.L1PrivateShopBuy;
import ks.model.L1PrivateShopSell;

import java.util.ArrayList;
import java.util.List;

public class L1UserShopHandleMessenger {
    private final int handleId;

    private final L1UserShopNpcInstance shopInstance;

    private final List<L1PrivateShopSell> sellList = new ArrayList<>();
    private final List<L1PrivateShopBuy> buyList = new ArrayList<>();

    private int type;

    private int step;

    public L1UserShopHandleMessenger(L1UserShopNpcInstance shopInstance, int handleId) {
        this.shopInstance = shopInstance;
        this.handleId = handleId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getHandleId() {
        return handleId;
    }

    public L1UserShopNpcInstance getShopInstance() {
        return shopInstance;
    }

    public List<L1PrivateShopSell> getSellList() {
        return sellList;
    }

    public List<L1PrivateShopBuy> getBuyList() {
        return buyList;
    }

    public L1PrivateShopSell findSell(int objectId) {
        for (L1PrivateShopSell o : sellList) {
            if (o.getItemObjectId() == objectId) {
                return o;
            }
        }

        return null;
    }

    public L1PrivateShopBuy findBuy(int objectId) {
        for (L1PrivateShopBuy o : buyList) {
            if (o.getItemObjectId() == objectId) {
                return o;
            }
        }

        return null;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}