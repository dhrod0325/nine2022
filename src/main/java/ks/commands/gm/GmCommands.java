package ks.commands.gm;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.commands.gm.command.L1Commands;
import ks.commands.gm.command.executor.L1CommandExecutor;
import ks.constants.*;
import ks.core.GameServer;
import ks.core.auth.AuthorizationUtils;
import ks.core.datatables.*;
import ks.core.datatables.badname.BadNameTable;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.datatables.pet.PetTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.*;
import ks.model.inventory.InventoryInfoMessengerAdapter;
import ks.model.inventory.S_InventoryInfo;
import ks.model.inventory.SelectedItem;
import ks.model.item.L1TreasureBox;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillTimer;
import ks.model.skill.L1SkillUse;
import ks.packets.clientpackets.C_ShopAndWarehouse;
import ks.packets.serverpackets.*;
import ks.scheduler.WarTimeScheduler;
import ks.system.boss.L1BossSpawnManager;
import ks.system.boss.model.L1Boss;
import ks.system.boss.table.L1BossSpawnListHotTable;
import ks.system.event.TimePickupEventManager;
import ks.system.robot.L1RobotTable;
import ks.system.robot.L1RobotType;
import ks.system.robot.is.L1RobotInstance;
import ks.system.robot.model.L1RobotHuntData;
import ks.system.robot.model.L1RobotTpl;
import ks.util.L1ClanUtils;
import ks.util.L1CommonUtils;
import ks.util.L1SpawnUtils;
import ks.util.L1TeleportUtils;
import ks.util.common.IntRange;
import ks.util.common.SqlUtils;
import ks.util.common.StringUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.socket.L1WebApiUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public class GmCommands {
    private static final Logger logger = LogManager.getLogger(GmCommands.class.getName());

    public static final int ?????????????????? = 0;
    public static final int ?????????????????? = 1;
    public static final int ?????????????????? = 2;
    public static final int ?????????????????? = 3;
    public static int ?????????????????? = ??????????????????;

    private static final GmCommands instance = new GmCommands();

    private final Map<String, Boolean> nextEnchantMap = new HashMap<>();
    private final Map<String, Integer> nextDropMap = new HashMap<>();
    private final Map<String, Integer> nextDropSkillMap = new HashMap<>();

    private final List<L1NpcInstance> lineList = new CopyOnWriteArrayList<>();
    private final List<L1NpcInstance> outLineList = new CopyOnWriteArrayList<>();
    private final List<L1NpcInstance> doorList = new CopyOnWriteArrayList<>();
    private final Map<String, Logger> pcLogMap = new HashMap<>();

    private SaveLocationThread saveLocation;

    public static GmCommands getInstance() {
        return instance;
    }

    private String complementClassName(String className) {
        if (className.contains(".")) {
            return className;
        }

        return "ks.commands.gm.command.executor." + className;
    }

    private boolean executeDatabaseCommand(L1PcInstance pc, String name, String arg) {
        try {
            L1Command command = L1Commands.get(name);
            if (command == null) {
                return false;
            }
            if (pc.getAccessLevel() < command.getLevel()) {
                pc.sendPackets(new S_ServerMessage(74, "[Command] ????????? " + name));
                return true;
            }

            Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));

            L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);

            exe.execute(pc, name, arg);

            return true;
        } catch (NoSuchElementException e) {
            logger.warn("?????? ????????? ?????? - {} {} {}", pc.getName(), name, arg);
        } catch (Exception e) {
            if (pc.isGm()) {
                logger.error("??????", e);
            }
        }

        return false;
    }

    public Integer isNextDropSkill(String charName) {
        Integer result = nextDropSkillMap.get(charName);
        nextDropSkillMap.remove(charName);
        return result;
    }

    public Integer isNextDropItem(String charName) {
        Integer result = nextDropMap.get(charName);
        nextDropMap.remove(charName);

        return result;
    }

    public Boolean isEnchantOnlySuccess(String charName) {
        Boolean result = nextEnchantMap.get(charName);
        nextEnchantMap.remove(charName);
        return result;
    }

    public void handleCommands(String cmdLine) {
        L1PcInstance gm = new CharacterTable().loadCharacter("?????????");
        handleCommands(gm, cmdLine);
    }

    public Logger getPcLogger(String charName) {
        return pcLogMap.get(charName);
    }

    public void handleCommands(L1PcInstance gm, String cmdLine) {
        StringTokenizer token = new StringTokenizer(cmdLine);

        String cmd = token.nextToken();

        StringBuilder param = new StringBuilder();

        while (token.hasMoreTokens()) {
            param.append(token.nextToken()).append(' ');
        }

        param = new StringBuilder(param.toString().trim());

        if (executeDatabaseCommand(gm, cmd, param.toString())) {
            return;
        }

        if (gm.getAccessLevel() != CodeConfig.GM_CODE) {
            gm.sendPackets(new S_ServerMessage(74, "[Command] ????????? " + cmd));
            return;
        }

        // GM??? ???????????? ???????????? ????????? ??????
        if (cmd.equalsIgnoreCase("?????????")) {
            showHelp(gm);
        } else if (cmd.equalsIgnoreCase("?????????")) {
            StringTokenizer st = new StringTokenizer(param.toString());

            String state = st.nextToken();

            if ("???".equalsIgnoreCase(state)) {
                gm.getDataMap().put(L1DataMapKey.GM_DEBUG, "on");
                gm.sendPackets("????????? ????????? ?????? ???????????????");
            } else {
                gm.getDataMap().put(L1DataMapKey.GM_DEBUG, "off");
                gm.sendPackets("????????? ????????? ?????? ???????????????");
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();
                String charName = st.nextToken();

                if (type.equalsIgnoreCase("??????")) {
                    pcLogMap.put(charName, LogManager.getLogger("LOG [" + charName + "]"));
                    gm.sendPackets("???????????? " + charName + " ?????? ??????");
                } else if (type.equalsIgnoreCase("??????")) {
                    pcLogMap.remove(charName);
                    gm.sendPackets("???????????? " + charName + " ?????? ??????");
                }
            } catch (Exception e) {
                gm.sendPackets("???????????? ??????/?????? ?????????");
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String charName = st.nextToken();

                L1PcInstance pc = L1World.getInstance().getPlayer(charName);

                if (pc != null) {
                    pc.setHuntPrice(0);
                    pc.setHuntCount(0);
                    pc.save();
                    pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_HUNT);
                }

            } catch (Exception e) {
                gm.sendPackets("???????????? ?????????");
            }
        } else if (cmd.equalsIgnoreCase("??????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();
                String o = st.nextToken();

                switch (type) {
                    case "?????????": {
                        L1LogUtils.LOG_DAMAGE = o.equalsIgnoreCase("???");
                        break;
                    }
                    case "??????": {
                        L1LogUtils.LOG_SKILL = o.equalsIgnoreCase("???");
                        break;
                    }
                }

            } catch (Exception e) {
                gm.sendPackets("????????? ???????????? ???/???");
            }
        } else if (cmd.equalsIgnoreCase("HTML")) {
            StringTokenizer st = new StringTokenizer(param.toString());
            String type;

            if (st.hasMoreTokens()) {
                type = st.nextToken();
            } else {
                type = "cc_test";
            }

            gm.sendPackets(new S_ShowCCHtml(gm.getId(), type));
        } else if (cmd.equalsIgnoreCase("??????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();

                if (type.equalsIgnoreCase("??????")) {
                    showSettingInfo(gm);
                } else {
                    String value = st.nextToken();

                    if (type.equalsIgnoreCase("??????")) {
                        int v = Integer.parseInt(value);

                        gm.sendPackets(String.format("???????????? ?????? : %d -> %d", CodeConfig.MAX_LEVEL, v));

                        CodeConfig.MAX_LEVEL = v;
                        CodeConfig.store("MAX_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("?????????")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("????????? ????????? ?????? : %d -> %d", CodeConfig.EXP_GIVE_MAX_LEVEL, v));
                        CodeConfig.EXP_GIVE_MAX_LEVEL = v;
                        CodeConfig.store("EXP_GIVE_MAX_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("?????????")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("?????????????????? ?????? : %d -> %d", CodeConfig.ADEN_SELL_MIN_LEVEL, v));
                        CodeConfig.ADEN_SELL_MIN_LEVEL = v;
                        CodeConfig.store("ADEN_SELL_MIN_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("??????")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("?????? ?????? ?????? : %d -> %d", CodeConfig.GIRAN_PORTAL_NUMBER, v));
                        CodeConfig.GIRAN_PORTAL_NUMBER = v;
                        CodeConfig.store("GIRAN_PORTAL_NUMBER", v + "");
                    } else if (type.equalsIgnoreCase("???????????????")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("??????????????? ?????? : %s -> %s", CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL, value));
                        CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL = v;
                        CodeConfig.store("CASTLE_WAR_MIN_CROWN_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("????????????")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("???????????? ?????? ?????? : %s -> %s", CodeConfig.CASTLE_WAR_WINNER_ADENA, value));
                        CodeConfig.CASTLE_WAR_WINNER_ADENA = v;
                        CodeConfig.store("CASTLE_WAR_WINNER_ADENA", v + "");
                    } else if (type.equalsIgnoreCase("?????????1")) {
                        double v = Double.parseDouble(value);
                        gm.sendPackets(String.format("1?????? ?????? HP ??????  ?????? : %s -> %s", CodeConfig.BOSS_HP_VALANCE1, value));
                        CodeConfig.BOSS_HP_VALANCE1 = v;
                        CodeConfig.store("BOSS_HP_VALANCE1", v + "");
                    } else if (type.equalsIgnoreCase("?????????2")) {
                        double v = Double.parseDouble(value);
                        gm.sendPackets(String.format("2?????? ?????? HP ??????  ?????? : %s -> %s", CodeConfig.BOSS_HP_VALANCE2, value));
                        CodeConfig.BOSS_HP_VALANCE2 = v;
                        CodeConfig.store("BOSS_HP_VALANCE2", v + "");
                    } else if (type.equalsIgnoreCase("?????????3")) {
                        double v = Double.parseDouble(value);
                        gm.sendPackets(String.format("3?????? ?????? HP ??????  ?????? : %s -> %s", CodeConfig.BOSS_HP_VALANCE3, value));
                        CodeConfig.BOSS_HP_VALANCE3 = v;
                        CodeConfig.store("BOSS_HP_VALANCE3", v + "");
                    }

                }
            } catch (Exception e) {
                gm.sendPackets(".?????? ??????/??????/?????????/?????????");
                gm.sendPackets(".?????? ??????/??????1/??????2/??????3");
                gm.sendPackets(".?????? ?????????1/?????????2/?????????3");
                gm.sendPackets(".?????? ???????????????/????????????");
                gm.sendPackets(".?????? ???????????? ?????????/????????????/??????");
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                int mapId = Integer.parseInt(st.nextToken());
                int count = Integer.parseInt(st.nextToken());

                List<L1Spawn> spawnList = SpawnTable.getInstance().getSpawnListByMapId(mapId);

                if (spawnList.isEmpty()) {
                    gm.sendPackets("???????????? ?????? ???????????? ????????????");
                    return;
                }

                for (int i = 0; i < count; i++) {
                    try {
                        L1Spawn spawn = spawnList.get(RandomUtils.nextInt(spawnList.size()));

                        MapsTable.MapData m = MapsTable.getInstance().getMaps().get(mapId);
                        L1NpcInstance npc = L1SpawnUtils.randomSpawn(spawn.getTemplate().getNpcId(), m.startX, m.endX, m.startY, m.endY, (short) mapId, 0, null);

                        if (npc != null) {
                            String msg = String.format("%s(%d) (%d) ??? ??????????????????. (???:%d)", npc.getName(), npc.getId(), 1, mapId);
                            gm.sendPackets(new S_SystemMessage(msg));
                        }
                    } catch (Exception e) {
                        logger.error("??????", e);
                    }

                }

            } catch (Exception e) {
                gm.sendPackets(".???????????? mapId count");
            }

        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString(), ".");
                String text1 = "&nbsp;";
                String text2 = "&nbsp;";
                String text3 = "&nbsp;";
                String text4 = "&nbsp;";

                if (st.hasMoreTokens()) {
                    text1 = st.nextToken();
                }

                if (st.hasMoreTokens()) {
                    text2 = st.nextToken();
                }

                if (st.hasMoreTokens()) {
                    text3 = st.nextToken();
                }
                if (st.hasMoreTokens()) {
                    text4 = st.nextToken();
                }

                Collection<L1PcInstance> pcList = L1World.getInstance().getAllPlayers();

                for (L1PcInstance pc : pcList) {
                    L1World.getInstance().broadcastPacketToAll(new S_ShowCCHtml(pc.getId(), "cc_notice", text1, text2, text3, text4));
                }

            } catch (Exception e) {
                gm.sendPackets(new S_SystemMessage(".???????????? ??????"));
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String accountName = st.nextToken();

                L1Client acc = AuthorizationUtils.getInstance().getAccountsMap().get(accountName);

                if (acc != null) {
                    if (acc.getActiveChar() != null) {
                        gm.sendPackets("????????? : " + acc.getActiveChar().getName());
                    }

                    acc.disconnect();
                } else {
                    AuthorizationUtils.getInstance().getAccountsMap().remove(accountName);
                }

                gm.sendPackets("????????? " + accountName + "??? ????????? ?????????????????????");
            } catch (Exception e) {
                gm.sendPackets("???????????? ?????????");
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String state = st.nextToken();
                String name = null;

                try {
                    name = st.nextToken();
                } catch (Exception ignored) {
                }

                if (state.equalsIgnoreCase("??????")) {
                    for (String key : nextEnchantMap.keySet()) {
                        Boolean value = nextEnchantMap.get(key);
                        gm.sendPackets(key + "??? ?????? ????????? : " + (value ? "??????" : "??????"));
                    }
                } else if (state.equalsIgnoreCase("??????") || state.equalsIgnoreCase("??????")) {
                    if (name != null) {
                        boolean success = state.equalsIgnoreCase("??????");
                        nextEnchantMap.put(name, success);
                        gm.sendPackets("???????????? : [" + name + "] ????????? ???????????? ????????? " + state + "????????? ?????????????????????");
                    } else {
                        gm.sendPackets("??????????????? ???????????? ???????????????");
                    }
                } else {
                    gm.sendPackets(".???????????? ??????/??????/?????? ????????? ");
                }

            } catch (Exception e) {
                gm.sendPackets(".???????????? ??????/??????/?????? ????????? ");
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());

                gm.sendPackets(".???????????? ?????????");

                String targetName = st.nextToken();

                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                S_InventoryInfo packet = new S_InventoryInfo(target);
                packet.setMessenger(new NextDropItemSelectedAction(target, gm));
                packet.build();

                gm.sendPackets("???????????? ???????????????");
                gm.sendPackets(packet);
            } catch (Exception ignored) {
            }
        } else if (cmd.equalsIgnoreCase("??????????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String name = st.nextToken();
                String skillName = st.nextToken();

                int skillId;

                try {
                    skillId = Integer.parseInt(skillName);
                } catch (NumberFormatException e) {
                    skillId = SkillsTable.getInstance().findSkillIdByNameWithoutSpace(skillName);

                    if (skillId == 0) {
                        gm.sendPackets(new S_SystemMessage("?????? ????????? ???????????? ????????????. "));
                        return;
                    }
                }

                L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

                if (skill == null) {
                    gm.sendPackets(new S_SystemMessage("?????? ????????? ???????????? ????????????. "));
                    return;
                }

                nextDropSkillMap.put(name, skillId);

                gm.sendPackets(name + "??? ?????? ??????????????? " + skill.getName() + "??? ?????????????????????");
            } catch (Exception e) {
                gm.sendPackets(".?????????????????? ????????? ?????????/??????ID");

                for (String key : nextDropSkillMap.keySet()) {
                    Integer value = nextDropSkillMap.get(key);
                    gm.sendPackets(key + "??? ?????? ???????????? : " + SkillsTable.getInstance().getTemplate(value).getName());
                }
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            tournament(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("?????????")) {
            dmgScarecrow(gm);
        } else if (cmd.equalsIgnoreCase("html")) {
            StringTokenizer st = new StringTokenizer(param.toString());
            String html = st.nextToken();
            gm.sendPackets(new S_ShowCCHtml(gm.getId(), html, "5900", "5901", "5902"));
        } else if (cmd.equalsIgnoreCase("????????????")) {
            HcPacketStop(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            allPresent(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("?????????")) {
            thread(gm);
        } else if (cmd.equalsIgnoreCase("?????????")) {
            event(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            safeMode(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????")) {
            gm.sendPackets("\\f1f1\\f2f2\\f3f3\\f4f4\\f5f5\\f6f6\\f7f7");
            gm.sendPackets("\\fRfR\\fSfS\\fTfT\\fUfU\\fVfV\\fWfW\\fXfX\\fYfY");
        } else if (cmd.equalsIgnoreCase("??????")) {
            try {
                gm.sendPackets(new S_SystemMessage("---------------????????? ??????-------------\n"));

                for (L1MonsterInstance bs : L1BossSpawnManager.getInstance().getBossList()) {
                    gm.sendPackets(new S_SystemMessage(bs.getName() + " x : " + bs.getX() + " Y : " + bs.getY() + " MAP : " + bs.getMapId()));
                }

                gm.sendPackets(new S_SystemMessage("---------------------------------------\n"));
            } catch (Exception e) {
                gm.sendPackets(".?????? ");
            }

        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                int npcId = Integer.parseInt(st.nextToken());
                int deleteTime = Integer.parseInt(st.nextToken());

                L1Boss bs = L1BossSpawnListHotTable.getInstance().findByNpcId(npcId);
                L1BossSpawnManager.getInstance().addBoss(bs, 1000L * 60 * deleteTime);

                gm.sendPackets(bs.getMonName() + "??? ?????????????????????");
            } catch (Exception e) {
                gm.sendPackets(".???????????? NPCID ????????????(???)");
            }
        } else if (cmd.equalsIgnoreCase("??????")) {
            sprImageCheck(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("???????????????")) {
            skillIconCheck(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            serverPacketCheck(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????")) {
            noCall(gm, param.toString());
        } else if (cmd.startsWith("????????????")) {
            stopWar(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            userSummon(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????")) {
            clear(gm);
        } else if (cmd.equalsIgnoreCase("?????????")) {
            sound(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("?????????")) {
            effect(gm, param.toString());
        } else if (cmd.startsWith("????????????")) {
            saveServer(gm);
        } else if (cmd.equalsIgnoreCase("????????????")) {
            allrecall(gm);
        } else if (cmd.equalsIgnoreCase("??????")) {
            givesItem2(gm, param.toString());
        } else if (cmd.equals("????????????")) {
            castleWarStart(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            chatX(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            checkEnchant(gm, param.toString());
        } else if (cmd.equals("???????????????")) {
            giveHouse(gm, param.toString());
        } else if (cmd.equals("??????2")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String name = st.nextToken();
                int time = Integer.parseInt(st.nextToken());

                L1PcInstance tg = L1World.getInstance().getPlayer(name);

                if (tg != null) {
                    tg.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, time * 1000);
                    tg.sendPackets(new S_SkillIconGFX(L1SkillIcon.??????, time / 60));
                    tg.sendPackets("?????? ?????? : ????????? ??? ??? ?????? (" + time + "???)");
                    L1World.getInstance().broadcastServerMessage("????????? ???????????? ?????? ???????????? ????????? " + name + "??? ????????? " + time + "?????? ???????????????.");
                    L1World.getInstance().broadcastServerMessage("?????? ????????? 400% ????????? ???????????????.");
                }
            } catch (Exception e) {
                gm.sendPackets(new S_SystemMessage("??????2 [????????????] [??????(???)] ????????? ????????? ?????????. "));
            }
        } else if (cmd.equals("???????????????")) {
            Collection<L1House> houseList = HouseTable.getInstance().getHouseTableList();

            HashMap<Integer, L1Clan> clanList = ClanTable.getInstance().getClans();
            List<L1House> remaningAgits = new ArrayList<>();

            for (L1House house : houseList) {
                boolean isExists = false;

                for (L1Clan clan : clanList.values()) {
                    if (clan.getHouseId() == house.getHouseId()) {
                        isExists = true;
                        break;
                    }
                }

                if (!isExists) {
                    remaningAgits.add(house);
                }
            }

            gm.sendPackets("# ??????????????? ????????? ????????? #");

            for (L1House house : remaningAgits) {
                gm.sendPackets("houseId : " + house.getHouseId() + "," + house.getHouseArea() + "???, ???????????? : " + house.getHouseName());
            }

        } else if (cmd.equalsIgnoreCase("??????")) {
            levelup3(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            checkAden(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            standBy(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) { // ????????????
            setMaxLevel(gm, param.toString());
        } else if (cmd.startsWith("???????????????")) {
            returnEXP(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????")) {
            L1Teleport.teleport(gm, 32736, 32799, (short) 34, 5, true);
        } else if (cmd.equalsIgnoreCase("????????????")) {
            changePassword(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("?????????")) {
            memFree(gm);
        } else if (cmd.equalsIgnoreCase("??????")) {
            rate(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) { // by????????????
            if (CodeConfig.IS_GM_CHAT) {
                CodeConfig.IS_GM_CHAT = false;
                gm.sendPackets(new S_SystemMessage("???????????? OFF"));
            } else {
                CodeConfig.IS_GM_CHAT = true;
                gm.sendPackets(new S_SystemMessage("???????????? ON"));
            }
        } else if (cmd.equalsIgnoreCase("?????????")) {
            clanMark(gm);
        } else if (cmd.equalsIgnoreCase("??????")) {
            packet(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????1")) {
            packet1(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????2")) {
            packet2(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            inventoryDelete(gm);
        } else if (cmd.equalsIgnoreCase("?????????")) {
            AllPlayerList(gm);
        } else if (cmd.equalsIgnoreCase("????????????")) {
            callClan(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            String msg = "[" + ServerConfig.SERVER_NAME + "] " + param;

            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, msg));
            L1World.getInstance().broadcastServerMessage("****** : " + param);

            L1WebApiUtils.chatLog("100", gm.getName(), "[??????]", param.toString());
        } else if (cmd.equalsIgnoreCase("??????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());

                ExpTable expTable = ExpTable.getInstance();

                int petObjectId = Integer.parseInt(st.nextToken());
                int level = Integer.parseInt(st.nextToken());

                int totalExp = expTable.getExpByLevel(level) - 1;

                L1Object o = L1World.getInstance().findObject(petObjectId);

                if (o instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) o;
                    L1PcInstance pc = (L1PcInstance) pet.getMaster();

                    int levelBefore = pet.getLevel();

                    pet.setExp(totalExp);
                    pet.setLevel(expTable.getLevelByExp(totalExp));

                    int expPercentage = expTable.getExpPercentage(pet.getLevel(), totalExp);
                    int gap = pet.getLevel() - levelBefore;

                    for (int i = 1; i <= gap; i++) {
                        IntRange hpUpRange = pet.getPetType().getHpUpRange();
                        IntRange mpUpRange = pet.getPetType().getMpUpRange();
                        pet.addMaxHp(hpUpRange.randomValue());
                        pet.addMaxMp(mpUpRange.randomValue());
                    }

                    pet.setExpPercent(expPercentage);
                    pc.sendPackets(new S_PetPack(pet, pc));

                    if (gap != 0) { // ???????????????(???) DB??? ????????????
                        L1Pet petTemplate = PetTable.getInstance().getTemplate(pet.getItemObjId());

                        if (petTemplate == null) { // PetTable??? ??????
                            logger.warn("L1Pet == null");
                            return;
                        }

                        petTemplate.setExp(pet.getExp());
                        petTemplate.setLevel(pet.getLevel());
                        petTemplate.setHp(pet.getMaxHp());
                        petTemplate.setMp(pet.getMaxMp());
                        PetTable.getInstance().storePet(petTemplate);
                        pc.sendPackets(new S_ServerMessage(320, pet.getName()));
                    }
                }
            } catch (Exception e) {
                gm.sendPackets("?????? ????????????????????? ??????");
                logger.error("??????", e);
            }
        } else if (cmd.equalsIgnoreCase("??????")) {
            StringTokenizer st = new StringTokenizer(param.toString());
            String pcName = st.nextToken();

            L1PcInstance pc = L1World.getInstance().getPlayer(pcName);

            if (pc != null) {
                int dis = gm.getLocation().getTileLineDistance(pc.getLocation());
                System.out.println(dis);
            }

        } else if (cmd.equalsIgnoreCase("????????????")) {
            Map<Integer, L1SkillTimer> e = gm.getSkillEffectTimerSet().getSkillEffect();

            for (Integer skillId : e.keySet()) {
                L1SkillTimer k = e.get(skillId);

                if (k != null) {
                    String msg = "skillId : " + skillId + ", time : " + k.getRemainingTime();
                    gm.sendPackets(msg);
                }
            }
        } else if (cmd.equalsIgnoreCase("?????????2")) {
            StringTokenizer st = new StringTokenizer(param.toString());

            int t1 = Integer.parseInt(st.nextToken());

            gm.sendPackets(new S_PacketBox(L1PacketBoxType.EFFECT_ICON, t1, 1800 * 1000));
        } else if (cmd.equalsIgnoreCase("?????????")) {
            StringTokenizer st = new StringTokenizer(param.toString());

            int type = Integer.parseInt(st.nextToken());

            gm.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, type, true));

        } else if (cmd.equalsIgnoreCase("???")) {
            StringTokenizer st = new StringTokenizer(param.toString());
            int opcode = Integer.parseInt(st.nextToken());

            L1ItemInstance item = gm.getInventory().getItem(272292931);

            System.out.println("op : " + opcode);

            ServerPacket serverPacket = new ServerPacket();

            serverPacket.writeC(opcode);
            serverPacket.writeC(0x42);
            serverPacket.writeD(item.getItemId());
            serverPacket.writeC(0);

            if (item.isEquipped()) {
                serverPacket.writeC(1);
            } else {
                serverPacket.writeC(0);
            }

            gm.sendPackets(serverPacket); // ???4
        } else if (cmd.equalsIgnoreCase("??????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                int time = Integer.parseInt(st.nextToken());

                gm.sendPackets(new S_SkillIconWindShackle(gm.getId(), time));
            } catch (Exception e) {
                logger.error(e);
            }
        } else if (cmd.equalsIgnoreCase("??????????????????")) {
            resolventSell(gm);
        } else if (cmd.equalsIgnoreCase("????????????")) {
            String finalParam = param.toString();
            LineageAppContext.commonTaskScheduler().execute(() -> bugCheck(gm, finalParam));
        } else if ("????????????".equalsIgnoreCase(cmd)) {
            GameServer.getInstance().shutdownWithCountdown(20);
        } else if (cmd.equalsIgnoreCase("??????")) {
            searchDatabase(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("??????")) {
            recallItem(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer tok = new StringTokenizer(param.toString());
                String cmdType = tok.nextToken();

                if ("???".equalsIgnoreCase(cmdType)) {
                    CodeConfig.SERVER_STATUS = 0;
                    gm.sendPackets("????????? ????????? ????????? ??? ?????? ????????? ?????????????????????");
                } else {
                    CodeConfig.SERVER_STATUS = 1;
                    gm.sendPackets("GM ????????? ????????? ????????? ??? ?????? ????????? ?????????????????????");
                }
            } catch (Exception e) {
                gm.sendPackets(".???????????? ???/???");
            }

        } else if (cmd.equalsIgnoreCase("???????????????")) {
            List<L1MonsterInstance> mis = new ArrayList<>();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                if (mi.getNpcId() == 45590 || mi.getTemplate().getTransformId() == 45590) {
                    mis.add(mi);
                }
            }

            gm.sendPackets("---?????????---");

            for (L1MonsterInstance mi : mis) {
                String mapName = MapsTable.getInstance().getMapName(mi.getMapId());
                String msg = mapName + " : " + mi.getName() + " x : " + mi.getX() + " Y : " + mi.getY() + " MAP : " + mi.getMapId();

                String type = null;

                if (mi.getNpcId() == 45590) {
                    type = "???????????????";
                } else if (mi.getTemplate().getTransformId() == 45590) {
                    type = "????????????";
                }

                gm.sendPackets(new S_SystemMessage(type + ":" + msg));
            }

            if (mis.isEmpty()) {
                gm.sendPackets("????????? ???????????? ????????????");
            }
        } else if (cmd.equalsIgnoreCase("???????????????")) {
            List<L1MonsterInstance> mis = new ArrayList<>();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                if (mi.isRiper()) {
                    mis.add(mi);
                }
            }

            gm.sendPackets("--- ????????? ?????? ---");

            for (L1MonsterInstance mi : mis) {
                gm.sendPackets(new S_SystemMessage(mi.getName() + " x : " + mi.getX() + " Y : " + mi.getY() + " MAP : " + mi.getMapId()));
            }

            if (mis.isEmpty()) {
                gm.sendPackets("????????? ?????????????????? ????????????");
            }
        } else if (cmd.equalsIgnoreCase("???????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String targetName = st.nextToken();
                String changeName = st.nextToken();

                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                if (target == null) {
                    gm.sendPackets(targetName + "?????? ???????????? ????????????");
                    return;
                }

                boolean exists = CharacterTable.getInstance().doesCharNameExist(changeName);

                if (exists) {
                    gm.sendPackets("?????? ???????????? ??????????????????");
                    return;
                }

                if (target.getClanId() > 0) {
                    L1Clan clan = target.getClan();

                    if (target.isCrown() && target.getId() == clan.getLeaderId()) {
                        L1ClanUtils.leaveClanBoss(clan, target);
                    } else {
                        L1ClanUtils.leaveClanMember(clan, target);
                    }
                }

                L1World.getInstance().broadcastServerMessage(String.format("[???????????????] : %s -> %s", targetName, changeName));
                CharacterTable.getInstance().updateCharName(changeName, targetName);
                target.getClient().disconnectNow();
            } catch (Exception e) {
                gm.sendPackets(".??????????????? ?????????");
            }
        } else if (cmd.equalsIgnoreCase("???????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String targetName = st.nextToken();

                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                if (target == null) {
                    gm.sendPackets(targetName + "?????? ???????????? ????????????");
                    return;
                }

                if (target.getClanId() > 0) {
                    L1Clan clan = target.getClan();

                    if (target.isCrown() && target.getId() == clan.getLeaderId()) {
                        L1ClanUtils.leaveClanBoss(clan, target);
                    } else {
                        L1ClanUtils.leaveClanMember(clan, target);
                    }
                }

                int number = BadNameTable.getInstance().nextId();
                String appendId = org.apache.commons.lang3.StringUtils.leftPad(number + "", 2, "0");
                String changeName = "???????????????" + appendId;

                L1World.getInstance().broadcastServerMessage(String.format("[?????????????????????] : %s -> %s", targetName, changeName));
                BadNameTable.getInstance().insert(targetName, target.getId());
                CharacterTable.getInstance().updateCharName(changeName, targetName);
                target.getClient().disconnectNow();
            } catch (Exception e) {
                gm.sendPackets(".??????????????? ?????????");
            }
        } else if ("????????????".equalsIgnoreCase(cmd)) {
            try {
                StringTokenizer tok = new StringTokenizer(param.toString());
                String cmdType = tok.nextToken();

                if ("???".equalsIgnoreCase(cmdType)) {
                    gm.getDataMap().put(L1DataMapKey.AUTO_LOOT, "true");
                    gm.sendPackets("???????????? ??????????????? ?????????????????????");
                } else if ("???".equalsIgnoreCase(cmdType)) {
                    gm.getDataMap().put(L1DataMapKey.AUTO_LOOT, "false");
                    gm.sendPackets("???????????? ??????????????? ?????????????????????");
                }
            } catch (Exception e) {
                gm.sendPackets(".???????????? ???/???/????????????/??????");

                logger.error("??????", e);
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer tok = new StringTokenizer(param.toString());
                String cmdType = tok.nextToken();

                if ("???".equalsIgnoreCase(cmdType)) {
                    TimePickupEventManager.getInstance().start();
                } else if ("???".equalsIgnoreCase(cmdType)) {
                    TimePickupEventManager.getInstance().stop();
                } else if ("????????????".equals(cmdType)) {
                    TimePickupEventManager.getInstance().expendDrop();
                } else if ("????????????".equals(cmdType)) {
                    TimePickupEventManager.getInstance().clearDrop();
                }
            } catch (Exception e) {
                gm.sendPackets(".???????????? ???/???/????????????/??????");

                logger.error("??????", e);
            }
        } else if (cmd.equalsIgnoreCase("??????1")) {
            StringTokenizer tok = new StringTokenizer(param.toString());
            String cmdType = tok.nextToken();

            if ("????????????".equalsIgnoreCase(cmdType)) {
                Thread t = new Thread(() -> {
                    for (L1RobotTpl tpl : L1RobotTable.getInstance().getCachedRobotCharacters()) {
                        if (tpl.getHuntId() == 0) {
                            continue;
                        }

                        if (L1World.getInstance().getRobot(tpl.getName()) != null) {
                            continue;
                        }

                        L1RobotInstance robot = L1RobotTable.getInstance().createRobot(tpl.getName());

                        if (robot != null) {
                            L1World.getInstance().storeObject(robot);
                            L1World.getInstance().addVisibleObject(robot);

                            gm.sendPackets("[??????] " + robot.getName() + " - ?????? ??????");

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                logger.error("??????", e);
                            }
                        }
                    }

                    L1RobotHuntData huntData = new L1RobotHuntData();
                    huntData.setEndCheck(0);
                    huntData.setStartTime(new Date());

                    L1RobotTable.getInstance().insertHuntData(huntData);

                    gm.sendPackets("?????? ????????? ?????????????????????.");
                });

                LineageAppContext.commonTaskScheduler().execute(t);
            } else if ("????????????".equalsIgnoreCase(cmdType)) {
                for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
                    if (robot.getRobotType() == L1RobotType.HUNT) {
                        gm.sendPackets(robot.getName() + " ?????? ??????");
                        robot.logout();
                    }
                }

                L1RobotTable.getInstance().endHuntTime();

                gm.sendPackets("??????????????? ?????? ?????????????????????");
            } else if ("????????????".equalsIgnoreCase(cmdType)) {
                int locId = 0;

                try {
                    locId = Integer.parseInt(tok.nextToken());
                } catch (Exception ignored) {
                }

                if (saveLocation != null) {
                    if (saveLocation.isIng()) {
                        gm.sendPackets("?????? ??????????????????");
                        return;
                    }
                }

                if (locId == 0) {
                    gm.sendPackets("????????? locId ?????????");
                    return;
                }

                saveLocation = new SaveLocationThread(gm, locId);
                saveLocation.start();

                gm.sendPackets("??????????????? ????????????????????? : " + saveLocation.getLocId());
            } else if ("??????????????????".equalsIgnoreCase(cmdType)) {
                if (saveLocation != null) {
                    gm.sendPackets("???????????? ?????? locId : " + saveLocation.getLocId());

                    saveLocation.stop();
                    saveLocation = null;
                }
            } else if ("?????????".equalsIgnoreCase(cmdType)) {
                L1RobotTable.getInstance().load();
                gm.sendPackets("?????? ????????? ??????");
            } else if ("????????????".equalsIgnoreCase(cmdType)) {
                int num = 1;

                try {
                    num = Integer.parseInt(tok.nextToken());
                } catch (Exception ignored) {

                }

                for (int z = 0; z < num; z++) {
                    String name = L1RobotTable.getInstance().findFirstRobotName();
                    L1RobotInstance robot = L1RobotTable.getInstance().createRobot(name);

                    int[] loc = {-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};

                    if (robot != null) {
                        for (int i = 0; i < 50; i++) {
                            int x = loc[RandomUtils.nextInt(17)];
                            int y = loc[RandomUtils.nextInt(17)];

                            robot.setX(gm.getX() + x);
                            robot.setY(gm.getY() + y);
                            robot.setMap(gm.getMapId());

                            if (gm.getMap().isPassable(robot.getX(), robot.getY())) {
                                break;
                            }
                        }

                        L1World.getInstance().storeObject(robot);
                        L1World.getInstance().addVisibleObject(robot);

                        gm.sendPackets("[??????] " + robot.getName() + " - ???????????? ??????");
                    }
                }
            } else if ("??????".equalsIgnoreCase(cmdType)) {
                int num = 1;

                try {
                    num = Integer.parseInt(tok.nextToken());
                } catch (Exception ignored) {

                }

                for (int z = 0; z < num; z++) {
                    String name = L1RobotTable.getInstance().findFirstRobotName();
                    L1RobotInstance robot = L1RobotTable.getInstance().createRobot(name);

                    int[] loc = {-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8};

                    if (robot != null) {
                        robot.setRobotType(L1RobotType.JOMBI);

                        for (int i = 0; i < 50; i++) {
                            int x = loc[RandomUtils.nextInt(17)];
                            int y = loc[RandomUtils.nextInt(17)];

                            robot.setX(gm.getX() + x);
                            robot.setY(gm.getY() + y);
                            robot.setMap(gm.getMapId());

                            if (gm.getMap().isPassable(robot.getX(), robot.getY())) {
                                break;
                            }
                        }

                        L1World.getInstance().storeObject(robot);
                        L1World.getInstance().addVisibleObject(robot);

                        gm.sendPackets("[??????] " + robot.getName() + " - ?????? ??????");
                    }
                }
            } else {
                L1RobotInstance robot = L1RobotTable.getInstance().createRobot(cmdType);

                if (robot != null) {
                    L1World.getInstance().storeObject(robot);
                    L1World.getInstance().addVisibleObject(robot);

                    gm.sendPackets(robot.getName() + " ?????? ?????? ??????");
                }
            }
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();
                String type2 = st.nextToken();

                int price = -1;

                String msg;

                if (type2.equalsIgnoreCase("???")) {
                    msg = "??????";
                    price = 1;
                } else {
                    msg = "??????";
                }

                int itemId = 0;

                String sql = "UPDATE SHOP SET SELLING_PRICE = ? WHERE npc_id = 460000117 AND item_id=?";

                if ("??????".equalsIgnoreCase(type)) {
                    itemId = 40921;
                } else if ("????????????".equalsIgnoreCase(type)) {
                    itemId = 60001212;
                } else if ("????????????".equalsIgnoreCase(type)) {
                    itemId = 6000112;

                    if (type2.equalsIgnoreCase("???")) {
                        price = 5;
                    } else {
                        price = -1;
                    }
                }

                SqlUtils.update(sql, price, itemId);

                handleCommands("????????? ??????");

                L1Item item = ItemTable.getInstance().getTemplate(itemId);

                gm.sendPackets("??????????????? " + item.getName() + " " + msg + " ??????");
            } catch (Exception e) {
                gm.sendPackets(".???????????? ??????/????????????/???????????? ???/???");
            }
        } else {
            gm.sendPackets(new S_SystemMessage("????????? " + cmd + "??? ???????????? ????????????. "));
        }
    }

    private void showSettingInfo(L1PcInstance gm) {
        gm.sendPackets("?????? : " + CodeConfig.MAX_LEVEL);
        gm.sendPackets("????????? : " + CodeConfig.EXP_GIVE_MAX_LEVEL);
        gm.sendPackets("?????? : " + CodeConfig.GIRAN_PORTAL_NUMBER);
        gm.sendPackets("?????????1 : " + CodeConfig.BOSS_HP_VALANCE1);
        gm.sendPackets("?????????2 : " + CodeConfig.BOSS_HP_VALANCE2);
        gm.sendPackets("?????????3 : " + CodeConfig.BOSS_HP_VALANCE3);
        gm.sendPackets("?????????????????? : " + CodeConfig.ADEN_SELL_MIN_LEVEL);
        gm.sendPackets("??????????????? : " + CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL);
        gm.sendPackets("???????????? : " + NumberFormat.getInstance().format(CodeConfig.CASTLE_WAR_WINNER_ADENA));
    }

    private void tournament(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String type = st.nextToken();

            if ("??????".equalsIgnoreCase(type)) {
                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance pc : players) {
                    if (pc.getMapId() == 90) {
                        if (pc.getX() >= 32689 && pc.getX() <= 32710 & pc.getY() >= 32885 && pc.getY() <= 32906) {
                            new L1SkillUse(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0).run();
                            new L1SkillUse(pc, L1SkillId.BRAVE_MENTAL, pc.getId(), pc.getX(), pc.getY(), 0).run();
                            new L1SkillUse(pc, L1SkillId.SHINING_SHILELD, pc.getId(), pc.getX(), pc.getY(), 0).run();
                        }
                    }
                }
            } else if ("??????".equalsIgnoreCase(type)) {
                if (?????????????????? == ??????????????????) {
                    gm.sendPackets("?????? ??????????????? ??????????????????");
                    return;
                }

                ?????????????????? = ??????????????????;

                L1World.getInstance().broadcastPacketGreenMessage("??????????????? ?????????????????????");

                if (!outLineList.isEmpty()) {
                    for (L1NpcInstance o : outLineList) {
                        o.deleteMe();
                        outLineList.remove(o);
                    }
                }

                for (int x = 32689; x <= 32710; x++) {
                    L1NpcInstance o1 = L1SpawnUtils.spawn(x, 32885, (short) 90, 0, 80125, 0, 0, null);
                    outLineList.add(o1);

                    L1NpcInstance o2 = L1SpawnUtils.spawn(x, 32906, (short) 90, 0, 80125, 0, 0, null);
                    outLineList.add(o2);
                }

                for (int y = 32885; y <= 32906; y++) {
                    if (y == 32895)
                        continue;

                    L1NpcInstance o1 = L1SpawnUtils.spawn(32689, y, (short) 90, 0, 80125, 0, 0, null);
                    outLineList.add(o1);

                    L1NpcInstance o2 = L1SpawnUtils.spawn(32710, y, (short) 90, 0, 80125, 0, 0, null);
                    outLineList.add(o2);
                }

                for (L1NpcInstance o : outLineList) {
                    o.getMap().setAttackAble(o.getX(), o.getY(), false);
                }


                for (int y = 32885; y <= 32906; y++) {
                    L1NpcInstance o = L1SpawnUtils.spawn(32700, y, (short) 90, 0, 80125, 0, 0, null);
                    lineList.add(o);

                    if (o != null) {
                        o.getMap().setAttackAble(o.getX(), o.getY(), false);
                    }
                }
            } else if ("??????".equalsIgnoreCase(type)) {
                if (?????????????????? != ??????????????????) {
                    gm.sendPackets("??????????????? ???????????? ????????????");
                    return;
                }

                ?????????????????? = ??????????????????;

                new Thread(() -> {
                    L1World.getInstance().broadcastPacketGreenMessage("??????????????? ????????? ???????????????");

                    L1NpcInstance o2 = L1SpawnUtils.spawn(32710, 32895, (short) 90, 0, 80125, 0, 0, null);
                    L1NpcInstance o3 = L1SpawnUtils.spawn(32689, 32895, (short) 90, 0, 80125, 0, 0, null);

                    doorList.add(o2);
                    doorList.add(o3);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        logger.error("??????", e);
                    }

                    for (int i = 15; i > 0; i--) {
                        try {
                            L1World.getInstance().broadcastPacketGreenMessage(i + "?????? ??????????????? ???????????????");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            logger.error("??????", e);
                        }
                    }

                    L1World.getInstance().broadcastPacketGreenMessage("??????????????? ?????????????????????");

                    for (L1NpcInstance o : lineList) {
                        o.getMap().setAttackAble(o.getX(), o.getY(), true);
                        o.deleteMe();
                        lineList.remove(o);
                    }
                }).start();
            } else if ("??????".equalsIgnoreCase(type)) {
                L1World.getInstance().broadcastPacketGreenMessage("??????????????? ?????????????????????");

                for (L1NpcInstance o : lineList) {
                    o.getMap().setAttackAble(o.getX(), o.getY(), true);
                    o.deleteMe();
                    lineList.remove(o);
                }

                for (L1NpcInstance o : doorList) {
                    o.getMap().setAttackAble(o.getX(), o.getY(), true);
                    o.deleteMe();
                    doorList.remove(o);
                }

                ?????????????????? = ??????????????????;
            } else if ("??????".equalsIgnoreCase(type)) {
                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance player : players) {
                    if (player.getMapId() == 90 && !player.isGm()) {
                        L1TeleportUtils.teleportToGiran(player);
                    }
                }

                ?????????????????? = ??????????????????;
            }
        } catch (Exception e) {
            gm.sendPackets(".???????????? ??????/??????/??????/??????/??????");
        }
    }

    private void event(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String type = st.nextToken();
            String option = st.nextToken();

            String msg = "";

            if ("??????".equalsIgnoreCase(type)) {
                if ("???".equalsIgnoreCase(option)) {
                    msg = "????????? ???????????? ?????????????????????. ?????????????????????????????? ???????????? ??? ????????????";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=20000000*pack_count WHERE NOTE='??????' and npc_id=460000090";
                    SqlUtils.update(sql);
                } else if ("???".equalsIgnoreCase(option)) {
                    msg = "????????? ???????????? ?????????????????????.";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=-1 WHERE NOTE='??????' and npc_id=460000090";
                    SqlUtils.update(sql);
                }

                L1World.getInstance().broadcastPacketGreenMessage(msg);
                L1World.getInstance().broadcastServerMessage(msg);

                ShopTable.getInstance().load();
            } else if ("??????".equalsIgnoreCase(type)) {
                if ("???".equalsIgnoreCase(option)) {
                    msg = "????????? ?????? ???????????? ?????????????????????. ?????????????????????????????? ???????????? ??? ????????????";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=200000*pack_count WHERE NOTE='??????' and npc_id=460000090";
                    SqlUtils.update(sql);
                } else if ("???".equalsIgnoreCase(option)) {
                    msg = "????????? ?????? ???????????? ?????????????????????.";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=-1 WHERE NOTE='??????' and npc_id=460000090";
                    SqlUtils.update(sql);
                }

                L1World.getInstance().broadcastPacketGreenMessage(msg);
                L1World.getInstance().broadcastServerMessage(msg);

                ShopTable.getInstance().load();
            } else if ("?????????".equalsIgnoreCase(type)) {
                if ("???".equalsIgnoreCase(option)) {
                    msg = "????????? ????????? ???????????? ?????????????????????. ?????????????????????????????? ???????????? ??? ????????????";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=200000*pack_count WHERE NOTE='?????????' and npc_id=460000090";
                    SqlUtils.update(sql);
                } else if ("???".equalsIgnoreCase(option)) {
                    msg = "????????? ????????? ???????????? ?????????????????????.";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=-1 WHERE NOTE='?????????' and npc_id=460000090";
                    SqlUtils.update(sql);
                }

                L1World.getInstance().broadcastPacketGreenMessage(msg);
                L1World.getInstance().broadcastServerMessage(msg);

                ShopTable.getInstance().load();
            }
        } catch (Exception e) {
            gm.sendPackets(".????????? ??????/??????/????????? ???/???");
        }
    }

    private void recallItem(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(name);

            if (target == null) {
                gm.sendPackets("????????? ?????? ?????? ??????????????????");
                return;
            }

            S_InventoryInfo packet = new S_InventoryInfo(target);
            packet.setMessenger(new RecallItemSelectedAction(target, gm));
            packet.build();

            gm.sendPackets("????????? ???????????? ????????? ???????????????");
            gm.sendPackets(packet);
        } catch (Exception e) {
            gm.sendPackets(".?????? ????????????");
        }
    }

    private void resolventSell(L1PcInstance gm) {
        try {
            SqlUtils.update("DELETE FROM shop WHERE ITEM_ID != 41246 and npc_id=70076");

            String sql = "SELECT * FROM resolvent where crystal_count > 0";

            List<Map<String, Object>> resolventList = SqlUtils.queryForList(sql);

            int orderId = 0;

            for (Map<String, Object> resolvent : resolventList) {
                int itemId = Integer.parseInt(resolvent.get("item_id").toString());
                int crystalCount = Integer.parseInt(resolvent.get("crystal_count").toString()) * CodeConfig.RATE_CRISTAL;

                if (crystalCount == 0)
                    continue;

                sql = "INSERT INTO shop (npc_id,item_id,order_id,purchasing_price) values (70076,?,?,?)";

                SqlUtils.update(sql, itemId, orderId, crystalCount * 5);

                orderId++;
            }

            ShopTable.getInstance().load();

            gm.sendPackets("???????????? ?????????????????????.");
        } catch (Exception e) {
            logger.error("??????", e);
            gm.sendPackets("????????? ??????");
        }
    }

    private void skillIconCheck(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int iconNumber = Integer.parseInt(st.nextToken());
            int repeatCount = 0;

            try {
                repeatCount = Integer.parseInt(st.nextToken());
            } catch (Exception ignored) {
            }

            int finalRepeatCount = repeatCount;

            new Thread(() -> {
                try {
                    for (int i = 0; i < finalRepeatCount + 1; i++) {
                        Thread.sleep(500);
                        int num = i + iconNumber;
                        pc.sendPackets(new S_SkillIconAura(num, 5));
                        logger.info("???????????? : {} ", num);
                    }
                } catch (Exception e) {
                    logger.error("??????", e);
                }
            }).start();
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("[Command] .??????????????? [??????] [??????] ??????"));
        }
    }

    private void bugCheck(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = null;

            if (tok.hasMoreTokens()) {
                type = tok.nextToken();
            }

            int checkCount = 100;

            if ("?????????".equalsIgnoreCase(type)) {
                bugCheck1(gm, checkCount);
            } else if ("?????????".equalsIgnoreCase(type)) {
                bugCheck2(gm, checkCount);
            } else if ("????????????".equalsIgnoreCase(type)) {
                bugCheck3(gm);
            } else {
                bugCheck1(gm, checkCount);
                bugCheck2(gm, checkCount);
            }

            if (type == null) {
                type = "??????";
            }

            gm.sendPackets(new S_SystemMessage(type + " ?????? ??????"));

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? [?????????,?????????]"));
        }
    }

    private void bugCheck3(L1PcInstance gm) {
        Map<Integer, L1EtcItem> itemList = ItemTable.getInstance().etcItems;

        for (L1EtcItem etcItem : itemList.values()) {
            if (etcItem.getType() == 16) {
                L1TreasureBox box = L1TreasureBox.get(etcItem.getItemId());

                if (box != null) {
                    if (box.getType() == L1TreasureBox.TYPE.SPECIFIC) {
                        L1Item boxItem = ItemTable.getInstance().findItem(box.getBoxId());

                        List<L1TreasureBox.Item> items = box.getItems();

                        gm.sendPackets("?????? : " + boxItem.getName());

                        for (L1TreasureBox.Item o : items) {
                            L1Item realItem = ItemTable.getInstance().findItem(o.getItemId());
                            int crystalCount = ResolventTable.getInstance().getCrystalCount(realItem.getItemId());

                            gm.sendPackets(String.format("??????????????? : %s, ???????????? : %d", realItem.getName(), crystalCount * CodeConfig.RATE_CRISTAL * 5));
                        }

                        gm.sendPackets(" ");
                    }
                }
            }
        }
    }

    private void bugCheck1(L1PcInstance pc, int checkCount) {
        String sql = "SELECT ( B.crystal_count * 0 ) price,A.item_id FROM all_item A, resolvent B WHERE A.item_id = B.item_id";

        List<Map<String, Object>> list = SqlUtils.queryForList(sql);

        int i = 0;

        for (Map<String, Object> o : list) {

            if (i++ % checkCount == 0) {
                double per = (int) (i / (double) list.size() * 100.0);
                pc.sendPackets(String.format("[????????? ?????????] %d ????????? ?????? (%d/%d)", (int) per, i, list.size()));
            }

            String sql2 = "SELECT * FROM SHOP WHERE ITEM_ID=? AND selling_price>0 AND NPC_ID NOT IN (SELECT NPCID FROM NPC_SHOP_INFO)";

            String itemId = String.valueOf(o.get("item_id"));
            int resolvePrice = Integer.parseInt(String.valueOf(o.get("price")));

            List<Map<String, Object>> sellList = SqlUtils.queryForList(sql2, itemId);

            for (Map<String, Object> sellItem : sellList) {
                int sellingPrice = Integer.parseInt(String.valueOf(sellItem.get("selling_price")));

                if (resolvePrice > sellingPrice) {
                    String msg = "??????????????? : ?????????????????? : " + itemId + ",????????? : " + sellingPrice + " , ????????? : " + resolvePrice;

                    pc.sendPackets(new S_SystemMessage(msg));
                }
            }
        }
    }

    private void bugCheck2(L1PcInstance pc, int checkCount) {
        String sql = "SELECT * FROM SHOP WHERE selling_price > 0 AND NPC_ID NOT IN (SELECT NPCID FROM NPC_SHOP_INFO)";

        List<Map<String, Object>> list = SqlUtils.queryForList(sql);

        int i = 0;

        for (Map<String, Object> sellItem : list) {
            if (i++ % checkCount == 0) {
                double per = (int) (i / (double) list.size() * 100.0);
                pc.sendPackets(String.format("[????????? ?????????] %d ????????? ?????? (%d/%d)", (int) per, i, list.size()));
            }

            int itemId = Integer.parseInt(String.valueOf(sellItem.get("item_id")));
            int sellingPrice = Integer.parseInt(String.valueOf(sellItem.get("selling_price")));
            int npcId = Integer.parseInt(String.valueOf(sellItem.get("npc_id")));

            String sql2 = "SELECT * FROM SHOP WHERE purchasing_price > 0 and item_id=? AND NPC_ID NOT IN (SELECT NPCID FROM NPC_SHOP_INFO)";

            List<Map<String, Object>> purchasingShopList = SqlUtils.queryForList(sql2, itemId);

            for (Map<String, Object> purItem : purchasingShopList) {
                int purchasingPrice = Integer.parseInt(String.valueOf(purItem.get("purchasing_price")));
                int purNpcId = Integer.parseInt(String.valueOf(purItem.get("npc_id")));

                if (purchasingPrice > sellingPrice) {
                    pc.sendPackets(new S_SystemMessage("????????? ???????????? : " + purNpcId + " , ???????????? : " + npcId + ", ??????????????? : " + itemId + ",?????? : " + (purchasingPrice - sellingPrice)));
                }
            }
        }
    }

    private void safeMode(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String status = st.nextToken();

            if (status.equalsIgnoreCase("???")) {
                CodeConfig.SAFE_MODE = true;

                String msg = "[" + ServerConfig.SERVER_NAME + "] ??????????????? ?????????????????????.";

                L1World.getInstance().broadcastServerMessage(msg);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, msg));

                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance pc : players) {
                    L1CommonUtils.safeMode(pc, true);
                }
            } else if (status.equalsIgnoreCase("???")) {
                CodeConfig.SAFE_MODE = false;

                String msg = "[" + ServerConfig.SERVER_NAME + "] ??????????????? ?????????????????????.";

                L1World.getInstance().broadcastServerMessage(msg);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, msg));

                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance pc : players) {
                    L1CommonUtils.safeMode(pc, false);
                }
            }

        } catch (Exception eee) {
            gm.sendPackets(new S_SystemMessage(".???????????? [???/???] ?????? ???????????????."));
        }
    }

    private void serverPacketCheck(L1PcInstance gm, String param) {
        new Thread(() -> {
            StringTokenizer st = new StringTokenizer(param);
            int code = Integer.parseInt(st.nextToken());
            int num = 1;

            try {
                num = Integer.parseInt(st.nextToken());
            } catch (Exception ignored) {
            }

            for (int i = 0; i < num; i++) {
                ServerPacket packet = new ServerPacket();
                packet.writeC(code + i);
                gm.sendPackets(packet);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("??????", e);
                }
            }
        }).start();
    }

    private void showHelp(L1PcInstance pc) {
        pc.sendPackets(new S_SystemMessage("\\fY???---------------------------------------------------???"));
        pc.sendPackets(new S_SystemMessage("  GM Commands ????????? List..."));
        pc.sendPackets(new S_SystemMessage("\\fY???---------------------------------------------------???"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV??? ?????? ?????? ?????? ????????? ??????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ?????? ???????????? ??????????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ?????? ?????? ??????????????? ??????????????????"));
        pc.sendPackets(new S_SystemMessage("         \\fV???????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ???????????? ???????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ?????? ?????? ?????? ????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ???????????? ?????? ???????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ???????????? ???????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV????????? ?????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ?????? ?????? ????????? ???????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ?????? ?????? ??????"));
        pc.sendPackets(new S_SystemMessage("         \\fV?????? ????????? ?????? ??????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV????????? ???????????? ???????????? "));
        pc.sendPackets(new S_SystemMessage("         \\fV???????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV???????????? ???????????? ???????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("         \\fV???????????? ???????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ????????????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV???????????? ??????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV??????1 ???????????? ???????????? ???????????? ??????"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV????????????"));
        pc.sendPackets(new S_SystemMessage("\\fY???---------------------------------------------------???"));
        pc.sendPackets(new S_SystemMessage("  ?????? : \\fV?????? ?????? ????????? ??????????????? ??????????????? "));
        pc.sendPackets(new S_SystemMessage("         \\fV????????? ??????????????? ????????? ????????? ???????????????"));
        pc.sendPackets(new S_SystemMessage("         \\fV??????????????? ?????? ???????????? ?????????????????? "));
        pc.sendPackets(new S_SystemMessage("         \\fV???????????? ??????????????? ??????????????? ????????????"));
        pc.sendPackets(new S_SystemMessage("         \\fV????????????"));

        pc.sendPackets(new S_SystemMessage("\\fY???---------------------------------------------------???"));
    }

    private void packet(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int id = Integer.parseInt(st.nextToken(), 10);
            gm.sendPackets(new S_PacketBox(id));
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("[Command] .?????? [id] ??????"));
        }
    }

    private void packet1(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);

            for (int i = 113; i < 200; i++) {
                gm.sendPackets("subCode : " + i);
                System.out.println("subCode : " + i);
                gm.sendPackets(new S_PacketBox(i));
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("[Command] .??????1 [id] ??????"));
        }
    }

    private void packet2(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int type = Integer.parseInt(st.nextToken(), 10);
            int value = Integer.parseInt(st.nextToken(), 10);
            gm.sendPackets(new S_SkillIconGFX(type, value));
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("[Command] .??????2 [id] ??????"));
        }
    }

    private void AllPlayerList(L1PcInstance gm) {
        try {
            int searchCount = 0;

            gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                try {
                    if (pc == null || pc.getClient() == null) {
                        continue;
                    }

                    gm.sendPackets(new S_SystemMessage("\\fU?????? : " + pc.getLevel() + ", ????????? : " + pc.getName() + ", ?????? : " + pc.getAccountName()));
                    searchCount++;
                } catch (Exception ignored) {

                }
            }
            gm.sendPackets(new S_SystemMessage("\\fY" + searchCount + "?????? ????????? ???????????????."));
            gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".?????????"));
        }
    }

    private void inventoryDelete(L1PcInstance pc) {
        try {
            for (L1ItemInstance item : pc.getInventory().getItems()) {
                if (!item.isEquipped()) {
                    pc.getInventory().setEquipped(item, false);
                    pc.getInventory().removeItem(item);
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".????????????"));
        }
    }

    private void stopWar(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();

            WarTimeScheduler.getInstance().stopWar(name);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? [??????????????????]"));
        }
    }/// ??????

    private void clanMark(L1PcInstance pc) {
        try {
            int i = 1;

            if (pc.gmCommandClanMark) {
                i = 3;
                pc.gmCommandClanMark = false;
            } else {
                pc.gmCommandClanMark = true;
            }

            for (L1Clan clan : L1World.getInstance().getAllClans()) {
                if (clan != null && clan.getEmblemId() != 0) {
                    pc.sendPackets(new S_War(i, pc.getClanName(), clan.getClanName()));
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("[Command] .?????????"));
        }
    }

    private void callClan(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String clanname = st.nextToken();
            L1Clan clan = L1World.getInstance().getClan(clanname);
            if (clan != null) {
                for (L1PcInstance player : clan.getOnlineClanMember()) {

                    if (!player.isFishing()) {
                        L1TeleportUtils.teleportToTargetFront(player, pc, 2);
                    }
                }
                pc.sendPackets(new S_SystemMessage("[ " + clanname + " ] ????????? ?????????????????????."));
            } else {
                pc.sendPackets(new S_SystemMessage("[ " + clanname + " ] ????????? ???????????? ????????????."));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".???????????? [????????????] ????????? ??????"));
        }
    }

    private void rate(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            int value = Integer.parseInt(tok.nextToken());

            StringBuilder text2 = new StringBuilder();

            if (type.equalsIgnoreCase("?????????")) {
                CodeConfig.RATE_XP = value;
            } else if (type.equalsIgnoreCase("?????????")) {
                CodeConfig.RATE_DROP_ITEMS = value;
            } else if (type.equalsIgnoreCase("?????????")) {
                CodeConfig.RATE_DROP_ADENA = value;
            } else {
                gm.sendPackets(new S_SystemMessage("[Command] .?????? [?????????, ?????????, ?????????] [???]??????"));
                return;
            }

            text2.append(" = ?????????: ").append(CodeConfig.RATE_XP).append("??? = ?????????: ")
                    .append(CodeConfig.RATE_DROP_ITEMS).append("??? = ?????????: ")
                    .append(CodeConfig.RATE_DROP_ADENA).append("??? =");
            String text = " = ?????????: " + CodeConfig.RATE_XP + "??? = ?????????: " +
                    CodeConfig.RATE_DROP_ITEMS + "??? = ?????????: " +
                    CodeConfig.RATE_DROP_ADENA + "??? =";
            gm.sendPackets(new S_SystemMessage("*?????? ??????*" + text));
            gm.sendPackets(new S_SystemMessage("*?????? ??????*" + text2));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("[Command] .?????? [?????????, ?????????, ?????????] [???]??????"));
        }
    }

    private void HcPacketStop(L1PcInstance pc, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();
            L1PcInstance player = L1World.getInstance().getPlayer(name);

            if (player == null) {
                pc.sendPackets(new S_SystemMessage(name + "?????? ???????????? ???????????? ????????????."));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".???????????? [????????????]"));
        }
    }

    private void allPresent(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int itemid = Integer.parseInt(st.nextToken(), 10);
            int enchant = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            Collection<L1PcInstance> player = L1World.getInstance().getAllPlayers();

            for (L1PcInstance target : player) {
                if (target == null)
                    continue;

                L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                item.setCount(count);
                item.setEnchantLevel(enchant);

                if (target.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                    target.getInventory().storeItem(item);
                }

                target.sendPackets(new S_SkillSound(target.getId(), 1091));//???????????????
                target.sendPackets(new S_SkillSound(target.getId(), 4856));// ????????????
                target.sendPackets(new S_SystemMessage("???????????? ?????????????????? ????????? ???????????????."));
                target.sendPackets(new S_SystemMessage("????????? :  [" + item.getViewName() + "]"));
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".???????????? ?????????ID ???????????? ??????????????? ????????? ?????????."));
        }
    }

    private void userSummon(L1PcInstance pc, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String idString = tok.nextToken();
            String nmString = tok.nextToken();

            L1PcInstance player = L1World.getInstance().getPlayer(user);

            if (player != null) {
                int npcId = Integer.parseInt(idString);
                int npcNm = Integer.parseInt(nmString);

                for (int i = 0; i < npcNm; i++) {
                    L1Npc npc = NpcTable.getInstance().getTemplate(npcId);
                    L1SummonInstance summonInst = new L1SummonInstance(npc, player);
                    summonInst.setPetCost(0);
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".???????????? ???????????? NPCID ?????????"));
        }
    }

    public void levelup3(L1PcInstance gm, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String user = tok.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(user);

            int level = Integer.parseInt(tok.nextToken());
            if (level == target.getLevel()) {
                return;
            }
            if (!IntRange.includes(level, 1, 99)) {
                gm.sendPackets(new S_SystemMessage("1-99??? ???????????? ????????? ?????????"));
                return;
            }

            target.setExp(ExpTable.getInstance().getExpByLevel(level));
            gm.sendPackets(new S_SystemMessage(target.getName() + "?????? ????????? ?????????! .??? [?????????]?????? ????????????"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".?????? [?????????] [??????] ??????"));
        }
    }

    private void setMaxLevel(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int level = Integer.parseInt(st.nextToken());

            CodeConfig.MAX_LEVEL = level;

            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "?????? ????????? " + level + "??? ?????????????????????."));
        } catch (Exception e) {
            gm.sendPackets(".???????????? ??????");
        }
    }

    private void thread(L1PcInstance gm) {
        String msg1 = "Thread Count : " + Thread.activeCount();
        gm.sendPackets(msg1);

        Thread[] th = new Thread[Thread.activeCount()];
        Thread.enumerate(th);

        for (Thread thread : th) {
            logger.info("N : [" + thread.getName() + "], S : " + thread.getState().name());
        }
    }

    private void sound(L1PcInstance pc, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            int sprid = Integer.parseInt(stringtokenizer.nextToken());

            pc.sendPackets(new S_SkillSound(pc.getId(), sprid));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), sprid));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".????????? [??????] ?????? ????????? ?????????."));
        }
    }

    private void effect(L1PcInstance pc, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            int sprid = Integer.parseInt(stringtokenizer.nextToken());

            pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), sprid));
            Broadcaster.broadcastPacket(pc, new S_EffectLocation(pc.getX(), pc.getY(), sprid));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".????????? [??????] ?????? ????????? ?????????."));
        }
    }

    //6675
    //12305
    private void castleWarStart(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();
            int minute = 0;

            try {
                minute = Integer.parseInt(tok.nextToken());
            } catch (Exception ignored) {

            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, minute);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            gm.sendPackets(new S_SystemMessage(".??????????????? " + formatter.format(cal.getTime()) + "??? ?????? ???????????????."));

            CastleTable.getInstance().updateWarTime(name, cal);
            WarTimeScheduler.getInstance().setWarStartTime(name, cal);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? ??????????????????(??????,??????,??????,??????,??????,??????,??????,??????) ???"));
        }
    }

    private void allrecall(L1PcInstance gm) {
        try {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (!pc.isGm()) {
                    recallNow(gm, pc);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? ????????? ??????"));
        }

    }

    private void recallNow(L1PcInstance gm, L1PcInstance target) {
        try {
            if (target.isDead()) {
                gm.sendPackets("????????? ??????????????????");
                return;
            }

            L1TeleportUtils.teleportToTargetFront(target, gm, 1);
            target.sendPackets(new S_SystemMessage("?????? ??????????????? ?????????????????????."));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void returnEXP(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);
            if (target != null) {
                int oldLevel = target.getLevel();
                int needExp = ExpTable.getInstance().getNeedExpNextLevel(oldLevel);
                int exp = 0;
                if (oldLevel >= 11 && oldLevel < 45) {
                    exp = (int) (needExp * 0.1);
                } else if (oldLevel == 45) {
                    exp = (int) (needExp * 0.09);
                } else if (oldLevel == 46) {
                    exp = (int) (needExp * 0.08);
                } else if (oldLevel == 47) {
                    exp = (int) (needExp * 0.07);
                } else if (oldLevel == 48) {
                    exp = (int) (needExp * 0.06);
                } else if (oldLevel >= 49) {
                    exp = (int) (needExp * 0.05);
                }
                target.addExp(+exp);
                target.save();
                target.saveInventory();
            } else {
                gm.sendPackets(new S_SystemMessage("????????? ????????? ???????????? ??????????????? ???????????? ????????????."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".??????????????? [????????????]??? ?????? ????????????."));
        }
    }

    private void memFree(L1PcInstance gm) {
        java.lang.System.gc();

        gm.sendPackets(new S_SystemMessage("gc ????????? ????????? ??????"));

        long long_total = Runtime.getRuntime().totalMemory();
        int int_total = Math.round(long_total / 1000000F);
        long long_free = Runtime.getRuntime().freeMemory();
        int int_free = Math.round(long_free / 1000000F);
        long long_max = Runtime.getRuntime().maxMemory();
        int int_max = Math.round(long_max / 1000000F);

        gm.sendPackets(new S_SystemMessage("????????? ????????? : " + int_total + "MB"));
        gm.sendPackets(new S_SystemMessage("?????? ????????? : " + int_free + "MB"));
        gm.sendPackets(new S_SystemMessage("?????? ???????????? ????????? : " + int_max + "MB"));
    }

    private void toChangePassword(L1PcInstance gm, L1PcInstance pc, String passwd) {
        SqlUtils.query("select account_name from characters where char_name = ?", (rs, i) -> {
            String login = rs.getString(1);

            SqlUtils.update("UPDATE accounts SET password=? WHERE login = ?", passwd, login);
            gm.sendPackets(new S_ChatPacket(pc, "??????????????????: [" + login + "] ??????: [" + passwd + "]", L1Opcodes.S_OPCODE_NORMALCHAT, 2));
            gm.sendPackets(new S_SystemMessage(pc.getName() + "??? ?????? ????????? ??????????????? ?????????????????????."));
            pc.sendPackets(new S_SystemMessage("????????? ?????? ????????? ?????????????????????."));

            return null;
        }, pc.getName());
    }

    private void toChangePassword(L1PcInstance pc, String name, String passwd) {
        SqlUtils.query("select account_name from characters where char_name = ?", (rs, i) -> {
            String login = rs.getString(1);
            SqlUtils.update("UPDATE accounts SET password=? WHERE login =?", passwd, login);
            pc.sendPackets(new S_ChatPacket(pc, "??????????????????: [" + login + "] ??????: [" + passwd + "]", L1Opcodes.S_OPCODE_NORMALCHAT, 2));
            pc.sendPackets(new S_SystemMessage(name + "??? ?????? ????????? ??????????????? ?????????????????????."));
            return null;
        }, name);
    }

    private void changePassword(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String passwd = tok.nextToken();

            if (passwd.length() < 4) {
                gm.sendPackets(new S_SystemMessage("???????????? ????????? ???????????? ?????? ????????????."));
                gm.sendPackets(new S_SystemMessage("?????? 4??? ?????? ????????? ????????????."));
                return;
            }

            if (passwd.length() > 20) {
                gm.sendPackets(new S_SystemMessage("???????????? ????????? ???????????? ?????? ?????????."));
                gm.sendPackets(new S_SystemMessage("?????? 12??? ????????? ????????? ????????????."));
                return;
            }

            if (!StringUtils.isDisitAlpha(passwd)) {
                gm.sendPackets(new S_SystemMessage("????????? ???????????? ?????? ????????? ?????????????????????."));
                return;
            }
            L1PcInstance target = L1World.getInstance().getPlayer(user);

            if (target != null) {
                toChangePassword(gm, target, passwd);
            } else {
                toChangePassword(gm, user, passwd);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? [?????????] [??????]??? ??????????????????."));
        }
    }

    private void searchDatabase(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            int type = Integer.parseInt(tok.nextToken());
            String name = tok.nextToken();

            searchObject(gm, type, "%" + name + "%");
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".?????? [0~5] [name]??? ?????? ????????????."));
            gm.sendPackets(new S_SystemMessage("0=??????, 1=??????, 2=??????, 3=npc, 4=polymorphs, 5=npc(gfxid)"));
        }
    }

    private void standBy(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String status = st.nextToken();
            if (status.equalsIgnoreCase("???")) {
                CodeConfig.STANDBY_SERVER = true;
                L1World.getInstance().broadcastServerMessage(L1Msg.STAND_BY_MSG);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, L1Msg.STAND_BY_MSG));
            } else if (status.equalsIgnoreCase("???")) {
                CodeConfig.STANDBY_SERVER = false;
                L1World.getInstance().broadcastServerMessage(L1Msg.STAND_BY_OFF_MSG);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, L1Msg.STAND_BY_OFF_MSG));
            }

        } catch (Exception eee) {
            gm.sendPackets(new S_SystemMessage(".???????????? [???/???] ?????? ???????????????."));
            gm.sendPackets(new S_SystemMessage("??? - ????????????????????? ?????? | ??? - ??????????????? ????????????"));
        }
    }

    private void givesItem2(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String pcname = st.nextToken();
            L1PcInstance pc = L1World.getInstance().getPlayer(pcname);

            if (pc == null) {
                gm.sendPackets(new S_SystemMessage("?????? ???????????? ?????? ???????????? ???????????? ????????????."));
                return;
            }
            String nameid = "";
            if (st.hasMoreTokens()) {
                nameid = st.nextToken();
            }

            int count = 1;
            if (st.hasMoreTokens()) {
                count = Integer.parseInt(st.nextToken());
            }

            int enchant = 0;

            if (st.hasMoreTokens()) {
                enchant = Integer.parseInt(st.nextToken());
            }

            int bless = 1;

            if (st.hasMoreTokens()) {
                bless = Integer.parseInt(st.nextToken());
            }

            int attr = 0;

            if (st.hasMoreTokens()) {
                attr = Integer.parseInt(st.nextToken());
            }

            int itemid;

            try {
                itemid = Integer.parseInt(nameid);
            } catch (NumberFormatException e) {
                itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);

                if (itemid == 0) {
                    gm.sendPackets(new S_SystemMessage("?????? ???????????? ???????????? ???????????????."));
                    return;
                }
            }

            L1Item temp = ItemTable.getInstance().getTemplate(itemid);

            if (temp != null) {
                if (temp.isStackable()) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                    item.setEnchantLevel(0);
                    item.setCount(count);
                    item.setAttrEnchantLevel(attr);
                    item.setBless(bless);

                    if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                        pc.getInventory().storeItem(item);
                        pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
                        pc.sendPackets(new S_SystemMessage("???????????? ?????????[" + item.getLogName() + "]??? ???????????????."));
                        String msg = item.getViewName2() + "(ID:" + itemid + ")??? " + pc.getName() + "?????? ???????????????.";

                        gm.sendPackets(msg);

                        logger.info(msg);
                    }
                } else {
                    L1ItemInstance item = null;

                    int createCount;

                    for (createCount = 0; createCount < count; createCount++) {
                        item = ItemTable.getInstance().createItem(itemid);
                        item.setEnchantLevel(enchant);
                        item.setAttrEnchantLevel(attr);
                        item.setBless(bless);

                        if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                            pc.getInventory().storeItem(item);
                        } else {
                            break;
                        }
                    }

                    if (createCount > 0) {
                        pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
                        pc.sendPackets(new S_SystemMessage("???????????? ?????????[" + item.getLogName() + "]??? ???????????????."));

                        String msg = item.getViewName2() + "(ID:" + itemid + ")??? " + pc.getName() + "?????? ???????????????.";
                        gm.sendPackets(msg);

                        logger.info(msg);
                    }
                }
            } else {
                gm.sendPackets(new S_SystemMessage("?????? ID??? ???????????? ???????????? ????????????."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".?????? [????????????] [itemid ?????? name] [??????] [????????????] [??????] [??????]??? ?????? ????????????."));
        }
    }

    private void saveServer(L1PcInstance pc) {
        Collection<L1PcInstance> list = L1World.getInstance().getAllPlayers();

        for (L1PcInstance player : list) {
            if (player == null)
                continue;

            try {
                player.save();
                player.saveInventory();
            } catch (Exception e) {
                logger.error(e);
            }
        }

        pc.sendPackets(new S_SystemMessage("??????????????? ?????????????????????."));
    }

    private void chatX(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            if (target != null) {
                target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_CHAT_PROHIBITED);
                target.sendPackets(new S_SkillIconGFX(L1SkillIcon.??????, 0));
                target.sendPackets(new S_ServerMessage(288));
                gm.sendPackets(new S_SystemMessage("??????????????? ????????? ?????? ????????????.."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? ???????????? ????????? ????????? ?????????."));
        }
    }

    private void checkEnchant(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String para1 = stringtokenizer.nextToken();
            int leaf = Integer.parseInt(para1);

            Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

            for (L1PcInstance player : players) {
                List<L1ItemInstance> enchant = player.getInventory().getItems();

                for (L1ItemInstance l1ItemInstance : enchant) {
                    if (l1ItemInstance.getEnchantLevel() >= leaf) {
                        gm.sendPackets(new S_SystemMessage(player.getName() + "?????? " + l1ItemInstance.getLogName() + " ???????????????????????????. "));
                    }
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? ?????? (?????? ????????? ??????????????? ?????? ??????)"));
        }
    }

    private void checkAden(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String para1 = stringtokenizer.nextToken();
            int money = Integer.parseInt(para1);

            List<Map<String, Object>> characterWarehouseAccounts = SqlUtils.queryForList("SELECT account_name,count from character_warehouse where item_Id=40308 and count > ?", money);

            for (Map<String, Object> o : characterWarehouseAccounts) {
                gm.sendPackets(new S_SystemMessage("[??????] ????????? : " + o.get("account_name") + "?????? " + o.get("count") + "??????"));
            }

            List<Map<String, Object>> characterNames = SqlUtils.queryForList("SELECT (select char_name from characters where objid=char_id)charName,count from character_items where item_Id=40308 and count>?", money);
            for (Map<String, Object> o : characterNames) {
                gm.sendPackets(new S_SystemMessage("[??????] ????????? : " + o.get("charName") + "?????? " + o.get("count") + "??????"));
            }

            List<Map<String, Object>> clanNames = SqlUtils.queryForList("SELECT clan_name,count from clan_warehouse where item_Id=40308 and count > ?", money);

            for (Map<String, Object> o : clanNames) {
                gm.sendPackets(new S_SystemMessage("[??????] ?????? : " + o.get("clan_name") + "?????? " + o.get("count") + "??????"));
            }

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".???????????? ?????? (?????? ????????? ???????????? ?????? ??????)"));
        }
    }

    private void searchObject(L1PcInstance gm, int type, String name) {
        try {
            String sql = "";

            switch (type) {
                case 0: // etcitem
                    sql = "select item_id, name from etcitem where name Like '" + name + "'";
                    break;
                case 1: // weapon
                    sql = "select item_id, name from weapon where name Like '" + name + "'";
                    break;
                case 2: // armor
                    sql = "select item_id, name from armor where name Like '" + name + "'";
                    break;
                case 3: // npc
                    sql = "select npcid, name from npc where name Like '" + name + "'";
                    break;
                case 4: // polymorphs
                    sql = "select polyid, name from polymorphs where name Like '" + name + "'";
                    break;
                case 5: // polymorphs
                    sql = "select gfxid, name from npc where name Like '" + name + "'";
                    break; // ??????
                default:
                    break;
            }

            if (org.apache.commons.lang3.StringUtils.isEmpty(sql))
                return;

            SqlUtils.query(sql, (rs, i) -> {
                String str1 = rs.getString(1);
                String str2 = rs.getString(2);

                gm.sendPackets(new S_SystemMessage("id : [" + str1 + "], name : [" + str2 + "]"));
                return null;
            });
        } catch (Exception ignored) {
        }
    }

    private void noCall(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            if (target != null) {
                L1TeleportUtils.teleportToGiran(target);
            } else {
                gm.sendPackets(new S_SystemMessage("??????????????? ?????? ?????? ID ?????????."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".?????? (??????????????????) ?????? ????????? ?????????."));
        }
    }

    private void giveHouse(L1PcInstance pc, String poby) {
        try {
            StringTokenizer st = new StringTokenizer(poby);
            String playerName = st.nextToken();

            int houseId = Integer.parseInt(st.nextToken());

            L1PcInstance target = L1World.getInstance().getPlayer(playerName);

            if (target != null) {
                if (target.getClanId() != 0) {
                    L1Clan targetClan = L1World.getInstance().getClan(target.getClanName());
                    L1House pobyHouse = HouseTable.getInstance().getHouseTable(houseId);
                    targetClan.setHouseId(houseId);

                    L1ClanUtils.updateClan(targetClan);

                    pc.sendPackets(new S_SystemMessage(target.getClanName() + " ???????????? " + pobyHouse.getHouseName() + "?????? ?????????????????????."));
                    for (L1PcInstance tc : targetClan.getOnlineClanMember()) {
                        tc.sendPackets(new S_SystemMessage("???????????????????????? " + pobyHouse.getHouseName() + "?????? ?????? ???????????????."));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage(target.getName() + "?????? ????????? ?????? ?????? ????????????."));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(73, playerName));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".??????????????? <??????????????????> <???????????????>"));
        }
    }

    private void dmgScarecrow(L1PcInstance gm) {
        try {
            if (gm.getScarecrow()) {
                gm.sendPackets(new S_SystemMessage("\\aH : ??????????????? OFF"));
                gm.setScarecrow(false);
            } else {
                gm.sendPackets(new S_SystemMessage("\\aH : ??????????????? ON"));
                gm.setScarecrow(true);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("????????? ????????? ??????."));
        }
    }

    private void sprImageCheck(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int codetest = Integer.parseInt(st.nextToken(), 10);
            int codetest2 = Integer.parseInt(st.nextToken(), 10);

            new Thread(() -> {
                try {
                    for (int i = 0; i < codetest2; i++) {
                        Thread.sleep(500);
                        int num = i + codetest;

                        pc.sendPackets("num : " + num);

                        pc.sendPackets(new S_UseAttackSkill(pc, pc.getId(), num, pc.getX(), pc.getY(), L1ActionCodes.ACTION_Attack, false));
                        Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, pc.getId(), num, pc.getX(), pc.getY(), L1ActionCodes.ACTION_Attack, false));
                    }

                } catch (Exception e) {
                    logger.error("??????", e);
                }
            }).start();
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("[Command] .?????? [??????] ??????"));
        }
    }

    private void clear(L1PcInstance gm) {
        for (L1Object obj : L1World.getInstance().getVisibleObjects(gm, 15)) {
            if (obj instanceof L1MonsterInstance) { // ???????????????
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.getCurrentHp() > 0) {
                    npc.receiveDamage(gm, 50000); // ?????????
                }
            }
        }
    }

    public static class RecallItemSelectedAction extends InventoryInfoMessengerAdapter {
        private final L1PcInstance target;
        private final L1PcInstance caller;

        public RecallItemSelectedAction(L1PcInstance target, L1PcInstance caller) {
            this.target = target;
            this.caller = caller;
        }

        @Override
        public void action(int handleId, int size, C_ShopAndWarehouse packet) {
            step2(size, packet);
        }

        public void step2(int size, C_ShopAndWarehouse packet) {
            for (int i = 0; i < size; i++) {
                int objectId = packet.readD();
                int count = packet.readD();

                L1ItemInstance item = getPc().getInventory().getItem(objectId);

                SelectedItem selectedItem = new SelectedItem();
                selectedItem.setItem(item);
                selectedItem.setCount(count);

                if (item.isEquipped()) {
                    target.getInventory().setEquipped(item, false);
                }

                target.getInventory().tradeItem(item, count, caller.getInventory());
                caller.sendPackets(target.getName() + "??? ????????? : " + item.getName() + "(" + count + ")??? ?????????????????????");
                target.sendPackets("???????????? ????????? ????????? : " + item.getName() + "(" + count + ")??? ?????????????????????");
            }
        }

        @Override
        public String key() {
            return "RecallItemSelectedAction";
        }
    }

    public static class SaveLocationThread implements Runnable {
        public static final int INTERVAL = 1000;
        private final L1PcInstance pc;
        private final int locId;
        private ScheduledFuture<?> watch;
        private int ord = 0;
        private boolean ing;

        public SaveLocationThread(L1PcInstance pc, int locId) {
            this.pc = pc;
            this.locId = locId;
        }

        public int getLocId() {
            return locId;
        }

        public void start() {
            watch = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(this, INTERVAL);
            ing = true;
        }

        public void save() {
            pc.sendPackets("??????????????? : " + pc.getX() + "," + pc.getY() + "," + pc.getMapId());
            L1RobotTable.getInstance().insertHuntWayList(locId, pc.getX(), pc.getY(), pc.getMapId(), ord);
            ord++;
        }

        public void stop() {
            if (watch != null) {
                watch.cancel(true);
                watch = null;
            }

            ing = false;
        }

        @Override
        public void run() {
            save();
        }

        public boolean isIng() {
            return ing;
        }
    }

    public class NextDropItemSelectedAction extends InventoryInfoMessengerAdapter {
        private final L1PcInstance target;
        private final L1PcInstance caller;

        public NextDropItemSelectedAction(L1PcInstance target, L1PcInstance caller) {
            this.target = target;
            this.caller = caller;
        }

        @Override
        public void action(int handleId, int size, C_ShopAndWarehouse packet) {
            if (size > 1) {
                caller.sendPackets("???????????? ????????? ???????????????");
                return;
            }

            for (int i = 0; i < size; i++) {
                int objectId = packet.readD();

                L1ItemInstance item = getPc().getInventory().getItem(objectId);

                nextDropMap.put(target.getName(), item.getId());

                caller.sendPackets(target.getName() + "??? ?????? ?????? ???????????? : " + item.getViewName2() + "??? ?????????");
            }
        }

        @Override
        public String key() {
            return getClass().getName();
        }
    }
}
