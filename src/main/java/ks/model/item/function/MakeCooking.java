package ks.model.item.function;

import ks.constants.L1ItemId;
import ks.constants.L1PacketBoxType;
import ks.model.*;
import ks.model.instance.L1EffectInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.COOKING_NOW;

public class MakeCooking extends L1ItemInstance {
    public MakeCooking(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            int c1 = packet.readC();
            int cookNo = packet.readC();

            logger.debug("cooking c1:{},c2:{}", c1, cookNo);

            if (c1 == 0) {
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.COOK_WINDOW, (getItemId() - 41255)));
            } else {
                makeCooking(pc, cookNo);
            }
        }
    }

    private void makeItem(L1PcInstance pc, int[] materials, int goodItem, int noneItem) {
        int chance = RandomUtils.nextInt(100) + 1;

        boolean goodSuccess = pc.getInventory().checkEquipped(490018) ? chance >= 76 : chance >= 91;
        boolean success = chance >= 1 && chance <= 90;

        for (int m : materials) {
            if (!pc.getInventory().checkItem(m)) {
                pc.sendPackets(new S_ServerMessage(1102));
                return;
            }
        }

        for (int m : materials) {
            pc.getInventory().consumeItem(m, 1);
        }

        if (goodSuccess) {
            L1CommonUtils.createNewItem(pc, goodItem, 1);
            pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 6390));
        } else if (success) {
            L1CommonUtils.createNewItem(pc, noneItem, 1);
            pc.sendPackets(new S_SkillSound(pc.getId(), 6392));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 6392));
        } else {
            pc.sendPackets(new S_ServerMessage(1101));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 6394));
        }
    }

    private void makeCooking(L1PcInstance pc, int cookNo) {
        logger.debug("makeCooking");

        boolean isNearFire = false;

        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 3)) {
            if (obj instanceof L1EffectInstance) {
                L1EffectInstance effect = (L1EffectInstance) obj;

                if (effect.getGfxId().getGfxId() == 5943) {
                    isNearFire = true;
                    break;
                }
            }
        }

        logger.debug("isNearFire:{}", isNearFire);

        if (!isNearFire) {
            pc.sendPackets("주변에 장작이 없습니다");
            return;
        }

        if (pc.getMaxWeight() <= pc.getInventory().getWeight()) {
            pc.sendPackets(new S_ServerMessage(1103));
            return;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(COOKING_NOW)) {
            return;
        }

        pc.getSkillEffectTimerSet().setSkillEffect(COOKING_NOW, 3 * 1000);

        switch (cookNo) {
            case 0: // 괴물눈 스테이크
                makeItem(pc, new int[]{40057}, 41285, 41277);
                break;
            case 1: // 곰고기 구이
                makeItem(pc, new int[]{41275}, 41286, 41278);
                break;
            case 2: // 씨호떡
                makeItem(pc, new int[]{41263, 41265}, 41287, 41279);
                break;
            case 3: // 개미다리 치즈구이
                makeItem(pc, new int[]{41274, 41267}, 41288, 41280);
                break;
            case 4: // 과일샐러드
                makeItem(pc, new int[]{40062, 40069, 40064}, 41289, 41281);
                break;
            case 5: // 과일 탕수육
                makeItem(pc, new int[]{40056, 40060, 40061}, 41290, 41282);
                break;
            case 6: // 멧돼지 꼬치 구이
                makeItem(pc, new int[]{41276}, 41291, 41283);
                break;
            case 7: // 버섯 스프
                makeItem(pc, new int[]{40499, 40060}, 41292, 41284);
                break;
            case 8: // 캐비어 카나페
                makeItem(pc, new int[]{49040, 49048}, 49057, 49049);
                break;
            case 9: // 악어 스테이크
                makeItem(pc, new int[]{49041, 49048}, 49058, 49050);
                break;
            case 10: // 터틀드래곤 과자
                makeItem(pc, new int[]{49042, 41265, 49048}, 49059, 49051);
                break;
            case 11: // 키위 패롯 구이
                makeItem(pc, new int[]{49043, 49048}, 49060, 49052);
                break;
            case 12: // 스콜피온 구이
                makeItem(pc, new int[]{49044, 49048}, 49061, 49053);
                break;
            case 13: // 일렉카둠 스튜
                makeItem(pc, new int[]{49045, 49048}, 49062, 49054);
                break;
            case 14: // 거미다리 꼬치 구이
                makeItem(pc, new int[]{49046, 49048}, 49063, 49055);
                break;
            case 15: // 크랩살 스프
                makeItem(pc, new int[]{49047, 40499, 49048}, 49064, 49056);
                break;
            case 16: // 크러스트시안 집게발 구이
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_CRUSTCEA_CLAW, 49048}, L1ItemId.SCOOKFOOD_CRUSTCEA_CLAW_CHARCOAL, L1ItemId.COOKFOOD_CRUSTCEA_CLAW_CHARCOAL);
                break;
            case 17: // 그리폰 구이
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_GRIFFON_FOOD, 49048}, L1ItemId.SCOOKFOOD_GRIFFON_CHARCOAL, L1ItemId.COOKFOOD_GRIFFON_CHARCOAL);
                break;
            case 18: // 코카트리스 스테이크
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_COCKATRICE_TAIL, 49048}, L1ItemId.SCOOKFOOD_COCKATRICE_STEAK, L1ItemId.COOKFOOD_COCKATRICE_STEAK);
                break;
            case 19: // 대왕거북 구이
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_TURTLEKING_FLESH, 49048}, L1ItemId.SCOOKFOOD_TURTLEKING_CHARCOAL, L1ItemId.COOKFOOD_TURTLEKING_CHARCOAL);
                break;
            case 20: // 레서 드래곤 날개꼬치
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_LESSERDRAGON_WING, 49048}, L1ItemId.SCOOKFOOD_LESSERDRAGON_WING_SKEWER, L1ItemId.COOKFOOD_LESSERDRAGON_WING_SKEWER);
                break;
            case 21: // 드레이크 구이
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_DRAKE_FOOD, 49048}, L1ItemId.SCOOKFOOD_DRAKE_CHARCOAL, L1ItemId.COOKFOOD_DRAKE_CHARCOAL);
                break;
            case 22: // 심해어 스튜
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_DEEP_SEA_FISH_FLESH, 49048}, L1ItemId.SCOOKFOOD_DEEP_SEA_FISH_STEW, L1ItemId.COOKFOOD_DEEP_SEA_FISH_STEW);
                break;
            case 23: // 바실리스크 알 스프
                makeItem(pc, new int[]{L1ItemId.COOK_HUB, L1ItemId.COOKSTUFF_BASILISK_EGG, 49048}, L1ItemId.SCOOKFOOD_BASILIST_EGG_SOUP, L1ItemId.COOKFOOD_BASILIST_EGG_SOUP);
                break;
            default:
                break;
        }
    }
}
