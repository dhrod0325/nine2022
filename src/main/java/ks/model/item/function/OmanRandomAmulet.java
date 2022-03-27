package ks.model.item.function;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Inventory;
import ks.model.L1Item;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.random.RandomUtils;

public class OmanRandomAmulet {
    public static void clickItem(L1PcInstance pc, int itemId, L1ItemInstance l1iteminstance) {
        int chance = RandomUtils.nextInt(100) + 1;

        switch (itemId) {
            case 830042: //혼돈의 오만의 탑 1층 이동 부적
            case 830052: //변이된 오만의 탑 1층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001215);
                } else {
                    saveItem(pc, 40289);
                }

                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830043: //혼돈의 오만의 탑 2층 이동 부적
            case 830053: //변이된 오만의 탑 2층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001216);
                } else {
                    saveItem(pc, 40290);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830044: //혼돈의 오만의 탑 3층 이동 부적
            case 830054: //변이된 오만의 탑 3층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001217);
                } else {
                    saveItem(pc, 40291);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830045: //혼돈의 오만의 탑 4층 이동 부적
            case 830055: //변이된 오만의 탑 4층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001218);
                } else {
                    saveItem(pc, 40292);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830046: //혼돈의 오만의 탑 5층 이동 부적
            case 830056: //변이된 오만의 탑 5층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001219);
                } else {
                    saveItem(pc, 40293);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830047: //혼돈의 오만의 탑 6층 이동 부적
            case 830057: //변이된 오만의 탑 6층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001220);
                } else {
                    saveItem(pc, 40294);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830048: //혼돈의 오만의 탑 7층 이동 부적
            case 830058: //변이된 오만의 탑 7층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001221);
                } else {
                    saveItem(pc, 40295);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830049: //혼돈의 오만의 탑 8층 이동 부적
            case 830059: //변이된 오만의 탑 8층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001222);
                    successMent(pc, 60001222);
                } else {
                    saveItem(pc, 40296);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830050: //혼돈의 오만의 탑 9층 이동 부적
            case 830060: //변이된 오만의 탑 9층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001223);
                    successMent(pc, 60001223);
                } else {
                    saveItem(pc, 40297);
                }
                pc.getInventory().removeItem(l1iteminstance, 1);
                break;
            case 830051: //혼돈의 오만의 탑 10층 이동 부적
            case 830061: //변이된 오만의 탑 10층 이동 부적
                if (chance <= 20) {
                    saveItem(pc, 60001224);
                    successMent(pc, 60001224);
                } else {
                    saveItem(pc, 60001312);
                }

                pc.getInventory().removeItem(l1iteminstance, 1);
                break;

        }
    }

    private static void successMent(L1PcInstance pc, int itemId) {
        L1Item item = ItemTable.getInstance().findItem(itemId);
        L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 " + item.getName() + "제작에 성공하였습니다");
    }

    private static void saveItem(L1PcInstance pc, int item_id) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);

        if (item != null) {
            item.setCount(1);
            item.setEnchantLevel(0);
            item.setIdentified(true);
            if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                pc.sendPackets(new S_ServerMessage(82));
                return;
            }

            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
        }
    }
}
