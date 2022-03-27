package ks.packets.clientpackets;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.event.L1ChatEvent;
import ks.constants.L1SkillId;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.log.L1LogUtils;

public class C_ChatWhisper extends ClientBasePacket {
    public C_ChatWhisper(byte[] data, L1Client client) {
        super(data);

        String targetName = readS();
        String text = readS();

        L1PcInstance whisperFrom = client.getActiveChar();

        if (L1World.getInstance().getNpcShop(targetName)) {
            whisperFrom.sendPackets(new S_SystemMessage("-> (" + targetName + ") " + text));
            return;
        }

        if (text.length() > 25) {
        }

        if (whisperFrom.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
            whisperFrom.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
            return;
        }

        if (whisperFrom.getLevel() < CodeConfig.WHISPER_CHAT_LEVEL) {
            whisperFrom.sendPackets(new S_ServerMessage(404, String.valueOf(CodeConfig.WHISPER_CHAT_LEVEL)));
            return;
        }

        L1PcInstance whisperTo = L1World.getInstance().getPlayer(targetName);

        boolean targetIsGm = "메티스".equalsIgnoreCase(targetName) || "미소피아".equalsIgnoreCase(targetName);

        if (whisperFrom.getAccessLevel() == 0) {
            if (targetIsGm) {
                whisperFrom.sendPackets("[문의]:편지 홈페이지(https://www.linclassic.me)");
                whisperFrom.sendPackets("[문의]:편지 내용없이 상담요청 금지");
                whisperFrom.sendPackets("[문의]:귓말답변 X 후원문의 : .후원");
                whisperFrom.sendPackets("[문의]:장비구매문의 답변없이 바로 채금 30분");
            }

            if (whisperTo == null) {
                if (!targetIsGm) {
                    whisperFrom.sendPackets(new S_ServerMessage(73, targetName));
                }

                L1LogUtils.chatLog("[못받은귓말] {} -> {} : {}", whisperFrom.getName(), targetName, text);
                return;
            }

            if (targetIsGm) {
                whisperTo.sendPackets(new S_ChatPacket(whisperFrom, text, L1Opcodes.S_OPCODE_WHISPERCHAT, 16));
                L1LogUtils.chatLog("[못받은귓말] {} -> {} : {}", whisperFrom.getName(), whisperTo.getName(), text);
                return;
            }
        }

        // 월드에 없는 경우
        if (whisperTo == null) {
            if (!targetName.equals("메티스")) {
                whisperFrom.sendPackets(new S_ServerMessage(73, targetName));
                return;
            } else {
                whisperTo = new L1PcInstance();
                whisperTo.setName("메티스");
            }
        }

        if (whisperTo.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
            whisperFrom.sendPackets(new S_SystemMessage("상대방이 채팅금지 중 입니다."));
            return;
        }

        // 자기 자신에 대한 wis의 경우
        if (whisperTo.equals(whisperFrom)) {
            return;
        }

        if (!whisperFrom.isGm()) {
            // 차단되고 있는 경우
            if (whisperTo.getExcludingList().contains(whisperFrom.getName())) {
                whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo.getName()));
                return;
            }

            if (!whisperTo.isCanWhisper()) {
                whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo.getName()));
                return;
            }
        }

        send(whisperFrom, whisperTo, text);
    }

    public static void send(L1PcInstance whisperFrom, L1PcInstance whisperTo, String text) {
        whisperFrom.sendPackets(new S_ChatPacket(whisperTo, text, L1Opcodes.S_OPCODE_MSG, 9));
        whisperTo.sendPackets(new S_ChatPacket(whisperFrom, text, L1Opcodes.S_OPCODE_WHISPERCHAT, 16));
        LineageAppContext.getCtx().publishEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource(whisperFrom.getName(), "귓말", text, whisperTo.getName())));
    }
}
