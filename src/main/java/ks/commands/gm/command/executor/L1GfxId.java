package ks.commands.gm.command.executor;

import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Npc;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1InstanceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1GfxId implements L1CommandExecutor {
    private static final Logger _log = LogManager.getLogger(L1GfxId.class.getName());

    public static L1CommandExecutor getInstance() {
        return new L1GfxId();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int gfxid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            int pcX = pc.getX();
            int pcY = pc.getY();
            for (int i = 0; i < count; i++) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(45001);
                if (l1npc != null) {
                    L1NpcInstance npc = L1InstanceFactory.createInstance(l1npc);

                    npc.setId(ObjectIdFactory.getInstance().nextId());
                    npc.getGfxId().setGfxId(gfxid + i);
                    npc.getGfxId().setTempCharGfx(0);
                    npc.setNameId("" + (gfxid + i) + "");
                    npc.setMap(pc.getMapId());
                    int e = i % 5;
                    if (e == 0 && i > 0) {
                        pcX -= 2;
                        pcY += 2;
                    }
                    npc.setX(pcX + e * 2);
                    npc.setY(pcY + e * 2);
                    npc.setHomeX(npc.getX());
                    npc.setHomeY(npc.getY());
                    npc.setHeading(4);

                    L1World.getInstance().storeObject(npc);
                    L1World.getInstance().addVisibleObject(npc);
                }
            }
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName
                    + " [id] [출현시키는 수]로 입력해 주세요. "));
        }
    }
}
