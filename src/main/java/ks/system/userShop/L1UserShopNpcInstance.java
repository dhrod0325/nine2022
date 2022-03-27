package ks.system.userShop;

import ks.constants.L1ActionCodes;
import ks.constants.L1ItemId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChangeShape;
import ks.packets.serverpackets.S_DoActionShop;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.ServerBasePacket;
import ks.system.userShop.buy.L1UserShopBuy;
import ks.system.userShop.buy.packet.S_PrivateShopShowBuyList;
import ks.system.userShop.sell.L1UserShopSell;
import ks.system.userShop.sell.packet.S_PrivateShopShowSellList;
import ks.system.userShop.table.L1UserShopTable;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class L1UserShopNpcInstance extends L1NpcInstance {
    private final Logger logger = LogManager.getLogger(getClass());

    private final L1UserShopTable shopTable = L1UserShopTable.getInstance();

    private final L1UserShopSell userShopSell = new L1UserShopSell(this);
    private final L1UserShopBuy userShopBuy = new L1UserShopBuy(this);

    private final L1UserShopInventory inventory = new L1UserShopInventory();

    private int masterObjId;
    private byte[] chat;
    private S_PrivateShopShowBuyList privateShopShowBuyList;

    private L1UserShopNpcInstance() {
        super(NpcTable.getInstance().getTemplate(460000071));
    }

    public L1UserShopNpcInstance(L1PcInstance pc) {
        this();

        String name = "[" + pc.getName() + "]";

        setId(ObjectIdFactory.getInstance().nextId());
        setX(pc.getX());
        setY(pc.getY());
        setMap(pc.getMap());
        setHeading(4);
        setMasterObjId(pc.getId());
        setName(name);
        setNameId(name);
        setLocation(pc.getX(), pc.getY(), pc.getMapId());

        getGfxId().setTempCharGfx(pc.getClassId());
        getGfxId().setGfxId(pc.getClassId());
    }

    @Override
    public L1UserShopInventory getInventory() {
        return inventory;
    }

    public void visibleWorld() {
        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(getMaster());

        for (L1PcInstance player : list) {
            if (player != null) {
                onPerceive(player);
            }
        }

        L1World.getInstance().addVisibleObject(this);
        L1World.getInstance().storeObject(this);

        L1UserShopManager.getInstance().register(this);

        Broadcaster.broadcastPacket(this, new S_ChangeShape(getId(), getGfxId().getGfxId()));
    }

    public void closeShop() {
        try {
            L1PcInstance master = getMaster();

            if (master != null) {
                master.sendPackets("개인상점을 종료합니다");

                shopTable.clearShopItems(getMasterObjId(), "buy");
                shopTable.clearShopItems(getMasterObjId(), "sell");
                shopTable.deleteShopBuyItem(getMasterObjId());
                shopTable.deleteShopLoc(getMasterObjId());

                for (L1ItemInstance item : new ArrayList<>(inventory.getItems())) {
                    try {
                        if (L1World.getInstance().getPlayer(master.getName()) == null) {
                            master.getInventory().loadItems(false);
                        }

                        inventory.tradeItem(item, item.getCount(), master.getInventory());

                        L1LogUtils.userShopLog("[개인 상점 종료] - {} 주인:{} 템:{}", getName(), master.getName(), L1LogUtils.logItemName(item, item.getCount()));

                        master.sendPackets(new S_SystemMessage(item.getNumberedViewName(item.getCount()) + "를 획득하였습니다."));
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            } else {
                logger.warn("상점 : 상점 종료중에 주인이 없는 상점이 있습니다 : " + getName());
            }

            userShopSell.getSellList().clear();
            userShopBuy.getBuyList().clear();

            L1UserShopCreateHandler.getInstance().unRegister(this);
            L1UserShopManager.getInstance().unRegister(this);

            deleteMe();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public L1UserShopHandleMessenger prepareStep1() {
        L1PcInstance pc = getMaster();

        L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

        if (shop == null) {
            List<L1Object> aroundPlayers = L1World.getInstance().getVisibleObjects(pc, 1);

            for (L1Object o : aroundPlayers) {
                if (o instanceof L1UserShopNpcInstance) {
                    pc.sendPackets("상점 : 주위에 개설된 상점이 있습니다. 1칸 떨어져서 상점을 개설하세요");

                    return null;
                }
            }
        }

        L1UserShopHandleMessenger messenger;

        int handleId = ObjectIdFactory.getInstance().nextId();

        if (shop == null) {
            messenger = new L1UserShopHandleMessenger(this, handleId);
        } else {
            messenger = new L1UserShopHandleMessenger(shop, handleId);
        }

        messenger.setStep(0);

        L1UserShopCreateHandler.getInstance().register(messenger);

        return messenger;
    }

    public void openShop(L1PcInstance pc) {
        if (userShopBuy.getBuyList().isEmpty() && userShopSell.getSellList().isEmpty()) {
            visibleWorld();
            pc.sendPackets("개인상점이 시작됩니다.");
            shopTable.saveShopLoc(getMasterObjId(), getX(), getY(), getMapId());
        } else {
            pc.sendPackets("물품이 추가 되었습니다.");
        }
    }

    public void showList(L1PcInstance pc, int type) {
        if (type == 0) {
            pc.setPartnersPrivateShopItemCount(userShopSell.getSellList().size());
            pc.sendPackets(new S_PrivateShopShowSellList(pc, this, userShopSell.getSellList()));
        } else if (type == 1) {
            pc.setPartnersPrivateShopItemCount(userShopBuy.getBuyList().size());

            this.privateShopShowBuyList = new S_PrivateShopShowBuyList(pc, this, userShopBuy.getBuyList());
            pc.sendPackets(privateShopShowBuyList);
        }
    }

    public S_PrivateShopShowBuyList getPrivateShopShowBuyList() {
        return privateShopShowBuyList;
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
    }

    @Override
    public void onAction(L1PcInstance pc) {
    }

    public byte[] getChat() {
        return chat;
    }

    public void setChat(byte[] chat) {
        this.chat = chat;
    }

    public List<L1PrivateShopSell> getSellList() {
        return userShopSell.getSellList();
    }

    public List<L1PrivateShopBuy> getBuyList() {
        return userShopBuy.getBuyList();
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        super.onPerceive(perceivedFrom);
        shopChatChange();
    }

    public void shopChatChange() {
        String msg = null;

        if (getChat() != null) {
            msg = new String(getChat());
        } else {
            if (!userShopSell.getSellList().isEmpty()) {
                L1PrivateShopSell a = getSellList().get(RandomUtils.nextInt(getSellList().size()));
                msg = a.getItem().getName();
            }
        }

        List<L1PcInstance> pcList = L1World.getInstance().getVisiblePlayer(this);

        for (L1PcInstance pc : pcList) {
            pc.sendPackets(new S_DoActionShop(getId(), L1ActionCodes.ACTION_Shop, msg));
        }
    }

    public int getMasterObjId() {
        return masterObjId;
    }

    public void setMasterObjId(int masterObjId) {
        this.masterObjId = masterObjId;
    }

    public void sendPacketToMaster(ServerBasePacket packet) {
        L1PcInstance master = getMaster();

        if (master == null) {
            logger.info("sendPacketToMaster() 상점 : " + getName() + " 미접속 상태라 전송하지 못함");
            return;
        }

        getMaster().sendPackets(packet);
    }

    @Override
    public L1PcInstance getMaster() {
        try {
            L1PcInstance master = L1World.getInstance().getPlayer(masterObjId);

            if (master == null) {
                return CharacterTable.getInstance().restoreCharacter(masterObjId);
            } else {
                return master;
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    @Override
    public void setMaster(L1PcInstance cha) {
        logger.info("적용 안되는 메소드(setMaster)가 호출되었습니다");
    }

    public L1UserShopSell getUserShopSell() {
        return userShopSell;
    }

    public L1UserShopBuy getUserShopBuy() {
        return userShopBuy;
    }

    public void pushAdena(L1PcInstance pc, int adenaCount) {
        if (!pc.getInventory().checkItem(L1ItemId.ADENA, adenaCount)) {
            pc.sendPackets("아데나가 부족합니다");
            return;
        }

        L1ItemInstance adena = pc.getInventory().findItemId(L1ItemId.ADENA);

        pc.getInventory().tradeItem(adena, adenaCount, getInventory());

        pc.sendPackets("상점으로 입금이 완료 되었습니다");

        shopTable.updateShopLoc(getInventory().getAdenaCount(), pc.getId());
    }
}
