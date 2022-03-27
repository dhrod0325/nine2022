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

    public static final int 토너먼트준비 = 0;
    public static final int 토너먼트시작 = 1;
    public static final int 토너먼트종료 = 2;
    public static final int 토너먼트퇴장 = 3;
    public static int 토너먼트상태 = 토너먼트종료;

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
                pc.sendPackets(new S_ServerMessage(74, "[Command] 커멘드 " + name));
                return true;
            }

            Class<?> cls = Class.forName(complementClassName(command.getExecutorClassName()));

            L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);

            exe.execute(pc, name, arg);

            return true;
        } catch (NoSuchElementException e) {
            logger.warn("없는 명령어 실행 - {} {} {}", pc.getName(), name, arg);
        } catch (Exception e) {
            if (pc.isGm()) {
                logger.error("오류", e);
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
        L1PcInstance gm = new CharacterTable().loadCharacter("메티스");
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
            gm.sendPackets(new S_ServerMessage(74, "[Command] 커맨드 " + cmd));
            return;
        }

        // GM에 개방하는 커맨드는 여기에 쓴다
        if (cmd.equalsIgnoreCase("도움말")) {
            showHelp(gm);
        } else if (cmd.equalsIgnoreCase("디버그")) {
            StringTokenizer st = new StringTokenizer(param.toString());

            String state = st.nextToken();

            if ("켬".equalsIgnoreCase(state)) {
                gm.getDataMap().put(L1DataMapKey.GM_DEBUG, "on");
                gm.sendPackets("디버그 모드가 시작 되었습니다");
            } else {
                gm.getDataMap().put(L1DataMapKey.GM_DEBUG, "off");
                gm.sendPackets("디버그 모드가 종료 되었습니다");
            }
        } else if (cmd.equalsIgnoreCase("캐릭로그")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();
                String charName = st.nextToken();

                if (type.equalsIgnoreCase("추가")) {
                    pcLogMap.put(charName, LogManager.getLogger("LOG [" + charName + "]"));
                    gm.sendPackets("캐릭로그 " + charName + " 추가 완료");
                } else if (type.equalsIgnoreCase("삭제")) {
                    pcLogMap.remove(charName);
                    gm.sendPackets("캐릭로그 " + charName + " 삭제 완료");
                }
            } catch (Exception e) {
                gm.sendPackets("캐릭로그 추가/삭제 캐릭명");
            }
        } else if (cmd.equalsIgnoreCase("수배삭제")) {
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
                gm.sendPackets("수배삭제 캐릭명");
            }
        } else if (cmd.equalsIgnoreCase("로그")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();
                String o = st.nextToken();

                switch (type) {
                    case "대미지": {
                        L1LogUtils.LOG_DAMAGE = o.equalsIgnoreCase("켬");
                        break;
                    }
                    case "스킬": {
                        L1LogUtils.LOG_SKILL = o.equalsIgnoreCase("켬");
                        break;
                    }
                }

            } catch (Exception e) {
                gm.sendPackets("대미지 로그네임 켬/끔");
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
        } else if (cmd.equalsIgnoreCase("설정")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();

                if (type.equalsIgnoreCase("확인")) {
                    showSettingInfo(gm);
                } else {
                    String value = st.nextToken();

                    if (type.equalsIgnoreCase("만렙")) {
                        int v = Integer.parseInt(value);

                        gm.sendPackets(String.format("만렙설정 변경 : %d -> %d", CodeConfig.MAX_LEVEL, v));

                        CodeConfig.MAX_LEVEL = v;
                        CodeConfig.store("MAX_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("지급단")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("지급단 렙설정 변경 : %d -> %d", CodeConfig.EXP_GIVE_MAX_LEVEL, v));
                        CodeConfig.EXP_GIVE_MAX_LEVEL = v;
                        CodeConfig.store("EXP_GIVE_MAX_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("아덴렙")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("아덴판매렙제 변경 : %d -> %d", CodeConfig.ADEN_SELL_MIN_LEVEL, v));
                        CodeConfig.ADEN_SELL_MIN_LEVEL = v;
                        CodeConfig.store("ADEN_SELL_MIN_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("기던")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("기던 층수 변경 : %d -> %d", CodeConfig.GIRAN_PORTAL_NUMBER, v));
                        CodeConfig.GIRAN_PORTAL_NUMBER = v;
                        CodeConfig.store("GIRAN_PORTAL_NUMBER", v + "");
                    } else if (type.equalsIgnoreCase("공성선포렙")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("공성선포렙 변경 : %s -> %s", CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL, value));
                        CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL = v;
                        CodeConfig.store("CASTLE_WAR_MIN_CROWN_LEVEL", v + "");
                    } else if (type.equalsIgnoreCase("공성보상")) {
                        int v = Integer.parseInt(value);
                        gm.sendPackets(String.format("공성보상 아덴 변경 : %s -> %s", CodeConfig.CASTLE_WAR_WINNER_ADENA, value));
                        CodeConfig.CASTLE_WAR_WINNER_ADENA = v;
                        CodeConfig.store("CASTLE_WAR_WINNER_ADENA", v + "");
                    } else if (type.equalsIgnoreCase("보스피1")) {
                        double v = Double.parseDouble(value);
                        gm.sendPackets(String.format("1등급 보스 HP 비율  변경 : %s -> %s", CodeConfig.BOSS_HP_VALANCE1, value));
                        CodeConfig.BOSS_HP_VALANCE1 = v;
                        CodeConfig.store("BOSS_HP_VALANCE1", v + "");
                    } else if (type.equalsIgnoreCase("보스피2")) {
                        double v = Double.parseDouble(value);
                        gm.sendPackets(String.format("2등급 보스 HP 비율  변경 : %s -> %s", CodeConfig.BOSS_HP_VALANCE2, value));
                        CodeConfig.BOSS_HP_VALANCE2 = v;
                        CodeConfig.store("BOSS_HP_VALANCE2", v + "");
                    } else if (type.equalsIgnoreCase("보스피3")) {
                        double v = Double.parseDouble(value);
                        gm.sendPackets(String.format("3등급 보스 HP 비율  변경 : %s -> %s", CodeConfig.BOSS_HP_VALANCE3, value));
                        CodeConfig.BOSS_HP_VALANCE3 = v;
                        CodeConfig.store("BOSS_HP_VALANCE3", v + "");
                    }

                }
            } catch (Exception e) {
                gm.sendPackets(".설정 확인/만렙/지급단/아덴렙");
                gm.sendPackets(".설정 기던/보주1/보주2/보주3");
                gm.sendPackets(".설정 보스피1/보스피2/보스피3");
                gm.sendPackets(".설정 공성선포렙/공성보상");
                gm.sendPackets(".설정 후원추가 용갑옷/무기변경/원소");
            }
        } else if (cmd.equalsIgnoreCase("맵몬스터")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                int mapId = Integer.parseInt(st.nextToken());
                int count = Integer.parseInt(st.nextToken());

                List<L1Spawn> spawnList = SpawnTable.getInstance().getSpawnListByMapId(mapId);

                if (spawnList.isEmpty()) {
                    gm.sendPackets("해당맵에 스폰 몬스터가 없습니다");
                    return;
                }

                for (int i = 0; i < count; i++) {
                    try {
                        L1Spawn spawn = spawnList.get(RandomUtils.nextInt(spawnList.size()));

                        MapsTable.MapData m = MapsTable.getInstance().getMaps().get(mapId);
                        L1NpcInstance npc = L1SpawnUtils.randomSpawn(spawn.getTemplate().getNpcId(), m.startX, m.endX, m.startY, m.endY, (short) mapId, 0, null);

                        if (npc != null) {
                            String msg = String.format("%s(%d) (%d) 를 소환했습니다. (맵:%d)", npc.getName(), npc.getId(), 1, mapId);
                            gm.sendPackets(new S_SystemMessage(msg));
                        }
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }

                }

            } catch (Exception e) {
                gm.sendPackets(".맵몬스터 mapId count");
            }

        } else if (cmd.equalsIgnoreCase("공지사항")) {
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
                gm.sendPackets(new S_SystemMessage(".공지사항 문구"));
            }
        } else if (cmd.equalsIgnoreCase("계정추방")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String accountName = st.nextToken();

                L1Client acc = AuthorizationUtils.getInstance().getAccountsMap().get(accountName);

                if (acc != null) {
                    if (acc.getActiveChar() != null) {
                        gm.sendPackets("캐릭명 : " + acc.getActiveChar().getName());
                    }

                    acc.disconnect();
                } else {
                    AuthorizationUtils.getInstance().getAccountsMap().remove(accountName);
                }

                gm.sendPackets("계정명 " + accountName + "의 접속이 종료되었습니다");
            } catch (Exception e) {
                gm.sendPackets("계정추방 계정명");
            }
        } else if (cmd.equalsIgnoreCase("캐릭인챈")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String state = st.nextToken();
                String name = null;

                try {
                    name = st.nextToken();
                } catch (Exception ignored) {
                }

                if (state.equalsIgnoreCase("확인")) {
                    for (String key : nextEnchantMap.keySet()) {
                        Boolean value = nextEnchantMap.get(key);
                        gm.sendPackets(key + "의 다음 인챈트 : " + (value ? "성공" : "실패"));
                    }
                } else if (state.equalsIgnoreCase("성공") || state.equalsIgnoreCase("실패")) {
                    if (name != null) {
                        boolean success = state.equalsIgnoreCase("성공");
                        nextEnchantMap.put(name, success);
                        gm.sendPackets("캐릭터명 : [" + name + "] 다음번 인챈트가 무조건 " + state + "하도록 설정되었습니다");
                    } else {
                        gm.sendPackets("캐릭터명을 올바르게 입력하세요");
                    }
                } else {
                    gm.sendPackets(".캐릭인챈 성공/실패/확인 캐릭명 ");
                }

            } catch (Exception e) {
                gm.sendPackets(".캐릭인챈 성공/실패/확인 캐릭명 ");
            }
        } else if (cmd.equalsIgnoreCase("확정드랍")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());

                gm.sendPackets(".확정드랍 캐릭명");

                String targetName = st.nextToken();

                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                S_InventoryInfo packet = new S_InventoryInfo(target);
                packet.setMessenger(new NextDropItemSelectedAction(target, gm));
                packet.build();

                gm.sendPackets("아이템을 선택하세요");
                gm.sendPackets(packet);
            } catch (Exception ignored) {
            }
        } else if (cmd.equalsIgnoreCase("확정드랍스킬")) {
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
                        gm.sendPackets(new S_SystemMessage("해당 스킬이 발견되지 않습니다. "));
                        return;
                    }
                }

                L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

                if (skill == null) {
                    gm.sendPackets(new S_SystemMessage("해당 스킬이 발견되지 않습니다. "));
                    return;
                }

                nextDropSkillMap.put(name, skillId);

                gm.sendPackets(name + "의 다음 드랍스킬이 " + skill.getName() + "로 설정되었습니다");
            } catch (Exception e) {
                gm.sendPackets(".확정드랍스킬 캐릭명 스킬명/스킬ID");

                for (String key : nextDropSkillMap.keySet()) {
                    Integer value = nextDropSkillMap.get(key);
                    gm.sendPackets(key + "의 다음 드랍스킬 : " + SkillsTable.getInstance().getTemplate(value).getName());
                }
            }
        } else if (cmd.equalsIgnoreCase("토너먼트")) {
            tournament(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("대미지")) {
            dmgScarecrow(gm);
        } else if (cmd.equalsIgnoreCase("html")) {
            StringTokenizer st = new StringTokenizer(param.toString());
            String html = st.nextToken();
            gm.sendPackets(new S_ShowCCHtml(gm.getId(), html, "5900", "5901", "5902"));
        } else if (cmd.equalsIgnoreCase("패킷정지")) {
            HcPacketStop(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("전체선물")) {
            allPresent(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("쓰레드")) {
            thread(gm);
        } else if (cmd.equalsIgnoreCase("이벤트")) {
            event(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("안전모드")) {
            safeMode(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("색상")) {
            gm.sendPackets("\\f1f1\\f2f2\\f3f3\\f4f4\\f5f5\\f6f6\\f7f7");
            gm.sendPackets("\\fRfR\\fSfS\\fTfT\\fUfU\\fVfV\\fWfW\\fXfX\\fYfY");
        } else if (cmd.equalsIgnoreCase("보스")) {
            try {
                gm.sendPackets(new S_SystemMessage("---------------스폰된 보스-------------\n"));

                for (L1MonsterInstance bs : L1BossSpawnManager.getInstance().getBossList()) {
                    gm.sendPackets(new S_SystemMessage(bs.getName() + " x : " + bs.getX() + " Y : " + bs.getY() + " MAP : " + bs.getMapId()));
                }

                gm.sendPackets(new S_SystemMessage("---------------------------------------\n"));
            } catch (Exception e) {
                gm.sendPackets(".보스 ");
            }

        } else if (cmd.equalsIgnoreCase("보스스폰")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                int npcId = Integer.parseInt(st.nextToken());
                int deleteTime = Integer.parseInt(st.nextToken());

                L1Boss bs = L1BossSpawnListHotTable.getInstance().findByNpcId(npcId);
                L1BossSpawnManager.getInstance().addBoss(bs, 1000L * 60 * deleteTime);

                gm.sendPackets(bs.getMonName() + "이 스폰되었습니다");
            } catch (Exception e) {
                gm.sendPackets(".보스스폰 NPCID 삭제시간(분)");
            }
        } else if (cmd.equalsIgnoreCase("코드")) {
            sprImageCheck(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("스킬아이콘")) {
            skillIconCheck(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("서버패킷")) {
            serverPacketCheck(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("가라")) {
            noCall(gm, param.toString());
        } else if (cmd.startsWith("공성종료")) {
            stopWar(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("유저서먼")) {
            userSummon(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("정리")) {
            clear(gm);
        } else if (cmd.equalsIgnoreCase("사운드")) {
            sound(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("이팩트")) {
            effect(gm, param.toString());
        } else if (cmd.startsWith("서버저장")) {
            saveServer(gm);
        } else if (cmd.equalsIgnoreCase("전체소환")) {
            allrecall(gm);
        } else if (cmd.equalsIgnoreCase("선물")) {
            givesItem2(gm, param.toString());
        } else if (cmd.equals("공성시작")) {
            castleWarStart(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("채금풀기")) {
            chatX(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("인첸검사")) {
            checkEnchant(gm, param.toString());
        } else if (cmd.equals("아지트지급")) {
            giveHouse(gm, param.toString());
        } else if (cmd.equals("채금2")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String name = st.nextToken();
                int time = Integer.parseInt(st.nextToken());

                L1PcInstance tg = L1World.getInstance().getPlayer(name);

                if (tg != null) {
                    tg.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, time * 1000);
                    tg.sendPackets(new S_SkillIconGFX(L1SkillIcon.채금, time / 60));
                    tg.sendPackets("채팅 금지 : 채팅을 할 수 없음 (" + time + "초)");
                    L1World.getInstance().broadcastServerMessage("게임에 적합하지 않는 행동이기 때문에 " + name + "의 채팅을 " + time + "초간 금지합니다.");
                    L1World.getInstance().broadcastServerMessage("받는 대미지 400% 효과가 적용됩니다.");
                }
            } catch (Exception e) {
                gm.sendPackets(new S_SystemMessage("채금2 [캐릭터명] [시간(초)] 이라고 입력해 주세요. "));
            }
        } else if (cmd.equals("아지트확인")) {
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

            gm.sendPackets("# 지급가능한 아지트 리스트 #");

            for (L1House house : remaningAgits) {
                gm.sendPackets("houseId : " + house.getHouseId() + "," + house.getHouseArea() + "평, 아지트명 : " + house.getHouseName());
            }

        } else if (cmd.equalsIgnoreCase("렙작")) {
            levelup3(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("아덴검사")) {
            checkAden(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("오픈대기")) {
            standBy(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("레벨제한")) { // 레벨제한
            setMaxLevel(gm, param.toString());
        } else if (cmd.startsWith("경험치복구")) {
            returnEXP(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("감옥")) {
            L1Teleport.teleport(gm, 32736, 32799, (short) 34, 5, true);
        } else if (cmd.equalsIgnoreCase("암호변경")) {
            changePassword(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("메모리")) {
            memFree(gm);
        } else if (cmd.equalsIgnoreCase("배율")) {
            rate(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("영자채팅")) { // by영자채팅
            if (CodeConfig.IS_GM_CHAT) {
                CodeConfig.IS_GM_CHAT = false;
                gm.sendPackets(new S_SystemMessage("영자채팅 OFF"));
            } else {
                CodeConfig.IS_GM_CHAT = true;
                gm.sendPackets(new S_SystemMessage("영자채팅 ON"));
            }
        } else if (cmd.equalsIgnoreCase("혈마크")) {
            clanMark(gm);
        } else if (cmd.equalsIgnoreCase("패킷")) {
            packet(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("패킷1")) {
            packet1(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("패킷2")) {
            packet2(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("인벤삭제")) {
            inventoryDelete(gm);
        } else if (cmd.equalsIgnoreCase("온라인")) {
            AllPlayerList(gm);
        } else if (cmd.equalsIgnoreCase("혈맹소환")) {
            callClan(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("전체멘트")) {
            String msg = "[" + ServerConfig.SERVER_NAME + "] " + param;

            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, msg));
            L1World.getInstance().broadcastServerMessage("****** : " + param);

            L1WebApiUtils.chatLog("100", gm.getName(), "[전체]", param.toString());
        } else if (cmd.equalsIgnoreCase("펫렙")) {
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

                    if (gap != 0) { // 레벨업하면(자) DB에 기입한다
                        L1Pet petTemplate = PetTable.getInstance().getTemplate(pet.getItemObjId());

                        if (petTemplate == null) { // PetTable에 없다
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
                gm.sendPackets("펩렙 오브젝트아이디 레벨");
                logger.error("오류", e);
            }
        } else if (cmd.equalsIgnoreCase("화면")) {
            StringTokenizer st = new StringTokenizer(param.toString());
            String pcName = st.nextToken();

            L1PcInstance pc = L1World.getInstance().getPlayer(pcName);

            if (pc != null) {
                int dis = gm.getLocation().getTileLineDistance(pc.getLocation());
                System.out.println(dis);
            }

        } else if (cmd.equalsIgnoreCase("버프시간")) {
            Map<Integer, L1SkillTimer> e = gm.getSkillEffectTimerSet().getSkillEffect();

            for (Integer skillId : e.keySet()) {
                L1SkillTimer k = e.get(skillId);

                if (k != null) {
                    String msg = "skillId : " + skillId + ", time : " + k.getRemainingTime();
                    gm.sendPackets(msg);
                }
            }
        } else if (cmd.equalsIgnoreCase("테스트2")) {
            StringTokenizer st = new StringTokenizer(param.toString());

            int t1 = Integer.parseInt(st.nextToken());

            gm.sendPackets(new S_PacketBox(L1PacketBoxType.EFFECT_ICON, t1, 1800 * 1000));
        } else if (cmd.equalsIgnoreCase("테스트")) {
            StringTokenizer st = new StringTokenizer(param.toString());

            int type = Integer.parseInt(st.nextToken());

            gm.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, type, true));

        } else if (cmd.equalsIgnoreCase("옵")) {
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

            gm.sendPackets(serverPacket); // 별4
        } else if (cmd.equalsIgnoreCase("내꺼")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                int time = Integer.parseInt(st.nextToken());

                gm.sendPackets(new S_SkillIconWindShackle(gm.getId(), time));
            } catch (Exception e) {
                logger.error(e);
            }
        } else if (cmd.equalsIgnoreCase("용해제동기화")) {
            resolventSell(gm);
        } else if (cmd.equalsIgnoreCase("버그검사")) {
            String finalParam = param.toString();
            LineageAppContext.commonTaskScheduler().execute(() -> bugCheck(gm, finalParam));
        } else if ("서버종료".equalsIgnoreCase(cmd)) {
            GameServer.getInstance().shutdownWithCountdown(20);
        } else if (cmd.equalsIgnoreCase("검색")) {
            searchDatabase(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("회수")) {
            recallItem(gm, param.toString());
        } else if (cmd.equalsIgnoreCase("서버오픈")) {
            try {
                StringTokenizer tok = new StringTokenizer(param.toString());
                String cmdType = tok.nextToken();

                if ("켬".equalsIgnoreCase(cmdType)) {
                    CodeConfig.SERVER_STATUS = 0;
                    gm.sendPackets("누구나 서버에 접근할 수 있는 상태로 변경되었습니다");
                } else {
                    CodeConfig.SERVER_STATUS = 1;
                    gm.sendPackets("GM 계정만 서버에 접근할 수 있는 상태로 변경되었습니다");
                }
            } catch (Exception e) {
                gm.sendPackets(".서버오픈 켬/끔");
            }

        } else if (cmd.equalsIgnoreCase("감시자리퍼")) {
            List<L1MonsterInstance> mis = new ArrayList<>();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                if (mi.getNpcId() == 45590 || mi.getTemplate().getTransformId() == 45590) {
                    mis.add(mi);
                }
            }

            gm.sendPackets("---감시자---");

            for (L1MonsterInstance mi : mis) {
                String mapName = MapsTable.getInstance().getMapName(mi.getMapId());
                String msg = mapName + " : " + mi.getName() + " x : " + mi.getX() + " Y : " + mi.getY() + " MAP : " + mi.getMapId();

                String type = null;

                if (mi.getNpcId() == 45590) {
                    type = "감시자리퍼";
                } else if (mi.getTemplate().getTransformId() == 45590) {
                    type = "감시자몹";
                }

                gm.sendPackets(new S_SystemMessage(type + ":" + msg));
            }

            if (mis.isEmpty()) {
                gm.sendPackets("스폰된 감시자가 없습니다");
            }
        } else if (cmd.equalsIgnoreCase("감시자보스")) {
            List<L1MonsterInstance> mis = new ArrayList<>();

            for (L1MonsterInstance mi : L1World.getInstance().getAllMonsters()) {
                if (mi.isRiper()) {
                    mis.add(mi);
                }
            }

            gm.sendPackets("--- 감시자 보스 ---");

            for (L1MonsterInstance mi : mis) {
                gm.sendPackets(new S_SystemMessage(mi.getName() + " x : " + mi.getX() + " Y : " + mi.getY() + " MAP : " + mi.getMapId()));
            }

            if (mis.isEmpty()) {
                gm.sendPackets("스폰된 감시자보스가 없습니다");
            }
        } else if (cmd.equalsIgnoreCase("아이디변경")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String targetName = st.nextToken();
                String changeName = st.nextToken();

                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                if (target == null) {
                    gm.sendPackets(targetName + "님은 게임중이 아닙니다");
                    return;
                }

                boolean exists = CharacterTable.getInstance().doesCharNameExist(changeName);

                if (exists) {
                    gm.sendPackets("이미 존재하는 아이디입니다");
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

                L1World.getInstance().broadcastServerMessage(String.format("[아이디변경] : %s -> %s", targetName, changeName));
                CharacterTable.getInstance().updateCharName(changeName, targetName);
                target.getClient().disconnectNow();
            } catch (Exception e) {
                gm.sendPackets(".아이디변경 캐릭명");
            }
        } else if (cmd.equalsIgnoreCase("불량아이디")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String targetName = st.nextToken();

                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                if (target == null) {
                    gm.sendPackets(targetName + "님은 게임중이 아닙니다");
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
                String changeName = "불량아이디" + appendId;

                L1World.getInstance().broadcastServerMessage(String.format("[불량아이디변경] : %s -> %s", targetName, changeName));
                BadNameTable.getInstance().insert(targetName, target.getId());
                CharacterTable.getInstance().updateCharName(changeName, targetName);
                target.getClient().disconnectNow();
            } catch (Exception e) {
                gm.sendPackets(".불량아이디 아이디");
            }
        } else if ("오토루팅".equalsIgnoreCase(cmd)) {
            try {
                StringTokenizer tok = new StringTokenizer(param.toString());
                String cmdType = tok.nextToken();

                if ("켬".equalsIgnoreCase(cmdType)) {
                    gm.getDataMap().put(L1DataMapKey.AUTO_LOOT, "true");
                    gm.sendPackets("운영자만 오투루팅이 작동되었습니다");
                } else if ("끔".equalsIgnoreCase(cmdType)) {
                    gm.getDataMap().put(L1DataMapKey.AUTO_LOOT, "false");
                    gm.sendPackets("운영자만 오투루팅이 종료되었습니다");
                }
            } catch (Exception e) {
                gm.sendPackets(".단풍나무 켬/끔/드랍추가/청소");

                logger.error("오류", e);
            }
        } else if (cmd.equalsIgnoreCase("단풍나무")) {
            try {
                StringTokenizer tok = new StringTokenizer(param.toString());
                String cmdType = tok.nextToken();

                if ("켬".equalsIgnoreCase(cmdType)) {
                    TimePickupEventManager.getInstance().start();
                } else if ("끔".equalsIgnoreCase(cmdType)) {
                    TimePickupEventManager.getInstance().stop();
                } else if ("드랍추가".equals(cmdType)) {
                    TimePickupEventManager.getInstance().expendDrop();
                } else if ("드랍청소".equals(cmdType)) {
                    TimePickupEventManager.getInstance().clearDrop();
                }
            } catch (Exception e) {
                gm.sendPackets(".단풍나무 켬/끔/드랍추가/청소");

                logger.error("오류", e);
            }
        } else if (cmd.equalsIgnoreCase("로봇1")) {
            StringTokenizer tok = new StringTokenizer(param.toString());
            String cmdType = tok.nextToken();

            if ("사냥시작".equalsIgnoreCase(cmdType)) {
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

                            gm.sendPackets("[로봇] " + robot.getName() + " - 사냥 시작");

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                logger.error("오류", e);
                            }
                        }
                    }

                    L1RobotHuntData huntData = new L1RobotHuntData();
                    huntData.setEndCheck(0);
                    huntData.setStartTime(new Date());

                    L1RobotTable.getInstance().insertHuntData(huntData);

                    gm.sendPackets("로봇 사냥이 시작되었습니다.");
                });

                LineageAppContext.commonTaskScheduler().execute(t);
            } else if ("사냥종료".equalsIgnoreCase(cmdType)) {
                for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
                    if (robot.getRobotType() == L1RobotType.HUNT) {
                        gm.sendPackets(robot.getName() + " 사냥 종료");
                        robot.logout();
                    }
                }

                L1RobotTable.getInstance().endHuntTime();

                gm.sendPackets("로봇사냥이 모두 종료되었습니다");
            } else if ("위치기록".equalsIgnoreCase(cmdType)) {
                int locId = 0;

                try {
                    locId = Integer.parseInt(tok.nextToken());
                } catch (Exception ignored) {
                }

                if (saveLocation != null) {
                    if (saveLocation.isIng()) {
                        gm.sendPackets("이미 기록중입니다");
                        return;
                    }
                }

                if (locId == 0) {
                    gm.sendPackets("잘못된 locId 입니다");
                    return;
                }

                saveLocation = new SaveLocationThread(gm, locId);
                saveLocation.start();

                gm.sendPackets("위치기록이 시작되었습니다 : " + saveLocation.getLocId());
            } else if ("위치기록종료".equalsIgnoreCase(cmdType)) {
                if (saveLocation != null) {
                    gm.sendPackets("위치기록 종료 locId : " + saveLocation.getLocId());

                    saveLocation.stop();
                    saveLocation = null;
                }
            } else if ("리로드".equalsIgnoreCase(cmdType)) {
                L1RobotTable.getInstance().load();
                gm.sendPackets("로봇 리로드 완료");
            } else if ("오픈대기".equalsIgnoreCase(cmdType)) {
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

                        gm.sendPackets("[로봇] " + robot.getName() + " - 오픈대기 배치");
                    }
                }
            } else if ("좀비".equalsIgnoreCase(cmdType)) {
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

                        gm.sendPackets("[로봇] " + robot.getName() + " - 좀비 배치");
                    }
                }
            } else {
                L1RobotInstance robot = L1RobotTable.getInstance().createRobot(cmdType);

                if (robot != null) {
                    L1World.getInstance().storeObject(robot);
                    L1World.getInstance().addVisibleObject(robot);

                    gm.sendPackets(robot.getName() + " 로봇 소환 완료");
                }
            }
        } else if (cmd.equalsIgnoreCase("후원추가")) {
            try {
                StringTokenizer st = new StringTokenizer(param.toString());
                String type = st.nextToken();
                String type2 = st.nextToken();

                int price = -1;

                String msg;

                if (type2.equalsIgnoreCase("켬")) {
                    msg = "추가";
                    price = 1;
                } else {
                    msg = "삭제";
                }

                int itemId = 0;

                String sql = "UPDATE SHOP SET SELLING_PRICE = ? WHERE npc_id = 460000117 AND item_id=?";

                if ("원소".equalsIgnoreCase(type)) {
                    itemId = 40921;
                } else if ("용갑변경".equalsIgnoreCase(type)) {
                    itemId = 60001212;
                } else if ("무기변경".equalsIgnoreCase(type)) {
                    itemId = 6000112;

                    if (type2.equalsIgnoreCase("켬")) {
                        price = 5;
                    } else {
                        price = -1;
                    }
                }

                SqlUtils.update(sql, price, itemId);

                handleCommands("리로드 상점");

                L1Item item = ItemTable.getInstance().getTemplate(itemId);

                gm.sendPackets("후원상점에 " + item.getName() + " " + msg + " 완료");
            } catch (Exception e) {
                gm.sendPackets(".후원추가 원소/용갑변경/무기변경 켬/끔");
            }
        } else {
            gm.sendPackets(new S_SystemMessage("커멘드 " + cmd + "는 존재하지 않습니다. "));
        }
    }

    private void showSettingInfo(L1PcInstance gm) {
        gm.sendPackets("만렙 : " + CodeConfig.MAX_LEVEL);
        gm.sendPackets("지급단 : " + CodeConfig.EXP_GIVE_MAX_LEVEL);
        gm.sendPackets("기던 : " + CodeConfig.GIRAN_PORTAL_NUMBER);
        gm.sendPackets("보스피1 : " + CodeConfig.BOSS_HP_VALANCE1);
        gm.sendPackets("보스피2 : " + CodeConfig.BOSS_HP_VALANCE2);
        gm.sendPackets("보스피3 : " + CodeConfig.BOSS_HP_VALANCE3);
        gm.sendPackets("아덴판매렙제 : " + CodeConfig.ADEN_SELL_MIN_LEVEL);
        gm.sendPackets("공성선포렙 : " + CodeConfig.CASTLE_WAR_MIN_CROWN_LEVEL);
        gm.sendPackets("공성보상 : " + NumberFormat.getInstance().format(CodeConfig.CASTLE_WAR_WINNER_ADENA));
    }

    private void tournament(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String type = st.nextToken();

            if ("캔슬".equalsIgnoreCase(type)) {
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
            } else if ("준비".equalsIgnoreCase(type)) {
                if (토너먼트상태 == 토너먼트준비) {
                    gm.sendPackets("이미 토너먼트가 준비중입니다");
                    return;
                }

                토너먼트상태 = 토너먼트준비;

                L1World.getInstance().broadcastPacketGreenMessage("토너먼트가 준비되었습니다");

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
            } else if ("시작".equalsIgnoreCase(type)) {
                if (토너먼트상태 != 토너먼트준비) {
                    gm.sendPackets("토너먼트가 준비중이 아닙니다");
                    return;
                }

                토너먼트상태 = 토너먼트시작;

                new Thread(() -> {
                    L1World.getInstance().broadcastPacketGreenMessage("토너먼트가 잠시후 시작됩니다");

                    L1NpcInstance o2 = L1SpawnUtils.spawn(32710, 32895, (short) 90, 0, 80125, 0, 0, null);
                    L1NpcInstance o3 = L1SpawnUtils.spawn(32689, 32895, (short) 90, 0, 80125, 0, 0, null);

                    doorList.add(o2);
                    doorList.add(o3);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        logger.error("오류", e);
                    }

                    for (int i = 15; i > 0; i--) {
                        try {
                            L1World.getInstance().broadcastPacketGreenMessage(i + "초후 토너먼트가 시작됩니다");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            logger.error("오류", e);
                        }
                    }

                    L1World.getInstance().broadcastPacketGreenMessage("토너먼트가 시작되었습니다");

                    for (L1NpcInstance o : lineList) {
                        o.getMap().setAttackAble(o.getX(), o.getY(), true);
                        o.deleteMe();
                        lineList.remove(o);
                    }
                }).start();
            } else if ("종료".equalsIgnoreCase(type)) {
                L1World.getInstance().broadcastPacketGreenMessage("토너먼트가 종료되었습니다");

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

                토너먼트상태 = 토너먼트종료;
            } else if ("퇴장".equalsIgnoreCase(type)) {
                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance player : players) {
                    if (player.getMapId() == 90 && !player.isGm()) {
                        L1TeleportUtils.teleportToGiran(player);
                    }
                }

                토너먼트상태 = 토너먼트퇴장;
            }
        } catch (Exception e) {
            gm.sendPackets(".토너먼트 준비/시작/종료/퇴장/캔슬");
        }
    }

    private void event(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String type = st.nextToken();
            String option = st.nextToken();

            String msg = "";

            if ("용갑".equalsIgnoreCase(type)) {
                if ("켬".equalsIgnoreCase(option)) {
                    msg = "용갑옷 이밴트가 시작되었습니다. 주차별이벤트상인에서 구매하실 수 있습니다";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=20000000*pack_count WHERE NOTE='용갑' and npc_id=460000090";
                    SqlUtils.update(sql);
                } else if ("끔".equalsIgnoreCase(option)) {
                    msg = "용갑옷 이밴트가 종료되었습니다.";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=-1 WHERE NOTE='용갑' and npc_id=460000090";
                    SqlUtils.update(sql);
                }

                L1World.getInstance().broadcastPacketGreenMessage(msg);
                L1World.getInstance().broadcastServerMessage(msg);

                ShopTable.getInstance().load();
            } else if ("반지".equalsIgnoreCase(type)) {
                if ("켬".equalsIgnoreCase(option)) {
                    msg = "스냅퍼 반지 이밴트가 시작되었습니다. 주차별이벤트상인에서 구매하실 수 있습니다";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=200000*pack_count WHERE NOTE='반지' and npc_id=460000090";
                    SqlUtils.update(sql);
                } else if ("끔".equalsIgnoreCase(option)) {
                    msg = "스냅퍼 반지 이밴트가 종료되었습니다.";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=-1 WHERE NOTE='반지' and npc_id=460000090";
                    SqlUtils.update(sql);
                }

                L1World.getInstance().broadcastPacketGreenMessage(msg);
                L1World.getInstance().broadcastServerMessage(msg);

                ShopTable.getInstance().load();
            } else if ("귀걸이".equalsIgnoreCase(type)) {
                if ("켬".equalsIgnoreCase(option)) {
                    msg = "룸티스 귀걸이 이밴트가 시작되었습니다. 주차별이벤트상인에서 구매하실 수 있습니다";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=200000*pack_count WHERE NOTE='귀걸이' and npc_id=460000090";
                    SqlUtils.update(sql);
                } else if ("끔".equalsIgnoreCase(option)) {
                    msg = "룸티스 귀걸이 이밴트가 종료되었습니다.";
                    String sql = "UPDATE SHOP SET SELLING_PRICE=-1 WHERE NOTE='귀걸이' and npc_id=460000090";
                    SqlUtils.update(sql);
                }

                L1World.getInstance().broadcastPacketGreenMessage(msg);
                L1World.getInstance().broadcastServerMessage(msg);

                ShopTable.getInstance().load();
            }
        } catch (Exception e) {
            gm.sendPackets(".이벤트 용갑/반지/귀걸이 켬/끔");
        }
    }

    private void recallItem(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(name);

            if (target == null) {
                gm.sendPackets("접속해 있지 않은 사용자입니다");
                return;
            }

            S_InventoryInfo packet = new S_InventoryInfo(target);
            packet.setMessenger(new RecallItemSelectedAction(target, gm));
            packet.build();

            gm.sendPackets("회수할 아이템과 수량을 선택하세요");
            gm.sendPackets(packet);
        } catch (Exception e) {
            gm.sendPackets(".회수 사용자명");
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

            gm.sendPackets("동기화가 완료되었습니다.");
        } catch (Exception e) {
            logger.error("오류", e);
            gm.sendPackets("동기화 실패");
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
                        logger.info("패킷전달 : {} ", num);
                    }
                } catch (Exception e) {
                    logger.error("오류", e);
                }
            }).start();
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("[Command] .스킬아이콘 [번호] [횟수] 입력"));
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

            if ("용해제".equalsIgnoreCase(type)) {
                bugCheck1(gm, checkCount);
            } else if ("되팔이".equalsIgnoreCase(type)) {
                bugCheck2(gm, checkCount);
            } else if ("박스용해".equalsIgnoreCase(type)) {
                bugCheck3(gm);
            } else {
                bugCheck1(gm, checkCount);
                bugCheck2(gm, checkCount);
            }

            if (type == null) {
                type = "버그";
            }

            gm.sendPackets(new S_SystemMessage(type + " 검사 완료"));

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".버그검사 [용해제,되팔이]"));
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

                        gm.sendPackets("박스 : " + boxItem.getName());

                        for (L1TreasureBox.Item o : items) {
                            L1Item realItem = ItemTable.getInstance().findItem(o.getItemId());
                            int crystalCount = ResolventTable.getInstance().getCrystalCount(realItem.getItemId());

                            gm.sendPackets(String.format("지급아이템 : %s, 용해금액 : %d", realItem.getName(), crystalCount * CodeConfig.RATE_CRISTAL * 5));
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
                pc.sendPackets(String.format("[용해제 검사중] %d 퍼센트 진행 (%d/%d)", (int) per, i, list.size()));
            }

            String sql2 = "SELECT * FROM SHOP WHERE ITEM_ID=? AND selling_price>0 AND NPC_ID NOT IN (SELECT NPCID FROM NPC_SHOP_INFO)";

            String itemId = String.valueOf(o.get("item_id"));
            int resolvePrice = Integer.parseInt(String.valueOf(o.get("price")));

            List<Map<String, Object>> sellList = SqlUtils.queryForList(sql2, itemId);

            for (Map<String, Object> sellItem : sellList) {
                int sellingPrice = Integer.parseInt(String.valueOf(sellItem.get("selling_price")));

                if (resolvePrice > sellingPrice) {
                    String msg = "용해제버그 : 아이템아이디 : " + itemId + ",판매가 : " + sellingPrice + " , 용해가 : " + resolvePrice;

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
                pc.sendPackets(String.format("[되팔이 검사중] %d 퍼센트 진행 (%d/%d)", (int) per, i, list.size()));
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
                    pc.sendPackets(new S_SystemMessage("템구매 매입상점 : " + purNpcId + " , 판매상점 : " + npcId + ", 판매아이템 : " + itemId + ",차액 : " + (purchasingPrice - sellingPrice)));
                }
            }
        }
    }

    private void safeMode(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String status = st.nextToken();

            if (status.equalsIgnoreCase("켬")) {
                CodeConfig.SAFE_MODE = true;

                String msg = "[" + ServerConfig.SERVER_NAME + "] 안전모드가 실행되었습니다.";

                L1World.getInstance().broadcastServerMessage(msg);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, msg));

                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance pc : players) {
                    L1CommonUtils.safeMode(pc, true);
                }
            } else if (status.equalsIgnoreCase("끔")) {
                CodeConfig.SAFE_MODE = false;

                String msg = "[" + ServerConfig.SERVER_NAME + "] 안전모드가 해제되었습니다.";

                L1World.getInstance().broadcastServerMessage(msg);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, msg));

                Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

                for (L1PcInstance pc : players) {
                    L1CommonUtils.safeMode(pc, false);
                }
            }

        } catch (Exception eee) {
            gm.sendPackets(new S_SystemMessage(".안전모드 [켬/끔] 으로 입력하세요."));
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
                    logger.error("오류", e);
                }
            }
        }).start();
    }

    private void showHelp(L1PcInstance pc) {
        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
        pc.sendPackets(new S_SystemMessage("  GM Commands 명령어 List..."));
        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
        pc.sendPackets(new S_SystemMessage("  인원 : \\fV뻥 누구 위치 검색 온라인 회수"));
        pc.sendPackets(new S_SystemMessage("  정리 : \\fV청소 삭제 확정드랍 확정드랍스킬"));
        pc.sendPackets(new S_SystemMessage("  셋팅 : \\fV셋팅 레벨 렙작 스킬마스터 봉인암호변경"));
        pc.sendPackets(new S_SystemMessage("         \\fV캐릭인챈 서버설정"));
        pc.sendPackets(new S_SystemMessage("  선물 : \\fV선물 전체선물 아지트지급"));
        pc.sendPackets(new S_SystemMessage("  정보 : \\fV정보 검사 감시 계정 메모리 서버저장"));
        pc.sendPackets(new S_SystemMessage("  채팅 : \\fV채금 채금풀기 채팅 퀴즈설정 퀴즈변경"));
        pc.sendPackets(new S_SystemMessage("  배치 : \\fV배치 유저서먼 인벤삭제 맵몬스터"));
        pc.sendPackets(new S_SystemMessage("  생성 : \\fV아데나 아이템"));
        pc.sendPackets(new S_SystemMessage("  버프 : \\fV피바 소생 투명 올버프 전체버프 특별버프"));
        pc.sendPackets(new S_SystemMessage("  이동 : \\fV출두 가라 이동 소환"));
        pc.sendPackets(new S_SystemMessage("         \\fV라던 사냥터 귀환 감옥"));
        pc.sendPackets(new S_SystemMessage("  이벤 : \\fV이벤트 토너먼트 안전모드 "));
        pc.sendPackets(new S_SystemMessage("         \\fV변신이벤트"));
        pc.sendPackets(new S_SystemMessage("  관리 : \\fV밴아이피 계정추가 계정압류 아덴검사"));
        pc.sendPackets(new S_SystemMessage("         \\fV레벨제한 원격교환 캐릭로그"));
        pc.sendPackets(new S_SystemMessage("  추방 : \\fV추방 영구추방"));
        pc.sendPackets(new S_SystemMessage("  대기 : \\fV오픈대기 맵핵"));
        pc.sendPackets(new S_SystemMessage("  로봇 : \\fV로봇1 로봇삭제 로봇전창 로봇장사 오토"));
        pc.sendPackets(new S_SystemMessage("  공성 : \\fV공성시작"));
        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
        pc.sendPackets(new S_SystemMessage("  옵션 : \\fV속도 날씨 리로드 게시판삭제 대미지체크 "));
        pc.sendPackets(new S_SystemMessage("         \\fV이미지 인벤이미지 혈맹원 혈마크 경험치복구"));
        pc.sendPackets(new S_SystemMessage("         \\fV감시자보스 보스 보스삭제 보스모두삭제 "));
        pc.sendPackets(new S_SystemMessage("         \\fV후원추가 아이디변경 불량아이디 단풍나무"));
        pc.sendPackets(new S_SystemMessage("         \\fV버그검사"));

        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
    }

    private void packet(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int id = Integer.parseInt(st.nextToken(), 10);
            gm.sendPackets(new S_PacketBox(id));
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("[Command] .패킷 [id] 입력"));
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
            gm.sendPackets(new S_SystemMessage("[Command] .패킷1 [id] 입력"));
        }
    }

    private void packet2(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int type = Integer.parseInt(st.nextToken(), 10);
            int value = Integer.parseInt(st.nextToken(), 10);
            gm.sendPackets(new S_SkillIconGFX(type, value));
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage("[Command] .패킷2 [id] 입력"));
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

                    gm.sendPackets(new S_SystemMessage("\\fU레벨 : " + pc.getLevel() + ", 캐릭명 : " + pc.getName() + ", 계정 : " + pc.getAccountName()));
                    searchCount++;
                } catch (Exception ignored) {

                }
            }
            gm.sendPackets(new S_SystemMessage("\\fY" + searchCount + "명의 유저가 겜중입니다."));
            gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".온라인"));
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
            pc.sendPackets(new S_SystemMessage(".인벤삭제"));
        }
    }

    private void stopWar(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();

            WarTimeScheduler.getInstance().stopWar(name);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".공성종료 [성이름두글자]"));
        }
    }/// 추가

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
            pc.sendPackets(new S_SystemMessage("[Command] .혈마크"));
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
                pc.sendPackets(new S_SystemMessage("[ " + clanname + " ] 혈맹을 소환하였습니다."));
            } else {
                pc.sendPackets(new S_SystemMessage("[ " + clanname + " ] 혈맹은 존재하지 않습니다."));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".혈맹소환 [혈맹이름] 순서로 입력"));
        }
    }

    private void rate(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            int value = Integer.parseInt(tok.nextToken());

            StringBuilder text2 = new StringBuilder();

            if (type.equalsIgnoreCase("경험치")) {
                CodeConfig.RATE_XP = value;
            } else if (type.equalsIgnoreCase("아이템")) {
                CodeConfig.RATE_DROP_ITEMS = value;
            } else if (type.equalsIgnoreCase("아데나")) {
                CodeConfig.RATE_DROP_ADENA = value;
            } else {
                gm.sendPackets(new S_SystemMessage("[Command] .배율 [경험치, 아이템, 아데나] [값]입력"));
                return;
            }

            text2.append(" = 경험치: ").append(CodeConfig.RATE_XP).append("배 = 아이템: ")
                    .append(CodeConfig.RATE_DROP_ITEMS).append("배 = 아데나: ")
                    .append(CodeConfig.RATE_DROP_ADENA).append("배 =");
            String text = " = 경험치: " + CodeConfig.RATE_XP + "배 = 아이템: " +
                    CodeConfig.RATE_DROP_ITEMS + "배 = 아데나: " +
                    CodeConfig.RATE_DROP_ADENA + "배 =";
            gm.sendPackets(new S_SystemMessage("*이전 배율*" + text));
            gm.sendPackets(new S_SystemMessage("*변경 배율*" + text2));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("[Command] .배율 [경험치, 아이템, 아데나] [값]입력"));
        }
    }

    private void HcPacketStop(L1PcInstance pc, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String name = tok.nextToken();
            L1PcInstance player = L1World.getInstance().getPlayer(name);

            if (player == null) {
                pc.sendPackets(new S_SystemMessage(name + "님은 월드상에 존재하지 않습니다."));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".패킷정지 [대상이름]"));
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

                target.sendPackets(new S_SkillSound(target.getId(), 1091));//비둘기액션
                target.sendPackets(new S_SkillSound(target.getId(), 4856));// 하트액션
                target.sendPackets(new S_SystemMessage("운영자가 전체유저에게 선물을 주었습니다."));
                target.sendPackets(new S_SystemMessage("아이템 :  [" + item.getViewName() + "]"));
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".전체선물 아이템ID 인첸트수 아이템수로 입력해 주세요."));
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
            pc.sendPackets(new S_SystemMessage(".유저서먼 캐릭터명 NPCID 마릿수"));
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
                gm.sendPackets(new S_SystemMessage("1-99의 범위에서 지정해 주세요"));
                return;
            }

            target.setExp(ExpTable.getInstance().getExpByLevel(level));
            gm.sendPackets(new S_SystemMessage(target.getName() + "님의 레벨이 변경됨! .검 [케릭명]으로 확인요망"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".렙작 [케릭명] [레벨] 입력"));
        }
    }

    private void setMaxLevel(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int level = Integer.parseInt(st.nextToken());

            CodeConfig.MAX_LEVEL = level;

            L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "최대 레벨이 " + level + "로 설정되었습니다."));
        } catch (Exception e) {
            gm.sendPackets(".레벨제한 숫자");
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
            pc.sendPackets(new S_SystemMessage(".이팩트 [숫자] 라고 입력해 주세요."));
        }
    }

    private void effect(L1PcInstance pc, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            int sprid = Integer.parseInt(stringtokenizer.nextToken());

            pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), sprid));
            Broadcaster.broadcastPacket(pc, new S_EffectLocation(pc.getX(), pc.getY(), sprid));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".이팩트 [숫자] 라고 입력해 주세요."));
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
            gm.sendPackets(new S_SystemMessage(".공성시간이 " + formatter.format(cal.getTime()) + "로 변경 되었습니다."));

            CastleTable.getInstance().updateWarTime(name, cal);
            WarTimeScheduler.getInstance().setWarStartTime(name, cal);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".공성시작 성이름두글자(켄트,오크,윈다,기란,하이,드워,아덴,디아) 분"));
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
            gm.sendPackets(new S_SystemMessage(".전체소환 커맨드 에러"));
        }

    }

    private void recallNow(L1PcInstance gm, L1PcInstance target) {
        try {
            if (target.isDead()) {
                gm.sendPackets("대상이 죽어있습니다");
                return;
            }

            L1TeleportUtils.teleportToTargetFront(target, gm, 1);
            target.sendPackets(new S_SystemMessage("게임 마스터에게 소환되었습니다."));
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
                gm.sendPackets(new S_SystemMessage("그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".경험치복구 [캐릭터명]을 입력 해주세요."));
        }
    }

    private void memFree(L1PcInstance gm) {
        java.lang.System.gc();

        gm.sendPackets(new S_SystemMessage("gc 사용후 메모리 정보"));

        long long_total = Runtime.getRuntime().totalMemory();
        int int_total = Math.round(long_total / 1000000F);
        long long_free = Runtime.getRuntime().freeMemory();
        int int_free = Math.round(long_free / 1000000F);
        long long_max = Runtime.getRuntime().maxMemory();
        int int_max = Math.round(long_max / 1000000F);

        gm.sendPackets(new S_SystemMessage("사용한 메모리 : " + int_total + "MB"));
        gm.sendPackets(new S_SystemMessage("남은 메모리 : " + int_free + "MB"));
        gm.sendPackets(new S_SystemMessage("최대 사용가능 메모리 : " + int_max + "MB"));
    }

    private void toChangePassword(L1PcInstance gm, L1PcInstance pc, String passwd) {
        SqlUtils.query("select account_name from characters where char_name = ?", (rs, i) -> {
            String login = rs.getString(1);

            SqlUtils.update("UPDATE accounts SET password=? WHERE login = ?", passwd, login);
            gm.sendPackets(new S_ChatPacket(pc, "암호변경계정: [" + login + "] 암호: [" + passwd + "]", L1Opcodes.S_OPCODE_NORMALCHAT, 2));
            gm.sendPackets(new S_SystemMessage(pc.getName() + "의 암호 변경이 성공적으로 완료되었습니다."));
            pc.sendPackets(new S_SystemMessage("귀하의 계정 정보가 갱신되었습니다."));

            return null;
        }, pc.getName());
    }

    private void toChangePassword(L1PcInstance pc, String name, String passwd) {
        SqlUtils.query("select account_name from characters where char_name = ?", (rs, i) -> {
            String login = rs.getString(1);
            SqlUtils.update("UPDATE accounts SET password=? WHERE login =?", passwd, login);
            pc.sendPackets(new S_ChatPacket(pc, "암호변경계정: [" + login + "] 암호: [" + passwd + "]", L1Opcodes.S_OPCODE_NORMALCHAT, 2));
            pc.sendPackets(new S_SystemMessage(name + "의 암호 변경이 성공적으로 완료되었습니다."));
            return null;
        }, name);
    }

    private void changePassword(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String passwd = tok.nextToken();

            if (passwd.length() < 4) {
                gm.sendPackets(new S_SystemMessage("입력하신 암호의 자릿수가 너무 짧습니다."));
                gm.sendPackets(new S_SystemMessage("최소 4자 이상 입력해 주십시오."));
                return;
            }

            if (passwd.length() > 20) {
                gm.sendPackets(new S_SystemMessage("입력하신 암호의 자릿수가 너무 깁니다."));
                gm.sendPackets(new S_SystemMessage("최대 12자 이하로 입력해 주십시오."));
                return;
            }

            if (!StringUtils.isDisitAlpha(passwd)) {
                gm.sendPackets(new S_SystemMessage("암호에 허용되지 않는 문자가 포함되었습니다."));
                return;
            }
            L1PcInstance target = L1World.getInstance().getPlayer(user);

            if (target != null) {
                toChangePassword(gm, target, passwd);
            } else {
                toChangePassword(gm, user, passwd);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".암호변경 [캐릭명] [암호]로 입력해주세요."));
        }
    }

    private void searchDatabase(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            int type = Integer.parseInt(tok.nextToken());
            String name = tok.nextToken();

            searchObject(gm, type, "%" + name + "%");
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".검색 [0~5] [name]을 입력 해주세요."));
            gm.sendPackets(new S_SystemMessage("0=잡템, 1=무기, 2=갑옷, 3=npc, 4=polymorphs, 5=npc(gfxid)"));
        }
    }

    private void standBy(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String status = st.nextToken();
            if (status.equalsIgnoreCase("켬")) {
                CodeConfig.STANDBY_SERVER = true;
                L1World.getInstance().broadcastServerMessage(L1Msg.STAND_BY_MSG);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, L1Msg.STAND_BY_MSG));
            } else if (status.equalsIgnoreCase("끔")) {
                CodeConfig.STANDBY_SERVER = false;
                L1World.getInstance().broadcastServerMessage(L1Msg.STAND_BY_OFF_MSG);
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, L1Msg.STAND_BY_OFF_MSG));
            }

        } catch (Exception eee) {
            gm.sendPackets(new S_SystemMessage(".오픈대기 [켬/끔] 으로 입력하세요."));
            gm.sendPackets(new S_SystemMessage("켬 - 오픈대기상태로 전환 | 끔 - 일반모드로 게임시작"));
        }
    }

    private void givesItem2(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String pcname = st.nextToken();
            L1PcInstance pc = L1World.getInstance().getPlayer(pcname);

            if (pc == null) {
                gm.sendPackets(new S_SystemMessage("해당 아이디를 가진 케릭터가 존재하지 않습니다."));
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
                    gm.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않았습니다."));
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
                        pc.sendPackets(new S_SystemMessage("운영자가 선물로[" + item.getLogName() + "]를 주었습니다."));
                        String msg = item.getViewName2() + "(ID:" + itemid + ")를 " + pc.getName() + "에게 주었습니다.";

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
                        pc.sendPackets(new S_SystemMessage("운영자가 선물로[" + item.getLogName() + "]를 주었습니다."));

                        String msg = item.getViewName2() + "(ID:" + itemid + ")를 " + pc.getName() + "에게 주었습니다.";
                        gm.sendPackets(msg);

                        logger.info(msg);
                    }
                }
            } else {
                gm.sendPackets(new S_SystemMessage("지정 ID의 아이템은 존재하지 않습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".선물 [캐릭터명] [itemid 또는 name] [개수] [인챈트수] [축복] [속성]를 입력 해주세요."));
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

        pc.sendPackets(new S_SystemMessage("서버저장이 완료되었습니다."));
    }

    private void chatX(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            if (target != null) {
                target.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.STATUS_CHAT_PROHIBITED);
                target.sendPackets(new S_SkillIconGFX(L1SkillIcon.채금, 0));
                target.sendPackets(new S_ServerMessage(288));
                gm.sendPackets(new S_SystemMessage("해당캐릭의 채금을 해제 했습니다.."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".채금풀기 캐릭터명 이라고 입력해 주세요."));
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
                        gm.sendPackets(new S_SystemMessage(player.getName() + "님이 " + l1ItemInstance.getLogName() + " 을소지하고있습니다. "));
                    }
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".인첸검사 레벨 (전체 사용자 일정인첸렙 이상 검사)"));
        }
    }

    private void checkAden(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String para1 = stringtokenizer.nextToken();
            int money = Integer.parseInt(para1);

            List<Map<String, Object>> characterWarehouseAccounts = SqlUtils.queryForList("SELECT account_name,count from character_warehouse where item_Id=40308 and count > ?", money);

            for (Map<String, Object> o : characterWarehouseAccounts) {
                gm.sendPackets(new S_SystemMessage("[창고] 계정명 : " + o.get("account_name") + "님이 " + o.get("count") + "아덴"));
            }

            List<Map<String, Object>> characterNames = SqlUtils.queryForList("SELECT (select char_name from characters where objid=char_id)charName,count from character_items where item_Id=40308 and count>?", money);
            for (Map<String, Object> o : characterNames) {
                gm.sendPackets(new S_SystemMessage("[인벤] 캐릭명 : " + o.get("charName") + "님이 " + o.get("count") + "아덴"));
            }

            List<Map<String, Object>> clanNames = SqlUtils.queryForList("SELECT clan_name,count from clan_warehouse where item_Id=40308 and count > ?", money);

            for (Map<String, Object> o : clanNames) {
                gm.sendPackets(new S_SystemMessage("[혈창] 혈명 : " + o.get("clan_name") + "님이 " + o.get("count") + "아덴"));
            }

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".아덴검사 액수 (전체 사용자 일정액수 이상 검사)"));
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
                    break; // 추가
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
                gm.sendPackets(new S_SystemMessage("접속중이지 않는 유저 ID 입니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".가라 (보낼케릭터명) 으로 입력해 주세요."));
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

                    pc.sendPackets(new S_SystemMessage(target.getClanName() + " 혈맹에게 " + pobyHouse.getHouseName() + "번을 지급하였습니다."));
                    for (L1PcInstance tc : targetClan.getOnlineClanMember()) {
                        tc.sendPackets(new S_SystemMessage("게임마스터로부터 " + pobyHouse.getHouseName() + "번을 지급 받았습니다."));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage(target.getName() + "님은 혈맹에 속해 있지 않습니다."));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(73, playerName));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".아지트지급 <지급할혈맹원> <아지트번호>"));
        }
    }

    private void dmgScarecrow(L1PcInstance gm) {
        try {
            if (gm.getScarecrow()) {
                gm.sendPackets(new S_SystemMessage("\\aH : 대미지체크 OFF"));
                gm.setScarecrow(false);
            } else {
                gm.sendPackets(new S_SystemMessage("\\aH : 대미지체크 ON"));
                gm.setScarecrow(true);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("대미지 명령어 에러."));
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
                    logger.error("오류", e);
                }
            }).start();
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage("[Command] .코드 [숫자] 입력"));
        }
    }

    private void clear(L1PcInstance gm) {
        for (L1Object obj : L1World.getInstance().getVisibleObjects(gm, 15)) {
            if (obj instanceof L1MonsterInstance) { // 몬스터라면
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.getCurrentHp() > 0) {
                    npc.receiveDamage(gm, 50000); // 대미지
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
                caller.sendPackets(target.getName() + "의 아이템 : " + item.getName() + "(" + count + ")를 회수하였습니다");
                target.sendPackets("운영자가 당신의 아이템 : " + item.getName() + "(" + count + ")를 회수하였습니다");
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
            pc.sendPackets("위치기록중 : " + pc.getX() + "," + pc.getY() + "," + pc.getMapId());
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
                caller.sendPackets("아이템을 하나만 선택하세요");
                return;
            }

            for (int i = 0; i < size; i++) {
                int objectId = packet.readD();

                L1ItemInstance item = getPc().getInventory().getItem(objectId);

                nextDropMap.put(target.getName(), item.getId());

                caller.sendPackets(target.getName() + "의 다음 드랍 아이템이 : " + item.getViewName2() + "로 설정됨");
            }
        }

        @Override
        public String key() {
            return getClass().getName();
        }
    }
}
