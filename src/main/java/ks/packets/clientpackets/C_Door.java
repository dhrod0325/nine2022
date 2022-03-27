package ks.packets.clientpackets;

import ks.core.datatables.HouseTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1House;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1DoorInstance;
import ks.model.pc.L1PcInstance;

public class C_Door extends ClientBasePacket {
    public C_Door(byte[] data, L1Client client) {
        super(data);

        int locX = readH();
        int locY = readH();
        int objectId = readD();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null)
            return;

        L1Object o = L1World.getInstance().findObject(objectId);

        if (!(o instanceof L1DoorInstance)) {
            return;
        }

        try {
            L1DoorInstance door = (L1DoorInstance) L1World.getInstance().findObject(objectId);

            if (locX != door.getX() || locY != door.getY()) {
                return;
            }

            if (locX > pc.getX() + 1 || locX < pc.getX() - 1 || locY > pc.getY() + 1 || locY < pc.getY() - 1) {
                return;
            }

            if (door.getDoorId() == 7200 || door.getDoorId() == 7300
                    || door.getDoorId() == 7510 || door.getDoorId() == 7511
                    || door.getDoorId() == 7520 || door.getDoorId() == 7530
                    || door.getDoorId() == 7540 || door.getDoorId() == 7550) {
                return;
            }

            if ((door.getDoorId() >= 5000 && door.getDoorId() <= 5009)) {
                return;
            }

            if (!isExistKeeper(pc, door.getKeeperId())) {
                if (door.getDoorId() >= 9049 && door.getDoorId() <= 9060 || door.getDoorId() == 121) {
                    if (!pc.getInventory().checkItem(40163)) {
                        return;
                    }

                    pc.getInventory().consumeItem(40163, 1);
                }

                if (door.getDoorId() == 125) {
                    if (!pc.getInventory().checkItem(40313)) {
                        return;
                    }

                    pc.getInventory().consumeItem(40313, 1);
                }

                if (door.isOpen()) {
                    door.close(pc);
                } else if (door.isClose()) {
                    door.open(pc);
                }
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private boolean isExistKeeper(L1PcInstance pc, int keeperId) {
        if (keeperId == 0) {
            return false;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                return keeperId != house.getKeeperId();
            }
        }

        return true;
    }
}
