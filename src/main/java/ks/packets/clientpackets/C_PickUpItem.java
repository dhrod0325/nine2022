package ks.packets.clientpackets;

import ks.constants.L1ActionCodes;
import ks.constants.L1ItemId;
import ks.core.datatables.MapsTable;
import ks.core.datatables.bugCheck.BugCheckTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.network.L1Client;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_AttackPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;

import java.util.List;

public class C_PickUpItem extends ClientBasePacket {
    public C_PickUpItem(byte[] data, L1Client client) {
        super(data);

        int x = readH();
        int y = readH();

        int objectId = readD();
        int pickupCount = readD();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        pc.saveInventory();

        if (pc.getTradeID() != 0) {
            L1Trade.cancel(pc);
        }

        if (pc.getOnlineStatus() != 1) {
            pc.disconnect(pc.getName() + " 줍기버그1");
            return;
        }

        if (L1CommonUtils.isTwoLogin(pc)) {
            return;
        }

        if (pc.isDead()) {
            return;
        }

        if (pc.isInvisible()) {
            return;
        }

        if (pc.isInvisDelay()) {
            return;
        }

        if (pc.getInventory().isFullWeightOrFullCount()) {
            return;
        }

        pc.cancelAbsoluteBarrier();

        L1Inventory groundInventory = L1World.getInstance().getInventory(x, y, pc.getMapId());
        L1ItemInstance item = groundInventory.getItem(objectId);

        if (item != null && !pc.isDead()) {
            if (item.getItemOwner() != null) {
                if (item.getItemOwner().isInParty()) {
                    if (!item.getItemOwner().getParty().isMember(pc)) {
                        pc.sendPackets(new S_ServerMessage(623));
                        return;
                    }
                } else {
                    if (item.getItemOwner().getId() != pc.getId()) {
                        pc.sendPackets(new S_ServerMessage(623));
                        return;
                    }
                }
            }

            if (!item.isStackable() && pickupCount != 1) {
                pc.disconnect(pc.getName() + " 줍기버그2");
                return;
            }

            if (objectId != item.getId()) {
                return;
            }

            int itemType = item.getItem().getType2();

            if (itemType != 0 && item.getCount() != 1) {
                return;
            }

            if (pickupCount <= 0 || item.getCount() <= 0) {
                pc.disconnect(pc.getName() + " 줍기버그3");
                groundInventory.deleteItem(item);
                return;
            }

            if (!BugCheckTable.getInstance().isPickUpAble(item, pickupCount)) {
                if (!pc.isGm()) {
                    L1LogUtils.bugLog("줍기 버그 : " + pc.getName() + "," + item.getName() + "," + item.getEnchantLevel() + "," + pickupCount);
                    pc.sendPackets("획득 불가능한 아이템입니다");
                    logger.info("주울수 없는 아이템 : pc:" + pc.getName() + "item:" + item.getName() + " x:" + item.getX() + " y:" + item.getY() + " m:" + item.getMapId());
                    return;
                }
            }

            if (pickupCount > item.getCount()) {
                pickupCount = item.getCount();
            }

            if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
                return;
            }

            if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) {
                if (pickupCount > 1 || item.getCount() > 1) {
                    pc.disconnect(pc.getName() + " 줍기버그5");
                    return;
                }
            }

            if (x > pc.getX() + 1 || x < pc.getX() - 1 || y > pc.getY() + 1 || y < pc.getY() - 1) {
                return;
            }

            if (item.getItem().getItemId() == L1ItemId.ADENA) {
                L1ItemInstance inventoryItem = pc.getInventory().findItemId(L1ItemId.ADENA);

                int inventoryItemCount = 0;

                if (inventoryItem != null) {
                    inventoryItemCount = inventoryItem.getCount();
                }

                if ((long) inventoryItemCount + (long) pickupCount > 2000000000L) {
                    pc.sendPackets(new S_ServerMessage(166, "소지하고 있는 아데나", "2,000,000,000을 초과하므로 주울 수 없습니다."));
                    return;
                }
            }

            if (item.getX() == 0 || item.getY() == 0) {
                return;
            }

            if (pc.getInventory().checkAddItem(item, pickupCount) != L1Inventory.OK) {
                return;
            }

            pickUpMotion(pc, objectId);

            if (pc.isInParty()) {
                pickUpOfParty(pc, item, pickupCount, groundInventory);
            } else {
                pickUpOfSolo(pc, item, pickupCount, groundInventory);
            }

            pickUpEnd(pc, item, pickupCount, groundInventory);
        }
    }

    private void pickUpEnd(L1PcInstance pc, L1ItemInstance item, int pickupCount, L1Inventory groundInventory) {
        if (item.getDropMobId() != 0) {
            item.setDropMobId(0);

            if (item.getItem().getPickupMent() == 1) {
                String mapName = MapsTable.getInstance().getMapName(pc.getMapId());

                L1World.getInstance().broadcastPacketGreenMessage(String.format("어느 아덴용사가 %s에서 %s을 획득하였습니다", mapName, item.getName()));
            }
        }
    }

    private void pickUpMotion(L1PcInstance pc, int objectId) {
        pc.getLight().turnOnOffLight();
        pc.sendPackets(new S_AttackPacket(pc, objectId, L1ActionCodes.ACTION_Pickup));

        if (!pc.isGmInvis()) {
            Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, objectId, L1ActionCodes.ACTION_Pickup));
        }
    }

    private void pickUpOfParty(L1PcInstance pc, L1ItemInstance item, int pickupCount, L1Inventory groundInventory) {
        List<L1PcInstance> partyMembers = pc.getParty().getVisiblePartyMembers(pc);

        if (item.getDropMobId() != 0) {
            L1Npc npc = NpcTable.getInstance().getTemplate(item.getDropMobId());

            if (item.getItemId() == L1ItemId.ADENA) {
                int count = pickupCount / partyMembers.size();

                int i = 0;

                for (L1PcInstance member : partyMembers) {
                    if (i == partyMembers.size() - 1) {
                        count += item.getCount() - count;
                    }

                    if (member.getMent().isDrop()) {
                        member.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(count), member.getName()));
                    }

                    saveInventory(member, item, count, groundInventory);

                    i++;
                }
            } else {
                L1PcInstance invUser = partyMembers.get(RandomUtils.nextInt(partyMembers.size()));

                for (L1PcInstance member : partyMembers) {
                    if (member.getMent().isDrop()) {
                        member.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(pickupCount), invUser.getName()));
                    }
                }

                saveInventory(invUser, item, pickupCount, groundInventory);
            }
        } else {
            saveInventory(pc, item, pickupCount, groundInventory);
        }
    }

    private void pickUpOfSolo(L1PcInstance pc, L1ItemInstance item, int pickupCount, L1Inventory groundInventory) {
        saveInventory(pc, item, pickupCount, groundInventory);
    }

    private void saveInventory(L1PcInstance pc, L1ItemInstance item, int pickupCount, L1Inventory groundInventory) {
        groundInventory.tradeItem(item, pickupCount, pc.getInventory());
        pc.saveInventory();

        L1LogUtils.debugLog("픽업:{} 획득:{} 아이템:{}", pc.getName(), pc.getName(), L1LogUtils.logItemName(item, pickupCount));
    }
}
