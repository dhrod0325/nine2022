package ks.system.race.task;

import ks.app.LineageAppContext;
import ks.core.ObjectIdFactory;
import ks.core.datatables.DoorSpawnTable;
import ks.core.datatables.npc.NpcTable;
import ks.model.Broadcaster;
import ks.model.L1Location;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1DoorInstance;
import ks.model.instance.L1MerchantInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1RaceInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCPack;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.system.race.L1RaceManager;
import ks.system.race.datatable.L1RaceTicketTable;
import ks.system.race.model.L1RaceRunner;
import ks.system.race.model.L1RaceTicket;
import ks.system.race.util.L1RaceUtils;
import ks.util.common.random.RandomUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ks.constants.L1NpcConstants.CHAT_TIMING_APPEARANCE;

public class RaceTask extends TimerTask {
    private final int startTime;
    private final List<L1RaceRunner> runners;

    private L1MerchantInstance pory;
    private L1MerchantInstance parkin;

    public static final int WAIT_TIME = 60;
    public static final int READY_TIME = 5 * 60 - 10;

    public RaceTask(int startTime) {
        this.startTime = startTime;
        this.runners = L1RaceManager.getInstance().getRunners();

        for (L1Object obj : L1World.getInstance().getAllObject()) {
            if (obj instanceof L1MerchantInstance) {
                if (((L1MerchantInstance) obj).getNpcId() == 70041) {
                    parkin = (L1MerchantInstance) obj;
                }
            }
        }

        for (L1Object obj : L1World.getInstance().getAllObject()) {
            if (obj instanceof L1MerchantInstance) {
                if (((L1MerchantInstance) obj).getNpcId() == 70042) {
                    pory = (L1MerchantInstance) obj;
                }
            }
        }
    }

    private void sendMessage(String id) {
        Broadcaster.wideBroadcastPacket(parkin, new S_NpcChatPacket(parkin, id, 2));
        Broadcaster.wideBroadcastPacket(pory, new S_NpcChatPacket(pory, id, 2));
    }

    @Override
    public void run() {
        try {
            L1RaceManager.getInstance().setGameStatus(L1RaceManager.STATUS_NONE);
            sendMessage("$376 10 $377");

            if (startTime > 0) {
                Thread.sleep(1000 * WAIT_TIME);
            }

            clearRunner();

            L1RaceManager.getInstance().setRound(L1RaceManager.getInstance().getRound() + 1);

            L1RaceTicket ticket = new L1RaceTicket();
            ticket.setItemObjId(L1RaceManager.FIRST_ID);
            ticket.setAllotmentPercentage(0);
            ticket.setRound(L1RaceManager.getInstance().getRound());
            ticket.setRunnerNum(0);
            ticket.setVictory(0);

            L1RaceTicketTable.getInstance().storeNewTiket(ticket);
            L1RaceTicketTable.getInstance().oldTicketDelete(L1RaceManager.getInstance().getRound());

            setRandomRunner();
            setRandomCondition();
            setWinningAverage();

            L1RaceManager.getInstance().initShop();
            L1RaceManager.getInstance().setGameStatus(L1RaceManager.STATUS_READY);

            for (int loop = 0; loop < READY_TIME - 1; loop++) {
                if (loop % 60 == 0) {
                    sendMessage("$376 " + (1 + (READY_TIME - loop) / 60) + " $377");
                }

                Thread.sleep(1000);
            }

            sendMessage("$363");

            Thread.sleep(1000);

            for (int loop = 10; loop > 0; loop--) {
                sendMessage("" + loop);
                Thread.sleep(1000);
            }

            sendMessage("$364");

            L1RaceManager.getInstance().setGameStatus(L1RaceManager.STATUS_PLAYING);

            for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
                if (door.getDoorId() <= 812 && door.getDoorId() >= 808) {
                    door.open();
                }
            }

            for (L1RaceRunner runner : runners) {
                new RaceRunnerTask(runner).begin(0);
            }

            LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(() -> {
                if (L1RaceManager.getInstance().getGameStatus() == L1RaceManager.STATUS_PLAYING) {
                    for (L1RaceRunner runner : runners) {
                        runner.randomBuff();
                    }
                }
            }, Instant.now().plusMillis(1000), Duration.ofMillis(1000));


            for (L1RaceRunner runner : runners) {
                if (runner.getBetCount() > 0) {
                    runner.setAllotmentPercentage(calcAllotmentPercentage(runner.getBetCount()));
                } else {
                    runner.setAllotmentPercentage(1.0);
                }
            }

            for (L1RaceRunner runner : runners) {
                Thread.sleep(1000);
                sendMessage(NpcTable.getInstance().getTemplate(runner.getNpc().getNpcId()).getNameId() + " $402 " + runner.getAllotmentPercentage());// 402
            }

            this.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double calcAllotmentPercentage(int betCount) {
        double num = (double) L1RaceManager.getInstance().getAllBet() / (betCount) / L1RaceManager.getInstance().getTicketPrice();
        return Math.round(num * 100) / 100.0;
    }

    public void begin() {
        Timer timer = new Timer();
        timer.schedule(this, startTime * 1000L);
    }

    private void clearRunner() {
        for (int i = 0; i < 5; i++) {
            runners.get(i).clear();
        }

        L1RaceManager.getInstance().setAllBet(0);

        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (door.getDoorId() <= 812 && door.getDoorId() >= 808) {
                door.close();
            }
        }
    }

    private void setRandomRunner() {
        for (int i = 0; i < 5; i++) {
            int npcId = L1RaceManager.FIRST_NPC_ID + RandomUtils.nextInt(20);

            while (checkDuplicate(npcId, i)) {
                npcId = L1RaceManager.FIRST_NPC_ID + RandomUtils.nextInt(20);
            }

            L1Location loc = new L1Location(33522 - (i * 2), 32861 + (i * 2), 4);
            runners.get(i).setNpc(spawnOne(loc, npcId));
        }
    }

    private boolean checkDuplicate(int npcId, int curi) {
        for (int i = 0; i < curi; i++) {
            if (runners.get(i).getNpc().getNpcId() == npcId) {
                return true;
            }
        }

        return false;
    }

    private void setWinningAverage() {
        int i = 0;

        for (L1RaceRunner runner : runners) {
            double winningAverage = L1RaceUtils.getRandomProbability();

            while (checkDuplicateAverage(winningAverage, i)) {
                winningAverage = L1RaceUtils.getRandomProbability();
            }

            runner.setWinningAverage(winningAverage);

            i++;
        }

    }

    private boolean checkDuplicateAverage(double winningAverage, int curi) {
        for (int i = 0; i < curi; i++) {
            L1RaceRunner runner = runners.get(i);
            L1RaceRunner cur = runners.get(curi);

            if (runner.getWinningAverage() == winningAverage && runner.getCondition() == cur.getCondition()) {
                return true;
            }
        }

        return false;
    }

    private void setRandomCondition() {
        for (L1RaceRunner runner : runners) {
            runner.setCondition(-1 + RandomUtils.nextInt(3));
        }
    }

    private L1NpcInstance spawnOne(L1Location loc, int npcId) {
        L1RaceInstance mob = new L1RaceInstance(NpcTable.getInstance().getTemplate(npcId));

        mob.setNameId("#" + (mob.getNpcId() - L1RaceManager.FIRST_NPC_ID + 1) + " " + mob.getNameId());
        mob.setId(ObjectIdFactory.getInstance().nextId());
        mob.setHeading(6);
        mob.setX(loc.getX());
        mob.setHomeX(loc.getX());
        mob.setY(loc.getY());
        mob.setHomeY(loc.getY());
        mob.setMap((short) loc.getMapId());
        mob.setPassiSpeed(mob.getPassiSpeed() * 2);

        L1World.getInstance().storeObject(mob);
        L1World.getInstance().addVisibleObject(mob);

        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(mob)) {
            pc.getNearObjects().addKnownObject(mob);
            mob.getNearObjects().addKnownObject(pc);
            pc.sendPackets(new S_NPCPack(mob));
        }

        mob.onNpcAI();
        mob.startChat(CHAT_TIMING_APPEARANCE);

        return mob;
    }
}
