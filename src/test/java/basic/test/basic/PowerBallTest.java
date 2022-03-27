package basic.test.basic;

import basic.test.BaseTest;
import powerball.PowerBallHtmlParser;
import powerball.table.PowerBallTable;
import powerball.vo.PowerBall;
import powerball.vo.PowerBallResult;

public class PowerBallTest extends BaseTest {
    public static void main(String[] args) {
        try {
            PowerBallHtmlParser parser = new PowerBallHtmlParser();
            PowerBallResult powerBalls = parser.parse("2021-10-27");

            for (PowerBall vo : powerBalls.getResults()) {
                PowerBallTable.getInstance().insertOrUpdate(vo);
            }
        } catch (Exception e) {
        }

        System.exit(0);
    }
}
