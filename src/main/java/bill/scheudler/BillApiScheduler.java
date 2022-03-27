package bill.scheudler;

import bill.api.BillApi;
import bill.database.BillTable;
import com.baroservice.ws.BankAccountLogEx;
import com.baroservice.ws.PagedBankAccountLogEx;
import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

//@Component
public class BillApiScheduler {
    private final Logger logger = LogManager.getLogger(getClass());

    //지급은 2분마다 동작
    @Scheduled(fixedDelay = 1000 * 60 * 2)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        LineageAppContext.commonTaskScheduler().execute(this::syncBill);
    }

    @LogTime
    public void syncBill() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DATE, -1);

        for (int i = 0; i < 3; i++) {
            String baseDate = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

            sync(baseDate);

            calendar.add(Calendar.DATE, 1);
        }
    }

    public void sync(String baseDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        logger.trace("계좌 내역 가저오기가 시작됩니다 일자 : {}", baseDate);

        int maxPageNo = 100;
        int page;

        for (page = 1; page <= maxPageNo; page++) {
            PagedBankAccountLogEx result = BillApi.getInstance().parseBill(baseDate, page);

            if (result != null) {
                List<BankAccountLogEx> list = result.getBankAccountLogList().getBankAccountLogEx();

                for (BankAccountLogEx log : list) {
                    BillTable.getInstance().insertOrUpdate(log);
                }

                if (page >= result.getMaxPageNum()) {
                    break;
                }
            }
        }

        stopWatch.stop();

        logger.trace("계좌내역 가저오기 종료 / 일자 : {} 횟수 : {} 실행시간 : {}ms",
                baseDate,
                page,
                stopWatch.getTotalTimeMillis()
        );
    }
}
