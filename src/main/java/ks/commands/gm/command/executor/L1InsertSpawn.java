package ks.commands.gm.command.executor;

import ks.core.datatables.NpcSpawnTable;
import ks.core.datatables.SpawnTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1SpawnUtils;

import java.util.StringTokenizer;

public class L1InsertSpawn implements L1CommandExecutor {
    private L1InsertSpawn() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1InsertSpawn();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        String msg = null;

        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String type = tok.nextToken();

            int npcId = Integer.parseInt(tok.nextToken().trim());

            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);

            if (npc == null) {
                msg = "해당 NPC가 발견되지 않습니다. ";
                return;
            }

            if (type.equalsIgnoreCase("m")) {
                if (!(npc instanceof L1MonsterInstance)) {
                    msg = "지정한 NPC는 몬스터가 아닙니다. ";
                    return;
                }

                SpawnTable.storeSpawn(pc, npc.getTemplate());
            } else if (type.equalsIgnoreCase("n")) {
                NpcSpawnTable.getInstance().storeSpawn(pc, npc.getTemplate());
            }

            L1SpawnUtils.spawn(pc, npcId, 0, 0);

            msg = npc.getName() + " (" + npcId + ") " + "를 추가했습니다. ";
        } catch (Exception e) {
            msg = cmdName + " [m/n] [NPCID] 라고 입력해 주세요. ";
        } finally {
            if (msg != null) {
                pc.sendPackets(new S_SystemMessage(msg));
            }
        }
    }
}
