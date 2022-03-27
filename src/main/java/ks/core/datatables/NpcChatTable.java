package ks.core.datatables;

import ks.constants.L1NpcConstants;
import ks.model.L1NpcChat;
import ks.util.common.SqlUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NpcChatTable {
    private static final NpcChatTable instance = new NpcChatTable();

    private final Map<Integer, L1NpcChat> npcChatAppearance = new HashMap<>();

    private final Map<Integer, L1NpcChat> npcChatDead = new HashMap<>();

    private final Map<Integer, L1NpcChat> npcChatHide = new HashMap<>();

    private final Map<Integer, L1NpcChat> npcChatGameTime = new HashMap<>();

    public static NpcChatTable getInstance() {
        return instance;
    }

    public void load() {
        fillNpcChatTable();
    }

    private void fillNpcChatTable() {
        npcChatAppearance.clear();
        npcChatDead.clear();
        npcChatHide.clear();
        npcChatGameTime.clear();

        SqlUtils.query("SELECT * FROM npcchat", (rs, i) -> {
            L1NpcChat npcChat = new L1NpcChat();
            npcChat.setNpcId(rs.getInt("npc_id"));
            npcChat.setChatTiming(rs.getInt("chat_timing"));
            npcChat.setStartDelayTime(rs.getInt("start_delay_time"));
            npcChat.setChatId1(rs.getString("chat_id1"));
            npcChat.setChatId2(rs.getString("chat_id2"));
            npcChat.setChatId3(rs.getString("chat_id3"));
            npcChat.setChatId4(rs.getString("chat_id4"));
            npcChat.setChatId5(rs.getString("chat_id5"));
            npcChat.setChatInterval(rs.getInt("chat_interval"));
            npcChat.setShout(rs.getBoolean("is_shout"));
            npcChat.setWorldChat(rs.getBoolean("is_world_chat"));
            npcChat.setRepeat(rs.getBoolean("is_repeat"));
            npcChat.setRepeatInterval(rs.getInt("repeat_interval"));
            npcChat.setGameTime(rs.getInt("game_time"));

            if (npcChat.getChatTiming() == L1NpcConstants.CHAT_TIMING_APPEARANCE) {
                npcChatAppearance.put(npcChat.getNpcId(), npcChat);
            } else if (npcChat.getChatTiming() == L1NpcConstants.CHAT_TIMING_DEAD) {
                npcChatDead.put(npcChat.getNpcId(), npcChat);
            } else if (npcChat.getChatTiming() == L1NpcConstants.CHAT_TIMING_HIDE) {
                npcChatHide.put(npcChat.getNpcId(), npcChat);
            } else if (npcChat.getChatTiming() == L1NpcConstants.CHAT_TIMING_GAME_TIME) {
                npcChatGameTime.put(npcChat.getNpcId(), npcChat);
            }

            return null;
        });
    }

    public L1NpcChat getTemplateAppearance(int i) {
        return npcChatAppearance.get(i);
    }

    public L1NpcChat getTemplateDead(int i) {
        return npcChatDead.get(i);
    }

    public L1NpcChat getTemplateHide(int i) {
        return npcChatHide.get(i);
    }

    public L1NpcChat getTemplateGameTime(int i) {
        return npcChatGameTime.get(i);
    }

    public Collection<L1NpcChat> getAllGameTime() {
        return npcChatGameTime.values();
    }
}
