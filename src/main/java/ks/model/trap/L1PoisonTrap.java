package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;
import ks.model.poison.L1DamagePoison;
import ks.model.poison.L1ParalysisPoison;
import ks.model.poison.L1SilencePoison;

public class L1PoisonTrap extends L1Trap {
    private final String type;

    private final int delay;

    private final int time;

    private final int damage;

    public L1PoisonTrap(TrapStorage storage) {
        super(storage);

        type = storage.getString("poisonType");
        delay = storage.getInt("poisonDelay");
        time = storage.getInt("poisonTime");
        damage = storage.getInt("poisonDamage");
    }

    @Override
    public void onTrod(L1PcInstance from, L1TrapInstance trap) {
        sendEffect(trap);

        if (type.equals("d")) {
            L1DamagePoison.doInfection(from, from, time, damage);
        } else if (type.equals("s")) {
            L1SilencePoison.doInfection(from);
        } else if (type.equals("p")) {
            L1ParalysisPoison.doInfection(from, delay, time);
        }
    }
}
