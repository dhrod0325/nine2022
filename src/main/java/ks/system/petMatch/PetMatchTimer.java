package ks.system.petMatch;

import ks.model.Broadcaster;
import ks.model.instance.L1PetInstance;
import ks.packets.serverpackets.S_SkillSound;

import java.util.Timer;
import java.util.TimerTask;

public class PetMatchTimer extends TimerTask {
    private final PetMatch petMatch;

    private final L1PetInstance pet1;
    private final L1PetInstance pet2;

    private final int petMatchNo;

    private int counter = 0;

    public PetMatchTimer(PetMatch petMatch, L1PetInstance pet1, L1PetInstance pet2, int petMatchNo) {
        this.petMatch = petMatch;
        this.pet1 = pet1;
        this.pet2 = pet2;
        this.petMatchNo = petMatchNo;
    }

    public void begin() {
        Timer timer = new Timer();
        timer.schedule(this, 0);
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                Thread.sleep(3000);

                counter++;

                if (pet1 == null || pet2 == null) {
                    cancel();

                    return;
                }

                if (pet1.isDead() || pet2.isDead()) {
                    int winner;

                    if (!pet1.isDead() && pet2.isDead()) {
                        winner = 1;
                        pet1.setCurrentPetStatus(3);
                        Broadcaster.broadcastPacket(pet1, new S_SkillSound(pet1.getId(), 6354));
                    } else if (pet1.isDead() && !pet2.isDead()) {
                        winner = 2;
                        pet2.setCurrentPetStatus(3);
                        Broadcaster.broadcastPacket(pet2, new S_SkillSound(pet2.getId(), 6354));
                    } else {
                        pet1.setCurrentPetStatus(3);
                        pet2.setCurrentPetStatus(3);
                        winner = 3;
                    }

                    petMatch.endPetMatch(petMatchNo, winner);

                    cancel();

                    return;
                }

                if (counter == 100) { // 5분 지나도 끝나지 않는 경우는 무승부
                    petMatch.endPetMatch(petMatchNo, 3);
                    cancel();
                    return;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
