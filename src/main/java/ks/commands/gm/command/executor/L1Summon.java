package ks.commands.gm.command.executor;

import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

public class L1Summon implements L1CommandExecutor {
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger();

    private L1Summon() {
    }

    public static L1Summon getInstance() {
        return new L1Summon();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String nameId = tok.nextToken();

            int npcId;

            try {
                npcId = Integer.parseInt(nameId);
            } catch (NumberFormatException e) {
                npcId = NpcTable.getInstance().findNpcIdByNameWithoutSpace(nameId);
                if (npcId == 0) {
                    pc.sendPackets(new S_SystemMessage("해당 NPC가 발견되지 않습니다. "));
                    return;
                }
            }
            int count = 1;
            if (tok.hasMoreTokens()) {
                count = Integer.parseInt(tok.nextToken());
            }
            L1Npc npc = NpcTable.getInstance().getTemplate(npcId);

            for (int i = 0; i < count; i++) {
                L1SummonInstance summonInst = new L1SummonInstance(npc, pc);
                summonInst.setPetCost(0);
            }

            nameId = NpcTable.getInstance().getTemplate(npcId).getName();
            pc.sendPackets(new S_SystemMessage(nameId + "(ID:" + npcId + ") (" + count + ")를 소환했습니다. "));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [npcid or name] [서먼수] 라고 입력해 주세요. "));
        }
    }
}
