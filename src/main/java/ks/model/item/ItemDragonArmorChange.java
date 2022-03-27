package ks.model.item;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_CloseList;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.util.L1CommonUtils;

public class ItemDragonArmorChange extends L1ItemInstance {
    public ItemDragonArmorChange(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            pc.getDragonArmorChange().clear();

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (targetItem == null) {
                closeHtml(pc);
                return;
            }

            if (!L1CommonUtils.isDragonArmor(targetItem.getItemId())) {
                pc.sendPackets("용갑옷 이외의 아이템에 사용이 불가능 합니다");
                closeHtml(pc);
                return;
            }

            if (targetItem.isEquipped()) {
                pc.sendPackets("착용중인 장비에 사용이 불가능 합니다");
                closeHtml(pc);
                return;
            }

            pc.getDragonArmorChange().setUseItem(useItem);
            pc.getDragonArmorChange().setTargetItem(targetItem);

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_dchange"));
        }
    }

    public void closeHtml(L1PcInstance pc) {
        pc.sendPackets(new S_CloseList(pc.getId()));
    }
}
