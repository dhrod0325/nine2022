package ks.system.race.model;

import ks.model.instance.L1NpcInstance;
import ks.system.race.util.L1RaceUtils;

public class L1RaceRunner {
    private L1NpcInstance npc;
    private int runnerStatus;
    private double winningAverage;
    private double allotmentPercentage;
    private int condition;
    private int betCount;

    public L1NpcInstance getNpc() {
        return npc;
    }

    public void setNpc(L1NpcInstance npc) {
        this.npc = npc;
    }

    public int getRunnerStatus() {
        return runnerStatus;
    }

    public void setRunnerStatus(int runnerStatus) {
        this.runnerStatus = runnerStatus;
    }

    public double getWinningAverage() {
        return winningAverage;
    }

    public void setWinningAverage(double winningAverage) {
        this.winningAverage = winningAverage;
    }

    public double getAllotmentPercentage() {
        return allotmentPercentage;
    }

    public void setAllotmentPercentage(double allotmentPercentage) {
        this.allotmentPercentage = allotmentPercentage;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getBetCount() {
        return betCount;
    }

    public void setBetCount(int betCount) {
        this.betCount = betCount;
    }

    public void clear() {
        if (npc != null) {
            npc.deleteMe();

            if (npc.getMap().isInMap(npc.getX(), npc.getY())) {
                npc.getMap().setPassable(npc.getX(), npc.getY(), true);
            }
        }

        setNpc(null);
        setRunnerStatus(0);
        setWinningAverage(0);
        setAllotmentPercentage(0);
        setCondition(0);
        setBetCount(0);
    }

    public void randomBuff() {
        if (L1RaceUtils.getRandomProbability() <= getWinningAverage() * (1 + (0.2 * getCondition()))) {
            npc.getMoveState().setBraveSpeed(1);
        } else {
            npc.getMoveState().setBraveSpeed(0);
        }
    }
}
