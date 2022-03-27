package ks.model.noDelay;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoDelayCheck {

    private static final Logger logger = LogManager.getLogger();

    private final Map<Integer, SkillDelayCheck> checkMap = new HashMap<>();

    public void startCheck(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
        checkMap.put(skillId, new SkillDelayCheck(skillId, skill.getReuseDelay()));
    }

    public boolean allDelayCheck() {
        for (SkillDelayCheck check : checkMap.values()) {
            if (check.isDelay()) {
                return true;
            }
        }

        return false;
    }

    public boolean isDelay(int skillId) {
        SkillDelayCheck check = checkMap.get(skillId);

        if (check == null) {
            return false;
        } else {
            return check.isDelay();
        }
    }

    private static class SkillDelayCheck {

        private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        private int skillId;
        private final long skillEndTime;

        public SkillDelayCheck(int skillId, long reuseTime) {
            skillEndTime = System.currentTimeMillis() + reuseTime;
            this.skillId = skillId;
        }

        public boolean isDelay() {
            long currentTime = System.currentTimeMillis();

            boolean result = skillEndTime > currentTime;

            if (!result) {
                logger.debug("currentTime : {}, endTime : {}, r : {}",
                        format.format(new Date(currentTime)),
                        format.format(new Date(skillEndTime)),
                        skillEndTime - currentTime);
            }


            return result;
        }

        public int getSkillId() {
            return skillId;
        }

        public void setSkillId(int skillId) {
            this.skillId = skillId;
        }
    }
}
