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
        } else if (itemInstance.getItem().getName().contains("마법인형")) {
            result += 49 * rate;
        } else if (NumberUtils.contains(itemInstance.getItemId(), 40087, 140087, 240087, 40074, 140074, 240074)) {//축젤 축데이
            result += 48 * rate;
        } else if ((itemId >= 40010 && itemId <= 40029) || itemId == 437011 || itemId == 437010) {
            result += 47 * rate;
        } else if ((itemId >= 41277 && itemId <= 41292) || (itemId >= 49049 && itemId <= 49064) || (itemId >= L1ItemId.NORMAL_COOKFOOD_3RD_START && itemId <= L1ItemId.SPECIAL_COOKFOOD_3RD_END) || (itemId >= 9800 && itemId <= 9803) || itemId == 436000) {
            result += 46 * rate;
        } else if ((itemId >= 76767 && itemId <= 76776) || (itemId >= 5000009 && itemId <= 5000041)) {
            result += 46 * rate;
        } else if (itemId >= 40090 && itemId <= 40094) {
            result += 45 * rate;
        } else if (itemInstance.getItem().getName().startsWith("마법 주문서")) {
            result += (44 * rate) + itemInstance.getItemId();
        } else if (itemInstance.getItem().getName().contains("층 이동 주문서") || NumberUtils.contains(itemInstance.getItemId(), 40804, 42011, 127008, 40809, 42007, 42081, 40113)) {

            if (itemId == 40113) {
                itemId = 42029;
            }

            result += (43 * rate) + itemId;
        } else if (itemInstance.getItem().getName().contains("주문서")) {
            result += (42 * rate) + itemInstance.getItem().getType() + itemInstance.getItem().getMaterial();
        } else {
            if (NumberUtils.contains(itemInstance.getItemId(), 60001209, 60001301, 430005)) {
                result = 600 * rate;
            } else if (NumberUtils.contains(itemInstance.getItemId(), 6000039, 6000042, 6000050, 6000056, 60001163, 60001155, 40328)) {//부적 제일 앞으로 정렬됨
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
            } else if (itemId >= 40044 && itemId <= 40055) {//보석류
                result += 300 * rate + itemId + 40044;
            } else {
                //법서류 순서 정렬
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

        if ("상점".equalsIgnoreCase(cmd)) {
            return shop(pc, nt);
        } else if ("상점이동".equalsIgnoreCase(cmd)) {
            return shopMove(pc, nt);
        } else if ("던전시간".equalsIgnoreCase(cmd)) {
            Map<Integer, L1TimeDungeonData> m = pc.getTimeDungeon().getTimeDungeonDataMap();

            for (L1TimeDungeonData data : m.values()) {
                pc.sendPackets("던전 : " + data.getTimeDungeon().getMapName() + " 남은시간 : " + data.getRemainingMinute() + "분");
            }

            return true;
        } else if ("랭킹".equalsIgnoreCase(cmd)) {
            try {
                String k = "ranking";

                if (!pc.getTimer().isTimeOver(k)) {
                    pc.sendPackets(pc.getTimer().remainingSecond(k) + "초 후에 확인하세요");
                    return true;
                }

                pc.getTimer().setWaitTime(k, 2 * 1000);

                pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_rank"));

            } catch (Exception e) {
                logger.error("오류", e);
            }

            return true;
        } else if ("혈맹".equals(cmd)) {
            return clanInfo(pc, nt);
        } else if ("전쟁".equalsIgnoreCase(cmd)) {
            try {
                String targetName = nt.nextToken();
                L1WarUtils.war(pc, targetName, 0);
            } catch (Exception e) {
                String warName = "혈맹명";

                for (L1Castle castle : CastleTable.getInstance().getCastleTableList()) {
                    if (WarTimeScheduler.getInstance().isNowWar(castle.getId())) {
                        L1Clan c = ClanTable.getInstance().getCastleLeaderClan(castle.getId());
                        warName = c.getClanName();
                        break;
                    }
                }

                pc.sendPackets(".전쟁 " + warName);
            }

            return true;
        } else if ("무인혈맹".equalsIgnoreCase(cmd)) {
            try {
                if (!pc.isCrown()) {
                    pc.sendPackets("혈맹의 군주만 사용가능한 명령어입니다.");

                    return true;
                }

                if (pc.getClanId() == 0) {
                    pc.sendPackets("혈맹을 창설하지 않았습니다");
                    return true;
                }

                String type = nt.nextToken();

                if (type.equalsIgnoreCase("켬")) {
                    pc.getStateMap().setAutoClan(true);
                    pc.sendPackets("무인혈맹이 활성화되었습니다");
                } else if (type.equalsIgnoreCase("끔")) {
                    pc.getStateMap().setAutoClan(false);

                    pc.sendPackets("무인혈맹이 종료되었습니다");
                }
            } catch (Exception e) {
                pc.sendPackets(".무인혈맹 켬/끔");
            }
            return true;
        } else if ("대미지체크".equalsIgnoreCase(cmd)) {
            String type = nt.nextToken();

            if (type.equalsIgnoreCase("켬")) {
                pc.getDataMap().put(L1DataMapKey.DMG_CHECK, "true");
                pc.sendPackets("대미지체크가 활성화 되었습니다");
            } else if (type.equalsIgnoreCase("끔")) {
                pc.getDataMap().put(L1DataMapKey.DMG_CHECK, "false");
                pc.sendPackets("대미지체크가 비활성화 되었습니다");
            }

            return true;
        } else if ("자동사냥".equalsIgnoreCase(cmd)) {
            String type = nt.nextToken();

            if (pc.isGm()) {
                if (type.equalsIgnoreCase("켬")) {
                    L1AutoHuntScheduler.getInstance().add(pc, new L1AutoHuntRandomMoveAi(pc));
                    pc.setAutoHunt(true);
                } else if (type.equalsIgnoreCase("끔")) {
                    L1AutoHuntScheduler.getInstance().remove(pc);
                    pc.setAutoHunt(false);
                }
            }

            return true;
        } else if (cmd.equalsIgnoreCase("혈")) {
            return bloodParty(pc);
        } else if (cmd.equalsIgnoreCase("장비저장")) {
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

                    pc.sendPackets(saveSlot + "번 장비가 저장되었습니다");
                } else {
                    pc.sendPackets(".장비저장 1/2");
                }
            } catch (Exception e) {
                pc.sendPackets(".장비저장 1/2");
            }

            return true;
        } else if (cmd.equalsIgnoreCase("사냥정보")) {
            /*
            Map<Integer, Hunt> huntCheckMap = pc.getHuntCheckMap();

            if (huntCheckMap.isEmpty()) {
                pc.sendPackets("사냥정보가 존재하지 않습니다");
                return true;
            }

            for (Integer h : huntCheckMap.keySet()) {
                Hunt hunt = huntCheckMap.get(h);
                hunt.printInfo(h, pc);
            }

            return true;
             */
        } else if (cmd.equalsIgnoreCase("우호도")) {
            return karamaCheck(pc);
        } else if (cmd.equalsIgnoreCase("자동군업")) {
            try {
                String type = nt.nextToken();

                if (!pc.isCrown()) {
                    pc.sendPackets("군주 클래스만 사용할 수 있는 명령어입니다");
                    return true;
                }

                if (pc.isInParty()) {
                    pc.sendPackets("파티중에는 사용할 수 없습니다");
                    return true;
                }

                if (pc.isInvisible()) {
                    pc.sendPackets("투명상태에서 자동군업을 사용 할 수 없습니다");
                    return true;
                }

                if (pc.isFishing()) {
                    pc.sendPackets("낚시상태에서 자동군업을 사용 할 수 없습니다");
                    return true;
                }

                if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())
                        && !L1CastleLocation.isInCastleInner(pc.getMapId())) {
                    pc.sendPackets("자동군업은 아지트 또는 내성에서만 사용할 수 있습니다");
                    return true;
                }

                if ("끔".equalsIgnoreCase(type)) {
                    pc.setAutoKingBuff(false);
                    pc.sendPackets("자동군업이 종료되었습니다");
                    return true;
                }

                if (pc.isAutoKingBuff()) {
                    pc.sendPackets("이미 자동군업이 동작중입니다 .자동군업 끔 으로 해제후 사용하세요");
                    return true;
                }

                if ("물방".equalsIgnoreCase(type)) {
                    if (!SkillsTable.getInstance().spellCheck(pc.getId(), SHINING_SHILELD)) {
                        pc.sendPackets("샤이닝 실드를 배우지 않았습니다");
                        return true;
                    }

                    L1PolyMorph.undoPoly(pc);
                    pc.setAutoKingBuff(true);
                    pc.sendPackets("자동군업(물방)이 시작 되었습니다");
                    pc.setAutoKingBuffState(type);
                } else if ("마방".equalsIgnoreCase(type)) {
                    if (!SkillsTable.getInstance().spellCheck(pc.getId(), GLOWING_WEAPON)) {
                        pc.sendPackets("글로잉 웨폰을 배우지 않았습니다");
                        return true;
                    }

                    L1PolyMorph.undoPoly(pc);
                    pc.setAutoKingBuff(true);
                    pc.sendPackets("자동군업(마방)이 시작 되었습니다");
                    pc.setAutoKingBuffState(type);
                } else if ("브레이브".equalsIgnoreCase(type)) {
                    L1PolyMorph.undoPoly(pc);
                    pc.setAutoKingBuff(true);
                    pc.sendPackets("자동군업(브레이브멘탈)이 시작 되었습니다");
                    pc.setAutoKingBuffState(type);
                } else {
                    pc.sendPackets("자동군업 물방/마방/브레이브/끔");
                }
            } catch (Exception e) {
                pc.sendPackets("자동군업 물방/마방/브레이브/끔");
            }

            return true;
        } else if (cmd.equalsIgnoreCase("일") ||
                cmd.equalsIgnoreCase("축") ||
                cmd.equalsIgnoreCase("저")) {

            int bless = 1;
            int enchantLvl = 0;

            if (cmd.equalsIgnoreCase("축")) {
                bless = 0;
            } else if (cmd.equalsIgnoreCase("저")) {
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
        } else if (cmd.equalsIgnoreCase("공성전도움말")) {
            try {
                ServerPacket packet = new ServerPacket();
                packet.writeC(L1Opcodes.S_OPCODE_BOARDREAD);
                packet.writeD(0);
                packet.writeS("공성전안내");
                packet.writeS("공성전안내");
                packet.writeS("");

                String msg = "공성전 선포 \r\n";
                msg += "/전쟁 성혈맹명 \r\n";
                msg += "- 군주 변신상태에서 선포 안됩니다 \r\n\n";
                msg += "공성전 진행 \r\n";
                msg += "수호탑파괴 -> 면류관 클릭 \r\n";
                msg += "- 무기 착용하고 면류관 클릭 안됩니다";
                msg += "- 변신상태에서 면류관 클릭 안됩니다";

                packet.writeS(msg);

                pc.sendPackets(packet);
            } catch (Exception e) {
            }
        } else if (cmd.equalsIgnoreCase("후원")) {
            try {
                File mobDataFile = new File("data/support.txt");

                String data = IOUtils.toString(new FileInputStream(mobDataFile), StandardCharsets.UTF_8);

                ServerPacket packet = new ServerPacket();
                packet.writeC(L1Opcodes.S_OPCODE_BOARDREAD);
                packet.writeD(0);
                packet.writeS("후원안내");
                packet.writeS("후원안내");
                packet.writeS("");
                packet.writeS(data);
                pc.sendPackets(packet);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".후원"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("버프")) {
            buff(pc);
            return true;
        } else if (cmd.equalsIgnoreCase("빨")) {
            pc.getAutoPotion().commandAutoPotion("빨", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("농빨")) {
            pc.getAutoPotion().commandAutoPotion("농빨", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("고빨")) {
            pc.getAutoPotion().commandAutoPotion("고빨", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("주")) {
            pc.getAutoPotion().commandAutoPotion("주", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("농주")) {
            pc.getAutoPotion().commandAutoPotion("농주", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("고주")) {
            pc.getAutoPotion().commandAutoPotion("고주", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("말")) {
            pc.getAutoPotion().commandAutoPotion("말", nt);
            return true;
        } else if (cmd.equals("전차보상")) {
            CharacterNextReturnUtils.nextReq(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("농말")) {
            pc.getAutoPotion().commandAutoPotion("농말", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("고말")) {
            pc.getAutoPotion().commandAutoPotion("고말", nt);
            return true;
        } else if (cmd.equalsIgnoreCase("드다")) {
            dragonDiamond(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("진주")) {
            dragonPerl(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("자동물약끔")) {
            pc.getAutoPotion().stop();
            return true;
        } else if (cmd.equalsIgnoreCase("계좌확인")) {
            try {
                AdenBankAccountController.getInstance().checkBankAccount(pc, pc.getAccountName());
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".계좌확인"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("계좌설정")) {
            try {
                String account_no = nt.nextToken();//계좌번호
                String account_name = nt.nextToken();//계좌주
                String bank_name = nt.nextToken();//은행명
                String phone = nt.nextToken();//연락처

                AdenBankAccount v = new AdenBankAccount();
                v.setAccount_id(pc.getAccountName());
                v.setBank_no(account_no);
                v.setBank_owner_name(account_name);
                v.setBank_name(bank_name);
                v.setPhone(phone);

                if (!StringUtils.isEmpty(account_no)) {
                    if (SqlUtils.selectInteger("SELECT count(*) FROM character_blacklist where bankNo=?", account_no) > 0) {
                        L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, true);
                        L1World.getInstance().broadcastServerMessage("[" + pc.getName() + " 감옥] : 블랙리스트");
                        return true;
                    }

                    if (SqlUtils.selectInteger("SELECT count(*) FROM character_blacklist where phone=?", phone) > 0) {
                        L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, true);
                        L1World.getInstance().broadcastServerMessage("[" + pc.getName() + " 감옥] : 블랙리스트");
                        return true;
                    }
                }

                AdenBankAccountController.getInstance().registerBankAccount(pc, v);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".계좌설정 계좌번호 예금주 은행명 연락처"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("판매신청")) {
            try {
                int aden = Integer.parseInt(nt.nextToken());//아덴수량
                int cash = Integer.parseInt(nt.nextToken());//현금수량

                AdenBankAccountController.getInstance().registerAden(pc, aden, cash);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".판매신청 아데나수량 현금수량"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("구매신청")) {
            try {

                if (pc.getLevel() < CodeConfig.ADEN_BUY_MIN_LEVEL) {
                    pc.sendPackets("아덴구매신청 최소레벨은 " + CodeConfig.ADEN_BUY_MIN_LEVEL + "입니다");
                    return true;
                }

                int id = Integer.parseInt(nt.nextToken());
                AdenBankAccountController.getInstance().buyAden(pc, id);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".구매신청 물품번호"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("판매완료")) {
            try {
                int id = Integer.parseInt(nt.nextToken());
                AdenBankAccountController.getInstance().sellAden(pc, id);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".판매완료 물품번호"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("판매취소")) {
            try {
                int id = Integer.parseInt(nt.nextToken());
                AdenBankAccountController.getInstance().cancelAden(pc, id);
            } catch (Exception e) {
                pc.sendPackets(new S_SystemMessage(".판매취소 물품번호"));
            }
            return true;
        } else if (cmd.equalsIgnoreCase("보상저장")) {
            CharacterNextReturnUtils.saveCommand(pc);

            return true;
        } else if (cmd.equalsIgnoreCase("아") || cmd.equalsIgnoreCase("아데나")) {
            try {
                String k = "adenBoardNpcId";

                if (!pc.getTimer().isTimeOver(k)) {
                    pc.sendPackets(pc.getTimer().remainingSecond(k) + "초 후에 확인하세요");
                    return true;
                }

                pc.getTimer().setWaitTime(k, 2 * 1000);

                L1NpcInstance o = L1World.getInstance().getNpc(CodeConfig.ADEN_BOARD_NPC_ID, 2007121683);
                pc.sendPackets(new S_AdenBoard(o.getId()));

                pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, CodeConfig.getAdenaClickMent()));
            } catch (Exception e) {
                logger.error("오류", e);
            }
            return true;
        } else if (cmd.equalsIgnoreCase("수배")) {
            hunt(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("텔렉") || cmd.equalsIgnoreCase("..")) {
            tell(pc);
            return true;
        } else if (cmd.equalsIgnoreCase("원")) {
            remoteParty(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("구슬조회")) {
            checkCharTradeNo(pc, nt);

            return true;
        } else if (cmd.equalsIgnoreCase("인벤정리") || cmd.equalsIgnoreCase("인")) {
            try {
                inventorySetup(pc);

                pc.sendPackets("인벤토리가 정리되었습니다");
            } catch (Exception e) {
                pc.sendPackets(".인/.인벤정리");
            }

            return true;
        } else if (cmd.equalsIgnoreCase("캐릭명변경")) {
            changeName(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("마크")) {
            markView(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("몹드랍")) {
            searchDropList(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("템드랍")) {
            itemDropList(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("맵핵") || cmd.equalsIgnoreCase("라이트")
        ) {
            mapHack(pc, nt);
            return true;
        } else if (cmd.equalsIgnoreCase("드랍멘트")) {
            dropment(pc, nt);

            return true;
        } else if (cmd.equalsIgnoreCase("물약멘트")) {
            try {
                String type = nt.nextToken();

                if (type.equalsIgnoreCase("켬")) {
                    pc.getMent().setPotion(true);
                } else if (type.equalsIgnoreCase("끔")) {
                    pc.getMent().setPotion(false);
                }

                pc.sendPackets("물약멘트 상태 : " + type);
            } catch (Exception e) {
                pc.sendPackets(".물약멘트 켬/끔");
            }
            return true;
        } else if (cmd.equalsIgnoreCase("자동")) {
            handleCommands(pc, "물약멘트 끔");
            handleCommands(pc, "맵핵 켬");
            handleCommands(pc, "마크 켬");
            handleCommands(pc, "드다 켬");
            handleCommands(pc, "진주 켬");

            return true;
        }

        return false;
    }

    private void dropment(L1PcInstance pc, StringTokenizer nt) {
        try {
            String type = nt.nextToken();

            if (type.equalsIgnoreCase("켬")) {
                pc.getMent().setDrop(true);
            } else if (type.equalsIgnoreCase("끔")) {
                pc.getMent().setDrop(false);
            }

            pc.sendPackets("드랍멘트 상태 : " + type);
        } catch (Exception e) {
            pc.sendPackets(".드랍멘트 켬/끔");
        }
    }

    private void dragonDiamond(L1PcInstance pc, StringTokenizer nt) {
        try {
            String onoff = nt.nextToken();

            if (onoff.equals("켬")) {
                pc.setAutoDragonDiamond(true);
                pc.sendPackets("드래곤의다이아몬드 자동 복용이 작동되었습니다");
            } else if (onoff.equals("끔")) {
                pc.setAutoDragonDiamond(false);
                pc.sendPackets("드래곤의다이아몬드 자동 복용이 종료되었습니다");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".드다 [켬 or 끔]"));
        }
    }

    private void dragonPerl(L1PcInstance pc, StringTokenizer nt) {
        try {
            String onoff = nt.nextToken();

            if (onoff.equals("켬")) {
                pc.setAutoDragonPerl(true);
                pc.sendPackets("드래곤의진주 자동 복용이 작동되었습니다");
            } else if (onoff.equals("끔")) {
                pc.setAutoDragonPerl(false);
                pc.sendPackets("드래곤의진주 자동 복용이 종료되었습니다");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".진주 [켬 or 끔]"));
        }
    }

    private boolean karamaCheck(L1PcInstance pc) {
        pc.sendPackets("우호도 : " + pc.getKarma());
        pc.sendPackets("단계 : " + pc.getKarmaLevel() + "단계");
        pc.sendPackets("진영 : " + (pc.getKarma() > 0 ? "발록진영" : "야히진영"));

        return true;
    }

    private void mapHack(L1PcInstance pc, StringTokenizer nt) {
        try {
            if (pc.getSkillEffectTimerSet().hasSkillEffect(DARKNESS, DARK_BLIND)) {
                pc.sendPackets("사용불가능한 상태입니다");
                return;
            }

            String onOff = nt.nextToken();

            if (onOff.equals("켬")) {
                pc.sendPackets(new S_Ability(3, true));
                pc.getDataMap().put(L1DataMapKey.MAP_HACK, "on");
            } else if (onOff.equals("끔")) {
                pc.sendPackets(new S_Ability(3, false));
                pc.getDataMap().put(L1DataMapKey.MAP_HACK, "off");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".맵핵/라이트 [켬 or 끔]"));
        }
    }

    private void itemDropList(L1PcInstance pc, StringTokenizer st) {
        try {
            String itemName = st.nextToken();
            String type = "일";

            if (st.hasMoreTokens()) {
                type = st.nextToken();
            }

            int bless = 1;

            if ("축".equalsIgnoreCase(type)) {
                bless = 0;
            } else if ("저".equalsIgnoreCase(type)) {
                bless = 2;
            }
            List<L1Drop> resultList = findDropList(itemName, bless);

            if (resultList.isEmpty()) {
                pc.sendPackets(new S_SystemMessage("아이템을 발견하지 못했습니다."));
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
            pc.sendPackets(new S_SystemMessage(".템드랍 아이템명 일"));
            pc.sendPackets(new S_SystemMessage(".템드랍 아이템명 축"));
            pc.sendPackets(new S_SystemMessage(".템드랍 아이템명 저"));
            pc.sendPackets(new S_SystemMessage(".템드랍 아이템명 (생략하면일반검색)"));
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
                    pc.sendPackets(new S_SystemMessage("해당 몬스터가 발견되지 않았습니다."));
                    return;
                }
            }

            pc.sendPackets(new S_DropInfo(npcid));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("ex).몹드랍 데스나이트"));
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
                pc.sendPackets(new S_SystemMessage("혈맹에 가입하셔야 합니다."));
                return;
            }

            if (check.equals("켬")) {
                if (CodeConfig.WAR_MARK_ALL) {
                    for (L1Clan otherClan : L1World.getInstance().getAllClans()) {
                        pc.sendPackets(new S_War(typeMarkOn, pc.getClanName(), otherClan.getClanName()));
                    }
                } else {
                    pc.sendPackets(new S_War(typeMarkOn, pc.getClanName(), clan.getClanName()));
                }

                pc.setMarkShow(true);
            } else if (check.equals("끔")) {
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
            pc.sendPackets(new S_SystemMessage(".마크 [켬,끔]"));
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
                    pc.sendPackets("혈맹에 가입중이 아닙니다");
                    return true;
                }

                targetClan = pc.getClan();
            }

            pc.sendPackets("\\fY＊---------------------------------------------------＊");
            pc.sendPackets("  혈맹정보  ");
            pc.sendPackets("\\fY＊---------------------------------------------------＊");
            pc.sendPackets("  혈맹경험치     : " + NumberFormat.getInstance().format(targetClan.getExp()));
            pc.sendPackets("  혈맹원 수      : " + targetClan.getClanMemberList().size());
            pc.sendPackets("  접속 혈맹원 수 : " + targetClan.getOnlineClanMember().size());
            pc.sendPackets("\\fY＊---------------------------------------------------＊");
        } catch (Exception e) {
            if (pc.isGm()) {
                pc.sendPackets(".혈맹 혈맹명");
            }
        }

        return true;
    }

    private void changeName(L1PcInstance pc, StringTokenizer nt) {
        try {
            if (!pc.getInventory().checkItem(467009)) {
                pc.sendPackets("캐릭명 변경 주문서를 가지고 있지 않습니다");
                return;
            }

            if (pc.getLevel() >= 52) {
                if (pc.getClanId() > 0) {
                    pc.sendPackets(new S_SystemMessage("혈맹 탈퇴후 캐릭명을 변경할수 있습니다."));
                    return;
                }

                String name = nt.nextToken();

                if (name.length() > 50 || StringUtils.isEmpty(name)) {
                    return;
                }

                if (L1BadNameUtils.getInstance().isBadName(name)) {
                    pc.sendPackets(new S_SystemMessage("생성 금지된 캐릭명입니다"));
                    return;
                }

                if (L1CommonUtils.isInValidName(name) || L1CommonUtils.isInvalidName(name)) {
                    pc.sendPackets(new S_SystemMessage("올바른 캐릭터명을 입력하세요"));
                    return;
                }

                if (CharacterTable.getInstance().doesCharNameExist(name) || CharacterTradeDao.getInstance().isExistCharTrade(name)) {
                    pc.sendPackets(new S_SystemMessage("동일한 캐릭명이 존재 합니다"));

                    return;
                }

                if (pc.getInventory().consumeItem(467009, 1)) {
                    SqlUtils.update("UPDATE letter set receiver=? WHERE receiver=?", name, pc.getName());
                    SqlUtils.update("UPDATE letter set sender=? WHERE sender=?", name, pc.getName());

                    CharacterTable.getInstance().updateCharName(name, pc.getName());
                    pc.save();

                    String str = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] " + pc.getName() + "  >  " + name + "  [캐릭명변경]";
                    StringBuilder FileName = new StringBuilder("log/changPcName.txt");
                    PrintWriter out;

                    try {
                        out = new PrintWriter(new FileWriter(FileName.toString(), true));
                        out.println(str);
                        out.close();
                    } catch (IOException e) {
                        logger.error("오류", e);
                    }

                    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("(" + pc.getName() + ")님이 캐릭터명을(" + name + ")으로 변경하였습니다."));
                    pc.disconnect(pc.getName() + " 캐릭명 변경");
                } else {
                    pc.sendPackets(new S_SystemMessage("캐릭터명 변경 주문서가 없습니다."));
                }
            } else {
                pc.sendPackets(new S_SystemMessage("Level : [55] 이상 캐릭만 사용 가능합니다"));
            }
        } catch (Exception e) {
            pc.sendPackets(".캐릭명변경 캐릭터명");
        }
    }

    private void checkCharTradeNo(L1PcInstance pc, StringTokenizer nt) {
        try {
            int number = Integer.parseInt(nt.nextToken());

            CharacterTradeInfo info = CharacterTradeDao.getInstance().getInfo(number);

            if (info == null) {
                pc.sendPackets(number + "는 존재하지 않는 구슬번호입니다.");
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
            sb.append("----캐릭구슬 정보-----").append("\n").append("\n");
            sb.append("캐릭명 : ").append(targetPc.getName()).append("\n");
            sb.append("레  벨 : ").append(targetPc.getLevel()).append(".").append(targetPc.getExpPer()).append("\n");
            sb.append("HP/MP : ").append("HP - ").append(targetPc.getMaxHp()).append(" / ").append("MP - ").append(targetPc.getMaxMp()).append("\n");
            sb.append("엘릭서 : ").append(pc.getAbility().getElixirCount()).append(" 복용").append("\n");

            pc.sendPackets(sb.toString());
        } catch (Exception e) {
            pc.sendPackets(".구슬조회 구슬번호");
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
                pc.sendPackets(new S_SystemMessage("잠시후 다시 시도하세요."));
                return;
            }

            if (!pc.getTimer().isTimeOver("tell")) {
                pc.sendPackets(new S_SystemMessage("잠시후 다시 시도하세요."));
                return;
            }

            if (pc.getMapId() == L1Map.MAP_FISHING) {
                pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
                return;
            }

            if (pc.getMapId() == 350) {
                pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
                return;
            }

            if (pc.isDead()) {
                pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
                return;
            }

            if (pc.getMapId() == 5153) {
                L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                return;
            }

            if (pc.isParalyzed()) {
                pc.sendPackets(new S_SystemMessage("마비중 잠수중에는 사용할 수 없습니다."));
                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.SLEEP_SKILLS)
                    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.STUN_SKILLS)
            ) {
                pc.sendPackets("현재 사용이 불가능한 상태 입니다.");
                return;
            }

            if (pc.isSleeped()) {
                pc.sendPackets(new S_SystemMessage("잠수중에는 사용할 수 없습니다."));
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
            pc.sendPackets(".수배 캐릭명");
        }
    }

    private boolean shopMove(L1PcInstance pc, StringTokenizer nt) {
        if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
            pc.sendPackets("시장 안에서만 이용이 가능합니다");
            return true;
        }

        String name = nt.nextToken();

        L1PcInstance target = CharacterTable.getInstance().restoreCharacter(name);

        L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(target);

        if (shop == null) {
            pc.sendPackets(target + "은 상점을 개설하지 않았습니다");
            return true;
        }

        L1Teleport.teleport(pc, shop.getX(), shop.getY(), shop.getMapId(), 5, true);

        return true;
    }

    private boolean shop(L1PcInstance pc, StringTokenizer nt) {
        try {
            L1UserShopCreateHandler handler = L1UserShopCreateHandler.getInstance();

            String type = nt.nextToken();

            if (type.equalsIgnoreCase("판매")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP) {
                    pc.sendPackets("시장 안에서만 이용이 가능합니다");
                    return true;
                }

                handler.startSell(pc);
            } else if (type.equalsIgnoreCase("매입")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP) {
                    pc.sendPackets("시장 안에서만 이용이 가능합니다");
                    return true;
                }

                pc.sendPackets("\\fY상점 매입 아이템명 인챈트 블레스(0:축,1:일,2:저) 속성");
                pc.sendPackets("\\fY속성 불(1~5) 물(6~10) 바람(11~15) 땅(16~20)");

                String shop_buy_enchant = pc.getDataMap().get(L1DataMapKey.SHOP_BUY_ENCHANT);
                String shop_buy_bless = pc.getDataMap().get(L1DataMapKey.SHOP_BUY_BLESS);
                String shop_buy_attr = pc.getDataMap().get(L1DataMapKey.SHOP_BUY_ATTR);

                String itemName = nt.nextToken();

                int enchant = 0;

                if (nt.hasMoreTokens()) {
                    enchant = Integer.parseInt(nt.nextToken());

                    if (enchant > 9) {
                        pc.sendPackets("인챈트는 9까지만 입력이 가능합니다");
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
                        pc.sendPackets("축복은 -1 0 1 중 하나만 선택하셔야 합니다");
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
                        pc.sendPackets("속성은 0~20까지만 입력이 가능합니다");
                        return true;
                    }
                } else if (shop_buy_attr != null) {
                    attrLevel = Integer.parseInt(shop_buy_attr);
                    pc.getDataMap().remove(L1DataMapKey.SHOP_BUY_ATTR);
                }

                handler.startBuy(pc, itemName, enchant, bless, attrLevel);
            } else if (type.equalsIgnoreCase("입금")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("시장 안에서만 이용이 가능합니다");
                    return true;
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                int adena = Integer.parseInt(nt.nextToken());

                shop.pushAdena(pc, adena);
            } else if (type.equalsIgnoreCase("종료")) {
                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("상점을 개설하지 않았습니다");
                    return true;
                }

                shop.closeShop();
            } else if (type.equalsIgnoreCase("상태")) {
                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("상점을 개설하지 않았습니다");
                    return true;
                }

                List<L1PrivateShopSell> sellList = shop.getSellList();

                StringBuilder msg = new StringBuilder();

                msg.append("\r\n").append("\r\n");
                msg.append("\\fY").append("----------- 판매중 -----------").append("\r\n").append("\r\n");
                for (L1PrivateShopSell o : sellList) {
                    o.getItem().setIdentified(true);

                    msg.append("\\fU").append(o.getItem().getNumberedViewName(o.getSellTotalCount() - o.getSellCount()))
                            .append(" : ")
                            .append(NumberFormat.getInstance().format(o.getSellPrice()))
                            .append("아데나")
                            .append("\r\n");
                }

                msg.append("\r\n");

                msg.append("\\fY").append("----------- 매입중 -----------").append("\r\n").append("\r\n");

                List<L1UserShop> k = L1UserShopTable.getInstance().selectUserShopList(pc.getId(), "buy");

                for (L1UserShop o : k) {
                    int cnt = L1UserShopTable.getInstance().selectCurrentBuyCountByUserShop(o);

                    msg.append("\\fU").append(o.getItemViewName()).append(" ").append(cnt).append("/").append(o.getTotalCount())
                            .append(" : ")
                            .append(NumberFormat.getInstance().format(o.getPrice()))
                            .append("아데나")
                            .append("\r\n");
                }

                msg.append("\r\n");

                msg.append("\\fY").append("---------- 매입완료 ----------").append("\r\n").append("\r\n");

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
                    msg.append("\\fY").append("---------- 상점인벤 ----------").append("\r\n").append("\r\n");
                    msg.append("\\fU").append(NumberFormat.getInstance().format(adena.getCount())).append(" 아데나");
                }

                pc.sendPackets(msg.toString());
            } else if (type.equalsIgnoreCase("출금")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("시장 안에서만 이용이 가능합니다");
                    return true;
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("상점을 개설하지 않았습니다");
                    return true;
                }

                L1ItemInstance adena = shop.getInventory().findItemId(L1ItemId.ADENA);

                if (adena == null) {
                    pc.sendPackets("출금할 아데나가 없습니다");
                    return true;
                }

                shop.getInventory().tradeItem(adena, adena.getCount(), pc.getInventory());
                pc.sendPackets("\\fU" + NumberFormat.getInstance().format(adena.getCount()) + "를 출금하였습니다");
                L1UserShopTable.getInstance().updateShopLoc(0, shop.getMasterObjId());
            } else if (type.equalsIgnoreCase("회수")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("시장 안에서만 이용이 가능합니다");
                    return true;
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("상점을 개설하지 않았습니다");
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

                    pc.sendPackets("\\fW[상점] : " + item.getNumberedViewName(item.getCount()) + "를 회수하였습니다");
                }

                shop.getBuyList().clear();

                L1UserShopTable.getInstance().deleteShopBuy(pc.getId());
                L1UserShopTable.getInstance().deleteShopBuyItem(pc.getId());

                pc.sendPackets("매입이 종료되었습니다");
            } else if (type.equalsIgnoreCase("매입종료")) {
                if (pc.getMapId() != L1Map.MAP_USER_SHOP && !pc.isGiranVillage()) {
                    pc.sendPackets("시장 안에서만 이용이 가능합니다");
                    return true;
                }
            } else if (type.equalsIgnoreCase("추방")) {
                if (pc.isGm()) {
                    String name = nt.nextToken();

                    L1PcInstance target = CharacterTable.getInstance().restoreCharacter(name);

                    if (target == null) {
                        pc.sendPackets("없는 사용자입니다");
                        return true;
                    }

                    L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(target);

                    if (shop == null) {
                        pc.sendPackets("상점을 개설하지 않습니다");
                        return true;
                    }

                    shop.closeShop();
                } else {
                    throw new Exception();
                }
            } else if (type.equalsIgnoreCase("리셋")) {
                if (pc.isGm()) {
                    String name = nt.nextToken();

                    L1PcInstance target = CharacterTable.getInstance().restoreCharacter(name);

                    if (target == null) {
                        pc.sendPackets("없는 사용자입니다");
                        return true;
                    }

                    L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(target);

                    if (shop == null) {
                        pc.sendPackets("상점을 개설하지 않습니다");
                        return true;
                    }
                } else {
                    throw new Exception();
                }
            } else if (type.equalsIgnoreCase("채팅")) {
                StringBuilder chat = new StringBuilder();

                while (nt.hasMoreElements()) {
                    chat.append(nt.nextToken()).append(" ");
                }

                L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

                if (shop == null) {
                    pc.sendPackets("상점을 개설하지 않았습니다");
                    return true;
                }

                String msg = chat.toString();

                if (msg.length() > 20) {
                    pc.sendPackets("상점 채팅이 너무 깁니다");

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
                logger.error("오류", e);
            }
        } else {
            pc.sendPackets(new S_ChatPacket(pc, "Lv:" + CodeConfig.MAX_SELF_BUFF_LEVEL + " 이상은 버프를 받을수 없습니다.", L1Opcodes.S_OPCODE_MSG, 15));
        }
    }

    public boolean bloodParty(L1PcInstance pc) {
        if (!pc.getTimer().isTimeOver("bloodParty")) {
            pc.sendPackets(new S_SystemMessage("아직 사용할 수 없습니다."));

            return true;
        }

        int clanId = pc.getClanId();

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan == null) {
            pc.sendPackets(new S_SystemMessage("혈맹이 존재하지 않아 사용할 수 없습니다."));
            return true;
        }

        if (pc.isDead()) {
            pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
            return true;
        }

        if (pc.isInParty()) {
            if (!pc.getParty().isLeader(pc)) {
                pc.sendPackets("당신은 파티의 리더가 아닙니다.");
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
                pc.sendPackets(new S_SystemMessage("[" + member.getName() + "]은 이미 파티 가입중입니다."));
                continue;
            }

            member.setPartyID(pc.getId()); // 파티아이디 설정
            member.sendPackets(new S_Message_YN(954, pc.getName())); // 분패파티 신청
            pc.sendPackets(new S_SystemMessage("당신은 [" + member.getName() + "]에게 파티를 신청했습니다."));
        }

        pc.getTimer().setWaitTime("bloodParty", 3000);

        return true;
    }

    private void remoteParty(L1PcInstance pc, StringTokenizer nt) {
        try {
            String targetName = nt.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(targetName);

            if (target == null) {
                pc.sendPackets(new S_SystemMessage("상대방이 접속해있지 않습니다"));
                return;
            }

            if (target.equals(pc)) {
                pc.sendPackets(new S_SystemMessage("자신에게 파티를 신청할 수 없습니다"));

                return;
            }

            if (pc.isDead()) {
                pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
                return;
            }

            if (target.isInParty()) {
                pc.sendPackets(new S_SystemMessage("[" + target.getName() + "]은 이미 파티 가입중입니다."));
                return;
            }

            if (pc.isInParty()) {
                if (!pc.getParty().isLeader(pc)) {
                    pc.sendPackets("당신은 파티의 리더가 아닙니다.");
                    return;
                }
            }

            target.setPartyID(pc.getId()); // 파티아이디 설정
            target.sendPackets(new S_Message_YN(954, pc.getName())); // 분패파티 신청
            pc.sendPackets(new S_SystemMessage("[" + target.getName() + "]에게 파티를 신청했습니다."));
        } catch (Exception e) {
            pc.sendPackets(".원 캐릭터명");
        }
    }
}
