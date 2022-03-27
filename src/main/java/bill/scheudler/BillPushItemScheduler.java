package bill.scheudler;

import bill.BillConfig;
import bill.api.BillApi;
import bill.database.BillGiftTable;
import bill.database.BillTable;
import bill.database.model.Bill;
import bill.database.model.BillGift;
import com.baroservice.ws.BankAccountLogEx;
import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Inventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;
import ks.util.log.L1LogUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

//@Component
public class BillPushItemScheduler {
    @Scheduled(fixedDelay = 1000 * 30)
    public void scheduled() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        pushItem();
    }

    @LogTime
    public void pushItem() {
        List<BankAccountLogEx> list = BillTable.getInstance().selectUnRegisteredInGiftList();

        for (BankAccountLogEx ex : list) {
            Bill bill = new Bill(ex);
            checkBill(bill);
        }
    }

    public void checkBill(Bill bill) {
        L1PcInstance pc = L1CommonUtils.findWorldUserOrOffLineUserByName(bill.getTransRemark());

        if (pc != null) {
            pc.getInventory().loadItems(false);

            String transRefKey = bill.getTransRefKey();
            String transRemark = bill.getTransRemark();
            String transDt = bill.getTransDT();
            int deposit = bill.getDeposit();

            int charId = pc.getId();
            String pcName = pc.getName();

            boolean isNotExists = BillGiftTable.getInstance().countByTransRefKey(transRefKey) == 0;

            if (isNotExists) {
                int cnt = deposit / BillConfig.BILL_GIFT_ITEM_PRICE;

                if (cnt <= 0) {
                    pc.sendPackets("잘못된 금액을 입금하였습니다. 운영자에게 편지로 확인 요청하세요");
                    return;
                }

                boolean result = pushItem(pc, cnt);

                if (result) {
                    BillGift billGift = new BillGift();
                    billGift.setTransRefKey(transRefKey);
                    billGift.setTransDT(transDt);
                    billGift.setTransRemark(transRemark);
                    billGift.setCharId(charId);
                    billGift.setCharName(pcName);
                    billGift.setDeposit(deposit + "");
                    billGift.setGift(true);
                    billGift.setGiftDate(new Date());
                    billGift.setRegDate(new Date());

                    BillGiftTable.getInstance().insert(billGift);
                }
            }
        }
    }

    public boolean pushItem(L1PcInstance pc, int cnt) {
        int itemId = BillConfig.BILL_GIFT_ITEM;

        L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
        item.setCount(cnt);
        item.setIdentified(true);

        if (pc.getInventory().checkAddItem(item, cnt) == L1Inventory.OK) {
            pc.getInventory().storeItem(item);

            String itemName = L1LogUtils.logItemName(item);

            pc.sendPackets(String.format("후원 아이템 %s가 지급되었습니다", itemName));

            String msg = String.format("후원지급 %s->%s", pc.getName(), itemName);

            L1CommonUtils.sendMessageToAllGm(msg);

            BillApi.getInstance().sendSms(BillConfig.BILL_SMS_MASTER_NUMBER, msg);

            L1LogUtils.debugLog("[후원아이템 지급] pcId:{} pcName:{} item:{}", pc.getId(), pc.getName(), itemName);
        }

        return true;
    }
}
