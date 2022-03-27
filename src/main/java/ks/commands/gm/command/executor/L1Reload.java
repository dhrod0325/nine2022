package ks.commands.gm.command.executor;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.commands.gm.GMCommandsUtils;
import ks.commands.gm.GmCommands;
import ks.core.datatables.*;
import ks.core.datatables.balance.MapBalanceTable;
import ks.core.datatables.boss.BossGradeDropTable;
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
import ks.core.datatables.pet.PetTable;
import ks.core.datatables.polyImg.PolyImgTable;
import ks.core.datatables.shopInfo.NpcShopInfoTable;
import ks.core.datatables.spr.SprStrictTable;
import ks.core.datatables.transform.NpcTransformTable;
import ks.core.datatables.weaponSkill.ItemSkillTable;
import ks.model.*;
import ks.model.instance.*;
import ks.model.item.L1TreasureBox;
import ks.model.item.function.L1DropItemList;
import ks.model.item.function.option.L1OptionScroll;
import ks.model.item.function.orim.L1OrimScrollEnchant;
import ks.model.pc.L1PcInstance;
import ks.model.rank.L1RankChecker;
import ks.model.trap.L1WorldTraps;
import ks.model.txt.L1TxtAlert;
import ks.model.txt.L1TxtChat;
import ks.model.warehouse.WarehouseManager;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.WarTimeScheduler;
import ks.system.boss.L1BossSpawnManager;
import ks.system.boss.table.L1BossSpawnListHotTable;
import ks.system.dogFight.L1DogFight;
import ks.system.dogFight.L1DogFightInstance;
import ks.system.infinityWar.table.InfinityWarTable;
import ks.system.lastabard.LastabardSpawnTable;
import ks.system.portalsystem.L1PortalSystemRunner;
import ks.system.race.L1RaceManager;
import ks.system.robot.RobotSpawnTable;
import ks.system.timeDungeon.L1TimeDungeonTable;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.config.WebServerConfig;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class L1Reload implements L1CommandExecutor {
    private static final L1Reload instance = new L1Reload();
    private boolean spawnLoading = false;

    private final Logger logger = LogManager.getLogger();

    private L1Reload() {
    }

    public static L1CommandExecutor getInstance() {
        return instance;
    }

    public void execute(L1PcInstance gm, String cmdName, String arg) {
        StringTokenizer st = new StringTokenizer(arg);
        String command = st.nextToken();

        if (command.equalsIgnoreCase("몬스터")) {
            try {
                NpcTransformTable.getInstance().load();
                NpcTable.getInstance().load();

                LastabardSpawnTable.getInstance().load();
                SpawnTable.getInstance().load();

                short mapId;

                if (st.hasMoreTokens()) {
                    mapId = Short.parseShort(st.nextToken());
                } else {
                    mapId = gm.getMapId();

                    if (mapId == 4) {
                        throw new Exception();
                    }
                }

                L1CommonUtils.removeSpawnByMapId(mapId);

                if (L1CommonUtils.isLastabardMap(mapId)) {
                    LastabardSpawnTable.getInstance().spawnByMapId(mapId);
                } else {
                    SpawnTable.getInstance().spawnByMapId(mapId);
                }

                gm.sendPackets(String.format("몬스터 리로드 완료 - 맵 : %d", mapId));
            } catch (Exception e) {
                gm.sendPackets(".리로드 몬스터 맵아이디");
            }
        } else if (command.equalsIgnoreCase("맵이벤트드랍")) {
            MapEventDropTable.getInstance().load();
            gm.sendPackets("MapEventDropTable Update Complete...");
            reloadDrop(gm);
        } else if (command.equalsIgnoreCase("맵밸런스")) {
            try {
                int mapId = Integer.parseInt(st.nextToken());

                MapBalanceTable.getInstance().load();
                GmCommands.getInstance().handleCommands("리로드 몬스터 " + mapId);

                gm.sendPackets("MapBalanceTable Update Complete...");
            } catch (Exception e) {
                gm.sendPackets(".리로드 맵밸런스 맵아이디");
            }
        } else if (command.equalsIgnoreCase("아이템메세지")) {
            ItemMsgTable.getInstance().load();
            gm.sendPackets("ItemMsgTable Update Complete...");
        } else if (command.equalsIgnoreCase("수배금")) {
            HuntPriceTable.getInstance().load();
            gm.sendPackets("HuntPriceTable Update Complete...");
        } else if (command.equalsIgnoreCase("시간제던전")) {
            L1TimeDungeonTable.getInstance().load();

            Collection<L1PcInstance> list = L1World.getInstance().getAllPlayers();

            for (L1PcInstance pc : list) {
                pc.getTimeDungeon().loadTimeDungeon();
            }

            gm.sendPackets(new S_SystemMessage("시간제던전 Update Complete..."));
        } else if (command.equalsIgnoreCase("스폰테이블")) {
            try {
                SpawnTable.getInstance().load();
                gm.sendPackets(new S_SystemMessage("스폰테이블 Update Complete..."));
            } catch (Exception e) {
                gm.sendPackets(".스폰테이블");
            }
        } else if (command.equalsIgnoreCase("무한대전")) {
            try {
                InfinityWarTable.getInstance().load();
                gm.sendPackets(new S_SystemMessage("무한대전 Update Complete..."));
            } catch (Exception e) {
                gm.sendPackets(".무한대전");
            }
        } else if (arg.equalsIgnoreCase("사냥터기억책")) {
            HuntBookTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("사냥터기억책 Update Complete..."));
        } else if (arg.equalsIgnoreCase("펫")) {
            PetTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("PetTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("랭킹")) {
            L1RankChecker.getInstance().load();
            gm.sendPackets(new S_SystemMessage("랭킹 Update Complete..."));
        } else if (arg.equalsIgnoreCase("창고")) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                WarehouseManager.getInstance().reloadAll(pc.getAccountName());
            }

            ClanTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("창고 Update Complete..."));
        } else if (arg.equalsIgnoreCase("spr")) {
            SprTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("SprTable Complete..."));
        } else if (arg.equalsIgnoreCase("드랍")) {
            reloadDrop(gm);
        } else if (arg.equalsIgnoreCase("무기대미지")) {
            WeaponDamageTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("무기대미지 Update Complete..."));
        } else if (arg.equalsIgnoreCase("무기스킬")) {
            ItemSkillTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("무기스킬 Update Complete..."));
        } else if (arg.equalsIgnoreCase("변신")) {
            PolyTable.getInstance().load();
            PolyImgTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("변신 Update Complete..."));
        } else if (arg.equalsIgnoreCase("용해제")) {
            ResolventTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("용해제 Update Complete..."));
        } else if (arg.equalsIgnoreCase("박스")) {
            L1TreasureBox.load();
            gm.sendPackets(new S_SystemMessage("TreasureBox Reload Complete..."));
        } else if (arg.equalsIgnoreCase("xml")) {
            L1TreasureBox.load();
            L1DropItemList.load();
            L1OrimScrollEnchant.reload();
            L1OptionScroll.getInstance().load();
            NpcActionTable.getInstance().load();
            GMCommandsUtils.load();

            gm.sendPackets(new S_SystemMessage("xml Reload Complete..."));
        } else if (arg.equalsIgnoreCase("스킬")) {
            SkillsTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("Skills Reload Complete..."));
        } else if (arg.equalsIgnoreCase("몹스킬")) {
            MobSkillTable.getInstance().load();
            MobSkillInfoTable.getInstance().load();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                mi.setMobSkill(new L1MobSkillUse(mi));
            }

            gm.sendPackets(new S_SystemMessage("MobSkills Reload Complete..."));
        } else if (arg.equalsIgnoreCase("맵")) {
            MapFixKeyTable.getInstance().load();
            MapsTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("Map Reload Complete..."));

            reloadDrop(gm);
        } else if (arg.equalsIgnoreCase("아이템")) {
            ItemTable.getInstance().load();
            EnchantBonusTable.getInstance().load();
            DollBonusTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("ItemTable Reload Complete..."));
        } else if (arg.equalsIgnoreCase("인챈트보너스")) {
            EnchantBonusTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("인챈트보너스 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("아머셋")) {
            ArmorSetTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("아머셋 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("상점")) {
            ShopTable.getInstance().load();
            L1RaceManager.getInstance().initShop();
            L1DogFight.getInstance().initShop();

            gm.sendPackets(new S_SystemMessage("상점 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("상점정보")) {
            NpcShopInfoTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("상점정보 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("엔피씨액션")) {
            NpcChatTable.getInstance().load();
            NPCTalkDataTable.getInstance().load();
            NpcActionTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("NpcAction Reload Complete..."));
        } else if (arg.equalsIgnoreCase("엔피씨톡")) {
            NpcTalkTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("엔피씨톡 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("알리미")) {
            L1TxtChat.getInstance().load();
            L1TxtAlert.getInstance().load();
            gm.sendPackets(new S_SystemMessage("알리미 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("아지트")) {
            ClanTable.getInstance().load();
            HouseTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("아지트 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("인챈트")) {
            EnchantSettingTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("인챈트 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("트랩")) {
            L1WorldTraps.getInstance().load();
            L1WorldTraps.getInstance().resetAllTraps();
            gm.sendPackets(new S_SystemMessage("트랩 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("문")) {
            for (L1DoorInstance door : L1World.getInstance().getDoors().values()) {
                door.setRespawn(false);
                door.deleteMe();
            }

            DoorSpawnTable.getInstance().load();

            for (L1DoorInstance door : L1World.getInstance().getDoors().values()) {
                List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(door);
                for (L1PcInstance pc : players) {
                    door.onPerceive(pc);
                }
            }

            gm.sendPackets(new S_SystemMessage("문 Reload Complete..."));
        } else if (arg.equalsIgnoreCase("스폰리스트")) {
            if (spawnLoading) {
                gm.sendPackets("스폰 재배치가 진행중입니다.");
                return;
            }

            LineageAppContext.commonTaskScheduler().execute(() -> {
                spawnLoading = true;

                for (L1Object npc : L1World.getInstance().getAllObject()) {
                    if (npc instanceof L1SummonInstance
                            || npc instanceof L1DoorInstance
                            || npc instanceof L1DollInstance
                            || npc instanceof L1PetInstance
                            || npc instanceof L1UserShopNpcInstance
                            || npc instanceof L1FurnitureInstance
                            || npc instanceof L1ModelInstance
                            || npc instanceof L1DogFightInstance
                            || npc instanceof L1RaceInstance) {
                        continue;
                    }

                    if (npc instanceof L1MonsterInstance) {
                        L1MonsterInstance m = (L1MonsterInstance) npc;

                        if (!L1BossSpawnManager.getInstance().isSpawned(m)) {
                            m.setRespawn(false);
                            m.deleteMe();
                        }
                    } else if (npc instanceof L1NpcInstance) {
                        L1NpcInstance m = (L1NpcInstance) npc;
                        m.setRespawn(false);
                        m.deleteMe();
                    }
                }

                NpcSpawnTable.getInstance().loadAndSpawn();
                SpawnTable.getInstance().loadAndSpawn();
                LastabardSpawnTable.getInstance().loadAndSpawn();
                RobotSpawnTable.getInstance().load();

                L1Teleport.teleport(gm, gm.getX(), gm.getY(), gm.getMapId(), gm.getHeading(), false);

                gm.sendPackets(new S_SystemMessage("spawnlist Reload Complete..."));

                spawnLoading = false;
            });
        } else if (arg.equalsIgnoreCase("겟백")) {
            GetBackTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("겟백 Complete..."));
        } else if (arg.equalsIgnoreCase("겟백리스")) {
            GetBackRestartTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("겟백리스 Complete..."));
        } else if (arg.equalsIgnoreCase("공성시간")) {
            WarTimeScheduler.getInstance().reload();
            gm.sendPackets(new S_SystemMessage("CastleTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("제작엔피씨")) {
            NpcMakingTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("NpcMakingTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("엔피씨")) {
            NpcTransformTable.getInstance().load();
            NpcTable.getInstance().load();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                L1Npc npc = NpcTable.getInstance().getTemplate(mi.getNpcId());
                mi.settingTemplate(npc);

                if (mi.isRiper()) {
                    mi.setTempLawful(0);
                    mi.setLawful(0);

                    List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(mi);

                    for (L1PcInstance pc : list) {
                        if (pc == null)
                            continue;

                        mi.onPerceive(pc);
                    }
                }
            }

            gm.sendPackets(new S_SystemMessage("NpcTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("성정보")) {
            CastleTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("CastleTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("서버설정")) {
            boolean temp = CodeConfig.STANDBY_SERVER;

            CommonCodeTable.getInstance().load();
            CodeConfig.load();

            WebServerConfig.load();

            ServerConfig.load();

            CodeConfig.STANDBY_SERVER = temp;

            BeginnerTable.getInstance().load();

            SprStrictTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("Config Update Complete..."));
        } else if (arg.equalsIgnoreCase("던전")) {
            DungeonTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("Dungeon Update Complete..."));
        } else if (arg.equalsIgnoreCase("보스")) {
            for (L1MonsterInstance m : L1World.getInstance().getAllMonsters()) {
                if (L1BossSpawnManager.getInstance().isSpawned(m)) {
                    L1BossSpawnManager.getInstance().removeBoss(m);
                }
            }

            L1BossSpawnListHotTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("보스 Update Complete..."));
        } else if (arg.equalsIgnoreCase("포털시스템")) {
            L1PortalSystemRunner.getInstance().reLoad();
            gm.sendPackets(new S_SystemMessage("포털시스템 Update Complete..."));
        } else if (arg.equalsIgnoreCase("경험치패널티")) {
            ExpTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("경험치패널티 Update Complete..."));
        } else if (arg.equalsIgnoreCase("벤")) {
            IpTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("벤 Update Complete..."));
        } else {
            gm.sendPackets(new S_SystemMessage("\\fY--------------------------------------------------"));
            gm.sendPackets(new S_SystemMessage("   드랍, 드랍아이템, 변신, 용해제, 박스, 상점"));
            gm.sendPackets(new S_SystemMessage("   스폰테이블, 스폰리스트, 몬스터"));
            gm.sendPackets(new S_SystemMessage("   균열, 스킬, 맵, 아이템, 매입, 서버설정"));
            gm.sendPackets(new S_SystemMessage("   로봇, 성정보, 인챈트 , 엔피씨, 몹스킬"));
            gm.sendPackets(new S_SystemMessage("   공성시간, 겟백,겟백리스, 엔피씨액션"));
            gm.sendPackets(new S_SystemMessage("   스폰리스트, 무기대미지, 던전"));
            gm.sendPackets(new S_SystemMessage("\\fY--------------------------------------------------"));
        }
    }

    private void reloadDrop(L1PcInstance gm) {
        DropTable.getInstance().load();
        BossGradeDropTable.getInstance().load();

        for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
            mi.getInventory().clearItems();
            mi.setDrop();
        }

        gm.sendPackets(new S_SystemMessage("DropTable Update Complete..."));
    }
}
