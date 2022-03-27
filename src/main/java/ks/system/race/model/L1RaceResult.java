package ks.system.race.model;

public class L1RaceResult {
    private int round;
    private int winnerNpcId;
    private double allotmentPercentage;
    private int type;
    private double winPer;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getWinnerNpcId() {
        return winnerNpcId;
    }

    public void setWinnerNpcId(int winnerNpcId) {
        this.winnerNpcId = winnerNpcId;
    }

    public double getAllotmentPercentage() {
        return allotmentPercentage;
    }

    public void setAllotmentPercentage(double allotmentPercentage) {
        this.allotmentPercentage = allotmentPercentage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getWinPer() {
        return winPer;
    }

    public void setWinPer(double winPer) {
        this.winPer = winPer;
    }
}
