package ks.core.datatables.npc;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1Npc;
import ks.model.instance.L1NpcInstance;
import ks.util.L1InstanceFactory;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NpcTable {
    private final Map<Integer, L1Npc> npcMap = new HashMap<>();

    private final Map<String, Integer> familyTypes = new HashMap<>();

    public static NpcTable getInstance() {
        return LineageAppContext.getBean(NpcTable.class);
    }

    @LogTime
    public void load() {
        familyTypes.clear();
        familyTypes.putAll(selectFamilyList());

        npcMap.clear();
        npcMap.putAll(selectNpcList());
    }


    private Map<Integer, L1Npc> selectNpcList() {
        Map<Integer, L1Npc> result = new HashMap<>();

        List<L1Npc> list = SqlUtils.query("SELECT * FROM npc", new NpcRowMapper(familyTypes));

        for (L1Npc npc : list) {
            result.put(npc.getNpcId(), npc);
        }

        return result;
    }

    public L1Npc getTemplate(int id) {
        return npcMap.get(id);
    }

    public L1NpcInstance newNpcInstance(int npcId) throws Exception {
        L1Npc npc = getTemplate(npcId);

        if (npc == null) {
            throw new Exception(String.format("NpcTemplate: %d not found", npcId));
        }

        return L1InstanceFactory.createInstance(npc);
    }

    public Map<String, Integer> selectFamilyList() {
        Map<String, Integer> familyTypes = new HashMap<>();

        List<String> list = SqlUtils.query("select distinct(family) as family from npc WHERE NOT trim(family) =''", (rs, i) -> rs.getString("family"));

        int id = 0;

        for (String s : list) {
            familyTypes.put(s, id++);
        }

        return familyTypes;
    }

    public int findNpcIdByNameWithoutSpace(String name) {
        for (L1Npc npc : npcMap.values()) {
            if (npc.getName().replace(" ", "").equals(name)) {
                return npc.getNpcId();
            }
        }
        return 0;
    }

    public int findNpcIdByNameId(String nameId) {
        for (L1Npc npc : npcMap.values()) {
            if (npc.getNameId().equals(nameId)) {
                return npc.getNpcId();
            }
        }
        return 0;
    }

    public Map<String, Integer> getFamilyTypes() {
        return familyTypes;
    }
}
