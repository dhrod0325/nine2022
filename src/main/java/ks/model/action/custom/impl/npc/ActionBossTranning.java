package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.model.L1Object;
import ks.model.L1Teleport;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.system.bossTraning.BossTrainingSystem;
import ks.system.bossTraning.BossTrainingTable;

public class ActionBossTranning extends L1AbstractNpcAction {
    public ActionBossTranning(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = null;

        if (action.equalsIgnoreCase("1")) { // 대여
            if (BossTrainingSystem.getInstance().isFull()) {
                html = "bosskey3";
            } else if (pc.getInventory().checkItem(L1ItemId.BOSS_TRANING_KEY)) {
                html = "bosskey6";
            } else {
                html = "bosskey4";
            }
        } else if (action.matches("[2-4]")) {
            if (!pc.getInventory().checkItem(L1ItemId.BOSS_TRANING_KEY)) { // 액션 조작 방지
                if (BossTrainingSystem.getInstance().isFull()) {
                    pc.sendPackets("모든 훈련장이 사용중입니다.");
                    return;
                }

                int count = 0;

                if (action.equalsIgnoreCase("2")) { // 4개
                    count = 4;
                } else if (action.equalsIgnoreCase("3")) { // 8개
                    count = 8;
                } else if (action.equalsIgnoreCase("4")) { // 16개
                    count = 16;
                }

                int mapId = BossTrainingSystem.getInstance().generateMapId();

                if (pc.getInventory().consumeItem(40308, count * 300)) {
                    BossTrainingSystem.getInstance().startRaid(mapId);
                    BossTrainingTable.getInstance().deleteByKeyId(mapId);

                    for (int i = 0; i < count; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(L1ItemId.BOSS_TRANING_KEY, 1);
                        item.setKeyId(mapId);
                        BossTrainingTable.getInstance().insert(item);
                    }
                    html = "bosskey7";
                } else {
                    html = "bosskey5";
                }
            } else {
                html = "bosskey6";
            }
        } else if (action.equalsIgnoreCase("6")) { // 입장
            if (!BossTrainingSystem.getInstance().isFull()) {
                L1ItemInstance item = pc.getInventory().findItemId(L1ItemId.BOSS_TRANING_KEY);
                if (item != null) {
                    int id = item.getKeyId();

                    if (id == 0) {
                        pc.sendPackets("이용 불가능한 열쇠를 소지중입니다 삭제 후 재구매하시기 바랍니다");
                        return;
                    }

                    L1Teleport.teleport(pc, 32901, 32814, (short) id, pc.getHeading(), true);
                } else {
                    html = "bosskey2";
                }
            } else {
                html = "bosskey3";
            }
        }

        if (html != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, html));
        }
    }
}