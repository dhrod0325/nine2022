package ks.model.skill.magic.skills;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.skill.magic.L1Skill;
import ks.model.skill.magic.L1SkillRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class L1SkillAdapter implements L1Skill {
    protected final Logger logger = LogManager.getLogger(getClass());

    protected int skillId;

    private int runSkillState;

    public L1SkillAdapter(int skillId) {
        this.skillId = skillId;
    }

    @Override
    public int getRunSkillState() {
        return runSkillState;
    }

    public void setRunSkillState(int runSkillState) {
        this.runSkillState = runSkillState;
    }

    @Override
    public void runSkill(L1SkillRequest request) {
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
    }

    @Override
    public void sendIcon(L1Character targetCharacter, int duration) {
    }

    @Override
    public void sendGrfx(L1SkillRequest request, boolean isSkillAction) {
    }

    @Override
    public int interceptorDuration(L1SkillRequest request, int duration) {
        return duration;
    }

    @Override
    public int interceptDamage(L1SkillRequest request, int dmg) {
        return dmg;
    }

    @Override
    public boolean interceptProbability(L1SkillRequest request, boolean success) {
        return success;
    }

    public L1Skills getSkill() {
        return SkillsTable.getInstance().getTemplate(skillId);
    }

    @Override
    public void completeSkill(L1SkillRequest request) {

    }

    @Override
    public void preSkill(L1SkillRequest request) {

    }
}
