package ks.packets.clientpackets;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.event.L1ChatEvent;
import ks.commands.common.CommonCommands;
import ks.commands.gm.GmCommands;
import ks.commands.user.UserCommands;
import ks.constants.L1ClanRankId;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.Broadcaster;
import ks.model.L1Clan;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1MonsterInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.model.txt.L1TxtChat;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.system.chat.L1ChatEventHandler;
import ks.system.chat.L1ChatEventListener;
import ks.util.log.L1LogUtils;

import java.util.List;

public class C_Chat extends ClientBasePacket {
    public C_Chat(byte[] data, L1Client client) {
        super(data);

        try {
            L1PcInstance pc = client.getActiveChar();

            if (pc == null) {
                client.disconnect();
                return;
            }

            if (L1World.getInstance().getPlayer(client.getActiveChar().getName()) == null) {
                client.disconnect();
                return;
            }

            if (pc.getMapId() == L1Map.MAP_2D && !pc.isGm()) {
                pc.sendPackets(new S_ServerMessage(912));
                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
                pc.sendPackets(new S_ServerMessage(242));
                return;
            }

            if (pc.isDeathMatch()) {
                pc.sendPackets(new S_ServerMessage(912));
                return;
            }

            int chatType = readC();
            String chatText = readS();

            L1ChatEventHandler handler = L1ChatEventListener.getInstance().get(pc);

            if (handler != null) {
                if (System.currentTimeMillis() < handler.getStartTime() + (handler.getWaitTimeSecond() * 1000)) {
                    if (handler.getChatMessage().equalsIgnoreCase(chatText)) {
                        handler.process();
                    } else {
                        handler.fail();
                    }

                    handler.stopHandle();

                    return;
                } else {
                    if (handler.getChatMessage().equalsIgnoreCase(chatText)) {
                        handler.process();
                    } else {
                        handler.fail();
                    }

                    handler.stopHandle();
                }

                L1ChatEventListener.getInstance().remove(pc);
            }

            switch (chatType) {
                case 0: {
                    if (chatText.equalsIgnoreCase("..")) {
                        CommonCommands.getInstance().tell(pc);
                        return;
                    }

                    if (chatText.startsWith(".") && (pc.getAccessLevel() == CodeConfig.GM_CODE || pc.getAccessLevel() == 1)) {
                        if (".".equalsIgnoreCase(chatText) && pc.getLastChat() != null) {
                            if (!CommonCommands.getInstance().handleCommands(pc, pc.getLastChat().substring(1))) {
                                GmCommands.getInstance().handleCommands(pc, pc.getLastChat().substring(1));
                            }
                        } else {
                            if (!CommonCommands.getInstance().handleCommands(pc, chatText.substring(1))) {
                                GmCommands.getInstance().handleCommands(pc, chatText.substring(1));
                            }

                            pc.setLastChat(chatText);
                        }

                        return;
                    }

                    if (chatText.startsWith("$")) {
                        String text = chatText.substring(1);
                        chatWorld(pc, text, 12);

                        if (!pc.isGm()) {
                            pc.getChatCheck().checkChatInterval();
                        }

                        return;
                    }

                    if (chatText.startsWith(".")) { // 유저코멘트
                        if (".".equalsIgnoreCase(chatText) && pc.getLastChat() != null) {
                            if (!CommonCommands.getInstance().handleCommands(pc, pc.getLastChat().substring(1))) {
                                UserCommands.getInstance().handleCommands(pc, pc.getLastChat().substring(1));
                            }
                        } else {
                            if (!CommonCommands.getInstance().handleCommands(pc, chatText.substring(1))) {
                                UserCommands.getInstance().handleCommands(pc, chatText.substring(1));
                            }

                            pc.setLastChat(chatText);
                        }

                        return;
                    }

                    if (!pc.getExcludingList().contains(pc.getName())) {
                        pc.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 0));
                    }

                    for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {
                        if (!listner.getExcludingList().contains(pc.getName())) {
                            listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 0));
                        }
                    }

                    for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
                        if (obj instanceof L1MonsterInstance) {
                            L1MonsterInstance mob = (L1MonsterInstance) obj;
                            if (mob.getTemplate().isDoppel() && mob.getName().equals(pc.getName())) {
                                Broadcaster.broadcastPacket(mob, new S_NpcChatPacket(mob, chatText, 0));
                            }
                        }
                    }

                    L1LogUtils.chatLog("[일반] {} : {}", pc.getName(), chatText);

                    LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "일반", chatText)));
                }

                break;
                case 2: {
                    if (!pc.getExcludingList().contains(pc.getName())) {
                        pc.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 2));
                    }

                    List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(pc, 50);

                    for (L1PcInstance player : players) {
                        if (!player.getExcludingList().contains(pc.getName())) {
                            player.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 2));
                        }
                    }

                    L1LogUtils.chatLog("[일반] {} : {}", pc.getName(), chatText);
                    LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "일반", chatText)));

                    for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
                        if (obj instanceof L1MonsterInstance) {
                            L1MonsterInstance mob = (L1MonsterInstance) obj;
                            if (mob.getTemplate().isDoppel() && mob.getName().equals(pc.getName())) {
                                for (L1PcInstance player : players) {
                                    player.sendPackets(new S_NpcChatPacket(mob, chatText, 2));
                                }
                            }
                        }
                    }


                }

                break;
                case 3:
                case 12:
                    chatWorld(pc, chatText, chatType);

                    break;
                case 4:
                    if (pc.getClanId() != 0) { // 크란 소속중
                        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

                        int rank = pc.getClanRank();

                        if (clan != null && (rank == L1ClanRankId.CLAN_RANK_PROBATION          //수련기사
                                || rank == L1ClanRankId.CLAN_RANK_PUBLIC             //일반
                                || rank == L1ClanRankId.CLAN_RANK_GUARDIAN           //수호기사
                                || rank == L1ClanRankId.CLAN_RANK_PRINCE)) {        //혈맹군주

                            L1LogUtils.chatLog("[혈맹] [{}] {}:{} ", pc.getClanName(), pc.getName(), chatText);
                            LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "혈맹", chatText)));

                            for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                if (!listner.getExcludingList().contains(pc.getName())) {
                                    listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, 4));
                                }
                            }
                        }
                    }

                    break;
                case 11:
                    if (pc.isInParty()) { // 파티중
                        for (L1PcInstance listner : pc.getParty().getMembers()) {
                            if (!listner.getExcludingList().contains(pc.getName())) {
                                listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, 11));
                            }
                        }
                    }

                    L1LogUtils.chatLog("[파티] {}:{} ", pc.getName(), chatText);
                    LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "파티", chatText)));

                    break;
                case 13:  // 연합 채팅
                    if (pc.getClanId() != 0) { // 혈맹 소속중
                        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                        int rank = pc.getClanRank();
                        if (clan != null && (rank == L1ClanRankId.CLAN_RANK_GUARDIAN //수호기사
                                || rank == L1ClanRankId.CLAN_RANK_PRINCE)) {//혈맹군주
                            for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                int listnerRank = listner.getClanRank();
                                if (!listner.getExcludingList().contains(pc.getName()) && (listnerRank == L1ClanRankId.CLAN_RANK_GUARDIAN //수호기사
                                        || listnerRank == L1ClanRankId.CLAN_RANK_PRINCE)) {//혈맹군주
                                    listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, 13));
                                }
                            }
                        }
                    }

                    L1LogUtils.chatLog("[연합] {}:{} ", pc.getName(), chatText);
                    LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "연합", chatText)));

                    break;
                case 14:  // 채팅 파티
                    if (pc.isInChatParty()) { // 채팅 파티중

                        for (L1PcInstance listner : pc.getChatParty().getMembers()) {
                            if (!listner.getExcludingList().contains(pc.getName())) {
                                listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 14));
                            }
                        }
                    }

                    L1LogUtils.chatLog("[파티] {}:{} ", pc.getName(), chatText);
                    LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "파티", chatText)));
                    break;
                case 15:
                    if (pc.getClanId() != 0) { // 혈맹 소속중
                        L1PcInstance allianceLeader = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());

                        if (allianceLeader != null) {
                            String TargetClanName = allianceLeader.getClanName();
                            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                            L1Clan TargetClan = L1World.getInstance().getClan(TargetClanName);

                            if (clan != null) {
                                // 원래는 온라인중인 자기의 혈원과 온라인중인 동맹의 혈원한테 쏘아주어야함. (현재는 대처용)
                                for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                    int AllianceClan = listner.getClanId();
                                    if (pc.getClanId() == AllianceClan) {
                                        listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 15));
                                    }
                                } // 자기혈맹 전송용
                                for (L1PcInstance alliancelistner : TargetClan.getOnlineClanMember()) {

                                    int AllianceClan = alliancelistner.getClanId();
                                    if (pc.getClanId() == AllianceClan) {
                                        alliancelistner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 15));
                                    }
                                } // 동맹혈맹 전송용
                            }
                        }
                    }
                    break;
                case 17:
                    if (pc.getClanId() != 0) { // 혈맹 소속중
                        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                        if (clan != null && (pc.isCrown() && pc.getId() == clan.getLeaderId())) {
                            for (L1PcInstance listner : clan.getOnlineClanMember()) {
                                if (!listner.getExcludingList().contains(pc.getName())) {
                                    listner.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, 17));
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

            if (!pc.isGm()) {
                pc.getChatCheck().checkChatInterval();
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private void chatWorld(L1PcInstance pc, String chatText, int chatType) {
        if (pc.isGm() || pc.getAccessLevel() == 1) {
            if (chatType == 3) {
                L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, chatType));

                L1LogUtils.chatLog("[전체] {} : {}", pc.getName(), chatText);
                LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "전체", chatText)));
            } else if (chatType == 12) {
                L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, 3));
                L1LogUtils.chatLog("[장사] {} : {}", pc.getName(), chatText);
                LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "장사", chatText)));
            }
        } else if (pc.getLevel() >= CodeConfig.GLOBAL_CHAT_LEVEL) {
            if (L1World.getInstance().isWorldChatEnable()) {

                if (CodeConfig.DIRTY_CHAT_TYPE == 1) {
                    chatText = L1TxtChat.getInstance().cleanChat(chatText);
                }

                if (pc.getFood() >= 12) {
                    if (chatType == 3) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.FOOD, pc.getFood()));
                        L1LogUtils.chatLog("[전체] {} : {}", pc.getName(), chatText);
                        LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "전체", chatText)));
                    } else if (chatType == 12) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.FOOD, pc.getFood()));
                        L1LogUtils.chatLog("[장사] {} : {}", pc.getName(), chatText);
                        LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(pc.getName(), "장사", chatText)));
                    }

                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.FOOD, pc.getFood()));

                    for (L1PcInstance user : L1World.getInstance().getAllPlayers()) {
                        if (!user.getExcludingList().contains(pc.getName())) {
                            if (user.isShowTradeChat() && chatType == 12) {
                                user.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, chatType));
                            } else if (user.isShowWorldChat() && chatType == 3) {
                                user.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_MSG, chatType));
                            }
                        }
                    }

                    if (CodeConfig.DIRTY_CHAT_TYPE == 0) {
                        if (L1TxtChat.getInstance().containsDirtyChat(chatText)) {
                            GmCommands.getInstance().handleCommands(String.format("채금 %s " + CodeConfig.DIRTY_CHAT_TIME, pc.getName()));
                        }
                    }

                } else {
                    pc.sendPackets(new S_ServerMessage(462));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(510));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(195, String.valueOf(CodeConfig.GLOBAL_CHAT_LEVEL)));
        }
    }
}
