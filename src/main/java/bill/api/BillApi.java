package bill.api;

import bill.BillConfig;
import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.PagedBankAccountLogEx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BillApi {
    private final Logger logger = LogManager.getLogger();

    private static final BillApi instance = new BillApi();

    public static BillApi getInstance() {
        return instance;
    }

    BarobillApiProfile profile = BarobillApiProfile.RELEASE;

    public PagedBankAccountLogEx parseBill(String baseDate, int currentPage) {
        try {
            BarobillApiService barobillApiService = new BarobillApiService(profile);

            String certKey = BillConfig.BILL_CERT;
            String corpNum = BillConfig.BILL_CORP;
            String id = BillConfig.BILL_ID;
            String bankAccountNum = BillConfig.BILL_BANK;

            int countPerPage = 10;          // 페이지수
            int orderDirection = 2;         // 1:ASC 2:DESC

            return barobillApiService.bankAccount.getDailyBankAccountLogEx(
                    certKey, corpNum, id, bankAccountNum,
                    baseDate,
                    countPerPage,
                    currentPage, orderDirection
            );

        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public void sendSms(String targetNumber, String msg) {
        try {
            String certKey = BillConfig.BILL_CERT;
            String corpNum = BillConfig.BILL_CORP;
            String id = BillConfig.BILL_ID;
            String billSender = BillConfig.BILL_SMS_SENDER;

            BarobillApiService barobillApiService = new BarobillApiService(profile);

            barobillApiService.sms.sendMessage(
                    certKey,
                    corpNum,
                    id,
                    BillConfig.BILL_SMS_MASTER_NUMBER,
                    "TEST",
                    targetNumber,
                    msg,
                    "",
                    billSender
            );
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
