package ks.util;

import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ReturnedStat;
import ks.packets.serverpackets.S_ServerMessage;

import java.io.File;
import java.util.List;

public class L1ClanUtils {
    public static void leaveClanBoss(L1Clan clan, L1PcInstance pc) {
        String pcName = pc.getName();
        String clanName = pc.getClanName();

        if (clan.getCastleId() > 0 || clan.getHouseId() > 0) {
            pc.sendPackets(new S_ServerMessage(665));
            return;
        }

        for (L1War war : L1World.getInstance().getWarList()) {
            if (war.checkClanInWar(clanName)) {
                pc.sendPackets(new S_ServerMessage(302));
                return;
            }
        }

        if (clan.getAlliance() != 0) {
            pc.sendPackets(new S_ServerMessage(1235));
            return;
        }

        for (L1ClanMember clanMember : clan.getClanMemberList()) {
            String name = clanMember.name;

            L1PcInstance member = L1World.getInstance().getPlayer(name);

            if (member == null) {
                member = CharacterTable.getInstance().restoreCharacter(name);
            } else {
                member.sendPackets(new S_ServerMessage(269, pcName, clanName));
            }

            member.clearPlayerClanData();

            member.sendPackets(new S_ReturnedStat(member.getId(), 0));
            Broadcaster.broadcastPacket(member, new S_ReturnedStat(member.getId(), 0));

            for (L1PcInstance onlineMember : clan.getOnlineClanMember()) {
                onlineMember.sendPackets(new S_ReturnedStat(onlineMember.getId(), 0));
                Broadcaster.broadcastPacket(onlineMember, new S_ReturnedStat(onlineMember.getId(), 0));
            }
        }

        String emblem = String.valueOf(clan.getEmblemId());

        File file = new File("data/emblem/" + emblem);
        file.delete();

        deleteClan(clanName);
    }

    public static void leaveClanMember(L1Clan clan, L1PcInstance pc) {
        String playerName = pc.getName();
        String clanName = pc.getClanName();

        List<L1PcInstance> clanMember = clan.getOnlineClanMember();

        for (L1PcInstance member : clanMember) {
            member.sendPackets(new S_ServerMessage(178, playerName, clanName));
        }

        pc.clearPlayerClanData();

        L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);

        clan.removeClanMember(playerName);

        pc.sendPackets(new S_ReturnedStat(pc.getId(), 0));
        Broadcaster.broadcastPacket(pc, new S_ReturnedStat(pc.getId(), 0));
    }

    public static void updateClan(L1Clan clan) {
        ClanTable.getInstance().updateClan(clan);
        L1World.getInstance().storeClan(clan);
    }

    public static void deleteClan(String clanName) {
        L1Clan clan = L1World.getInstance().getClan(clanName);
        ClanTable.getInstance().deleteClan(clan.getClanName());
        L1World.getInstance().removeClan(clan);
    }
}
