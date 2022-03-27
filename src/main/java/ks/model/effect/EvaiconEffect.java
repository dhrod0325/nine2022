package ks.model.effect;

import ks.model.L1ArmorSetEffect;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillIconBlessOfEva;

public class EvaiconEffect implements L1ArmorSetEffect {
    public void giveEffect(L1PcInstance pc) {
        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), -1));
    }

    public void cancelEffect(L1PcInstance pc) {
        pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
    }
}
