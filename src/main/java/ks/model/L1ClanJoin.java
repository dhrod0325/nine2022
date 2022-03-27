package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ClanRankId;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.pc.CharacterTable;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CharTitle;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ReturnedStat;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1ClanUtils;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class L1ClanJoin {
    private static final Logger logger = LogManager.getLogger(L1ClanJoin.class.getName());

    private static final L1ClanJoin instance = new L1ClanJoin();

    public static L1ClanJoin getInstance() {
        return instance;
    }

    public boolean clanJoin(L1PcInstance pc, L1PcInstance joinPc) {
        int clan_id = pc.getClanId();
        String clanName = pc.getClanName();
        L1Clan clan = L1World.getInstance().getClan(clanName);

        if (clan != null) {
            int maxMember;
            int charisma;

            if (pc.getId() != clan.getLeaderId())
                charisma = pc.getAbility().getTotalCha();
            else
                charisma = getOfflineClanLeaderCha(clan.getLeaderId());

            boolean lv45quest = pc.getQuest().isEnd(L1Quest.QUEST_LEVEL45);

            if (pc.getLevel() >= 50) {
                if (lv45quest) {
                    maxMember = charisma * 9;
                } else {
                    maxMember = charisma * 3;
                }
            } else {
                if (lv45quest) {
                    maxMember = charisma * 6;
                } else {
                    maxMember = charisma * 2;
                }
            }

            if (CodeConfig.CLAN_MEMBER_MAX_COUNT > 0) {
                maxMember = CodeConfig.CLAN_MEMBER_MAX_COUNT;
            }

            if (joinPc.getClanId() == 0) {
                if (maxMember <= clan.getClanMemberList().size()) {
                    joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
                    return false;
                }

                for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
                    clanMembers.sendPackets(new S_ServerMessage(94, joinPc.getName()));
                }

                joinPc.setClanId(clan_id);
                joinPc.setClanName(clanName);
                joinPc.setClanRank(L1ClanRankId.CLAN_RANK_PUBLIC);
                joinPc.setTitle("");
                joinPc.sendPackets(new S_CharTitle(joinPc.getId(), ""));
                Broadcaster.broadcastPacket(joinPc, new S_CharTitle(joinPc.getId(), ""));

                try {
                    joinPc.save(); // DB에 캐릭터 정보를 기입한다
                } catch (Exception e) {
                    logger.error("오류", e);
                }

                clan.addClanMember(joinPc.getName(), joinPc.getClanRank());

                pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.PLEDGE_REFRESH_PLUS));

                joinPc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_RANK_CHANGED, L1ClanRankId.CLAN_RANK_PUBLIC, joinPc.getName()));
                joinPc.sendPackets(new S_ServerMessage(95, clanName));
                L1Teleport.teleport(joinPc, joinPc.getX(), joinPc.getY(), joinPc.getMapId(), joinPc.getHeading(), false);

                joinPc.sendPackets(new S_ReturnedStat(joinPc.getId(), clan.getClanId()));

                for (L1PcInstance player : clan.getOnlineClanMember()) {
                    player.sendPackets(new S_ReturnedStat(joinPc.getId(), joinPc.getClan().getEmblemId()));
                    Broadcaster.broadcastPacket(player, new S_ReturnedStat(player.getId(), joinPc.getClan().getEmblemId()));
                }

                L1Teleport.teleport(joinPc, joinPc.getX(), joinPc.getY(), joinPc.getMapId(), joinPc.getHeading(), false);
            } else {
                if (CodeConfig.CLAN_ALLIANCE) {
                    changeClan(pc, joinPc, maxMember);
                } else {
                    joinPc.sendPackets(new S_ServerMessage(89));
                }
            }
        } else {
            return false;
        }

        return true;
    }


    private void changeClan(L1PcInstance pc, L1PcInstance joinPc, int maxMember) {
        int clanId = pc.getClanId();
        String clanName = pc.getClanName();
        L1Clan clan = L1World.getInstance().getClan(clanName);
        int clanNum = clan.getClanMemberList().size();

        int oldClanId = joinPc.getClanId();
        String oldClanName = joinPc.getClanName();
        L1Clan oldClan = L1World.getInstance().getClan(oldClanName);
        int oldClanNum = oldClan.getClanMemberList().size();

        if (joinPc.isCrown() && joinPc.getId() == oldClan.getLeaderId()) {
            if (maxMember < clanNum + oldClanNum) {
                joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
                return;
            }

            List<L1PcInstance> clanMembers = clan.getOnlineClanMember();

            for (L1PcInstance clanMember : clanMembers) {
                clanMember.sendPackets(new S_ServerMessage(94, joinPc.getName()));
                L1Teleport.teleport(joinPc, joinPc.getX(), joinPc.getY(), joinPc.getMapId(), joinPc.getHeading(), false);
            }

            for (int i = 0; i < oldClan.getClanMemberList().size(); i++) {
                L1PcInstance oldClanMember = L1World.getInstance().getPlayer(oldClan.getClanMemberList().get(i).name);
                if (oldClanMember != null) {
                    oldClanMember.setClanId(clanId);
                    oldClanMember.setClanName(clanName);
                    if (oldClanMember.getId() == joinPc.getId()) {
                        oldClanMember.setClanRank(L1ClanRankId.CLAN_RANK_GUARDIAN);
                    } else {
                        oldClanMember.setClanRank(L1ClanRankId.CLAN_RANK_PROBATION);
                    }

                    try {
                        oldClanMember.save();
                    } catch (Exception e) {
                        logger.error(e);
                    }

                    clan.addClanMember(oldClanMember.getName(), oldClanMember.getClanRank());
                    oldClanMember.sendPackets(new S_ServerMessage(95, clanName)); // \f1%0

                    L1Teleport.teleport(joinPc, joinPc.getX(), joinPc.getY(), joinPc.getMapId(), joinPc.getHeading(), false);

                    oldClanMember.sendPackets(new S_ReturnedStat(oldClanMember.getId(), clan.getClanId()));

                    for (L1PcInstance player : clan.getOnlineClanMember()) {
                        player.sendPackets(new S_ReturnedStat(oldClanMember.getId(), oldClanMember.getClan().getEmblemId()));
                        Broadcaster.broadcastPacket(player, new S_ReturnedStat(player.getId(), oldClanMember.getClan().getEmblemId()));
                    }

                    L1Teleport.teleport(oldClanMember, oldClanMember.getX(), oldClanMember.getY(), oldClanMember.getMapId(), oldClanMember.getHeading(), false);
                } else {
                    try {
                        L1PcInstance offClanMember = CharacterTable.getInstance().restoreCharacter(oldClan.getClanMemberList().get(i).name);
                        offClanMember.setClanId(clanId);
                        offClanMember.setClanName(clanName);
                        offClanMember.setClanRank(L1ClanRankId.CLAN_RANK_PROBATION);
                        offClanMember.save();
                        clan.addClanMember(offClanMember.getName(), offClanMember.getClanRank());
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }

            // 이전혈맹 삭제
            String emblem_file = String.valueOf(oldClanId);
            File file = new File("data/emblem/" + emblem_file);
            file.delete();

            L1ClanUtils.deleteClan(oldClanName);
        }
    }

    public int getOfflineClanLeaderCha(int member) {
        return SqlUtils.selectInteger("SELECT Cha FROM characters WHERE objid=?", member);
    }
}
