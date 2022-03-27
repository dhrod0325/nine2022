package ks.model.pc;

import ks.util.common.random.RandomUtils;

public class L1PcHpMpRegen {
    private final L1PcInstance pc;

    public L1PcHpMpRegen(L1PcInstance pc) {
        this.pc = pc;
    }

    public void regenMpBySchedule() {
        int newMp = pc.getCurrentMp() + pc.getCurrentMpTic();

        if (!pc.isDead()) {
            pc.setCurrentMp(newMp);
        }
    }

    public void regenHpBySchedule() {
        int newHp = pc.getCurrentHp() + pc.getCurrentHpTic() + RandomUtils.nextInt(pc.getHprMaxBonus());

        if (newHp < 1) {
            newHp = 1;
        }

        if (pc.isUnderwater()) {
            newHp -= 10;

            if (newHp < 1) {
                if (pc.isGm()) {
                    newHp = 1;
                } else {
                    pc.death(null); // HP가 0이 되었을 경우는 사망한다.
                }
            }
        }

        if (pc.getMapId() == 410) {
            newHp -= 5;

            if (newHp < 1) {
                if (pc.isGm()) {
                    newHp = 1;
                } else {
                    pc.death(null);
                }
            }
        }

        if (!pc.isDead()) {
            pc.setCurrentHp(Math.min(newHp, pc.getMaxHp()));
        }
    }
}
