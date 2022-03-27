package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.L1Quest;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.util.L1CommonUtils;

public class ActionRobinHood extends L1AbstractNpcAction {
    public ActionRobinHood(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = null;

        switch (action) {
            case "A":
                L1CommonUtils.checkItemAndMessage(pc, 40068);

                if (pc.getInventory().checkItem(40068)) { /* 엘븐와퍼 체크 */
                    pc.getInventory().consumeItem(40068, 1); /* 엘븐와퍼 소비 */
                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 1); /* 1단계 완료 */
                    html = "robinhood4";
                } else {
                    html = "robinhood19";
                }
                break;
            case "B":  /* robinhood8 */
                createItem(pc, new int[]{41346, 41348}, new int[]{1, 1});

                html = "robinhood13";
                pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 2);

                break;
            case "C":  /* robinhood9 */

                L1CommonUtils.checkItemAndMessage(pc, 41346);
                L1CommonUtils.checkItemAndMessage(pc, 41351);
                L1CommonUtils.checkItemAndMessage(pc, 41352, 4);
                L1CommonUtils.checkItemAndMessage(pc, 40618, 30);
                L1CommonUtils.checkItemAndMessage(pc, 40643, 30);
                L1CommonUtils.checkItemAndMessage(pc, 40645, 30);
                L1CommonUtils.checkItemAndMessage(pc, 40651, 30);
                L1CommonUtils.checkItemAndMessage(pc, 40676, 30);
                L1CommonUtils.checkItemAndMessage(pc, 40514, 20);

                if (pc.getInventory().checkItem(41346)//메모지
                        && pc.getInventory().checkItem(41351)//달빛의정기
                        && pc.getInventory().checkItem(41352, 4)//신성한 유니콘의 뿔
                        && pc.getInventory().checkItem(40618, 30)//대지의 숨결
                        && pc.getInventory().checkItem(40643, 30)//물의숨결
                        && pc.getInventory().checkItem(40645, 30)//바람의숨결
                        && pc.getInventory().checkItem(40651, 30)//불의숨결
                        && pc.getInventory().checkItem(40676, 30)//어둠의숨결
                        && pc.getInventory().checkItem(40514, 20)) {//정령의눈물
                    pc.getInventory().consumeItem(41346, 1); // 메모장, 정기, 유뿔, 불, 물, 바람, 대지 어둠숨결
                    pc.getInventory().consumeItem(41351, 1);
                    pc.getInventory().consumeItem(41352, 4);
                    pc.getInventory().consumeItem(40651, 30);
                    pc.getInventory().consumeItem(40643, 30);
                    pc.getInventory().consumeItem(40645, 30);
                    pc.getInventory().consumeItem(40618, 30);
                    pc.getInventory().consumeItem(40676, 30);
                    pc.getInventory().consumeItem(40514, 20);

                    createItem(pc, new int[]{41350, 41347}, new int[]{1, 1});

                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 7);

                    html = "robinhood10";
                } else {
                    html = "robinhood15";
                }
                break;
            case "E":  /* robinhood11 */
                L1CommonUtils.checkItemAndMessage(pc, 41350);
                L1CommonUtils.checkItemAndMessage(pc, 41347);
                L1CommonUtils.checkItemAndMessage(pc, 40491, 30);
                L1CommonUtils.checkItemAndMessage(pc, 40495, 40);
                L1CommonUtils.checkItemAndMessage(pc, 100);
                L1CommonUtils.checkItemAndMessage(pc, 40509, 12);
                L1CommonUtils.checkItemAndMessage(pc, 40052);
                L1CommonUtils.checkItemAndMessage(pc, 40053);
                L1CommonUtils.checkItemAndMessage(pc, 40054);
                L1CommonUtils.checkItemAndMessage(pc, 40055);

                if (pc.getInventory().checkItem(41350)//로빈후드의 반지
                        && pc.getInventory().checkItem(41347)//로빈후드 메모지
                        && pc.getInventory().checkItem(40491, 30)//그리폰의 깃털
                        && pc.getInventory().checkItem(40495, 40)//미스릴실
                        && pc.getInventory().checkItem(100)//오리하루콘 도금뿔
                        && pc.getInventory().checkItem(40509, 12)//오리하루콘판금
                        && pc.getInventory().checkItem(40052)//최고급다이아몬드
                        && pc.getInventory().checkItem(40053)//최고급루비
                        && pc.getInventory().checkItem(40054)//최고급사파이어
                        && pc.getInventory().checkItem(40055)) {//최급에메랄드
                    pc.getInventory().consumeItem(41350, 1); // 반지, 메모지, 그리폰깃털, 미스릴실, 오리뿔, 오판, 최고급보석1개씩
                    pc.getInventory().consumeItem(41347, 1);
                    pc.getInventory().consumeItem(40491, 30);
                    pc.getInventory().consumeItem(40495, 40);
                    pc.getInventory().consumeItem(100, 1);
                    pc.getInventory().consumeItem(40509, 12);
                    pc.getInventory().consumeItem(40052, 1);
                    pc.getInventory().consumeItem(40053, 1);
                    pc.getInventory().consumeItem(40054, 1);
                    pc.getInventory().consumeItem(40055, 1);

                    createItem(pc, new int[]{205}, new int[]{1});

                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 0); // 퀘스트 리셋
                    html = "robinhood12"; /* 완성이야 */
                } else {
                    html = "robinhood17"; /* 재료가 부족한걸 */
                }
                break;
        }

        if (html != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, html));
        }
    }


}
