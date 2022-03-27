package powerball.vo;

import java.util.List;

public class PowerBallResult {
    private List<PowerBall> results;
    private String endYn;

    public List<PowerBall> getResults() {
        return results;
    }

    public void setResults(List<PowerBall> results) {
        this.results = results;
    }

    public String getEndYn() {
        return endYn;
    }

    public void setEndYn(String endYn) {
        this.endYn = endYn;
    }
}
