package ks.core.datatables.npcTalk;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NpcTalkTable {
    private final List<NpcTalk> list = new ArrayList<>();

    public static NpcTalkTable getInstance() {
        return LineageAppContext.getBean(NpcTalkTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<NpcTalk> selectList() {
        return SqlUtils.query("SELECT * FROM npc_talk", new BeanPropertyRowMapper<>(NpcTalk.class));
    }

    public NpcTalk findByNpcId(int npcId) {
        for (NpcTalk s : list) {
            if (s.getNpcId() == npcId) {
                return s;
            }
        }

        return null;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc, NpcTalk talk) {
        String name = npc.getName();
        String[] names = name.split("\\^");

        String npcName = "[" + names[0] + "] ";

        if (names.length == 2) {
            npcName += names[1];
        }

        pc.sendPackets(new S_ShowCCHtml(npc.getId(), "cc_n" + talk.getType(), npcName, talk.getTalk1(), talk.getTalk2()));
    }

    public List<NpcTalk> getList() {
        return list;
    }
}
