package ks.system.race.task;

import ks.core.datatables.npc.NpcTable;
import ks.model.Broadcaster;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1MerchantInstance;
import ks.model.instance.L1NpcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.system.race.L1RaceManager;
import ks.system.race.datatable.L1RaceResultTable;
import ks.system.race.datatable.L1RaceTicketTable;
import ks.system.race.model.L1RaceResult;
import ks.system.race.model.L1RaceRunner;
import ks.util.common.random.RandomUtils;

import java.util.Timer;
import java.util.TimerTask;

public class RaceRunnerTask extends TimerTask {
    private L1MerchantInstance cecile;

    private final L1RaceRunner runner;

    public RaceRunnerTask(L1RaceRunner runner) {
        this.runner = runner;

        for (L1Object obj : L1World.getInstance().getAllObject()) {
            if (obj instanceof L1MerchantInstance) {
                if (((L1MerchantInstance) obj).getNpcId() == 70035) {
                    cecile = (L1MerchantInstance) obj;
                }
            }
        }
    }

    @Override
    public void run() {
        int sleepTime = 0;

        L1NpcInstance npc = runner.getNpc();

        while (L1RaceManager.getInstance().getGameStatus() == L1RaceManager.STATUS_PLAYING) {
            sleep(sleepTime);

            if (RandomUtils.isWinning(150, 3)) {
                Broadcaster.broadcastPacket(npc, new S_DoActionGFX(npc.getId(), 30));
                int[] ran = new int[]{1000, 1500, 2000, 2500, 3000};
                sleep(ran[RandomUtils.nextInt(ran.length - 1)]);
            }

            while (!npc.getMap().isPassable(npc.getX(), npc.getY(), npc.getHeading())) {
                if (npc.getMap().isPassable(npc.getX(), npc.getY(), npc.getHeading() + 1)) {
                    npc.setHeading(rePressHeading(npc.getHeading() + 1));

                    break;
                } else {
                    npc.setHeading(rePressHeading(npc.getHeading() - 1));

                    if (npc.getMap().isPassable(npc.getX(), npc.getY(), npc.getHeading())) {
                        break;
                    }
                }
            }

            npc.directionMove(npc.getHeading());

            if (checkPosition()) {
                return;
            } else {
                sleepTime = calcSleepTime(npc.getPassiSpeed());
            }
        }
    }

    public void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int calcSleepTime(int sleepTime) {
        L1NpcInstance npc = runner.getNpc();

        if (npc.getMoveState().getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }

        return sleepTime;
    }

    private int rePressHeading(int heading) {
        if (0 > heading) {
            heading = 7;
        }
        if (7 < heading) {
            heading = 0;
        }

        return heading;
    }

    public boolean checkPosition() {
        final int[] defaultHead = {6, 7, 0, 1, 2, 2};

        if (L1RaceManager.getInstance().getGameStatus() != L1RaceManager.STATUS_PLAYING) {
            return false;
        }

        boolean flag = false;

        L1NpcInstance npc = runner.getNpc();
        int runnerStatus = runner.getRunnerStatus();

        int x = npc.getX();
        int y = npc.getY();

        if (runnerStatus == 0) {
            if ((x >= 33476 && x <= 33476 + 8) && (y >= 32861 && y <= 32861 + 8)) {
                runner.setRunnerStatus(runnerStatus + 1);
            }

            npc.setHeading(defaultHead[runner.getRunnerStatus()]);
        } else if (runnerStatus == 1) {
            if ((x <= 33473 && x >= 33473 - 9) && y == 32858) {
                runner.setRunnerStatus(runnerStatus + 1);
            }

            npc.setHeading(defaultHead[runner.getRunnerStatus()]);
        } else if (runnerStatus == 2) {
            if ((x <= 33473 && x >= 33473 - 9) && y == 32852) {
                runner.setRunnerStatus(runnerStatus + 1);
            }

            npc.setHeading(defaultHead[runner.getRunnerStatus()]);
        } else if (runnerStatus == 3) {//
            if ((x == 33478 && (y <= 32847 && y >= 32847 - 9))) {
                runner.setRunnerStatus(runnerStatus + 1);
            }

            npc.setHeading(defaultHead[runner.getRunnerStatus()]);
        } else if (runnerStatus == 4) {//
            if (x == 33523 && (y >= 32847 - 9 && y <= 32847)) {
                runner.setRunnerStatus(runnerStatus + 1);
                goal();
            }

            npc.setHeading(defaultHead[runner.getRunnerStatus()]);
        } else if (runnerStatus == 5) {
            npc.setHeading(defaultHead[runner.getRunnerStatus()]);

            if (x == 33527 && (y >= 32847 - 8 && y <= 32847)) {
                L1RaceManager.getInstance().finish();
                flag = true;
            }
        }

        return flag;
    }

    public void goal() {
        int cnt = 0;

        for (L1RaceRunner runner : L1RaceManager.getInstance().getRunners()) {
            if (runner.getRunnerStatus() == 5) {
                cnt++;
            }
        }

        if (cnt == 1) {
            finishRace();
        }
    }

    public void finishRace() {
        L1NpcInstance npc = runner.getNpc();
        double allotmentPercentage = runner.getAllotmentPercentage();

        int round = L1RaceManager.getInstance().getRound();

        Broadcaster.wideBroadcastPacket(cecile, new S_NpcChatPacket(cecile, round + " $366 " + NpcTable.getInstance().getTemplate(npc.getNpcId()).getNameId() + " $367", 2));
        L1RaceTicketTable.getInstance().updateTicket(round, npc.getNpcId() - L1RaceManager.FIRST_NPC_ID + 1, allotmentPercentage);

        L1RaceResult vo = new L1RaceResult();
        vo.setRound(round);
        vo.setWinnerNpcId(npc.getNpcId());
        vo.setType(npc.getNpcId() - L1RaceManager.FIRST_NPC_ID + 1);
        vo.setAllotmentPercentage(allotmentPercentage);

        L1RaceResultTable.getInstance().insert(vo);
    }

    public void begin(int startTime) {
        Timer timer = new Timer();
        timer.schedule(this, startTime);
    }
}
