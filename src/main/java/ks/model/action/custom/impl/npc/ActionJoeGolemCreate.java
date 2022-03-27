package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.util.L1CommonUtils;

public class ActionJoeGolemCreate extends L1AbstractNpcAction {
    public ActionJoeGolemCreate(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = null;

        if (action.equalsIgnoreCase("A")) {
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 5, 1, 7);
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 6, 1, 7);
            L1CommonUtils.checkItemAndMessage(pc, 41246, 1000);
            L1CommonUtils.checkItemAndMessage(pc, L1ItemId.BRAVE_CRYSTAL, 5);

            if (pc.getInventory().makeCheckEnchant(5, 7)
                    && pc.getInventory().makeCheckEnchant(6, 7)
                    && pc.getInventory().checkItem(41246, 1000)
                    && pc.getInventory().checkItem(L1ItemId.BRAVE_CRYSTAL, 5)) {
                pc.getInventory().makeDeleteEnchant(5, 7);
                pc.getInventory().makeDeleteEnchant(6, 7);
                pc.getInventory().consumeItem(41246, 1000);
                pc.getInventory().consumeItem(L1ItemId.BRAVE_CRYSTAL, 5);
                pc.getInventory().storeItem(412002, 1);
                pc.sendPackets("마력의 단검 제작에 성공하였습니다");

                html = "joegolem9";
            } else {
                html = "joegolem15";
            }
        }
        // 광풍의 도끼
        if (action.equalsIgnoreCase("B")) {
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 145, 1, 7);
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 148, 1, 7);
            L1CommonUtils.checkItemAndMessage(pc, 41246, 1000);
            L1CommonUtils.checkItemAndMessage(pc, L1ItemId.BRAVE_CRYSTAL, 20);

            if (pc.getInventory().makeCheckEnchant(145, 7)
                    && pc.getInventory().makeCheckEnchant(148, 7)
                    && pc.getInventory().checkItem(41246, 1000)
                    && pc.getInventory().checkItem(L1ItemId.BRAVE_CRYSTAL, 20)) {
                pc.getInventory().makeDeleteEnchant(145, 7);
                pc.getInventory().makeDeleteEnchant(148, 7);
                pc.getInventory().consumeItem(41246, 1000);
                pc.getInventory().consumeItem(L1ItemId.BRAVE_CRYSTAL, 20);
                pc.getInventory().storeItem(412005, 1);
                pc.sendPackets("광풍의 도끼 제작에 성공하였습니다");
                html = "joegolem10";
            } else {
                html = "joegolem15";
            }
        }
        // 파멸의 대검
        if (action.equalsIgnoreCase("C")) {
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 52, 1, 7);
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 64, 1, 7);
            L1CommonUtils.checkItemAndMessage(pc, 41246, 1000);
            L1CommonUtils.checkItemAndMessage(pc, L1ItemId.BRAVE_CRYSTAL, 20);

            if (pc.getInventory().makeCheckEnchant(52, 7)
                    && pc.getInventory().makeCheckEnchant(64, 7)
                    && pc.getInventory().checkItem(41246, 1000)
                    && pc.getInventory().checkItem(L1ItemId.BRAVE_CRYSTAL, 20)) {

                pc.getInventory().makeDeleteEnchant(52, 7);
                pc.getInventory().makeDeleteEnchant(64, 7);
                pc.getInventory().consumeItem(41246, 1000);
                pc.getInventory().consumeItem(L1ItemId.BRAVE_CRYSTAL, 20);
                pc.getInventory().storeItem(412001, 1);
                pc.sendPackets("파멸의 대검을 제작에 성공하였습니다");

                html = "joegolem11";
            } else {
                html = "joegolem15";
            }
        }
        // 아크메이지의 지팡이
        if (action.equalsIgnoreCase("D")) {
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 125, 1, 7);
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 129, 1, 7);
            L1CommonUtils.checkItemAndMessage(pc, 41246, 1000);
            L1CommonUtils.checkItemAndMessage(pc, L1ItemId.BRAVE_CRYSTAL, 5);

            if (pc.getInventory().makeCheckEnchant(125, 7)
                    && pc.getInventory().makeCheckEnchant(129, 7)
                    && pc.getInventory().checkItem(41246, 1000)
                    && pc.getInventory().checkItem(L1ItemId.BRAVE_CRYSTAL,
                    5)) {
                pc.getInventory().makeDeleteEnchant(125, 7);
                pc.getInventory().makeDeleteEnchant(129, 7);
                pc.getInventory().consumeItem(41246, 1000);
                pc.getInventory().consumeItem(L1ItemId.BRAVE_CRYSTAL, 5);
                pc.getInventory().storeItem(412003, 1);
                pc.sendPackets("아크메이지의 지팡이 제작에 성공하였습니다");

                html = "joegolem12";
            } else {
                html = "joegolem15";
            }
        }
        // 혹한의 창
        if (action.equalsIgnoreCase("E")) {
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 99, 1, 7);
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 104, 1, 7);
            L1CommonUtils.checkItemAndMessage(pc, 41246, 1000);
            L1CommonUtils.checkItemAndMessage(pc, L1ItemId.BRAVE_CRYSTAL, 20);

            if (pc.getInventory().makeCheckEnchant(99, 7)
                    && pc.getInventory().makeCheckEnchant(104, 7)
                    && pc.getInventory().checkItem(41246, 1000)
                    && pc.getInventory().checkItem(L1ItemId.BRAVE_CRYSTAL,
                    20)) {
                pc.getInventory().makeDeleteEnchant(99, 7);
                pc.getInventory().makeDeleteEnchant(104, 7);
                pc.getInventory().consumeItem(41246, 1000);
                pc.getInventory().consumeItem(L1ItemId.BRAVE_CRYSTAL, 20);
                pc.getInventory().storeItem(412004, 1);
                pc.sendPackets("혹한의 창 제작에 성공하였습니다");

                html = "joegolem13";
            } else {
                html = "joegolem15";
            }
        }
        // 뇌신검
        if (action.equalsIgnoreCase("F")) {
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 32, 1, 7);
            L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, 42, 1, 7);
            L1CommonUtils.checkItemAndMessage(pc, 41246, 1000);
            L1CommonUtils.checkItemAndMessage(pc, L1ItemId.BRAVE_CRYSTAL, 5);

            if (pc.getInventory().makeCheckEnchant(32, 7)
                    && pc.getInventory().makeCheckEnchant(42, 7)
                    && pc.getInventory().checkItem(41246, 1000)
                    && pc.getInventory().checkItem(L1ItemId.BRAVE_CRYSTAL,
                    5)) {
                pc.getInventory().makeDeleteEnchant(32, 7);
                pc.getInventory().makeDeleteEnchant(42, 7);
                pc.getInventory().consumeItem(41246, 1000);
                pc.getInventory().consumeItem(L1ItemId.BRAVE_CRYSTAL, 5);
                pc.getInventory().storeItem(412000, 1);
                pc.sendPackets("뇌신검 제작에 성공하였습니다");

                html = "joegolem14";
            } else {
                html = "joegolem15";
            }
        }

        pc.sendPackets(new S_NPCTalkReturn(objId, html));
    }
}
