package ks.packets.clientpackets;

import ks.app.LineageAppContext;
import ks.app.event.NpcActionEvent;
import ks.commands.common.CommonCommands;
import ks.commands.gm.command.executor.L1Describe2;
import ks.constants.L1DataMapKey;
import ks.constants.L1ItemId;
import ks.core.datatables.MapsTable;
import ks.core.datatables.NpcActionTable;
import ks.core.datatables.PolyTable;
import ks.core.datatables.SpawnTable;
import ks.core.datatables.huktBook.HuntBook;
import ks.core.datatables.huktBook.HuntBookTable;
import ks.core.datatables.npc_making.NpcMakingManager;
import ks.core.datatables.polyCard.action.L1PolyActionUtils;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.action.custom.L1ActionExecutor;
import ks.model.action.custom.L1ActionFactory;
import ks.model.action.xml.L1NpcAction;
import ks.model.action.xml.L1NpcHtml;
import ks.model.instance.L1NpcInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.model.rank.L1Rank;
import ks.model.rank.L1RankChecker;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.system.boss.model.L1Boss;
import ks.system.boss.table.L1BossSpawnListHotTable;
import ks.system.lastabard.LastabardSpawnTable;
import ks.system.portalsystem.L1PortalSystem;
import ks.system.portalsystem.L1PortalSystemRunner;
import ks.system.userShop.L1UserShopManager;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.system.userShop.table.L1UserShop;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static ks.constants.L1SkillId.EARTH_BIND;
import static ks.core.datatables.polyCard.action.L1PolyActionUtils.usePolyScroll;
import static ks.model.skill.utils.L1SkillUtils.ICE_SKILLS;
import static ks.model.skill.utils.L1SkillUtils.STUN_SKILLS;

public class C_NPCAction extends ClientBasePacket {
    public C_NPCAction(byte[] data, L1Client client) {
        super(data);

        int objId = readD();
        String action = readS();

        if (action.equalsIgnoreCase("deadTrans")
                || action.equalsIgnoreCase("pvpSet")
                || action.equalsIgnoreCase("ShowHPMPRecovery")
                || action.equalsIgnoreCase("showDisableEffectIcon")
                || action.equalsIgnoreCase("showDungeonTimeLimit")) {
            return;
        }

        String param = null;

        if (action.equalsIgnoreCase("select")
                || action.equalsIgnoreCase("map")
                || action.equalsIgnoreCase("apply")) {
            param = readS();
        } else if (action.equalsIgnoreCase("ent")) {
            L1Object obj = L1World.getInstance().findObject(objId);

            if (obj instanceof L1NpcInstance) {
                if (((L1NpcInstance) obj).getNpcId() == 80088) {
                    param = readS();
                }
            }
        }

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1Object obj = L1World.getInstance().findObject(objId);

        L1LogUtils.gmLog(pc, "objId : {}, Action : {}, obj : {}", objId, action, obj);

        LineageAppContext.getCtx().publishEvent(new NpcActionEvent(new NpcActionEvent.NpcActionEventSource(action, pc, obj, param)));

        if (obj != null) {
            if (obj instanceof L1PcInstance) {
                logger.debug("pc action");
                pcAction(pc, (L1PcInstance) obj, action, param);
            } else if (obj instanceof L1NpcInstance) {
                logger.debug("npc action");
                npcAction(pc, (L1NpcInstance) obj, action, param);
            }
        }
    }

    private void pcAction(L1PcInstance pc, L1PcInstance target, String action, String param) {
        if (action.matches("[0-9]+")) {
            if (target.isSummonMonster()) {
                summonMonster(target, action);
                target.setSummonMonster(false);
            }
        }

        if (actionRank(pc, action)) {
            return;
        }

        if (L1PolyActionUtils.action(pc, action)) {
            return;
        }

        if ("cc_shop_sell".equalsIgnoreCase(action)) {
            CommonCommands.getInstance().handleCommands(pc, "상점 판매");
            return;
        } else if (action.startsWith("cc_shop_buy_")) {
            String act = action.replace("cc_shop_buy_", "");

            if (act.startsWith("type")) {
                int type = Integer.parseInt(act.replace("type", ""));

                if (type == 1) {
                    pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_shopbuy2"));
                } else if (type == 2) {
                    pc.sendPackets("잡화매입명령어는 .상점 매입 아이템명 입니다");
                }
            } else if (act.startsWith("enchant")) {
                int enchant = Integer.parseInt(act.replace("enchant", ""));

                pc.getDataMap().put(L1DataMapKey.SHOP_BUY_ENCHANT, enchant + "");
                pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_shopbuy3"));
                pc.sendPackets("매입 인챈트 : + " + enchant);
            } else if (act.startsWith("bless")) {
                int bless = Integer.parseInt(act.replace("bless", ""));

                pc.getDataMap().put(L1DataMapKey.SHOP_BUY_BLESS, bless + "");
                pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_shopbuy4"));

                String type = "";

                if (bless == 1) {
                    type = "일반";
                } else if (bless == 0) {
                    type = "축복";
                } else if (bless == 2) {
                    type = "저주";
                }

                pc.sendPackets("매입 유형 : + " + type);
            } else if (act.startsWith("attr")) {
                int attr = Integer.parseInt(act.replace("attr", ""));
                pc.getDataMap().put(L1DataMapKey.SHOP_BUY_ATTR, attr + "");

                String msg = ".상점 매입 아이템명 을 입력하세요";

                pc.sendPackets("매입 속성 : + " + L1CommonUtils.getAttrNameKr(attr));
                pc.sendPackets(new S_CloseList(pc.getId()));
                pc.sendPackets(new S_ChatPacket(pc, msg, L1Opcodes.S_OPCODE_NORMALCHAT, 2));
                pc.sendPackets(msg);
            }
        } else if ("cc_shop_buy1".equalsIgnoreCase(action)) {
            pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ENCHANT);
            pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ATTR);
            pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_BLESS);

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_shopbuy1"));
        } else if ("cc_shop_status".equalsIgnoreCase(action)) {
            CommonCommands.getInstance().handleCommands(pc, "상점 상태");
            return;
        } else if ("cc_shop_withdraw".equalsIgnoreCase(action)) {
            CommonCommands.getInstance().handleCommands(pc, "상점 출금");
            return;
        } else if ("cc_shop_collect".equalsIgnoreCase(action)) {
            CommonCommands.getInstance().handleCommands(pc, "상점 회수");
            return;
        } else if ("cc_shop_end".equalsIgnoreCase(action)) {
            CommonCommands.getInstance().handleCommands(pc, "상점 종료");
            return;
        }

        if (action.startsWith("cc_moninfo")) {
            int idx = Integer.parseInt(action.replace("cc_moninfo", ""));

            L1Drop drop = pc.getItemDropSearchList().get(idx - 1);

            List<String> params = new ArrayList<>();
            params.add(drop.getMobName());

            List<L1Spawn> list = SpawnTable.getInstance().findSpawnList(drop.getMobId());
            list.addAll(LastabardSpawnTable.getInstance().findSpawnList(drop.getMobId()));

            for (L1Spawn spawn : list) {
                String mapName = MapsTable.getInstance().getMapName(spawn.getMapId());

                if (!params.contains(mapName))
                    params.add(mapName);
            }

            L1Boss bs = L1BossSpawnListHotTable.getInstance().findByNpcId(drop.getMobId());

            if (bs != null) {
                String mapName = MapsTable.getInstance().getMapName(bs.getSpawnMap());

                if (!StringUtils.isEmpty(mapName))
                    params.add(mapName);
            }

            for (int start = list.size() - 1; start < 10; start++) {
                params.add(" ");
            }

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_moninfo", params.toArray()));

            return;
        }

        if (action.startsWith("cc_ntradeprev")) {
            pc.getPagination().prev();
            pc.getSearchShopItem().showHtml();
            return;
        } else if (action.startsWith("cc_ntradenext")) {
            pc.getPagination().next();
            pc.getSearchShopItem().showHtml();

            return;
        } else if (action.startsWith("ntraidx")) {
            int idx = Integer.parseInt(action.replace("ntraidx", ""));

            if (pc.getPagination().getSearchList().size() < idx) {
                return;
            }

            if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                pc.sendPackets("시장 안에서만 이동이 가능합니다");
                return;
            }

            L1UserShop o = (L1UserShop) pc.getPagination().getSearchList().get(idx - 1);

            if (o == null) {
                return;
            }

            L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(o.getCharId());

            if (shop != null) {
                L1Teleport.teleport(pc, shop.getX(), shop.getY(), shop.getMapId(), shop.getHeading(), true);
            }

            pc.sendPackets(new S_CloseList(pc.getId()));

            return;
        } else if (action.startsWith("ntrapidx")) {
            int idx = Integer.parseInt(action.replace("ntrapidx", ""));

            if (pc.getSearchShopItem().getBuyItems().size() < idx) {
                return;
            }

            if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                pc.sendPackets("시장 안에서만 이동이 가능합니다");
                return;
            }

            L1UserShop o = pc.getSearchShopItem().getBuyItems().get(idx - 1);

            if (o == null) {
                return;
            }

            L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(o.getCharId());

            if (shop != null) {
                L1Teleport.teleport(pc, shop.getX(), shop.getY(), shop.getMapId(), shop.getHeading(), true);
            }

            pc.sendPackets(new S_CloseList(pc.getId()));

            return;
        }

        if (action.startsWith("cc_dchange")) {
            String act = action.replace("cc_dchange_", "");
            int actNum = Integer.parseInt(act);

            int newItemId = 0;

            switch (actNum) {
                case 1:
                    newItemId = 420112;
                    break;
                case 2:
                    newItemId = 420114;
                    break;
                case 3:
                    newItemId = 420115;
                    break;
                case 4:
                    newItemId = 420113;
                    break;
                case 5:
                    newItemId = 420104;
                    break;
                case 6:
                    newItemId = 420106;
                    break;
                case 7:
                    newItemId = 420107;
                    break;
                case 8:
                    newItemId = 420105;
                    break;
                case 9:
                    newItemId = 420100;
                    break;
                case 10:
                    newItemId = 420102;
                    break;
                case 11:
                    newItemId = 420103;
                    break;
                case 12:
                    newItemId = 420101;
                    break;
                case 13:
                    newItemId = 420108;
                    break;
                case 14:
                    newItemId = 420110;
                    break;
                case 15:
                    newItemId = 420111;
                    break;
                case 16:
                    newItemId = 420109;
                    break;
            }

            pc.getDragonArmorChange().changeItem(newItemId);

            return;
        } else if (action.startsWith("cc_hunt_")) {
            if (!pc.getInventory().checkItem(L1ItemId.HUNT_BOOK)) {
                pc.sendPackets("사냥터 이동기억책을 소지해야 합니다");

                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(ICE_SKILLS)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(STUN_SKILLS)) {
                return;
            }

            String act = action.replace("cc_hunt_", "");
            int actNum = Integer.parseInt(act);
            HuntBook hb = HuntBookTable.getInstance().find(actNum);

            if (hb != null) {
                if (pc.isEscapable()) {
                    if (pc.isHuntMapAndNoHunt(hb.getMapId())) {
                        return;
                    }

                    L1Teleport.teleport(pc, hb.getX(), hb.getY(), hb.getMapId(), pc.getHeading(), true, hb.getRandom());

                    pc.getInventory().removeItemById(L1ItemId.HUNT_BOOK, 1);
                    pc.sendPackets(new S_CloseList(pc.getId()));
                } else {
                    pc.sendPackets(new S_ServerMessage(647));
                }
            }
        } else if (action.startsWith("cc_supp_")) {
            String act = action.replace("cc_supp_", "");

            if ("desc".equalsIgnoreCase(act)) {
                L1Describe2.getInstance().execute(pc, "", pc.getName());
            } else if ("settingPotion".equalsIgnoreCase(act)) {
                L1CommonUtils.guidePotion(pc);
            } else if ("toggleA".equalsIgnoreCase(act)) {
                if (pc.isAutoDragonDiamond()) {
                    CommonCommands.getInstance().handleCommands(pc, "드다 끔");
                } else {
                    CommonCommands.getInstance().handleCommands(pc, "드다 켬");
                }

                L1CommonUtils.guidePotion(pc);
            } else if ("toggleB".equalsIgnoreCase(act)) {
                if (pc.isAutoDragonPerl()) {
                    CommonCommands.getInstance().handleCommands(pc, "진주 끔");
                } else {
                    CommonCommands.getInstance().handleCommands(pc, "진주 켬");
                }

                L1CommonUtils.guidePotion(pc);
            } else if ("toggleC".equalsIgnoreCase(act)) {
                if (pc.isMarkShow()) {
                    CommonCommands.getInstance().handleCommands(pc, "마크 끔");
                } else {
                    CommonCommands.getInstance().handleCommands(pc, "마크 켬");
                }

                L1CommonUtils.guidePotion(pc);
            } else if ("toggleD".equalsIgnoreCase(act)) {
                CommonCommands.getInstance().handleCommands(pc, "후원");
            } else if (act.startsWith("potion")) {
                String[] str = act.replace("potion", "").split("_");
                int potionNum = Integer.parseInt(str[0]);
                int percent = Integer.parseInt(str[1]);

                CommonCommands.getInstance().handleCommands(pc, pc.getAutoPotion().getAutoPotionNumString(potionNum) + " " + percent);

                L1CommonUtils.guidePotion(pc);
            }

            return;
        }

        if (target.isShapeChange()) {
            L1PolyMorph.handleCommands(target, action);
            target.setShapeChange(false);
        } else if (target.isDesShapeChange()) {
            int time = 1200;
            L1PolyMorph.doPolyByItemMagic(target, action, time);
            target.setDesShapeChange(false);
        } else if (target.isArchShapeChange()) {
            int time;

            if (target.isArchPolyType()) {
                time = 1200;
            } else {
                time = -1;
            }

            L1PolyMorph.doPolyByItemMagic(target, action, time);
            target.setArchShapeChange(false);
        } else {
            L1PolyMorph poly = PolyTable.getInstance().getTemplate(action);

            if (poly != null || action.equals("none")) {
                Object polyItem = pc.getEtcMap().get("polyItem");

                int useItem = 0;

                if (polyItem != null) {
                    useItem = Integer.parseInt(pc.getEtcMap().get("polyItem").toString());
                }

                if (useItem != 0) {
                    if (target.getInventory().checkItem(useItem)) {
                        usePolyScroll(target, useItem, action);
                    }
                } else {
                    if (target.getInventory().checkItem(40088)) {
                        usePolyScroll(target, 40088, action);
                    } else if (target.getInventory().checkItem(40096)) {
                        usePolyScroll(target, 40096, action);
                    } else if (target.getInventory().checkItem(140088)) {
                        usePolyScroll(target, 140088, action);
                    } else if (target.getInventory().checkItem(60001213)) {
                        usePolyScroll(target, 60001213, action);
                    }
                }

                pc.getEtcMap().remove("polyItem");
            }
        }
    }

    private void npcAction(L1PcInstance pc, L1NpcInstance npc, String action, String param) {
        int diffX = Math.abs(pc.getX() - npc.getX());
        int diffY = Math.abs(pc.getY() - npc.getY());

        if (diffX > 12 || diffY > 12) {
            return;
        }

        if (actionRank(pc, action)) {
            return;
        }

        if (action.startsWith("cc_doll_link")) {
            int step = Integer.parseInt(action.replace("cc_doll_link", ""));
            pc.getDollCombine().startCombine(step);

            return;
        } else if (action.startsWith("cc_craft")) {
            if (NpcMakingManager.getInstance().npcAction(pc, npc, action)) {
                return;
            }
        } else if (action.startsWith("cc_rotation")) {
            int idx = Integer.parseInt(action.replace("cc_rotation", ""));

            L1PortalSystem system = L1PortalSystemRunner.getInstance().findByNpcId(npc.getNpcId());

            if (system != null) {
                system.teleportToStartLocation(pc, idx);
            }
        } else if ("cc_orim".equalsIgnoreCase(action)) {
            if (!pc.getInventory().checkItem(60001227, 3)) {
                pc.sendPackets("오림의장신구마법주문서 3장을 소지하고 있어야합니다");
                return;
            }

            if (RandomUtils.isWinning(100, 20)) {
                pc.getInventory().removeItemById(60001227, 3);
                pc.getInventory().storeItem(60001226, 1);
                pc.sendPackets("성공 : 축복받은 오림의 장신구 마법주문서 획득");
            } else {
                pc.getInventory().removeItemById(60001227, 2);
                pc.sendPackets("실패 : 오림의 장신구 마법주문서 합성");
            }
        }

        npc.onFinalAction(pc, action);

        L1NpcAction npcAction = NpcActionTable.getInstance().get(action, pc, npc);
        if (npcAction != null) {
            L1NpcHtml result = npcAction.execute(action, pc, npc, readByte());

            if (result != null) {
                logger.debug("action from npcAction : {}", npcAction);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
            }

            return;
        }

        L1ActionExecutor executor = L1ActionFactory.create(action, pc, npc, param);

        if (executor != null) {
            executor.execute();
            logger.debug("action from executor : {}", executor);
            return;
        }

        if (action.equalsIgnoreCase("room")) {
            L1Teleport.teleport(pc, 32740, 32804, (short) 18432, 0, true);
        } else if (action.equalsIgnoreCase("hall")) {
            L1Teleport.teleport(pc, 32740, 32804, (short) 18432, 0, true);
        } else if (action.equalsIgnoreCase("return")) {
            L1Teleport.teleport(pc, 32740, 32804, (short) 18432, 0, true);
        }
    }

    private boolean actionRank(L1PcInstance pc, String action) {
        if (action.startsWith("cc_rank_")) {
            int type = Integer.parseInt(action.replace("cc_rank_", ""));

            List<String> params = new ArrayList<>();
            List<L1Rank> rankList = new ArrayList<>();

            if (type == 10) {
                params.add("전체");
                rankList.addAll(L1RankChecker.getInstance().getAllRankerList());
            } else {
                params.add(L1CommonUtils.getClassNameByType(type));
                rankList.addAll(L1RankChecker.getInstance().getClassRankList(type));
            }

            for (int i = 0; i < 10; i++) {
                if (rankList.size() > i) {
                    L1Rank rank = rankList.get(i);
                    params.add(String.format("[%s] %s", L1CommonUtils.getClassNameByType(rank.getType()), rank.getCharName()));
                } else {
                    params.add("없음");
                }
            }

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_rank1", params));

            return true;
        }

        return false;
    }

    public void summonMonster(L1PcInstance pc, String s) {
        String[] summonStrList = new String[]{"7", "263", "519", "8", "264", "520",
                "9", "265", "521", "10", "266", "522", "11", "267", "523",
                "12", "268", "524", "13", "269", "525", "14", "270", "526",
                "15", "271", "527", "16", "17", "18", "274"};

        int[] summonIdList = new int[]{81210, 81211, 81212, 81213, 81214, 81215,
                81216, 81217, 81218, 81219, 81220, 81221, 81222, 81223, 81224,
                81225, 81226, 81227, 81228, 81229, 81230, 81231, 81232, 81233,
                81234, 81235, 81236, 81237, 81238, 81239, 81240};

        int[] summonLvlList = new int[]{28, 28, 28, 32, 32, 32, 36, 36, 36, 40, 40, 40, 44, 44, 44, 48, 48, 48, 52, 52, 52, 56, 56, 56, 60, 60, 60, 64, 68, 72, 72};
        int[] summonChaList = new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 14, 42, 42, 50};

        int summonId = 0;
        int levelRange = 0;
        int summonCost = 0;

        for (int loop = 0; loop < summonStrList.length; loop++) {
            if (s.equalsIgnoreCase(summonStrList[loop])) {
                summonId = summonIdList[loop];
                levelRange = summonLvlList[loop];
                summonCost = summonChaList[loop];
                break;
            }
        }

        L1SkillUtils.summon(pc, levelRange, summonId, summonCost);
    }
}
