package ks.commands.gm.command.executor;

import ks.constants.L1ItemId;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Adena implements L1CommandExecutor {
    private L1Adena() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Adena();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(arg);
            int count = Integer.parseInt(stringtokenizer.nextToken());

            L1ItemInstance adena = pc.getInventory().storeItem(
                    L1ItemId.ADENA, count);
            if (adena != null) {
                pc.sendPackets(new S_SystemMessage((new StringBuilder())
                        .append(count).append("아데나를 생성했습니다. ").toString()));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(
                    ".아데나 [액수]로 입력해 주세요. ").toString()));
        }
    }
}
