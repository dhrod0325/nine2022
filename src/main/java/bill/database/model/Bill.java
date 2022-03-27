package bill.database.model;

import com.baroservice.ws.BankAccountLogEx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bill {
    private final Logger logger = LogManager.getLogger();

    private final BankAccountLogEx bankAccountLogEx;

    public Bill(BankAccountLogEx bankAccountLogEx) {
        this.bankAccountLogEx = bankAccountLogEx;
    }

    public int getDeposit() {
        return Integer.parseInt(bankAccountLogEx.getDeposit());
    }

    public int getWidthDraw() {
        return Integer.parseInt(bankAccountLogEx.getWithdraw());
    }

    public String getTransRemark() {
        return bankAccountLogEx.getTransRemark();
    }

    public Date getTransDate() {
        try {
            return new SimpleDateFormat("yyyyMMddHHmmss").parse(bankAccountLogEx.getTransDT());
        } catch (Exception e) {
            logger.warn("transDate convert error");
            return null;
        }
    }

    public String getTransDT() {
        return bankAccountLogEx.getTransDT();
    }

    public String getTransRefKey() {
        return bankAccountLogEx.getTransRefKey();
    }
}
