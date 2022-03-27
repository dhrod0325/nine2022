package ks.core.datatables.next_items;


import ks.app.config.prop.CodeConfig;
import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.model.inventory.InventoryInfoMessengerAdapter;
import ks.model.inventory.S_InventoryInfo;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ShopAndWarehouse;
import ks.packets.serverpackets.S_ItemStatus;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.log.L1LogUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class CharacterNextReturnUtils {
    public static void changeNextReturn(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem, int count) {
        if (!CodeConfig.NEXT_REQ_START) {
            pc.sendPackets("보상이벤트가 시작되지 않았습니다");
            return;
        }

        if (CharacterNextReqTable.getInstance().isExists(targetItem.getId())) {
            pc.sendPackets("이미 이월 저장되었습니다");
            return;
        }

        CharacterNextReturn check = CharacterNextReqTable.getInstance().getReturnItem(targetItem.getItemId(), targetItem.getEnchantLevel(), targetItem.getBless());

        if (check != null) {
            if (targetItem.isEquipped()) {
                pc.getInventory().setEquipped(targetItem, false);
            }

            if (count <= 0 || count > targetItem.getCount()) {
                pc.sendPackets("[오류발생]");
                return;
            }

            CharacterNextReq vo = new CharacterNextReq();
            vo.setPassword(pc.getAccountName() + "," + pc.getAccount().getPassword());
            vo.setItemId(targetItem.getItemId());
            vo.setItemName(targetItem.getName());
            vo.setItemCount(count);
            vo.setServerState(0);
            vo.setRegDate(new Date());
            vo.setItemEnchant(targetItem.getEnchantLevel());
            vo.setItemAttr(targetItem.getAttrEnchantLevel());
            vo.setItemBless(targetItem.getBless());
            vo.setServerState(CodeConfig.NEXT_REQ_SERVER_STATE);
            vo.setItemObjId(targetItem.getId());

            CharacterNextReqTable.getInstance().insert(vo);
            targetItem.setNextReq(1);

            pc.sendPackets(new S_ItemStatus(targetItem));
            pc.sendPackets(String.format("[보상저장] : %s가 저장되었습니다", targetItem.getName() + "(" + count + ")"));

            pc.getInventory().removeItem(useItem, 1);

            L1LogUtils.debugLog(String.format("[보상저장] : [%s] 아이템 : %s", pc.getName(), targetItem.getViewName() + "(" + count + ")"));
        } else {
            pc.sendPackets(String.format("[보상저장] : %s는 다음차 보상 아이템이 아닙니다", targetItem.getViewName2()));

        }
    }

    public static void saveNextReturn(L1PcInstance pc, L1ItemInstance item, int count) {
        if (!CodeConfig.NEXT_REQ_START) {
            pc.sendPackets("보상이벤트가 시작되지 않았습니다");
            return;
        }

        CharacterNextReturn check = CharacterNextReqTable.getInstance().getReturnItem(item.getItemId(), item.getEnchantLevel(), item.getBless());

        if (check != null) {
            if (item.isEquipped()) {
                pc.getInventory().setEquipped(item, false);
            }

            if (count <= 0 || count > item.getCount()) {
                pc.sendPackets("[오류발생]");
                return;
            }

            CharacterNextReq vo = new CharacterNextReq();
            vo.setPassword(pc.getAccountName() + "," + pc.getAccount().getPassword());
            vo.setItemId(item.getItemId());
            vo.setItemName(item.getName());
            vo.setItemCount(count);
            vo.setServerState(0);
            vo.setRegDate(new Date());
            vo.setItemEnchant(item.getEnchantLevel());
            vo.setItemAttr(item.getAttrEnchantLevel());
            vo.setItemBless(item.getBless());
            vo.setServerState(CodeConfig.NEXT_REQ_SERVER_STATE);
            vo.setItemObjId(item.getId());

            CharacterNextReqTable.getInstance().insert(vo);

            pc.sendPackets(String.format("[보상저장] : %s가 저장되었습니다", item.getName() + "(" + count + ")"));
            pc.getInventory().removeItem(item, count);

            L1LogUtils.debugLog(String.format("[보상저장] : [%s] 아이템 : %s", pc.getName(), item.getViewName() + "(" + count + ")"));
        } else {
            pc.sendPackets(String.format("[보상저장] : %s는 다음차 보상 아이템이 아닙니다", item.getViewName2()));
        }
    }

    private static L1ItemInstance findById(List<L1ItemInstance> itemList, int id) {
        for (L1ItemInstance item : itemList) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    public static void nextReq(L1PcInstance pc, StringTokenizer st) {
        try {
            String passwd = st.nextToken();

            List<L1ItemInstance> itemList = CharacterNextReqTable.getInstance().getNextReqItemsByPassword(passwd);

            if (itemList.isEmpty()) {
                pc.sendPackets("지급될 아이템이 없습니다");
                return;
            }

            S_InventoryInfo packet = new S_InventoryInfo(pc);

            packet.setMessenger(new InventoryInfoMessengerAdapter() {
                @Override
                public void action(int handleId, int size, C_ShopAndWarehouse packet) {
                    step2(size, packet);
                }

                public void step2(int size, C_ShopAndWarehouse packet) {
                    for (int i = 0; i < size; i++) {
                        int objectId = packet.readD();
                        int count = packet.readD();

                        L1ItemInstance item = findById(itemList, objectId);
                        L1ItemInstance returnItem;

                        if (item == null) {
                            pc.sendPackets("오류가 발생하였습니다");
                            return;
                        }

                        if (count > item.getCount() || count < 0) {
                            return;
                        }

                        int storeCount;

                        CharacterNextReturn characterNextReturn = CharacterNextReqTable.getInstance().getReturnItem(item.getItemId(), item.getEnchantLevel(), item.getBless());

                        if (characterNextReturn != null) {
                            returnItem = ItemTable.getInstance().createItem(characterNextReturn.getReturnItemId());
                            returnItem.setCount(characterNextReturn.getReturnItemCount());
                            returnItem.setIdentified(true);
                            returnItem.setBless(characterNextReturn.getReturnItemBless());
                            returnItem.setEnchantLevel(characterNextReturn.getReturnItemEnchant());

                            storeCount = returnItem.getCount();
                        } else {
                            returnItem = item;
                            storeCount = count;
                        }

                        pc.sendPackets(String.format("[전차보상] : %s가 지급 되었습니다", returnItem.getName() + "(" + storeCount + ")"));
                        pc.getInventory().storeItem(returnItem);

                        if (count == item.getCount()) {
                            CharacterNextReqTable.getInstance().delete(objectId);
                        } else {
                            CharacterNextReqTable.getInstance().updateCount(objectId, item.getCount() - count);
                        }

                        L1LogUtils.debugLog("[전차보상] : [" + pc.getName() + "] 아이템 : " + item.getViewName2());
                    }
                }
            });

            packet.build(itemList);

            pc.sendPackets("보상 아이템을 선택하세요");
            pc.sendPackets(packet);
        } catch (Exception e) {
            pc.sendPackets(".전차보상 아이디,패스워드");
        }
    }

    public static void saveCommand(L1PcInstance pc) {
        try {
            if (!CodeConfig.NEXT_REQ_START) {
                pc.sendPackets("보상이벤트가 시작되지 않았습니다");
                return;
            }

            List<L1ItemInstance> returnAbleItems = new ArrayList<>();

            for (L1ItemInstance item : pc.getInventory().getItems()) {
                CharacterNextReturn check = CharacterNextReqTable.getInstance().getReturnItem(item.getItemId(), item.getEnchantLevel(), item.getBless());
                if (check != null) {
                    returnAbleItems.add(item);
                }
            }

            if (returnAbleItems.isEmpty()) {
                pc.sendPackets("다음차 보상 가능한 아이템을 소지하고 있지 않습니다");
                return;
            }

            S_InventoryInfo packet = new S_InventoryInfo(pc);
            packet.setMessenger(new SaveNextReqSelectedAction(pc));
            packet.build(returnAbleItems);
            pc.sendPackets("다음차 보상 아이템을 선택하세요");
            pc.sendPackets(packet);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".보상저장"));
        }
    }

    public static class SaveNextReqSelectedAction extends InventoryInfoMessengerAdapter {
        private final L1PcInstance pc;

        public SaveNextReqSelectedAction(L1PcInstance pc) {
            this.pc = pc;
        }

        @Override
        public void action(int handleId, int size, C_ShopAndWarehouse packet) {
            step2(size, packet);
        }

        public void step2(int size, C_ShopAndWarehouse packet) {
            for (int i = 0; i < size; i++) {
                int objectId = packet.readD();
                int count = packet.readD();

                L1ItemInstance item = getPc().getInventory().getItem(objectId);

                CharacterNextReturnUtils.saveNextReturn(pc, item, count);
            }
        }

        @Override
        public String key() {
            return "SaveNextReqSelectedAction";
        }
    }
}
