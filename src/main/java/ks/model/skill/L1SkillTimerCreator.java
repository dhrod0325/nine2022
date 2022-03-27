package ks.model.skill;

import ks.app.config.prop.CodeConfig;
import ks.model.L1Character;

public class L1SkillTimerCreator {
    public static L1SkillTimer create(L1Character cha, int skillId, int timeMillis) {
        if (CodeConfig.SKILL_TIMER_IMPL_TYPE == 1) {
            return new L1SkillTimerTimerImpl(cha, skillId, timeMillis);
        } else if (CodeConfig.SKILL_TIMER_IMPL_TYPE == 2) {
            return new L1SkillTimerThreadImpl(cha, skillId, timeMillis);
        }

        return new L1SkillTimerTimerImpl(cha, skillId, timeMillis);
    }
}