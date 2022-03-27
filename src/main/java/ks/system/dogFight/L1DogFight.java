package ks.system.dogFight;

import ks.app.LineageAppContext;
import ks.core.ObjectIdFactory;
import ks.core.datatables.ShopTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.shop.L1Shop;
import ks.model.shop.L1ShopBuyOrder;
import ks.model.shop.L1ShopBuyOrderList;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ks.constants.L1NpcConstants.CHAT_TIMING_APPEARANCE;

@Component
public class L1DogFight {
    public static final int FIRST_ID = 0x0000000;

    public static final int FIRST_NPC_ID = 460000294;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_WAIT = 1;
    public static final int STATUS_PLAYING = 2;

    //2분 주기로 실행함
    //public static final int NEXT_ROUND_WAIT = 1000 * 60 * 2;
    public static final int NEXT_ROUND_WAIT = 1000 * 30;

    // public static final int SHOP_ITEM_ID = 60001438;
    public static final int SHOP_ITEM_ID = 60001438;
    public static final int RESTORE_TICKET_ID = 60001440;

    private final Logger logger = LogManager.getLogger();

    public static L1DogFight getInstance() {
        return LineageAppContext.getBean(L1DogFight.class);
    }

    private int allBet;
    private int round;

    private int ticketPrice;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    private int status;

    private L1DogFightInstance dog1;
    private L1DogFightInstance dog2;

    private final List<L1DogFightInstance> dogs = new ArrayList<>();

    public L1NpcInstance getManager() {
        return L1World.getInstance().findNpcBySpawnId(2007122105);
    }

    public void start() {
        new DogFightTask().start();
    }

    public void sendMessage(String msg) {
        Broadcaster.wideBroadcastPacket(getManager(), new S_NpcChatPacket(getManager(), msg, 2));
        logger.trace(msg);
    }

    public void initShop() {
        L1Shop shop = ShopTable.getInstance().findShop(460000293);
        this.ticketPrice = shop.getBuyingItems().get(0).getPrice();
        shop.getSellingItems().clear();

        for (int i = 0; i < dogs.size(); i++) {
            L1ShopItem shopItem1 = new L1ShopItem(SHOP_ITEM_ID, ticketPrice, 1, 0);
            shopItem1.setName(i);
            shop.getSellingItems().add(shopItem1);
        }
    }

    public void initNpc() {
        dogs.clear();

        dog1 = (L1DogFightInstance) spawnOne("홀#", new L1Location(33529, 32863, 4), 4, getRandomNpcId());
        dogs.add(dog1);

        dog2 = (L1DogFightInstance) spawnOne("짝#", new L1Location(33529, 32866, 4), 0, getRandomNpcId());
        dogs.add(dog2);

        for (L1DogFightInstance dog : dogs) {
            dog.setAi(false);
        }
    }

    public void finish(L1DogFightInstance winner) {
        double per = winner.getAllotmentPercentage();

        int type = winner.getNameId().startsWith("홀#") ? 1 : 2;

        L1DogFightResult vo = new L1DogFightResult();
        vo.setRound(round);
        vo.setWinnerNpcId(winner.getNpcId());
        vo.setType(type);
        vo.setAllotmentPercentage(per);

        L1DogFightResultTable.getInstance().insert(vo);
        L1DogFightResultTable.getInstance().load();

        L1DogFightTicketTable.getInstance().updateTicket(round, winner.getNpcId() - FIRST_NPC_ID + 1, per);

        for (L1DogFightInstance dog : dogs) {
            dog.deleteMe();
        }
    }

    public int getRandomNpcId() {
        int npcId = FIRST_NPC_ID + RandomUtils.nextInt(20);

        while (checkDuplicate(npcId, dogs.size())) {
            npcId = FIRST_NPC_ID + RandomUtils.nextInt(20);
        }

        return npcId;
    }

    private boolean checkDuplicate(int npcId, int curi) {
        for (int i = 0; i < curi; i++) {
            if (dogs.get(i).getNpcId() == npcId) {
                return true;
            }
        }

        return false;
    }

    public void initTicket() {
        L1DogFightTicket ticket = new L1DogFightTicket();
        ticket.setItemObjId(FIRST_ID);
        ticket.setAllotmentPercentage(0);
        ticket.setRound(getRound());
        ticket.setRunnerNum(0);
        ticket.setVictory(0);

        L1DogFightTicketTable.getInstance().storeNewTiket(ticket);
        L1DogFightTicketTable.getInstance().oldTicketDelete(getRound());
    }

    public int buildPrice(int targetId, int assessedPrice) {
        L1DogFightTicket ticket = L1DogFightTicketTable.getInstance().getTemplate(targetId);

        int price = 0;

        if (ticket != null) {
            price = (int) (assessedPrice * ticket.getAllotmentPercentage() * ticket.getVictory());
        }

        return price;
    }

    public void loadItems(L1ItemInstance item) {
        L1DogFightTicket ticket = L1DogFightTicketTable.getInstance().getTemplate(item.getId());

        if (ticket != null) {
            L1Npc m = NpcTable.getInstance().getTemplate(ticket.getRunnerNpcId());
            L1Item temp = SerializationUtils.clone(item.getItem());
            String buf = m.getNameId() + " " + ticket.getRound() + "-" + ticket.getRunnerNum();

            temp.setName(buf);
            temp.setNameId(buf);

            item.setItem(temp);
        }
    }

    public void bet(L1ItemInstance item, L1ShopBuyOrder order, int amount) {
        item.setItem(order.getItem().getItem());

        setAllBet(getAllBet() + (amount * order.getItem().getPrice()));

        String[] runNum = item.getItem().getNameId().split("-");

        int trueNum = 0;

        for (int i = 0; i < dogs.size(); i++) {
            L1DogFightInstance dog = dogs.get(i);

            int npcId = dog.getNpcId();
            int ticketId = Integer.parseInt(runNum[runNum.length - 1]) - 1;

            if (npcId - FIRST_NPC_ID == ticketId) {
                trueNum = i;
                break;
            }
        }

        L1DogFightInstance dog = getDog(trueNum);
        dog.setBetCount(dog.getBetCount() + amount);
    }

    public void initBet() {
        for (L1DogFightInstance dog : dogs) {

            if (dog.getBetCount() > 0) {
                dog.setAllotmentPercentage(calcAllotmentPercentage(dog.getBetCount()));
            } else {
                dog.setAllotmentPercentage(1);
            }
        }
    }

    public double calcAllotmentPercentage(int betCount) {
        double num = (double) getAllBet() / (betCount) / ticketPrice;
        return Math.round(num * 100) / 100.0;
    }

    public boolean ensure(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        if (L1CommonUtils.isStandByServer(pc)) {
            L1CommonUtils.sendStandByMsg(pc);
            return false;
        }

        if (orderList.containsItemId(SHOP_ITEM_ID)) {
            if (status == STATUS_PLAYING) {
                pc.sendPackets("경기가 진행중입니다");
                return false;
            } else if (status == STATUS_WAIT) {
                pc.sendPackets("경기가 준비중입니다");
                return false;
            }
        }

        return true;
    }

    public String getTalkHtml() {
        String html = null;

        if (status == STATUS_NONE) {
            html = "goraEv1";
            logger.debug("개경 티켓 판매중");
        } else if (status == STATUS_WAIT) {
            html = "gora5";
            logger.debug("개경 다음경기 준비중");
        } else if (status == STATUS_PLAYING) {
            html = "gora3";
            logger.debug("개경 진행중");
        }

        return html;
    }

    public void talkStatus(L1PcInstance pc, int objId) {
        List<String> data = new ArrayList<>();
        List<L1DogFightResult> list = L1DogFightResultTable.getInstance().getList();

        for (L1DogFightResult o : list) {
            L1Npc n = NpcTable.getInstance().getTemplate(o.getWinnerNpcId());

            data.add(o.getRound() + "회 " + (o.getType() == 1 ? "홀" : "짝"));
            data.add(n.getNameId());
            data.add(o.getWinPer() + "%");
        }

        pc.sendPackets(new S_ShowCCHtml(objId, "gora4", data));
    }

    public class DogFightTask extends TimerTask {
        @Override
        public void run() {
            try {
                setStatus(STATUS_NONE);
                setRound(L1DogFightTicketTable.getInstance().getRoundNumOfMax());
                sendMessage("잠시후 버그베어 전투 경기가 시작됩니다");

                initNpc();

                initTicket();

                initShop();

                sleep(NEXT_ROUND_WAIT);

                sendMessage("10초 후 티켓 판매를 마감합니다");

                sleep(1000 * 10);

                initBet();

                LineageAppContext.commonTaskScheduler().schedule(() -> {
                    try {
                        sendMessage("경기 시작!");

                        Thread.sleep(1000 * 3);

                        for (L1DogFightInstance dog : dogs) {
                            sendMessage(dog.getNameId() + " : " + dog.getAllotmentPercentage());
                            Thread.sleep(1000 * 3);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Instant.now());

                setStatus(STATUS_PLAYING);

                for (L1DogFightInstance dog : dogs) {
                    dog.setAi(true);
                }

                new DogFightMonitor().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sleep(long mill) {
            try {
                Thread.sleep(mill);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void start() {
            new Timer().schedule(new DogFightTask(), 0);
        }
    }

    public class DogFightMonitor extends TimerTask {
        @Override
        public void run() {
            while (true) {
                if (status == STATUS_PLAYING) {
                    Broadcaster.wideBroadcastPacket(dog1, new S_HPMeter(dog1));
                    dog1.setTarget(dog2);

                    Broadcaster.wideBroadcastPacket(dog2, new S_HPMeter(dog2));
                    dog2.setTarget(dog1);

                    if (dog1.isDead() || dog2.isDead()) {
                        L1DogFightInstance winner;

                        if (dog1.isDead()) {
                            winner = dog2;
                            dog2.setAi(false);
                            Broadcaster.broadcastPacket(dog2, new S_SkillSound(dog2.getId(), 6354));
                        } else {
                            winner = dog1;
                            dog1.setAi(false);
                            Broadcaster.broadcastPacket(dog1, new S_SkillSound(dog1.getId(), 6354));
                        }

                        sendMessage("제 " + getRound() + "회 경기가 종료되었습니다. 승리 : " + winner.getNameId());

                        sleep(1000 * 10);

                        finish(winner);

                        setStatus(STATUS_WAIT);

                        LineageAppContext.commonTaskScheduler().schedule(L1DogFight.this::start, Instant.now().plusMillis(1000 * 10));
                    }
                } else {
                    break;
                }

                sleep(200);
            }
        }

        public void sleep(long mill) {
            try {
                Thread.sleep(mill);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void start() {
            new Timer().schedule(new DogFightMonitor(), 0);
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAllBet(int allBet) {
        this.allBet = (int) (allBet * 0.9);
    }

    public int getAllBet() {
        return allBet;
    }

    public void storeItem(L1ItemInstance item) {
        String[] temp1 = item.getItem().getNameId().split(" ");

        String[] temp = temp1[temp1.length - 1].split("-");
        int runnerNum = Integer.parseInt(temp[1]);

        L1DogFightTicket ticket = new L1DogFightTicket();
        ticket.setItemObjId(item.getId());
        ticket.setRound(Integer.parseInt(temp[0]));
        ticket.setAllotmentPercentage(0.0);
        ticket.setVictory(0);
        ticket.setRunnerNum(runnerNum);

        int r = NpcTable.getInstance().findNpcIdByNameId(temp1[1]);

        if (r != 0) {
            ticket.setRunnerNpcId(r);
        }

        L1DogFightTicketTable.getInstance().storeNewTiket(ticket);
    }

    public L1DogFightInstance getDog(int num) {
        return dogs.get(num);
    }

    public L1NpcInstance spawnOne(String prefixName, L1Location loc, int heading, int npcId) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);

            npc.setId(ObjectIdFactory.getInstance().nextId());
            npc.setNameId(prefixName + " " + npc.getNameId());
            npc.setHeading(heading);
            npc.setX(loc.getX());
            npc.setHomeX(loc.getX());
            npc.setY(loc.getY());
            npc.setHomeY(loc.getY());
            npc.setMap((short) loc.getMapId());
            npc.setPassiSpeed(npc.getPassiSpeed() * 2);

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);

            for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(npc)) {
                pc.getNearObjects().addKnownObject(npc);
                npc.getNearObjects().addKnownObject(pc);
                pc.sendPackets(new S_NPCPack(npc));
            }

            npc.onNpcAI();
            npc.startChat(CHAT_TIMING_APPEARANCE);

            return npc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getStatus() {
        return status;
    }
}
