package ks.commands.gm.command.executor;

import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1SpawnUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class L1SpawnCmd implements L1CommandExecutor {
    private static final Logger _log = LogManager.getLogger(L1SpawnCmd.class.getName());

    private L1SpawnCmd() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1SpawnCmd();
    }

    private void sendErrorMessage(L1PcInstance pc, String cmdName) {
        String errorMsg = cmdName + " npcid|name [수] [범위] 라고 입력해 주세요. ";
        pc.sendPackets(new S_SystemMessage(errorMsg));
    }

    private int parseNpcId(String nameId) {
        int npcid;
        try {
            npcid = Integer.parseInt(nameId);
        } catch (NumberFormatException e) {
            npcid = NpcTable.getInstance().findNpcIdByNameWithoutSpace(nameId);
        }
        return npcid;
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String nameId = tok.nextToken();

            int count = 1;

            if (tok.hasMoreTokens()) {
                count = Integer.parseInt(tok.nextToken());
            }

            int randomRange = 0;

            if (tok.hasMoreTokens()) {
                randomRange = Integer.parseInt(tok.nextToken(), 10);
            }

            int npcId = parseNpcId(nameId);

            L1Npc npc = NpcTable.getInstance().getTemplate(npcId);

            if (npc == null) {
                pc.sendPackets(new S_SystemMessage("해당 NPC가 발견되지 않습니다."));
                return;
            }
            
            for (int i = 0; i < count; i++) {
                L1SpawnUtils.spawn(pc, npcId, randomRange, 0);
            }

            String msg = String.format("%s(%d) (%d) 를 소환했습니다. (범위:%d)", npc.getName(), npcId, count, randomRange);
            pc.sendPackets(new S_SystemMessage(msg));
        } catch (NoSuchElementException | NumberFormatException e) {
            sendErrorMessage(pc, cmdName);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            pc.sendPackets(new S_SystemMessage(cmdName + " 내부 에러입니다."));
        }
    }
}