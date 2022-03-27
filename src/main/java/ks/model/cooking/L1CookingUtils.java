package ks.model.cooking;

import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;

import java.util.ArrayList;
import java.util.List;

import static ks.constants.L1SkillId.*;

public class L1CookingUtils {
    public static boolean isCookingSkill(int skillId) {
        return getCookingSkillIdList().contains(skillId);
    }

    public static List<Integer> getCookingSkillIdList() {
        List<Integer> result = new ArrayList<>();

        for (int i = COOKING_BEGIN; i <= COOKING_END; i++) {
            result.add(i);
        }

        return result;
    }

    public static int getCookingIdByItemId(int itemId) {
        int cookingId = 0;

        switch (itemId) {
            case 9800: //한우
                cookingId = COOKING_NEW_1;
                break;
            case 9801: //연어
                cookingId = COOKING_NEW_2;
                break;
            case 9802: //칠면조
                cookingId = COOKING_NEW_3;
                break;
            case 9803: //닭고기
                cookingId = COOKING_NEW_4;
                break;
            case 41277:
            case 41285:
                if (itemId == 41277) {
                    cookingId = COOKING_1_0_N;
                } else {
                    cookingId = COOKING_1_0_S;
                }
                break;
            case 41278:
            case 41286:
                if (itemId == 41278) {
                    cookingId = COOKING_1_1_N;
                } else {
                    cookingId = COOKING_1_1_S;
                }
                break;
            case 41279:
            case 41287:
                if (itemId == 41279) {
                    cookingId = COOKING_1_2_N;
                } else {
                    cookingId = COOKING_1_2_S;
                }
                break;
            case 41280:
            case 41288:
                if (itemId == 41280) {
                    cookingId = COOKING_1_3_N;
                } else {
                    cookingId = COOKING_1_3_S;
                }
                break;
            case 41281:
            case 41289:
                if (itemId == 41281) {
                    cookingId = COOKING_1_4_N;
                } else {
                    cookingId = COOKING_1_4_S;
                }
                break;
            case 41282:
            case 41290:
                if (itemId == 41282) {
                    cookingId = COOKING_1_5_N;
                } else {
                    cookingId = COOKING_1_5_S;
                }
                break;
            case 41283:
            case 41291:
                if (itemId == 41283) {
                    cookingId = COOKING_1_6_N;
                } else {
                    cookingId = COOKING_1_6_S;
                }
                break;
            case 41284:
            case 41292:
                if (itemId == 41284) {
                    cookingId = COOKING_1_7_N;
                } else {
                    cookingId = COOKING_1_7_S;
                }
                break;
            case 49049:
            case 49057:
                if (itemId == 49049) {
                    cookingId = COOKING_1_8_N;
                } else {
                    cookingId = COOKING_1_8_S;
                }
                break;
            case 49050:
            case 49058:
                if (itemId == 49050) {
                    cookingId = COOKING_1_9_N;
                } else {
                    cookingId = COOKING_1_9_S;
                }
                break;
            case 49051:
            case 49059:
                if (itemId == 49051) {
                    cookingId = COOKING_1_10_N;
                } else {
                    cookingId = COOKING_1_10_S;
                }
                break;
            case 49052:
            case 49060:
                if (itemId == 49052) {
                    cookingId = COOKING_1_11_N;
                } else {
                    cookingId = COOKING_1_11_S;
                }
                break;
            case 49053:
            case 49061:
                if (itemId == 49053) {
                    cookingId = COOKING_1_12_N;
                } else {
                    cookingId = COOKING_1_12_S;
                }
                break;
            case 49054:
            case 49062:
                if (itemId == 49054) {
                    cookingId = COOKING_1_13_N;
                } else {
                    cookingId = COOKING_1_13_S;
                }
                break;
            case 49055:
            case 49063:
                if (itemId == 49055) {
                    cookingId = COOKING_1_14_N;
                } else {
                    cookingId = COOKING_1_14_S;
                }
                break;
            case 49056:
            case 49064:
                if (itemId == 49056) {
                    cookingId = COOKING_1_15_N;
                } else {
                    cookingId = COOKING_1_15_S;
                }
                break;
            case L1ItemId.COOKFOOD_CRUSTCEA_CLAW_CHARCOAL:
            case L1ItemId.SCOOKFOOD_CRUSTCEA_CLAW_CHARCOAL:
                if (itemId == L1ItemId.COOKFOOD_CRUSTCEA_CLAW_CHARCOAL) {
                    cookingId = COOKING_1_16_N;
                } else {
                    cookingId = COOKING_1_16_S;
                }
                break;
            case L1ItemId.COOKFOOD_GRIFFON_CHARCOAL:
            case L1ItemId.SCOOKFOOD_GRIFFON_CHARCOAL:
                if (itemId == L1ItemId.COOKFOOD_GRIFFON_CHARCOAL) {
                    cookingId = COOKING_1_17_N;
                } else {
                    cookingId = COOKING_1_17_S;
                }
                break;
            case L1ItemId.COOKFOOD_COCKATRICE_STEAK:
            case L1ItemId.SCOOKFOOD_COCKATRICE_STEAK:
                if (itemId == L1ItemId.COOKFOOD_COCKATRICE_STEAK) {
                    cookingId = COOKING_1_18_N;
                } else {
                    cookingId = COOKING_1_18_S;
                }
                break;
            case L1ItemId.COOKFOOD_TURTLEKING_CHARCOAL:
            case L1ItemId.SCOOKFOOD_TURTLEKING_CHARCOAL:
                if (itemId == L1ItemId.COOKFOOD_TURTLEKING_CHARCOAL) {
                    cookingId = COOKING_1_19_N;
                } else {
                    cookingId = COOKING_1_19_S;
                }
                break;
            case L1ItemId.COOKFOOD_LESSERDRAGON_WING_SKEWER:
            case L1ItemId.SCOOKFOOD_LESSERDRAGON_WING_SKEWER:
                if (itemId == L1ItemId.COOKFOOD_LESSERDRAGON_WING_SKEWER) {
                    cookingId = COOKING_1_20_N;
                } else {
                    cookingId = COOKING_1_20_S;
                }
                break;
            case L1ItemId.COOKFOOD_DRAKE_CHARCOAL:
            case L1ItemId.SCOOKFOOD_DRAKE_CHARCOAL:
                if (itemId == L1ItemId.COOKFOOD_DRAKE_CHARCOAL) {
                    cookingId = COOKING_1_21_N;
                } else {
                    cookingId = COOKING_1_21_S;
                }
                break;
            case L1ItemId.COOKFOOD_DEEP_SEA_FISH_STEW:
            case L1ItemId.SCOOKFOOD_DEEP_SEA_FISH_STEW:
                if (itemId == L1ItemId.COOKFOOD_DEEP_SEA_FISH_STEW) {
                    cookingId = COOKING_1_22_N;
                } else {
                    cookingId = COOKING_1_22_S;
                }
                break;
            case L1ItemId.COOKFOOD_BASILIST_EGG_SOUP:
            case L1ItemId.SCOOKFOOD_BASILIST_EGG_SOUP:
                if (itemId == L1ItemId.COOKFOOD_BASILIST_EGG_SOUP) {
                    cookingId = COOKING_1_23_N;
                } else {
                    cookingId = COOKING_1_23_S;
                }

                break;
        }

        return cookingId;
    }

    public static int getCookingTypeByCookingId(int cookingId) {
        int cookingType = 0;

        switch (cookingId) {
            case COOKING_NEW_1://한우
            case COOKING_1_0_N:
            case COOKING_1_0_S:
                cookingType = 0;
                break;
            case COOKING_NEW_2://연어
            case COOKING_1_1_N:
            case COOKING_1_1_S:
                cookingType = 1;

                break;
            case COOKING_NEW_3://칠면조
            case COOKING_1_2_N:
            case COOKING_1_2_S:
                cookingType = 2;

                break;
            case COOKING_NEW_4://닭고기 스프
            case COOKING_1_7_N:
            case COOKING_1_7_S:
                cookingType = 7;
                break;
            case COOKING_1_3_N:
            case COOKING_1_3_S:
                cookingType = 3;
                // pc.sendPackets(new S_OwnCharStatus(pc));
                break;
            case COOKING_1_4_N:
            case COOKING_1_4_S:
                cookingType = 4;
                break;
            case COOKING_1_5_N:
            case COOKING_1_5_S:
                cookingType = 5;
                break;
            case COOKING_1_6_N:
            case COOKING_1_6_S:
                cookingType = 6;
                break;
            case COOKING_1_8_N:
            case COOKING_1_8_S:
                cookingType = 16;
                break;
            case COOKING_1_9_N:
            case COOKING_1_9_S:
                cookingType = 17;
                break;
            case COOKING_1_10_N:
            case COOKING_1_10_S:
                cookingType = 18;
                break;
            case COOKING_1_11_N:
            case COOKING_1_11_S:
                cookingType = 19;
                break;
            case COOKING_1_12_N:
            case COOKING_1_12_S:
                cookingType = 20;
                break;
            case COOKING_1_13_N:
            case COOKING_1_13_S:
                cookingType = 21;
                break;
            case COOKING_1_14_N:
            case COOKING_1_14_S:
                cookingType = 22;
                break;
            case COOKING_1_15_N:
            case COOKING_1_15_S:
                cookingType = 23;
                break;
            case COOKING_1_16_N:
            case COOKING_1_16_S:
                cookingType = 45;
                break;
            case COOKING_1_17_N:
            case COOKING_1_17_S:
                cookingType = 46;
                break;
            case COOKING_1_18_N:
            case COOKING_1_18_S:
                cookingType = 47;
                break;
            case COOKING_1_19_N:
            case COOKING_1_19_S:
                cookingType = 48;
                break;
            case COOKING_1_20_N:
            case COOKING_1_20_S:
                cookingType = 49;
                break;
            case COOKING_1_21_N:
            case COOKING_1_21_S:
                cookingType = 50;
                break;
            case COOKING_1_22_N:
            case COOKING_1_22_S:
                cookingType = 51;
                break;
            case COOKING_1_23_N:
            case COOKING_1_23_S:
                cookingType = 52;
                break;
            default:
                break;
        }

        return cookingType;
    }

    public static void startCookingBuff(L1PcInstance pc, int cookingId, int time) {
        switch (cookingId) {
            case COOKING_NEW_1://한우
                pc.addHitUp(1);
                pc.addDmgUp(2);
                pc.addMpr(2);
                pc.addHpr(2);
                pc.getResistance().addAllNaturalResistance(10);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.getResistance().addMr(10);
                pc.sendPackets(new S_SPMR(pc));
                break;
            case COOKING_NEW_2://연어
                pc.addBowHitup(1);
                pc.addBowDmgUp(2);
                pc.addMpr(2);
                pc.addHpr(2);
                pc.getResistance().addAllNaturalResistance(10);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.getResistance().addMr(10);
                pc.sendPackets(new S_SPMR(pc));

                break;
            case COOKING_NEW_3://칠면조
                pc.getAbility().addSp(2);
                pc.addMpr(3);
                pc.addHpr(2);
                pc.getResistance().addAllNaturalResistance(10);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.getResistance().addMr(10);
                pc.sendPackets(new S_SPMR(pc));

                break;
            case COOKING_NEW_4://닭고기 스프
                break;
            case COOKING_1_0_N:
            case COOKING_1_0_S:
                pc.getResistance().addAllNaturalResistance(10);
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                break;
            case COOKING_1_1_N:
            case COOKING_1_1_S:
                pc.addMaxHp(30);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) {
                    pc.getParty().updateMiniHP(pc);
                }
                break;
            case COOKING_1_2_N:
            case COOKING_1_2_S:
                pc.addMpr(3);
                break;
            case COOKING_1_3_N:
            case COOKING_1_3_S:
                pc.getAC().addAc(-1);
                // pc.sendPackets(new S_OwnCharStatus(pc));
                break;
            case COOKING_1_4_N:
            case COOKING_1_4_S:
                pc.addMaxMp(20);
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                break;
            case COOKING_1_5_N:
            case COOKING_1_5_S:
                pc.addHpr(3);
                break;
            case COOKING_1_6_N:
            case COOKING_1_6_S:
                pc.getResistance().addMr(5);
                pc.sendPackets(new S_SPMR(pc));
                break;
            case COOKING_1_8_N:
            case COOKING_1_8_S:
                pc.addHitUp(1);
                pc.addDmgUp(1);
                break;
            case COOKING_1_9_N:
            case COOKING_1_9_S:
                pc.addMaxHp(30);
                pc.addMaxMp(30);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) {
                    pc.getParty().updateMiniHP(pc);
                }
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                break;
            case COOKING_1_10_N:
            case COOKING_1_10_S:
                pc.getAC().addAc(-2);
                pc.sendPackets(new S_OwnCharStatus2(pc));
                break;
            case COOKING_1_11_N:
            case COOKING_1_11_S:
                pc.addBowHitup(1);
                pc.addBowDmgUp(1);
                break;
            case COOKING_1_12_N:
            case COOKING_1_12_S:
                pc.addHpr(2);
                pc.addMpr(2);
                break;
            case COOKING_1_13_N:
            case COOKING_1_13_S:
                pc.getResistance().addMr(10);
                pc.sendPackets(new S_SPMR(pc));
                break;
            case COOKING_1_14_N:
            case COOKING_1_14_S:
                pc.getAbility().addSp(1);
                pc.sendPackets(new S_SPMR(pc));
                break;
            case COOKING_1_16_N:
            case COOKING_1_16_S:
                pc.addBowHitup(2);
                pc.addBowDmgUp(1);
                break;
            case COOKING_1_17_N:
            case COOKING_1_17_S:
                pc.addMaxHp(50);
                pc.addMaxMp(50);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) {
                    pc.getParty().updateMiniHP(pc);
                }
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                break;
            case COOKING_1_18_N:
            case COOKING_1_18_S:
                pc.addHitUp(2);
                pc.addDmgUp(1);
                break;
            case COOKING_1_19_N:
            case COOKING_1_19_S:
                pc.getAC().addAc(-3);
                pc.sendPackets(new S_OwnCharStatus2(pc));
                break;
            case COOKING_1_20_N:
            case COOKING_1_20_S:
                pc.getResistance().addAllNaturalResistance(10);
                pc.getResistance().addMr(15);
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                break;
            case COOKING_1_21_N:
            case COOKING_1_21_S:
                pc.addMpr(2);
                pc.getAbility().addSp(2);
                pc.sendPackets(new S_SPMR(pc));
                break;
            case COOKING_1_22_N:
            case COOKING_1_22_S:
                pc.addHpr(2);
                pc.addMaxHp(30);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) {
                    pc.getParty().updateMiniHP(pc);
                }
                break;
        }

        sendPacket(pc, cookingId, time);
    }

    public static void stopCookingBuff(int skillId, L1Character cha) {
        switch (skillId) {
            case COOKING_1_0_N:
            case COOKING_1_0_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getResistance().addAllNaturalResistance(-10);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 0, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_1_N:
            case COOKING_1_1_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-30);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    if (pc.isInParty()) {
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 1, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_2_N:
            case COOKING_1_2_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(-3);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 2, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_3_N:
            case COOKING_1_3_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getAC().addAc(1);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 3, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_4_N:
            case COOKING_1_4_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-20);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 4, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_5_N:
            case COOKING_1_5_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHpr(-3);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 5, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_6_N:
            case COOKING_1_6_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getResistance().addMr(-5);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 6, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_NEW_4:
            case COOKING_1_7_N:
            case COOKING_1_7_S:
            case COOKING_1_15_N:
            case COOKING_1_15_S:
            case COOKING_1_23_N:
            case COOKING_1_23_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 7, 0));
                    pc.setDessertId(0);
                }
            }
            break;
            case COOKING_1_8_N:
            case COOKING_1_8_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitUp(-1);
                    pc.addDmgUp(-1);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 16, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_9_N:
            case COOKING_1_9_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-30);
                    pc.addMaxHp(-30);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 17, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_10_N:
            case COOKING_1_10_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getAC().addAc(2);
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 18, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_11_N:
            case COOKING_1_11_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(-1);
                    pc.addBowDmgUp(-1);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 19, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_12_N:
            case COOKING_1_12_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHpr(-2);
                    pc.addMpr(-2);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 20, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_13_N:
            case COOKING_1_13_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getResistance().addMr(-10);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 21, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_14_N:
            case COOKING_1_14_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getAbility().addSp(-1);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 22, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_NEW_1: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitUp(-1);
                    pc.addDmgUp(-2);
                    pc.addMpr(-2);
                    pc.addHpr(-2);
                    pc.getResistance().addAllNaturalResistance(-10);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.getResistance().addMr(-10);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 7, 0));
                    pc.setDessertId(0);
                }
            }
            break;
            case COOKING_NEW_2: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(-1);
                    pc.addBowDmgUp(-2);
                    pc.addMpr(-2);
                    pc.addHpr(-2);
                    pc.getResistance().addAllNaturalResistance(-10);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.getResistance().addMr(-10);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 7, 0));
                    pc.setDessertId(0);
                }
            }
            break;
            case COOKING_NEW_3: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getAbility().addSp(-2);
                    pc.addMpr(-3);
                    pc.addHpr(-2);
                    pc.getResistance().addAllNaturalResistance(-10);
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.getResistance().addMr(-10);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 7, 0));
                    pc.setDessertId(0);
                }
            }
            break;
            case COOKING_1_16_N:
            case COOKING_1_16_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addBowHitup(-2);
                    pc.addBowDmgUp(-1);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 45, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_17_N:
            case COOKING_1_17_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-50);
                    pc.addMaxMp(-50);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    if (pc.isInParty()) {
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 46, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_18_N:
            case COOKING_1_18_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitUp(-2);
                    pc.addDmgUp(-1);
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 47, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_19_N:
            case COOKING_1_19_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getAC().addAc(3);
                    pc.sendPackets(new S_OwnCharStatus2(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 48, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_20_N:
            case COOKING_1_20_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.getResistance().addAllNaturalResistance(-10);
                    pc.getResistance().addMr(-15);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 49, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_21_N:
            case COOKING_1_21_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(-2);
                    pc.getAbility().addSp(-2);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 50, 0));
                    pc.setCookingId(0);
                }
            }
            break;
            case COOKING_1_22_N:
            case COOKING_1_22_S: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHpr(-2);
                    pc.addMaxHp(-30);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    if (pc.isInParty()) {
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 51, 0));
                    pc.setCookingId(0);
                }
            }
            break;
        }
    }

    public static void sendPacket(L1PcInstance pc, int cookingId, int time) {
        int cookingType = getCookingTypeByCookingId(cookingId);

        if (cookingType == 7 || cookingType == 23 || cookingType == 52) {
            if (pc.getFood() < 225) {
                pc.getSkillEffectTimerSet().removeSkillEffect(
                        L1SkillId.COOKING_1_7_N, L1SkillId.COOKING_1_7_S,
                        L1SkillId.COOKING_1_15_N, L1SkillId.COOKING_1_15_S,
                        L1SkillId.COOKING_1_23_N, L1SkillId.COOKING_1_23_S
                );

                return;
            }

            pc.sendPackets(new S_PacketBox(L1PacketBoxType.FOOD, pc.getFood()));
        }


        pc.getSkillEffectTimerSet().setSkillEffect(cookingId, time * 1000);
        pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, cookingType, time));

        if (cookingId >= COOKING_1_0_S && cookingId <= COOKING_1_23_S) {
            pc.getSkillEffectTimerSet().setSkillEffect(SPECIAL_COOKING, time * 1000);
        }

        if (cookingId != COOKING_NEW_4) {
            int newCookingTime = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(COOKING_NEW_4);

            if (newCookingTime > 0) {
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.FOOD, pc.getFood()));
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_COOKING, 7, newCookingTime));
            }
        }

        if (cookingId == COOKING_1_7_N
                || cookingId == COOKING_1_7_S
                || cookingId == COOKING_1_15_N
                || cookingId == COOKING_1_15_S
                || cookingId == COOKING_1_23_N
                || cookingId == COOKING_1_23_S
                || cookingId == COOKING_NEW_4) {
            pc.setDessertId(cookingId);
        } else {
            pc.setCookingId(cookingId);
        }

        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    public static boolean isEatItem(int itemId) {
        return itemId == 41284
                || itemId == 49056
                || itemId == 49064
                || itemId == 41292
                || itemId == 9803
                || itemId == L1ItemId.COOKFOOD_BASILIST_EGG_SOUP // 바실리스크 알
                || itemId == L1ItemId.SCOOKFOOD_BASILIST_EGG_SOUP;
    }
}
