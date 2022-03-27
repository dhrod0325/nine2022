package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;

public class C_CallPlayer extends ClientBasePacket {
    public C_CallPlayer(byte[] decrypt, L1Client client) {
        super(decrypt);
        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        if (!pc.isGm())
            return;

        String name = readS();
        if (name.isEmpty())
            return;

        L1PcInstance target = L1World.getInstance().getPlayer(name);
        if (target == null)
            return;

        L1Location loc = L1Location.randomLocation(target.getLocation(), 1, 2, false);
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), target.getMapId(), pc.getHeading(), false);
    }
}
