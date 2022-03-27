package ks.model.skill.magic;

import ks.model.L1Character;
import ks.model.attack.magic.L1MagicRun;

import java.util.ArrayList;
import java.util.List;

public class L1SkillRequest {
    private L1Character targetCharacter;
    private L1Character skillUseCharacter;
    private List<L1Character> targetList = new ArrayList<>();

    private int skillId;
    private int targetId;
    private int targetX;
    private int targetY;
    private int duration;
    private L1MagicRun magic;
    private int targetItemId;
    private boolean success = true;
    private int damage;

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public L1Character getTargetCharacter() {
        return targetCharacter;
    }

    public void setTargetCharacter(L1Character targetCharacter) {
        this.targetCharacter = targetCharacter;
    }

    public L1Character getSkillUseCharacter() {
        return skillUseCharacter;
    }

    public void setSkillUseCharacter(L1Character skillUseCharacter) {
        this.skillUseCharacter = skillUseCharacter;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public L1MagicRun getMagic() {
        return magic;
    }

    public void setMagic(L1MagicRun magic) {
        this.magic = magic;
    }

    public int getTargetItemId() {
        return targetItemId;
    }

    public void setTargetItemId(int targetItemId) {
        this.targetItemId = targetItemId;
    }

    public List<L1Character> getTargetList() {
        return targetList;
    }

    public void setTargetList(List<L1Character> targetList) {
        this.targetList = targetList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
