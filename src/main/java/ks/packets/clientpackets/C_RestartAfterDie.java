package ks.packets.clientpackets;

import ks.core.datatables.getback.GetBackTable;
import ks.core.network.L1Client;
import ks.model.Broadcaster;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;

public class C_RestartAfterDie extends ClientBasePacket {
    public C_RestartAfterDie(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (!pc.isDead()) {
            return;
        }

        int[] loc;

        if (pc.getHellTime() > 0) {
            loc = new int[3];
            loc[1] = 32777;
            loc[2] = 666;
        } else {
            loc = GetBackTable.getInstance().getBackLocation(pc);
        }

        pc.getNearObjects().removeAllKnownObjects();
        Broadcaster.broadcastPacket(pc, new S_RemoveObject(pc));

        pc.setCurrentHp(pc.getLevel());
        pc.setFood(39);
        pc.setDead(false);
        pc.setActionStatus(0);
        L1World.getInstance().moveVisibleObject(pc, loc[2]);
        pc.setX(loc[0]);
        pc.setY(loc[1]);
        pc.setMap((short) loc[2]);
        pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap().isUnderwater()));
        Broadcaster.broadcastPacket(pc, new S_OtherCharPacks(pc));
        pc.sendPackets(new S_OwnCharPack(pc));
        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));

        pc.getRankBuff().reload();
    }
}
