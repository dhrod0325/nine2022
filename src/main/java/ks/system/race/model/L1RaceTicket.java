package ks.system.race.model;

public class L1RaceTicket {

    private int itemObjId;
    private int round;
    private double allotmentPercentage;
    private int victory;
    private int runnerNum;
    private int runnerNpcId;

    public int getItemObjId() {
        return itemObjId;
    }

    public void setItemObjId(int i) {
        itemObjId = i;
    }

    public void setAllotmentPercentage(double allotment_percentage) {
        this.allotmentPercentage = allotment_percentage;
    }

    public double getAllotmentPercentage() {
        return allotmentPercentage;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public void setVictory(int victory) {
        this.victory = victory;
    }

    public int getVictory() {
        return victory;
    }

    public void setRunnerNum(int runnerNum) {
        this.runnerNum = runnerNum;
    }

    public int getRunnerNum() {
        return runnerNum;
    }

    public int getRunnerNpcId() {
        return runnerNpcId;
    }

    public void setRunnerNpcId(int runnerNpcId) {
        this.runnerNpcId = runnerNpcId;
    }
}
