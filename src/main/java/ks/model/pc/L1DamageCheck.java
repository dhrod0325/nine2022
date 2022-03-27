package ks.model.pc;

import ks.model.instance.L1ItemInstance;
import ks.util.log.L1LogUtils;

public class L1DamageCheck {
    private long lastAttackTime;
    private long firstAttackTime;
    private int attackCount;
    private int totalDamage;
    private long totalHitSpeed;
    private int minDamage;
    private int maxDamage;
    private long hitSpeed;
    private int speedCount;
    private int lastSkillId;

    public void calc(int dmg) {
        int diffSecond = (int) ((System.currentTimeMillis() - lastAttackTime) / 1000);

        if (diffSecond > 3) {
            lastAttackTime = System.currentTimeMillis();
            minDamage = 0;
            maxDamage = 0;
            attackCount = 0;
            totalDamage = 0;
            hitSpeed = 0;
            totalHitSpeed = 0;
            speedCount = 0;
            lastSkillId = 0;
        }

        hitSpeed = System.currentTimeMillis() - lastAttackTime;

        if (minDamage == 0) {
            minDamage = dmg;
        }

        if (dmg <= minDamage) {
            minDamage = dmg;
        }

        if (dmg > maxDamage) {
            maxDamage = dmg;
        }

        if (attackCount == 0) {
            firstAttackTime = System.currentTimeMillis();
        }

        attackCount++;

        totalDamage += dmg;

        if (hitSpeed > 0) {
            totalHitSpeed += hitSpeed;
            speedCount++;
        }

        lastAttackTime = System.currentTimeMillis();
    }

    public int getAvgDmage() {
        try {
            return totalDamage / attackCount;
        } catch (Exception e) {
            return 0;
        }
    }

    public long getAvgSpeed() {
        try {
            return totalHitSpeed / speedCount;
        } catch (Exception e) {
            return 0;
        }
    }

    public void damageCheck(L1PcInstance pc, int dmg) {
        damageCheck(pc, dmg, null);
    }

    public void damageCheck(L1PcInstance pc, int dmg, String title) {
        calc(dmg);

        String msg2 = "";
        String msg3 = "";
        String msg4 = "";
        String msg5 = "";

        if (title == null) {
            if (pc.getWeapon() != null) {
                L1ItemInstance weapon = pc.getWeapon();
                title = L1LogUtils.logItemName(weapon);
            }
        }

        pc.sendPackets("\\fT[" + title + "]");

        msg2 += "\\fU현재대미지:[" + dmg + "] ";
        msg2 += "\\fT타격:[" + attackCount + "회] ";
        msg2 += "\\fV누적:[" + totalDamage + "] ";

        msg3 += "\\fW최저대미지:[" + minDamage + "] ";
        msg3 += "\\fR최고대미지:[" + maxDamage + "] ";
        msg3 += "\\fV평균대미지:[" + getAvgDmage() + "] ";

        pc.sendPackets(msg2);
        pc.sendPackets(msg3);

        if (pc.isGm()) {
            msg4 += "\\fU공격속도:[" + hitSpeed + "] ";
            msg4 += "\\fT평균공속:[" + getAvgSpeed() + "] ";
            msg5 += "\\fT총공격시간:[" + getTotalAttackSecond() + " 초] ";

            pc.sendPackets(msg4);
            pc.sendPackets(msg5);
        }
    }

    public int getTotalAttackSecond() {
        long diffTime = lastAttackTime - firstAttackTime;

        if (diffTime == 0) {
            return 0;
        }

        return (int) (diffTime / 1000);
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public int getLastSkillId() {
        return lastSkillId;
    }

    public void setLastSkillId(int lastSkillId) {
        this.lastSkillId = lastSkillId;
    }
}
