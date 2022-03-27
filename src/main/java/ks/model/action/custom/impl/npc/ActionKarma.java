package ks.model.action.custom.impl.npc;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.model.Broadcaster;
import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;

import static ks.constants.L1SkillId.STATUS_CURSE_BARLOG;
import static ks.constants.L1SkillId.STATUS_CURSE_YAHEE;

//TODO 오류체크
public class ActionKarma extends L1AbstractNpcAction {
    public ActionKarma(String action, L1PcInstance pc, L1Object obj, String param) {
        super(action, pc, obj, param);
    }

    @Override
    public void execute() {
        String htmlId = null;
        String[] htmldata = null;

        if (npcId == 80049) {
            if (action.equalsIgnoreCase("1")) {
                if (pc.getKarma() <= -10000000) {
                    pc.setKarma(1000000);
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlId = "betray13";
                }
            }
        } else if (npcId == 80050) {
            if (action.equalsIgnoreCase("1")) {
                htmlId = "meet105";
            } else if (action.equalsIgnoreCase("2")) {
                if (pc.getInventory().checkItem(40718)) {
                    htmlId = "meet106";
                } else {
                    htmlId = "meet110";
                }
            } else if (action.equalsIgnoreCase("a")) {
                if (pc.getInventory().consumeItem(40718, 1)) {
                    pc.addKarma(-100 * CodeConfig.RATE_KARMA);
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlId = "meet107";
                } else {
                    htmlId = "meet104";
                }
            } else if (action.equalsIgnoreCase("b")) {
                if (pc.getInventory().consumeItem(40718, 10)) {
                    pc.addKarma(-1000 * CodeConfig.RATE_KARMA);
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlId = "meet108";
                } else {
                    htmlId = "meet104";
                }
            } else if (action.equalsIgnoreCase("c")) {
                if (pc.getInventory().consumeItem(40718, 100)) {
                    pc.addKarma(-10000 * CodeConfig.RATE_KARMA);
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlId = "meet109";
                } else {
                    htmlId = "meet104";
                }
            } else if (action.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(40615)
                        || pc.getInventory().checkItem(40616)) {
                    htmlId = "";
                } else {
                    if (pc.getKarmaLevel() <= -1) {
                        L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
                    }
                }

            }

            pc.sendUhodo();// 우호도표기
        } else if (npcId == 80052) {
            if (action.equalsIgnoreCase("a")) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_YAHEE)) {
                    pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_CURSE_YAHEE);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_AURA, 2, 0));
                }

                pc.getSkillEffectTimerSet().setSkillEffect(STATUS_CURSE_BARLOG, 1020 * 1000); // 1020
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_AURA, 1, 1020));
                pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 750));
                pc.sendPackets(new S_ServerMessage(1127));
            }
        } else if (npcId == 80053) {
            if (action.equalsIgnoreCase("a")) {
                int aliceMaterialId = 0;
                int[] material = null;
                int[] count = null;
                int createItem = 0;

                String successHtmlId = null;
                int[] aliceMaterialIdList = {40991, 196, 197, 198, 199, 200, 201, 202};
                int[][] materialsList = {{40995, 40718, 40991},
                        {40997, 40718, 196}, {40990, 40718, 197},
                        {40994, 40718, 198}, {40993, 40718, 199},
                        {40998, 40718, 200}, {40996, 40718, 201},
                        {40992, 40718, 202}};

                int[][] countList = {{100, 100, 1}, {100, 100, 1},
                        {100, 100, 1}, {50, 100, 1}, {50, 100, 1},
                        {50, 100, 1}, {10, 100, 1}, {10, 100, 1}};

                int[] createItemList = {196, 197, 198, 199, 200, 201, 202, 203};
                String[] successHtmlIdList = {"alice_1", "alice_2", "alice_3", "alice_4", "alice_5", "alice_6", "alice_7", "alice_8"};
                for (int i = 0; i < aliceMaterialIdList.length; i++) {
                    if (pc.getInventory().checkItem(aliceMaterialIdList[i])) {
                        aliceMaterialId = aliceMaterialIdList[i];
                        material = materialsList[i];
                        count = countList[i];
                        createItem = createItemList[i];
                        successHtmlId = successHtmlIdList[i];
                        break;
                    }
                }
                if (aliceMaterialId == 0) {
                    htmlId = "alice_no";
                } else {
                    createItem(new int[]{createItem}, new int[]{1}, material, count, successHtmlId, "alice_no", null);
                    return;
                }
            }
        } else if (npcId == 80055) {
            htmlId = getYaheeAmulet(pc, npc, action);
        } else if (npcId == 80056) {
            if (pc.getKarma() <= -10000000) {
                getBloodCrystalByKarma(pc, npc, action);
            }
            htmlId = "";
        } else if (npcId == 80063) {
            if (action.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(40921)) {
                    L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
                } else {
                    htmlId = "gpass02";
                }
            }
        } else if (npcId == 80064) {
            if (action.equalsIgnoreCase("1")) {
                htmlId = "meet005";
            } else if (action.equalsIgnoreCase("2")) {
                if (pc.getInventory().checkItem(40678)) {
                    htmlId = "meet006";
                } else {
                    htmlId = "meet010";
                }
            } else if (action.equalsIgnoreCase("a")) {
                if (pc.getInventory().consumeItem(40678, 1)) {
                    pc.addKarma(100 * CodeConfig.RATE_KARMA);
                    pc.sendPackets(new S_ServerMessage(1078));

                    htmlId = "meet007";
                } else {
                    htmlId = "meet004";
                }
            } else if (action.equalsIgnoreCase("b")) {
                if (pc.getInventory().consumeItem(40678, 10)) {
                    pc.addKarma(1000 * CodeConfig.RATE_KARMA);
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlId = "meet008";
                } else {
                    htmlId = "meet004";
                }
            } else if (action.equalsIgnoreCase("c")) {
                if (pc.getInventory().consumeItem(40678, 100)) {
                    pc.addKarma(10000 * CodeConfig.RATE_KARMA);
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlId = "meet009";
                } else {
                    htmlId = "meet004";
                }
            } else if (action.equalsIgnoreCase("d")) {
                if (pc.getKarmaLevel() >= 1) {
                    L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
                }
            }
            pc.sendUhodo();// 우호도표기
        } else if (npcId == 80066) {
            if (action.equalsIgnoreCase("1")) {
                if (pc.getKarma() >= 10000000) {
                    pc.setKarma(-1000000);
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlId = "betray03";
                }
            }
        } else if (npcId == 80071) {
            htmlId = getBarlogEarring(pc, npc, action);
        } else if (npcId == 80073) {
            if (action.equalsIgnoreCase("a")) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CURSE_BARLOG)) {
                    pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_CURSE_BARLOG);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_AURA, 1, 0));
                }

                pc.getSkillEffectTimerSet().setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_AURA, 2, 1020));
                pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 750));
                pc.sendPackets(new S_ServerMessage(1127));
            }
        } else if (npcId == 80072) {
            String sEquals = null;
            int karmaLevel = 0;
            int[] material = null;
            int[] count = null;
            int createItem = 0;
            String failureHtmlId = null;

            String[] sEqualsList = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "a", "b", "c", "d", "e", "f", "g", "h"};
            String[] htmlIdList = {"lsmitha", "lsmithb", "lsmithc", "lsmithd", "lsmithe", "", "lsmithf", "lsmithg", "lsmithh"};
            int[] karmaLevelList = {1, 2, 3, 4, 5, 6, 7, 8};
            int[][] materialsList = {{20158, 40669, 40678},
                    {20144, 40672, 40678}, {20075, 40671, 40678},
                    {20183, 40674, 40678}, {20190, 40674, 40678},
                    {20078, 40674, 40678}, {20078, 40670, 40678},
                    {40719, 40673, 40678}};
            int[][] countList = {{1, 50, 100}, {1, 50, 100},
                    {1, 50, 100}, {1, 20, 100}, {1, 40, 100},
                    {1, 5, 100}, {1, 1, 100}, {1, 1, 100}};
            int[] createItemList = {20083, 20131, 20069, 20179, 20209, 20290,
                    20261, 20031};

            String[] failureHtmlIdList = {"lsmithaa", "lsmithbb", "lsmithcc", "lsmithdd", "lsmithee", "lsmithff", "lsmithgg", "lsmithhh"};

            for (int i = 0; i < sEqualsList.length; i++) {
                if (action.equalsIgnoreCase(sEqualsList[i])) {
                    sEquals = sEqualsList[i];
                    if (i <= 8) {
                        htmlId = htmlIdList[i];
                    } else {
                        karmaLevel = karmaLevelList[i - 9];
                        material = materialsList[i - 9];
                        count = countList[i - 9];
                        createItem = createItemList[i - 9];
                        failureHtmlId = failureHtmlIdList[i - 9];
                    }
                    break;
                }
            }

            if (action.equalsIgnoreCase(sEquals)) {
                if (karmaLevel != 0 && (pc.getKarmaLevel() >= karmaLevel)) {
                    createItem(new int[]{createItem}, new int[]{1}, material, count, "", failureHtmlId, null);
                }
            }
        } else if (npcId == 80074) {
            if (pc.getKarma() >= 10000000) {
                getSoulCrystalByKarma(pc, npc, action);
            }

            htmlId = "";
        } else if (npcId == 80057) {
            htmlId = karmaLevelToHtmlId(pc.getKarmaLevel());
            htmldata = new String[]{String.valueOf(pc.getKarmaPercent())};
        } else if (npcId == 80059
                || npcId == 80060
                || npcId == 80061
                || npcId == 80062) {
            htmlId = talkToDimensionDoor(pc, (L1NpcInstance) obj, action);
        }

        if (htmlId != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, htmlId, htmldata));
        }
    }

    public String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
        int npcId = npc.getNpcId();

        String htmlid = "";
        int protectionId = 0;
        int sealId = 0;
        int locX = 0;
        int locY = 0;
        short mapId = 0;

        if (npcId == 80059) {
            protectionId = 40909;
            sealId = 40913;
            locX = 32773;
            locY = 32835;
            mapId = 607;
        } else if (npcId == 80060) {
            protectionId = 40912;
            sealId = 40916;
            locX = 32757;
            locY = 32842;
            mapId = 606;
        } else if (npcId == 80061) {
            protectionId = 40910;
            sealId = 40914;
            locX = 32830;
            locY = 32822;
            mapId = 604;
        } else if (npcId == 80062) {
            protectionId = 40911;
            sealId = 40915;
            locX = 32835;
            locY = 32822;
            mapId = 605;
        }

        if (s.equalsIgnoreCase("a")) {
            L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
            htmlid = "";
        } else if (s.equalsIgnoreCase("b")) {
            L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate()
                        .getName(), item.getLogName()));
            }
            htmlid = "";
        } else if (s.equalsIgnoreCase("c")) {
            htmlid = "wpass07";
        } else if (s.equalsIgnoreCase("d")) {
            if (pc.getInventory().checkItem(sealId)) {
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
        } else if (s.equalsIgnoreCase("e")) {
            htmlid = "";
        } else if (s.equalsIgnoreCase("f")) {
            if (pc.getInventory().checkItem(protectionId)) {
                pc.getInventory().consumeItem(protectionId, 1);
            }
            if (pc.getInventory().checkItem(sealId)) {
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
            htmlid = "";
        }
        return htmlid;
    }

    public String karmaLevelToHtmlId(int level) {
        if (level == 0 || level < -7 || 7 < level) {
            return "";
        }
        String htmlid;
        if (0 < level) {
            htmlid = "vbk" + level;
        } else {
            htmlid = "vyk" + Math.abs(level);
        }
        return htmlid;
    }

    public void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc, String s) {
        if (s.equalsIgnoreCase("1")) {
            pc.addKarma(-500 * CodeConfig.RATE_KARMA);
            L1ItemInstance item = pc.getInventory().storeItem(40678, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        } else if (s.equalsIgnoreCase("2")) {
            pc.addKarma(-5000 * CodeConfig.RATE_KARMA);
            L1ItemInstance item = pc.getInventory().storeItem(40678, 10);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        } else if (s.equalsIgnoreCase("3")) {
            pc.addKarma(-50000 * CodeConfig.RATE_KARMA);
            L1ItemInstance item = pc.getInventory().storeItem(40678, 100);

            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        }
    }

    public void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc, String s) {
        if (s.equalsIgnoreCase("1")) {
            pc.addKarma(500 * CodeConfig.RATE_KARMA);
            L1ItemInstance item = pc.getInventory().storeItem(40718, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        } else if (s.equalsIgnoreCase("2")) {
            pc.addKarma(5000 * CodeConfig.RATE_KARMA);
            L1ItemInstance item = pc.getInventory().storeItem(40718, 10);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        } else if (s.equalsIgnoreCase("3")) {
            pc.addKarma(50000 * CodeConfig.RATE_KARMA);
            L1ItemInstance item = pc.getInventory().storeItem(40718, 100);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        }
    }

    public String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String s) {
        int[] amuletIdList = {20358, 20359, 20360, 20361, 20362, 20363, 20364, 20365};
        int amuletId = 0;
        int karmalevel = pc.getKarmaLevel();
        int bug = 0;

        L1ItemInstance item;
        String htmlid = null;
        if (s.equalsIgnoreCase("1")) {
            bug = -1;
            amuletId = amuletIdList[0];
        } else if (s.equalsIgnoreCase("2")) {
            bug = -2;
            amuletId = amuletIdList[1];
        } else if (s.equalsIgnoreCase("3")) {
            bug = -3;
            amuletId = amuletIdList[2];
        } else if (s.equalsIgnoreCase("4")) {
            bug = -4;
            amuletId = amuletIdList[3];
        } else if (s.equalsIgnoreCase("5")) {
            bug = -5;
            amuletId = amuletIdList[4];
        } else if (s.equalsIgnoreCase("6")) {
            bug = -6;
            amuletId = amuletIdList[5];
        } else if (s.equalsIgnoreCase("7")) {
            bug = -7;
            amuletId = amuletIdList[6];
        } else if (s.equalsIgnoreCase("8")) {
            bug = -8;
            amuletId = amuletIdList[7];
        }
        if (amuletId != 0) {
            if (karmalevel == bug) {
                item = pc.getInventory().storeItem(amuletId, 1);
                if (item != null) {
                    pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("현재 우호도가 부족합니다."));
            }
            for (int id : amuletIdList) {
                if (id == amuletId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    public String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String s) {
        int[] earringIdList = {21020, 21021, 21022, 21023, 21024, 21025, 21026, 21027};
        int earringId = 0;
        int karmalevel = pc.getKarmaLevel();
        int bug = 0;

        String htmlid = null;

        if (s.equalsIgnoreCase("1")) {
            earringId = earringIdList[0];
            bug = 1;
        } else if (s.equalsIgnoreCase("2")) {
            earringId = earringIdList[1];
            bug = 2;
        } else if (s.equalsIgnoreCase("3")) {
            earringId = earringIdList[2];
            bug = 3;
        } else if (s.equalsIgnoreCase("4")) {
            earringId = earringIdList[3];
            bug = 4;
        } else if (s.equalsIgnoreCase("5")) {
            earringId = earringIdList[4];
            bug = 5;
        } else if (s.equalsIgnoreCase("6")) {
            earringId = earringIdList[5];
            bug = 6;
        } else if (s.equalsIgnoreCase("7")) {
            earringId = earringIdList[6];
            bug = 7;
        } else if (s.equalsIgnoreCase("8")) {
            earringId = earringIdList[7];
            bug = 8;
        }

        if (earringId != 0) {
            if (karmalevel == bug) {
                L1ItemInstance item = pc.getInventory().storeItem(earringId, 1);
                if (item != null) {
                    pc.sendPackets(new S_ServerMessage(143, npc.getTemplate().getName(), item.getLogName()));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("현재 우호도가 부족합니다."));
            }

            for (int id : earringIdList) {
                if (id == earringId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }
}
