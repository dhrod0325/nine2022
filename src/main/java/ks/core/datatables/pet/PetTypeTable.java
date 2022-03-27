package ks.core.datatables.pet;

import ks.model.L1PetType;
import ks.util.common.IntRange;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PetTypeTable {
    private static final PetTypeTable instance = new PetTypeTable();

    private final Map<Integer, L1PetType> types = new HashMap<>();

    private final Set<String> defaultNames = new HashSet<>();

    public static PetTypeTable getInstance() {
        return instance;
    }

    public void load() {
        types.clear();
        defaultNames.clear();

        SqlUtils.query("SELECT * FROM pettypes", (rs, x) -> {
            int baseNpcId = rs.getInt("BaseNpcId");
            String name = rs.getString("Name");
            int itemIdForTaming = rs.getInt("ItemIdForTaming");
            int hpUpMin = rs.getInt("HpUpMin");
            int hpUpMax = rs.getInt("HpUpMax");
            int mpUpMin = rs.getInt("MpUpMin");
            int mpUpMax = rs.getInt("MpUpMax");
            int npcIdForEvolving = rs.getInt("NpcIdForEvolving");
            int[] msgIds = new int[5];

            for (int i = 0; i < 5; i++) {
                msgIds[i] = rs.getInt("MessageId" + (i + 1));
            }

            int defyMsgId = rs.getInt("DefyMessageId");
            IntRange hpUpRange = new IntRange(hpUpMin, hpUpMax);
            IntRange mpUpRange = new IntRange(mpUpMin, mpUpMax);

            types.put(baseNpcId, new L1PetType(baseNpcId, name, itemIdForTaming, hpUpRange, mpUpRange, npcIdForEvolving, msgIds, defyMsgId));
            defaultNames.add(name.toLowerCase());

            return null;
        });
    }

    public L1PetType get(int baseNpcId) {
        return types.get(baseNpcId);
    }

    public boolean isNameDefault(String name) {
        return defaultNames.contains(name.toLowerCase());
    }
}
