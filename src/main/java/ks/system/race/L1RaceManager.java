package ks.system.race;

import ks.app.LineageAppContext;
import ks.core.datatables.ShopTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.L1Item;
import ks.model.L1Npc;
import ks.model.L1ShopItem;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.shop.L1Shop;
import ks.model.shop.L1ShopBuyOrder;
import ks.model.shop.L1ShopBuyOrderList;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.system.race.datatable.L1RaceTicketTable;
import ks.system.race.model.L1RaceRunner;
import ks.system.race.model.L1RaceTicket;
import ks.system.race.task.RaceTask;
import ks.util.L1CommonUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class L1RaceManager {
    public static final int FIRST_ID = 0x0000000;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_END = 3;

    public static final int SHOP_ITEM_ID = 40309;

    public static final int FIRST_NPC_ID = 91350;// ~20

    public static final int RESTORE_TICKET_ID = 60001439;

    private final List<L1RaceRunner> runners = new ArrayList<>(5);

    private int allBet;
    private int round;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public static L1RaceManager getInstance() {
        return LineageAppContext.getBean(L1RaceManager.class);
    }

    public void run() {
        setRound(L1RaceTicketTable.getInstance().getRoundNumOfMax());

        runners.clear();

        for (int i = 0; i < 5; i++) {
            runners.add(new L1RaceRunner());
        }

        RaceTask raceTask = new RaceTask(0);
        raceTask.begin();
    }

    private int status = 0;

    public void setGameStatus(int i) {
        status = i;
    }

    public int getGameStatus() {
        return status;
    }

    public void setAllBet(int allBet) {
        this.allBet = (int) (allBet * 0.9);
    }

    public int getAllBet() {
        return allBet;
    }

    public L1RaceRunner getRunner(int num) {
        return runners.get(num);
    }

    public List<L1RaceRunner> getRunners() {
        return runners;
    }

    public void finish() {
        int cnt = 0;

        for (L1RaceRunner runner : getRunners()) {
            if (runner.getRunnerStatus() == 5) {
                cnt++;
            }
        }

        if (cnt == 5) {
            setGameStatus(STATUS_END);
            new RaceTask(30).begin();
        }
    }

    public void storeItem(L1ItemInstance item) {
        String[] temp1 = item.getItem().getNameId().split(" ");

        String[] temp = temp1[temp1.length - 1].split("-");
        int runnerNum = Integer.parseInt(temp[1]);

        L1RaceTicket ticket = new L1RaceTicket();
        ticket.setItemObjId(item.getId());
        ticket.setRound(Integer.parseInt(temp[0]));
        ticket.setAllotmentPercentage(0.0);
        ticket.setVictory(0);
        ticket.setRunnerNum(runnerNum);

        int r = NpcTable.getInstance().findNpcIdByNameId(temp1[1]);

        if (r != 0) {
            ticket.setRunnerNpcId(r);
        }

        L1RaceTicketTable.getInstance().storeNewTiket(ticket);
    }

    public void loadItems(L1ItemInstance item) {
        L1RaceTicket ticket = L1RaceTicketTable.getInstance().getTemplate(item.getId());

        if (ticket != null) {
            L1Npc m = NpcTable.getInstance().getTemplate(ticket.getRunnerNpcId());
            L1Item temp = SerializationUtils.clone(item.getItem());
            String buf = m.getNameId() + " " + ticket.getRound() + "-" + ticket.getRunnerNum();

            temp.setName(buf);
            temp.setNameId(buf);

            item.setItem(temp);
        }
    }

    public int buildPrice(int targetId, int assessedPrice) {
        L1RaceTicket ticket = L1RaceTicketTable.getInstance().getTemplate(targetId);

        int price = 0;

        if (ticket != null) {
            price = (int) (assessedPrice * ticket.getAllotmentPercentage() * ticket.getVictory());
        }

        return price;
    }

    public void bet(L1ItemInstance item, L1ShopBuyOrder order, int amount) {
        item.setItem(order.getItem().getItem());

        setAllBet(getAllBet() + (amount * order.getItem().getPrice()));

        String[] runNum = item.getItem().getNameId().split("-");

        int trueNum = 0;

        for (int i = 0; i < 5; i++) {
            int npcId = getRunner(i).getNpc().getNpcId();
            int ticketId = Integer.parseInt(runNum[runNum.length - 1]) - 1;

            if (npcId - 91350 == ticketId) {
                trueNum = i;
                break;
            }
        }

        L1RaceRunner runner = getRunner(trueNum);
        runner.setBetCount(runner.getBetCount() + amount);
    }

    public boolean ensure(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (L1CommonUtils.isStandByServer(pc)) {
            L1CommonUtils.sendStandByMsg(pc);
            return false;
        }

        if (orderList.containsItemId(SHOP_ITEM_ID)) {
            if (status == STATUS_PLAYING) {
                pc.sendPackets("경기가 진행중 입니다");
                return false;
            }
        }

        return true;
    }

    public String getHtml() {
        String html = null;

        if (status == STATUS_NONE) {
            html = "maeno5";
        } else if (status == STATUS_READY) {
            html = "maeno1";
        } else if (status == STATUS_PLAYING) {
            html = "maeno3";
        } else if (status == STATUS_END) {
            html = "maeno5";
        }

        return html;
    }

    public void talkStatus(L1PcInstance pc, int objId) {
        String[] data = new String[15];

        for (int i = 0; i < 5; i++) {
            L1RaceRunner runner = getRunner(i);

            L1NpcInstance runnerNpc = runner.getNpc();

            if (runnerNpc == null)
                continue;

            data[i * 3] = NpcTable.getInstance().getTemplate(runnerNpc.getNpcId()).getNameId();

            String condition;

            if (runner.getCondition() == 0) {
                condition = "$610";
            } else {
                if (runner.getCondition() > 0) {
                    condition = "$368";
                } else {
                    condition = "$370";
                }
            }

            data[i * 3 + 1] = condition;
            data[i * 3 + 2] = String.valueOf(runner.getWinningAverage());
        }

        pc.sendPackets(new S_NPCTalkReturn(objId, "maeno4", data));
    }

    public void initShop() {
        L1Shop shop1 = ShopTable.getInstance().findShop(70035);
        L1Shop shop2 = ShopTable.getInstance().findShop(70041);
        L1Shop shop3 = ShopTable.getInstance().findShop(70042);

        shop1.getSellingItems().clear();
        shop2.getSellingItems().clear();
        shop3.getSellingItems().clear();

        int price = getTicketPrice();

        for (int i = 0; i < 5; i++) {
            L1ShopItem shopItem1 = new L1ShopItem(SHOP_ITEM_ID, price, 1, 0);
            shopItem1.setName(i);

            L1ShopItem shopItem2 = new L1ShopItem(SHOP_ITEM_ID, price, 1, 0);
            shopItem2.setName(i);

            L1ShopItem shopItem3 = new L1ShopItem(SHOP_ITEM_ID, price, 1, 0);
            shopItem3.setName(i);

            shop1.getSellingItems().add(shopItem1);
            shop2.getSellingItems().add(shopItem2);
            shop3.getSellingItems().add(shopItem3);
        }
    }

    public int getTicketPrice() {
        L1Shop shop1 = ShopTable.getInstance().findShop(70035);
        L1ShopItem item = shop1.getBuyingItems().get(0);

        return item.getPrice();
    }
}
