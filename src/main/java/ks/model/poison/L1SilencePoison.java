package ks.model.poison;

import ks.constants.L1SkillId;
import ks.model.L1Character;

public class L1SilencePoison extends L1Poison {
    private final L1Character target;

    private L1SilencePoison(L1Character cha) {
        target = cha;

        doInfection();
    }

    public static void doInfection(L1Character cha) {
        if (L1Poison.isNotValidTarget(cha)) {
            return;
        }

        cha.setPoison(new L1SilencePoison(cha));
    }

    private void doInfection() {
        target.setPoisonEffect(1);
        sendMessageIfPlayer(target, 310);
        target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_POISON_SILENCE, 0);
    }

    @Override
    public int getEffectId() {
        return 1;
    }

    @Override
    public void cure() {
        target.setPoisonEffect(0);
        sendMessageIfPlayer(target, 311);

        target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_POISON_SILENCE);
        target.setPoison(null);
    }
}
