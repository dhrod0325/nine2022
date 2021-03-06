package ks.model.instance;

import ks.core.datatables.NPCTalkDataTable;
import ks.model.*;
import ks.model.action.xml.L1NpcHtml;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.system.timeDungeon.L1TimeDungeonData;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class L1TeleporterInstance extends L1NpcInstance {
    private static final Logger logger = LogManager.getLogger(L1TeleporterInstance.class.getName());
    private final boolean delay = false;

    public L1TeleporterInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        L1AttackRun attack = new L1AttackRun(player, this);
        attack.action();
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        if (player == null)
            return;

        int objId = getId();

        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        int npcId = getTemplate().getNpcId();
        L1Quest quest = player.getQuest();
        String htmlid = null;

        if (talking != null) {
            switch (npcId) {
                case 50001:
                    if (player.isElf()) {
                        htmlid = "barnia3";
                    } else if (player.isKnight() || player.isCrown()) {
                        htmlid = "barnia2";
                    } else if (player.isWizard() || player.isDarkElf()) {
                        htmlid = "barnia1";
                    }
                    break;
                case 50014:
                    if (player.isWizard()) {
                        if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1
                                && !player.getInventory().checkItem(40579)) {
                            htmlid = "dilong1";
                        } else {
                            htmlid = "dilong3";
                        }
                    }
                    break;
                case 50016:
                    if (player.getLevel() >= 13)
                        htmlid = "zeno2";
                    break;
                case 50031:
                    if (player.isElf()) { // ?????????
                        if (quest.getStep(L1Quest.QUEST_LEVEL45) == 2) {
                            if (!player.getInventory().checkItem(40602)) { // ?????? ??????
                                htmlid = "sepia1";
                            }
                        }
                    }
                    break;
                case 50020:
                case 50024:
                case 50036:
                case 50039: //????????? - ??????
                case 50044: //????????????-??????1
                case 50046: //???????????? - ??????2
                case 50051: //???????????? - ??????
                case 50054: //????????? - ????????????
                case 50066: //????????? - ??????
                    if (player.getLevel() < 45) {
                        htmlid = "starttel1";
                    } else if (player.getLevel() >= 45 && player.getLevel() <= 51) {
                        htmlid = "starttel2";
                    } else {
                        htmlid = "starttel3";
                    }
                    break;
                case 50043://?????????
                    if (quest.getStep(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
                        htmlid = "ramuda2";
                    } else if (quest.getStep(L1Quest.QUEST_LEVEL50) == 1) {
                        if (player.isCrown()) {
                            if (delay) {
                                htmlid = "ramuda4";
                            } else {
                                htmlid = "ramudap1";
                            }
                        } else { // ?????? ??????
                            htmlid = "ramuda1";
                        }
                    } else {
                        htmlid = "ramuda3";
                    }
                    break;
                case 50055://????????????
                    if (player.getLevel() >= 13)
                        htmlid = "drist1";
                    break;
                case 50069://??????
                    if (!player.isDarkElf())
                        htmlid = "enya2";
                    else if (player.getLevel() >= 13)
                        htmlid = "enya4";
                    break;
                case 50056: // ?????? - ??????
                    if (player.getLevel() < 45) {
                        htmlid = "telesilver4";
                    } else if (player.getLevel() >= 45 && player.getLevel() <= 99) {
                        htmlid = "telesilver4";
                    } else {
                        htmlid = "telesilver1";
                    }
                    break;

                case 70779://????????? ??????
                    if (player.getGfxId().getTempCharGfx() == 1037) { // ???????????? ??????
                        htmlid = "ants3";
                    } else if (player.getGfxId().getTempCharGfx() == 1039) {// ???????????? ?????????
                        // ??????
                        if (player.isCrown()) { // ??????
                            if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1) {
                                if (player.getInventory().checkItem(40547)) { // ????????????
                                    // ??????
                                    htmlid = "antsn";
                                } else {
                                    htmlid = "ants1";
                                }
                            } else { // Step1 ??????
                                htmlid = "antsn";
                            }
                        } else { // ?????? ??????
                            htmlid = "antsn";
                        }
                    }
                    break;
                case 70853://????????? ????????????
                    if (player.isElf()) { // ?????????
                        if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1) {
                            if (!player.getInventory().checkItem(40592)) { // ????????????
                                // ?????????
                                if (RandomUtils.nextInt(100) < 50) { // 50%??? ??????????????????
                                    htmlid = "fairyp2";
                                } else { // ?????? ????????? ?????? ??????
                                    htmlid = "fairyp1";
                                }
                            }
                        }
                    }
                    break;
            }

            if (htmlid != null) {
                player.sendPackets(new S_NPCTalkReturn(objId, htmlid));
            } else {
                if (player.getLawful() < -1000) {
                    player.sendPackets(new S_NPCTalkReturn(talking, objId, 2));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(talking, objId, 1));
                }
            }
        } else {
            logger.warn("No actions for npc id : " + npcId);
        }
    }

    @Override
    public void onFinalAction(L1PcInstance player, String action) {
        if (player == null)
            return;
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        if (action.equalsIgnoreCase("teleportURL")) {
            L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
            String[] price = null;

            int npcid = getTemplate().getNpcId();

            switch (npcid) {
                case 50015: // ???????????? ??????
                    price = new String[]{"1500"};
                    break;
                case 50017: // ????????? ??? ?????????
                    price = new String[]{"50"};
                    break;
                case 50020: // ?????? ?????????
                    price = new String[]{"50", "50", "50", "120", "120", "120",
                            "120", "180", "180", "200", "200", "600", "7100", "10000"};
                    break;
                case 50024: // ???????????? ?????????
                    price = new String[]{"75", "75", "75", "180", "180", "270",
                            "270", "270", "360", "360", "360", "300", "300", "750",
                            "10200", "10000"};
                    break;
                case 50026: // ????????? ??????????????? ??????, ?????? ??????, ????????? ??????
                    price = new String[]{"550", "700", "810"};
                    break;
                case 50033: // ?????? ?????????????????? ??????, ?????? ??????, ????????? ??????
                case 500033:
                    price = new String[]{"560", "720", "560"};
                    break;
                case 50035: // ????????? ????????? ??????
                    price = new String[]{"210", "210", "420", "210"};
                    break;
                case 50036: // ?????? ??????
                case 9426: // ?????? ??????
                    price = new String[]{"75", "75", "75", "180", "180", "180", "180", "270", "270", "450", "450", "450", "1050", "11100", "10000"};
                    break;
                case 50039: // ?????? ?????????
                    price = new String[]{"72", "72", "174", "174", "261", "261",
                            "261", "348", "348", "580", "580", "1160", "11165", "10000"};
                    break;
                case 50040: // ?????? ????????? ??????
                    price = new String[]{"210", "420", "210"};
                    break;
                case 50044: // ?????? ????????????
                case 50046: // ?????? ????????????
                    price = new String[]{"70", "168", "168", "252", "252", "252",
                            "336", "336", "420", "700", "700", "1260", "10360", "10000"};
                    break;
                case 50049: // ?????? ?????????????????? ??????, ?????? ??????, ?????? ????????? ?????? ??????
                    price = new String[]{"1150", "980", "590"};
                    break;
                case 50051: // ??????????????????
                    price = new String[]{"75", "180", "270", "270", "360", "360",
                            "360", "450", "450", "750", "750", "1350", "12000", "10000"};
                    break;
                case 50054: // ?????????????????????
                    price = new String[]{"75", "75", "180", "180", "180", "270",
                            "270", "360", "450", "300", "300", "750", "9750", "10000"};
                    break;
                case 50056: // ??????????????? ??????
                    price = new String[]{"75", "75", "75", "180", "180", "180",
                            "270", "270", "270", "360", "360", "450", "450",
                            "1050", "10200", "10000"};
                    break;
                case 50059: // ????????? ????????????????????????????????? ??????, ?????? ??????, ?????? ??????
                    price = new String[]{"580", "680", "680"};
                    break;
                case 50063: // ?????? ?????? ????????? ??????
                    price = new String[]{"210", "420", "210"};
                    break;
                case 50066: // ???????????????
                    price = new String[]{"990", "450", "400", "550", "400",
                            "710", "350", "680", "1000", "180", "180", "3200",
                            "6900", "10000"};
                    break;
                case 50068: // ????????????
                    price = new String[]{"1500", "800", "600", "1800", "1800",
                            "1000"};
                    break;
                case 50072: // ??????????????? ????????????
                    price = new String[]{"2200", "1800", "1000", "1600", "2200",
                            "1200", "1300", "2000", "2000"};
                    break;
                case 50073: // ??????????????? ????????????
                    price = new String[]{"380", "850", "290", "290", "290",
                            "180", "480", "150", "150", "380", "480", "380", "850",
                            "1000"};
                    break;
                case 50079: // ????????? ?????????
                    price = new String[]{"550", "550", "550", "600", "600",
                            "600", "650", "700", "750", "750", "500", "500", "700"};
                    break;
                case 4208002: // ???????????????

                    break;
                case 40108:
                    break; //??????
                case 40109:
                    price = new String[]{"7000", "7000", "7000", "14000", "14000"};
                    break; //??????

                case 4918000: // ???????????? ????????????
                    price = new String[]{"50", "50", "50", "50", "120", "120",
                            "180", "180", "180", "240", "240", "400", "400", "800",
                            "7700"};
                    break;
                case 4919000: // ???????????? ?????????
                    price = new String[]{"50", "50", "50", "120", "180", "180", "240", "240", "240", "300", "300", "500", "500", "900", "8000"};
                    break;
                case 6000014: // ????????? ??????
                    price = new String[]{"14000"};
                    break;
                case 6000016: // ?????? ?????????
                    price = new String[]{"1000"};
                    break;
                default:
                    price = new String[]{""};
                    break;
            }

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLA")) {
            String html = "";
            String[] price;
            int npcid = getTemplate().getNpcId();
            if (npcid == 50079) {//?????????
                html = "telediad3";
                price = new String[]{"700", "800", "800", "1000", "10000"};
            } else if (npcid == 4918000) {//???????????? ???????????? ????????????
                html = "dekabia3";
                price = new String[]{"100", "220", "220", "220", "330", "330", "330", "330", "440", "440"};
            } else if (npcid == 4919000) {//?????????
                html = "sharial3";
                price = new String[]{"220", "330", "330", "330", "440", "440", "550", "550", "550", "550"};
            } else {
                price = new String[]{""};
            }
            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLB")) {
            String html = "guide_1_1";
            String[] price = new String[]{"450", "450", "450", "450"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLC")) {
            String html = "guide_1_2";

            String[] price = new String[]{"465", "465", "465", "465", "1065", "1065"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLD")) {
            String html = "guide_1_3";
            String[] price = new String[]{"480", "480", "480", "480", "630", "1080", "630"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLE")) {
            String html = "guide_2_1";
            String[] price = new String[]{"600", "600", "750", "750"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLF")) {
            String html = "guide_2_2";
            String[] price = new String[]{"615", "615", "915", "765"};
            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLG")) {
            String html = "guide_2_3";

            String[] price = new String[]{"630", "780", "630", "1080", "930"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLH")) {
            String html = "guide_3_1";
            String[] price = new String[]{"750", "750", "750", "1200", "1050"};


            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLI")) {
            String html = "guide_3_2";
            String[] price = new String[]{"765", "765", "765", "765", "1515", "1215", "915"};


            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLJ")) {
            String html = "guide_3_3";

            String[] price = new String[]{"780", "780", "780", "780", "780", "1230", "1080"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLK")) {
            String html = "guide_4";
            String[] price = new String[]{"780", "780", "780", "780", "780", "1080", "1080", "1080"};

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLL")) {
            int npcid = getTemplate().getNpcId();
            String html;
            String[] price;

            if (npcid == 50056) { // ??????
                html = "guide_0_1";
                price = new String[]{"30", "30", "30", "70", "80", "90", "100"};
            } else {
                html = "guide_6"; //??????
                price = new String[]{"700", "700"};
            }
            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        } else if (action.equalsIgnoreCase("teleportURLM")) {
            int npcid = getTemplate().getNpcId();
            String html;

            String[] price = null;
            if (npcid == 50056) { // ??????
                html = "hp_storm1"; // ?????? ?????? ??????
            } else {
                html = "guide_7"; //??????
                price = new String[]{"500", "500", "500", "500", "500", "500", "500", "500", "500", "500", "500"};
            }

            player.sendPackets(new S_NPCTalkReturn(objid, html, price));
        }

        if (action.startsWith("teleport")) {
            doFinalAction(player, action);
        }
    }

    private void doFinalAction(L1PcInstance pc, String action) {
        if (pc == null)
            return;
        int objid = getId();

        int npcid = getTemplate().getNpcId();
        String htmlid = null;
        boolean isTeleport = true;

        if (npcid == 50014) { // ??? ???
            if (!pc.getInventory().checkItem(40581)) { // ??? ????????? ???
                isTeleport = false;
                htmlid = "dilongn";
            }
        } else if (npcid == 50043) { // Lambda
            if (delay) { // ???????????? ?????????
                isTeleport = false;
            }
        } else if (npcid == 50625) {
            if (delay) { // ???????????? ?????????
                isTeleport = false;
            }
        }

        if (isTeleport) {
            try {
                if (action.equalsIgnoreCase("teleport mutant-dungen_la")) {
                    // 3 ?????? ????????? Pc
                    for (L1PcInstance otherPc : L1World.getInstance().getVisiblePlayer(pc, 3)) {
                        if (otherPc == null)
                            continue;
                        if (otherPc.getClanId() != 0
                                && otherPc.getClanId() == pc.getClanId()
                                && otherPc.getId() != pc.getId()) {
                            L1Teleport.teleport(otherPc, 32740, 32800,
                                    (short) 217, 5, true);
                        }
                    }
                    L1Teleport.teleport(pc, 32740, 32800, (short) 217, 5, true);
                } else if (action.equalsIgnoreCase("teleport giranD")) {
                    L1TimeDungeonData data = pc.getTimeDungeon().getTimeDungeonData(53);

                    if (data != null) {
                        if (data.isTimeOver()) {
                            pc.sendPackets("?????? ??????????????? ?????????????????????");
                            return;
                        }
                    }

                    L1Teleport.teleport(pc, 32807, 32734, (short) 53, pc.getHeading(), true, 1);
                } else if (action.equalsIgnoreCase("teleport mage-quest-dungen_la")) {
                    L1Teleport.teleport(pc, 32791, 32788, (short) 201, 5, true);
                } else if (action.equalsIgnoreCase("teleport 29_la")) { // Lambda
                    L1PcInstance kni = null;
                    L1PcInstance elf = null;
                    L1PcInstance wiz = null;
                    L1Quest quest;

                    for (L1PcInstance otherPc : L1World.getInstance().getVisiblePlayer(pc, 3)) {
                        if (otherPc == null)
                            continue;
                        quest = otherPc.getQuest();
                        if (otherPc.isKnight()
                                && quest.getStep(L1Quest.QUEST_LEVEL50) == 1) {
                            if (kni == null) {
                                kni = otherPc;
                            }
                        } else if (otherPc.isElf()
                                && quest.getStep(L1Quest.QUEST_LEVEL50) == 1) {
                            if (elf == null) {
                                elf = otherPc;
                            }
                        } else if (otherPc.isWizard() // ?????????
                                && quest.getStep(L1Quest.QUEST_LEVEL50) == 1) {
                            if (wiz == null) {
                                wiz = otherPc;
                            }
                        }
                    }

                    if (kni != null && elf != null && wiz != null) {
                        L1Teleport.teleport(pc, 32723, 32850, (short) 2000, 2, true);
                        L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6, true);
                        L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6, true);
                        L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0, true);
                    }
                } else if (action.equalsIgnoreCase("teleport barlog_la")) { // ?????????(Lv50
                    L1Teleport.teleport(pc, 32755, 32844, (short) 2002, 5, true);
                }
            } catch (Exception e) {
                logger.error("??????", e);
            }
        }

        if (htmlid != null) { // ???????????? html??? ?????? ??????
            pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
        }
    }
}
