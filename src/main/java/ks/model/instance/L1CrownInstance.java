package ks.model.instance;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.CastleTable;
import ks.core.datatables.clan.ClanTable;
import ks.model.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1ClanUtils;
import ks.util.L1WarUtils;

import java.util.Collection;
import java.util.List;

public class L1CrownInstance extends L1NpcInstance {
    public L1CrownInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        boolean inWar = false;

        if (pc.getClanId() == 0) { // 크란미소속
            return;
        }

        String clanName = pc.getClanName();
        L1Clan clan = L1World.getInstance().getClan(clanName);

        if (clan == null) {
            return;
        }

        if (!pc.isCrown()) { // 군주 이외
            return;
        }

        if (pc.getLevel() < CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL) {
            pc.sendPackets(new S_SystemMessage(CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL + "렙 이상 군주캐릭만 왕관을 클릭 가능합니다"));
            return;
        }


        if (pc.getWeapon() != null) {
            pc.sendPackets(new S_SystemMessage("무기를 장착하고 면류관을 클릭할 수 없습니다."));
            return;
        }

        if (pc.getGfxId().getTempCharGfx() != 0 && pc.getGfxId().getTempCharGfx() != 1) {
            pc.sendPackets(new S_SystemMessage("변신상태에서 면류관을 클릭할 수 없습니다."));

            return;
        }

        if (pc.getId() != clan.getLeaderId()) {
            pc.sendPackets(new S_SystemMessage("혈맹의 군주만이 면류관을 클릭할 수 있습니다."));

            return;
        }

        if (!checkRange(pc)) { // 크라운의 1 셀 이내
            return;
        }

        if (clan.getCastleId() != 0) {
            pc.sendPackets(new S_ServerMessage(474));
            return;
        }

        int castleId = L1CastleLocation.getCastleId(getX(), getY(), getMapId());

        boolean existDefenseClan = false;
        L1Clan defenceClan = null;

        for (L1Clan defClan : L1World.getInstance().getAllClans()) {
            if (castleId == defClan.getCastleId()) {
                defenceClan = L1World.getInstance().getClan(defClan.getClanName());
                existDefenseClan = true;
                break;
            }
        }

        Collection<L1War> wars = L1World.getInstance().getWarList();

        for (L1War war : wars) {
            if (castleId == war.getCastleId()) {
                inWar = war.checkClanInWar(clanName);
                break;
            }
        }

        if (existDefenseClan && !inWar) {
            return;
        }

        if (existDefenseClan && defenceClan != null) {
            defenceClan.setCastleId(0);
            L1ClanUtils.updateClan(defenceClan);
        }

        pc.setCastleIn(true);
        clan.setCastleId(castleId);

        L1ClanUtils.updateClan(clan);

        L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);

        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, clanName + "혈맹이 " + castle.getName() + "을 획득하였습니다"));

        Collection<L1PcInstance> list = L1World.getInstance().getAllPlayers();

        for (L1PcInstance otherMember : list) {
            if (pc.getClanId() != otherMember.getClanId()) {
                if (L1CastleLocation.checkInWarArea(castleId, otherMember)) {
                    int[] loc = L1CastleLocation.getGetBackLoc(castleId);
                    int locX = loc[0];
                    int locY = loc[1];
                    short mapId = (short) loc[2];
                    LineageAppContext.commonTaskScheduler().execute(() -> L1Teleport.teleport(otherMember, locX, locY, mapId, 5, true));
                }
            }
        }

        for (L1War war : wars) {
            if (war.checkClanInWar(clanName) && existDefenseClan) {
                war.winCastleWar(clanName);
                break;
            }
        }

        List<L1PcInstance> clanMember = clan.getOnlineClanMember();

        if (clanMember.size() > 0) {
            for (L1PcInstance member : clanMember) {
                member.sendPackets(new S_ServerMessage(643));
            }
        }

        deleteMe();

        Collection<L1Object> list2 = L1World.getInstance().getAllObject();

        for (L1Object obj : list2) {
            if (obj == null) {
                continue;
            }

            if (obj instanceof L1TowerInstance) {
                L1TowerInstance lt = (L1TowerInstance) obj;

                if (L1CastleLocation.checkInWarArea(castleId, lt)) {
                    lt.deleteMe();
                }
            }

            if (obj instanceof L1CastleGuardInstance) {
                L1CastleGuardInstance lt = (L1CastleGuardInstance) obj;

                if (L1CastleLocation.checkInWarArea(castleId, lt)) {
                    lt.allTargetClear();

                    L1Spawn spawn = lt.getSpawn();

                    if (spawn != null) {
                        L1Teleport.npcTeleport(lt, spawn.getLocX(), spawn.getLocY(), spawn.getMapId(), spawn.getHeading(), true);
                    }
                }
            }
        }

        L1WarSpawn warSpawn = new L1WarSpawn();
        warSpawn.spawnTower(castleId);

        pc.setCastleIn(false);

        ClanTable.getInstance().load();

        L1WarUtils.getInstance().startDefenseTimer(castle.getId());
    }

    @Override
    public void deleteMe() {
        super.deleteMe();
    }

    private boolean checkRange(L1PcInstance pc) {
        return (getX() - 1 <= pc.getX() && pc.getX() <= getX() + 1 && getY() - 1 <= pc.getY() && pc.getY() <= getY() + 1);
    }
}
