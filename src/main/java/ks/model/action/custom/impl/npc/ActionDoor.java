package ks.model.action.custom.impl.npc;

import ks.core.datatables.DoorSpawnTable;
import ks.core.datatables.HouseTable;
import ks.model.L1Clan;
import ks.model.L1House;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1DoorInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public class ActionDoor extends L1AbstractNpcAction {
    public ActionDoor(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        openCloseDoor(pc, npc, action);
    }

    public void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcId() == keeperId) {
                    L1DoorInstance door1 = null;
                    L1DoorInstance door2 = null;
                    L1DoorInstance door3 = null;
                    L1DoorInstance door4 = null;

                    for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                        if (door.getKeeperId() == keeperId) {
                            if (door1 == null) {
                                door1 = door;
                                continue;
                            }
                            if (door2 == null) {
                                door2 = door;
                                continue;
                            }
                            if (door3 == null) {
                                door3 = door;
                                continue;
                            }

                            door4 = door;

                            break;
                        }
                    }

                    doorOpenOrClose(s, door1, door2);
                    doorOpenOrClose(s, door3, door4);
                }
            }
        }
    }

    private void doorOpenOrClose(String s, L1DoorInstance door1, L1DoorInstance door2) {
        if (door1 != null) {
            if (s.equalsIgnoreCase("open")) {
                door1.open(pc);
            } else if (s.equalsIgnoreCase("close")) {
                door1.close(pc);
            }
        }
        if (door2 != null) {
            if (s.equalsIgnoreCase("open")) {
                door2.open(pc);
            } else if (s.equalsIgnoreCase("close")) {
                door2.close(pc);
            }
        }
    }
}
