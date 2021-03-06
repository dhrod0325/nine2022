package ks.commands.common;

import bill.BillCommand;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.constants.L1DataMapKey;
import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.CastleTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.clan.ClanTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.next_items.CharacterNextReturnUtils;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.datatables.slotSave.SlotSaveTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.item.characterTrade.CharacterTradeDao;
import ks.model.item.characterTrade.CharacterTradeInfo;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.scheduler.WarTimeScheduler;
import ks.system.adenBoard.AdenBankAccountController;
import ks.system.adenBoard.model.AdenBankAccount;
import ks.system.adenBoard.packet.S_AdenBoard;
import ks.system.autoHunt.L1AutoHuntRandomMoveAi;
import ks.system.autoHunt.scheduler.L1AutoHuntScheduler;
import ks.system.timeDungeon.L1TimeDungeonData;
import ks.system.userShop.L1UserShopCreateHandler;
import ks.system.userShop.L1UserShopManager;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.system.userShop.table.L1UserShop;
import ks.system.userShop.table.L1UserShopTable;
import ks.util.L1BadNameUtils;
import ks.util.L1CommonUtils;
import ks.util.L1WarUtils;
import ks.util.common.NumberUtils;
import ks.util.common.SqlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ks.constants.L1SkillId.*;

public class CommonCommands {
    private final Logger logger = LogManager.getLogger();

    private final static CommonCommands instance = new CommonCommands();

    public static CommonCommands getInstance() {
        return instance;
    }

    private static int inventorySortNum(L1ItemInstance itemInstance) {
        int rate = 100000;
        int result = 0;

        int itemId = itemInstance.getItemId();

        if (itemInstance.getItem().isWeapon()) {
            result += 70 * rate - itemInstance.getItem().getType();

            if (itemInstance.isEquipped()) {
                result += 90 * rate;
            }
        } else if (itemInstance.getItem().isArmor()) {
            if (!itemInstance.getItem().isAccessorie()) {
                result += 60 * rate - itemInstance.getItem().getType();

                if (itemInstance.isEquipped()) {
                    result += 80 * rate;
                }
            }
        } else if (itemInstance.getItem().isAccessorie()) {
            result += 50 * rate - itemInstance.getItem().getType();

            if (itemInstance.isEquipped()) {
                result += 70 * rate;
            }
        } else if (itemInstance.getItem().getName().contains("????????????")) {
            result += 49 * rate;
        } else if (NumberUtils.contains(itemInstance.getItemId(), 40087, 140087, 240087, 40074, 140074, 240074)) {//?????? ?????????
            result += 48 * rate;
        } else if ((itemId >= 40010 && itemId <= 40029) || itemId == 437011 || itemId == 437010) {
            result += 47 * rate;
        } else if ((itemId >= 41277 && itemId <= 41292) || (itemId >= 49049 && itemId <= 49064) || (itemId >= L1ItemId.NORMAL_COOKFOOD_3RD_START && itemId <= L1ItemId.SPECIAL_COOKFOOD_3RD_END) || (itemId >= 9800 && itemId <= 9803) || itemId == 436000) {
            result += 46 * rate;
        } else if ((itemId >= 76767 && itemId <= 76776) || (itemId >= 5000009 && itemId <= 5000041)) {
            result += 46 * rate;
        } else if (itemId >= 40090 && itemId <= 40094) {
            result += 45 * rate;
        } else if (itemInstance.getItem().getName().startsWith("?????? ?????????")) {
            result += (44 * rate) + itemInstance.getItemId();
        } else if (itemInstance.getItem().getName().contains("??? ?????? ?????????") || NumberUtils.contains(itemInstance.getItemId(), 40804, 42011, 127008, 40809, 42007, 42081, 40113)) {

            if (itemId == 40113) {
                itemId = 42029;
            }

            result += (43 * rate) + itemId;
        } else if (itemInstance.getItem().getName().contains("?????????")) {
            result += (42 * rate) + itemInstance.getItem().getType() + itemInstance.getItem().getMaterial();
        } else {
            if (NumberUtils.contains(itemInstance.getItemId(), 60001209, 60001301, 430005)) {
                result = 600 * rate;
            } else if (NumberUtils.contains(itemInstance.getItemId(), 6000039, 6000042, 6000050, 6000056, 60001163, 60001155, 40328)) {//?????? ?????? ????????? ?????????
                result = 500 * rate;
            } else if (NumberUtils.contains(itemInstance.getItemId(),
                    40289,
                    40290, 40291,
                    40292, 40293,
                    40294, 40295,
                    40296, 40297,
                    6000064
            )) {
                result += 400 * rate;
            } else if (itemId >= 40044 && itemId <= 40055) {//?????????
                result += 300 * rate + itemId + 40044;
            } else {
                //????????? ?????? ??????
                if (itemId >= 40170 && itemId <= 45022) {
                    result -= itemId - 40170 + 2000;
                }

                result += itemInstance.getItem().getType() + itemInstance.getItem().getMaterial();
            }
        }

        return result;
    }

    public boolean handleCommands(L1PcInstance pc, String cmdLine) {
        StringTokenizer token = new StringTokenizer(cmdLine);

        String cmd;

        if (token.hasMoreTokens()) {
            cmd = token.nextToken();
        } else {
            return true;
        }

        StringBuilder param = new StringBuilder();

        while (token.hasMoreTokens()) {
            param.append(token.nextToken()).append(' ');
        }

        param = new StringBuilder(param.toString().trim());

        StringTokenizer nt = new StringTokenizer(param.toString());

        if (BillCommand.getInstance().command(pc, cmd, nt)) {
            return true;
        }

        if ("??????".equalsIgnoreCase(cmd)) {
            return shop(pc, nt);
        } else if ("????????????".equalsIgnoreCase(cmd)) {
            return shopMove(pc, nt);
        } else if ("????????????".equalsIgnoreCase(cmd)) {
            Map<Integer, L1TimeDungeonData> m = pc.getTimeDungeon().getTimeDungeonDataMap();

            for (L1TimeDungeonData data : m.values()) {
                pc.sendPackets("?????? : " + data.getTimeDungeon().getMapName() + " ???????????? : " + data.getRemainingMinute() + "???");
            }

            return true;
        } else if ("??????".equalsIgnoreCase(cmd)) {
            try {
                String k = "ranking";

                if (!pc.getTimer().isTimeOver(k)) {
                    pc.sendPackets(pc.getTimer().remainingSecond(k) + "??? ?????? ???????????????");
                    return true;
                }

                pc.getTimer().setWaitTime(k, 2 * 1000);

                pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_rank"));

            } catch (Exception e) {
                logger.error("??????", e);
            }

            return true;
        } else if ("??????".equals(cmd)) {
            return clanInfo(pc, nt);
        } else if ("??????".equalsIgnoreCase(cmd)) {
            try {
                String targetName = nt.nextToken();
                L1WarUtils.war(pc, targetName, 0);
            } catch (Exception e) {
                String warName = "?????????";

                for (L1Castle castle : CastleTable.getInstance().getCastleTableList()) {
                    if (WarTimeScheduler.getInstance().isNowWar(castle.getId())) {
                        L1Clan c = ClanTable.getInstance().getCastleLeaderClan(castle.getId());
                        warName = c.getClanName();
                        break;
                    }
                }

                pc.sendPackets(".?????? " + warName);
            }

            return true;
        } else if ("????????????".equalsIgnoreCase(cmd)) {
            try {
                if (!pc.isCrown()) {
                    pc.sendPackets("????????? ????????? ??????????????? ??????????????????.");

                    return true;
                }

                if (pc.getClanId() == 0) {
                    pc.sendPackets("????????? ???????????? ???????????????");
                    return true;
                }

                String type = nt.nextToken();

                if (type.equalsIgnoreCase("???")) {
                    pc.getStateMap().setAutoClan(true);
                    pc.sendPackets("??????????????? ????????????????????????");
                } else if (type.equalsIgnoreCase("???")) {
                    pc.getStateMap().setAutoClan(false);

                    pc.sendPackets("??????????????? ?????????????????????");
                }
            } catch (Exception e) {
                pc.sendPackets(".???????????? ???/???");
            }
            return true;
        } else if ("???????????????".equalsIgnoreCase(cmd)) {
            String type = nt.nextToken();

            if (type.equalsIgnoreCase("???")) {
                pc.getDataMap().put(L1DataMapKey.DMG_CHECK, "true");
                pc.sendPackets("?????????????????? ????????? ???????????????");
            } else if (type.equalsIgnoreCase("???")) {
                pc.getDataMap().put(L1DataMapKey.DMG_CHECK, "false");
                pc.sendPackets("?????????????????? ???????????? ???????????????");
            }

            return true;
        } else if ("????????????".equalsIgnoreCase(cmd)) {
            String type = nt.nextToken();

            if (pc.isGm()) {
                if (type.equalsIgnoreCase("???")) {
                    L1AutoHuntScheduler.getInstance().add(pc, new L1AutoHuntRandomMoveAi(pc));
                    pc.setAutoHunt(true);
                } else if (type.equalsIgnoreCase("???")) {
                    L1AutoHuntScheduler.getInstance().remove(pc);
                    pc.setAutoHunt(false);
                }
            }

            return true;
        } else if (cmd.equalsIgnoreCase("???")) {
            return bloodParty(pc);
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                int saveSlot = Integer.parseInt(nt.nextToken());

                if (saveSlot >= 1 && saveSlot <= 2) {
                    SlotSaveTable.getInstance().deleteSlot(pc.getId(), saveSlot);

                    L1ItemInstance weapon = pc.getEquipSlot().getWeapon();
                    List<L1ItemInstance> armors = pc.getEquipSlot().getArmors();

                    if (weapon != null) {
                        SlotSaveTable.getInstance().saveSlot(pc.getId(), saveSlot, weapon);
                    }

                    if (!armors.isEmpty()) {
                        SlotSaveTable.getInstance().saveSlot(pc.getId(), saveSlot, armors);
                    }

                    SlotSaveTable.getInstance().saveCache(pc.getId());

                    pc.sendPackets(saveSlot + "??? ????????? ?????????????????????");
                } else {
                    pc.sendPackets(".???????????? 1/2");
                }
            } catch (Exception e) {
                pc.sendPackets(".???????????? 1/2");
            }

            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            /*
            Map<Integer, Hunt> huntCheckMap = pc.getHuntCheckMap();

            if (huntCheckMap.isEmpty()) {
                pc.sendPackets("??????????????? ???????????? ????????????");
                return true;
            }

            for (Integer h : huntCheckMap.keySet()) {
                Hunt hunt = huntCheckMap.get(h);
                hunt.printInfo(h, pc);
            }

            return true;
             */
        } else if (cmd.equalsIgnoreCase("?????????")) {
            return karamaCheck(pc);
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                String type = nt.nextToken();

                if (!pc.isCrown()) {
                    pc.sendPackets("?????? ???????????? ????????? ??? ?????? ??????????????????");
                    return true;
                }

                if (pc.isInParty()) {
                    pc.sendPackets("??????????????? ????????? ??? ????????????");
                    return true;
                }

                if (pc.isInvisible()) {
                    pc.sendPackets("?????????????????? ??????????????? ?????? ??? ??? ????????????");
                    return true;
                }

                if (pc.isFishing()) {
                    pc.sendPackets("?????????????????? ??????????????? ?????? ??? ??? ????????????");
                    return true;
                }

                if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())
                        && !L1CastleLocation.isInCastleInner(pc.getMapId())) {
                    pc.sendPackets("??????????????? ????????? ?????? ??????????????? ????????? ??? ????????????");
                    return true;
                }

                if ("???".equalsIgnoreCase(type)) {
                    pc.setAutoKingBuff(false);
                    pc.sendPackets("??????????????? ?????????????????????");
                    return true;
                }

                if (pc.isAutoKingBuff()) {
                    pc.sendPackets("?????? ??????????????? ?????????????????? .???????????? ??? ?????? ????????? ???????????????");
                    return true;
                }

                if ("??????".equalsIgnoreCase(type)) {
                    if (!SkillsTable.getInstance().spellCheck(pc.getId(), SHINING_SHILELD)) {
                        pc.sendPackets("????????? ????????? ????????? ???????????????");
                        return true;
                    }

                    L1PolyMorph.undoPoly(pc);
                    pc.setAutoKingBuff(true);
                    pc.sendPackets("????????????(??????)??? ?????? ???????????????");
                    pc.setAutoKingBuffState(type);
                } else if ("??????".equalsIgnoreCase(type)) {
                    if (!SkillsTable.getInstance().spellCheck(pc.getId(), GLOWING_WEAPON)) {
                        pc.sendPackets("????????? ????????? ????????? ???????????????");
                        return true;
                    }

                    L1PolyMorph.undoPoly(pc);
                    pc.setAutoKingBuff(true);
                    pc.sendPackets("????????????(??????)??? ?????? ???????????????");
                    pc.setAutoKingBuffState(type);
                } else if ("????????????".equalsIgnoreCase(type)) {
                    L1PolyMorph.undoPoly(pc);
                    pc.setAutoKingBuff(true);
                    pc.sendPackets("????????????(??????????????????)??? ?????? ???????????????");
                    pc.setAutoKingBuffState(type);
                } else {
                    pc.sendPackets("???????????? ??????/??????/????????????/???");
                }
            } catch (Exception e) {
                pc.sendPackets("???????????? ??????/??????/????????????/???");
            }

            return true;
        } else if (cmd.equalsIgnoreCase("???") ||
                cmd.equalsIgnoreCase("???") ||
                cmd.equalsIgnoreCase("???")) {

            int bless = 1;
            int enchantLvl = 0;

            if (cmd.equalsIgnoreCase("???")) {
                bless = 0;
            } else if (cmd.equalsIgnoreCase("???")) {
                bless = 2;
            }

            String name = nt.nextToken();

            if (nt.hasMoreTokens()) {
                enchantLvl = Integer.parseInt(nt.nextToken());
            }

            pc.getPagination().setCurrentPageNo(1);
            pc.getSearchShopItem().init(name, enchantLvl, bless);
            pc.getSearchShopItem().showHtml();

            return true;
        } else if (cmd.equalsIgnoreCase("??????????????????")) {
            try {
                ServerPacket packet = new ServerPacket();
                packet.writeC(L1Opcodes.S_OPCODE_BOARDREAD);
                packet.writeD(0);
                packet.writeS("???????????????");
                packet.writeS("???????????????");
                packet.writeS("");

                String msg = "????????? ?????? \r\n";
                msg += "/?????? ???????????? \r\n";
                msg += "- ?????? ?????????????????? ?????? ???????????? \r\n\n";
                msg += "????????? ?????? \r\n";
                msg += "??????????????? -> ????????? ?????? \r\n";
                msg += "- ?????? ???????????? ????????? ?????? ????????????";
                msg += "- ?????????????????? ????????? ?????? ????????????";

                packet.writeS(msg);

                pc.sendPackets(packet);
            } catch (Exception e) {
            }
        } else if (cmd.equalsIgnoreCase("??????")) {
            try {
                File mobDataFile = new File("data/support.txt");

                String data = IOUtils.toString(new FileInputStream(mobDataFile), StandardCharsets.UTF_8);

                ServerPacket packet = new ServerPacket();
                packet.writeC(L1Opcodes.S_OPCODE_BOARDREAD);
                packet.writeD(0);
                packet.writeS("????????????");
                packet.writeS("????????????");
                packet.writeS("");
                packet.writeS(data);
                pc.sendPackets(packet);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".??????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            buff(pc);
            return true;
        } else if (cmd.equalsIgnoreCase("???")) {
            pc.getAutoPotion().commandAutoPotion("???", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            pc.getAutoPotion().commandAutoPotion("??????", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            pc.getAutoPotion().commandAutoPotion("??????", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("???")) {
            pc.getAutoPotion().commandAutoPotion("???", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            pc.getAutoPotion().commandAutoPotion("??????", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            pc.getAutoPotion().commandAutoPotion("??????", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("???")) {
            pc.getAutoPotion().commandAutoPotion("???", nt);
            return true;
        } else if (cmd.equals("????????????")) {
            CharacterNextReturnUtils.nextReq(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            pc.getAutoPotion().commandAutoPotion("??????", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            pc.getAutoPotion().commandAutoPotion("??????", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            dragonDiamond(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            dragonPerl(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("???????????????")) {
            pc.getAutoPotion().stop();
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                AdenBankAccountController.getInstance().checkBankAccount(pc, pc.getAccountName());
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".????????????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                String account_no = nt.nextToken();//????????????
                String account_name = nt.nextToken();//?????????
                String bank_name = nt.nextToken();//?????????
                String phone = nt.nextToken();//?????????

                AdenBankAccount v = new AdenBankAccount();
                v.setAccount_id(pc.getAccountName());
                v.setBank_no(account_no);
                v.setBank_owner_name(account_name);
                v.setBank_name(bank_name);
                v.setPhone(phone);

                if (!StringUtils.isEmpty(account_no)) {
                    if (SqlUtils.selectInteger("SELECT count(*) FROM character_blacklist where bankNo=?", account_no) > 0) {
                        L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, true);
                        L1World.getInstance().broadcastServerMessage("[" + pc.getName() + " ??????] : ???????????????");
                        return true;
                    }

                    if (SqlUtils.selectInteger("SELECT count(*) FROM character_blacklist where phone=?", phone) > 0) {
                        L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, true);
                        L1World.getInstance().broadcastServerMessage("[" + pc.getName() + " ??????] : ???????????????");
                        return true;
                    }
                }

                AdenBankAccountController.getInstance().registerBankAccount(pc, v);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".???????????? ???????????? ????????? ????????? ?????????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                int aden = Integer.parseInt(nt.nextToken());//????????????
                int cash = Integer.parseInt(nt.nextToken());//????????????

                AdenBankAccountController.getInstance().registerAden(pc, aden, cash);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".???????????? ??????????????? ????????????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {

                if (pc.getLevel() < CodeConfig.ADEN_BUY_MIN_LEVEL) {
                    pc.sendPackets("?????????????????? ??????????????? " + CodeConfig.ADEN_BUY_MIN_LEVEL + "?????????");
                    return true;
                }

                int id = Integer.parseInt(nt.nextToken());
                AdenBankAccountController.getInstance().buyAden(pc, id);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".???????????? ????????????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                int id = Integer.parseInt(nt.nextToken());
                AdenBankAccountController.getInstance().sellAden(pc, id);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".???????????? ????????????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                int id = Integer.parseInt(nt.nextToken());
                AdenBankAccountController.getInstance().cancelAden(pc, id);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".???????????? ????????????"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            CharacterNextReturnUtils.saveCommand(pc);

            return true;
        } else if (cmd.equalsIgnoreCase("???") || cmd.equalsIgnoreCase("?????????")) {
            try {
                String k = "adenBoardNpcId";

                if (!pc.getTimer().isTimeOver(k)) {
                    pc.sendPackets(pc.getTimer().remainingSecond(k) + "??? ?????? ???????????????");
                    return true;
                }

                pc.getTimer().setWaitTime(k, 2 * 1000);

                L1NpcInstance o = L1World.getInstance().getNpc(CodeConfig.ADEN_BOARD_NPC_ID, 2007121683);
                pc.sendPackets(new S_AdenBoard(o.getId()));

                pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, CodeConfig.getAdenaClickMent()));
            } catch (Exception e) {
                logger.error("??????", e);
            }
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            hunt(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????") || cmd.equalsIgnoreCase("..")) {
            tell(pc);
            return true;
        } else if (cmd.equalsIgnoreCase("???")) {
            remoteParty(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            checkCharTradeNo(pc, nt);

            return true;
        } else if (cmd.equalsIgnoreCase("????????????") || cmd.equalsIgnoreCase("???")) {
            try {
                inventorySetup(pc);

                pc.sendPackets("??????????????? ?????????????????????");
            } catch (Exception e) {
                pc.sendPackets(".???/.????????????");
            }

            return true;
        } else if (cmd.equalsIgnoreCase("???????????????")) {
            changeName(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            markView(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("?????????")) {
            searchDropList(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("?????????")) {
            itemDropList(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("??????") || cmd.equalsIgnoreCase("?????????")
        ) {
            mapHack(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            dropment(pc, nt);

            return true;
        } else if (cmd.equalsIgnoreCase("????????????")) {
            try {
                String type = nt.nextToken();

                if (type.equalsIgnoreCase("???")) {
                    pc.getMent().setPotion(true);
                } else if (type.equalsIgnoreCase("???")) {
                    pc.getMent().setPotion(false);
                }

                pc.sendPackets("???????????? ?????? : " + type);
            } catch (Exception e) {
                pc.sendPackets(".???????????? ???/???");
            }
            return true;
        } else if (cmd.equalsIgnoreCase("??????")) {
            handleCommands(pc, "???????????? ???");
            handleCommands(pc, "?????? ???");
            handleCommands(pc, "?????? ???");
            handleCommands(pc, "?????? ???");
            handleCommands(pc, "?????? ???");

            return true;
        }

        return false;
    }

    private void dropment(L1PcInstance pc, StringTokenizer nt) {
        try {
            String type = nt.nextToken();

            if (type.equalsIgnoreCase("???")) {
                pc.getMent().setDrop(true);
            } else if (type.equalsIgnoreCase("???")) {
                pc.getMent().setDrop(false);
            }

            pc.sendPackets("???????????? ?????? : " + type);
        } catch (Exception e) {
            pc.sendPackets(".???????????? ???/???");
        }
    }

    private void dragonDiamond(L1PcInstance pc, StringTokenizer nt) {
        try {
            String onoff = nt.nextToken();

            if (onoff.equals("???")) {
                pc.setAutoDragonDiamond(true);
                pc.sendPackets("??????????????????????????? ?????? ????????? ?????????????????????");
            } else if (onoff.equals("???")) {
                pc.setAutoDragonDiamond(false);
                pc.sendPackets("??????????????????????????? ?????? ????????? ?????????????????????");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".?????? [??? or ???]"));
        }
    }

    private void dragonPerl(L1PcInstance pc, StringTokenizer nt) {
        try {
            String onoff = nt.nextToken();

            if (onoff.equals("???")) {
                pc.setAutoDragonPerl(true);
                pc.sendPackets("?????????????????? ?????? ????????? ?????????????????????");
            } else if (onoff.equals("???")) {
                pc.setAutoDragonPerl(false);
                pc.sendPackets("?????????????????? ?????? ????????? ?????????????????????");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".?????? [??? or ???]"));
        }
    }

    private boolean karamaCheck(L1PcInstance pc) {
        pc.sendPackets("????????? : " + pc.getKarma());
        pc.sendPackets("?????? : " + pc.getKarmaLevel() + "??????");
        pc.sendPackets("?????? : " + (pc.getKarma() > 0 ? "????????????" : "????????????"));

        return true;
    }

    private void mapHack(L1PcInstance pc, StringTokenizer nt) {
        try {
            if (pc.getSkillEffectTimerSet().hasSkillEffect(DARKNESS, DARK_BLIND)) {
                pc.sendPackets("?????????????????? ???????????????");
                return;
            }

            String onOff = nt.nextToken();

            if (onOff.equals("???")) {
                pc.sendPackets(new S_Ability(3, true));
                pc.getDataMap().put(L1DataMapKey.MAP_HACK, "on");
            } else if (onOff.equals("???")) {
                pc.sendPackets(new S_Ability(3, false));
                pc.getDataMap().put(L1DataMapKey.MAP_HACK, "off");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".??????/????????? [??? or ???]"));
        }
    }

    private void itemDropList(L1PcInstance pc, StringTokenizer st) {
        try {
            String itemName = st.nextToken();
            String type = "???";

            if (st.hasMoreTokens()) {
                type = st.nextToken();
            }

            int bless = 1;

            if ("???".equalsIgnoreCase(type)) {
                bless = 0;
            } else if ("???".equalsIgnoreCase(type)) {
                bless = 2;
            }
            List<L1Drop> resultList = findDropList(itemName, bless);

            if (resultList.isEmpty()) {
                pc.sendPackets(new S_SystemMessage("???????????? ???????????? ???????????????."));
            } else {
                int itemId = resultList.get(0).getItemId();

                L1Item item = ItemTable.getInstance().findItem(itemId);

                List<String> resultString = new ArrayList<>();
                resultString.add("[" + type + "] " + item.getName());

                for (L1Drop o : resultList) {
                    resultString.add(o.getMobName());
                }

                for (int start = resultString.size(); start <= 50; start++) {
                    resultString.add(" ");
                }

                pc.setItemDropSearchList(resultList);
                pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_itemdrop", resultString.toArray()));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".????????? ???????????? ???"));
            pc.sendPackets(new S_SystemMessage(".????????? ???????????? ???"));
            pc.sendPackets(new S_SystemMessage(".????????? ???????????? ???"));
            pc.sendPackets(new S_SystemMessage(".????????? ???????????? (????????????????????????)"));
        }
    }

    private List<L1Drop> findDropList(String itemName, int bless) {
        List<Object> params = new ArrayList<>();

        String sql = "SELECT\n" +
                "   itemId,\n" +
                "	mobId,\n" +
                "	mobName \n" +
                "FROM\n" +
                "	DROPLIST \n" +
                "WHERE\n" +
                "	ITEMID IN ( SELECT item_id FROM all_item WHERE REPLACE ( NAME, ' ', '' ) LIKE concat('%',?,'%') ";

        params.add(itemName);

        if (bless == 0) {
            sql += "    and bless= ?";
            params.add(bless);
        } else if (bless == 2) {
            sql += "    and bless= ?";
            params.add(bless);
        }

        sql += "     ) \n";

        sql += "	AND (\n" +
                "		(( SELECT COUNT(*) FROM spawnlist WHERE npc_templateid = mobId AND count > 0 ) > 0 ) \n" +
                "	OR (( SELECT COUNT(*) FROM spawnlist_lastabard WHERE npc_templateid = mobId AND count > 0 ) > 0 ) \n" +
                "	OR (( SELECT COUNT(*) FROM spawnlist_boss_hot WHERE npcid = mobId) > 0 ) \n" +
                "	) \n" +
                "GROUP BY\n" +
                "	mobid \n" +
                "ORDER BY\n" +
                "	moblevel";

        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1Drop.class), params.toArray());
    }

    private void searchDropList(L1PcInstance pc, StringTokenizer nt) {
        try {
            String nameid = nt.nextToken();

            int npcid;

            try {
                npcid = Integer.parseInt(nameid);
            } catch (NumberFormatException e) {
                npcid = NpcTable.getInstance().findNpcIdByNameWithoutSpace(nameid);
                if (npcid == 0) {
                    pc.sendPackets(new S_SystemMessage("?????? ???????????? ???????????? ???????????????."));
                    return;
                }
            }

            pc.sendPackets(new S_DropInfo(npcid));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("ex).????????? ???????????????"));
        }
    }

    private void markView(L1PcInstance pc, StringTokenizer nt) {
        try {
            int typeMarkOn = 1;
            int typeMarkOff = 3;

            String check = nt.nextToken();

            String clan_name = pc.getClanName();
            L1Clan clan = L1World.getInstance().getClan(clan_name);

            if (clan == null) {
                pc.sendPackets(new S_SystemMessage("????????? ??????????????? ?????????."));
                return;
            }

            if (check.equals("???")) {
                if (CodeConfig.WAR_MARK_ALL) {
                    for (L1Clan otherClan : L1World.getInstance().getAllClans()) {
                        pc.sendPackets(new S_War(typeMarkOn, pc.getClanName(), otherClan.getClanName()));
                    }
                } else {
                    pc.sendPackets(new S_War(typeMarkOn, pc.getClanName(), clan.getClanName()));
                }

                pc.setMarkShow(true);
            } else if (check.equals("???")) {
                if (CodeConfig.WAR_MARK_ALL) {
                    for (L1Clan otherClan : L1World.getInstance().getAllClans()) {
                        pc.sendPackets(new S_War(typeMarkOff, pc.getClanName(), otherClan.getClanName()));
                    }
                } else {
                    pc.sendPackets(new S_War(typeMarkOff, clan_name, clan_name));
                }

                pc.setMarkShow(false);
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".?????? [???,???]"));
        }
    }

    public boolean clanInfo(L1PcInstance pc, StringTokenizer st) {
        try {
            L1Clan targetClan;

            if (pc.isGm()) {
                String targetName;

                if (st.hasMoreTokens()) {
                    targetName = st.nextToken();
                } else {
                    targetName = pc.getClanName();
                }

                targetClan = L1World.getInstance().getClan(targetName);
            } else {
                if (pc.getClanId() == 0) {
                    pc.sendPackets("????????? ???????????? ????????????");
                    return true;
                }

                targetClan = pc.getClan();
            }

            pc.sendPackets("\\fY???---------------------------------------------------???");
            pc.sendPackets("  ????????????  ");
            pc.sendPackets("\\fY???---------------------------------------------------???");
            pc.sendPackets("  ???????????????     : " + NumberFormat.getInstance().format(targetClan.getExp()));
            pc.sendPackets("  ????????? ???      : " + targetClan.getClanMemberList().size());
            pc.sendPackets("  ?????? ????????? ??? : " + targetClan.getOnlineClanMember().size());
            pc.sendPackets("\\fY???---------------------------------------------------???");
        } catch (Exception e) {
            if (pc.isGm()) {
                pc.sendPackets(".?????? ?????????");
            }
        }

        return true;
    }

    private void changeName(L1PcInstance pc, StringTokenizer nt) {
        try {
            if (!pc.getInventory().checkItem(467009)) {
                pc.sendPackets("????????? ?????? ???????????? ????????? ?????? ????????????");
                return;
            }

            if (pc.getLevel() >= 52) {
                if (pc.getClanId() > 0) {
                    pc.sendPackets(new S_SystemMessage("?????? ????????? ???????????? ???????????? ????????????."));
                    return;
                }

                String name = nt.nextToken();

                if (name.length() > 50 || StringUtils.isEmpty(name)) {
                    return;
                }

                if (L1BadNameUtils.getInstance().isBadName(name)) {
                    pc.sendPackets(new S_SystemMessage("?????? ????????? ??????????????????"));
                    return;
                }

                if (L1CommonUtils.isInValidName(name) || L1CommonUtils.isInvalidName(name)) {
                    pc.sendPackets(new S_SystemMessage("????????? ??????????????? ???????????????"));
                    return;
                }

                if (CharacterTable.getInstance().doesCharNameExist(name) || CharacterTradeDao.getInstance().isExistCharTrade(name)) {
                    pc.sendPackets(new S_SystemMessage("????????? ???????????? ?????? ?????????"));

                    return;
                }

                if (pc.getInventory().consumeItem(467009, 1)) {
                    SqlUtils.update("UPDATE letter set receiver=? WHERE receiver=?", name, pc.getName());
                    SqlUtils.update("UPDATE letter set sender=? WHERE sender=?", name, pc.getName());

                    CharacterTable.getInstance().updateCharName(name, pc.getName());
                    pc.save();

                    String str = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] " + pc.getName() + "  >  " + name + "  [???????????????]";
                    StringBuilder FileName = new StringBuilder("log/changPcName.txt");
                    PrintWriter out;

                    try {
                        out = new PrintWriter(new FileWriter(FileName.toString(), true));
                        out.println(str);
                        out.close();
                    } catch (IOException e) {
                        logger.error("??????", e);
                    }

                    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("(" + pc.getName() + ")?????? ???????????????(" + name + ")?????? ?????????????????????."));
                    pc.disconnect(pc.getName() + " ????????? ??????");
                } else {
                    pc.sendPackets(new S_SystemMessage("???????????? ?????? ???????????? ????????????."));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("Level : [55] ?????? ????????? ?????? ???????????????"));
            }
        } catch (Exception e) {
            pc.sendPackets(".??????????????? ????????????");
        }
    }

    private void checkCharTradeNo(L1PcInstance pc, StringTokenizer nt) {
        try {
            int number = Integer.parseInt(nt.nextToken());

            CharacterTradeInfo info = CharacterTradeDao.getInstance().getInfo(number);

            if (info == null) {
                pc.sendPackets(number + "??? ???????????? ?????? ?????????????????????.");
                return;
            }

            L1PcInstance targetPc = info.getTargetPc();

            targetPc.loadItems();

            List<L1ItemInstance> items = targetPc.getInventory().getItems();

            ServerPacket packet = new ServerPacket();

            packet.writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
            packet.writeD(pc.getId());
            packet.writeH(items.size());
            packet.writeC(6);

            for (L1ItemInstance item : items) {
                packet.writeD(item.getId());
                packet.writeC(item.getItem().getType2());
                packet.writeH(item.getGfxId());
                packet.writeC(item.getBless());
                packet.writeD(item.getCount());
                packet.writeC(item.isIdentified() ? 1 : 0);
                packet.writeS(item.getViewName());
            }

            packet.writeD(30);
            pc.sendPackets(packet);

            StringBuilder sb = new StringBuilder();
            sb.append("----???????????? ??????-----").append("\n").append("\n");
            sb.append("????????? : ").append(targetPc.getName()).append("\n");
            sb.append("???  ??? : ").append(targetPc.getLevel()).append(".").append(targetPc.getExpPer()).append("\n");
            sb.append("HP/MP : ").append("HP - ").append(targetPc.getMaxHp()).append(" / ").append("MP - ").append(targetPc.getMaxMp()).append("\n");
            sb.append("????????? : ").append(pc.getAbility().getElixirCount()).append(" ??????").append("\n");

            pc.sendPackets(sb.toString());
        } catch (Exception e) {
            pc.sendPackets(".???????????? ????????????");
        }
    }

    public void inventorySetup(L1PcInstance pc) {
        List<L1ItemInstance> items = pc.getInventory().getItems();

        items.sort((o1, o2) -> {
            int sort1 = inventorySortNum(o1);
            int sort2 = inventorySortNum(o2);
            return sort2 - sort1;
        });

        Map<Integer, Boolean> map = new HashMap<>();

        for (L1ItemInstance item : items) {
            boolean isEquipped = item.isEquipped();
            map.put(item.getId(), isEquipped);
            pc.getInventory().setEquipped(item, false);
            pc.sendPackets(new S_DeleteInventoryItem(item));
        }

        pc.sendPackets(new S_InvList(pc));

        for (L1ItemInstance item : pc.getInventory().getItems()) {
            pc.getInventory().setEquipped(item, false);
            pc.getInventory().setEquipped(item, map.get(item.getId()));
        }
    }

    public void tell(L1PcInstance pc) {
        try {
            if (!pc.getTimer().isTimeOver("lastDamagedTime")) {
                pc.sendPackets(new S_SystemMessage("????????? ?????? ???????????????."));
                return;
            }

            if (!pc.getTimer().isTimeOver("tell")) {
                pc.sendPackets(new S_SystemMessage("????????? ?????? ???????????????."));
                return;
            }

            if (pc.getMapId() == L1Map.MAP_FISHING) {
                pc.sendPackets(new S_SystemMessage("??????????????? ????????? ??? ????????????."));
                return;
            }

            if (pc.getMapId() == 350) {
                pc.sendPackets(new S_SystemMessage("??????????????? ????????? ??? ????????????."));
                return;
            }

            if (pc.isDead()) {
                pc.sendPackets(new S_SystemMessage("?????? ???????????? ????????? ??? ????????????."));
                return;
            }

            if (pc.getMapId() == 5153) {
                L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                return;
            }

            if (pc.isParalyzed()) {
                pc.sendPackets(new S_SystemMessage("????????? ??????????????? ????????? ??? ????????????."));
                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.SLEEP_SKILLS)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.STUN_SKILLS)
            ) {
                pc.sendPackets("?????? ????????? ???????????? ?????? ?????????.");
                return;
            }

            if (pc.isSleeped()) {
                pc.sendPackets(new S_SystemMessage("??????????????? ????????? ??? ????????????."));
                return;
            }

            L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
            pc.getTimer().setWaitTime("tell", 2000);
        } catch (Exception ignored) {
        }
    }

    private void hunt(L1PcInstance pc, StringTokenizer nt) {
        try {
            String targetName = nt.nextToken();
            L1PcInstance target = L1World.getInstance().getPlayer(targetName);

            L1CommonUtils.doHunt(pc, target);
        } catch (Exception e) {
            pc.sendPackets(".?????? ?????????");
        }
    }

    private boolean shopMove(L1PcInstance pc, StringTokenizer nt) {
        if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
            pc.sendPackets("?????? ???????????? ????????? ???????????????");
            return true;
        }

        String name = nt.nextToken();

        L1PcInstance target = CharacterTable.getInstance().restoreCharacter(name);

        L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(target);

        if (shop == null) {
            pc.sendPackets(target + "??? ????????? ???????????? ???????????????");
            return true;
        }

        L1Teleport.teleport(pc, shop.getX(), shop.getY(), shop.getMapId(), 5, true);

        return true;
    }

    private boolean shop(L1PcInstance pc, StringTokenizer nt) {
        try {
            L1UserShopCreateHandler handler = L1UserShopCreateHandler.getInstance();

            String type = nt.nextToken();

            if (type.equalsIgnoreCase("??????")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP) {
                    pc.sendPackets("?????? ???????????? ????????? ???????????????");
                    return true;
                }

                handler.startSell(pc);
            } else if (type.equalsIgnoreCase("??????")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP) {
                    pc.sendPackets("?????? ???????????? ????????? ???????????????");
                    return true;
                }

                pc.sendPackets("\\fY?????? ?????? ???????????? ????????? ?????????(0:???,1:???,2:???) ??????");
                pc.sendPackets("\\fY?????? ???(1~5) ???(6~10) ??????(11~15) ???(16~20)");

                String shop_buy_enchant = pc.getDataMap().get(L1DataMapKey.SHOP_BUY_ENCHANT);
                String shop_buy_bless = pc.getDataMap().get(L1DataMapKey.SHOP_BUY_BLESS);
                String shop_buy_attr = pc.getDataMap().get(L1DataMapKey.SHOP_BUY_ATTR);

                String itemName = nt.nextToken();

                int enchant = 0;

                if (nt.hasMoreTokens()) {
                    enchant = Integer.parseInt(nt.nextToken());

                    if (enchant > 9) {
                        pc.sendPackets("???????????? 9????????? ????????? ???????????????");
                        return true;
                    }
                } else if (shop_buy_bless != null) {
                    enchant = Integer.parseInt(shop_buy_enchant);
                    pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ENCHANT);
                }

                int bless = 1;

                if (nt.hasMoreTokens()) {
                    bless = Integer.parseInt(nt.nextToken());
                    if (bless < -1 || bless > 1) {
                        pc.sendPackets("????????? -1 0 1 ??? ????????? ??????????????? ?????????");
                        return true;
                    }
                } else if (shop_buy_bless != null) {
                    bless = Integer.parseInt(shop_buy_bless);
                    pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_BLESS);
                }

                int attrLevel = 0;

                if (nt.hasMoreTokens()) {
                    attrLevel = Integer.parseInt(nt.nextToken());
                    if (attrLevel > 20 || attrLevel < 0) {
                        pc.sendPackets("????????? 0~20????????? ????????? ???????????????");
                        return true;
                    }
                } else if (shop_buy_attr != null) {
                    attrLevel = Integer.parseInt(shop_buy_attr);
                    pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ATTR);
                }

                handler.startBuy(pc, itemName, enchant, bless, attrLevel);
            } else if (type.equalsIgnoreCase("??????")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("?????? ???????????? ????????? ???????????????");
                    return true;
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                int adena = Integer.parseInt(nt.nextToken());

                shop.pushAdena(pc, adena);
            } else if (type.equalsIgnoreCase("??????")) {
                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("????????? ???????????? ???????????????");
                    return true;
                }

                shop.closeShop();
            } else if (type.equalsIgnoreCase("??????")) {
                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("????????? ???????????? ???????????????");
                    return true;
                }

                List<L1PrivateShopSell> sellList = shop.getSellList();

                StringBuilder msg = new StringBuilder();

                msg.append("\r\n").append("\r\n");
                msg.append("\\fY").append("----------- ????????? -----------").append("\r\n").append("\r\n");
                for (L1PrivateShopSell o : sellList) {
                    o.getItem().setIdentified(true);

                    msg.append("\\fU").append(o.getItem().getNumberedViewName(o.getSellTotalCount() - o.getSellCount()))
                            .append(" : ")
                            .append(NumberFormat.getInstance().format(o.getSellPrice()))
                            .append("?????????")
                            .append("\r\n");
                }

                msg.append("\r\n");

                msg.append("\\fY").append("----------- ????????? -----------").append("\r\n").append("\r\n");

                List<L1UserShop> k = L1UserShopTable.getInstance().selectUserShopList(pc.getId(), "buy");

                for (L1UserShop o : k) {
                    int cnt = L1UserShopTable.getInstance().selectCurrentBuyCountByUserShop(o);

                    msg.append("\\fU").append(o.getItemViewName()).append(" ").append(cnt).append("/").append(o.getTotalCount())
                            .append(" : ")
                            .append(NumberFormat.getInstance().format(o.getPrice()))
                            .append("?????????")
                            .append("\r\n");
                }

                msg.append("\r\n");

                msg.append("\\fY").append("---------- ???????????? ----------").append("\r\n").append("\r\n");

                List<L1UserShop> buyCompleteList = L1UserShopTable.getInstance().selectUserShopBuyList(pc.getId());

                for (L1UserShop o : buyCompleteList) {
                    int cnt = L1UserShopTable.getInstance().selectCurrentBuyCountByUserShop(o);

                    L1ItemInstance item = ItemTable.getInstance().createItem(o.getItemId());
                    item.setIdentified(true);
                    item.setEnchantLevel(o.getEnchantLvl());
                    item.setAttrEnchantLevel(o.getAttrLvl());
                    item.setBless(o.getBless());
                    item.setCount(o.getCount());

                    msg.append("\\fU").append(item.getNumberedViewName(cnt)).append("\r\n");
                }

                msg.append("\r\n");

                L1ItemInstance adena = shop.getInventory().findItemId(L1ItemId.ADENA);

                if (adena != null) {
                    msg.append("\\fY").append("---------- ???????????? ----------").append("\r\n").append("\r\n");
                    msg.append("\\fU").append(NumberFormat.getInstance().format(adena.getCount())).append(" ?????????");
                }

                pc.sendPackets(msg.toString());
            } else if (type.equalsIgnoreCase("??????")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("?????? ???????????? ????????? ???????????????");
                    return true;
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("????????? ???????????? ???????????????");
                    return true;
                }

                L1ItemInstance adena = shop.getInventory().findItemId(L1ItemId.ADENA);

                if (adena == null) {
                    pc.sendPackets("????????? ???????????? ????????????");
                    return true;
                }

                shop.getInventory().tradeItem(adena, adena.getCount(), pc.getInventory());
                pc.sendPackets("\\fU" + NumberFormat.getInstance().format(adena.getCount()) + "??? ?????????????????????");
                L1UserShopTable.getInstance().updateShopLoc(0, shop.getMasterObjId());
            } else if (type.equalsIgnoreCase("??????")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("?????? ???????????? ????????? ???????????????");
                    return true;
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("????????? ???????????? ???????????????");
                    return true;
                }

                List<L1UserShop> buyItems = L1UserShopTable.getInstance().selectUserShopBuyList(pc.getId());

                for (L1UserShop o : buyItems) {
                    if (o.getItemObjectId() == 0) {
                        continue;
                    }

                    L1ItemInstance item = shop.getInventory().findItemObjId(o.getItemObjectId());

                    if (item == null) {
                        continue;
                    }

                    shop.getInventory().tradeItem(item, item.getCount(), pc.getInventory());

                    pc.sendPackets("\\fW[??????] : " + item.getNumberedViewName(item.getCount()) + "??? ?????????????????????");
                }

                shop.getBuyList().clear();

                L1UserShopTable.getInstance().deleteShopBuy(pc.getId());
                L1UserShopTable.getInstance().deleteShopBuyItem(pc.getId());

                pc.sendPackets("????????? ?????????????????????");
            } else if (type.equalsIgnoreCase("????????????")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("?????? ???????????? ????????? ???????????????");
                    return true;
                }
            } else if (type.equalsIgnoreCase("??????")) {
                if (pc.isGm()) {
                    String name = nt.nextToken();

                    L1PcInstance target = CharacterTable.getInstance().restoreCharacter(name);

                    if (target == null) {
                        pc.sendPackets("?????? ??????????????????");
                        return true;
                    }

                    L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(target);

                    if (shop == null) {
                        pc.sendPackets("????????? ???????????? ????????????");
                        return true;
                    }

                    shop.closeShop();
                } else {
                    throw new Exception();
                }
            } else if (type.equalsIgnoreCase("??????")) {
                if (pc.isGm()) {
                    String name = nt.nextToken();

                    L1PcInstance target = CharacterTable.getInstance().restoreCharacter(name);

                    if (target == null) {
                        pc.sendPackets("?????? ??????????????????");
                        return true;
                    }

                    L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(target);

                    if (shop == null) {
                        pc.sendPackets("????????? ???????????? ????????????");
                        return true;
                    }
                } else {
                    throw new Exception();
                }
            } else if (type.equalsIgnoreCase("??????")) {
                StringBuilder chat = new StringBuilder();

                while (nt.hasMoreElements()) {
                    chat.append(nt.nextToken()).append(" ");
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("????????? ???????????? ???????????????");
                    return true;
                }

                String msg = chat.toString();

                if (msg.length() > 20) {
                    pc.sendPackets("?????? ????????? ?????? ?????????");

                    return true;
                }

                shop.setChat(msg.getBytes());
                shop.sendPackets(new S_DoActionShop(pc.getId(), L1ActionCodes.ACTION_Shop, msg.getBytes()));
                Broadcaster.broadcastPacket(pc, new S_DoActionShop(pc.getId(), L1ActionCodes.ACTION_Shop, msg.getBytes()));
                L1UserShopTable.getInstance().updateShopChat(shop.getMasterObjId(), chat.toString());
            }
        } catch (Exception e) {
            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_shophelp"));
        }

        return true;
    }

    private void buff(L1PcInstance pc) {
        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON};
        if (pc.isDead())
            return;

        if (pc.getLevel() <= CodeConfig.MAX_SELF_BUFF_LEVEL) {
            try {
                for (int value : allBuffSkill) {
                    L1SkillUse skillUse = new L1SkillUse(pc, value, pc.getId(), pc.getX(), pc.getY(), 0);
                    skillUse.run();
                }
            } catch (Exception e) {
                logger.error("??????", e);
            }
        } else {
            pc.sendPackets(new S_ChatPacket(pc, "Lv:" + CodeConfig.MAX_SELF_BUFF_LEVEL + " ????????? ????????? ????????? ????????????.", L1Opcodes.S_OPCODE_MSG, 15));
        }
    }

    public boolean bloodParty(L1PcInstance pc) {
        if (!pc.getTimer().isTimeOver("bloodParty")) {
            pc.sendPackets(new S_SystemMessage("?????? ????????? ??? ????????????."));

            return true;
        }

        int clanId = pc.getClanId();

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan == null) {
            pc.sendPackets(new S_SystemMessage("????????? ???????????? ?????? ????????? ??? ????????????."));
            return true;
        }

        if (pc.isDead()) {
            pc.sendPackets(new S_SystemMessage("?????? ???????????? ????????? ??? ????????????."));
            return true;
        }

        if (pc.isInParty()) {
            if (!pc.getParty().isLeader(pc)) {
                pc.sendPackets("????????? ????????? ????????? ????????????.");
                return true;
            }
        }

        for (L1PcInstance member : clan.getOnlineClanMember()) {
            if (member.getClanId() != clanId) {
                continue;
            }

            if (member.getName().equals(pc.getName())) {
                continue;
            }

            if (member.isInParty()) {
                pc.sendPackets(new S_SystemMessage("[" + member.getName() + "]??? ?????? ?????? ??????????????????."));
                continue;
            }

            member.setPartyID(pc.getId()); // ??????????????? ??????
            member.sendPackets(new S_Message_YN(954, pc.getName())); // ???????????? ??????
            pc.sendPackets(new S_SystemMessage("????????? [" + member.getName() + "]?????? ????????? ??????????????????."));
        }

        pc.getTimer().setWaitTime("bloodParty", 3000);

        return true;
    }

    private void remoteParty(L1PcInstance pc, StringTokenizer nt) {
        try {
            String targetName = nt.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(targetName);

            if (target == null) {
                pc.sendPackets(new S_SystemMessage("???????????? ??????????????? ????????????"));
                return;
            }

            if (target.equals(pc)) {
                pc.sendPackets(new S_SystemMessage("???????????? ????????? ????????? ??? ????????????"));

                return;
            }

            if (pc.isDead()) {
                pc.sendPackets(new S_SystemMessage("?????? ???????????? ????????? ??? ????????????."));
                return;
            }

            if (target.isInParty()) {
                pc.sendPackets(new S_SystemMessage("[" + target.getName() + "]??? ?????? ?????? ??????????????????."));
                return;
            }

            if (pc.isInParty()) {
                if (!pc.getParty().isLeader(pc)) {
                    pc.sendPackets("????????? ????????? ????????? ????????????.");
                    return;
                }
            }

            target.setPartyID(pc.getId()); // ??????????????? ??????
            target.sendPackets(new S_Message_YN(954, pc.getName())); // ???????????? ??????
            pc.sendPackets(new S_SystemMessage("[" + target.getName() + "]?????? ????????? ??????????????????."));
        } catch (Exception e) {
            pc.sendPackets(".??? ????????????");
        }
    }
}
