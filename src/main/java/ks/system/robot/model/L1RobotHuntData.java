package ks.system.robot.model;

import java.util.Date;

public class L1RobotHuntData {
    private int id;
    private Date startTime;
    private Date endTime;
    private int endCheck;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getEndCheck() {
        return endCheck;
    }

    public void setEndCheck(int endCheck) {
        this.endCheck = endCheck;
    }
}
