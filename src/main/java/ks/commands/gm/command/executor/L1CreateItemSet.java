package ks.commands.gm.command.executor;

import ks.commands.gm.GMCommandsUtils;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.L1ItemSetItem;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.List;
import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1CreateItemSet implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1CreateItemSet();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            String name = new StringTokenizer(arg).nextToken();
            List<L1ItemSetItem> list = GMCommandsUtils.ITEM_SETS.get(name);

            if (list == null) {
                pc.sendPackets(new S_SystemMessage(name + "은 없습니다."));
                return;
            }

            for (L1ItemSetItem item : list) {
                L1Item temp = ItemTable.getInstance().getTemplate(item.getId());
                if (!temp.isStackable() && 0 != item.getEnchant()) {
                    for (int i = 0; i < item.getAmount(); i++) {
                        L1ItemInstance inst = ItemTable.getInstance().createItem(item.getId());
                        inst.setEnchantLevel(item.getEnchant());
                        pc.getInventory().storeItem(inst);
                    }
                } else {
                    pc.getInventory().storeItem(item.getId(), item.getAmount());
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".세트아이템 세트명으로 입력해 주세요. "));
        }
    }
}
