package ks.model.item.function;

import ks.constants.L1ActionCodes;
import ks.core.ObjectIdFactory;
import ks.core.datatables.FurnitureSpawnTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.*;
import ks.model.instance.L1FurnitureInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_AttackPacket;
import ks.packets.serverpackets.S_ServerMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnitureItem extends L1ItemInstance {
    private final Logger logger = LogManager.getLogger();

    public FurnitureItem(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = this.getItemId();
            if (itemId >= 41383 && itemId <= 41400) { // 가구
                useFurnitureItem(pc, itemId, this.getId());
            } else if (itemId == 41401) {
                useFurnitureRemovalWand(pc, packet.readD(), useItem);
            }
        }
    }

    private void useFurnitureItem(L1PcInstance pc, int itemId, int itemObjectId) {
        if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
            pc.sendPackets(new S_ServerMessage(563));
            pc.tell();

            return;
        }

        boolean isAppear = true;
        L1FurnitureInstance furniture = null;

        for (L1Object l1object : L1World.getInstance().getAllObject()) {
            if (l1object instanceof L1FurnitureInstance) {
                furniture = (L1FurnitureInstance) l1object;
                if (furniture.getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 가구
                    isAppear = false;
                    break;
                }
            }
        }

        if (isAppear) {
            if (pc.getHeading() != 0 && pc.getHeading() != 2) {
                return;
            }

            int npcId = 0;

            switch (itemId) {
                case 41383:
                    npcId = 80109;
                    break;
                case 41384:
                    npcId = 80110;
                    break;
                case 41385:
                    npcId = 80113;
                    break;
                case 41386:
                    npcId = 80114;
                    break;
                case 41387:
                    npcId = 80115;
                    break;
                case 41388:
                    npcId = 80124;
                    break;
                case 41389:
                case 41390:
                    npcId = 80118;
                    break;
                case 41391:
                    npcId = 80120;
                    break;
                case 41392:
                    npcId = 80121;
                    break;
                case 41393:
                    npcId = 80126;
                    break;
                case 41394:
                    npcId = 80125;
                    break;
                case 41395:
                    npcId = 80111;
                    break;
                case 41396:
                    npcId = 80112;
                    break;
                case 41397:
                    npcId = 80116;
                    break;
                case 41398:
                    npcId = 80117;
                    break;
                case 41399:
                    npcId = 80122;
                    break;
                case 41400:
                    npcId = 80123;
                    break;
            }

            try {
                L1Npc npc = NpcTable.getInstance().getTemplate(npcId);

                if (npc != null) {
                    try {
                        furniture = new L1FurnitureInstance(npc);
                        furniture.setId(ObjectIdFactory.getInstance().nextId());
                        furniture.setMap(pc.getMapId());
                        if (pc.getHeading() == 0) {
                            furniture.setX(pc.getX());
                            furniture.setY(pc.getY() - 1);
                        } else if (pc.getHeading() == 2) {
                            furniture.setX(pc.getX() + 1);
                            furniture.setY(pc.getY());
                        }
                        furniture.setHomeX(furniture.getX());
                        furniture.setHomeY(furniture.getY());
                        furniture.setHeading(0);
                        furniture.setItemObjId(itemObjectId);

                        L1World.getInstance().storeObject(furniture);
                        L1World.getInstance().addVisibleObject(furniture);

                        FurnitureSpawnTable.getInstance().insertFurniture(furniture);
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }
            } catch (Exception e) {
                logger.error("오류", e);
            }
        } else {
            furniture.deleteMe();
            FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
        }
    }

    private void useFurnitureRemovalWand(L1PcInstance pc, int targetId, L1ItemInstance item) {
        pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
        Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
        int chargeCount = item.getChargeCount();
        if (chargeCount <= 0) {
            return;
        }

        L1Object target = L1World.getInstance().findObject(targetId);

        if (target instanceof L1FurnitureInstance) {
            L1FurnitureInstance furniture = (L1FurnitureInstance) target;
            furniture.deleteMe();
            FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
            item.setChargeCount(item.getChargeCount() - 1);
            pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
        }
    }
}
