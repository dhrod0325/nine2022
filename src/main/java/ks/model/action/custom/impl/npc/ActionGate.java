package ks.model.action.custom.impl.npc;

import ks.constants.L1ActionCodes;
import ks.constants.L1ClanRankId;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.DoorSpawnTable;
import ks.model.L1CastleLocation;
import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1DoorInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.WarTimeScheduler;

public class ActionGate extends L1AbstractNpcAction {
    public ActionGate(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("openigate")) {
            openCloseGate(pc, npcId, true);
        } else if (action.equalsIgnoreCase("closeigate")) {
            openCloseGate(pc, npcId, false);
        } else if (action.equalsIgnoreCase("castlegate")) { // 성문
            castleGateStatus(pc, objId);
        } else if (action.equalsIgnoreCase("healegate_giran outer gatef")) {// 외성 남문
            repairGate(pc, 2031);
        } else if (action.equalsIgnoreCase("healegate_giran outer gatel")) {// 외성 서문
            repairGate(pc, 2032);
        } else if (action.equalsIgnoreCase("healegate_giran inner gatef")) {// 내성 남문
            repairGate(pc, 2033);
        } else if (action.equalsIgnoreCase("healegate_giran inner gatel")) {// 내성 서문
            repairGate(pc, 2034);
        } else if (action.equalsIgnoreCase("healegate_giran inner gater")) {// 내성 동문
            repairGate(pc, 2035);
        } else if (action.equalsIgnoreCase("healigate_giran castle house door")) {// 현관문
            repairGate(pc, 2030);
        } else if (action.equalsIgnoreCase("hhealegate_iron door a")) {// 난성 외성 남문
            repairGate(pc, 2051);
        } else if (action.equalsIgnoreCase("hhealegate_iron door b")) {// 난성 외성 동문문
            repairGate(pc, 2052);
        } else if (action.equalsIgnoreCase("autorepairon")) {// 자동수리 On
            repairAutoGate(pc, 1);
        } else if (action.equalsIgnoreCase("autorepairoff")) {// 자동수리 Off
            repairAutoGate(pc, 0);
        }
    }

    public void repairGate(L1PcInstance pc, int npcId) {
        if (pc.getClan().getCastleId() != 4)
            return;

        if (WarTimeScheduler.getInstance().isNowWar(4))
            return;

        L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(npcId);
        door.repairGate();
    }


    public void repairAutoGate(L1PcInstance pc, int order) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan != null) {
            int castleId = clan.getCastleId();
            if (castleId != 0) {
                if (!WarTimeScheduler.getInstance().isNowWar(castleId)) {
                    for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                        if (L1CastleLocation.checkInWarArea(castleId, door)) {
                            door.setAutoStatus(order);
                        }
                    }

                    pc.sendPackets(new S_ServerMessage(990));
                } else {
                    pc.sendPackets(new S_ServerMessage(991));
                }
            }
        }
    }

    public void openCloseGate(L1PcInstance pc, int keeperId, boolean isOpen) {
        boolean isNowWar = false;
        int pcCastleId = 0;

        if (pc.getClanId() != 0) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
            if (clan != null) {
                pcCastleId = clan.getCastleId();
            }
        }

        int rank = pc.getClanRank();

        if (!pc.isGm()) {
            if (keeperId == 70656 || keeperId == 70549 || keeperId == 70985) {
                if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
                        return;
                    }
                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE,
                                "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }
                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.KENT_CASTLE_ID);
            } else if (keeperId == 70600) { // OT
                if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
                        return;
                    }
                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }

                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.OT_CASTLE_ID);
            } else if (keeperId == 70778 || keeperId == 70987 || keeperId == 70687) {
                if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
                        return;
                    }
                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE,
                            "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }

                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.WW_CASTLE_ID);
            } else if (keeperId == 70817 || keeperId == 70800 || keeperId == 70988
                    || keeperId == 70990 || keeperId == 70989 || keeperId == 70991) {
                if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
                        return;
                    }

                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }

                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.GIRAN_CASTLE_ID);
            } else if (keeperId == 70863 || keeperId == 70992 || keeperId == 70862) {
                if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
                        return;
                    }
                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }

                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.HEINE_CASTLE_ID);
            } else if (keeperId == 70995 || keeperId == 70994 || keeperId == 70993) {
                if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
                        return;
                    }
                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }

                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.DOWA_CASTLE_ID);
            } else if (keeperId == 70996) {
                if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
                    if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
                        return;
                    }
                    if (rank != L1ClanRankId.CLAN_RANK_GUARDIAN && rank != L1ClanRankId.CLAN_RANK_PRINCE) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "수호기사 계급 이상만 성문을 통제할 수 있습니다."));
                        return;
                    }
                } else {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "NPC가 소유하고 있는 성 문은 열 수 없습니다."));
                    return;
                }

                isNowWar = WarTimeScheduler.getInstance().isNowWar(L1CastleLocation.ADEN_CASTLE_ID);
            }
        }

        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (door.getKeeperId() == keeperId) {
                if (isNowWar && door.getMaxHp() > 1) {
                    continue;
                }

                if (isOpen) {
                    door.open(pc);
                } else {
                    door.close(pc);
                }
            }
        }
    }

    public boolean isExistDefenseClan(int castleId) {
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (castleId == clan.getCastleId()) {
                return true;
            }
        }

        return false;
    }

    public void castleGateStatus(L1PcInstance pc, int objid) {
        String htmlid = null;
        String doorStatus = null;
        String[] htmldata = null;
        String[] doorName = null;
        String doorCrack;

        int[] doorNpc = null;

        switch (pc.getClan().getCastleId()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                htmlid = "orville5";
                doorNpc = new int[]{2031, 2032, 2033, 2034, 2035, 2030};
                doorName = new String[]{"$1399", "$1400", "$1401", "$1402",
                        "$1403", "$1386"};
                htmldata = new String[12];
                break;
            case 5:
            case 6:
                htmlid = "potempin5";
                doorNpc = new int[]{2051, 2052, 2050}; // 남문, 동문, 현관문
                doorName = new String[]{"$1399", "$1603", "$1386"};
                htmldata = new String[4];
                break;
        }

        if (doorNpc != null) {
            for (int i = 0; i < doorNpc.length; i++) {
                L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorNpc[i]);
                if (door.getOpenStatus() == L1ActionCodes.ACTION_Close) {
                    doorStatus = "$442";
                } else if (door.getOpenStatus() == L1ActionCodes.ACTION_Open) {
                    doorStatus = "$443";
                }

                htmldata[i] = "" + doorName[i] + "" + doorStatus + "";

                switch (door.getCrackStatus()) {
                    case 0:
                        doorCrack = "$439";
                        break;
                    case 1:
                        doorCrack = "$438";
                        break;
                    case 2:
                        doorCrack = "$437";
                        break;
                    case 3:
                        doorCrack = "$436";
                        break;
                    case 4:
                        doorCrack = "$435";
                        break;
                    default:
                        doorCrack = "$434";
                        break;
                }

                htmldata[i + doorNpc.length] = "" + doorName[i] + "" + doorCrack + "";
            }
        }

        pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
    }
}
