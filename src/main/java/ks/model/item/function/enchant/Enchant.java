package ks.model.item.function.enchant;

import ks.app.config.prop.CodeConfig;
import ks.commands.gm.GmCommands;
import ks.core.datatables.enchant.CharacterEnchantTable;
import ks.core.datatables.enchantSetting.EnchantSettingTable;
import ks.model.Broadcaster;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ItemStatus;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Enchant extends L1ItemInstance {
    public static final int SCROLL_PER_SAFE_ENCHANT = 50;
    public static final int SCROLL_PER_SAFE_ENCHANT2 = SCROLL_PER_SAFE_ENCHANT - 10;
    public static final int SCROLL_3_PER = 3;

    private static final Logger logger = LogManager.getLogger(Enchant.class);

    public Enchant(L1Item item) {
        super(item);
    }

    public void successEnchant(L1PcInstance pc, L1ItemInstance item, int randomEnchant) {
        boolean isEquippedChange = false;

        if (item.isEquipped()) {
            isEquippedChange = true;
            pc.getInventory().setEquipped(item, false);
        }

        item.setProtection(0);//장비보호추가

        sendSuccessMent(pc, item, randomEnchant);

        pc.setLastEnchantItemId(0, null);

        int oldEnchantLvl = item.getEnchantLevel();
        int newEnchantLvl = item.getEnchantLevel() + randomEnchant;
        int safeEnchant = item.getItem().getSafeEnchant();
        item.setEnchantLevel(newEnchantLvl);
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.saveInventory();

        if (newEnchantLvl > safeEnchant) {
            pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
        }

        if (item.getItem().isWeapon()) {
            if (newEnchantLvl >= 8) {
                L1LogUtils.enchantLog("[인챈트][성공][무기] - {} / 결과:{} / {} -> {}", pc.getName(), L1LogUtils.logItemName(item), oldEnchantLvl, newEnchantLvl);
            }
        }

        if (item.getItem().isArmor()) {
            if (newEnchantLvl >= 6) {
                L1LogUtils.enchantLog("[인챈트][성공][방어] - {} / 결과:{} / {} -> {}", pc.getName(), L1LogUtils.logItemName(item), oldEnchantLvl, newEnchantLvl);
            }
        }

        if (item.getItem().isAccessorie()) {
            L1LogUtils.enchantLog("[인챈트][성공][악세] - {} / 결과:{} / {} -> {}", pc.getName(), L1LogUtils.logItemName(item), oldEnchantLvl, newEnchantLvl);
        }

        if (isEquippedChange) {
            pc.getInventory().setEquipped(item, true);
        }

        if (item.getItem().isWeapon()) {
            enchantWeaponEffect(pc, item);
        } else if (item.getItem().isArmor()) {
            enchantArmorEffect(pc, item);
        } else if (item.getItem().isAccessorie()) {
            enchantAccEffect(pc, item);
        }

        CharacterEnchantTable.getInstance().insert(pc, item, oldEnchantLvl, newEnchantLvl, true);
    }

    public void enchantAccEffect(L1PcInstance pc, L1ItemInstance item) {
        int enchantLevel = item.getEnchantLevel();
        int safeEnchant = item.getItem().getSafeEnchant();
        int gfx = 0;

        if (safeEnchant == 0) {
            if (enchantLevel >= 8) {
                gfx = 8686;
            } else if (enchantLevel >= 7) {
                gfx = 8685;
            } else if (enchantLevel >= 5) {
                gfx = 8684;
            } else if (enchantLevel >= 3) {
                gfx = 8683;
            }
        }

        if (gfx > 0) {
            pc.sendPackets(new S_SkillSound(pc.getId(), gfx));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfx));
        }

        if (enchantLevel >= CodeConfig.ENCHANT_MENT_ACC && CodeConfig.ENCHANT_MENT) {
            L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 +" + enchantLevel + " " + item.getName() + " 강화에 성공하였습니다");
        }
    }

    public void enchantArmorEffect(L1PcInstance pc, L1ItemInstance item) {
        int enchantLevel = item.getEnchantLevel();
        int safeEnchant = item.getItem().getSafeEnchant();
        int gfx = 0;

        if (safeEnchant == 0) {
            if (enchantLevel >= 8) {
                gfx = 8686;
            } else if (enchantLevel >= 7) {
                gfx = 8685;
            } else if (enchantLevel >= 5) {
                gfx = 8684;
            } else if (enchantLevel >= 3) {
                gfx = 8683;
            }
        } else if (safeEnchant == 4) {
            if (enchantLevel >= 9) {
                gfx = 8686;
            } else if (enchantLevel >= 8) {
                gfx = 8685;
            } else if (enchantLevel >= 7) {
                gfx = 8684;
            } else if (enchantLevel >= 5) {
                gfx = 8683;
            }
        } else if (safeEnchant == 6) {
            if (enchantLevel >= 7) {
                gfx = 8676 + enchantLevel;

                if (gfx <= 8683) {
                    gfx = 8683;
                }

                if (gfx >= 8686) {
                    gfx = 8686;
                }
            }
        }

        if (gfx > 0) {
            pc.sendPackets(new S_SkillSound(pc.getId(), gfx));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfx));
        }

        if (enchantLevel >= CodeConfig.ENCHANT_MENT_ARMOR && CodeConfig.ENCHANT_MENT) {
            L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 +" + enchantLevel + " " + item.getName() + " 강화에 성공하였습니다");
        }
    }

    public void enchantWeaponEffect(L1PcInstance pc, L1ItemInstance item) {
        int enchantLevel = item.getEnchantLevel();
        int safeEnchant = item.getItem().getSafeEnchant();
        int gfx = 0;

        if (safeEnchant == 0) {
            if (enchantLevel >= 8) {
                gfx = 8686;
            } else if (enchantLevel >= 7) {
                gfx = 8685;
            } else if (enchantLevel >= 5) {
                gfx = 8684;
            } else if (enchantLevel >= 3) {
                gfx = 8683;
            }
        } else if (safeEnchant == 6) {
            if (enchantLevel >= 7) {
                gfx = 8676 + enchantLevel;

                if (gfx <= 8683) {
                    gfx = 8683;
                }

                if (gfx >= 8686) {
                    gfx = 8686;
                }
            }
        }

        if (gfx > 0) {
            pc.sendPackets(new S_SkillSound(pc.getId(), gfx));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfx));
        }

        if (enchantLevel >= CodeConfig.ENCHANT_MENT_WEAPON && CodeConfig.ENCHANT_MENT) {
            L1World.getInstance().broadcastPacketGreenMessage("어느 아덴용사가 +" + enchantLevel + " " + item.getName() + " 강화에 성공하였습니다");
        }
    }

    public void failureEnchant(L1PcInstance pc, L1ItemInstance item) {
        if (item.getProtection() == 1) {
            if (item.getItem().getType2() == 2 && item.isEquipped()) {
                pc.getAC().addAc(+item.getEnchantLevel());
            }

            item.setProtection(0);
            successEnchant(pc, item, -1);
            pc.sendPackets(new S_ItemStatus(item));
            pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.sendPackets(new S_ServerMessage(1310));
            return;
        }

        String s = "";
        String sa = "";

        int itemType = item.getItem().getType2();
        String nameId = item.getName();
        String pm = "";

        if (itemType == 1) { // 무기
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                s = nameId;
            } else {
                if (item.getEnchantLevel() > 0) {
                    pm = "+";
                }

                s = pm + item.getEnchantLevel() + " " + nameId;
            }

            sa = "$245";
        } else if (itemType == 2) {
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                s = nameId;
            } else {
                if (item.getEnchantLevel() > 0) {
                    pm = "+";
                }

                s = pm + item.getEnchantLevel() + " " + nameId;
            }

            if (item.getItem().getGrade() < 0) {
                sa = " $252";
            } else {
                sa = "$245";
            }
        }

        pc.setLastEnchantItemId(item.getId(), item);
        pc.sendPackets(new S_ServerMessage(164, s, sa));
        pc.getInventory().removeItem(item, item.getCount());
        pc.saveInventory();

        L1LogUtils.enchantLog("[인챈트][실패][증발] - {} / 결과:{} ", pc.getName(), L1LogUtils.logItemName(item));

        CharacterEnchantTable.getInstance().insert(pc, item, item.getEnchantLevel(), item.getEnchantLevel(), false);
    }

    public int blessScrollRandomEnchant(L1ItemInstance item, int itemId) {
        int result = 1;

        if (L1CommonUtils.isBlessScroll(itemId)) {
            if (item.getEnchantLevel() > 0) {
                if (item.getItem().isWeapon()) {
                    if (item.getItem().getSafeEnchant() == 0) {
                        result = enchantLevelWeaponSafe0(item);
                    } else {
                        result = enchantLevelWeaponSafe6(item);
                    }
                } else {
                    if (item.getItem().getSafeEnchant() == 0) {
                        result = enchantLevelArmorSafe0(item);
                    } else if (item.getItem().getSafeEnchant() == 4) {
                        result = enchantLevelArmorSafe4(item);
                    } else {
                        result = enchantLevelArmorSafe6(item);
                    }
                }
            }
        }

        return result;
    }

    private int enchantLevelArmorSafe0(L1ItemInstance item) {
        int per = RandomUtils.nextInt(100) + 1;

        if (item.getEnchantLevel() <= 2) {
            if (per <= SCROLL_3_PER) {
                return 3;
            } else if (per <= SCROLL_PER_SAFE_ENCHANT) {
                return 2;
            }
        } else if (item.getEnchantLevel() <= 3) {
            if (per <= SCROLL_PER_SAFE_ENCHANT) {
                return 2;
            }
        } else if (item.getEnchantLevel() <= 5) {
            if (per <= 40) {
                return 2;
            }
        }

        return 1;
    }

    private int enchantLevelArmorSafe4(L1ItemInstance item) {
        int per = RandomUtils.nextInt(100) + 1;

        if (item.getEnchantLevel() <= 3) {
            if (per <= SCROLL_PER_SAFE_ENCHANT) {
                return 2;
            }
        } else if (item.getEnchantLevel() <= 5) {
            if (per <= SCROLL_PER_SAFE_ENCHANT2) {
                return 2;
            }
        }

        return 1;
    }

    private int enchantLevelArmorSafe6(L1ItemInstance item) {
        int per = RandomUtils.nextInt(100) + 1;

        if (item.getEnchantLevel() <= 5) {
            if (per <= SCROLL_PER_SAFE_ENCHANT) {
                return 2;
            }
        }

        return 1;
    }

    private int enchantLevelWeaponSafe6(L1ItemInstance item) {
        int per = RandomUtils.nextInt(100) + 1;

        if (item.getEnchantLevel() <= 2) {
            if (per <= SCROLL_3_PER) {
                return 3;
            } else if (per <= SCROLL_PER_SAFE_ENCHANT) {
                return 2;
            }
        } else if (item.getEnchantLevel() <= 5) {
            if (per <= 45) {
                return 2;
            }
        }

        return 1;
    }

    private int enchantLevelWeaponSafe0(L1ItemInstance item) {
        int per = RandomUtils.nextInt(100) + 1;

        if (item.getEnchantLevel() <= 2) {
            if (per <= SCROLL_3_PER) {
                return 3;
            } else if (per <= SCROLL_PER_SAFE_ENCHANT) {
                return 2;
            }
        } else if (item.getEnchantLevel() <= 3) {
            if (per <= 35) {
                return 2;
            }
        } else if (item.getEnchantLevel() <= 5) {
            if (per <= 30) {
                return 2;
            }
        }

        return 1;
    }

    public boolean isNotEnableEnchant(L1PcInstance pc, L1ItemInstance targetItem) {
        if (targetItem == null)
            return true;

        if (pc.isInvisible()) {
            pc.sendPackets(new S_SystemMessage("투명상태에서는 인첸트을 할 수 없습니다.."));
            return true;
        }

        if (pc.getLastEnchantItemId() == targetItem.getId()) {
            pc.setLastEnchantItemId(targetItem.getId(), targetItem);
            return true;
        }

        if (targetItem.getBless() >= 128) {
            pc.sendPackets(new S_ServerMessage(79));
            return true;
        }

        if (targetItem.getItem().getSafeEnchant() < 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return true;
        }

        if (targetItem.getItem().isEtc()) {
            pc.sendPackets(new S_ServerMessage(79));
            return true;
        }

        return false;
    }

    public int getEnchantChance(L1PcInstance pc, L1ItemInstance targetItem, String type) {
        int enchantLevel = targetItem.getEnchantLevel();
        int safeEnchant = targetItem.getItem().getSafeEnchant();

        int enchantChance;

        int detailEnchant = EnchantSettingTable.getInstance().getEnchantPerDetail(enchantLevel + 1, targetItem.getItemId(), type);

        if (detailEnchant > 0) {
            enchantChance = detailEnchant;
        } else {
            enchantChance = EnchantSettingTable.getInstance().getEnchantPer(enchantLevel + 1, safeEnchant, type);
        }

        Boolean c = GmCommands.getInstance().isEnchantOnlySuccess(pc.getName());

        if (c != null) {
            if (c) {
                enchantChance = 100;
            } else {
                enchantChance = 0;
            }
        }

        if (pc.isGm()) {
            pc.sendPackets(new S_SystemMessage("\\fY확률 : [ " + enchantChance + " ]"));
        }

        return enchantChance;
    }

    private void sendSuccessMent(L1PcInstance pc, L1ItemInstance item, int randomEnchant) {
        String itemName = item.getName();

        String targetItemName = "";
        String enchantMent1 = "";
        String enchantMent2 = "";
        String enchantStatus = "";

        if (item.getEnchantLevel() > 0) {
            enchantStatus = "+";
        }

        if (item.getItem().getType2() == 1) {
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                switch (randomEnchant) {
                    case -1:
                        targetItemName = itemName;
                        enchantMent1 = "$246";
                        enchantMent2 = "$247";
                        break;
                    case 1:
                        targetItemName = itemName;
                        enchantMent1 = "$245";
                        enchantMent2 = "$247";
                        break;
                    case 2:
                    case 3:
                        targetItemName = itemName;
                        enchantMent1 = "$245";
                        enchantMent2 = "$248";
                        break;
                }
            } else {
                switch (randomEnchant) {
                    case -1:
                        targetItemName = enchantStatus + item.getEnchantLevel() + " " + itemName;
                        enchantMent1 = "$246";
                        enchantMent2 = "$247";
                        break;
                    case 1: // '\001'
                        targetItemName = enchantStatus + item.getEnchantLevel() + " " + itemName;
                        enchantMent1 = "$245";
                        enchantMent2 = "$247";
                        break;
                    case 2: // '\002'
                    case 3: // '\003'
                        targetItemName = enchantStatus + item.getEnchantLevel() + " " + itemName;
                        enchantMent1 = "$245";
                        enchantMent2 = "$248";
                        break;
                }
            }
        } else if (item.getItem().getType2() == 2) {
            if (!item.isIdentified() || item.getEnchantLevel() == 0) {
                switch (randomEnchant) {
                    case -1:
                        targetItemName = itemName;
                        enchantMent1 = "$246";
                        enchantMent2 = "$247";
                        break;
                    case 1: // '\001'
                        if (item.getItem().getGrade() < 0) {
                            targetItemName = itemName;
                            enchantMent1 = "$252";
                            enchantMent2 = "$247 ";
                        } else {
                            targetItemName = itemName;
                            enchantMent1 = "$245";
                            enchantMent2 = "$248 ";
                        }
                        break;
                    case 2: // '\002'

                    case 3: // '\003'
                        targetItemName = itemName;
                        enchantMent1 = "$252";
                        enchantMent2 = "$248 ";
                        break;
                }
            } else {
                switch (randomEnchant) {
                    case -1:
                        targetItemName = enchantStatus + item.getEnchantLevel() + " " + itemName;
                        enchantMent1 = "$246";
                        enchantMent2 = "$247";
                        break;

                    case 1:
                        targetItemName = enchantStatus + item.getEnchantLevel() + " " + itemName;
                        if (item.getItem().getGrade() < 0) {
                            enchantMent1 = "$252";
                            enchantMent2 = "$247 ";
                        } else {
                            enchantMent1 = "$245";
                            enchantMent2 = "$248 ";
                        }
                        break;

                    case 2:
                    case 3:
                        targetItemName = enchantStatus + item.getEnchantLevel() + " " + itemName;
                        enchantMent1 = "$252";
                        enchantMent2 = "$248 ";
                        break;
                }
            }
        }

        pc.sendPackets(new S_ServerMessage(161, targetItemName, enchantMent1, enchantMent2));
    }
}
