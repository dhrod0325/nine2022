import com.baroservice.api.BarobillApiProfile;
import com.baroservice.api.BarobillApiService;
import com.baroservice.ws.PagedBankAccountLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BankTest {
    private static final Logger logger = LogManager.getLogger();

    //IDEAPEOPLE

    public static void main(String[] args) {
        try {
            BarobillApiService barobillApiService = new BarobillApiService(BarobillApiProfile.RELEASE);

            String certKey = "4A99895F-B95D-42F2-9C58-A1A0EF616835";            // 인증키
            String corpNum = "1674500056";            // 바로빌 회원 사업자번호 ('-' 제외, 10자리)
            String id = "dhrod5457";                 // 바로빌 회원 아이디
            String bankAccountNum = "3520489053663";     // 계좌번호
            String baseDate = "20211112";           // 기준날짜

            int countPerPage = 10;          // 페이지수
            int currentPage = 1;            // 현재페이지
            int orderDirection = 2;         // 1:ASC 2:DESC

            PagedBankAccountLog result =
                    barobillApiService.bankAccount.getDailyBankAccountLog(
                            certKey, corpNum, id, bankAccountNum,
                            baseDate,
                            countPerPage,
                            currentPage, orderDirection
                    );

            barobillApiService.sms.sendMessage(
                    certKey,
                    corpNum,
                    "dhrod5457",
                    "01066468631",
                    "TEST",
                    "01066468631",
                    "test",
                    "",
                    "IDEAPEOPLE"
            );

        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

}
