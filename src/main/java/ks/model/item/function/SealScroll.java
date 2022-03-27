package ks.model.item.function;

import ks.constants.L1PacketBoxType;
import ks.core.datatables.next_items.CharacterNextReturnUtils;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;

public class SealScroll extends L1ItemInstance {
    public SealScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = this.getItemId();
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (itemId == 401261) { // 미확인주문서
                if (targetItem.getBless() >= 128) { // 봉인아이템일경우 미확인불가능하게
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
                targetItem.setIdentified(false);
                pc.getInventory().updateItem(targetItem, L1PcInventory.COL_IS_ID);
                pc.sendPackets(new S_ItemColor(targetItem, 3));
                pc.sendPackets(new S_SystemMessage(targetItem.getLogName() + "에 어두운 그림자가 스며듭니다."));
                pc.getInventory().removeItem(useItem, 1);
            } else if (itemId == 401262) { // 축복받은 주문서
                if (targetItem == null || targetItem.getItem().getType2() == 0) { // 무기와
                    pc.sendPackets(new S_SystemMessage("무기와 방어구에만 사용할 수 있습니다."));
                    return;
                }

                if (targetItem.getBless() >= 128 || targetItem.getBless() == 0) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }

                targetItem.setBless(0);

                pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
                pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);
                pc.getInventory().removeItem(useItem, 1);

                pc.sendPackets(new S_SkillSound(pc.getId(), 9268));
                pc.sendPackets(new S_SystemMessage(targetItem.getLogName() + "에 축복의 기운이 스며듭니다."));
            } else if (itemId == 50020) { // 봉인줌서
                if (targetItem.getBless() >= 0 && targetItem.getBless() <= 3
                        && (targetItem.getItem().getType2() == 1
                        || targetItem.getItem().getType2() == 2
                        || targetItem.getItem().getType2() == 0 &&
                        targetItem.getItem().getType() == 17)) {
                    int Bless = 0;

                    switch (targetItem.getBless()) {
                        case 0:
                            Bless = 128;
                            break; //축
                        case 1:
                            Bless = 129;
                            break; //보통
                        case 2:
                            Bless = 130;
                            break; //저주
                        case 3:
                            Bless = 131;
                            break; //미확인
                    }

                    targetItem.setBless(Bless);

                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ITEM_STATUS, targetItem, L1CommonUtils.checkSt(targetItem)));
                    targetItem.setBless(Bless);

                    pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
                    pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (itemId == 50021) { // 봉인해제줌서
                if (targetItem.getBless() >= 128
                        && targetItem.getBless() <= 131) {
                    int Bless = 0;
                    switch (targetItem.getBless()) {
                        case 128:
                            Bless = 0;
                            break;
                        case 129:
                            Bless = 1;
                            break;
                        case 130:
                            Bless = 2;
                            break;
                        case 131:
                            Bless = 3;
                            break;
                    }
                    targetItem.setBless(Bless);

                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ITEM_STATUS, targetItem, L1CommonUtils.checkSt(targetItem)));
                    targetItem.setBless(Bless);

                    pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
                    pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);
                    pc.getInventory().removeItem(useItem, 1);
                } else
                    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
            } else if (itemId == 60001256) {
                CharacterNextReturnUtils.changeNextReturn(pc, useItem, targetItem, 1);
            }
        }
    }
}
