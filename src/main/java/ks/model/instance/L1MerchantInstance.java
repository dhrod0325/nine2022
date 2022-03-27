package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.core.datatables.CastleTable;
import ks.core.datatables.NPCTalkDataTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_ChangeHeading;
import ks.packets.serverpackets.S_NPCPack;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.npc.NpcRestScheduler;
import ks.scheduler.timer.gametime.GameTimeScheduler;
import ks.system.dogFight.L1DogFight;
import ks.system.race.L1RaceManager;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1MerchantInstance extends L1NpcInstance {
    private static final long REST_MILLISEC = 10000;

    private static final Logger logger = LogManager.getLogger(L1MerchantInstance.class);

    public L1MerchantInstance(L1Npc template) {
        super(template);
    }

    private static String talkToTownAdviser(L1PcInstance pc, int town_id) {
        String htmlid;
        if (pc.getHomeTownId() == town_id) {
            htmlid = "artisan1";
        } else {
            htmlid = "artisan2";
        }

        return htmlid;
    }

    private static String talkToTownMaster(L1PcInstance pc, int town_id) {
        String htmlid;
        if (pc.getHomeTownId() == town_id) {
            htmlid = "hometown";
        } else {
            htmlid = "othertown";
        }

        return htmlid;
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }

        setActivated(false);
        startAI();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        int npcId = getTemplate().getNpcId();

        L1Quest quest = player.getQuest();

        String htmlid = null;
        String[] htmldata = null;

        int pcX = player.getX();
        int pcY = player.getY();
        int npcX = getX();
        int npcY = getY();

        if (getTemplate().getChangeHead()) {
            int heading = 0;

            if (pcX > npcX && pcY < npcY)
                heading = 1;
            else if (pcX > npcX && pcY == npcY)
                heading = 2;
            else if (pcX > npcX)
                heading = 3;
            else if (pcX == npcX && pcY > npcY)
                heading = 4;
            else if (pcX < npcX && pcY > npcY)
                heading = 5;
            else if (pcX < npcX && pcY == npcY)
                heading = 6;
            else if (pcX < npcX)
                heading = 7;

            setHeading(heading);
            Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));

            synchronized (this) {
                if (isRest()) {
                    restTime = System.currentTimeMillis() + REST_MILLISEC;
                } else {
                    setRest(true);
                    restTime = System.currentTimeMillis() + REST_MILLISEC;
                    NpcRestScheduler.getInstance().addNpc(this);
                }
            }
        }


        L1LogUtils.gmLog(player, "엔피씨 ID : {} , TalkData : {}", npcId, talking);

        if (npcId == 460000129) {
            if (player.getClan() == null) {
                return;
            }

            int castleId = L1CastleLocation.getCastleIdByArea(player.getLocation());

            if (castleId == 0) {
                return;
            }

            L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);

            if (player.getClan().getCastleId() != castleId) {
                player.sendPackets("당신은 " + castle.getName() + "의 성주혈이 아닙니다");
                return;
            }

            if (player.getTimer().isTimeOver("성혈코마버프")) {
                L1SkillUse l1skilluse = new L1SkillUse(player, L1SkillId.STATUS_COMA_5, player.getId(), player.getX(), player.getY(), 3600, L1SkillUse.TYPE_NPC_BUFF);
                l1skilluse.run();

                player.getTimer().setWaitTime("성혈코마버프", 3000);
            }

            return;
        }

        switch (npcId) {
            case 50113:  //레크만
                if (player.isKnight() || player.isElf() || player.isDarkElf() || player.isIllusionist()) {
                    int talkStep = quest.getStep(L1Quest.QUEST_FIRSTQUEST);
                    if (talkStep == 1) {
                        if (player.getLevel() >= 5) {
                            htmlid = "orena4";
                        } else {
                            htmlid = "orena14";
                        }
                    } else if (talkStep == 255) {
                        htmlid = "orena11";
                    }
                } else {
                    htmlid = "orena12";
                }
                break;
            case 4200088:
                htmlid = "expgive";
                htmldata = new String[]{CodeConfig.EXP_GIVE_MAX_LEVEL + ""};
                break;
            case 70009:
                if (player.isCrown()) {
                    htmlid = "gerengp1";
                } else if (player.isKnight()) {
                    htmlid = "gerengk1";
                } else if (player.isElf()) {
                    htmlid = "gerenge1";
                } else if (player.isWizard()) {
                    if (player.getLevel() >= 30) {
                        if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                            int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
                            if (lv30_step >= 4) {
                                htmlid = "gerengw3";
                            } else if (lv30_step >= 3) {
                                htmlid = "gerengT4";
                            } else if (lv30_step >= 2) {
                                htmlid = "gerengT3";
                            } else if (lv30_step >= 1) {
                                htmlid = "gerengT2";
                            } else {
                                htmlid = "gerengT1";
                            }
                        } else {
                            htmlid = "gerengw3";
                        }
                    } else {
                        htmlid = "gerengw3";
                    }
                } else if (player.isDarkElf()) {
                    htmlid = "gerengde1";
                }
                break;
            case 70011:
                int time = GameTimeScheduler.getInstance().getTime().getSeconds() % 86400;
                if (time < 60 * 60 * 6 || time > 60 * 60 * 20) { // 20:00?6:00
                    htmlid = "shipEvI6";
                }
                break;
            case 70087:
                if (player.isDarkElf()) {
                    htmlid = "sedia";
                }
                break;
            case 70528:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_TALKING_ISLAND);
                break;
            case 70534:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_TALKING_ISLAND);
                break;
            case 70546:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_KENT);
                break;
            case 70553:
                boolean hascastle = checkHasCastle(player, L1CastleLocation.KENT_CASTLE_ID);

                if (hascastle) {
                    if (checkClanLeader(player)) {
                        htmlid = "ishmael1";
                    } else {
                        htmlid = "ishmael6";
                        htmldata = new String[]{player.getName()};
                    }
                } else {
                    htmlid = "ishmael7";
                }
                break;
            case 70633:
                boolean hascastle04 = checkHasCastle(player, L1CastleLocation.GIRAN_CASTLE_ID);
                if (hascastle04) {
                    htmlid = "colbert1";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "colbert4";
                }
                break;
            case 70654:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_WERLDAN);
                break;
            case 70556:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_KENT);
                break;
            case 70567:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_GLUDIO);
                break;
            case 70572:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_GLUDIO);
                break;
            case 70594:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_GIRAN);
                break;
            case 70623:
                boolean hasCastle3 = checkHasCastle(player, L1CastleLocation.GIRAN_CASTLE_ID);
                if (hasCastle3) {
                    if (checkClanLeader(player)) {
                        htmlid = "orville1";
                    } else {
                        htmlid = "orville6";
                        htmldata = new String[]{player.getName()};
                    }
                } else {
                    htmlid = "orville7";
                }
                break;
            case 70631:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_GIRAN);
                break;
            case 70663:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_WERLDAN);
                break;
            case 70665:
                boolean hascastle5 = checkHasCastle(player, L1CastleLocation.DOWA_CASTLE_ID);
                if (hascastle5) {
                    if (checkClanLeader(player)) {
                        htmlid = "potempin1";
                    } else {
                        htmlid = "potempin6";
                        htmldata = new String[]{player.getName()};
                    }
                } else {
                    htmlid = "potempin7";
                }
                break;
            case 70721:
                boolean hascastle6 = checkHasCastle(player, L1CastleLocation.ADEN_CASTLE_ID);
                if (hascastle6) {
                    if (checkClanLeader(player)) {
                        htmlid = "timon1";
                    } else {
                        htmlid = "timon6";
                        htmldata = new String[]{player.getName()};
                    }
                } else {
                    htmlid = "timon7";
                }
                break;
            case 70748:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_OREN);
                break;
            case 70761:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_OREN);
                break;
            case 70762://왕도제작게속가능하게
                if (!player.isDarkElf())
                    htmlid = "karif9";
                if (player.getLevel() < 50) {
                    htmlid = "karif9";
                }
                break;
            case 70763:
                if (player.isWizard()) {
                    int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
                    if (lv30_step == L1Quest.QUEST_END) {
                        if (player.getLevel() >= 45) {
                            int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
                            if (lv45_step >= 1 && lv45_step != L1Quest.QUEST_END) {
                                htmlid = "talassmq2";
                            } else if (lv45_step <= 0) {
                                htmlid = "talassmq1";
                            }
                        }
                    } else if (lv30_step == 4) {
                        htmlid = "talassE1";
                    } else if (lv30_step == 5) {
                        htmlid = "talassE2";
                    }
                }
                break;
            case 70774:
                htmlid = talkToTownMaster(player,
                        L1TownLocation.TOWNID_WINDAWOOD);
                break;
            case 70775:
                if (player.isKnight()) {
                    if (player.getLevel() >= 30) {
                        if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                            int lv30_step = quest
                                    .getStep(L1Quest.QUEST_LEVEL30);
                            if (lv30_step == 0) {
                                htmlid = "mark1";
                            } else {
                                htmlid = "mark2";
                            }
                        }
                    }
                }
                break;
            case 70776:
                if (player.isCrown()) {
                    int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
                    if (lv45_step == 1) {
                        htmlid = "meg1";
                    } else if (lv45_step == 2) {
                        htmlid = "meg2";
                    } else if (lv45_step >= 4) {
                        htmlid = "meg3";
                    }
                }
                break;
            case 70782:
                if (player.getGfxId().getTempCharGfx() == 1037) {
                    if (player.isCrown()) {
                        if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1) {
                            htmlid = "ant1";
                        } else {
                            htmlid = "ant3";
                        }
                    } else {
                        htmlid = "ant3";
                    }
                }
                break;

            case 70783:
                if (player.isCrown()) {
                    if (player.getLevel() >= 30) {
                        if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                            int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
                            if (lv30_step == L1Quest.QUEST_END) {
                                htmlid = "aria3";
                            } else if (lv30_step == 1) {
                                htmlid = "aria2";
                            } else {
                                htmlid = "aria1";
                            }
                        }
                    }
                }
                break;
            case 70784:
                boolean hascastle2 = checkHasCastle(player, L1CastleLocation.WW_CASTLE_ID);
                if (hascastle2) {
                    if (checkClanLeader(player)) {
                        htmlid = "othmond1";
                    } else {
                        htmlid = "othmond6";
                        htmldata = new String[]{player.getName()};
                    }
                } else {
                    htmlid = "othmond7";
                }
                break;
            case 70788:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_WINDAWOOD);
                break;
            case 70796:
                if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
                    if (player.getLevel() > 13) {
                        htmlid = "dunham1";
                    }
                }
                break;
            case 70798:
                if (player.isKnight()) {
                    if (player.getLevel() >= 15) {
                        int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
                        if (lv15_step >= 1) {
                            htmlid = "riky5";
                        } else {
                            htmlid = "riky1";
                        }
                    } else {
                        htmlid = "riky6";
                    }
                }
                break;
            case 70799:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                break;
            case 70802:
                if (player.isKnight()) {
                    if (player.getLevel() >= 15) {
                        int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
                        if (lv15_step == L1Quest.QUEST_END) {
                            htmlid = "aanon7";
                        } else if (lv15_step == 1) {
                            htmlid = "aanon4";
                        }
                    }
                }
                break;
            case 70806:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
                break;
            case 70811:
                if (quest.getStep(L1Quest.QUEST_LYRA) >= 1) {
                    htmlid = "lyraEv3";
                } else {
                    htmlid = "lyraEv1";
                }
                break;
            case 70815:
                htmlid = talkToTownMaster(player,
                        L1TownLocation.TOWNID_ORCISH_FOREST);
                break;
            case 70822:
                boolean hascastle1 = checkHasCastle(player, L1CastleLocation.OT_CASTLE_ID);
                if (hascastle1) {
                    if (checkClanLeader(player)) {
                        htmlid = "seghem1";
                    } else {
                        htmlid = "seghem6";
                        htmldata = new String[]{player.getName()};
                    }
                } else {
                    htmlid = "seghem7";
                }
                break;
            case 70830:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_ORCISH_FOREST);
                break;
            case 70838: // 네루파
                if (player.isCrown() || player.isKnight() || player.isWizard()
                        || player.isDragonKnight() || player.isIllusionist()) {
                    htmlid = "nerupam1";
                } else if (player.isDarkElf() && (player.getLawful() <= -1)) {
                    htmlid = "nerupaM2";
                } else if (player.isDarkElf()) {
                    htmlid = "nerupace1";
                } else if (player.isElf()) {
                    htmlid = "nerupae1";
                }
                break;
            case 70841:
                if (player.isElf()) {
                    htmlid = "luudielE1";
                } else if (player.isDarkElf()) {
                    htmlid = "luudielCE1";
                } else {
                    htmlid = "luudiel1";
                }
                break;
            case 70844:
                if (player.isElf()) {
                    if (player.getLevel() >= 30) {
                        if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                            int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
                            if (lv30_step == L1Quest.QUEST_END) {
                                htmlid = "motherEE3";
                            } else if (lv30_step >= 1) {
                                htmlid = "motherEE2";
                            } else {
                                htmlid = "motherEE1";
                            }
                        } else {
                            htmlid = "mothere1";
                        }
                    } else {
                        htmlid = "mothere1";
                    }
                }
                break;
            case 70860:
                htmlid = talkToTownMaster(player, L1TownLocation.TOWNID_HEINE);
                break;
            case 70876:
                htmlid = talkToTownAdviser(player, L1TownLocation.TOWNID_HEINE);
                break;
            case 70885:
                if (player.isDarkElf()) {
                    if (player.getLevel() >= 15) {
                        int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
                        if (lv15_step == L1Quest.QUEST_END) {
                            htmlid = "kanguard3";
                        } else if (lv15_step >= 1) {
                            htmlid = "kanguard2";
                        } else {
                            htmlid = "kanguard1";
                        }
                    } else {
                        htmlid = "kanguard5";
                    }
                }
                break;
            case 70892:
                if (player.isDarkElf()) {
                    if (player.getLevel() >= 30) {
                        if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
                            int lv30_step = quest
                                    .getStep(L1Quest.QUEST_LEVEL30);
                            if (lv30_step == L1Quest.QUEST_END) {
                                htmlid = "ronde5";
                            } else if (lv30_step >= 2) {
                                htmlid = "ronde3";
                            } else if (lv30_step >= 1) {
                                htmlid = "ronde2";
                            } else {
                                htmlid = "ronde1";
                            }
                        } else {
                            htmlid = "ronde7";
                        }
                    } else {
                        htmlid = "ronde7";
                    }
                }
                break;
            case 70895:
                if (player.isDarkElf()) {
                    if (player.getLevel() >= 45) {
                        if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
                            int lv45_step = quest
                                    .getStep(L1Quest.QUEST_LEVEL45);
                            if (lv45_step == L1Quest.QUEST_END) {
                                if (player.getLevel() < 50) {
                                    htmlid = "bluedikaq3";
                                } else {
                                    int lv50_step = quest
                                            .getStep(L1Quest.QUEST_LEVEL50);
                                    if (lv50_step == L1Quest.QUEST_END) {
                                        htmlid = "bluedikaq8";
                                    } else {
                                        htmlid = "bluedikaq6";
                                    }
                                }
                            } else if (lv45_step >= 1) {
                                htmlid = "bluedikaq2";
                            } else {
                                htmlid = "bluedikaq1";
                            }
                        } else {
                            htmlid = "bluedikaq5";
                        }
                    } else {
                        htmlid = "bluedikaq5";
                    }
                }
                break;
            case 70904:
                if (player.isDarkElf()) {
                    if (quest.getStep(L1Quest.QUEST_LEVEL45) == 1) {
                        htmlid = "koup12";
                    }
                }
                break;
            case 80047:
                if (player.getKarmaLevel() > -3) {
                    htmlid = "uhelp1";
                } else {
                    htmlid = "uhelp2";
                }
                break;
            case 80048:
                int level = player.getLevel();
                if (level <= 44) {
                    htmlid = "entgate3";
                } else if (level <= 69) {//버땅레벨
                    htmlid = "entgate2";
                } else {
                    htmlid = "entgate";
                }
                break;
            case 80049:
                if (player.getKarma() <= -10000000) {
                    htmlid = "betray11";
                } else {
                    htmlid = "betray12";
                }
                break;
            case 80050:
                if (player.getKarmaLevel() > -1) {
                    htmlid = "meet103";
                } else {
                    htmlid = "meet101";
                }
                break;
            case 80053:
                int karmaLevel = player.getKarmaLevel();

                if (karmaLevel == 0) {
                    htmlid = "aliceyet";
                } else if (karmaLevel >= 1) {
                    if (player.getInventory().checkItem(196)
                            || player.getInventory().checkItem(197)
                            || player.getInventory().checkItem(198)
                            || player.getInventory().checkItem(199)
                            || player.getInventory().checkItem(200)
                            || player.getInventory().checkItem(201)
                            || player.getInventory().checkItem(202)
                            || player.getInventory().checkItem(203)) {
                        htmlid = "alice_gd";
                    } else {
                        htmlid = "gd";
                    }
                } else {
                    if (player.getInventory().checkItem(40991)) {
                        htmlid = "Mate_1";
                    } else if (player.getInventory().checkItem(196)) {
                        htmlid = "Mate_2";
                    } else if (player.getInventory().checkItem(197)) {
                        htmlid = "Mate_3";
                    } else if (player.getInventory().checkItem(198)) {
                        htmlid = "Mate_4";
                    } else if (player.getInventory().checkItem(199)) {
                        htmlid = "Mate_5";
                    } else if (player.getInventory().checkItem(200)) {
                        htmlid = "Mate_6";
                    } else if (player.getInventory().checkItem(201)) {
                        htmlid = "Mate_7";
                    } else if (player.getInventory().checkItem(202)) {
                        htmlid = "Mate_8";
                    } else if (player.getInventory().checkItem(203)) {
                        htmlid = "alice_8";
                    } else {
                        htmlid = "alice_no";
                    }
                }
                break;

            case 80055:
                int amuletLevel = 0;
                if (player.getInventory().checkItem(20358)) {
                    amuletLevel = 1;
                } else if (player.getInventory().checkItem(20359)) {
                    amuletLevel = 2;
                } else if (player.getInventory().checkItem(20360)) {
                    amuletLevel = 3;
                } else if (player.getInventory().checkItem(20361)) {
                    amuletLevel = 4;
                } else if (player.getInventory().checkItem(20362)) {
                    amuletLevel = 5;
                } else if (player.getInventory().checkItem(20363)) {
                    amuletLevel = 6;
                } else if (player.getInventory().checkItem(20364)) {
                    amuletLevel = 7;
                } else if (player.getInventory().checkItem(20365)) {
                    amuletLevel = 8;
                }
                if (player.getKarmaLevel() == -1) {
                    if (amuletLevel >= 1) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet1";
                    }
                } else if (player.getKarmaLevel() == -2) {
                    if (amuletLevel >= 2) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet2";
                    }
                } else if (player.getKarmaLevel() == -3) {
                    if (amuletLevel >= 3) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet3";
                    }
                } else if (player.getKarmaLevel() == -4) {
                    if (amuletLevel >= 4) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet4";
                    }
                } else if (player.getKarmaLevel() == -5) {
                    if (amuletLevel >= 5) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet5";
                    }
                } else if (player.getKarmaLevel() == -6) {
                    if (amuletLevel >= 6) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet6";
                    }
                } else if (player.getKarmaLevel() == -7) {
                    if (amuletLevel >= 7) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet7";
                    }
                } else if (player.getKarmaLevel() == -8) {
                    if (amuletLevel >= 8) {
                        htmlid = "uamuletd";
                    } else {
                        htmlid = "uamulet8";
                    }
                } else {
                    htmlid = "uamulet0";
                }
                break;
            case 80056:
                if (player.getKarma() <= -10000000) {
                    htmlid = "infamous11";
                } else {
                    htmlid = "infamous12";
                }
                break;
            case 80058:
                int level5 = player.getLevel();
                if (level5 <= 44) {
                    htmlid = "cpass03";
                } else if (level5 <= 69) {//버땅레벨
                    htmlid = "cpass02";
                } else {
                    htmlid = "cpass01";
                }
                break;
            case 80059:
                if (player.getKarmaLevel() >= 3) {
                    htmlid = "cpass03";
                } else if (player.getInventory().checkItem(40921)) {
                    htmlid = "wpass02";
                } else if (player.getInventory().checkItem(40917)) {
                    htmlid = "wpass14";
                } else if (player.getInventory().checkItem(40912)
                        || player.getInventory().checkItem(40910)
                        || player.getInventory().checkItem(40911)) {
                    htmlid = "wpass04";
                } else if (player.getInventory().checkItem(40909)) {
                    int count = getNecessarySealCount(player);
                    if (player.getInventory().checkItem(40913, count)) {
                        createRuler(player, 1, count);
                        htmlid = "wpass06";
                    } else {
                        htmlid = "wpass03";
                    }
                } else if (player.getInventory().checkItem(40913)) {
                    htmlid = "wpass08";
                } else {
                    htmlid = "wpass05";
                }
                break;
            case 80060:
                if (player.getKarmaLevel() >= 3) {
                    htmlid = "cpass03";
                } else if (player.getInventory().checkItem(40921)) {
                    htmlid = "wpass02";
                } else if (player.getInventory().checkItem(40920)) {
                    htmlid = "wpass13";
                } else if (player.getInventory().checkItem(40909)
                        || player.getInventory().checkItem(40910)
                        || player.getInventory().checkItem(40911)) {
                    htmlid = "wpass04";
                } else if (player.getInventory().checkItem(40912)) {
                    int count = getNecessarySealCount(player);
                    if (player.getInventory().checkItem(40916, count)) {
                        createRuler(player, 8, count);
                        htmlid = "wpass06";
                    } else {
                        htmlid = "wpass03";
                    }
                } else if (player.getInventory().checkItem(40916)) {
                    htmlid = "wpass08";
                } else {
                    htmlid = "wpass05";
                }
                break;
            case 80061:
                if (player.getKarmaLevel() >= 3) {
                    htmlid = "cpass03";
                } else if (player.getInventory().checkItem(40921)) {
                    htmlid = "wpass02";
                } else if (player.getInventory().checkItem(40918)) {
                    htmlid = "wpass11";
                } else if (player.getInventory().checkItem(40909)
                        || player.getInventory().checkItem(40912)
                        || player.getInventory().checkItem(40911)) {
                    htmlid = "wpass04";
                } else if (player.getInventory().checkItem(40910)) {
                    int count = getNecessarySealCount(player);
                    if (player.getInventory().checkItem(40914, count)) {
                        createRuler(player, 4, count);
                        htmlid = "wpass06";
                    } else {
                        htmlid = "wpass03";
                    }
                } else if (player.getInventory().checkItem(40914)) {
                    htmlid = "wpass08";
                } else {
                    htmlid = "wpass05";
                }
                break;
            case 80062:
                if (player.getKarmaLevel() >= 3) {
                    htmlid = "cpass03";
                } else if (player.getInventory().checkItem(40921)) {
                    htmlid = "wpass02";
                } else if (player.getInventory().checkItem(40919)) {
                    htmlid = "wpass12";
                } else if (player.getInventory().checkItem(40909)
                        || player.getInventory().checkItem(40912)
                        || player.getInventory().checkItem(40910)) {
                    htmlid = "wpass04";
                } else if (player.getInventory().checkItem(40911)) {
                    int count = getNecessarySealCount(player);
                    if (player.getInventory().checkItem(40915, count)) {
                        createRuler(player, 2, count);
                        htmlid = "wpass06";
                    } else {
                        htmlid = "wpass03";
                    }
                } else if (player.getInventory().checkItem(40915)) {
                    htmlid = "wpass08";
                } else {
                    htmlid = "wpass05";
                }
                break;
            case 80064:
                if (player.getKarmaLevel() < 1) {
                    htmlid = "meet003";
                } else {
                    htmlid = "meet001";
                }
                break;
            case 80065:
                if (player.getKarmaLevel() < 3) {
                    htmlid = "uturn0";
                } else {
                    htmlid = "uturn1";
                }
                break;
            case 80066:
                if (player.getKarma() >= 10000000) {
                    htmlid = "betray01";
                } else {
                    htmlid = "betray02";
                }
                break;
            case 80071:
                int earringLevel = 0;
                if (player.getInventory().checkItem(21020)) {
                    earringLevel = 1;
                } else if (player.getInventory().checkItem(21021)) {
                    earringLevel = 2;
                } else if (player.getInventory().checkItem(21022)) {
                    earringLevel = 3;
                } else if (player.getInventory().checkItem(21023)) {
                    earringLevel = 4;
                } else if (player.getInventory().checkItem(21024)) {
                    earringLevel = 5;
                } else if (player.getInventory().checkItem(21025)) {
                    earringLevel = 6;
                } else if (player.getInventory().checkItem(21026)) {
                    earringLevel = 7;
                } else if (player.getInventory().checkItem(21027)) {
                    earringLevel = 8;
                }

                if (player.getKarmaLevel() == 1) {
                    if (earringLevel >= 1) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring1";
                    }
                } else if (player.getKarmaLevel() == 2) {
                    if (earringLevel >= 2) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring2";
                    }
                } else if (player.getKarmaLevel() == 3) {
                    if (earringLevel >= 3) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring3";
                    }
                } else if (player.getKarmaLevel() == 4) {
                    if (earringLevel >= 4) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring4";
                    }
                } else if (player.getKarmaLevel() == 5) {
                    if (earringLevel >= 5) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring5";
                    }
                } else if (player.getKarmaLevel() == 6) {
                    if (earringLevel >= 6) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring6";
                    }
                } else if (player.getKarmaLevel() == 7) {
                    if (earringLevel >= 7) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring7";
                    }
                } else if (player.getKarmaLevel() == 8) {
                    if (earringLevel >= 8) {
                        htmlid = "lringd";
                    } else {
                        htmlid = "lring8";
                    }
                } else {
                    htmlid = "lring0";
                }
                break;
            case 80072:
                int karmaLevel1 = player.getKarmaLevel();
                switch (karmaLevel1) {
                    case 1:
                        htmlid = "lsmith0";
                        break;
                    case 2:
                        htmlid = "lsmith1";
                        break;
                    case 3:
                        htmlid = "lsmith2";
                        break;
                    case 4:
                        htmlid = "lsmith3";
                        break;
                    case 5:
                        htmlid = "lsmith4";
                        break;
                    case 6:
                        htmlid = "lsmith5";
                        break;
                    case 7:
                        htmlid = "lsmith7";
                        break;
                    case 8:
                        htmlid = "lsmith8";
                        break;
                    default:
                        htmlid = "";
                        break;
                }
                break;
            case 80076:
                if (player.getInventory().checkItem(41058)) { // 완성한 항해 일지
                    htmlid = "voyager8";
                } else if (player.getInventory().checkItem(49082) || player.getInventory().checkItem(49083)) {
                    if (player.getInventory().checkItem(41038) // 항해 일지 1 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 2 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 3 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 4 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 5 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 6 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 7 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 8 페이지
                            || player.getInventory().checkItem(41039) // 항해 일지 9 페이지
                            || player.getInventory().checkItem(41039)) { // 항해 일지 10 페이지
                        htmlid = "voyager9";
                    } else {
                        htmlid = "voyager7";
                    }
                } else if (player.getInventory().checkItem(49082) // 미완성의 항해 일지
                        || player.getInventory().checkItem(49083)
                        || player.getInventory().checkItem(49084)
                        || player.getInventory().checkItem(49085)
                        || player.getInventory().checkItem(49086)
                        || player.getInventory().checkItem(49087)
                        || player.getInventory().checkItem(49088)
                        || player.getInventory().checkItem(49089)
                        || player.getInventory().checkItem(49090)
                        || player.getInventory().checkItem(49091)) {
                    htmlid = "voyager7";
                }
                break;
            case 80074:
                if (player.getKarma() >= 10000000) {
                    htmlid = "infamous01";
                } else {
                    htmlid = "infamous02";
                }
                break;
            case 4200009: // 보석세공사 데이빗(얼녀귀걸이)
                if (checkItem(player, 49031)) {
                    if (checkItem(player, 21081)) { // 얼녀귀걸이 1
                        htmlid = "gemout1";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 1) {
                        htmlid = "gemout2";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 2) {
                        htmlid = "gemout3";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 3) {
                        htmlid = "gemout4";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 4) {
                        htmlid = "gemout5";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 5) {
                        htmlid = "gemout6";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 6) {
                        htmlid = "gemout7";
                    } else if (player.getQuest().getStep(
                            L1Quest.QUEST_ICEQUEENRING) == 7) {
                        htmlid = "gemout8";
                    } else { // 보석만 가지고있다.
                        htmlid = "gemout17";
                    }
                }
                break;
            case 4204000: // 요정 달장퀘스트 로빈후드
                if (!player.isElf()) {
                    return;
                }

                if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 0) {
                    htmlid = "robinhood1";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 1) {
                    htmlid = "robinhood8";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 2) {
                    htmlid = "robinhood13";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 6) {
                    htmlid = "robinhood9";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 7) {
                    htmlid = "robinhood11";
                } else {
                    htmlid = "robinhood3";
                }
                break;
            case 4210000:
                if (!player.isElf()) {
                    return;
                }

                if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 2) {
                    htmlid = "zybril1";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 3) {
                    htmlid = "zybril7";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 4) {
                    htmlid = "zybril8";
                } else if (player.getQuest().getStep(L1Quest.QUEST_MOONBOW) == 5) {
                    htmlid = "zybril18";
                } else {
                    htmlid = "zybril16";
                }
                break;
            case 70035:
            case 70041:
            case 70042: {
                htmlid = L1RaceManager.getInstance().getHtml();
            }
            default:
                break;
        }

        if (L1DogFight.getInstance().getManager().getNpcId() == npcId) {
            htmlid = L1DogFight.getInstance().getTalkHtml();
        }

        if (htmlid != null) {
            if (htmldata != null) {
                logger.debug("objectId:" + getId() + ", htmldata 데이터 리턴 :" + htmlid);
                player.sendPackets(new S_NPCTalkReturn(getId(), htmlid, htmldata));
            } else {
                logger.debug("objectId:" + getId() + ", htmldata 없는 데이터 리턴 :" + htmlid);
                player.sendPackets(new S_NPCTalkReturn(getId(), htmlid));
            }
        } else {
            if (player.getLawful() < -1000) { // 플레이어가 카오틱
                logger.debug("objectId:" + getId() + ", htmlid 없는 데이터 리턴 :" + ",action:2");
                player.sendPackets(new S_NPCTalkReturn(talking, getId(), 2));
            } else {
                logger.debug("objectId:" + getId() + ", htmlid 없는 데이터 리턴 :" + ",action:1");
                player.sendPackets(new S_NPCTalkReturn(talking, getId(), 1));
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
    }

    private boolean checkItem(L1PcInstance player, int itemid) {
        return player.getInventory().checkItem(itemid);
    }

    private boolean checkHasCastle(L1PcInstance player, int castle_id) {
        if (player.getClanId() != 0) {
            L1Clan clan = L1World.getInstance().getClan(player.getClanName());
            if (clan != null) {
                return clan.getCastleId() == castle_id;
            }
        }
        return false;
    }

    private boolean checkClanLeader(L1PcInstance player) {
        if (player.isCrown()) {
            L1Clan clan = L1World.getInstance().getClan(player.getClanName());
            if (clan != null) {
                return player.getId() == clan.getLeaderId();
            }
        }
        return false;
    }

    private int getNecessarySealCount(L1PcInstance pc) {
        int rulerCount = 0;
        int necessarySealCount = 10;

        if (pc.getInventory().checkItem(40917)) {
            rulerCount++;
        }
        if (pc.getInventory().checkItem(40920)) {
            rulerCount++;
        }
        if (pc.getInventory().checkItem(40918)) {
            rulerCount++;
        }
        if (pc.getInventory().checkItem(40919)) {
            rulerCount++;
        }
        if (rulerCount == 0) {
        } else if (rulerCount == 1) {
            necessarySealCount = 100;
        } else if (rulerCount == 2) {
            necessarySealCount = 200;
        } else if (rulerCount == 3) {
            necessarySealCount = 500;
        }
        return necessarySealCount;
    }

    private void createRuler(L1PcInstance pc, int attr, int sealCount) {
        int rulerId = 0;
        int protectionId = 0;
        int sealId = 0;
        if (attr == 1) {
            rulerId = 40917;
            protectionId = 40909;
            sealId = 40913;
        } else if (attr == 2) {
            rulerId = 40919;
            protectionId = 40911;
            sealId = 40915;
        } else if (attr == 4) {
            rulerId = 40918;
            protectionId = 40910;
            sealId = 40914;
        } else if (attr == 8) {
            rulerId = 40920;
            protectionId = 40912;
            sealId = 40916;
        }
        pc.getInventory().consumeItem(protectionId, 1);
        pc.getInventory().consumeItem(sealId, sealCount);
        L1ItemInstance item = pc.getInventory().storeItem(rulerId, 1);
        if (item != null) {
            pc.sendPackets(new S_ServerMessage(143, getTemplate().getName(), item.getLogName()));
        }
    }
}
