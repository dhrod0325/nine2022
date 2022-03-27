package ks.model.item;

import ks.commands.gm.GmCommands;
import ks.core.datatables.enchant.CharacterEnchantTable;
import ks.core.datatables.enchantSetting.EnchantSettingTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ItemStatus;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class ItemSealScroll extends L1ItemInstance {
    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            int itemId = getItemId();
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (itemId == 6000043) {
                if (targetItem == null || targetItem.getItem().getType2() != 1) {
                    pc.sendPackets(new S_SystemMessage("무기에만 사용할 수 있습니다."));
                    return;
                }

                use(targetItem, useItem, pc);
            } else if (itemId == 6000044) {//갑옷축복
                if (targetItem == null || !targetItem.getItem().isArmor()) {
                    pc.sendPackets(new S_SystemMessage("갑옷에만 사용할 수 있습니다."));
                    return;
                }

                if (L1CommonUtils.isDragonT(targetItem.getItemId())) {
                    pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
                    return;
                }

                use(targetItem, useItem, pc);
            } else if (itemId == 6000045) {//악세축복
                if (targetItem == null || !targetItem.getItem().isAccessorie()) {
                    pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
                    return;
                }

                if (targetItem.getItem().getGrade() == 10) {
                    pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
                    return;
                }

                if (targetItem.getItem().getGrade() == 50) {
                    pc.sendPackets(new S_SystemMessage("아무일도 일어나지 않았습니다."));
                    return;
                }

                use(targetItem, useItem, pc);
            } else if (itemId == 60001251) {
                if (targetItem == null || !targetItem.getName().startsWith("마법인형 :")) {
                    pc.sendPackets(new S_SystemMessage("인형에만 사용할 수 있습니다."));
                    return;
                }

                use(targetItem, useItem, pc);
            }
        }
    }

    public void use(L1ItemInstance targetItem, L1ItemInstance useItem, L1PcInstance pc) {
        if (targetItem.getBless() >= 128 || targetItem.getBless() == 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        int per = RandomUtils.nextInt(1000) + 1;

        String scrollType;

        if (targetItem.getItem().isWeapon()) {
            scrollType = "blessScrollWeapon";
        } else if (targetItem.getItem().isArmor()) {
            scrollType = "blessScrollArmor";
        } else if (targetItem.getItem().getName().startsWith("마법인형")) {
            scrollType = "blessScrollDoll";
        } else {
            scrollType = "blessScrollAcc";
        }

        int successPer = EnchantSettingTable.getInstance().getEnchantPer(0, 0, scrollType);

        Boolean c = GmCommands.getInstance().isEnchantOnlySuccess(pc.getName());

        if (c != null) {
            if (c) {
                successPer = 1000;
            } else {
                successPer = 0;
            }
        }

        if (pc.isGm()) {
            pc.sendPackets("per : " + per + ",successPer : " + successPer);
        }

        if (per <= successPer) {
            targetItem.setBless(0);

            pc.getInventory().updateItem(targetItem, L1PcInventory.COL_BLESS);
            pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);

            pc.sendPackets(new S_SkillSound(pc.getId(), 7322));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7322));
            pc.sendPackets(new S_SystemMessage(targetItem.getLogName() + "에 축복의 기운이 스며듭니다."));
            pc.sendPackets(new S_ItemStatus(targetItem));

            CharacterEnchantTable.getInstance().insert(pc, targetItem, 1, 0, true, "축복");
        } else {
            L1CommonUtils.enchantFailEffect(pc);
            pc.sendPackets(new S_SystemMessage("축복 부여에 실패 하였습니다."));
            CharacterEnchantTable.getInstance().insert(pc, targetItem, 1, 1, false, "축복");
        }

        pc.getInventory().removeItem(useItem, 1);
    }
}
