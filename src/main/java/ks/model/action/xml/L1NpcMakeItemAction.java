package ks.model.action.xml;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.L1Object;
import ks.model.L1ObjectAmount;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_HowManyMake;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.IterableElementList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class L1NpcMakeItemAction extends L1NpcXmlAction {
    private final List<L1ObjectAmount<Integer>> materials = new ArrayList<>();

    private final List<L1ObjectAmount<Integer>> items = new ArrayList<>();

    private final boolean isAmountInputable;

    private final L1NpcAction actionOnSucceed;

    private final L1NpcAction actionOnFail;

    public L1NpcMakeItemAction(Element element) {
        super(element);

        isAmountInputable = L1NpcXmlParser.getBoolAttribute(element, "AmountInputable", true);

        NodeList list = element.getChildNodes();


        for (Element elem : new IterableElementList(list)) {
            if (elem.getNodeName().equalsIgnoreCase("Material")) {
                int id = Integer.parseInt(elem.getAttribute("ItemId"));
                int amount = Integer.parseInt(elem.getAttribute("Amount"));
                materials.add(new L1ObjectAmount<>(id, amount));
                continue;
            }

            if (elem.getNodeName().equalsIgnoreCase("Item")) {
                int id = Integer.parseInt(elem.getAttribute("ItemId"));
                int amount = Integer.parseInt(elem.getAttribute("Amount"));
                items.add(new L1ObjectAmount<>(id, amount));
            }
        }

        if (items.isEmpty() || materials.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Element elem = L1NpcXmlParser.getFirstChildElementByTagName(element, "Succeed");
        actionOnSucceed = elem == null ? null : new L1NpcListedAction(elem);
        elem = L1NpcXmlParser.getFirstChildElementByTagName(element, "Fail");
        actionOnFail = elem == null ? null : new L1NpcListedAction(elem);

    }

    private boolean makeItems(L1PcInstance pc, String npcName, int amount) {
        // 제작버그 관련 추가
        if (amount <= 0 || amount > 9999) {
            return false;
        }

        boolean isEnoughMaterials = true;

        for (L1ObjectAmount<Integer> material : materials) {
            if (!pc.getInventory().checkItemNotEquipped(material.getObject(), material.getAmount() * amount)) {
                L1Item temp = ItemTable.getInstance().getTemplate(material.getObject());
                pc.sendPackets(new S_ServerMessage(337, temp.getName() + "(" + ((material.getAmount() * amount) - pc.getInventory().countItems(temp.getItemId())) + ")"));
                isEnoughMaterials = false;
            }
        }

        if (!isEnoughMaterials) {
            return false;
        }

        int countToCreate = 0;
        int weight = 0;

        for (L1ObjectAmount<Integer> makingItem : items) {
            L1Item temp = ItemTable.getInstance().getTemplate(makingItem.getObject());

            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(makingItem.getObject())) {
                    countToCreate += 1;
                }
            } else {
                countToCreate += makingItem.getAmount() * amount;
            }

            weight += temp.getWeight() * (makingItem.getAmount() * amount) / 1000;

            // 제작 버그 관련 추가
            if (countToCreate < 0 || countToCreate > 9999) {
                return false;
            }
        }

        if (pc.getInventory().getSize() + countToCreate > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }

        if (pc.getMaxWeight() < pc.getInventory().getWeight() + weight) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }

        for (L1ObjectAmount<Integer> material : materials) {
            pc.getInventory().consumeItem(material.getObject(), material.getAmount() * amount);
        }

        for (L1ObjectAmount<Integer> makingItem : items) {
            if (npcName.equals("베테르랑")) {
                L1Item VeteranItemCheck = ItemTable.getInstance().getTemplate(makingItem.getObject());
                int veteranitem = VeteranItemCheck.getItemId();
                int PcType = pc.getType();

                if (veteranitem == 303) {
                    if (PcType == 5 || PcType == 6) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 304) {
                    if (PcType == 3 || PcType == 4 || PcType == 6) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 305) {
                    if (PcType == 2 || PcType == 3 || PcType == 4 || PcType == 6) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 306) {
                    if (PcType == 0 || PcType == 1 || PcType == 3 || PcType == 5) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 307) {
                    if (PcType == 3 || PcType == 6) {
                    } else {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 308) {
                    if (PcType != 4) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 309) {
                    if (PcType != 5) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 310) {
                    if (PcType != 6) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 900031) {
                    if (PcType != 1) {
                        pc.sendPackets(new S_ServerMessage(264));

                        return false;
                    }
                } else if (veteranitem == 900032) {
                    if (PcType == 1) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 900033) {
                    if (PcType == 3 || PcType == 6) {
                    } else {
                        pc.sendPackets(new S_ServerMessage(264));

                        return false;
                    }
                } else if (veteranitem == 900034) {
                    if (PcType == 1 || PcType == 5) {
                    } else {
                        pc.sendPackets(new S_ServerMessage(264));

                        return false;
                    }
                } else if (veteranitem == 900037) {
                    if (PcType == 2 || PcType == 3 || PcType == 4 || PcType == 6) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                } else if (veteranitem == 900038) {
                    if (PcType == 0 || PcType == 1 || PcType == 3 || PcType == 5) {
                        pc.sendPackets(new S_ServerMessage(264));
                        return false;
                    }
                }
            }

            L1ItemInstance item = pc.getInventory().storeItem(makingItem.getObject(), makingItem.getAmount() * amount);

            if (item != null) {
                String itemName = ItemTable.getInstance().getTemplate(makingItem.getObject()).getName();

                if (makingItem.getAmount() * amount > 1) {
                    itemName = itemName + " (" + makingItem.getAmount() * amount + ")";
                }

                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
            }
        }

        return true;
    }

    private int countNumOfMaterials(L1PcInventory inv) {
        int count = Integer.MAX_VALUE;

        for (L1ObjectAmount<Integer> material : materials) {
            int numOfSet = inv.countItems(material.getObject()) / material.getAmount();
            count = Math.min(count, numOfSet);
        }

        return count;
    }

    @Override
    public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj, byte[] args) {
        int numOfMaterials = countNumOfMaterials(pc.getInventory());

        if (1 < numOfMaterials && isAmountInputable) {
            pc.sendPackets(new S_HowManyMake(obj.getId(), numOfMaterials, actionName));
            return null;
        }

        return executeWithAmount(actionName, pc, obj, 1);
    }

    @Override
    public L1NpcHtml executeWithAmount(String actionName, L1PcInstance pc, L1Object obj, int amount) {
        L1NpcInstance npc = (L1NpcInstance) obj;
        L1NpcHtml result = null;

        if (makeItems(pc, npc.getTemplate().getName(), amount)) {
            if (actionOnSucceed != null) {
                result = actionOnSucceed.execute(actionName, pc, obj, new byte[0]);
            }
        } else {
            if (actionOnFail != null) {
                result = actionOnFail.execute(actionName, pc, obj, new byte[0]);
            }
        }

        result = result == null ? L1NpcHtml.HTML_CLOSE : result;

        return result;
    }
}
