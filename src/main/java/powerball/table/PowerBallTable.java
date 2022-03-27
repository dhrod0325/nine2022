package powerball.table;

import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import powerball.vo.PowerBall;

public class PowerBallTable {
    private static final PowerBallTable instance = new PowerBallTable();

    public static PowerBallTable getInstance() {
        return instance;
    }

    public void insertOrUpdate(PowerBall vo) {
        String sql = "insert into powerball (round,\n" +
                "                todayRound,\n" +
                "                powerball,\n" +
                "                powerballPeriod,\n" +
                "                powerballUnderOver,\n" +
                "                number,\n" +
                "                numberSum,\n" +
                "                numberSumPeriod,\n" +
                "                numberPeriod,\n" +
                "                numberUnderOver,\n" +
                "                `time`,\n" +
                "                `date`,\n" +
                "                `timeDate`) values (" +
                "                :round,\n" +
                "                :todayRound,\n" +
                "                :powerball,\n" +
                "                :powerballPeriod,\n" +
                "                :powerballUnderOver,\n" +
                "                :number,\n" +
                "                :numberSum,\n" +
                "                :numberSumPeriod,\n" +
                "                :numberPeriod,\n" +
                "                :numberUnderOver,\n" +
                "                :time,\n" +
                "                :date,\n" +
                "                :timeDate) ON DUPLICATE KEY UPDATE todayRound=:todayRound";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }
}
