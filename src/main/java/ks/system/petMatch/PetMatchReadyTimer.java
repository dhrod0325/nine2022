package ks.system.petMatch;

import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;

import java.util.Timer;
import java.util.TimerTask;

public class PetMatchReadyTimer extends TimerTask {
    private final PetMatch petMatch;
    private final int petMatchNo;
    private final L1PcInstance pc;
    private final L1PetInstance pet;

    public PetMatchReadyTimer(PetMatch petMatch, int petMatchNo, L1PcInstance pc, L1PetInstance pet) {
        this.petMatch = petMatch;
        this.petMatchNo = petMatchNo;
        this.pc = pc;
        this.pet = pet;
    }

    public void begin() {
        Timer timer = new Timer();
        timer.schedule(this, 3000);
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                Thread.sleep(1000L);

                if (pc == null || pet == null) {
                    cancel();

                    return;
                }

                if (pc.isTeleport())
                    continue;

                if (petMatch.setPetMatchPc(petMatchNo, pc, pet) == PetMatch.STATUS_PLAYING) {
                    petMatch.startPetMatch(petMatchNo);
                }

                cancel();

                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
