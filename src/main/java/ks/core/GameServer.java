package ks.core;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.app.event.L1InitializeEvent;
import ks.commands.gm.GMCommandsUtils;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.*;
import ks.core.datatables.badMsg.BadMsgTable;
import ks.core.datatables.balance.MapBalanceTable;
import ks.core.datatables.boss.BossGradeDropTable;
import ks.core.datatables.bugCheck.BugCheckTable;
import ks.core.datatables.characterStat.CharacterStatTable;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.commonCode.CommonCodeTable;
import ks.core.datatables.dollBonus.DollBonusTable;
import ks.core.datatables.drop.DropTable;
import ks.core.datatables.enchantBonus.EnchantBonusTable;
import ks.core.datatables.enchantSetting.EnchantSettingTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.getback.GetBackTable;
import ks.core.datatables.huktBook.HuntBookTable;
import ks.core.datatables.hunt.HuntPriceTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.itemMsg.ItemMsgTable;
import ks.core.datatables.mapEvent.MapEventDropTable;
import ks.core.datatables.mobskill.MobSkillInfoTable;
import ks.core.datatables.mobskill.MobSkillTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.npcTalk.NpcTalkTable;
import ks.core.datatables.npc_making.NpcMakingTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.datatables.pet.PetItemTable;
import ks.core.datatables.pet.PetTable;
import ks.core.datatables.pet.PetTypeTable;
import ks.core.datatables.polyImg.PolyImgTable;
import ks.core.datatables.shopInfo.NpcShopInfoTable;
import ks.core.datatables.slotSave.SlotSaveTable;
import ks.core.datatables.spr.SprStrictTable;
import ks.core.datatables.transform.NpcTransformTable;
import ks.core.datatables.weaponSkill.ItemSkillTable;
import ks.listener.FieldItemDeleteListener;
import ks.listener.L1TimeDungeonListener;
import ks.listener.LightTimeListener;
import ks.model.L1World;
import ks.model.item.L1TreasureBox;
import ks.model.item.function.L1DropItemList;
import ks.model.item.function.orim.L1OrimScrollEnchant;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.model.trap.L1WorldTraps;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_Weather;
import ks.scheduler.BackupScheduler;
import ks.scheduler.timer.realTime.RealTimeScheduler;
import ks.system.boss.L1BossSpawnManager;
import ks.system.boss.table.L1BossDieHistoryTable;
import ks.system.boss.table.L1BossSpawnListHotTable;
import ks.system.bossTraning.BossTrainingTable;
import ks.system.dogFight.L1DogFight;
import ks.system.dogFight.L1DogFightResultTable;
import ks.system.dogFight.L1DogFightTicketTable;
import ks.system.infinityWar.table.InfinityWarTable;
import ks.system.lastabard.LastabardListener;
import ks.system.lastabard.LastabardSpawnTable;
import ks.system.portalsystem.L1PortalSystemRunner;
import ks.system.portalsystem.table.PortalSpawnTable;
import ks.system.portalsystem.table.PortalSystemTable;
import ks.system.race.L1RaceManager;
import ks.system.race.datatable.L1RaceResultTable;
import ks.system.race.datatable.L1RaceTicketTable;
import ks.system.robot.L1RobotTable;
import ks.system.robot.RobotSpawnTable;
import ks.system.timeDungeon.L1TimeDungeonTable;
import ks.system.userShop.L1UserShopManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;

@Component
public class GameServer {
    private final Logger logger = LogManager.getLogger();

    @Resource
    private ApplicationContext applicationContext;

    private ServerShutdownThread shutdownThread = null;

    public static GameServer getInstance() {
        return LineageAppContext.getBean(GameServer.class);
    }

    @LogTime
    public void initialize() {
        applicationContext.publishEvent(new L1InitializeEvent.Start(new Object()));

        CommonCodeTable.getInstance().load();
        CodeConfig.load();

        showGameServerSetting();

        applicationContext.publishEvent(new L1InitializeEvent.OnLoadConfig(new Object()));

        MapsTable.getInstance().load();
        MapEventDropTable.getInstance().load();
        MapBalanceTable.getInstance().load();

        applicationContext.publishEvent(new L1InitializeEvent.OnLoadMap(new Object()));

        L1WorldMap.getInstance().load();
        L1World.getInstance().load();

        applicationContext.publishEvent(new L1InitializeEvent.OnLoadWorld(new Object()));

        HuntPriceTable.getInstance().load();
        SprStrictTable.getInstance().load();
        ExpTable.getInstance().load();
        NpcTalkTable.getInstance().load();
        CharacterStatTable.getInstance().load();

        HuntBookTable.getInstance().load();
        PolyImgTable.getInstance().load();
        NpcMakingTable.getInstance().load();
        ItemSkillTable.getInstance().load();
        ItemMsgTable.getInstance().load();

        BugCheckTable.getInstance().clear();
        BossTrainingTable.getInstance().clear();

        BadMsgTable.getInstance().load();

        NpcActionTable.getInstance().load();

        ItemTable.getInstance().load();
        L1TreasureBox.load();
        L1OrimScrollEnchant.load();
        L1DropItemList.load();

        BossGradeDropTable.getInstance().load();

        ClanTable.getInstance().load();
        CastleTable.getInstance().load();

        MobSkillTable.getInstance().load();
        MobSkillInfoTable.getInstance().load();

        SkillsTable.getInstance().load();

        PolyTable.getInstance().load();
        DropTable.getInstance().load();
        ShopTable.getInstance().load();

        EnchantBonusTable.getInstance().load();
        DollBonusTable.getInstance().load();
        CharacterTable.getInstance().load();

        HouseTable.getInstance().load();
        BeginnerTable.getInstance().load();
        BuddyTable.getInstance().load();
        NpcShopInfoTable.getInstance().load();

        NpcTransformTable.getInstance().load();
        NpcTable.getInstance().load();

        MobGroupTable.getInstance().load();

        NPCTalkDataTable.getInstance().load();
        DungeonTable.getInstance().load();
        DungeonRandomTable.getInstance().load();
        IpTable.getInstance().load();
        MapFixKeyTable.getInstance().load();

        TrapTable.getInstance().load();
        L1WorldTraps.getInstance().load();

        PetTable.getInstance().load();
        GetBackRestartTable.getInstance().load();

        GMCommandsUtils.load();
        GetBackTable.getInstance().load();
        PetItemTable.getInstance().load();
        PetTypeTable.getInstance().load();

        InfinityWarTable.getInstance().load();

        SprTable.getInstance().load();
        ResolventTable.getInstance().load();
        NpcChatTable.getInstance().load();
        SoldierTable.getInstance().load();
        CharSoldierTable.getInstance().load();
        ArmorSetTable.getInstance().load();
        L1TimeDungeonTable.getInstance().load();
        WeaponDamageTable.getInstance().load();
        SlotSaveTable.getInstance().load();
        L1BossDieHistoryTable.getInstance().load();
        EnchantSettingTable.getInstance().load();
        MapEventDropTable.getInstance().load();

        NpcTransformTable.getInstance().load();
        L1BossSpawnListHotTable.getInstance().load();

        L1RobotTable.getInstance().load();
        L1RobotTable.getInstance().endHuntTime();

        //스폰 테이블은 나중에
        DoorSpawnTable.getInstance().load();
        RobotSpawnTable.getInstance().load();
        LightSpawnTable.getInstance().load();

        SpawnTable.getInstance().loadAndSpawn();
        LastabardSpawnTable.getInstance().loadAndSpawn();
        NpcSpawnTable.getInstance().loadAndSpawn();

        ModelSpawnTable.getInstance().load();
        EffectSpawnTable.getInstance().load();
        PortalSpawnTable.getInstance().load();
        PortalSystemTable.getInstance().load();
        FurnitureSpawnTable.getInstance().load();

        if (CodeConfig.USE_BOSS_SYSTEM) {
            L1BossSpawnManager.getInstance().spawnServerDownPrevBoss();
        }

        L1UserShopManager.getInstance().load();

        BackupScheduler.getInstance().start();

        L1PortalSystemRunner.getInstance().load();

        registerRealTimeListener();

        LightTimeListener.getInstance().load();

        L1RaceResultTable.getInstance().load();
        L1RaceTicketTable.getInstance().load();
        L1RaceTicketTable.getInstance().restoreTickets();
        L1RaceManager.getInstance().run();

        L1DogFightResultTable.getInstance().load();
        L1DogFightTicketTable.getInstance().load();
        L1DogFightTicketTable.getInstance().restoreTickets();
        L1DogFight.getInstance().start();

        applicationContext.publishEvent(new L1InitializeEvent.End(new Object()));

        LineageAppContext.setRun(true);
    }

    private void registerRealTimeListener() {
        RealTimeScheduler realTimeScheduler = RealTimeScheduler.getInstance();

        realTimeScheduler.addListener(LastabardListener.getInstance());
        realTimeScheduler.addListener(L1TimeDungeonListener.getInstance());
        realTimeScheduler.addListener(FieldItemDeleteListener.getInstance());
        realTimeScheduler.addListener(LightTimeListener.getInstance());
    }

    private void showGameServerSetting() {
        double rateXp = CodeConfig.RATE_XP;
        double rateLawful = CodeConfig.RATE_LAWFUL;
        double rateKarma = CodeConfig.RATE_KARMA;
        double rateDropItems = CodeConfig.RATE_DROP_ITEMS;
        double rateDropAdena = CodeConfig.RATE_DROP_ADENA;

        logger.info("경험치: x{} / 라우풀: x{} / 아데나: x{}", rateXp, rateLawful, rateDropAdena);
        logger.info("우호도: x{} / 아이템: x{}", rateKarma, rateDropItems);
        logger.info("채팅 레벨: {}", CodeConfig.GLOBAL_CHAT_LEVEL);
        logger.info("접속가능 인원: {}", ServerConfig.SERVER_MAX_USERS);
    }

    public void disconnectAllCharacters() {

        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : players) {
            if (pc == null)
                continue;

            try {
                pc.disconnect();
                pc.logout();
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    public void saveAllCharInfo() {
        Collection<L1PcInstance> pList = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : pList) {
            try {
                if (pc == null) {
                    continue;
                }
                pc.save();
                pc.saveInventory();
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    public synchronized void shutdownWithCountdown(int secondsCount) {
        if (shutdownThread != null) {
            return;
        }

        shutdownThread = new ServerShutdownThread(secondsCount);

        LineageAppContext.commonTaskScheduler().execute(shutdownThread);
    }

    public void shutdown() {
        saveAllCharInfo();
        disconnectAllCharacters();

        Server.getInstance().shutDown();

        LineageAppContext.shutdown();
    }

    public synchronized void abortShutdown() {
        if (shutdownThread == null) {
            return;
        }

        shutdownThread.interrupt();
        shutdownThread = null;

        L1World world = L1World.getInstance();
        world.setWeather(0);
        world.broadcastPacketToAll(new S_Weather(0));
    }

    private class ServerShutdownThread extends Thread {
        private final int secondsCount;

        public ServerShutdownThread(int secondsCount) {
            this.secondsCount = secondsCount;
        }

        @Override
        public void run() {
            L1World world = L1World.getInstance();

            try {
                world.setWeather(3);
                world.broadcastPacketToAll(new S_Weather(3));

                String serverName = "[" + ServerConfig.SERVER_NAME + "] ";

                String msg1 = "잠시 후 서버를 종료 합니다";
                String msg2 = "안전한 장소에서 로그아웃 해 주세요";

                world.broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, serverName + msg1 + " " + msg2));

                world.broadcastServerMessage(serverName + msg1);
                world.broadcastServerMessage(serverName + msg2);

                logger.info(msg1 + " " + msg2);

                Thread.sleep(5000);

                for (int i = 0; i < secondsCount; i++) {
                    int count = secondsCount - i;

                    if (count <= 20) {
                        String msg3 = "게임이 " + count + "초 후에 종료 됩니다";

                        logger.info(msg3);
                        world.broadcastServerMessage(msg3);
                    } else {
                        if (count % 60 == 0) {
                            logger.info("게임이 {}분 후에 종료 됩니다", count / 60);
                            world.broadcastServerMessage("게임이 " + count / 60 + "분 후에 종료 됩니다");
                        }
                    }

                    Thread.sleep(1000);
                }

                shutdown();
            } catch (InterruptedException e) {
                logger.info("서버 종료가 중단되었습니다. 서버는 정상 가동중입니다.");
                world.broadcastServerMessage("서버 종료가 중단되었습니다. 서버는 정상 가동중입니다.");
            }
        }
    }
}
