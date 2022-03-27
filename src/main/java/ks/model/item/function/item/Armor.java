package ks.model.item.function.item;

import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;

public class Armor extends L1ItemInstance {
    public Armor(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = this.getItemId();

            if (pc.getMapId() == L1Map.MAP_FISHING || pc.getMapId() == 5153) {
                if (itemId == 20077 || itemId == 120077 || itemId == 20062 || itemId == 421003 || itemId == 421004 || itemId == 421005 || itemId == 21005 || itemId == 421006 || itemId == 421007 || itemId == 421008 || itemId == 30458) {
                    pc.sendPackets(new S_ServerMessage(1170));
                    return;
                }
            }

            if (useItem.getItem().getType2() == 2) { // 종별：방어용 기구
                if (pc.isGm()) {
                    useArmor(pc, useItem);
                } else if (L1CommonUtils.itemUseAbleCheck(pc, useItem)) {
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
                        useArmor(pc, useItem);
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(264));
                }
            }
        }
    }

    private void useArmor(L1PcInstance activeChar, L1ItemInstance armor) {
        int type = armor.getItem().getType();

        L1PcInventory inv = activeChar.getInventory();

        boolean equipSpace;

        int slotcount = 1;
        int slotear = 1;

        slotcount += 1;
        slotcount += 1;
        slotear += 1;

        if (type == 9) {
            equipSpace = inv.getTypeEquipped(2, 9) <= slotcount;
        } else {
            equipSpace = inv.getTypeEquipped(2, type) <= 0;
        }

        if (type == 12) {
            equipSpace = inv.getTypeEquipped(0, 12) <= slotear;
        }

        if (type == 4) {
            if (armor.getItemId() != 20077) {
                for (L1ItemInstance item : inv.getItems()) {
                    if (item.getItemId() == 20077 && item.isEquipped()) {
                        inv.setEquipped(item, false);
                    }
                }
            }

            if (armor.getItemId() != 20062) {
                for (L1ItemInstance item : inv.getItems()) {
                    if (item.getItemId() == 20062 && item.isEquipped()) {
                        inv.setEquipped(item, false);
                    }
                }
            }
        }

        if (equipSpace && !armor.isEquipped()) {
            if (type == 9) {
                if (inv.getTypeEquipped(2, 9) == 4) {
                    activeChar.sendPackets(new S_SystemMessage("더 이상 착용이 불가능합니다."));
                    return;
                }

                if (inv.getTypeAndItemIdEquipped(2, 9, armor.getItem().getItemId()) == 2) {
                    activeChar.sendPackets(new S_SystemMessage("동일한 이름의 아이템은 최대 2개까지 착용 가능합니다."));
                    return;
                } else if (inv.getTypeAndGradeEquipped(2, 9, armor.getItem().getGrade()) == 2) {
                    activeChar.sendPackets(new S_SystemMessage("더 이상 착용이 불가능합니다."));
                    return;
                }
            }

            if (type == 12) {
                if (inv.getTypeEquipped(2, 12) == 1) {
                    activeChar.sendPackets(new S_SystemMessage("더 이상 착용이 불가능합니다."));
                    return;
                }
            }

            if (inv.getTypeAndItemIdEquipped(2, 12, armor.getItem().getItemId()) == 1) {
                activeChar.sendPackets(new S_SystemMessage("동일한 이름의 귀걸이는 하나만 착용이 가능합니다."));
                return;
            }

            int polyId = activeChar.getGfxId().getTempCharGfx();

            if (!L1PolyMorph.isEquipableArmor(polyId, type)) {
                activeChar.sendPackets(new S_ServerMessage(2055, armor.getName()));
                return;
            }

            logger.debug("type:{}", type);

            if (type == 7 && inv.getTypeEquipped(2, 13) >= 1 ||
                    type == 13 && inv.getTypeEquipped(2, 7) >= 1) {
                activeChar.sendPackets(new S_ServerMessage(124));
                return;
            }

            if (type == 7 && activeChar.getWeapon() != null) {
                if (activeChar.getWeapon().getItem().isTwoHandedWeapon() && armor.getItem().getUseType() != 13) {
                    activeChar.sendPackets(new S_ServerMessage(129));
                    return;
                }
            }

            activeChar.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
            inv.setEquipped(armor, true);
        } else if (armor.isEquipped()) {
            if (armor.getBless() == 2) {
                activeChar.sendPackets(new S_ServerMessage(150));
                return;
            }

            if (type == 7) {
                if (activeChar.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) {
                    activeChar.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.SOLID_CARRIAGE);
                }
            }

            inv.setEquipped(armor, false);
        } else {
            if (armor.getItem().getType2() == 2) {
                int itemType = armor.getItem().getType();

                switch (itemType) {
                    case 1:
                        changeEqItem(inv, inv.getCurrentItem().getHelm(), armor);
                        break;
                    case 2:
                        changeEqItem(inv, inv.getCurrentItem().getArmor(), armor);
                        break;
                    case 3:
                        changeEqItem(inv, inv.getCurrentItem().getShirt(), armor);
                        break;
                    case 4:
                        changeEqItem(inv, inv.getCurrentItem().getCloak(), armor);
                        break;
                    case 5:
                        changeEqItem(inv, inv.getCurrentItem().getGlove(), armor);
                        break;
                    case 6:
                        changeEqItem(inv, inv.getCurrentItem().getBoots(), armor);
                        break;
                    case 7:
                    case 13:
                        changeEqItem(inv, inv.getCurrentItem().getShield(), armor);
                        break;
                    case 8:
                        changeEqItem(inv, inv.getCurrentItem().getNecklace(), armor);
                        break;
                    case 10:
                        changeEqItem(inv, inv.getCurrentItem().getBelt(), armor);
                        break;
                    default:
                        activeChar.sendPackets(new S_ServerMessage(124));
                        break;
                }
            }
        }

        activeChar.setCurrentHp(activeChar.getCurrentHp());
        activeChar.setCurrentMp(activeChar.getCurrentMp());
        activeChar.sendPackets(new S_OwnCharAttrDef(activeChar));
        activeChar.sendPackets(new S_OwnCharStatus(activeChar));
        activeChar.sendPackets(new S_SPMR(activeChar));

        L1ItemDelay.onItemUse(activeChar, armor); // 아이템 지연 개시
    }

    private void changeEqItem(L1PcInventory inv, L1ItemInstance oldArmor, L1ItemInstance newArmor) {
        if (oldArmor != null) {
            inv.setEquipped(oldArmor, false);
        }

        inv.setEquipped(newArmor, true);
    }
}
