package basic.test.basic;

import bill.api.BillApi;
import com.baroservice.ws.PagedBankAccountLogEx;

public class LBH {
    public static void main(String[] args) {
        BillApi api = new BillApi();
        PagedBankAccountLogEx list = api.parseBill("20211115", 1);
        System.out.println(list);
    }
}
