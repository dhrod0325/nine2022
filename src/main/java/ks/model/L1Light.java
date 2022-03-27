package ks.model;

import ks.constants.L1SkillId;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Light;

import java.util.ArrayList;
import java.util.List;

public class L1Light {
    private final L1Character character;
    private int chaLightSize = 0;
    private int ownLightSize = 0;

    public L1Light(L1Character character) {
        this.character = character;
    }

    public int getChaLightSize() {
        if (character.isInvisible()) {
            return 0;
        }
        return chaLightSize;
    }

    public void setChaLightSize(int i) {
        chaLightSize = i;
    }

    public int getOwnLightSize() {
        return ownLightSize;
    }

    public void setOwnLightSize(int i) {
        ownLightSize = i;
    }

    public void turnOnOffLight() {
        if (character == null)
            return;

        int lightSize = 0;

        if (character instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) character;
            lightSize = npc.getLightSize();
        }

        if (character.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.LIGHT)) {
            lightSize = 14;
        }

        final int TYPE_ETC_ITEM = 0, TYPE_LIGHT = 2;

        List<L1ItemInstance> items = character.getInventory().getItems();

        for (L1ItemInstance item : new ArrayList<>(items)) {
            if (item.getItem().getType2() == TYPE_ETC_ITEM && item.getItem().getType() == TYPE_LIGHT) {
                int itemlightSize = item.getItem().getLightRange();
                if (itemlightSize != 0 && item.isNowLighting()) {
                    if (itemlightSize > lightSize) {
                        lightSize = itemlightSize;
                    }
                }
            }
        }

        if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;
            pc.sendPackets(new S_Light(pc.getId(), lightSize));
        }

        if (!character.isInvisible())
            Broadcaster.broadcastPacket(character, new S_Light(character.getId(), lightSize));

        setOwnLightSize(lightSize);
        setChaLightSize(lightSize);
    }
}
