package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_OwnCharPack;

public class C_Ship extends ClientBasePacket {
    public C_Ship(byte[] data, L1Client client) {
        super(data);

        int shipMapId = readH();
        int x = readH();
        int y = readH();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        int mapId = pc.getMapId();

        switch (mapId) {
            case 5:
                pc.getInventory().consumeItem(40299, 1);
                break;
            case 6:
                pc.getInventory().consumeItem(40298, 1);
                break;
            case 83:
                pc.getInventory().consumeItem(40300, 1);
                break;
            case 84:
                pc.getInventory().consumeItem(40301, 1);
                break;
            case 446:
                pc.getInventory().consumeItem(40303, 1);
                break;
            case 447:
                pc.getInventory().consumeItem(40302, 1);
                break;
            default:
                break;
        }

        pc.sendPackets(new S_OwnCharPack(pc));
        L1Teleport.teleport(pc, x, y, (short) shipMapId, 0, false);
    }
}
