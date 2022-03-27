package ks.model.action.custom.impl.npc;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;

public class ActionLastabardCreate extends L1AbstractNpcAction {
    public ActionLastabardCreate(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String act = action.replace("cc_lastw_", "");

        switch (act) {
            case "paper":
                if (!pc.getInventory().checkItem(41019)) {
                    pc.sendPackets("라스타바드의 역사서 1장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41020)) {
                    pc.sendPackets("라스타바드의 역사서 2장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41021)) {
                    pc.sendPackets("라스타바드의 역사서 3장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41022)) {
                    pc.sendPackets("라스타바드의 역사서 4장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41023)) {
                    pc.sendPackets("라스타바드의 역사서 5장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41024)) {
                    pc.sendPackets("라스타바드의 역사서 6장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41025)) {
                    pc.sendPackets("라스타바드의 역사서 7장이 필요합니다");
                }
                if (!pc.getInventory().checkItem(41026)) {
                    pc.sendPackets("라스타바드의 역사서 8장이 필요합니다");
                }

                if (pc.getInventory().checkItem(41019)
                        && pc.getInventory().checkItem(41020)
                        && pc.getInventory().checkItem(41021)
                        && pc.getInventory().checkItem(41022)
                        && pc.getInventory().checkItem(41023)
                        && pc.getInventory().checkItem(41024)
                        && pc.getInventory().checkItem(41025)
                        && pc.getInventory().checkItem(41026)) {
                    pc.getInventory().removeItemById(41019, 1);
                    pc.getInventory().removeItemById(41020, 1);
                    pc.getInventory().removeItemById(41021, 1);
                    pc.getInventory().removeItemById(41022, 1);
                    pc.getInventory().removeItemById(41023, 1);
                    pc.getInventory().removeItemById(41024, 1);
                    pc.getInventory().removeItemById(41025, 1);
                    pc.getInventory().removeItemById(41026, 1);

                    pc.getInventory().storeItem(40965, 1);
                    pc.sendPackets("라스타바드 무기제작 비법서 제작에 성공하였습니다");
                }
                break;
            case "a": //집행
                createLastabardItem(pc, 61);
                break;
            case "b": //바람칼날의단검
                createLastabardItem(pc, 12);
                break;
            case "c": //붉은그림자의이도류
                createLastabardItem(pc, 86);
                break;
            case "d": //수정결정체지팡이
                createLastabardItem(pc, 134);
                break;
            case "e": //가이아의격노
                createLastabardItem(pc, 45000601);
                break;
        }
    }

    public static void createLastabardItem(L1PcInstance pc, int newItemId) {
        if (pc.getInventory().checkItem(40965)) {
            pc.getInventory().storeItem(newItemId, 1);
            pc.getInventory().removeItemById(40965, 1);

            L1Item item = ItemTable.getInstance().findItem(newItemId);

            pc.sendPackets(item.getName() + " 제작에 성공하였습니다");

            L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 " + item.getName() + " 제작에 성공하였습니다");

        } else {
            pc.sendPackets("라스타바드 무기제작 비법서가 필요합니다");
        }
    }
}
