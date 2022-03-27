package ks.model.pc.hackCheck.speedHack.spr;

import ks.constants.L1SkillId;
import ks.model.pc.L1PcInstance;

import java.util.Date;
import java.util.Objects;

public class SprCheck {
    private int charId;
    private int spr;
    private int act;
    private int interval;
    private int weaponId;

    private boolean haste;
    private boolean brave;
    private boolean fastmove;
    private boolean elfbrave;
    private boolean thirdspeed;
    private boolean dancing;

    private Date regDate;

    private long rinterval;

    private int rightInterval;

    public SprCheck(L1PcInstance pc, int act, int interval) {
        this.charId = pc.getId();
        this.spr = pc.getGfxId().getTempCharGfx();
        this.act = act;
        this.interval = interval;
        this.weaponId = pc.getWeaponInfo().getWeaponId();

        setHaste(pc.isHaste());
        setBrave(pc.isBrave());
        setFastmove(pc.isFastMovable());
        setElfbrave(pc.isElfBrave());
        setThirdspeed(pc.isThirdSpeed());
        setDancing(pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DANCING_BLADES));
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public int getSpr() {
        return spr;
    }

    public void setSpr(int spr) {
        this.spr = spr;
    }

    public int getAct() {
        return act;
    }

    public void setAct(int act) {
        this.act = act;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }

    public boolean isHaste() {
        return haste;
    }

    public void setHaste(boolean haste) {
        this.haste = haste;
    }

    public boolean isBrave() {
        return brave;
    }

    public void setBrave(boolean brave) {
        this.brave = brave;
    }

    public boolean isFastmove() {
        return fastmove;
    }

    public void setFastmove(boolean fastmove) {
        this.fastmove = fastmove;
    }

    public boolean isElfbrave() {
        return elfbrave;
    }

    public void setElfbrave(boolean elfbrave) {
        this.elfbrave = elfbrave;
    }

    public boolean isThirdspeed() {
        return thirdspeed;
    }

    public void setThirdspeed(boolean thirdspeed) {
        this.thirdspeed = thirdspeed;
    }

    public boolean isDancing() {
        return dancing;
    }

    public void setDancing(boolean dancing) {
        this.dancing = dancing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SprCheck sprCheck = (SprCheck) o;

        return charId == sprCheck.charId &&
                spr == sprCheck.spr &&
                act == sprCheck.act &&
                weaponId == sprCheck.weaponId &&
                haste == sprCheck.haste &&
                brave == sprCheck.brave &&
                fastmove == sprCheck.fastmove &&
                elfbrave == sprCheck.elfbrave &&
                thirdspeed == sprCheck.thirdspeed &&
                dancing == sprCheck.dancing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(charId, spr, act, weaponId, haste, brave, fastmove, elfbrave, thirdspeed, dancing);
    }

    public int getRightInterval() {
        return rightInterval;
    }

    public void setRightInterval(int rightInterval) {
        this.rightInterval = rightInterval;
    }

    public long getRinterval() {
        return rinterval;
    }

    public void setRinterval(long rinterval) {
        this.rinterval = rinterval;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
}
