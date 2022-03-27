package ks.model.item.function.orim;

import ks.app.config.prop.CodeConfig;
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
import ks.util.common.NumberUtils;
import ks.util.common.random.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class L1OrimScrollEnchantItem extends Enchant {
    public L1OrimScrollEnchantItem(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        super.clickItem(cha, packet);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (isNotEnableEnchant(pc, targetItem)) {
                return;
            }

            use(pc, useItem, targetItem);
        }
    }

    public void use(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem) {
        L1OrimScrollEnchant e = L1OrimScrollEnchant.get(useItem.getItemId());

        if (e == null) {
            pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
            return;
        }

        int armorType = targetItem.getItem().getType();

        if (e.getTargetItemIds() != null) {
            boolean check = false;

            String[] itemIdArray = e.getTargetItemIds().split(",");

            for (String s : itemIdArray) {
                if (targetItem.getItemId() == Integer.parseInt(s)) {
                    check = true;
                    break;
                }
            }
            if (check) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        }

        int enchantLevel = targetItem.getEnchantLevel();

        if (NumberUtils.contains(useItem.getItemId(), 60001371)) {
            if (targetItem.getItem().getItemId() != 55000055) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        } else if (NumberUtils.contains(useItem.getItemId(), 60001226, 60001227)) {
            if (targetItem.getItem().getType2() == 0 || targetItem.getItem().getType2() == 1) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (targetItem.getItem().getType2() == 2) {
                if (targetItem.getItem().getGrade() < 0) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }

                if (L1Ring.is스냅퍼반지(targetItem.getItemId()) || L1EarRing.is룸티스(targetItem.getItemId())) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
            }

            if (armorType >= 8 && armorType <= 12) {
                if (enchantLevel >= CodeConfig.MAX_ACC_ENCHANT) {
                    pc.sendPackets(new S_SystemMessage("악세사리는 +" + CodeConfig.MAX_ACC_ENCHANT + " 이상 강화할수없습니다."));
                    return;
                }
            }
        } else if (NumberUtils.contains(useItem.getItemId(), 60001340, 60001341)) {
            if (!targetItem.getItem().isWeapon()) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (enchantLevel >= CodeConfig.MAX_WEAPON_ENCHANT) {
                pc.sendPackets(new S_SystemMessage("무기는 +" + CodeConfig.MAX_WEAPON_ENCHANT + " 이상 강화할수없습니다."));
                return;
            }
        } else if (NumberUtils.contains(useItem.getItemId(), 60001342, 60001343)) {
            if (!targetItem.getItem().isArmor()) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (L1CommonUtils.isDragonT(targetItem.getItemId())) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (enchantLevel >= CodeConfig.MAX_ARMOR_ENCHANT) {
                pc.sendPackets(new S_SystemMessage("갑옷은 +" + CodeConfig.MAX_ARMOR_ENCHANT + " 이상 강화할수없습니다."));
                return;
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));

            return;
        }

        enchant(pc, useItem, targetItem, e);
    }

    private void enchant(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem, L1OrimScrollEnchant e) {
        L1OrimScrollEffect l1OrimScrollEffect = null;

        for (L1OrimScrollEffect each : e.getEffects()) {
            if (each.getEnchantLevel() == targetItem.getEnchantLevel()) {
                l1OrimScrollEffect = each;
                break;
            }
        }

        if (l1OrimScrollEffect == null) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        String[] probArray = l1OrimScrollEffect.getProbs().split(",");

        int c1 = Integer.parseInt(probArray[0]);
        int c2 = Integer.parseInt(probArray[1]);
        int c3 = Integer.parseInt(probArray[2]);

        List<Integer> p = new ArrayList<>();

        for (int i = 0; i < c1; i++) {
            p.add(0);
        }
        for (int i = 0; i < c2; i++) {
            p.add(1);
        }
        for (int i = 0; i < c3; i++) {
            p.add(2);
        }

        int chance = p.get(RandomUtils.nextInt(p.size()));

        if (pc.isGm()) {
            pc.sendPackets("e: " + targetItem.getEnchantLevel() + " / c1: " + c1 + " / c2: " + c2 + " / c3: " + c3 + " / chance: " + chance);
        }

        if (chance == 0) {
            if (e.getTargetType() == 5 && useItem.getBless() % 128 == 0) {
                pc.sendPackets(new S_ServerMessage(1310, targetItem.getLogName()));
            } else {
                successEnchant(pc, targetItem, -1);
            }
        } else if (chance == 1) {
            pc.sendPackets(new S_ServerMessage(1310, targetItem.getLogName()));
        } else {
            if (c3 == 0) {
                pc.sendPackets(new S_ServerMessage(1310, targetItem.getLogName()));
            } else {
                successEnchant(pc, targetItem, 1);
            }
        }

        if (useItem.getItem().getMaxChargeCount() > 0) {
            if (useItem.getChargeCount() > 0) {
                useItem.setChargeCount(useItem.getChargeCount() - 1);
                pc.getInventory().updateItem(useItem, 128);
            } else {
                pc.getInventory().removeItem(useItem, 1);
            }
        } else {
            pc.getInventory().removeItem(useItem, 1);
        }
    }
}
