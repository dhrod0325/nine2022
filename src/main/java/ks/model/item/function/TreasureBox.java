package ks.model.item.function;

import ks.commands.common.CommonCommands;
import ks.model.L1Character;
import ks.model.L1EtcItem;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.item.L1TreasureBox;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.util.L1CommonUtils;

import java.sql.Timestamp;
import java.util.Calendar;

public class TreasureBox extends L1ItemInstance {
    public TreasureBox(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = useItem.getItemId();

            if (useItem.getItem().getType2() == 0) {
                int delayEffect = useItem.getItem().getDelayEffect();

                if (delayEffect > 0) {
                    Timestamp lastUsed = useItem.getLastUsed();

                    if (lastUsed != null) {
                        Calendar cal = Calendar.getInstance();

                        if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 <= delayEffect) {
                            long value = delayEffect - ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000);

                            Calendar useTime = Calendar.getInstance();
                            useTime.add(Calendar.SECOND, (int) value);

                            pc.sendPackets("사용가능시간 : " + L1CommonUtils.dateFormat(useTime.getTime()) + " 이후");

                            return;
                        }
                    }
                }
            }

            L1TreasureBox box = L1TreasureBox.get(itemId);

            if (box == null) {
                return;
            }

            int bxCount = box.getReqCount();
            int checkCount = useItem.getCount() - bxCount;

            if (checkCount < 0) {
                pc.sendPackets(useItem.getName() + " 최소 개봉 수량 : " + bxCount);

                return;
            }

            if (box.open(pc).isEmpty()) {
                return;
            }

            L1EtcItem temp = (L1EtcItem) useItem.getItem();

            if (temp.getDelayEffect() > 0) {
                if (useItem.getChargeCount() > 0) {
                    int chargeCount = useItem.getChargeCount();
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    useItem.setChargeCount(useItem.getChargeCount() - 1);

                    if (chargeCount <= 1) {
                        pc.getInventory().removeItem(useItem, bxCount);
                    }

                    useItem.setLastUsed(ts);

                    pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
                    pc.getInventory().saveItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
                } else {
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    useItem.setLastUsed(ts);
                    pc.getInventory().updateItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
                    pc.getInventory().saveItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
                }
            } else {
                pc.getInventory().removeItem(useItem.getId(), bxCount);
            }

            if (itemId == 6000038) {
                CommonCommands.getInstance().inventorySetup(pc);
            }
        }
    }
}
