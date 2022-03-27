package ks.model.item.function.enchant;

import ks.core.datatables.enchant.CharacterEnchantTable;
import ks.core.datatables.enchantSetting.EnchantSettingTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.attack.utils.L1WeaponUtils;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_EffectLocation;
import ks.packets.serverpackets.S_ItemStatus;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantAttr extends Enchant {
    private final Logger logger = LogManager.getLogger();

    public EnchantAttr(L1Item item) {
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

            if (!targetItem.getItem().isWeapon()) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            use(pc, useItem, targetItem);
        }
    }

    private void use(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem) {
        try {
            int oldAttrLevel = targetItem.getAttrEnchantLevel();
            int newAttrLevel;

            int attrEnchantLevel = L1CommonUtils.attrEnchantLevelValue(useItem.getItemId(), oldAttrLevel);

            if (attrEnchantLevel == 0) {
                pc.sendPackets(new S_SystemMessage("더이상 속성을 강화할 수 없습니다."));
                return;
            }

            pc.getInventory().removeItem(useItem, 1);

            int grade = L1WeaponUtils.getWeaponAttrLevelGrade(oldAttrLevel);

            int per = EnchantSettingTable.getInstance().getEnchantPer(grade + 1, 0, "attr");

            if (RandomUtils.isWinning(100, per)) {
                pc.sendPackets(new S_ServerMessage(1410, targetItem.getLogName()));
                targetItem.setAttrEnchantLevel(attrEnchantLevel);
            } else {
                pc.sendPackets(new S_ServerMessage(1411, targetItem.getLogName()));
            }

            newAttrLevel = targetItem.getAttrEnchantLevel();

            if (newAttrLevel > oldAttrLevel) {
                pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), 7322));
                Broadcaster.broadcastPacket(pc, new S_EffectLocation(pc.getX(), pc.getY(), 7322));

                CharacterEnchantTable.getInstance().insert(pc, targetItem, oldAttrLevel, newAttrLevel, true, "속성");
            } else {
                CharacterEnchantTable.getInstance().insert(pc, targetItem, oldAttrLevel, oldAttrLevel, false, "속성");
            }

            pc.getInventory().updateItem(targetItem, L1PcInventory.COL_ATTRENCHANTLVL);
            pc.getInventory().saveItem(targetItem, L1PcInventory.COL_ATTRENCHANTLVL);

            pc.sendPackets(new S_ItemStatus(targetItem));

            pc.saveInventory();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
