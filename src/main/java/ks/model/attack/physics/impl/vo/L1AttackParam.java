package ks.model.attack.physics.impl.vo;

public class L1AttackParam {
    private int leverage = 10;

    private int damage;

    private boolean hitUp;
    private int hitRate;

    private boolean critical;
    private boolean hitCheck = true;
    private int actId;

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean isHitUp() {
        return hitUp;
    }

    public void setHitUp(boolean hitUp) {
        this.hitUp = hitUp;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getActId() {
        return actId;
    }

    public void setActId(int actId) {
        this.actId = actId;
    }

    public int getLeverage() {
        return leverage;
    }

    public void setLeverage(int leverage) {
        this.leverage = leverage;
    }

    public int getHitRate() {
        return hitRate;
    }

    public void setHitRate(int hitRate) {
        this.hitRate = hitRate;
    }

    public boolean isHitCheck() {
        return hitCheck;
    }

    public void setHitCheck(boolean hitCheck) {
        this.hitCheck = hitCheck;
    }
}
