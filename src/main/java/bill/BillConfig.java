package bill;

import ks.app.LineageAppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

public class BillConfig {
    private final static Logger logger = LogManager.getLogger();

    public static String BILL_SMS_SENDER;
    public static String BILL_SMS_MASTER_NUMBER;
    public static String BILL_ID;
    public static String BILL_CERT;
    public static String BILL_CORP;
    public static String BILL_BANK;
    public static Integer BILL_GIFT_ITEM;
    public static Integer BILL_GIFT_ITEM_PRICE;

    static {
        load();
    }

    public static void load() {
        try {
            Environment env = LineageAppContext.getCtx().getBean(Environment.class);

            BILL_ID = env.getProperty("l1j.bill.id", String.class);
            BILL_CERT = env.getProperty("l1j.bill.cert", String.class);
            BILL_CORP = env.getProperty("l1j.bill.corp", String.class);
            BILL_BANK = env.getProperty("l1j.bill.bank", String.class);
            BILL_BANK = env.getProperty("l1j.bill.bank", String.class);

            BILL_GIFT_ITEM_PRICE = env.getProperty("l1j.bill.giftItemPrice", Integer.class);
            BILL_GIFT_ITEM = env.getProperty("l1j.bill.giftItem", Integer.class);

            BILL_SMS_SENDER = env.getProperty("l1j.bill.sender", String.class);
            BILL_SMS_MASTER_NUMBER = env.getProperty("l1j.bill.masterSmsNumber", String.class);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
