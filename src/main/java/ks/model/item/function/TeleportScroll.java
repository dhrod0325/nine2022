package ks.model.item.function;

import ks.core.datatables.getback.GetBackTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.bookMark.L1BookMark;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_Paralysis;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1TeleportUtils;

public class TeleportScroll extends L1ItemInstance {
    public TeleportScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(getId());

            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

            int itemId = useItem.getItemId();

            if (L1ItemDelay.hasItemDelay(pc, this)) {
                return;
            }

            pc.cancelAbsoluteBarrier();

            if (itemId == 140100 || itemId == 6000070 || itemId == 40100 || itemId == 40099 || itemId == 40086 || itemId == 40863) {
                순간(pc, useItem, packet);
            } else if (itemId == 240100) {
                L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), true);
                pc.getInventory().removeItem(useItem, 1);
            } else if (itemId == 40117) {
                은말(pc, useItem);
            } else if (itemId == 40081) {
                기란(pc, useItem);
            } else if (itemId == 40079 || itemId == 40095 || itemId == 40521) {
                귀환(pc, useItem);
            } else if (itemId == 40124) {
                혈귀(pc, useItem);
            }

            L1ItemDelay.onItemUse(pc, useItem);
        }
    }

    private void 순간(L1PcInstance pc, L1ItemInstance useItem, ClientBasePacket packet) {
        int itemId = useItem.getItemId();

        short bookmarkMapId = (short) packet.readH();
        int bookmarkX = 0;

        L1BookMark book = null;

        if (L1Opcodes.SERVER_VERSION == 3.1) {
            bookmarkX = packet.readD();
            book = pc.getBookMark().findById(bookmarkX);
        } else if (L1Opcodes.SERVER_VERSION == 3.8) {
            bookmarkX = packet.readH();
            int bookmarkY = packet.readH();
            book = pc.getBookMark().findByLocation(bookmarkX, bookmarkY, bookmarkMapId);
        }


        if (itemId == 140100 || itemId == 6000070) {
            if (book == null) {
                randomTeleport(pc, useItem);
                return;
            }
        }

        if (pc.getInventory().checkEquipped(20288)) {
            if (book == null) {
                return;
            }
        }

        if (bookmarkX != 0) {
            boolean 말섬2층 = pc.getMapId() == 2 && bookmarkMapId == 2;

            if (말섬2층) {
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                pc.sendPackets(new S_ServerMessage(276));
                return;
            }

            if (pc.isEscapable()) {
                teleport(pc, useItem, bookmarkMapId, book.getLocX(), book.getLocY());
            } else {
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                pc.sendPackets(new S_ServerMessage(79));
            }
        } else {
            randomTeleport(pc, useItem);
        }
    }

    private void randomTeleport(L1PcInstance pc, L1ItemInstance useItem) {
        if (pc.isTeleportAble() || L1TeleportUtils.omanAmuletTeleportAble(pc)) {
            L1Location newLocation = pc.getLocation().randomLocation(200, true);

            for (int i = 0; i < 10; i++) {
                if (newLocation.equals(pc.getLocation())) {
                    newLocation = pc.getLocation().randomLocation(200, true);
                } else {
                    break;
                }
            }

            int newX = newLocation.getX();
            int newY = newLocation.getY();
            short mapId = (short) newLocation.getMapId();

            teleport(pc, useItem, mapId, newX, newY);
        } else {
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            pc.sendPackets(new S_ServerMessage(276));
        }
    }

    private void 은말(L1PcInstance pc, L1ItemInstance useItem) {
        if (pc.isEscapable()) {
            L1TeleportUtils.teleportToSilverTown(pc);
            pc.getInventory().removeItem(useItem, 1);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }

    private void 기란(L1PcInstance pc, L1ItemInstance useItem) {
        if (pc.isEscapable()) {
            L1TeleportUtils.teleportToGiran(pc);
            pc.getInventory().removeItem(useItem, 1);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }

    private void 귀환(L1PcInstance pc, L1ItemInstance useItem) {
        if (pc.isEscapable()) {
            int[] backLocation = GetBackTable.getInstance().getBackLocation(pc);
            L1Location loc = new L1Location(backLocation[0], backLocation[1], (short) backLocation[2]);
            L1Map map = loc.getMap();

            L1Location loc2 = L1Location.randomLocation(backLocation[0], backLocation[1], map, (short) backLocation[2], 1, 5, false);
            L1Teleport.teleport(pc, loc2.getX(), loc2.getY(), (short) loc2.getMapId(), pc.getHeading(), true);
            pc.getInventory().removeItem(useItem, 1);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }

    private void 혈귀(L1PcInstance pc, L1ItemInstance useItem) {
        if (pc.isEscapable()) {
            int castleId = 0;
            int houseId = 0;

            if (pc.getClanId() != 0) { // 크란 소속
                L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                if (clan != null) {
                    castleId = clan.getCastleId();
                    houseId = clan.getHouseId();
                }
            }

            if (castleId != 0) { // 성주 크란원
                if (pc.isEscapable()) {
                    int[] loc = L1CastleLocation.getCastleLoc(castleId);

                    int locx = loc[0];
                    int locy = loc[1];
                    short mapid = (short) (loc[2]);
                    L1Teleport.teleport(pc, locx, locy, mapid, pc.getHeading(), true);
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
            } else if (houseId != 0) { // 아지트 소유 크란원
                if (pc.isEscapable()) {
                    int[] loc = L1HouseLocation.getHouseLoc(houseId);

                    int locx = loc[0];
                    int locy = loc[1];
                    short mapid = (short) (loc[2]);
                    L1Teleport.teleport(pc, locx, locy, mapid, pc.getHeading(), true);
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
            } else {
                int[] loc = GetBackTable.getInstance().getBackLocation(pc);
                L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], pc.getHeading(), true);
                pc.getInventory().removeItem(useItem, 1);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }

    private void teleport(L1PcInstance pc, L1ItemInstance useItem, short bookmarkMapId, int bookmark_x, int bookmark_y) {
        if (pc.isHuntMapAndNoHunt(bookmarkMapId)) {
            return;
        }

        if (useItem.getItemId() == 40086) {
            for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc)) {
                if (pc.getLocation().getTileLineDistance(member.getLocation()) <= 3 && member.getClanId() == pc.getClanId() && pc.getClanId() != 0 && member.getId() != pc.getId()) {
                    L1Teleport.teleport(member, bookmark_x, bookmark_y, bookmarkMapId, member.getHeading(), true);
                }
            }
        }

        L1Teleport.teleport(pc, bookmark_x, bookmark_y, bookmarkMapId, pc.getHeading(), true);

        pc.getInventory().removeItem(useItem, 1);
    }
}
