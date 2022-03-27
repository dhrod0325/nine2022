package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.log.L1LogUtils;

import java.util.Collection;

import static ks.core.network.opcode.L1Opcodes.SERVER_VERSION;

public class C_DeleteInventoryItem extends ClientBasePacket {
    public C_DeleteInventoryItem(byte[] data, L1Client client) {
        super(data);
        if (client == null) {
            return;
        }
        int itemObjectId = readD();
        L1PcInstance pc = client.getActiveChar();
        L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

        if (item == null) {
            return;
        }

        if (!item.getItem().isDeleteAble()) {
            pc.sendPackets(new S_ServerMessage(125));
            return;
        }

        if (item.isEquipped()) {
            pc.sendPackets(new S_ServerMessage(125));
            return;
        }

        if (!pc.isGm() && (
                item.getItemId() >= 0 && (item.getItemId() == 46115//마빈
                        || item.getItemId() == 46116//구슬조각
                        || item.getItemId() == 46118//구슬조각
                        || item.getItemId() == 4500011//서버정보
                        || item.getItemId() == 41159//서버정보
                        || item.getItemId() == 46193))) {//생존외침
            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
            return;
        }

        if (item.getName().contains("마법인형 : ")) {
            try {
                if (pc.isUsingDoll()) {
                    if (pc.getCurrentDollItem().getItemId() == item.getItemId()) {
                        pc.sendPackets("소환중인 인형은 삭제 할 수 없습니다");

                        return;
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }

        if (item.getNextReq() == 1) {
            pc.sendPackets("이월 아이템은 삭제 할 수 없습니다");
            return;
        }

        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (L1NpcInstance petObject : petList) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                if (item.getId() == pet.getItemObjId()) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }
            }
        }

        L1LogUtils.debugLog("[인벤삭제] : {} / {}", pc.getName(), L1LogUtils.logItemName(item));

        if (SERVER_VERSION == 3.1) {
            pc.getInventory().removeItem(item, item.getCount());
        } else if (SERVER_VERSION == 3.8) {
            if (item.getCount() > 1) {
                pc.getInventory().removeItem(item, readD());
            } else {
                pc.getInventory().removeItem(item, item.getCount());
            }
        }

        pc.getLight().turnOnOffLight();
    }
}
