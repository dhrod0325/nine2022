package ks.commands.gm.command.executor;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.common.SqlUtils;

import java.util.List;
import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1LevelPresent implements L1CommandExecutor {
    private L1LevelPresent() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1LevelPresent();
    }

    public static void present(int minlvl, int maxlvl, int itemid, int enchant, int count) throws Exception {
        L1Item temp = ItemTable.getInstance().getTemplate(itemid);

        if (temp == null) {
            return;
        }

        List<String> accountList = SqlUtils.queryForList("SELECT distinct(account_name) as account_name FROM characters WHERE level between ? and ?", String.class, minlvl, maxlvl);

        present(accountList, itemid, enchant, count);
    }

    private static void present(List<String> accountList, int itemid, int enchant, int count) throws Exception {
        L1Item temp = ItemTable.getInstance().getTemplate(itemid);

        if (temp == null) {
            throw new Exception("존재하지 않는 아이템 ID");
        }

        for (String account : accountList) {
            if (temp.isStackable()) {
                L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                item.setEnchantLevel(enchant);
                item.setCount(count);

                SqlUtils.update("INSERT INTO character_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?",
                        item.getId(),
                        account,
                        item.getItemId(),
                        item.getName(),
                        item.getCount(),
                        item.getEnchantLevel(),
                        item.isIdentified() ? 1 : 0,
                        item.getDurability(),
                        item.getChargeCount(),
                        item.getRemainingTime()
                );
            } else {
                for (int createCount = 0; createCount < count; createCount++) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                    item.setEnchantLevel(enchant);

                    SqlUtils.update("INSERT INTO character_warehouse SET id = ?, account_name = ?, item_id = ?, item_name = ?, count = ?, is_equipped=0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?",
                            item.getId(),
                            account,
                            item.getItemId(),
                            item.getName(),
                            item.getCount(),
                            item.getEnchantLevel(),
                            item.isIdentified() ? 1 : 0,
                            item.getDurability(),
                            item.getChargeCount(),
                            item.getRemainingTime()
                    );
                }
            }
        }
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int minlvl = Integer.parseInt(st.nextToken(), 10);
            int maxlvl = Integer.parseInt(st.nextToken(), 10);
            int itemid = Integer.parseInt(st.nextToken(), 10);
            int enchant = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            L1Item temp = ItemTable.getInstance().getTemplate(itemid);

            if (temp == null) {
                pc.sendPackets(new S_SystemMessage("존재하지 않는 아이템 ID입니다. "));
                return;
            }

            present(minlvl, maxlvl, itemid, enchant, count);
            pc.sendPackets(new S_SystemMessage(temp.getName() + "를 " + count + " 개 선물 했습니다. (Lv" + minlvl + "~" + maxlvl + ")"));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".렙선물 [최저레벨] [최고레벨] [아이템ID] [인챈트] [갯수]로 입력해 주세요. "));
        }
    }
}
