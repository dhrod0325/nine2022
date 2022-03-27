package ks.commands.gm.command.executor;

import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1InvGfxId implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1InvGfxId.class.getName());

    private L1InvGfxId() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1InvGfxId();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int gfxid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            for (int i = 0; i < count; i++) {
                L1ItemInstance item = ItemTable.getInstance().createItem(40005);
                item.getItem().setGfxId(gfxid + i);
                item.getItem().setName(String.valueOf(gfxid + i));
                item.getItem().setNameId(String.valueOf(gfxid + i));
                pc.getInventory().storeItem(item);
            }
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [id] [출현시키는 수]로 입력해 주세요. "));
        }
    }
}
