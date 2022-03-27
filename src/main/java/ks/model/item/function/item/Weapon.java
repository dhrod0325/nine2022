package ks.model.item.function.item;

import ks.constants.L1PacketBoxType;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.L1PolyMorph;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;

public class Weapon extends L1ItemInstance {

    public Weapon(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            if (useItem.getItem().getType2() == 1) {
                int min = useItem.getItem().getMinLevel();
                int max = useItem.getItem().getMaxLevel();
                if (min != 0 && min > pc.getLevel()) {
                    pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
                } else if (max != 0 && max < pc.getLevel()) {
                    if (max < 50) {
                        pc.sendPackets(new S_PacketBox(L1PacketBoxType.MSG_LEVEL_OVER, max));
                    } else {
                        pc.sendPackets(new S_SystemMessage("이 아이템은" + max + "레벨 이하만 사용할 수 있습니다. "));
                    }
                } else {
                    if (pc.isGm()) {
                        useWeapon(pc, useItem);
                    } else if (L1CommonUtils.itemUseAbleCheck(pc, useItem)) {
                        useWeapon(pc, useItem);
                    } else {
                        pc.sendPackets(new S_ServerMessage(264));
                    }
                }
            }
        }
    }

    private void useWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
        L1PcInventory pcInventory = activeChar.getInventory();
        if (activeChar.getWeapon() == null
                || !activeChar.getWeapon().equals(weapon)) {
            int weapon_type = weapon.getItem().getType();
            int polyid = activeChar.getGfxId().getTempCharGfx();

            if (!L1PolyMorph.isEquipAbleWeapon(polyid, weapon_type)) {
                activeChar.sendPackets(new S_ServerMessage(2055, weapon.getName()));
                return;
            }

            if (weapon.getItem().isTwoHandedWeapon()
                    && pcInventory.getGarderEquipped(2, 7, 13) >= 1) {
                activeChar.sendPackets(new S_ServerMessage(128));
                return;
            }
        }

        activeChar.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제

        if (activeChar.getWeapon() != null) {
            if (activeChar.getWeapon().getBless() == 2) { // 저주해지고 있었을
                activeChar.sendPackets(new S_ServerMessage(150));
                return;
            }
            if (activeChar.getWeapon().equals(weapon)) {
                pcInventory.setEquipped(activeChar.getWeapon(), false, false, false);
                return;
            } else {
                pcInventory.setEquipped(activeChar.getWeapon(), false, false, true);
            }
        }

        if (weapon.getItemId() == 200002) {
            activeChar.sendPackets(new S_ServerMessage(149, weapon.getLogName()));
        }

        pcInventory.setEquipped(weapon, true, false, false);
    }
}

