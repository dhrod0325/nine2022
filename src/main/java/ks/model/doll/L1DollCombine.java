package ks.model.doll;

import ks.constants.L1DollItemId;
import ks.core.datatables.item.ItemTable;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.MagicDollItemInstance;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class L1DollCombine {
    private final L1PcInstance pc;

    public L1DollCombine(L1PcInstance pc) {
        this.pc = pc;
    }

    public void startCombine(int step) {
        int removeItemSize = 3;

        if (step == 5) {
            removeItemSize = 2;
        }

        List<L1ItemInstance> removeItems = new ArrayList<>();

        List<Integer> check = L1DollItemId.dollMap.get("dollList" + step);
        List<Integer> nextCheck = L1DollItemId.dollMap.get("dollList" + (step + 1));

        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (!(item instanceof MagicDollItemInstance)) {
                continue;
            }

            MagicDollItemInstance doll = (MagicDollItemInstance) item;

            if (doll.isAppear()) {
                continue;
            }

            if (check.contains(item.getItemId())) {
                removeItems.add(item);

                if (removeItems.size() >= removeItemSize) {
                    break;
                }
            }
        }

        if (removeItems.size() < removeItemSize) {
            pc.sendPackets(step + "단계 인형 " + removeItemSize + "개를 소지하고 있어야합니다");
            return;
        }

        int successPer = 0;

        if (step == 1) {
            successPer = 50;
        } else if (step == 2) {
            successPer = 40;
        } else if (step == 3) {
            successPer = 20;
        } else if (step == 4) {
            successPer = 20;
        } else if (step == 5) {
            successPer = 10;
        }

        int itemId;

        if (RandomUtils.isWinning(100, successPer)) {
            if (step <= 2) {
                itemId = nextCheck.get(RandomUtils.nextInt(nextCheck.size()));
            } else {
                List<Integer> normal = L1DollItemId.dollMap.get("dollList" + (step + 1) + "Normal");
                List<Integer> bless = L1DollItemId.dollMap.get("dollList" + (step + 1) + "Bless");

                if (RandomUtils.isWinning(100, 20)) {
                    itemId = bless.get(RandomUtils.nextInt(bless.size()));
                } else {
                    itemId = normal.get(RandomUtils.nextInt(normal.size()));
                }
            }
        } else {
            itemId = check.get(RandomUtils.nextInt(check.size()));
        }

        L1ItemInstance dollItem = ItemTable.getInstance().createItem(itemId);
        dollItem.setCount(1);
        dollItem.setIdentified(true);

        int endStep = L1CommonUtils.getDollStep(itemId);

        if (endStep >= 4) {
            L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 " + dollItem.getName() + " 제작에 성공하였습니다");
        }

        for (L1ItemInstance oldItem : removeItems) {
            pc.getInventory().removeItem(oldItem);
        }

        pc.getInventory().storeItem(dollItem);

        pc.sendPackets(dollItem.getName() + "을 획득하였습니다");
    }
}
