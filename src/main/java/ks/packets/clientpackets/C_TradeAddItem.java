package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Inventory;
import ks.model.L1Object;
import ks.model.L1Trade;
import ks.model.L1World;
import ks.model.instance.L1DollInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;

import java.util.Collection;

public class C_TradeAddItem extends ClientBasePacket {
    public C_TradeAddItem(byte[] data, L1Client client) {
        super(data);

        int itemid = readD();
        int itemcount = readD();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }

        L1ItemInstance item = pc.getInventory().getItem(itemid);

        if (itemid != item.getId()) {
            client.disconnect();
            return;
        }

        if (!item.isStackable() && itemcount != 1) {
            client.disconnect();
            return;
        }

        if (itemcount <= 0 || item.getCount() <= 0) {
            return;
        }

        if (itemcount > item.getCount()) {
            itemcount = item.getCount();
        }

        if (itemcount > 2000000000) {
            return;
        }

        if (!item.getItem().isTradeAble()) {
            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
            return;
        }

        if (pc.getTradeID() == 0) {
            return;
        }

        if (item.getBless() >= 128) {
            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
            return;
        }

        if (item.isEquipped()) {
            pc.sendPackets("착용중인 장비는 거래가 불가능합니다");
            return;
        }

        Collection<L1DollInstance> dollList = pc.getDollList().values();

        for (L1DollInstance doll : dollList) {
            if (item.getId() == doll.getItemObjId()) {
                pc.sendPackets(new S_ServerMessage(1181));
                return;
            }
        }

        Collection<L1NpcInstance> petlist = pc.getPetList().values();

        for (L1NpcInstance petObject : petlist) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                if (item.getId() == pet.getItemObjId()) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }
            }
        }

        L1Object tradingPartner = L1World.getInstance().findObject(pc.getTradeID());

        if (tradingPartner != null) {
            if (tradingPartner instanceof L1PcInstance) {
                L1PcInstance tradepc = (L1PcInstance) tradingPartner;

                if (tradepc.getInventory().checkAddItem(item, itemcount) != L1Inventory.OK) {
                    tradepc.sendPackets(new S_ServerMessage(270));
                    pc.sendPackets(new S_ServerMessage(271));
                    return;
                }

                if (pc.getTradeOk() || tradepc.getTradeOk()) {
                    String msg = "완료한 상태에서는 추가로 아이템을 올릴 수 없습니다.";

                    pc.sendPackets(msg);
                    tradepc.sendPackets(msg);

                    return;
                }

                pc.setTradeOk(false);
            }
        }

        L1CommonUtils.clearMagicItem(pc, item);

        L1Trade.addItem(pc, itemid, itemcount);
    }
}