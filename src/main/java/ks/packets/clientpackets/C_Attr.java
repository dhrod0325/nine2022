package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.core.ObjectIdFactory;
import ks.core.datatables.BuddyTable;
import ks.core.datatables.HouseTable;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.datatables.pet.PetTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1PetInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class C_Attr extends ClientBasePacket {
    public static final int ATTR_YES = 1;
    public static final int ATTR_NO = 0;

    private static final byte[] HEADING_TABLE_X = CodeConfig.HEADING_TABLE_X;
    private static final byte[] HEADING_TABLE_Y = CodeConfig.HEADING_TABLE_Y;

    private static final Logger logger = LogManager.getLogger();

    public C_Attr(byte[] data, L1Client client) {
        super(data);

        int i = readH();
        int attrCode;
        int reply;

        if (i == 479) {
            attrCode = i;
        } else {
            readD();
            attrCode = readH();

            if (attrCode == 65535) {
                attrCode = 622;
            }
        }

        logger.debug("i:{},attrCode:{}", i, attrCode);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        String name;

        switch (attrCode) {
            case 622:
                reply = readC();

                S_Message_YN.MessageYnWrapper ynWrapper = S_Message_YN.idxMap.get(attrCode);

                if (ynWrapper == null)
                    return;

                int idx = ynWrapper.getIdx();

                if (idx == 0) {
                    BuddyTable buddyTable = BuddyTable.getInstance();
                    L1Buddy buddyList = buddyTable.getBuddyTable(pc.getId());
                    L1PcInstance target2 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());

                    pc.setTempID(0);
                    String name2 = pc.getName();

                    if (target2 != null) {
                        if (reply == ATTR_NO) {
                            target2.sendPackets(new S_SystemMessage(pc.getName() + "님이 친구 요청을 거절하였습니다."));
                        } else if (reply == ATTR_YES) { // Yes
                            buddyList.add(pc.getId(), name2);
                            buddyTable.addBuddy(target2.getId(), pc.getId(), name2);
                            target2.sendPackets(new S_SystemMessage(pc.getName() + "님이 친구 등록 되었습니다."));
                            pc.sendPackets(new S_SystemMessage(target2.getName() + "님에게 친구 등록이 되었습니다."));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("그러한 케릭명을 가진 사람이 없습니다."));
                    }
                }

                break;
            case 97:
                reply = readC();

                L1PcInstance joinPc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
                pc.setTempID(0);

                if (joinPc != null) {
                    if (reply == ATTR_NO) {
                        joinPc.sendPackets(new S_ServerMessage(96, pc.getName()));
                    } else if (reply == ATTR_YES) {
                        L1ClanJoin.getInstance().clanJoin(pc, joinPc);
                    }
                }

                break;
            case 217:
            case 221:
            case 222:
                reply = readC();

                L1PcInstance enemyLeader = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());

                if (enemyLeader == null) {
                    return;
                }

                pc.setTempID(0);
                String clanName = pc.getClanName();
                String enemyClanName = enemyLeader.getClanName();

                if (reply == ATTR_NO) {
                    if (i == 217) {
                        enemyLeader.sendPackets(new S_ServerMessage(236, clanName));
                    } else {
                        enemyLeader.sendPackets(new S_ServerMessage(237, clanName));
                    }
                } else if (reply == ATTR_YES) {
                    if (i == 217) {
                        L1War war = new L1War();
                        war.handleCommands(2, enemyClanName, clanName);
                    } else {
                        for (L1War war : L1World.getInstance().getWarList()) {
                            if (war.checkClanInWar(clanName)) {
                                if (i == 221) {
                                    war.surrenderWar(enemyClanName, clanName);
                                } else {
                                    war.ceaseWar(enemyClanName, clanName);
                                }

                                break;
                            }
                        }
                    }
                }
                break;
            case 223: { //동맹요청
                reply = readC();

                if (reply == ATTR_YES) {
                    L1PcInstance targetPc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
                    pc.setTempID(0);

                    if (targetPc.getClanId() > 0 && pc.getClanId() > 0) {
                        L1Clan targetClan = targetPc.getClan();
                        L1Clan pcClan = pc.getClan();

                        int allianceId;

                        if (targetClan.getAlliance() != 0) {
                            allianceId = targetClan.getAlliance();
                        } else {
                            allianceId = pcClan.getAlliance();
                        }

                        if (allianceId == 0) {
                            allianceId = ObjectIdFactory.getInstance().nextId();
                        }

                        targetClan.setAlliance(allianceId);
                        pcClan.setAlliance(allianceId);

                        ClanTable.getInstance().updateClan(targetClan);
                        ClanTable.getInstance().updateClan(pcClan);

                        targetPc.sendPackets(new S_PacketBox(targetPc, L1PacketBoxType.드래곤포탈선택));
                        targetPc.sendPackets(new S_ServerMessage(1200, pc.getClanName()));

                        pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.드래곤포탈선택));
                        pc.sendPackets(new S_ServerMessage(1200, targetPc.getClanName()));
                    }
                }
            }

            break;
            case 1210://동맹탈퇴
                reply = readC();

                if (reply == ATTR_YES) {
                    if (pc.getClanId() == 0) {
                        return;
                    }

                    List<Integer> clanList = ClanTable.getInstance().selectClanIdListByAllience(pc.getClan().getAlliance());

                    for (Integer clanId : clanList) {
                        if (clanId == pc.getClanId()) {
                            continue;
                        }

                        L1Clan clan = L1World.getInstance().getClan(clanId);

                        List<L1PcInstance> members = clan.getOnlineClanMember();

                        for (L1PcInstance member : members) {
                            member.sendPackets(new S_ServerMessage(225, pc.getClanName(), clan.getClanName()));
                        }

                        if (clanList.size() <= 2) {
                            clan.setAlliance(0);
                            ClanTable.getInstance().updateClan(clan);
                        }
                    }

                    pc.getClan().setAlliance(0);
                    ClanTable.getInstance().updateClan(pc.getClan());
                }

                break;
            case 252:
                reply = readC();

                L1Object tradingPartner = L1World.getInstance().findObject(pc.getTradeID());

                if (tradingPartner != null) {
                    if (tradingPartner instanceof L1PcInstance) {
                        L1PcInstance target = (L1PcInstance) tradingPartner;

                        if (reply == ATTR_NO) {
                            target.sendPackets(new S_ServerMessage(253, pc.getName()));
                            pc.setTradeID(0);
                            target.setTradeID(0);
                        } else if (reply == ATTR_YES) {
                            pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "사기 방지를 위해 상대방과 마주본후 거래 해주세요."));
                            target.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "사기 방지를 위해 상대방과 마주본후 거래 해주세요."));
                            pc.sendPackets(new S_Trade(target.getName()));
                            target.sendPackets(new S_Trade(pc.getName()));
                        }
                    }
                }
                break;
            case 321: {
                reply = readC();

                L1PcInstance resPc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
                pc.setTempID(0);

                if (resPc != null) {
                    if (reply == ATTR_NO) {
                        return;
                    }

                    if (reply == ATTR_YES) {
                        if (L1CastleLocation.isNowWarByArea(pc)) {
                            pc.sendPackets(new S_SystemMessage("\\fY공성장에서는 부활이 불가능 합니다"));
                            return;
                        }

                        if (pc.isInParty()) {
                            if (pc.isDead()) {
                                pc.getParty().refresh(pc);
                            }
                        }

                        pc.sendPackets(new S_SkillSound(pc.getId(), 346));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 346));
                        pc.resurrect(pc.getMaxHp() / 2);
                        pc.setCurrentHp(pc.getMaxHp() / 2);
                        pc.startMpRegenerationByDoll();
                        pc.sendPackets(new S_Resurrection(pc, resPc, 0));
                        Broadcaster.broadcastPacket(pc, new S_Resurrection(pc, resPc, 0));
                        pc.sendPackets(new S_CharVisualUpdate(pc));
                        Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                        pc.tell();
                    }
                }
            }

            break;
            case 322: {
                reply = readC();

                L1PcInstance resPc2 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());

                pc.setTempID(0);

                if (resPc2 != null) {
                    if (reply == ATTR_NO) {
                        return;
                    }

                    if (reply == ATTR_YES) {
                        if (L1CastleLocation.isNowWarByArea(pc)) {
                            pc.sendPackets(new S_SystemMessage("\\fY공성장에서는 부활이 불가능 합니다"));
                            return;
                        }

                        if (pc.isInParty()) {//파티
                            if (pc.isDead()) {
                                pc.getParty().refresh(pc);
                            }
                        }

                        pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), '\346'));
                        pc.resurrect(pc.getMaxHp());
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.startHpRegenerationByDoll();
                        pc.startMpRegenerationByDoll();
                        pc.sendPackets(new S_Resurrection(pc, resPc2, 0));
                        Broadcaster.broadcastPacket(pc, new S_Resurrection(pc, resPc2, 0));
                        pc.sendPackets(new S_CharVisualUpdate(pc));
                        Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));

                        if (pc.getExpRes() == 1 && pc.isGres() && pc.isGresValid()) {
                            pc.resExp();
                            pc.setExpRes(0);
                            pc.setGres(false);
                        }

                        pc.getRankBuff().reload();

                        pc.tell();
                    }
                }
            }

            break;
            case 325:
                readC();
                name = readS();

                int len = name.length();

                if (len > 6 || len < 1) {
                    pc.sendPackets("펫 이름 길이를 확인해주세요.");
                    pc.setTempID(0);
                    break;
                }

                L1PetInstance pet = (L1PetInstance) L1World.getInstance().findObject(pc.getTempID());
                pc.setTempID(0);
                reNamePet(pet, name);

                break;
            case 512:
                readC();
                name = readS();
                int houseId = pc.getTempID();
                pc.setTempID(0);
                if (name.length() <= 16) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    house.setHouseName(name);
                    HouseTable.getInstance().updateHouse(house);
                } else {
                    pc.sendPackets(new S_ServerMessage(513));
                }
                break;
            case 2923:
                reply = readH();

                if (reply == ATTR_NO) {
                    pc.sendPackets(new S_SystemMessage("드래곤 포탈 입장이 취소되었습니다."));
                }

                break;
            case 630:
                reply = readC();

                L1PcInstance fightPc = (L1PcInstance) L1World.getInstance().findObject(pc.getFightId());

                if (reply == ATTR_NO) {
                    pc.setFightId(0);
                    fightPc.setFightId(0);
                    fightPc.sendPackets(new S_ServerMessage(631, pc.getName()));
                } else if (reply == ATTR_YES) {
                    fightPc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_DUEL, fightPc.getFightId(), fightPc.getId()));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_DUEL, pc.getFightId(), pc.getId()));
                }
                break;

            case 653:
                reply = readC();
                L1PcInstance target653 = (L1PcInstance) L1World.getInstance().findObject(pc.getPartnerId());
                if (reply == ATTR_NO) { // No
                    return;
                } else if (reply == ATTR_YES) { // Yes
                    if (!pc.getInventory().checkItem(40308, 2000000)) {
                        pc.sendPackets(new S_SystemMessage("\\fC이혼을 하려면 위자료  200만 아데나가 필요합니다."));    //위자료 BY호꾸
                        return;
                    }


                    if (target653 != null) {
                        target653.setPartnerId(0);
                        target653.save();
                        target653.sendPackets(new S_ServerMessage(662));
                    } else {
                        CharacterTable.getInstance().updatePartnerId(pc.getPartnerId());
                    }
                }

                pc.setPartnerId(0);
                pc.save(); // DB에 캐릭터 정보를 기입한다
                pc.getInventory().consumeItem(40308, 2000000);
                pc.sendPackets(new S_ServerMessage(662));
                break;
            case 3589:
                reply = readC();
                if (reply == ATTR_NO) {
                    return;
                }

                if (reply == ATTR_YES) {
                    if (pc.getInventory().consumeItem(40308, 2000000) && pc.getLevel() >= 59) {
                        pc.getQuest().setEnd(L1Quest.QUEST_SNAP_RARING);
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
                    }
                }
                break;
            case 654:
                reply = readC();
                L1PcInstance partner = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
                pc.setTempID(0);

                if (partner != null) {
                    if (reply == ATTR_NO) { // No
                        partner.sendPackets(new S_ServerMessage(656, pc.getName())); // %0%s는
                    } else if (reply == ATTR_YES) { // Yes
                        pc.setPartnerId(partner.getId());
                        pc.save();
                        pc.sendPackets(new S_ServerMessage(790)); // 모두의 축복
                        pc.sendPackets(new S_ServerMessage(655, partner.getName())); // 축하합니다!
                        pc.sendPackets(new S_SkillSound(pc.getId(), 2059));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2059));
                        L1World.getInstance().broadcastServerMessage("축하합니다! " + pc.getName() + "님과 " + partner.getName() + "님이 결혼하셨습니다.");

                        partner.setPartnerId(pc.getId());
                        partner.save();
                        partner.sendPackets(new S_ServerMessage(790));
                        partner.sendPackets(new S_ServerMessage(655, pc.getName()));
                        partner.sendPackets(new S_SkillSound(partner.getId(), 2059));
                        Broadcaster.broadcastPacket(partner, new S_SkillSound(partner.getId(), 2059));
                    }
                }
                break;
            case 729:
                reply = readC();
                if (reply == ATTR_NO) {
                    return;
                }

                if (reply == ATTR_YES) { // Yes
                    callClan(pc);
                }
                break;
            case 2551: //경험치 회복에는 구호 증서가 소모됩니다. 경험치를 회복하시겠습니까? (Y/N)
                reply = readC();

                if (reply == ATTR_NO) {
                    return;
                }

                if (reply == ATTR_YES && pc.getExpRes() == 1) {
                    if (L1CommonUtils.isNotExpRestoreAble(pc)) {
                        return;
                    }

                    pc.getInventory().consumeItem(701235, 1);
                    int needExp = ExpTable.getInstance().getNeedExpNextLevel(pc.getLevel());
                    double PobyExp = needExp * 0.05;
                    pc.addExp((int) PobyExp);
                    pc.setExpRes(0);
                }
                break;
            case 738:
                reply = readC();

                if (reply == ATTR_NO) {
                    return;
                }

                if (reply == ATTR_YES && pc.getExpRes() == 1) { // Yes
                    if (L1CommonUtils.isNotExpRestoreAble(pc)) {
                        return;
                    }

                    int cost;
                    int level = pc.getLevel();
                    int lawful = pc.getLawful();

                    if (level < 45) {
                        cost = level * level * 50;
                    } else {
                        cost = level * level * 100;
                    }

                    if (lawful >= 0) {
                        cost = (cost / 2);
                    }

                    cost *= 2;

                    if (pc.getLevel() > 9)
                        if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
                            pc.resExpToTemple();
                            pc.setExpRes(0);
                        } else {
                            pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분치 않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                        }
                    else {
                        pc.sendPackets(new S_ChatPacket(pc, "경험치를 회복하려면 레벨10부터 사용하실수 있습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                    }
                }

                break;
            case 951:
                reply = readC();
                L1PcInstance chatPc = (L1PcInstance) L1World.getInstance().findObject(pc.getPartyID());
                if (chatPc != null) {
                    if (reply == ATTR_NO) {
                        chatPc.sendPackets(new S_ServerMessage(423, pc.getName()));
                        pc.setPartyID(0);
                    } else if (reply == ATTR_YES) {
                        if (chatPc.isInChatParty()) {
                            if (chatPc.getChatParty().isVacancy() || chatPc.isGm()) {
                                chatPc.getChatParty().addMember(pc);
                            } else {
                                chatPc.sendPackets(new S_ServerMessage(417));
                            }
                        } else {
                            L1ChatParty chatParty = new L1ChatParty();
                            chatParty.addMember(chatPc);
                            chatParty.addMember(pc);
                            chatPc.sendPackets(new S_ServerMessage(424, pc.getName()));
                        }
                    }
                }
                break;
            case 953:
            case 954:
                reply = readC();

                L1PcInstance target = (L1PcInstance) L1World.getInstance().findObject(pc.getPartyID());
                if (target != null) {
                    if (reply == ATTR_NO) {
                        target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가
                        pc.setPartyID(0);
                    } else if (reply == ATTR_YES) { // Yes
                        if (target.isInParty()) {
                            if (target.getParty().isVacancy()) {
                                target.getParty().addMember(pc);
                            } else {
                                target.sendPackets(new S_ServerMessage(417));
                            }
                        } else {
                            L1Party party = new L1Party();
                            party.addMember(target);
                            party.addMember(pc);

                            target.sendPackets(new S_ServerMessage(424, pc.getName())); // %0가
                        }
                    }
                }
                break;
            case 479:
                reply = readC();

                if (reply == ATTR_YES) {
                    String s = readS();

                    try {
                        pc.onStat(s);
                    } catch (Exception e) {
                        logger.info(String.format("%s님이 스텟오버를 시도했습니다.", pc.getName()));
                    }
                }
                break;
            case 3348:
                reply = readC();

                if (reply == 1) {
                    L1PcInstance requestPlayer = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
                    pc.setTempID(0);

                    if (requestPlayer == null)
                        return;

                    L1Clan pcClan = L1World.getInstance().getClan(pc.getClanName());

                    if (pcClan == null) {
                        return;
                    }

                    L1Clan targetClan = L1World.getInstance().getClan(requestPlayer.getClanName());

                    if (targetClan == null) {
                        return;
                    }

                    targetClan.addGazelist(pcClan.getClanName());
                    pcClan.addGazelist(targetClan.getClanName());

                    for (L1PcInstance member : pcClan.getOnlineClanMember()) {
                        member.sendPackets(new S_ClanAttention(true, targetClan.getClanName()));
                        member.sendPackets(new S_ClanAttention(pcClan.getGazeSize(), pcClan.getGazeList()));
                    }

                    for (L1PcInstance member : targetClan.getOnlineClanMember()) {
                        member.sendPackets(new S_ClanAttention(true, pcClan.getClanName()));
                        member.sendPackets(new S_ClanAttention(targetClan.getGazeSize(), targetClan.getGazeList()));
                    }
                }
                break;
            default:
                break;
        }
    }

    private static void reNamePet(L1PetInstance pet, String name) {
        if (pet == null || name == null) {
            throw new NullPointerException();
        }

        int petItemObjId = pet.getItemObjId();
        L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);

        if (petTemplate == null) {
            throw new NullPointerException();
        }

        L1PcInstance pc = (L1PcInstance) pet.getMaster();

        if (PetTable.isNameExists(name)) {
            pc.sendPackets(new S_ServerMessage(327));
            return;
        }

        pet.setName(name);

        petTemplate.setName(name);
        PetTable.getInstance().storePet(petTemplate);
        L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
        pc.getInventory().updateItem(item);
        pc.sendPackets(new S_ChangeName(pet.getId(), name));
        Broadcaster.broadcastPacket(pc, new S_ChangeName(pet.getId(), name));
    }

    private void callClan(L1PcInstance pc) {
        L1PcInstance callClanPc = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());

        short mapId = callClanPc.getMapId();

        pc.setTempID(0);

        if (!pc.isEscapable() && !pc.isGm()) {
            pc.sendPackets(new S_ServerMessage(647));
            L1Teleport.teleport(pc, pc.getLocation(), pc.getHeading(), false);
            return;
        }

        if (pc.getId() != callClanPc.getCallClanId()) {
            return;
        }

        if (mapId != 0 && mapId != 4 && mapId != 304 || L1CastleLocation.isNowWarByArea(pc)) {
            pc.sendPackets(new S_ServerMessage(547));
            return;
        }

        L1Map map = callClanPc.getMap();

        int heading = callClanPc.getCallClanHeading();

        int locX = callClanPc.getX() + HEADING_TABLE_X[heading];
        int locY = callClanPc.getY() + HEADING_TABLE_Y[heading];

        heading = (heading + 4) % 4;

        boolean isExistCharacter = false;

        for (L1Object object : L1World.getInstance().getVisibleObjects(callClanPc, 1)) {
            if (object instanceof L1Character) {
                L1Character cha = (L1Character) object;
                if (cha.getX() == locX && cha.getY() == locY && cha.getMapId() == mapId) {
                    isExistCharacter = true;
                    break;
                }
            }
        }

        if (locX == 0 && locY == 0 || !map.isPassable(locX, locY) || isExistCharacter) {
            pc.sendPackets(new S_ServerMessage(627));
            return;
        }

        L1Teleport.teleport(pc, locX, locY, mapId, heading, true);
    }
}
