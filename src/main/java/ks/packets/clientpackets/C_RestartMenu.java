package ks.packets.clientpackets;

import ks.constants.L1ClanRankId;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1War;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1FaceUtils;

public class C_RestartMenu extends ClientBasePacket {
    public C_RestartMenu(byte[] data, L1Client client) {
        super(data);

        int type = readC();
        int rank = readC();

        //logger.debug("type:{},rank:{}", type, rank);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanId());
        String clanName = pc.getClanName();

        if (clan == null) {
            return;
        }

        switch (type) {
            case 0: {
                pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.PLEDGE_TWO)); // 임시
                break;
            }

            case 1: {
                String name = readS();
                L1PcInstance targetPc = L1World.getInstance().getPlayer(name);

                if (rank < 2) {
                    pc.sendPackets(new S_ServerMessage(781));
                    return;
                }

                if (pc.isCrown()) { // 군주
                    if (pc.getId() != clan.getLeaderId()) { // 혈맹주
                        pc.sendPackets(new S_ServerMessage(785));
                        return;
                    }
                } else if (pc.getClanRank() == 3 || pc.getClanRank() == 6) {
                    if (!(rank == 2 || rank == 5)) {
                        pc.sendPackets(new S_SystemMessage("수호기사는 계급을 견습, 일반 만 줄 수 있습니다."));
                        return;
                    }
                } else {//
                    pc.sendPackets(new S_ServerMessage(518));
                    return;
                }

                if (targetPc != null) { // 온라인중
                    if (pc == targetPc) {
                        if (pc.isCrown())
                            pc.sendPackets(new S_SystemMessage("혈맹군주의 계급은 변경 할 수 없습니다."));
                        else
                            pc.sendPackets(new S_SystemMessage("자신의 계급은 변경 할 수 없습니다."));
                        return;
                    }

                    if (pc.getClanId() == targetPc.getClanId()) { // 같은 크란
                        try {
                            targetPc.setClanRank(rank);
                            targetPc.save(); // DB에 캐릭터 정보를 기입한다
                            targetPc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_RANK_CHANGED, rank));

                            if (rank == L1ClanRankId.CLAN_RANK_PUBLIC) {
                                targetPc.sendPackets(new S_SystemMessage("당신의 계급이 일반기사로 변경되었습니다."));
                                pc.sendPackets(new S_SystemMessage("" + name + "의 계급이 일반기사로 변경되었습니다."));

                            } else if (rank == L1ClanRankId.CLAN_RANK_GUARDIAN) {
                                targetPc.sendPackets(new S_SystemMessage("당신의 계급이 수호기사로 변경되었습니다."));
                                pc.sendPackets(new S_SystemMessage("" + name + "의 계급이 수호기사로 변경되었습니다."));
                            }
                            clan.updataClanMember(targetPc.getName(), targetPc.getClanRank());
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(414)); // 같은 혈맹원이 아닙니다.
                        return;
                    }
                } else { // 오프 라인중
                    L1PcInstance restorePc = CharacterTable.getInstance().restoreCharacter(name);

                    if (restorePc != null && restorePc.getClanId() == pc.getClanId()) {
                        try {
                            restorePc.setClanRank(rank);
                            restorePc.save(); // DB에 캐릭터 정보를 기입한다
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(109, name));
                        return;
                    }
                }
                break;
            }
            case 2: {
                if (clan.getAlliance() > 0) {
                    pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.드래곤포탈선택));
                } else {
                    pc.sendPackets(new S_ServerMessage(1233)); // 동맹이 없습니다.
                    return;
                }

                break;
            }

            case 3: {
                L1PcInstance allianceLeader = L1FaceUtils.faceToFace(pc);

                if (pc.getLevel() < 25 || !pc.isCrown()) {
                    pc.sendPackets(new S_ServerMessage(1206));
                    return;
                }

                int allienceSize = ClanTable.getInstance().selectAllienceList(pc.getClan().getAlliance()).size();

                if (allienceSize > 4) {
                    pc.sendPackets(new S_ServerMessage(1202));
                    return;
                }

                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.checkClanInWar(clanName)) {
                        pc.sendPackets(new S_ServerMessage(1234));
                        return;
                    }
                }

                if (allianceLeader != null) {
                    if (allianceLeader.getLevel() > 24 && allianceLeader.isCrown()) {
                        if (allianceLeader.getClanId() == pc.getClanId()) {
                            return;
                        }

                        allianceLeader.setTempID(pc.getId());
                        allianceLeader.sendPackets(new S_Message_YN(223, pc.getName()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(1201));
                    }
                }
                break;
            }

            case 4: {
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.checkClanInWar(clanName)) {
                        pc.sendPackets(new S_ServerMessage(1203));
                        return;
                    }
                }

                if (clan.getAlliance() > 0) {
                    pc.sendPackets(new S_Message_YN(1210));
                } else {
                    pc.sendPackets(new S_ServerMessage(1233));
                }

                break;
            }

            case 5:
                pc.sendPackets(new S_SystemMessage("지원하지 않는 기능입니다"));
                break;
            case 6:
            case 8:
                break;
            case 9:
                pc.sendPackets(new S_MapTimer(pc));
                break;
        }
    }
}
