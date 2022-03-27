package ks.system.huntCheck.vo;

import java.util.ArrayList;
import java.util.List;

public class HuntReport {
    public int mapId;
    public int adenaSum;
    public int resolventSum;
    public double expSum;
    public String charName;
    public String etc;
    public double expPerSum;

    public List<HuntResult> adenaList = new ArrayList<>();
    public List<HuntResult> resolveList = new ArrayList<>();

    public HuntReport(int mapId, int adenaSum, int resolventSum) {
        this.mapId = mapId;
        this.adenaSum = adenaSum;
        this.resolventSum = resolventSum;
    }

    public int getTotal() {
        return adenaSum + resolventSum;
    }
}
