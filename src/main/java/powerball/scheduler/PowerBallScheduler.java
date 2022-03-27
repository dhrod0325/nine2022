package powerball.scheduler;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import powerball.PowerBallHtmlParser;
import powerball.table.PowerBallTable;
import powerball.vo.PowerBall;
import powerball.vo.PowerBallResult;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//@Component
public class PowerBallScheduler {
    private final Logger logger = LogManager.getLogger();

    private final PowerBallHtmlParser parser = new PowerBallHtmlParser();

    @Scheduled(fixedDelay = 1000 * 60 * 2)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        try {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            loadAll(date, 1);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    @LogTime
    public void loadAll(String date, int page) throws IOException {
        PowerBallResult result = parser.parse(date, page);

        for (PowerBall powerBall : result.getResults()) {
            PowerBallTable.getInstance().insertOrUpdate(powerBall);
        }

        if ("N".equals(result.getEndYn())) {
            loadAll(date, page + 1);
        }
    }
}
