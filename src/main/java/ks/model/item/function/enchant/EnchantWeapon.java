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
import ks.util.common.random.RandomUtils;

public class EnchantWeapon extends Enchant {
    public EnchantWeapon(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (isNotEnableEnchant(pc, targetItem)) {
                return;
            }

            if (!targetItem.getItem().isWeapon()) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            use(pc, useItem, targetItem);
        }
    }

    private void use(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem) {
        int useItemId = getItemId();
        int enchantLevel = targetItem.getEnchantLevel();

        int safeEnchant = targetItem.getItem().getSafeEnchant();

        if (safeEnchant < 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        if (enchantLevel >= CodeConfig.MAX_WEAPON_ENCHANT) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        if (useItemId == 60001149) {
            if (enchantLevel == 9) {
                int per = EnchantSettingTable.getInstance().getEnchantPer(10, 0, "masterWeapon");

                if (RandomUtils.isWinning(100, per)) {
                    successEnchant(pc, targetItem, 1);

                    pc.sendPackets(new S_SkillSound(pc.getId(), 8686));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8686));
                } else {
                    pc.sendPackets(new S_ServerMessage(1310));
                    L1CommonUtils.enchantFailEffect(pc);
                }

                pc.getInventory().removeItem(useItem, 1);
            } else {
                pc.sendPackets(new S_SystemMessage("무기인챈트 +9에서만 사용이 가능합니다."));
            }
        } else if (useItemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) {
            pc.getInventory().removeItem(useItem, 1);

            if (enchantLevel < -6) {
                failureEnchant(pc, targetItem);
            } else {
                successEnchant(pc, targetItem, -1);
            }
        } else if (enchantLevel < safeEnchant) {
            pc.getInventory().removeItem(useItem, 1);
            successEnchant(pc, targetItem, blessScrollRandomEnchant(targetItem, useItemId));
        } else {
            pc.getInventory().removeItem(useItem, 1);

            int rnd = RandomUtils.nextInt(100);

            String type;

            if (L1CommonUtils.isBlessScroll(useItemId)) {
                type = "blessWeapon";
            } else {
                type = "weapon";
            }

            int enchantChance = getEnchantChance(pc, targetItem, type);

            if (rnd <= enchantChance) {
                int randomEnchantLevel = blessScrollRandomEnchant(targetItem, useItemId);
                successEnchant(pc, targetItem, randomEnchantLevel);

                if (enchantLevel >= 9 && CodeConfig.ENCHANT_MENT) {
                    L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 +" + (enchantLevel + 1) + " " + targetItem.getName() + " 강화에 성공하였습니다");
                }
            } else if (enchantLevel >= 9 && rnd < (enchantChance * 2)) {
                pc.sendPackets(new S_ServerMessage(160, targetItem.getLogName(), "$245", "$248"));
            } else {
                L1CommonUtils.enchantFailEffect(pc);

                failureEnchant(pc, targetItem);
            }
        }
    }
}
