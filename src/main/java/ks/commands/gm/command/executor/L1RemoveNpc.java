package ks.commands.gm.command.executor;


import ks.core.datatables.NpcSpawnTable;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.npc.NpcDeleteScheduler;

import java.util.StringTokenizer;

public class L1RemoveNpc implements L1CommandExecutor {

    private L1RemoveNpc() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1RemoveNpc();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);

            int npcid = Integer.parseInt(tok.nextToken());
            int time;

            try {
                time = Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                time = 0;
            }

            for (L1Object obj : L1World.getInstance().getVisibleObjects(pc)) {
                if (obj instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) obj;

                    if (npc.getNpcId() == npcid) {
                        NpcSpawnTable.getInstance().removeSpawn(npc);
                        npc.setRespawn(false);
                        NpcDeleteScheduler.getInstance().addNpcDelete(npc, time * 60 * 1000);
//                        new L1NpcDeleteTimer(npc, time * 60 * 1000).begin();
                        pc.sendPackets(new S_SystemMessage(npc.getName() + "을(를) " + time + "분 뒤에 삭제 합니다."));
                    }
                }
            }

        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".삭제 [시간(분)] (시야에 있는 npc의 id를 입력하면 입력시간 뒤 삭제(DB에도 적용) 됩니다)"));
        }
    }
}
