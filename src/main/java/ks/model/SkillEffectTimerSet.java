package ks.model;

import ks.model.skill.L1SkillStop;
import ks.model.skill.L1SkillTimer;
import ks.model.skill.L1SkillTimerCreator;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillEffectTimerSet {
    private static final Logger logger = LogManager.getLogger(SkillEffectTimerSet.class);

    private final Map<Integer, L1SkillTimer> skillEffect = new HashMap<>();

    private final L1Character cha;

    public SkillEffectTimerSet(L1Character cha) {
        this.cha = cha;
    }

    public Map<Integer, L1SkillTimer> getSkillEffect() {
        return skillEffect;
    }

    private void addSkillEffect(int skillId, int timeMillis) {
        L1SkillTimer timer = null;

        if (timeMillis > 0) {
            timer = L1SkillTimerCreator.create(cha, skillId, timeMillis);
            timer.begin();
        }

        skillEffect.put(skillId, timer);

        L1LogUtils.skillLog("타이머 설정 -캐릭명:{} 스킬아이디:{}", cha.getName(), skillId);
    }

    public void setSkillEffect(int skillId, int timeMillis) {
        if (hasSkillEffect(skillId)) {
            int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

            if (remainingTimeMills >= 0 && (remainingTimeMills < timeMillis || timeMillis == 0)) {
                killSkillEffectTimer(skillId);
                addSkillEffect(skillId, timeMillis);
            }
        } else {
            addSkillEffect(skillId, timeMillis);
        }
    }

    public void removeSkillEffect(List<Integer> skills) {
        for (Integer skill : skills) {
            removeSkillEffect(skill);
        }
    }

    public void removeSkillEffect(Integer... skillId) {
        for (Integer skill : skillId) {
            removeSkillEffect(skill);
        }
    }

    public void removeSkillEffect(int skillId) {
        boolean key = skillEffect.containsKey(skillId);

        if (key) {
            L1SkillTimer timer = skillEffect.remove(skillId);

            if (timer != null) {
                timer.end();
            } else {
                L1SkillStop.stopSkill(cha, skillId);
            }

            L1LogUtils.skillLog("타이머 삭제 - 캐릭명:{} 스킬아이디:{}", cha.getName(), skillId);
        }
    }

    public void killSkillEffectTimer(int skillId) {
        L1SkillTimer timer = skillEffect.remove(skillId);

        if (timer != null) {
            timer.kill();
        }
    }

    public void clearSkillEffectTimer() {
        Collection<L1SkillTimer> list = skillEffect.values();

        try {
            for (L1SkillTimer timer : list) {
                if (timer != null) {
                    timer.kill();
                }
            }
        } catch (NullPointerException e) {
            logger.warn("clearSkillEffectTimer is null");
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public boolean hasSkillEffect(int skillId) {
        return skillEffect.containsKey(skillId);
    }

    public boolean hasSkillEffect(List<Integer> skillIds) {
        for (Integer skillId : skillIds) {
            if (skillEffect.containsKey(skillId)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSkillEffect(Integer... skillIds) {
        for (Integer skillId : skillIds) {
            if (skillEffect.containsKey(skillId)) {
                return true;
            }
        }

        return false;
    }

    public int getSkillEffectTimeSec(int skillId) {
        L1SkillTimer timer = skillEffect.get(skillId);

        if (timer == null) {
            return -1;
        }

        return timer.getRemainingTime();
    }
}
