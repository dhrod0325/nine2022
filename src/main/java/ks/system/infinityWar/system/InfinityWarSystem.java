package ks.system.infinityWar.system;

import ks.core.datatables.item.ItemTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1GroundInventory;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.system.infinityWar.model.InfinityWar;
import ks.system.infinityWar.model.InfinityWarItem;
import ks.system.infinityWar.model.InfinityWarSpawn;
import ks.system.portalsystem.model.L1Time;
import ks.util.L1SpawnUtils;
import ks.util.L1TeleportUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InfinityWarSystem implements Runnable {
    private final Logger logger = LogManager.getLogger();

    private InfinityWar war;

    private final List<L1PcInstance> players = new CopyOnWriteArrayList<>();

    private final List<L1NpcInstance> mobList = new CopyOnWriteArrayList<>();

    private boolean open;

    private boolean start;

    @Override
    public void run() {
        open();

        start();

        end();
    }

    public void open() {
        logger.debug("무한대전 오픈");

        String[] msg = new String[]{
                "안녕하세요. 리니지입니다",
                String.format("잠시후 %s분부터 %s에서 무한대전이 진행되오니 많은 참여 바랍니다", war.nextTime(), war.getName()),
                "감사합니다"
        };

        L1World.getInstance().setWorldChatEnable(false);

        int chatTime = 1000 * 5;

        for (String chatText : msg) {
            L1PcInstance sender = new L1PcInstance();
            sender.setName("******");

            L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(sender, chatText, L1Opcodes.S_OPCODE_MSG, 3));

            sleep(chatTime);
        }

        L1World.getInstance().setWorldChatEnable(true);

        int waitTime = 1000 * 60 * 5;
        int totalChatTime = chatTime * msg.length;

        sleep(waitTime - totalChatTime);
    }

    private void sleep(long milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playerCheck() {
        for (L1PcInstance pc : players) {
            if (pc.getMapId() != war.getMapId()) {
                removePlayer(pc);
            }
        }
    }

    public void start() {
        try {
            logger.debug("무한대전 시작: {}", war.getId());

            setStart(true);

            int pattern = RandomUtils.nextInt(1, war.getMaxPattern());

            for (int round = 1; round < 4; round++) {
                sendMessage("콜롯세움 관리인 : " + round + "군 투입");

                List<InfinityWarSpawn> spawnList = war.getSpawnListByRound(pattern, round);

                for (InfinityWarSpawn spawn : spawnList) {
                    if (!players.isEmpty()) {
                        spawnMonster(spawn);
                    }

                    sleep(spawn.getDelay() * 1000L);
                }

                spawnItem(round);

                waitNextRound(round);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitNextRound(int round) {
        int[] WAIT_TIME_TABLE = {1, 2, 6, 3};
        int wait = WAIT_TIME_TABLE[round - 1];

        sendMessage("콜롯세움 관리인 : 제 " + round + " 군의 투입이 완료되었습니다.");
        sendMessage("콜롯세움 관리인 : " + wait + "분 후에 제 " + (round + 1) + " 군의 투입이 시작됩니다.");

        sleep(1000 * 60 * wait);
    }

    private void spawnItem(int round) {
        List<InfinityWarItem> items = war.getItemsByRound(round);

        for (InfinityWarItem warItem : items) {
            if (!players.isEmpty()) {
                for (int i = 0; i < warItem.getSpawnCount(); i++) {
                    L1Location loc = war.getLocation().randomLocation((war.getX2() - war.getX1()) / 2);
                    L1GroundInventory inv = L1World.getInstance().getInventory(loc);
                    L1ItemInstance createItem = ItemTable.getInstance().createItem(warItem.getItemId());
                    createItem.setCount(warItem.getCount());
                    inv.storeItem(createItem);
                }
            }
        }
    }

    private void spawnMonster(InfinityWarSpawn spawn) {
        for (int i = 0; i < spawn.getCount(); i++) {
            L1NpcInstance npc = L1SpawnUtils.randomSpawn(
                    spawn.getNpcId(),
                    war.getX1(), war.getX2(), war.getY1(), war.getY2(), (short) war.getMapId(), 1000 * 60 * 30, null
            );

            if (npc != null) {
                npc.getInventory().clearItems();
                mobList.add(npc);
            }
        }
    }

    public void end() {
        for (L1PcInstance pc : players) {
            L1TeleportUtils.teleportToGiran(pc);
        }

        for (L1NpcInstance npc : mobList) {
            npc.deleteMe();
        }

        sendMessage("무한대전이 종료되었습니다");

        mobList.clear();
        players.clear();
    }

    private void sendMessage(String msg) {
        for (L1PcInstance pc : players) {
            pc.sendPackets(msg);
            pc.sendGreenMessage(msg);
        }
    }

    public boolean isOpenTime() {
        if (war.getTimes().isEmpty()) {
            return false;
        } else {
            Calendar cal = Calendar.getInstance();

            Date current = cal.getTime();

            for (L1Time runTime : getStartTimeList()) {
                Calendar openTime = Calendar.getInstance();
                openTime.setTime(runTime.getTime());

                openTime.set(Calendar.SECOND, 0);
                openTime.add(Calendar.MINUTE, -5);

                if (current.after(openTime.getTime()) && current.before(runTime.getTime())) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<L1Time> getStartTimeList() {
        List<L1Time> result = new ArrayList<>();

        for (String o : war.getTimes()) {
            String[] tt = o.split(":");
            result.add(new L1Time(Integer.parseInt(tt[0]), Integer.parseInt(tt[1])));
        }

        return result;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public List<L1PcInstance> getPlayers() {
        return players;
    }

    public void setWar(InfinityWar war) {
        this.war = war;
    }

    public void addPlayer(L1PcInstance pc) {
        if (!players.contains(pc))
            players.add(pc);
    }

    public void removePlayer(L1PcInstance pc) {
        players.remove(pc);
    }

    public boolean isFull() {
        return players.size() >= war.getMaxPlayer();
    }

    public void npcTalk(String action, L1PcInstance pc, L1NpcInstance npc) {
        if ("ent".equalsIgnoreCase(action)) {
            if (war.getWarSystem().isOpen()) {
                if (isStart()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "colos1"));
                } else {
                    if (isFull()) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "colos4"));
                    } else {
                        addPlayer(pc);
                        L1Location loc = war.getLocation().randomLocation(10, false);
                        L1Teleport.teleport(pc, loc, 5, true);
                    }
                }
            } else {
                npcInfo(pc, npc);
            }
        } else if ("sco".equalsIgnoreCase(action)) {

        } else if ("info".equalsIgnoreCase(action)) {
            npcInfo(pc, npc);
        }
    }

    public void npcInfo(L1PcInstance pc, L1NpcInstance npc) {
        List<Object> data = new ArrayList<>();

        data.add(war.nextTime());

        StringBuilder classesBuff = new StringBuilder();

        if (war.getEnterDarkelf() > 0) {
            classesBuff.append("다크엘프 ");
        }
        if (war.getEnterMage() > 0) {
            classesBuff.append("마법사 ");
        }
        if (war.getEnterElf() > 0) {
            classesBuff.append("요정 ");
        }
        if (war.getEnterKnight() > 0) {
            classesBuff.append("기사 ");
        }
        if (war.getEnterRoyal() > 0) {
            classesBuff.append("군주 ");
        }

        data.add(classesBuff.toString());

        StringBuilder sexBuff = new StringBuilder();

        if (war.getEnterMale() > 0) {
            sexBuff.append("남자 ");
        }
        if (war.getEnterFemale() > 0) {
            sexBuff.append("여자 ");
        }

        data.add(sexBuff.toString());
        data.add(war.getMinLvl());
        data.add(war.getMaxLvl());

        L1Map map = war.getMap();
        data.add(map.isEscapable() ? "가능" : "불가능");
        data.add(map.isUseResurrection() ? "가능" : "불가능");
        data.add(war.getUsePot() > 0 ? "가능" : "불가능");
        data.add(war.getHprBonus());
        data.add(war.getMprBonus());
        data.add(map.isTakePets() ? "가능" : "불가능");
        data.add(map.isRecallPets() ? "가능" : "불가능");

        pc.sendPackets(new S_ShowCCHtml(npc.getId(), "colos2", data));
    }
}
