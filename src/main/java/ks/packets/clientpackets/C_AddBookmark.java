package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1CastleLocation;
import ks.model.L1HouseLocation;
import ks.model.bookMark.L1BookMarkTable;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class C_AddBookmark extends ClientBasePacket {
    public C_AddBookmark(byte[] data, L1Client client) {
        super(data);

        String name = readS();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (pc.isGm()) {
            L1BookMarkTable.addBookmark(pc, name);
            return;
        }

        if (pc.getMap().isMarkAble()) {
            if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
                pc.sendPackets(new S_ServerMessage(214));
                return;
            }

            if (L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
                pc.sendPackets(new S_ServerMessage(214));
                return;
            }

            if (((pc.getX() >= 33514 && pc.getX() <= 33809) && (pc.getY() >= 32216 && pc.getY() <= 32457) && pc.getMapId() == 4)
                    || ((pc.getX() >= 34280 && pc.getX() <= 34287) && (pc.getY() >= 33103 && pc.getY() <= 33492) && pc.getMapId() == 4)
                    || ((pc.getX() >= 33464 && pc.getX() <= 33532) && (pc.getY() >= 32839 && pc.getY() <= 32878) && pc.getMapId() == 4)
                    || ((pc.getX() >= 33449 && pc.getX() <= 33473) && (pc.getY() >= 32324 && pc.getY() <= 32347) && pc.getMapId() == 4)
                    || ((pc.getX() >= 33470 && pc.getX() <= 33530) && (pc.getY() >= 33177 && pc.getY() <= 33231) && pc.getMapId() == 4)
                    || ((pc.getX() >= 32707 && pc.getX() <= 32826) && (pc.getY() >= 33117 && pc.getY() <= 33229) && pc.getMapId() == 4)
                    || ((pc.getX() >= 24190 && pc.getX() <= 34281) && (pc.getY() >= 33240 && pc.getY() <= 33452) && pc.getMapId() == 4)
                    || (pc.getMapId() == 430)
            ) {
                pc.sendPackets(new S_ServerMessage(214));
            } else {
                L1BookMarkTable.addBookmark(pc, name);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(214));
        }
    }
}
