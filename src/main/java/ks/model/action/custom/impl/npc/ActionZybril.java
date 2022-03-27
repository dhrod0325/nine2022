package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.L1Quest;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.util.L1CommonUtils;

public class ActionZybril extends L1AbstractNpcAction {
    public ActionZybril(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        String html = null;

        switch (action) {
            case "A":  /* zybril1 ~ zybril6 */
                L1CommonUtils.checkItemAndMessage(pc, 41348);

                if (pc.getInventory().checkItem(41348)) { /* 소개장 */
                    pc.getInventory().consumeItem(41348, 1);
                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 3); //3단계완료
                    html = "zybril13"; /* 아 그 활쟁이 */
                } else {
                    html = "zybril11"; /* 편지는 어디에? */
                }
                break;
            case "B":  /* zybril7 */
                L1CommonUtils.checkItemAndMessage(pc, 40048, 10);
                L1CommonUtils.checkItemAndMessage(pc, 40049, 10);
                L1CommonUtils.checkItemAndMessage(pc, 40050, 10);
                L1CommonUtils.checkItemAndMessage(pc, 40051, 10);

                if (pc.getInventory().checkItem(40048, 10)
                        && pc.getInventory().checkItem(40049, 10)
                        && pc.getInventory().checkItem(40050, 10)
                        && pc.getInventory().checkItem(40051, 10)) {
                    pc.getInventory().consumeItem(40048, 10); // 고다, 고루, 고사,고에
                    pc.getInventory().consumeItem(40049, 10);
                    pc.getInventory().consumeItem(40050, 10);
                    pc.getInventory().consumeItem(40051, 10);

                    createItem(pc, new int[]{41353}, new int[]{1});

                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 4); // 4단계 완료
                    html = "zybril12"; /* 기부금을 받을게요 */
                } else {
                    html = "";
                }
                break;
            case "C":  /* zybril8 */
                L1CommonUtils.checkItemAndMessage(pc, 40514, 10);
                L1CommonUtils.checkItemAndMessage(pc, 41353);

                if (pc.getInventory().checkItem(40514, 10)
                        && pc.getInventory().checkItem(41353, 1)) {
                    pc.getInventory().consumeItem(40514, 10); //정령의 눈물, 에바의 단검
                    pc.getInventory().consumeItem(41353, 1);

                    createItem(pc, new int[]{41354}, new int[]{1});

                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 5); // 5단계 완료
                    html = "zybril9"; /* 고마워요 한가지부탁더 */
                } else {
                    html = "zybril13"; /* 정령의눈물이 필요합니다.. */
                }
                break;
            case "D":
                L1CommonUtils.checkItemAndMessage(pc, 41349);

                if (pc.getInventory().checkItem(41349)) { /* 사엘의 반지 */
                    pc.getInventory().consumeItem(41349, 1);

                    createItem(pc, new int[]{41351}, new int[]{1});

                    pc.getQuest().setStep(L1Quest.QUEST_MOONBOW, 6); // 완료
                    html = "zybril10";
                } else {
                    html = "zybril14";
                }
                break;
        }

        if (html != null) {
            pc.sendPackets(new S_NPCTalkReturn(objId, html));
        }
    }


}
