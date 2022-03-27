package ks.commands.gm.command.executor;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Inventory;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1CreateItem implements L1CommandExecutor {
    private static final Logger logger = LogManager.getLogger(L1CreateItem.class);

    public static L1CommandExecutor getInstance() {
        return new L1CreateItem();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);

            String nameId = st.nextToken();

            int count = 1;

            if (st.hasMoreTokens()) {
                count = Integer.parseInt(st.nextToken());
            }

            int enchant = 0;

            if (st.hasMoreTokens()) {
                enchant = Integer.parseInt(st.nextToken());
            }

            int bless = 1;

            if (st.hasMoreTokens()) {
                bless = Integer.parseInt(st.nextToken());
            }

            int attrEnchant = 0;

            if (st.hasMoreTokens()) {
                attrEnchant = Integer.parseInt(st.nextToken());
            }

            int itemId;

            try {
                itemId = Integer.parseInt(nameId);
            } catch (NumberFormatException e) {
                itemId = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameId);

                if (itemId == 0) {
                    pc.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않습니다. "));
                    return;
                }
            }

            L1Item temp = ItemTable.getInstance().getTemplate(itemId);

            if (temp != null) {
                if (temp.isStackable()) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
                    item.setEnchantLevel(0);
                    item.setCount(count);
                    item.setBless(bless);
                    item.setIdentified(true);

                    if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                        pc.getInventory().storeItem(item);
                        pc.sendPackets(new S_ServerMessage(403, item.getViewName2() + "(ID:" + itemId + ")"));

                        logger.info("운영자 아이템 생성 : " + item.getViewName2() + "(ID:" + itemId + ")");
                    }
                } else {
                    int createCount;

                    L1ItemInstance item = null;

                    for (createCount = 0; createCount < count; createCount++) {
                        item = ItemTable.getInstance().createItem(itemId);
                        item.setEnchantLevel(enchant);
                        item.setAttrEnchantLevel(attrEnchant);
                        item.setBless(bless);
                        item.setIdentified(true);

                        if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                            pc.getInventory().storeItem(item);
                        } else {
                            break;
                        }
                    }

                    if (createCount > 0) {
                        logger.info("운영자 아이템 생성 : " + item.getViewName2() + "(ID:" + itemId + ")");
                        pc.sendPackets(new S_ServerMessage(403, item.getViewName2() + "(ID:" + itemId + ")"));
                    }
                }
            } else {
                pc.sendPackets(new S_SystemMessage("지정 ID의 아이템은 존재하지 않습니다"));
            }
        } catch (Exception e) {
            logger.error("오류", e);
            pc.sendPackets(new S_SystemMessage(".아이템(이름/ID) [갯수] [인챈트수] [축복] [속성] 라고 입력해 주세요. "));
        }
    }
}
