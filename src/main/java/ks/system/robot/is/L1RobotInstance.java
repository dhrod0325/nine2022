package ks.system.robot.is;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.system.robot.L1RobotInventory;
import ks.system.robot.L1RobotTable;
import ks.system.robot.L1RobotType;
import ks.system.robot.ai.L1RobotAi;
import ks.system.robot.model.L1RobotTpl;

public class L1RobotInstance extends L1PcInstance {
    private final L1RobotInventory inventory;
    private long aiSleepTime = 0;
    private L1RobotType robotType;
    private L1RobotTpl tpl;
    private L1RobotAi ai;

    public L1RobotInstance() {
        super();

        setRobotType(L1RobotType.NORMAL);
        inventory = new L1RobotInventory(this);
    }

    @Override
    public void curePoison() {
        super.curePoison();
        Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 192));
    }

    @Override
    public L1RobotInventory getInventory() {
        return inventory;
    }

    @Override
    public synchronized void receiveDamage(L1Character attacker, int damage) {
        damage = 1;

        super.receiveDamage(attacker, damage);

        if (ai != null)
            ai.receiveDamage(attacker, damage);
    }

    public void toAI(long time) {
        if (ai != null)
            ai.toAI(time);
    }

    @Override
    public void logout() {
        if (robotType == L1RobotType.AUTO_CREATE) {
            L1RobotTable.getInstance().getCachedNameList().add(getName());
        }

        super.logout();
        L1World.getInstance().removeObject(this);
    }

    public L1RobotType getRobotType() {
        return robotType;
    }

    public void setRobotType(L1RobotType robotType) {
        this.robotType = robotType;
    }

    public L1RobotTpl getTpl() {
        return tpl;
    }

    public void setTpl(L1RobotTpl tpl) {
        this.tpl = tpl;
    }

    public void setAi(L1RobotAi ai) {
        this.ai = ai;
    }

    public long getAiSleepTime() {
        return aiSleepTime;
    }

    public void setAiSleepTime(long millSecond) {
        this.aiSleepTime = System.currentTimeMillis() + millSecond;
    }
}
