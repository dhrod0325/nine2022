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

        if (command.equalsIgnoreCase("?????????")) {
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

                gm.sendPackets(String.format("????????? ????????? ?????? - ??? : %d", mapId));
            } catch (Exception e) {
                gm.sendPackets(".????????? ????????? ????????????");
            }
        } else if (command.equalsIgnoreCase("??????????????????")) {
            MapEventDropTable.getInstance().load();
            gm.sendPackets("MapEventDropTable Update Complete...");
            reloadDrop(gm);
        } else if (command.equalsIgnoreCase("????????????")) {
            try {
                int mapId = Integer.parseInt(st.nextToken());

                MapBalanceTable.getInstance().load();
                GmCommands.getInstance().handleCommands("????????? ????????? " + mapId);

                gm.sendPackets("MapBalanceTable Update Complete...");
            } catch (Exception e) {
                gm.sendPackets(".????????? ???????????? ????????????");
            }
        } else if (command.equalsIgnoreCase("??????????????????")) {
            ItemMsgTable.getInstance().load();
            gm.sendPackets("ItemMsgTable Update Complete...");
        } else if (command.equalsIgnoreCase("?????????")) {
            HuntPriceTable.getInstance().load();
            gm.sendPackets("HuntPriceTable Update Complete...");
        } else if (command.equalsIgnoreCase("???????????????")) {
            L1TimeDungeonTable.getInstance().load();

            Collection<L1PcInstance> list = L1World.getInstance().getAllPlayers();

            for (L1PcInstance pc : list) {
                pc.getTimeDungeon().loadTimeDungeon();
            }

            gm.sendPackets(new S_SystemMessage("??????????????? Update Complete..."));
        } else if (command.equalsIgnoreCase("???????????????")) {
            try {
                SpawnTable.getInstance().load();
                gm.sendPackets(new S_SystemMessage("??????????????? Update Complete..."));
            } catch (Exception e) {
                gm.sendPackets(".???????????????");
            }
        } else if (command.equalsIgnoreCase("????????????")) {
            try {
                InfinityWarTable.getInstance().load();
                gm.sendPackets(new S_SystemMessage("???????????? Update Complete..."));
            } catch (Exception e) {
                gm.sendPackets(".????????????");
            }
        } else if (arg.equalsIgnoreCase("??????????????????")) {
            HuntBookTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("?????????????????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("???")) {
            PetTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("PetTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            L1RankChecker.getInstance().load();
            gm.sendPackets(new S_SystemMessage("?????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                WarehouseManager.getInstance().reloadAll(pc.getAccountName());
            }

            ClanTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("?????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("spr")) {
            SprTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("SprTable Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            reloadDrop(gm);
        } else if (arg.equalsIgnoreCase("???????????????")) {
            WeaponDamageTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("??????????????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("????????????")) {
            ItemSkillTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("???????????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            PolyTable.getInstance().load();
            PolyImgTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("?????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
            ResolventTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("????????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
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
        } else if (arg.equalsIgnoreCase("??????")) {
            SkillsTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("Skills Reload Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
            MobSkillTable.getInstance().load();
            MobSkillInfoTable.getInstance().load();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                mi.setMobSkill(new L1MobSkillUse(mi));
            }

            gm.sendPackets(new S_SystemMessage("MobSkills Reload Complete..."));
        } else if (arg.equalsIgnoreCase("???")) {
            MapFixKeyTable.getInstance().load();
            MapsTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("Map Reload Complete..."));

            reloadDrop(gm);
        } else if (arg.equalsIgnoreCase("?????????")) {
            ItemTable.getInstance().load();
            EnchantBonusTable.getInstance().load();
            DollBonusTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("ItemTable Reload Complete..."));
        } else if (arg.equalsIgnoreCase("??????????????????")) {
            EnchantBonusTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("?????????????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
            ArmorSetTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            ShopTable.getInstance().load();
            L1RaceManager.getInstance().initShop();
            L1DogFight.getInstance().initShop();

            gm.sendPackets(new S_SystemMessage("?????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("????????????")) {
            NpcShopInfoTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("???????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("???????????????")) {
            NpcChatTable.getInstance().load();
            NPCTalkDataTable.getInstance().load();
            NpcActionTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("NpcAction Reload Complete..."));
        } else if (arg.equalsIgnoreCase("????????????")) {
            NpcTalkTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("???????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
            L1TxtChat.getInstance().load();
            L1TxtAlert.getInstance().load();
            gm.sendPackets(new S_SystemMessage("????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
            ClanTable.getInstance().load();
            HouseTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
            EnchantSettingTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("????????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            L1WorldTraps.getInstance().load();
            L1WorldTraps.getInstance().resetAllTraps();
            gm.sendPackets(new S_SystemMessage("?????? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("???")) {
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

            gm.sendPackets(new S_SystemMessage("??? Reload Complete..."));
        } else if (arg.equalsIgnoreCase("???????????????")) {
            if (spawnLoading) {
                gm.sendPackets("?????? ???????????? ??????????????????.");
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
        } else if (arg.equalsIgnoreCase("??????")) {
            GetBackTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("?????? Complete..."));
        } else if (arg.equalsIgnoreCase("????????????")) {
            GetBackRestartTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("???????????? Complete..."));
        } else if (arg.equalsIgnoreCase("????????????")) {
            WarTimeScheduler.getInstance().reload();
            gm.sendPackets(new S_SystemMessage("CastleTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("???????????????")) {
            NpcMakingTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("NpcMakingTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("?????????")) {
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
        } else if (arg.equalsIgnoreCase("?????????")) {
            CastleTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("CastleTable Update Complete..."));
        } else if (arg.equalsIgnoreCase("????????????")) {
            boolean temp = CodeConfig.STANDBY_SERVER;

            CommonCodeTable.getInstance().load();
            CodeConfig.load();

            WebServerConfig.load();

            ServerConfig.load();

            CodeConfig.STANDBY_SERVER = temp;

            BeginnerTable.getInstance().load();

            SprStrictTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("Config Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            DungeonTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("Dungeon Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????")) {
            for (L1MonsterInstance m : L1World.getInstance().getAllMonsters()) {
                if (L1BossSpawnManager.getInstance().isSpawned(m)) {
                    L1BossSpawnManager.getInstance().removeBoss(m);
                }
            }

            L1BossSpawnListHotTable.getInstance().load();

            gm.sendPackets(new S_SystemMessage("?????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("???????????????")) {
            L1PortalSystemRunner.getInstance().reLoad();
            gm.sendPackets(new S_SystemMessage("??????????????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("??????????????????")) {
            ExpTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("?????????????????? Update Complete..."));
        } else if (arg.equalsIgnoreCase("???")) {
            IpTable.getInstance().load();
            gm.sendPackets(new S_SystemMessage("??? Update Complete..."));
        } else {
            gm.sendPackets(new S_SystemMessage("\\fY--------------------------------------------------"));
            gm.sendPackets(new S_SystemMessage("   ??????, ???????????????, ??????, ?????????, ??????, ??????"));
            gm.sendPackets(new S_SystemMessage("   ???????????????, ???????????????, ?????????"));
            gm.sendPackets(new S_SystemMessage("   ??????, ??????, ???, ?????????, ??????, ????????????"));
            gm.sendPackets(new S_SystemMessage("   ??????, ?????????, ????????? , ?????????, ?????????"));
            gm.sendPackets(new S_SystemMessage("   ????????????, ??????,????????????, ???????????????"));
            gm.sendPackets(new S_SystemMessage("   ???????????????, ???????????????, ??????"));
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
