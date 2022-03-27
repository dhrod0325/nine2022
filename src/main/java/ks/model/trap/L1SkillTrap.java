package ks.model.trap;

import ks.core.storage.TrapStorage;
import ks.model.instance.L1TrapInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;

public class L1SkillTrap extends L1Trap {
    private final int skillId;

    private final int skillTimeSeconds;

    public L1SkillTrap(TrapStorage storage) {
        super(storage);

        skillId = storage.getInt("skillId");
        skillTimeSeconds = storage.getInt("skillTimeSeconds");
    }

    @Override
    public void onTrod(L1PcInstance target, L1TrapInstance trap) {
        sendEffect(trap);

        new L1SkillUse(target, skillId, target.getId(), target.getX(), target.getY(), skillTimeSeconds).run();
    }
}
