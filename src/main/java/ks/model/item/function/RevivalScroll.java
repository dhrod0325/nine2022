package ks.model.item.function;

import ks.commands.gm.GmCommands;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.item.ItemTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_EffectLocation;
import ks.util.common.random.RandomUtils;

public class RevivalScroll extends L1ItemInstance {
    public RevivalScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (targetItem == null) {
                return;
            }

            L1Item item = targetItem.getItem();

            if (item.getRevival() > 0) {
                int effectId;

                boolean isSuccess = RandomUtils.isWinning(100, item.getRevivalPer());

                Boolean c = GmCommands.getInstance().isEnchantOnlySuccess(pc.getName());

                if (c != null) {
                    isSuccess = c;
                }

                if (isSuccess) {
                    effectId = 9913;
                    L1ItemInstance revivalTarget = ItemTable.getInstance().createItem(item.getRevival());
                    pc.getInventory().storeItem(revivalTarget);
                    pc.sendPackets(targetItem.getName() + "에 새 생명을 부여하였습니다");

                    if (targetItem.getItem().getRevivalMent() == 1) {
                        L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 " + targetItem.getName() + "에 새 생명을 부여하였습니다");
                    }
                } else {

                    effectId = SkillsTable.getInstance().getTemplate(L1SkillId.ENCHANT_FAIL).getCastGfx();
                    pc.sendPackets(targetItem.getName() + "에 새로운 생명을 부여하지 못했습니다");
                }

                pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), effectId));
                Broadcaster.broadcastPacket(pc, new S_EffectLocation(pc.getX(), pc.getY(), effectId));
            } else {
                pc.sendPackets("아무일도 일어나지 않았습니다");
                return;
            }

            pc.getInventory().removeItem(targetItem, 1);
            pc.getInventory().removeItem(useItem, 1);
        }
    }
}
