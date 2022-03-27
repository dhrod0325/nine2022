package ks.system.dogFight;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Npc;
import ks.model.instance.L1MonsterInstance;
import ks.packets.serverpackets.S_EffectLocation;
import ks.util.common.random.RandomUtils;

public class L1DogFightInstance extends L1MonsterInstance {
    public L1DogFightInstance(L1Npc template) {
        super(template);
    }

    private int betCount;
    private double allotmentPercentage;

    public int getBetCount() {
        return betCount;
    }

    public void setBetCount(int betCount) {
        this.betCount = betCount;
    }

    public double getAllotmentPercentage() {
        return allotmentPercentage;
    }

    public void setAllotmentPercentage(double allotmentPercentage) {
        this.allotmentPercentage = allotmentPercentage;
    }

    @Override
    public double onAttack(L1Character target, double damage) {
        if (RandomUtils.isWinning(100, 20)) {
            damage *= 1.2;
            Broadcaster.wideBroadcastPacket(target, new S_EffectLocation(target.getLocation(), 6532));
        }

        return damage;
    }
}
