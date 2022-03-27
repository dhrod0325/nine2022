package ks.model.item.function.enchant;

import ks.app.config.prop.CodeConfig;
import ks.model.L1Acc;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class EnchantAcc extends Enchant {
    public EnchantAcc(L1Item item) {
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

            if (!L1Acc.isNormalAcc(targetItem)) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            use(pc, useItem, targetItem);
        }
    }

    private void use(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem) {
        int enchantLevel = targetItem.getEnchantLevel();

        if (enchantLevel >= CodeConfig.MAX_ACC_ENCHANT) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        pc.getInventory().removeItem(useItem, 1);

        int rnd = RandomUtils.nextInt(100);

        String type = "acc";

        int enchantChance = getEnchantChance(pc, targetItem, type);

        if (rnd < enchantChance) {
            successEnchant(pc, targetItem, 1);
        } else if (enchantLevel >= 9 && rnd < (enchantChance * 2)) {
            String msg = "+" + enchantLevel + " " + targetItem.getName();
            pc.sendPackets(new S_ServerMessage(160, msg, "$252", "$248"));
        } else {
            failureEnchant(pc, targetItem);
            L1CommonUtils.enchantFailEffect(pc);
        }
    }
}
