package ks.commands.gm.command.executor;

import ks.core.datatables.NpcSpawnTable;
import ks.core.datatables.SpawnTable;
import ks.model.L1Spawn;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class L1ToSpawn implements L1CommandExecutor {
    private static final Map<Integer, Integer> _spawnId = new HashMap<>();
    @SuppressWarnings("unused")
    private static final Logger _log = LogManager.getLogger(L1ToSpawn.class.getName());

    private L1ToSpawn() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ToSpawn();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            if (!_spawnId.containsKey(pc.getId())) {
                _spawnId.put(pc.getId(), 0);
            }
            int id = _spawnId.get(pc.getId());
            if (arg.isEmpty() || arg.equals("+")) {
                id++;
            } else if (arg.equals("-")) {
                id--;
            } else {
                StringTokenizer st = new StringTokenizer(arg);
                id = Integer.parseInt(st.nextToken());
            }
            L1Spawn spawn = NpcSpawnTable.getInstance().getTemplate(id);
            if (spawn == null) {
                spawn = SpawnTable.getInstance().getTemplate(id);
            }
            if (spawn != null) {
                L1Teleport.teleport(pc, spawn.getLocX(), spawn.getLocY(), spawn.getMapId(), 5, false);
                pc.sendPackets(new S_SystemMessage("spawnid(" + id + ")의 원래로 납니다"));
            } else {
                pc.sendPackets(new S_SystemMessage("spawnid(" + id + ")(은)는 발견되지 않습니다"));
            }
            _spawnId.put(pc.getId(), id);
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [스폰아이디] [+,-]"));
        }
    }
}
