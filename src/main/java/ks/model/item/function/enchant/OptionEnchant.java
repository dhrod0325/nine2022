package ks.model.item.function.enchant;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.item.function.option.L1Option;
import ks.model.item.function.option.L1OptionItem;
import ks.model.item.function.option.L1OptionScroll;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.*;

public class OptionEnchant extends L1ItemInstance {
    public OptionEnchant(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        super.clickItem(cha, packet);

        L1PcInstance pc = (L1PcInstance) cha;
        L1ItemInstance useItem = pc.getInventory().getItem(getId());
        L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

        if (targetItem.getItem().isWeapon() || targetItem.getItem().isArmor()) {
            if (targetItem.isEquipped()) {
                pc.sendPackets("착용중인 장비에 옵션부여주문서를 사용할수 없습니다");
                return;
            }

            if (targetItem.getItem().isWeapon()) {
                if (getItemId() == 60001239) {
                    optionWeapon(pc, targetItem, useItem);
                }

                if (getItemId() == 60001240) {
                    optionBlessWeapon(pc, targetItem, useItem);
                }
            } else if (targetItem.getItem().isArmor()) {
                if (getItemId() == 60001239) {
                    optionArmor(pc, targetItem, useItem);
                }

                if (getItemId() == 60001240) {
                    optionBlessArmor(pc, targetItem, useItem);
                }
            }

            pc.getInventory().removeItem(useItem, 1);
        } else {
            pc.sendPackets("무기 또는 방어구에만 옵션 부여가 가능합니다");
        }
    }

    private void optionBlessWeapon(L1PcInstance pc, L1ItemInstance targetItem, L1ItemInstance useItem) {
        L1OptionItem o = L1OptionScroll.getInstance().getOption("blessWeapon");
        optionBless(pc, targetItem, o);
    }

    private void optionWeapon(L1PcInstance pc, L1ItemInstance targetItem, L1ItemInstance useItem) {
        L1OptionItem o = L1OptionScroll.getInstance().getOption("weapon");
        optionNormal(pc, targetItem, o);
    }

    private void optionBlessArmor(L1PcInstance pc, L1ItemInstance targetItem, L1ItemInstance useItem) {
        L1OptionItem o = L1OptionScroll.getInstance().getOption("blessArmor");
        optionBless(pc, targetItem, o);
    }

    private void optionArmor(L1PcInstance pc, L1ItemInstance targetItem, L1ItemInstance useItem) {
        L1OptionItem o = L1OptionScroll.getInstance().getOption("blessArmor");
        optionNormal(pc, targetItem, o);
    }

    private void optionNormal(L1PcInstance pc, L1ItemInstance targetItem, L1OptionItem o) {
        L1Option option = o.open();

        if (option.getValue() > 0) {
            int value = option.getValue();
            targetItem.setOptionGrade(value);
            pc.sendPackets("옵션 부여에 성공하였습니다");
            pc.sendPackets(new S_SkillSound(pc.getId(), 7322));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7322));
        } else {
            targetItem.setOptionGrade(0);
            pc.sendPackets("옵션 부여에 실패하였습니다.");
        }

        pc.getInventory().saveItem(targetItem, L1PcInventory.COL_OPTION);
        pc.getInventory().updateItem(targetItem, L1PcInventory.COL_OPTION);

        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_HPUpdate(pc));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    private void optionBless(L1PcInstance pc, L1ItemInstance targetItem, L1OptionItem o) {
        L1Option option = o.open();

        if (option.getValue() > 0) {
            int value = option.getValue();
            targetItem.setOptionGrade(value);
            pc.sendPackets("옵션 부여에 성공하였습니다");
            pc.sendPackets(new S_SkillSound(pc.getId(), 7322));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7322));

            pc.getInventory().saveItem(targetItem, L1PcInventory.COL_OPTION);
            pc.getInventory().updateItem(targetItem, L1PcInventory.COL_OPTION);

            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_HPUpdate(pc));
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else {
            pc.sendPackets("옵션 부여에 실패하였습니다.");
        }
    }
}
