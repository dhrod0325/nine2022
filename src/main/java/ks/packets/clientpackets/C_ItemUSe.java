package ks.packets.clientpackets;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.commands.gm.GmCommands;
import ks.constants.*;
import ks.core.datatables.BeginnerTable;
import ks.core.datatables.MapFixKeyTable;
import ks.core.datatables.PolyTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.buff.CharBuffTable;
import ks.core.datatables.huktBook.HuntBookTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.itemMsg.ItemMsg;
import ks.core.datatables.itemMsg.ItemMsgTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pet.PetTable;
import ks.core.datatables.slotSave.SlotSaveTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.board.S_Board;
import ks.model.bookMark.L1BookMarkTable;
import ks.model.instance.*;
import ks.model.item.function.OmanRandomAmulet;
import ks.model.item.function.potion.HealingPotion;
import ks.model.map.L1Map;
import ks.model.pc.L1AutoAttack;
import ks.model.pc.L1PcInstance;
import ks.model.poison.L1DamagePoison;
import ks.model.skill.L1SkillUse;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.system.event.TimePickupEventManager;
import ks.util.L1CharPosUtils;
import ks.util.L1ClassUtils;
import ks.util.L1CommonUtils;
import ks.util.L1SpawnUtils;
import ks.util.common.NumberUtils;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;

import java.util.*;

import static ks.constants.L1SkillId.*;

public class C_ItemUSe extends ClientBasePacket {
    public C_ItemUSe(byte[] data, L1Client client) {
        super(data);

        int itemObjId = readD();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (L1World.getInstance().getPlayer(pc.getName()) == null) {
            client.disconnect();
            return;
        }

        if (pc.getOnlineStatus() == 0) {
            client.disconnect();
            return;
        }

        L1ItemInstance useItem = pc.getInventory().getItem(itemObjId);

        if (useItem == null) {
            return;
        }

        if (useItem.getItem().getUseType() == -1) {
            pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
            return;
        }

        if (pc.isTeleport()) {
            return;
        }

        if (pc.isDead()) {
            return;
        }

        if (pc.isAutoKingBuff()) {
            pc.sendPackets("자동군업 상태입니다");
            return;
        }

        if (!pc.getMap().isUsableItem()) {
            pc.sendPackets(new S_ServerMessage(563));
            pc.tell();

            return;
        }

        int itemId;

        try {
            itemId = useItem.getItemId();
        } catch (Exception e) {
            logger.error("오류", e);
            return;
        }

        ItemMsg itemMsg = ItemMsgTable.getInstance().find(itemId);

        if (itemMsg != null) {
            String key = "itemMsgCheck";

            if (pc.getTimer().isTimeOver(key)) {
                pc.sendGreenMessageAndSystemMessage(itemMsg.getMessage());
                pc.getTimer().setWaitTime(key, 3000);
            }
        }

        logger.debug("itemId:{}", itemId);

        if (useItem.isWorking()) {
            if (pc.getCurrentHp() > 0) {

                if (useItem instanceof HealingPotion) {
                    pc.getAutoPotion().stop();
                }

                useItem.clickItem(pc, this);
            }

            return;
        }

        int targetId = 0;
        int spellObjId = 0;
        int spellX = 0;
        int spellY = 0;

        int useType = useItem.getItem().getUseType();

        if (useType == 14 || useType == 46 || useType == 7) {
            targetId = readD();
        } else if (useType == 30 || itemId == 40870 || itemId == 40879) {
            spellObjId = readD();
        } else if (useType == 5 || useType == 17) {
            spellObjId = readD();
            spellX = readH();
            spellY = readH();
        } else {
            targetId = readC();
        }

        if (pc.getCurrentHp() > 0) {
            if (L1ItemDelay.hasItemDelay(pc, useItem)) {
                return;
            }

            pc.cancelAbsoluteBarrier();

            L1ItemInstance targetItem = pc.getInventory().getItem(targetId);

            if (useItem.getItem().getUseType() == 100) {
                boolean isInTrainingMap = pc.getMapId() >= 1400 && pc.getMapId() < 1500;

                if (!isInTrainingMap) {
                    pc.sendPackets("영혼석은 훈련소 내부에서만 사용 가능합니다");
                    return;
                }

                int x = pc.getX();
                int y = pc.getY();

                if (!(x >= 32876 && x <= 32879 && y >= 32815 && y <= 32818)) {
                    pc.sendPackets("영혼석은 중앙 법진 안에서 사용하셔야 합니다");
                    return;
                }

                int targetNpcId = Integer.parseInt(useItem.getItem().getEtc1());

                L1Npc npc = NpcTable.getInstance().getTemplate(targetNpcId);

                L1Location loc = L1CharPosUtils.getFrontLocation(pc);
                L1SpawnUtils.spawn(loc.getX(), loc.getY(), (short) loc.getMapId(), targetNpcId, 0, 0);

                pc.sendPackets("[영혼석] : " + npc.getName() + "가 스폰되었습니다");
                pc.getInventory().removeItem(useItem, 1);
            } else if (useItem.getItem().getType2() == 0) {
                int min = useItem.getItem().getMinLevel();
                int max = useItem.getItem().getMaxLevel();

                if ((min != 0) && (min > pc.getLevel())) {
                    pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
                    return;
                } else if ((max != 0) && (max < pc.getLevel())) {
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_LEVEL_OVER, max));
                    return;
                }

                if (itemId == 40889) {
//                    if (pc.getNoDelayCheck().isDelay()) {
//                        if (pc.getNoDelayCheck().getDelaySkillId() != SHOCK_STUN) {
//                            return;
//                        }
//                    }
                }

                switch (itemId) {
                    case 51093:
                    case 51094:
                    case 51095:
                    case 51096:
                    case 51097:
                    case 51098:
                    case 51099:
                    case 51100:
                        if (pc.getLevel() <= 50) {
                            pc.sendPackets(new S_SystemMessage("50이하 캐릭은 클래스변경을 할 수 없습니다"));
                            return;
                        }

                        if (pc.getClanId() != 0) {
                            pc.sendPackets("혈맹을 먼저 탈퇴하여 주시기 바랍니다.");
                            return;
                        }

                        if (itemId == 51093 && pc.getType() == 0) {
                            pc.sendPackets("당신은 이미 군주 클래스 입니다.");
                            return;
                        } else if (itemId == 51094 && pc.getType() == 1) {
                            pc.sendPackets("당신은 이미 기사 클래스 입니다.");
                            return;
                        } else if (itemId == 51095 && pc.getType() == 2) {
                            pc.sendPackets("당신은 이미 요정 클래스 입니다.");
                            return;
                        } else if (itemId == 51096 && pc.getType() == 3) {
                            pc.sendPackets("당신은 이미 마법사 클래스 입니다.");
                            return;
                        } else if (itemId == 51097 && pc.getType() == 4) {
                            pc.sendPackets("당신은 이미 다크엘프 클래스 입니다.");
                            return;
                        }

                        if (pc.getLevel() != pc.getHighLevel()) {
                            pc.sendPackets("레벨이 따운된 케릭터 입니다. 렙업후 이용해주세요.");
                            return;
                        }

                        int[] Mclass = new int[]{0, 61, 138, 734, 2786, 6658, 6671, 12490};
                        int[] Wclass = new int[]{1, 48, 37, 1186, 2796, 6661, 6650, 12494};


                        if (itemId == 51093 && pc.getType() != 0) {// 군주
                            pc.setType(0);
                        } else if (itemId == 51094 && pc.getType() != 1) { // 기사
                            pc.setType(1);
                        } else if (itemId == 51095 && pc.getType() != 2) { // 요정
                            pc.setType(2);
                        } else if (itemId == 51096 && pc.getType() != 3) { // 마법사
                            pc.setType(3);
                        } else if (itemId == 51097 && pc.getType() != 4) { // 다크엘프
                            pc.setType(4);
                        }

                        if (pc.getSex() == 0) {
                            pc.setClassId(Mclass[pc.getType()]);
                        } else if (pc.getSex() == 1) {
                            pc.setClassId(Wclass[pc.getType()]);
                        }

                        L1CommonUtils.statInit(pc);

                        if (pc.getWeapon() != null) {
                            pc.getInventory().setEquipped(pc.getWeapon(), false);
                        }

                        pc.getInventory().takeoffEquip(945);
                        pc.sendPackets(new S_CharVisualUpdate(pc));

                        for (L1ItemInstance armor : pc.getInventory().getItems()) {
                            if (armor != null && armor.isEquipped()) {
                                pc.getInventory().setEquipped(armor, false);
                            }
                        }

                        pc.sendPackets(new S_DelSkill(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255));
                        L1CommonUtils.deleteSpell(pc);
                        CharBuffTable.delete(pc);

                        L1PolyMorph.doPoly(pc, pc.getClassId(), 0, L1PolyMorph.MORPH_BY_GM);
                        pc.getInventory().removeItem(useItem, 1);
                        pc.save();

                        pc.disconnect(pc.getName() + " 클래스변경 종료");

                        return;
                }

                if (itemId == 40003) {
                    for (L1ItemInstance lightItem : pc.getInventory().getItems()) {
                        if (lightItem.getItem().getItemId() == 40002) {
                            lightItem.setRemainingTime(useItem.getItem().getLightFuel());
                            pc.sendPackets(new S_ItemName(lightItem));
                            pc.sendPackets(new S_ChatPacket(pc, "랜턴에 기름을 새로 채웠습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                            break;
                        }
                    }
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 41036) { // 풀
                    int diaryId = targetItem.getItem().getItemId();
                    if (diaryId >= 41038 && 41047 >= diaryId) {
                        if (RandomUtils.isWinning(100, 10)) {
                            createNewItem(pc, diaryId + 10);
                        } else {
                            pc.sendPackets(new S_ServerMessage(158, targetItem.getName()));
                        }
                        pc.getInventory().removeItem(targetItem, 1);
                        pc.getInventory().removeItem(useItem, 1);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId >= 41048 && 41055 >= itemId) {
                    int logbookId = targetItem.getItem().getItemId();

                    if (logbookId == (itemId + 8034)) {
                        createNewItem(pc, logbookId + 2);
                        pc.getInventory().removeItem(targetItem, 1);
                        pc.getInventory().removeItem(useItem, 1);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 41056 || itemId == 41057) {
                    int logbookId = targetItem.getItem().getItemId();
                    if (logbookId == (itemId + 8034)) {
                        createNewItem(pc, 41058);
                        pc.getInventory().removeItem(targetItem, 1);
                        pc.getInventory().removeItem(useItem, 1);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 437001) {
                    int[] MALE_LIST = new int[]{0, 61, 138, 734, 2786, 6658, 6671};
                    int[] FEMALE_LIST = new int[]{1, 48, 37, 1186, 2796, 6661, 6650};

                    if (pc.getSex() == 0) {
                        pc.setSex(1);
                        pc.setClassId(FEMALE_LIST[pc.getType()]);
                    } else {
                        pc.setSex(0);
                        pc.setClassId(MALE_LIST[pc.getType()]);
                    }

                    pc.getGfxId().setTempCharGfx(pc.getClassId());
                    pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getClassId()));
                    Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getClassId()));
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 60001301) {
                    if ("true".equals(pc.getDataMap().get(L1DataMapKey.PC_ATTACK))) {
                        pc.getDataMap().put(L1DataMapKey.PC_ATTACK, "false");

                        for (L1PcInstance other : L1World.getInstance().getVisiblePlayer(pc)) {
                            if (pc.getId() == other.getId())
                                continue;
                            pc.sendPackets(new S_PinkName(other.getId(), 0));

                        }

                        pc.sendPackets(new S_SystemMessage("PK모드 활성화가 종료 되었습니다."));
                    } else {

                        pc.getDataMap().put(L1DataMapKey.PC_ATTACK, "true");
                        pc.sendPackets(new S_SystemMessage("PK모드 활성화가 시작 되었습니다."));
                    }

                    useItem.setPc(pc);
                    pc.sendPackets(new S_ItemName(useItem));
                } else if (itemId == 60001235) {
                    TimePickupEventManager.getInstance().teleportToPickupEvent(pc);
                    pc.getInventory().removeItem(useItem);
                } else if (itemId == 60001295) {
                    pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_monlist", pc.getFavPolyImgList()));
                } else if (itemId == 6000098) {
                    if (!pc.isEscapable()) {
                        pc.sendPackets(new S_ServerMessage(79));
                        return;
                    }

                    if (!pc.isTeleportAble()) {
                        pc.sendPackets(new S_ServerMessage(79));
                        return;
                    }

                    if (GmCommands.토너먼트상태 != GmCommands.토너먼트종료) {
                        if (RandomUtils.isWinning(100, 50)) {
                            L1Teleport.teleport(pc, 32702, 32880, (short) 90, 5, true);
                        } else {
                            L1Teleport.teleport(pc, 32702, 32910, (short) 90, 5, true);
                        }
                        pc.getInventory().removeItem(useItem, 1);
                    } else {
                        pc.sendPackets("토너먼트로 이동할 수 있는 상태가 아닙니다");
                    }
                } else if (NumberUtils.contains(itemId,
                        L1ItemId.INCRESE_HP_SCROLL,
                        L1ItemId.CHUNSANG_HP_SCROLL,
                        L1ItemId.INCRESE_MP_SCROLL,
                        L1ItemId.CHUNSANG_MP_SCROLL,
                        L1ItemId.INCRESE_ATTACK_SCROLL,
                        L1ItemId.CHUNSANG_ATTACK_SCROLL,
                        L1ItemId.DRAGON_STONE
                )) {
                    int skillId = 0;

                    if (itemId == L1ItemId.INCRESE_HP_SCROLL || itemId == L1ItemId.CHUNSANG_HP_SCROLL) {
                        skillId = STATUS_CASHSCROLL1;
                    } else if (itemId == L1ItemId.INCRESE_MP_SCROLL || itemId == L1ItemId.CHUNSANG_MP_SCROLL) {
                        skillId = STATUS_CASHSCROLL2;
                    } else if (itemId == L1ItemId.INCRESE_ATTACK_SCROLL || itemId == L1ItemId.CHUNSANG_ATTACK_SCROLL || itemId == L1ItemId.DRAGON_STONE) {
                        skillId = STATUS_CASHSCROLL3;
                    }

                    L1SkillUtils.skillByGm(pc, skillId);

                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == L1ItemId.EXP_POTION1
                        || itemId == L1ItemId.EXP_POTION2
                        || itemId == L1ItemId.EXP_POTION3
                        || itemId == L1ItemId.EXP_POTION4) { // 천상의물약

                    if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
                        pc.sendPackets(new S_ServerMessage(698, ""));
                        return;
                    }

                    L1SkillUtils.skillByGm(pc, itemId);

                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 400253) { // 1억아덴교환템
                    if (pc.getInventory().checkItem(40308, 100000000)) { // 1억아덴이 인벤에 있나 체크
                        pc.getInventory().consumeItem(40308, 100000000); // 1억아데나를 소모
                        pc.getInventory().storeItem(400254, 1); // 수표를 1개 생성
                        pc.sendPackets(new S_SystemMessage("\\fY100,000,000 아데나 수표를 얻었습니다.")); // 성공시 멘트전송
                    } else {
                        pc.sendPackets(new S_SystemMessage("100,000,000 아데나가 부족합니다.")); // 실패시 멘트전송

                    }
                } else if (itemId == 400254) { // 1억아덴수표
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 1500000000)) { //15억 이상일때
                        pc.sendPackets(new S_SystemMessage("\\fY보유하신 아데나가 너무 많아 사용하실수 없습니다."));
                    } else {  //
                        pc.getInventory().storeItem(40308, 100000000); // 1억아덴 생성
                        pc.getInventory().consumeItem(400254, 1); // 수표 1개 소모
                        pc.sendPackets(new S_SystemMessage("100,000,000 아데나를 얻었습니다."));
                    }
                } else if (itemId == 54012) { // 경험치물약
                    pc.setExp(pc.getExp() + 2000000);
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 40000018) {
                    List<Integer> checkItems = Arrays.asList(40000019, 40000020, 40000021);
                    List<L1ItemInstance> items = pc.getInventory().getItems();
                    List<L1ItemInstance> targetItems = new LinkedList<>();

                    int totalPrice = 0;

                    for (L1ItemInstance item : items) {
                        if (checkItems.contains(item.getItemId())) {
                            int price = 0;

                            if (item.getItemId() == 40000019) {
                                price = 100;
                            } else if (item.getItemId() == 40000020) {
                                price = 500;
                            } else if (item.getItemId() == 40000021) {
                                price = 1000;
                            }

                            totalPrice += price * item.getCount();

                            targetItems.add(item);
                        }
                    }

                    Collections.reverse(targetItems);

                    if (totalPrice >= 10000) {
                        int removedPrice = 0;

                        for (L1ItemInstance item : targetItems) {
                            int price = 0;

                            if (item.getItemId() == 40000021) {
                                price = 1000;
                            } else if (item.getItemId() == 40000020) {
                                price = 500;
                            } else if (item.getItemId() == 40000019) {
                                price = 100;
                            }

                            int itemCount = item.getCount();

                            for (int i = 0; i < itemCount; i++) {
                                removedPrice += price;

                                pc.getInventory().removeItem(item, 1);

                                if (removedPrice >= 10000) {
                                    break;
                                }
                            }

                            if (removedPrice >= 10000) {
                                break;
                            }
                        }

                        pc.getInventory().storeItem(60001145, 1);
                        pc.sendPackets("후원코인 획득");
                    } else {
                        pc.sendPackets("후원조각 합계가 1만원이 되지않습니다.");
                    }
                } else if (itemId >= 87885 && itemId <= 87889) {
                    if (pc.getMapId() == L1Map.MAP_FISHING || pc.getMapId() == 5153) { // 배틀/인던/낚시터
                        pc.sendPackets(new S_ChatPacket(pc, "이곳에서 변신할수 없습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                        return;
                    }
                } else if (itemId == 60001163) {
                    SlotSaveTable.getInstance().changeEqumentItem(pc.getId());

                    return;
                } else if (itemId == L1AutoAttack.ITEM_ID) {
                    pc.getAutoAttack().useItem();
                    return;
                } else if (itemId == 60001146) {
                    if (targetItem.getBless() != 0) {
                        pc.sendPackets("아무일도 일어나지 않았습니다");
                        return;
                    }

                    targetItem.setBless(1);

                    pc.getInventory().removeItem(useItem, 1);
                    pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
                    pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);
                    pc.saveInventory();
                    pc.sendPackets("장비에 스며든 축복이 제거되었습니다");
                } else if (itemId == 6000094) {
                    if (!targetItem.getItem().isWeapon() || targetItem.getAttrEnchantLevel() <= 0) {
                        pc.sendPackets("아무일도 일어나지 않았습니다");
                        return;
                    }

                    targetItem.setAttrEnchantLevel(0);

                    pc.getInventory().removeItem(useItem, 1);
                    pc.getInventory().updateItem(targetItem, L1PcInventory.COL_ATTRENCHANTLVL);
                    pc.getInventory().saveItem(targetItem, L1PcInventory.COL_ATTRENCHANTLVL);
                    pc.saveInventory();
                    pc.sendPackets("무기에 스며든 속성이 제거되었습니다");
                } else if (itemId == 60001209) {
                    L1CommonUtils.guide(pc);
                } else if (itemId == 400252) { // 라우풀물약 <<<400259
                    int lawful = pc.getLawful() + 3000;

                    if (lawful >= 32767) {
                        lawful = 32767;
                    }

                    pc.setLawful(lawful);
                    pc.sendPackets(new S_ServerMessage(674));
                    pc.getInventory().removeItem(useItem, 1);
                    pc.save();
                    pc.sendPackets(new S_SystemMessage("라우풀성향이 상승하였습니다"));
                } else if (itemId == 60001147) { //랭커 변신 주문서
                    String male = pc.getMaleName();
                    String classType = L1ClassUtils.className(pc);

                    int rank = L1CommonUtils.getRankingByName(pc.getType(), pc.getName());
                    int allRank = L1CommonUtils.getAllRankingByName(pc.getName());

                    if (allRank <= CodeConfig.RANK_POLY_ALL_RANK || rank <= CodeConfig.RANK_POLY_RANK || pc.isGm()) {
                        int polyLevel = 0;

                        int level = pc.getLevel();

                        if (level >= 60 && level < 65) {
                            polyLevel = 60;
                        } else if (level >= 65 && level < 70) {
                            polyLevel = 65;
                        } else if (level >= 70 && level < 75) {
                            polyLevel = 70;
                        } else if (level >= 75 && level < 80) {
                            polyLevel = 75;
                        } else if (level >= 80 && level < 100) {
                            polyLevel = 80;
                        }

                        String polyName = "rangking " + classType + " " + male + polyLevel;

                        L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyName);
                        L1PolyMorph.doPoly(pc, poly.getPolyId(), 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);

                        pc.getInventory().removeItem(useItem, 1);
                    } else {
                        pc.sendPackets("전체랭킹 " + CodeConfig.RANK_POLY_ALL_RANK + "위 또는 클래스랭킹 " + +CodeConfig.RANK_POLY_RANK + "위 안에 들어야만 사용할 수 있습니다");
                    }
                } else if (itemId == 400259) {
                    int lawful = pc.getLawful() - 3000;

                    if (lawful <= -32767) {
                        lawful = -32767;
                    }

                    pc.setLawful(lawful);
                    pc.sendPackets(new S_ServerMessage(674));
                    pc.getInventory().removeItem(useItem, 1);
                    pc.save();
                    pc.sendPackets(new S_SystemMessage("카오틱성향이 상승하였습니다."));
                } else if (itemId == 40066 || itemId == 41413) { // 떡, 월병
                    pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
                    pc.setCurrentMp(pc.getCurrentMp() + (7 + RandomUtils.nextInt(6))); // 7~12
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 40067 || itemId == 41414) { // 쑥떡, 복떡
                    pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
                    pc.setCurrentMp(pc.getCurrentMp() + (15 + RandomUtils.nextInt(16))); // 15~30
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 410002) { // 빛나는 나뭇잎
                    pc.sendPackets(new S_ServerMessage(338, "$1084"));
                    pc.setCurrentMp(pc.getCurrentMp() + 44);
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 40735) { // 용기의 코인
                    pc.sendPackets(new S_ServerMessage(338, "$1084"));
                    pc.setCurrentMp(pc.getCurrentMp() + 60);
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 40042) { // 스피릿 일부
                    pc.sendPackets(new S_ServerMessage(338, "$1084"));
                    pc.setCurrentMp(pc.getCurrentMp() + 50);
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 41404) {
                    pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
                    pc.setCurrentMp(pc.getCurrentMp() + (80 + RandomUtils.nextInt(21))); // 80~100
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 41412) { // 금의 톨즈
                    pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
                    pc.setCurrentMp(pc.getCurrentMp() + (5 + RandomUtils.nextInt(16))); // 5~20
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == L1ItemId.PROTECTION_SCROLL) {
                    if (targetItem.getItem().getType2() != 0 && targetItem.getProtection() == 0) {
                        targetItem.setProtection(1);
                        pc.getInventory().removeItem(useItem, 1);
                        pc.sendPackets(new S_SystemMessage("" + targetItem.getLogName() + "이(가) 마력의 기운이 스며들었습니다."));
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                    pc.getInventory().updateItem(targetItem, L1PcInventory.COL_ENCHANTLVL);
                    pc.getInventory().saveItem(targetItem, L1PcInventory.COL_ENCHANTLVL);
                    pc.saveInventory();
                } else if (itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN || itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN) {
                    int itemId2 = targetItem.getItem().getItemId();

                    if (itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN && itemId2 == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_UP) {
                        if (pc.getInventory().checkItem(L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_UP)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT, 1);
                        }
                    } else if (itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN && itemId2 == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_UP) {
                        if (pc.getInventory().checkItem(L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_UP)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT, 1);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
                    }
                } else if (itemId == L1ItemId.DEVIL_PRESENT_PIECE_DOWN) {
                    int itemId2 = targetItem.getItem().getItemId();

                    if (itemId2 == L1ItemId.DEVIL_PRESENT_PIECE_UP) {
                        if (pc.getInventory().checkItem(L1ItemId.DEVIL_PRESENT_PIECE_UP)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.DEVIL_PRESENT, 1);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
                    }
                }
//                else if (itemId == 5000341) {// 기란감옥 3시간충전
//                    L1TimeDungeonData d = pc.getTimeDungeon().getTimeDungeonDataMap().get(53);
//                    pc.getTimeDungeon().getTimeDungeonDataMap().get(53).setUseSecond(0);
//                    pc.getInventory().consumeItem(5000341, 1);
//                    pc.sendPackets(new S_SystemMessage("" + pc.getName() + "님의 기란 감옥 시간이 충전되었습니다."));
//                    L1TimeDungeonTable.getInstance().saveTimeDungeonData(pc.getId(), d.getMapId(), d.getUseSecond(), new Date());
//                }
                else if (itemId == 437008) {
                    pc.sendPackets(new S_SystemMessage("이아이템은 신녀 유리스에게 가져가면 라우풀은 3천씩 받을수 있습니다."));
                } else if (itemId == 467009) {
                    pc.sendPackets(new S_SystemMessage("[.캐릭명변경] [변경할아이디] 쳐주시면 됩니다."));
                } else if (itemId == 400246) {
                    pc.setKillCount(0);
                    pc.setDeathCount(0);
                    pc.sendPackets(new S_SystemMessage("\\fY[킬데스초기화] : 당신의 승률이 초기화 되었습니다."));
                    pc.save();
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN || itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN) {
                    int itemId2 = targetItem.getItem().getItemId();
                    if (itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN && itemId2 == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_UP) {
                        if (pc.getInventory().checkItem(L1ItemId.LOWER_TIKAL_PRESENT_PIECE_UP)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.CLOSE_LOWER_TIKAL_PRESENT, 1);
                        }
                    } else if (itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN && itemId2 == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_UP) {
                        if (pc.getInventory().checkItem(L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_UP)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT, 1);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 57250) {
                    pc.getBookMark().clear();

                    L1BookMarkTable.deleteByCharId(pc.getId());
                    BeginnerTable.getInstance().addBeginBookMarks(pc);

                    pc.getInventory().removeItem(useItem, 1);

                    pc.sendPackets("[북마크] : 새로운 기억창 내용이 저장되었습니다");
                    pc.sendPackets("[북마크] : 추가 사용한 경우 리스타트 필수");
                } else if (itemId == L1ItemId.TIMECRACK_CORE) { // 균열의 핵
                    int itemId2 = targetItem.getItem().getItemId();
                    if (itemId2 == L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT) {
                        if (pc.getInventory().checkItem(L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.OPEN_LOWER_OSIRIS_PRESENT, 1);
                        }
                    } else if (itemId2 == L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT) {
                        if (pc.getInventory().checkItem(L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.OPEN_HIGHER_OSIRIS_PRESENT, 1);
                        }
                    } else if (itemId2 == L1ItemId.CLOSE_LOWER_TIKAL_PRESENT) {
                        if (pc.getInventory().checkItem(L1ItemId.CLOSE_LOWER_TIKAL_PRESENT)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.OPEN_LOWER_TIKAL_PRESENT, 1);
                        }
                    } else if (itemId2 == L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT) {
                        if (pc.getInventory().checkItem(L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT)) {
                            pc.getInventory().removeItem(targetItem, 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(L1ItemId.OPEN_HIGHER_TIKAL_PRESENT, 1);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 40097 || itemId == 40119 || itemId == 140119 || itemId == 140329) {
                    if (targetItem.getBless() != 2) {
                        pc.sendPackets("아무일도 일어나지 않았습니다");
                    } else {
                        if (!targetItem.isStackable()) {
                            targetItem.setBless(targetItem.getBless() - 1);

                            pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
                            pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);

                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_ServerMessage(155));
                        }
                    }
                } else if (itemId == 60001237) {
                    try {
                        if (pc.isGm()) {
                            L1Character character = (L1Character) L1World.getInstance().findObject(spellObjId);

                            if (character instanceof L1MonsterInstance) {
                                L1MonsterInstance mon = (L1MonsterInstance) character;

                                if (mon.getHp() >= 32767) {
                                    int hp = mon.getHp() - 32767;
                                    mon.setOptionHp(hp);
                                }
                            }

                            character.setCurrentHp(character.getMaxHp());
                            character.setCurrentMp(character.getMaxMp());

                            pc.sendPackets("회복완료");
                        }
                    } catch (Exception ignored) {

                    }
                } else if (itemId == 60001155) {
                    if (targetItem != null) {
                        int cnt = targetItem.getCount();

                        String noMsg = CodeConfig.STACK_OPEN_BOX_ID_LIST_STR() + "에 사용가능합니다";

                        if (!CodeConfig.STACK_OPEN_BOX_ID_LIST().contains(targetItem.getItemId())) {
                            pc.sendPackets(noMsg);
                            return;
                        }

                        if (cnt < 100) {
                            pc.sendPackets("상자개봉막대는 100개 이상의 아이템에만 사용가능합니다");
                            return;
                        }

                        if (!targetItem.isStackable()) {
                            pc.sendPackets("상자개봉막대는 겹치는 아이템에만 사용 가능합니다");
                            return;
                        }

                        for (int i = 0; i < 100; i++) {
                            try {
                                targetItem.clickItem(pc, null);
                            } catch (Exception e) {
                                logger.error(e);
                            }
                        }
                    }
                } else if (itemId >= 830042 && itemId <= 830061) {
                    OmanRandomAmulet.clickItem(pc, itemId, useItem);
                } else if (itemId == 40964) { // 흑마법 가루
                    int historybookId = targetItem.getItem().getItemId();

                    if (historybookId >= 41011 && 41018 >= historybookId) {
                        int per = targetItem.getItem().getRevivalPer();

                        Boolean c = GmCommands.getInstance().isEnchantOnlySuccess(pc.getName());

                        if (c != null) {
                            if (c) {
                                per = 1000;
                            } else {
                                per = 0;
                            }
                        }

                        if (RandomUtils.isWinning(1000, per)) {
                            createNewItem(pc, historybookId + 8);
                            L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 " + targetItem.getName() + "의 봉인을 해제하였습니다");
                        } else {
                            pc.sendPackets(new S_ServerMessage(158, targetItem.getName()));
                        }

                        pc.getInventory().removeItem(targetItem, 1);
                        pc.getInventory().removeItem(useItem, 1);

                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 60001332) {
                    if (targetItem == null) {
                        return;
                    }

                    int step = L1CommonUtils.getDollStep(targetItem.getItemId());

                    if (step == 0) {
                        pc.sendPackets(new S_ServerMessage(79));
                        return;
                    }

                    List<Integer> changed;

                    if (step <= 2) {
                        changed = L1DollItemId.dollMap.get("dollList" + step);
                    } else {
                        if (RandomUtils.isWinning(100, 20)) {
                            changed = L1DollItemId.dollMap.get("dollList" + step + "Bless");
                        } else {
                            changed = L1DollItemId.dollMap.get("dollList" + step + "Normal");
                        }
                    }

                    L1CommonUtils.changeItem(pc, targetItem, new ArrayList<>(changed), -1);

                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == L1ItemId.HUNT_BOOK) {
                    pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_huntbook", HuntBookTable.getInstance().getNameList()));
                } else if (itemId == 60001386) {
                    pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_polyring", pc.getFavPolyImgList()));
                } else if (itemId == 60001345) {
                    L1SkillUtils.skillByGm(pc, L1SkillId.STATUS_LUCK_A);

                    pc.getInventory().removeItem(useItem, 1);
                } else if (NumberUtils.contains(itemId, 60001315, 60001316, 60001317)) {
                    if (spellObjId == pc.getId() && useItem.getItem().getUseType() != 30) {
                        pc.sendPackets(new S_ServerMessage(281));
                        return;
                    }

                    pc.getInventory().removeItem(useItem, 1);

                    if (spellObjId == 0 && useItem.getItem().getUseType() != 0 && useItem.getItem().getUseType() != 26 && useItem.getItem().getUseType() != 27) {
                        return;
                    }

                    pc.cancelAbsoluteBarrier();

                    int skillid = 0;

                    switch (itemId) {
                        case 60001315://글로잉
                            skillid = 5000063;
                            break;
                        case 60001316://샤이닝
                            skillid = 5000062;
                            break;
                        case 60001317://브레이브
                            skillid = 5000061;
                            break;
                    }

                    L1Skills skill = SkillsTable.getInstance().getTemplate(skillid);

                    L1SkillUse skillUse = new L1SkillUse(client.getActiveChar(), skillid, spellObjId, spellX, spellY, skill.getBuffDuration(), L1SkillUse.TYPE_SPELL_SC);
                    skillUse.run();


                } else if (
                        ((itemId >= 40859 && itemId <= 40898) && itemId != 40863)
                                || itemId == 60001148) {
                    if (spellObjId == pc.getId() && useItem.getItem().getUseType() != 30) {
                        pc.sendPackets(new S_ServerMessage(281));
                        return;
                    }

                    pc.getInventory().removeItem(useItem, 1);

                    if (spellObjId == 0 && useItem.getItem().getUseType() != 0 && useItem.getItem().getUseType() != 26 && useItem.getItem().getUseType() != 27) {
                        return;
                    }

                    pc.cancelAbsoluteBarrier();

                    int skillid = itemId - 40858;
                    int buffTime = 0;

                    if (itemId == 60001148) {
                        skillid = 68;
                        buffTime = 32;
                    }

                    if (skillid == 39) {
                        if (!pc.isWizard()) {
                            pc.sendPackets("마나드레인 주문서는 법사만 사용 가능합니다");
                            return;
                        }
                    }

                    L1SkillUse skillUse = new L1SkillUse(client.getActiveChar(), skillid, spellObjId, spellX, spellY, buffTime, L1SkillUse.TYPE_SPELL_SC);
                    skillUse.run();
                } else if (itemId >= 40373 && itemId <= 40384 || itemId >= 40385 && itemId <= 40390) {
                    pc.sendPackets(new S_UseMap(useItem.getId(), useItem.getItem().getItemId()));
                } else if (itemId == 40314 || itemId == 40316) {
                    if (pc.getInventory().checkItem(41160)) {
                        if (withDrawPet(pc, itemObjId)) {
                            pc.getInventory().consumeItem(41160, 1);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 40315) { // 펫의 피리
                    pc.sendPackets(new S_Sound(437));
                    Broadcaster.broadcastPacket(pc, new S_Sound(437));
                    Collection<L1NpcInstance> petList = pc.getPetList().values();
                    for (Object petObject : petList) {
                        if (petObject instanceof L1PetInstance) { // 펫
                            L1PetInstance pet = (L1PetInstance) petObject;
                            pet.call();
                        }
                    }
                } else if (itemId == 40493) { // 매직 플룻
                    pc.sendPackets(new S_Sound(165));
                    Broadcaster.broadcastPacket(pc, new S_Sound(165));

                    for (L1Object visible : pc.getNearObjects().getKnownObjects()) {
                        if (visible instanceof L1GuardianInstance) {
                            L1GuardianInstance guardian = (L1GuardianInstance) visible;

                            if (guardian.getTemplate().getNpcId() == 70850) { // 빵
                                if (createNewItem(pc, 88)) {
                                    pc.getInventory().removeItem(useItem, 1);
                                }
                            }
                        } else if (visible instanceof L1MonsterInstance) {
                            L1MonsterInstance m = (L1MonsterInstance) visible;
                            m.setHate(pc, 0);
                        }
                    }
                } else if (itemId == 40325) {
                    if (pc.getInventory().checkItem(40318, 1)) {
                        int gfxid = 3237 + RandomUtils.nextInt(2);
                        pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
                        pc.getInventory().consumeItem(40318, 1);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 40326) {
                    if (pc.getInventory().checkItem(40318, 1)) {
                        int gfxid = 3229 + RandomUtils.nextInt(3);
                        pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
                        pc.getInventory().consumeItem(40318, 1);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 40327) {
                    if (pc.getInventory().checkItem(40318, 1)) {
                        int gfxid = 3241 + RandomUtils.nextInt(4);
                        pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
                        pc.getInventory().consumeItem(40318, 1);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 40328) {
                    if (pc.getInventory().checkItem(40318, 1)) {
                        int gfxid = 3204 + RandomUtils.nextInt(6);
                        pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
                        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
                        pc.getInventory().consumeItem(40318, 1);
                    } else {
                        pc.sendPackets("마력의돌 1개가 필요합니다");
                    }
                } else if (itemId == 40991) {
                    if (useItem.getCount() >= 5) {
                        pc.getInventory().removeItem(useItem, 5);
                        pc.getInventory().storeItem(203, 1);

                        L1Item item = ItemTable.getInstance().getTemplate(203);

                        pc.sendPackets(item.getName() + "을 획득하였습니다");
                    } else {
                        pc.sendPackets(useItem.getName() + "5개를 모으셔야 앨리스로 변환이 가능합니다");
                    }

                } else if (itemId == L1ItemId.CHARACTER_REPAIR_SCROLL) {
                    SqlUtils.update("UPDATE characters SET LocX=33087,LocY=33399,MapID=4 WHERE account_name=? and MapID not in (99,997,5166)", client.getAccountName());
                    pc.getInventory().removeItem(useItem, 1);
                    pc.sendPackets(new S_SystemMessage("모든 케릭터의 좌표가 정상적으로 복구 되었습니다."));
                } else if (itemId == 40417) {
                    if ((pc.getX() >= 32667 && pc.getX() <= 32673) && (pc.getY() >= 32978 && pc.getY() <= 32984) && pc.getMapId() == 440) {
                        L1Teleport.teleport(pc, 32922, 32812, (short) 430, 5, true);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if ((itemId >= 40289 && itemId <= 40297)
                        || (itemId >= 60001215 && itemId <= 60001224)
                        || itemId == 60001312) {
                    useToiTeleportAmulet(pc, itemId, useItem);
                } else if ((itemId >= 40280 && itemId <= 40288) || itemId == 60001303) {
                    pc.getInventory().removeItem(useItem, 1);

                    L1ItemInstance item = pc.getInventory().storeItem(itemId + 9, 1);

                    if (item != null) {
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    }
                } else if (itemId == 40615) {
                    if ((pc.getX() >= 32701 && pc.getX() <= 32705)
                            && (pc.getY() >= 32894 && pc.getY() <= 32898)
                            && pc.getMapId() == 522) { // 그림자의 신전 1F
                        L1Teleport.teleport(pc, useItem.getItem()
                                .getLocx(), useItem.getItem()
                                .getLocY(), useItem.getItem()
                                .getMapid(), 5, true);
                    } else {

                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 437011 || itemId == 50005) { // 아이템번호..
                    L1CommonUtils.useDragonPerl(pc);

                    pc.getInventory().consumeItem(437011, 1);// 해당아이템 삭제
                    pc.getInventory().consumeItem(50005, 1);// 해당아이템 삭제

                    pc.sendPackets(new S_ServerMessage(1065)); // 드진 멘트
                } else if (itemId == 40616 || itemId == 40782 || itemId == 40783) { // 그림자의 신전 3층의 열쇠
                    if ((pc.getX() >= 32698 && pc.getX() <= 32702) && (pc.getY() >= 32894 && pc.getY() <= 32898) && pc.getMapId() == 523) { // 그림자의 신전 2층
                        L1Teleport.teleport(pc, useItem.getItem().getLocx(), useItem.getItem().getLocY(), useItem.getItem().getMapid(), 5, true);
                    } else {
                        pc.sendPackets(new S_ServerMessage(79));
                    }
                } else if (itemId == 41146) { // 드로몬드의 초대장
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei001"));
                } else if (itemId == 41209) { // 포피레아의 의뢰서
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei002"));
                } else if (itemId == 41210) { // 연마재
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei003"));
                } else if (itemId == 436000) { // 허브
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei004"));
                } else if (itemId == 41212) { // 특제 캔디
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei005"));
                } else if (itemId == 41213) { // 티미의 바스켓
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei006"));
                } else if (itemId == 41214) { // 운의 증거
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei012"));
                } else if (itemId == 41215) { // 지의 증거
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei010"));
                } else if (itemId == 41216) { // 력의 증거
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei011"));
                } else if (itemId == 41222) { // 마슈르
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei008"));
                } else if (itemId == 41223) { // 무기의 파편
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei007"));
                } else if (itemId == 41224) { // 배지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei009"));
                } else if (itemId == 41225) { // 케스킨의 발주서
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei013"));
                } else if (itemId == 41226) { // 파고의 약
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei014"));
                } else if (itemId == 41227) { // 알렉스의 소개장
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei033"));
                } else if (itemId == 41228) { // 율법박사의 부적
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei034"));
                } else if (itemId == 41229) { // 스켈리턴의 머리
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei025"));
                } else if (itemId == 41230) { // 지난에의 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei020"));
                } else if (itemId == 41231) { // 맛티에의 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei021"));
                } else if (itemId == 41233) { // 케이이에의 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei019"));
                } else if (itemId == 41234) { // 뼈가 들어온 봉투
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei023"));
                } else if (itemId == 41235) { // 재료표
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei024"));
                } else if (itemId == 41236) { // 본아챠의 뼈
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei026"));
                } else if (itemId == 41237) { // 스켈리턴 스파이크의 뼈
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei027"));
                } else if (itemId == 41239) { // 브트에의 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei018"));
                } else if (itemId == 41240) { // 페다에의 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei022"));
                } else if (itemId == 41060) { // 노나메의 추천서
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
                } else if (itemId == 41061) { // 조사단의 증서：에르프 지역 두다마라카메
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
                } else if (itemId == 41062) { // 조사단의 증서：인간 지역 네르가바크모
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
                } else if (itemId == 41063) { // 조사단의 증서：정령 지역 두다마라브카
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
                } else if (itemId == 41064) { // 조사단의 증서：오크 지역 네르가후우모
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
                } else if (itemId == 41065) { // 조사단의 증서：조사단장 아트바노아
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
                } else if (itemId == 41356) { // 파룸의 자원 리스트
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
                } else if (itemId == 40701) { // 작은 보물의 지도
                    if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 1) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "firsttmap"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 2) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapa"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 3) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapb"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 4) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "secondtmapc"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 5) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapd"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 6) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmape"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 7) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapf"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 8) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapg"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 9) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmaph"));
                    } else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 10) {
                        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "thirdtmapi"));
                    }
                } else if (itemId == 40663) { // 아들의 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "sonsletter"));
                } else if (itemId == 40630) { // 디에고의 낡은 일기
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "diegodiary"));
                } else if (itemId == 41340) { // 용병단장 티온
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
                } else if (itemId == 41317) { // 랄슨의 추천장
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
                } else if (itemId == 41318) { // 쿠엔의 메모
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
                } else if (itemId == 41329) { // 박제의 제작 의뢰서
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
                            "anirequest"));
                } else if (itemId == 41346) { // 로빈훗드의 메모 1
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinscroll"));
                } else if (itemId == 41347) { // 로빈훗드의 메모 2
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinscroll2"));
                } else if (itemId == 41348) { // 로빈훗드의 소개장
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "robinhood"));
                } else if (itemId == 41007) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll"));
                } else if (itemId == 41009) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll2"));
                } else if (itemId == 41019) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory1"));
                } else if (itemId == 41020) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory2"));
                } else if (itemId == 41021) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory3"));
                } else if (itemId == 41022) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory4"));
                } else if (itemId == 41023) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory5"));
                } else if (itemId == 41024) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory6"));
                } else if (itemId == 41025) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory7"));
                } else if (itemId == 41026) {
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "lashistory8"));
                } else if (itemId == 210087) { // 프로켈의 첫 번째 지령서
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "first_p"));
                } else if (itemId == 210093) { // 실레인의 첫 번째 편지
                    pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "silrein1lt"));
                } else if (itemId == 42501) { // 스톰 워크
                    if (!pc.isGm()) {
                        return;
                    }

                    L1Teleport.teleport(pc, spellX, spellY, pc.getMapId(), pc.getHeading(), true);
                } else if (itemId == 70001243) {
                    L1Object target = L1World.getInstance().findObject(spellObjId);

                    if (target instanceof L1Character) {
                        L1Character targetCha = (L1Character) target;

                        int heading = L1CharPosUtils.targetDirection(pc, target.getX(), target.getY());
                        pc.setHeading(heading);

                        int x = targetCha.getX();
                        int y = targetCha.getY();

                        switch (pc.getHeading()) {
                            case 0:
                                y += 1;
                                break;
                            case 1:
                                x -= 1;
                                y += 1;
                                break;
                            case 2:
                                x -= 1;
                                break;
                            case 3:
                                x -= 1;
                                y -= 1;
                                break;
                            case 4:
                                y -= 1;
                                break;
                            case 5:
                                x += 1;
                                y -= 1;
                                break;
                            case 6:
                                x += 1;
                                break;
                            case 7:
                                x += 1;
                                y += 1;
                                break;
                        }

                        L1Teleport.teleport(pc, x, y, pc.getMapId(), pc.getHeading(), true);
                    }
                } else if (itemId == 50101) { // 위치막대
                    identMapWand(pc, spellX, spellY);
                } else if (itemId == 50102) { // 위치변경막대
                    mapFixKeyWand(pc, spellX, spellY);
                } else if (itemId == 6000095) { // 화살
                    mapFixKeyWand(pc, spellX, spellY, 2);
                } else if (itemId == L1ItemId.CHANGING_PETNAME_SCROLL) {
                    if (targetItem.getItem().getItemId() == 40314 || targetItem.getItem().getItemId() == 40316) {
                        L1Pet petTemplate = PetTable.getInstance().getTemplate(targetItem.getId());
                        L1Npc l1npc = NpcTable.getInstance().getTemplate(petTemplate.getNpcId());
                        petTemplate.setName(l1npc.getName());
                        PetTable.getInstance().storePet(petTemplate);
                        L1ItemInstance item = pc.getInventory().getItem(targetItem.getId());
                        pc.getInventory().updateItem(item);
                        pc.getInventory().removeItem(useItem, 1);
                        pc.sendPackets(new S_ServerMessage(1322, l1npc.getName()));
                        pc.sendPackets(new S_ChangeName(petTemplate.getObjId(), l1npc.getName()));
                        Broadcaster.broadcastPacket(pc, new S_ChangeName(petTemplate.getObjId(), l1npc.getName()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(1164));
                    }
                } else if (itemId == 41260) { // 신
                    for (L1Object object : L1World.getInstance().getVisibleObjects(pc, 3)) {
                        if (object instanceof L1EffectInstance) {
                            if (((L1NpcInstance) object).getTemplate().getNpcId() == 81170) {
                                pc.sendPackets(new S_ServerMessage(1162));
                                return;
                            }
                        }
                    }

                    int[] loc = L1CharPosUtils.getFrontLoc(pc.getX(), pc.getY(), pc.getHeading());
                    L1EffectSpawn.getInstance().spawnEffect(81170, 600000, loc[0], loc[1], pc.getMapId());
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 41345) { // 산성의 유액
                    L1DamagePoison.doInfection(pc, pc, 3000, 5);
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 41315) { // 성수
                    if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
                        pc.sendPackets(new S_ServerMessage(79));
                        return;
                    }
                    if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
                        pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_HOLY_MITHRIL_POWDER);
                    }
                    pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HOLY_WATER, 900 * 1000);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 190));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
                    pc.sendPackets(new S_ServerMessage(1141));
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 41316) { // 신성한 미스리르파우다
                    if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
                        pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도일어나지 않았습니다.
                        return;
                    }
                    if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER)) {
                        pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_HOLY_WATER);
                    }
                    pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HOLY_MITHRIL_POWDER, 900 * 1000);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 190));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(
                            pc.getId(), 190));
                    pc.sendPackets(new S_ServerMessage(1142));
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 60001319) {
                    if (pc.getInventory().checkItem(40308, 100000000)) {
                        pc.getInventory().consumeItem(40308, 100000000);
                        pc.getInventory().storeItem(400254, 1);
                        pc.sendPackets(new S_SystemMessage("아데나 1억원이 수표로 전환 되었습니다."));
                    } else {
                        pc.sendPackets(new S_SystemMessage("100,000,000 아데나가 부족합니다."));
                    }
                } else if (itemId == 41354) { // 신성한 에바의 물
                    if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER)
                            || pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
                        pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도일어나지 않았습니다.
                        return;
                    }
                    pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HOLY_WATER_OF_EVA, 900 * 1000);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 190));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
                    pc.sendPackets(new S_ServerMessage(1140));
                    pc.getInventory().removeItem(useItem, 1);
                } else if (itemId == 60001318) {
                    int[] allBuffSkill = {
                            PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, EARTH_SKIN, ADVANCE_SPIRIT, BRAVE_MENTAL_SINGLE, GLOWING_WEAPON_SINGLE, STATUS_CASHSCROLL3
                    };

                    for (int value : allBuffSkill) {
                        new L1SkillUse(pc, value, pc.getId(), pc.getX(), pc.getY(), 0).run();
                    }
                } else if (itemId == 6000104
                        || itemId == 6000105
                        || itemId == 6000106
                ) {
                    int[] allBuffSkill = {
                            PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, EARTH_SKIN, ADVANCE_SPIRIT, FIRE_WEAPON, BUFF_COIN
                    };

                    for (int value : allBuffSkill) {
                        new L1SkillUse(pc, value, pc.getId(), pc.getX(), pc.getY(), 0).run();
                    }
                } else if (itemId == 6000107
                        || itemId == 6000108
                        || itemId == 6000109
                ) {
                    int[] allBuffSkill = {
                            PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, EARTH_SKIN, ADVANCE_SPIRIT, WIND_SHOT, BUFF_COIN
                    };

                    for (int value : allBuffSkill) {
                        new L1SkillUse(pc, value, pc.getId(), pc.getX(), pc.getY(), 0).run();
                    }
                } else if (itemId == 60001398) { // 버프물약
                    pc.getInventory().removeItem(useItem, 1);

                    int[] allBuffSkill = {
                            PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON
                    };// 힘.덱.블

                    for (int value : allBuffSkill) {
                        new L1SkillUse(pc, value, pc.getId(), pc.getX(), pc.getY(), 0).run();
                    }
                } else if (itemId == 400247) { // 근거리
                    pc.getInventory().removeItem(useItem, 1);

                    int[] allBuffSkill = {
                            PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, EARTH_SKIN, FIRE_WEAPON
                    };// 힘.덱.칼업.어벤.어스.파폰

                    for (int value : allBuffSkill) {
                        new L1SkillUse(pc, value, pc.getId(), pc.getX(), pc.getY(), 0).run();
                    }
                } else if (itemId == 400248) { // 원거리
                    pc.getInventory().removeItem(useItem, 1);
                    int[] allBuffSkill = {
                            PHYSICAL_ENCHANT_STR, PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, EARTH_SKIN, WIND_SHOT
                    };// 힘.덱.칼업.어벤.어스.윈드샷

                    for (int value : allBuffSkill) {
                        new L1SkillUse(pc, value, pc.getId(),
                                pc.getX(), pc.getY(), 0, 0
                        ).run();
                    }
                } else if (itemId == L1ItemId.DRAGON_EMERALD_BOX) {//드래곤의에메랄드상자
                    int[] DRAGONSCALE = new int[]{40393, 40394, 40395, 40396};
                    int bonus = RandomUtils.nextInt(100) + 1;
                    int rullet = RandomUtils.nextInt(100) + 1;
                    L1ItemInstance bonusitem;
                    pc.getInventory().storeItem(L1ItemId.DRAGON_EMERALD, 1);
                    pc.getInventory().removeItem(useItem, 1);
                    if (bonus <= 3) {
                        bonusitem = pc.getInventory().storeItem(DRAGONSCALE[rullet % DRAGONSCALE.length], 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
                    } else if (bonus <= 8) {
                        bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_PEARL, 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
                    } else if (bonus <= 15) {
                        bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_SAPHIRE, 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
                    } else if (bonus <= 25) {
                        bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_RUBY, 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
                    }
                } else if (itemId == L1ItemId.DRAGON_JEWEL_BOX) {//드래곤의보물상자
                    int[] DRAGONSCALE = new int[]{40393, 40394, 40395, 40396};
                    int bonus = RandomUtils.nextInt(100) + 1;
                    int rullet = RandomUtils.nextInt(100) + 1;

                    L1ItemInstance bonusItem;

                    pc.getInventory().storeItem(L1ItemId.DRAGON_DIAMOND, 1);
                    pc.sendPackets(new S_ServerMessage(403, "$7969"));
                    pc.getInventory().removeItem(useItem, 1);
                    if (bonus <= 3) {
                        bonusItem = pc.getInventory().storeItem(DRAGONSCALE[rullet % DRAGONSCALE.length], 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusItem.getItem().getNameId()));
                    } else if (bonus <= 8) {
                        bonusItem = pc.getInventory().storeItem(L1ItemId.DRAGON_PEARL, 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusItem.getItem().getNameId()));
                    } else if (bonus <= 15) {
                        bonusItem = pc.getInventory().storeItem(L1ItemId.DRAGON_SAPHIRE, 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusItem.getItem().getNameId()));
                    } else if (bonus <= 25) {
                        bonusItem = pc.getInventory().storeItem(L1ItemId.DRAGON_RUBY, 1);
                        pc.sendPackets(new S_ServerMessage(403, bonusItem.getItem().getNameId()));
                    }
                } else if (itemId == 4500011) { // 아이템 번호
                    for (L1Object obj : L1World.getInstance().getAllObject()) {
                        if (obj instanceof L1BoardInstance) {
                            L1NpcInstance board = (L1NpcInstance) obj;
                            if (board.getTemplate().getNpcId() == 4500300) {//게시판 번호
                                pc.sendPackets(new S_Board(board, pc, 0));
                            }
                        }
                    }
                } else if (itemId == 7060) { // 발록봉헌주문서(우호도)
                    if (pc.getKarma() <= 10000000) {
                        pc.setKarma(pc.getKarma() + 100000);
                        pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 우호도가 향상되었습니다."));
                        pc.getInventory().removeItem(useItem, 1);
                    } else
                        pc.sendPackets(new S_SystemMessage("8단계 이하에서만 사용할 수 있습니다."));
                    pc.sendUhodo();// 우호도표기
                } else if (itemId == 7061) { // 야히봉헌주문서(우호도)
                    if (pc.getKarma() >= -10000000) {
                        pc.setKarma(pc.getKarma() - 100000);
                        pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 우호도가 향상되었습니다."));
                        pc.getInventory().removeItem(useItem, 1);
                    } else
                        pc.sendPackets(new S_SystemMessage("8단계 이하에서만 사용할 수 있습니다."));
                    pc.sendUhodo();// 우호도표기
                } else if (itemId == 57247) {//기억 확장 구슬
                    if (pc.getMarkCount() < 100) {
                        int booksize = pc.getMarkCount() + 10;
                        pc.setMarkCount(booksize);
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.BOOKMARK_SIZE_PLUS_10, booksize));
                        pc.getInventory().removeItem(useItem, 1);
                        pc.save();
                    } else {
                        pc.sendPackets(new S_ServerMessage(2930));
                    }
                } else if (itemId == 60001184) {
                    Integer[] gfxIds = new Integer[]{
                            6818,
                            6823,
                            6822,
                            6825,
                            6808,
                            6806,
                            6809
                    };

                    LineageAppContext.commonTaskScheduler().execute(() -> {
                        for (int i = 0; i < 100; i++) {
                            useItem.setGfxId(gfxIds[RandomUtils.nextInt(gfxIds.length)]);
                            try {
                                Thread.sleep(100 + (i * 2));
                            } catch (InterruptedException e) {
                                logger.error("오류", e);
                            }

                            pc.sendPackets(new S_DeleteInventoryItem(useItem));
                            pc.sendPackets(new S_AddItem(useItem));
                        }
                    });
                } else {
                    int locX = useItem.getItem().getLocx();
                    int locY = useItem.getItem().getLocY();
                    short mapId = useItem.getItem().getMapid();

                    if (locX != 0 && locY != 0) {
                        if (pc.isEscapable()) {
                            if (pc.isHuntMapAndNoHunt(mapId)) {
                                return;
                            }
                            L1Teleport.teleport(pc, locX, locY, mapId, pc.getHeading(), true);
                            if (useItem.getItem().getItemId() != 5000297) {
                                pc.getInventory().removeItem(useItem, 1);
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(647));
                        }

                    } else {
                        if (useItem.getCount() < 1) {
                            pc.sendPackets(new S_ServerMessage(329, useItem.getLogName()));
                        } else {
                            pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
                        }
                    }
                }
            }

            L1ItemDelay.onItemUse(pc, useItem); // 아이템 지연 개시
        }
    }

    private boolean createNewItem(L1PcInstance pc, int itemId) {
        L1ItemInstance item = ItemTable.getInstance().createItem(itemId);

        if (item != null) {
            item.setCount(1);

            if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                pc.getInventory().storeItem(item);
            } else {
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            return true;
        } else {
            return false;
        }
    }

    private void useToiTeleportAmulet(L1PcInstance pc, int itemId, L1ItemInstance item) {
        boolean isTeleport = false;

        if ((itemId >= 40289 && itemId <= 40297) || (itemId >= 60001215 && itemId <= 60001224)) {
            if (pc.isEscapable()) {
                isTeleport = true;
            }
        }

        if (itemId == 60001312) {
            if (pc.isEscapable()) {
                isTeleport = true;
            }
        }

        if (isTeleport) {
            L1Teleport.teleport(pc, item.getItem().getLocx(), item.getItem().getLocY(), item.getItem().getMapid(), 5, true);
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }

    private boolean withDrawPet(L1PcInstance pc, int itemObjectId) {
        if (!pc.getMap().isTakePets()) {
            pc.sendPackets(new S_ServerMessage(563));
            pc.tell();
            return false;
        }

        int petCost = 0;

        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (L1NpcInstance pet : petList) {
            if (pet instanceof L1PetInstance) {
                if (((L1PetInstance) pet).getItemObjId() == itemObjectId) { // 이미
                    return false;
                }
            }

            petCost += pet.getPetCost();
        }

        int charisma = pc.getAbility().getTotalCha();

        charisma = C_GiveItem.petCharisma(pc, petCost, charisma);

        int petCount = charisma / 6;
        if (petCount <= 0) {
            pc.sendPackets(new S_ServerMessage(489));
            return false;
        }

        L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
        if (l1pet != null) {
            L1Npc npcTemp = NpcTable.getInstance().getTemplate(l1pet.getNpcId());
            L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
            pet.setPetCost(6);
            pet.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
        }
        return true;
    }

    private void identMapWand(L1PcInstance pc, int locX, int locY) {
        pc.sendPackets(new S_SystemMessage("Gab :" + pc.getMap().getOriginalTile(locX, locY) + ",x :" + locX + ",y :" + locY + ", mapId :" + pc.getMapId()));

        if (pc.getMap().isPassable(locX, locY)) {
            pc.sendPackets(new S_EffectLocation(locX, locY, 10));
            Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX, locY, 10));
            pc.sendPackets(new S_SystemMessage("지나갈수 있는곳임"));
        } else {
            pc.sendPackets(new S_SystemMessage("지나갈수 없는곳임"));
        }
    }

    private void mapFixKeyWand(L1PcInstance pc, int locX, int locY, int type) {
        String key = String.valueOf(pc.getMapId()) + locX + locY;

        if (type == 2) {
            fixMap(pc, locX, locY, type);
        } else {
            if (!pc.getMap().isPassable(locX, locY)) {
                if (!MapFixKeyTable.getInstance().isNotPass(key)) {
                    fixMap(pc, locX, locY, type);
                }
            }
        }
    }

    private void fixMap(L1PcInstance pc, int locX, int locY, int type) {
        MapFixKeyTable.getInstance().storeLocFix(locX, locY, pc.getMapId(), type);
        pc.sendPackets(new S_EffectLocation(locX, locY, 1815));
        Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX, locY, 1815));
        pc.sendPackets(new S_SystemMessage("key추가 ,x :" + locX + ",y :" + locY + ", mapId :" + pc.getMapId()));
    }

    private void mapFixKeyWand(L1PcInstance pc, int locX, int locY) {
        mapFixKeyWand(pc, locX, locY, 0);
    }
}
