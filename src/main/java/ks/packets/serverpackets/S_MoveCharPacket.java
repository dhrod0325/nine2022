package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

import java.util.List;

public class S_MoveCharPacket extends ServerBasePacket {
    public S_MoveCharPacket(L1Character cha) {
        build(cha);
    }

    public void build(L1Character cha) {
        int x = cha.getX();
        int y = cha.getY();

        build(cha, x, y, cha.getHeading());
    }

    public void build(L1Character cha, int x, int y, int h) {
        switch (cha.getHeading()) {
            case 1:
                x--;
                y++;
                break;
            case 2:
                x--;
                break;
            case 3:
                x--;
                y--;
                break;
            case 4:
                y--;
                break;
            case 5:
                x++;
                y--;
                break;
            case 6:
                x++;
                break;
            case 7:
                x++;
                y++;
                break;
            case 0:
                y++;
                break;
        }

        writeC(L1Opcodes.S_OPCODE_MOVEOBJECT);
        writeD(cha.getId());
        writeH(x);
        writeH(y);
        writeC(h);

        writeC(129);
        writeD(0);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.getPierceCheck().onMove();

            List<L1Object> list = L1World.getInstance().getVisibleObjects(pc, -1);

            for (L1Object o : list) {
                if (o instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) o;
                    npc.onVisiblePcMoved(pc);
                    //logger.debug("npc : {}, target :{}", npc.getName(), npc.getTarget());
                }
            }
        }
    }
}
