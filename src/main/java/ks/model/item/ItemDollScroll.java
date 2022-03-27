package ks.model.item;

import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ItemStatus;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemDollScroll extends L1ItemInstance {
    private final Logger logger = LogManager.getLogger();

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        try {
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;

                L1ItemInstance useItem = pc.getInventory().getItem(getId());
                L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

                use(targetItem, useItem, pc);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void use(L1ItemInstance targetItem, L1ItemInstance useItem, L1PcInstance pc) {
        if (!targetItem.getName().startsWith("마법인형 :")) {
            pc.sendPackets(new S_SystemMessage("인형에만 사용할 수 있습니다."));
            return;
        }

        if (pc.getCurrentDollId() == targetItem.getId()) {
            pc.sendPackets(new S_SystemMessage("소환중인 인형은 강화할 수 없습니다."));
            return;
        }

        if (targetItem.getEnchantLevel() == 0) {
            enchant(10, targetItem, useItem, pc);
        } else if (targetItem.getEnchantLevel() == 1) {
            enchant(5, targetItem, useItem, pc);
        }

//        else if(targetItem.getEnchantLevel() == 2){
//            enchant(10,targetItem,useItem,pc);
//        }

        else {
            pc.sendPackets(new S_SystemMessage("2단계 이상 강화할 수 없습니다."));
        }
    }

    public void enchant(int successPer, L1ItemInstance targetItem, L1ItemInstance useItem, L1PcInstance pc) {
        int per = RandomUtils.nextInt(100) + 1;

        if (per < successPer) {
            targetItem.setEnchantLevel(targetItem.getEnchantLevel() + 1);

            pc.sendPackets(new S_SkillSound(pc.getId(), 8950));
            pc.sendPackets(new S_SystemMessage(targetItem.getLogName() + "에 강화의 기운이 스며듭니다."));
            pc.sendPackets(new S_ItemStatus(targetItem));
            pc.save();
        } else {
            pc.sendPackets(new S_SystemMessage("인형 강화에 실패 하였습니다."));
        }

        pc.getInventory().removeItem(useItem, 1);
    }
}
