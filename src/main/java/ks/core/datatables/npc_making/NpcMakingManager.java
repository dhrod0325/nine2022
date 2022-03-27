package ks.core.datatables.npc_making;

import ks.constants.L1DataMapKey;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Inventory;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CloseList;
import ks.packets.serverpackets.S_ShowCCHtml;

import java.util.ArrayList;
import java.util.List;

public class NpcMakingManager {
    private static final NpcMakingManager instance = new NpcMakingManager();

    public static NpcMakingManager getInstance() {
        return instance;
    }

    public boolean npcAction(L1PcInstance pc, L1NpcInstance npc, String action) {
        if (action.equalsIgnoreCase("cc_craft_back")) {
            NpcMakingTable.getInstance().showHtml(pc, npc);
        } else if (action.equalsIgnoreCase("cc_craftstart")) {
            int idx = Integer.parseInt(pc.getDataMap().get(L1DataMapKey.CRAFT_IDX));

            NpcMaking making = (NpcMaking) pc.getPagination().getSearchList().get(idx);

            boolean check = true;

            L1PcInventory inv = pc.getInventory();

            for (NpcMakingMaterial m : making.getMakingMaterialList()) {
                int itemId = m.getMakingMaterialItemId();
                int enchant = m.getMakingMaterialEnchant();

                L1ItemInstance o = ItemTable.getInstance().createItem(itemId);

                int invMaterialCount;

                List<L1ItemInstance> list = inv.findItemsIdNotEquipped(itemId, enchant, 1);

                if (list.isEmpty()) {
                    invMaterialCount = 0;
                } else if (o.isStackable()) {
                    invMaterialCount = list.get(0).getCount();
                } else {
                    invMaterialCount = list.size();
                }

                if (invMaterialCount == 0) {
                    check = false;
                    pc.sendPackets("수량 부족 : " + m.getPrintName());
                } else {
                    if (invMaterialCount < m.getMakingMaterialCount()) {
                        int cnt = m.getMakingMaterialCount() - invMaterialCount;
                        check = false;
                        pc.sendPackets("수량 부족 : " + m.getPrintName(false) + "(" + cnt + ")");
                    }
                }
            }

            if (check) {
                L1ItemInstance newItem = ItemTable.getInstance().createItem(making.getMakingItemId());
                newItem.setIdentified(true);
                newItem.setCount(making.getMakingCount());
                newItem.setBless(making.getMakingItemBless());
                newItem.setEnchantLevel(making.getMakingItemEnchant());

                if (pc.getInventory().checkAddItem(newItem, newItem.getCount()) != L1Inventory.OK) {
                    pc.sendPackets("소지하고 있는 아이템이 너무 많습니다");
                    return check;
                }

                for (NpcMakingMaterial m : making.getMakingMaterialList()) {
                    int oldItemId = m.getMakingMaterialItemId();
                    int oldEnchant = m.getMakingMaterialEnchant();
                    int oldItemCount = m.getMakingMaterialCount();

                    int deleteCount = 0;

                    List<L1ItemInstance> list = pc.getInventory().getItems(oldItemId, oldItemCount, oldEnchant);

                    for (L1ItemInstance is : new ArrayList<>(list)) {
                        int count = pc.getInventory().removeItem(is, m.getMakingMaterialCount());
                        deleteCount += count;
                    }

                    if (deleteCount != oldItemCount) {
                        pc.sendPackets("오류가 발생했습니다 운영자에게 문의하세요");
                        return true;
                    }
                }

                pc.getInventory().storeItem(newItem);
                pc.sendPackets(newItem.getViewName() + "를 획득하였습니다");
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (action.equalsIgnoreCase("cc_craftprev") || action.equalsIgnoreCase("cc_craftnext")) {
            if ("cc_craftprev".equalsIgnoreCase(action)) {
                pc.getPagination().prev();
            } else if ("cc_craftnext".equalsIgnoreCase(action)) {
                pc.getPagination().next();
            }

            NpcMakingTable.getInstance().showHtml(pc, npc);

            return false;
        } else {
            int idx = Integer.parseInt(action.replace("cc_craft_", ""));

            pc.getDataMap().put(L1DataMapKey.CRAFT_IDX, idx + "");

            List<NpcMaking> makingList = NpcMakingTable.getInstance().getMakingList(pc, npc);

            NpcMaking making = makingList.get(idx);

            List<String> params = new ArrayList<>();

            params.add(making.getMakingItemName());

            for (NpcMakingMaterial m : making.getMakingMaterialList()) {
                params.add(m.getPrintName());
            }

            pc.sendPackets(new S_ShowCCHtml(npc.getId(), "cc_making99", params));
        }

        return false;
    }

    public boolean npcTalk(L1PcInstance pc, L1NpcInstance npc) {
        List<NpcMaking> making = NpcMakingTable.getInstance().findByNpcId(npc.getNpcId());

        if (!making.isEmpty()) {
            pc.getDataMap().put(L1DataMapKey.CRAFT_IDX, "0");
            pc.getPagination().setCurrentPageNo(1);
            NpcMakingTable.getInstance().showHtml(pc, npc);

            return true;
        }

        return false;
    }
}
