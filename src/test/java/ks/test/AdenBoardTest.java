package ks.test;

import ks.base.AbstractTest;
import ks.system.adenBoard.database.AdenBankAccountTable;
import ks.system.adenBoard.model.AdenBankAccount;
import ks.system.adenBoard.model.AdenSell;

import java.util.Date;

public class AdenBoardTest extends AbstractTest {
    public void test() {
        AdenBankAccountTable adenBankAccountTable = AdenBankAccountTable.getInstance();

        //등록
        AdenBankAccount adenBankAccount = new AdenBankAccount();
        adenBankAccount.setAccount_id("test");
        adenBankAccount.setBank_name("test");
        adenBankAccount.setBank_no("test");
        adenBankAccount.setPhone("test");
        adenBankAccount.setBank_owner_name("test");
        adenBankAccount.setReg_date(new Date());
        //adenBankAccountDao.insertOrUpdateBankAccount(adenBankAccount);

        AdenBankAccount t = adenBankAccountTable.getBankAccount(adenBankAccount.getAccount_id());

        System.out.println(t);

        AdenSell adenSell = new AdenSell();
        adenSell.setAden(10);
        adenSell.setAccount_id("test");
        adenSell.setReg_date(new Date());
        adenSell.setCash(100);
        adenSell.setStatus("test");

        //adenBankAccountDao.insertAdenSell(adenSell);

        //adenBankAccountDao.deleteAdenSell(1);
    }
}
