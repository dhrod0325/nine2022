package ks.model;

import ks.core.datatables.npc.NpcTable;
import ks.util.common.IntRange;

public class L1PetType {
    private final int baseNpcId;

    private final L1Npc baseNpcTemplate;

    private final String name;

    private final int itemIdForTaming;

    private final IntRange hpUpRange;

    private final IntRange mpUpRange;

    private final int npcIdForEvolving;

    private final int[] msgIds;

    private final int defyMsgId;

    public L1PetType(int baseNpcId, String name, int itemIdForTaming,
                     IntRange hpUpRange, IntRange mpUpRange, int npcIdForEvolving,
                     int[] msgIds, int defyMsgId) {
        this.baseNpcId = baseNpcId;
        this.baseNpcTemplate = NpcTable.getInstance().getTemplate(baseNpcId);
        this.name = name;
        this.itemIdForTaming = itemIdForTaming;
        this.hpUpRange = hpUpRange;
        this.mpUpRange = mpUpRange;
        this.npcIdForEvolving = npcIdForEvolving;
        this.msgIds = msgIds;
        this.defyMsgId = defyMsgId;
    }

    public static int getMessageNumber(int level) {
        if (50 <= level) {
            return 5;
        }
        if (48 <= level) {
            return 4;
        }
        if (36 <= level) {
            return 3;
        }
        if (24 <= level) {
            return 2;
        }
        if (12 <= level) {
            return 1;
        }
        return 0;
    }

    public int getBaseNpcId() {
        return baseNpcId;
    }

    public L1Npc getBaseNpcTemplate() {
        return baseNpcTemplate;
    }

    public String getName() {
        return name;
    }

    public int getItemIdForTaming() {
        return itemIdForTaming;
    }

    public boolean canTame() {
        return itemIdForTaming != 0;
    }

    public IntRange getHpUpRange() {
        return hpUpRange;
    }

    public IntRange getMpUpRange() {
        return mpUpRange;
    }

    public int getNpcIdForEvolving() {
        return npcIdForEvolving;
    }

    public boolean canEvolve() {
        return npcIdForEvolving != 0;
    }

    public int getMessageId(int num) {
        if (num == 0) {
            return 0;
        }
        return msgIds[num - 1];
    }

    public int getDefyMessageId() {
        return defyMsgId;
    }

}
