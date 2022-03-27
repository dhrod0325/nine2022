package ks.model.item.function.enchant;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.core.datatables.enchantSetting.EnchantSettingTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.NumberUtils;
import ks.util.common.random.RandomUtils;

public class EnchantArmor extends Enchant {
    public EnchantArmor(L1Item item) {
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

            if (!targetItem.getItem().isArmor()) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            use(pc, useItem, targetItem);
        }
    }

    private void use(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem) {
        int safeEnchant = targetItem.getItem().getSafeEnchant();

        int itemId = getItemId();
        int enchantLevel = targetItem.getEnchantLevel();

        if (enchantLevel >= CodeConfig.MAX_ARMOR_ENCHANT) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        if (NumberUtils.contains(useItem.getItemId(), 60001320, 60001321, 60001322)) {
            if (!NumberUtils.contains(targetItem.getItemId(), 55000095, 55000096, 55000097, 55000098, 155000095, 155000096, 155000097, 155000098
            )) {
                pc.sendPackets(new S_ServerMessage(79));

                return;
            }
        }

        if (itemId == 60001236) {
            if (enchantLevel == CodeConfig.MIN_MASTER_ARMOR_ENCHANT) {
                int per = EnchantSettingTable.getInstance().getEnchantPer(enchantLevel + 1, 0, "masterArmor");

                if (RandomUtils.isWinning(100, per)) {
                    successEnchant(pc, targetItem, 1);

                    pc.sendPackets(new S_SkillSound(pc.getId(), 8686));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8686));

                    if (CodeConfig.ENCHANT_MENT) {
                        L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 +" + (enchantLevel + 1) + " " + targetItem.getName() + " 강화에 성공하였습니다");
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(1310));
                    L1CommonUtils.enchantFailEffect(pc);
                }

                pc.getInventory().removeItem(useItem, 1);
            } else {
                pc.sendPackets(new S_SystemMessage(String.format("방어구 인챈트 +%d에서만 사용이 가능합니다.", CodeConfig.MIN_MASTER_ARMOR_ENCHANT)));
            }
        } else if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR || itemId == 60001322) {
            pc.getInventory().removeItem(useItem, 1);
            int rnd = RandomUtils.nextInt(100);

            if (safeEnchant == 0 && rnd <= 30) {
                failureEnchant(pc, targetItem);
                return;
            }

            if (enchantLevel < 1) {
                failureEnchant(pc, targetItem);
            } else {
                successEnchant(pc, targetItem, -1);
            }
        } else if (enchantLevel < safeEnchant) {
            pc.getInventory().removeItem(useItem, 1);
            successEnchant(pc, targetItem, blessScrollRandomEnchant(targetItem, itemId));
        } else {
            pc.getInventory().removeItem(useItem, 1);
            int rnd = RandomUtils.nextInt(100);

            String type;

            if (L1CommonUtils.isBlessScroll(itemId)) {
                type = "blessArmor";
            } else {
                type = "armor";
            }

            int enchantChance = getEnchantChance(pc, targetItem, type);

            if (rnd < enchantChance) {
                int randomEnchantLevel = blessScrollRandomEnchant(targetItem, itemId);
                successEnchant(pc, targetItem, randomEnchantLevel);
            } else if (enchantLevel >= 9 && rnd < (enchantChance * 2)) {
                String msg = "+" + enchantLevel + " " + targetItem.getName();
                pc.sendPackets(new S_ServerMessage(160, msg, "$252", "$248"));
            } else {
                failureEnchant(pc, targetItem);

                L1CommonUtils.enchantFailEffect(pc);
            }
        }
    }
}
