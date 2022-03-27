package ks.model.item;

import ks.constants.L1ItemId;
import ks.model.L1Character;
import ks.model.L1EarRing;
import ks.model.L1Item;
import ks.model.L1Ring;
import ks.model.instance.L1ItemInstance;
import ks.model.item.function.enchant.Enchant;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class ItemEnchantRingAndEarRing extends Enchant {
    public ItemEnchantRingAndEarRing(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (isNotEnableEnchant(pc, targetItem)) {
                return;
            }

            int itemId = this.getItemId();

            //스냅퍼의 반지강화주문서
            if (itemId == L1ItemId.스냅퍼의반지강화주문서) {
                if (!L1Ring.is스냅퍼반지(targetItem.getItemId())) {
                    pc.sendPackets(new S_SystemMessage("스냅퍼 장신구에만 사용할 수 있습니다."));
                    return;
                }

                use(useItem, targetItem, pc);
            } else if (itemId == L1ItemId.룸티스강화주문서) {
                if (!L1EarRing.is룸티스(targetItem.getItemId())) {
                    pc.sendPackets(new S_SystemMessage("룸티스 장신구에만 사용할 수 있습니다."));
                    return;
                }

                use(useItem, targetItem, pc);
            }
        }
    }

    public void use(L1ItemInstance useItem, L1ItemInstance targetItem, L1PcInstance pc) {
        int rnd = RandomUtils.nextInt(100) + 1;
        int enchantLevel = targetItem.getEnchantLevel();

        String type = "sring";

        int enchantChance = getEnchantChance(pc, targetItem, type);

        if (rnd <= enchantChance) {
            successEnchant(pc, targetItem, 1);
        } else if (enchantLevel >= 9 && rnd < (enchantChance * 2)) {
            pc.sendPackets(new S_ServerMessage(160, targetItem.getLogName(), "$245", "$248"));
        } else {
            failureEnchant(pc, targetItem);
            L1CommonUtils.enchantFailEffect(pc);
        }

        pc.getInventory().removeItem(useItem, 1);
    }
}
