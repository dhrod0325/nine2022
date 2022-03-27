package ks.model;

import ks.constants.L1Options;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.ArmorSetTable;
import ks.core.datatables.SkillsTable;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1DollInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.item.function.potion.BravePotion;
import ks.model.pc.L1DamageCheck;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static ks.constants.L1SkillId.*;

public class L1EquipmentSlot {
    private final L1PcInstance owner;

    private final List<L1ArmorSet> currentArmorSet = new ArrayList<>();
    private final List<L1ItemInstance> armors = new ArrayList<>();
    private L1ItemInstance weapon;

    public L1EquipmentSlot(L1PcInstance owner) {
        this.owner = owner;
    }

    private void removeWeapon(L1ItemInstance item) {
        int polyId = owner.getGfxId().getTempCharGfx();

        owner.setWeapon(null);
        owner.setCurrentWeapon(0);
        owner.sendPackets(new S_SkillIconGFX(polyId, false));
        owner.setDamageCheck(new L1DamageCheck());

        item.stopEquipmentTimer();
        this.weapon = null;

        if (item.getItemId() >= 11011 && item.getItemId() <= 11013) {
            L1PolyMorph.undoPoly(owner);
        }

        if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
            owner.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.COUNTER_BARRIER);
        }

        if (item.getDurability() > 0) {
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.DURABILITY, 0));
        }

        if (item.getItemId() == 532) {
            L1PolyMorph.undoPoly(owner);
            owner.getResistance().addFire(-25);
        }

        if (item.getDmgUp() > 0) {
            owner.addDmgUp(-item.getDmgUp());
        }

        if (item.getBowDmgUp() > 0) {
            owner.addBowDmgUp(-item.getBowDmgUp());
        }

        if (item.getHitUp() > 0) {
            owner.addHitUp(-item.getHitUp());
        }

        if (item.getBowHitup() > 0) {
            owner.addBowHitup(-item.getBowHitup());
        }

        if (item.getAddDmgUp() > 0) {
            owner.addDmgUp(-item.getAddDmgUp());
            owner.addBowDmgUp(-item.getAddDmgUp());
        }

        if (item.getAddStunHit() > 0) {
            owner.addAddStunHit(-item.getAddStunHit());
        }

        if (item.getSkillIcon() > 0) {
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, item.getSkillIcon(), false));
        }

        owner.getInventory().initArrow();

        int optionGrade = item.getOptionGrade();

        if (optionGrade == L1Options.무기옵션_추가대미지1) {
            owner.addDmgUp(-1);
            owner.addBowDmgUp(-1);
        } else if (optionGrade == L1Options.무기옵션_추가대미지2) {
            owner.addDmgUp(-2);
            owner.addBowDmgUp(-2);
        } else if (optionGrade == L1Options.무기옵션_추가대미지3) {
            owner.addDmgUp(-3);
            owner.addBowDmgUp(-3);
        } else if (optionGrade == L1Options.무기옵션_명중1) {
            owner.addHitUp(-1);
            owner.addBowHitup(-1);
        } else if (optionGrade == L1Options.무기옵션_명중2) {
            owner.addHitUp(-2);
            owner.addBowHitup(-2);
        } else if (optionGrade == L1Options.무기옵션_명중3) {
            owner.addHitUp(-3);
            owner.addBowHitup(-3);
        } else if (optionGrade == L1Options.무기옵션_sp1) {
            owner.getAbility().addSp(-1);
        } else if (optionGrade == L1Options.무기옵션_sp2) {
            owner.getAbility().addSp(-2);
        } else if (optionGrade == L1Options.무기옵션_sp3) {
            owner.getAbility().addSp(-3);
        }
    }

    private void setArmor(L1ItemInstance item) {
        L1Item template = item.getItem();

        int itemId = item.getItem().getItemId();

        owner.getAC().addAc(item.getAc());

        owner.addDamageReductionByArmor(item.getDamageReductionByArmor());
        owner.addWeightReduction(item.getWeightReduction());

        owner.addHitUpByArmor(item.getHitUpByArmor());
        owner.addDmgUpByArmor(item.getDmgUpByArmor());

        owner.addBowHitupByArmor(item.getBowHitUpByArmor());
        owner.addBowDmgupByArmor(item.getBowDmgUpByArmor());

        owner.addAddDmgUpByArmor(item.getAddDmgUpByArmor());
        owner.addAddHitUpByArmor(item.getAddHitUpByArmor());

        owner.addAddPvpDmgUp(item.getPvpDamage());
        owner.addAddPvpReudction(item.getPvpReduction());
        owner.addAddMagicHitUp(item.getAddMagicHitUp());

        owner.getResistance().addEarth(template.getDefenseEarth());
        owner.getResistance().addWind(template.getDefenseWind());
        owner.getResistance().addWater(template.getDefenseWater());
        owner.getResistance().addFire(template.getDefenseFire());

        owner.getResistance().addStun(item.getRegistStun());

        owner.getResistance().addPetrifaction(template.getRegistStone());
        owner.getResistance().addSleep(template.getRegistSleep());
        owner.getResistance().addFreeze(template.getRegistFreeze());
        owner.getResistance().addHold(template.getRegistSustAin());
        owner.getResistance().addBlind(template.getRegistBlind());
        owner.getResistance().addElf(item.getRegistElf());
        owner.addAddPotionPer(item.getAddPotionPer());
        owner.addExpBonus(item.getAddExp());

        owner.addAddEr(item.getAddEr());
        owner.addCriticalPer(item.getCriticalPer());
        owner.addBowCriticalPer(item.getBowCriticalPer());

        armors.add(item);

        for (L1ArmorSet armorSet : ArmorSetTable.getInstance().getAllSet()) {
            if (armorSet.isPartOfSet(itemId) && armorSet.isValid(owner)) {
                if (item.getItem().getType2() == 2 && item.getItem().getType() == 9) {
                    if (!armorSet.isEquippedRingOfArmorSet(owner)) {
                        armorSet.giveEffect(owner);
                        currentArmorSet.add(armorSet);
                    }
                } else {
                    armorSet.giveEffect(owner);
                    currentArmorSet.add(armorSet);
                }
            }
        }

        if (itemId == 20284 || itemId == 120284) {
            owner.sendPackets(new S_Ability(5, true));
        }

        if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
            if (!owner.isGmInvis()) {
                for (L1DollInstance doll : owner.getDollList().values()) {
                    doll.deleteDoll();
                    owner.sendPackets(new S_SkillIconGFX(56, 0));
                    owner.sendPackets(new S_OwnCharStatus(owner));
                }

                L1MagicUtils.startInvisible(owner);
            }
        }

        if (itemId == 20288) {
            owner.sendPackets(new S_Ability(1, true));
        }

        if (itemId == 20036) {
            owner.sendPackets(new S_Ability(3, true));
        }

        if (itemId == 20207) {
            owner.sendPackets(new S_SkillIconBlessOfEva(owner.getId(), -1));
        }

        if (itemId == 20383) {
            if (item.getChargeCount() != 0) {
                item.setChargeCount(item.getChargeCount() - 1);
                owner.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
            }
        }

        if (L1CommonUtils.isArmor(item)) {
            if (item.getSkillIcon() > 0) {
                owner.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, item.getSkillIcon(), true));
            }
        }

        if (itemId == 55000055) {
            owner.setEquipServerRune(true);
        }

        item.startEquipmentTimer(owner);
    }

    private void removeArmor(L1ItemInstance item) {
        L1Item template = item.getItem();
        int itemId = item.getItem().getItemId();

        owner.getAC().addAc(-item.getAc());

        owner.addDamageReductionByArmor(-item.getDamageReductionByArmor());
        owner.addWeightReduction(-item.getWeightReduction());

        owner.addHitUpByArmor(-item.getHitUpByArmor());
        owner.addDmgUpByArmor(-item.getDmgUpByArmor());

        owner.addBowHitupByArmor(-item.getBowHitUpByArmor());
        owner.addBowDmgupByArmor(-item.getBowDmgUpByArmor());

        owner.addAddDmgUpByArmor(-item.getAddDmgUpByArmor());
        owner.addAddHitUpByArmor(-item.getAddHitUpByArmor());

        owner.addAddPvpDmgUp(-item.getPvpDamage());
        owner.addAddPvpReudction(-item.getPvpReduction());

        owner.addAddMagicHitUp(-item.getAddMagicHitUp());

        owner.getResistance().addEarth(-template.getDefenseEarth());
        owner.getResistance().addWind(-template.getDefenseWind());
        owner.getResistance().addWater(-template.getDefenseWater());
        owner.getResistance().addFire(-template.getDefenseFire());

        owner.getResistance().addStun(-item.getRegistStun());

        owner.getResistance().addPetrifaction(-template.getRegistStone());
        owner.getResistance().addSleep(-template.getRegistSleep());
        owner.getResistance().addFreeze(-template.getRegistFreeze());
        owner.getResistance().addHold(-template.getRegistSustAin());
        owner.getResistance().addBlind(-template.getRegistBlind());
        owner.getResistance().addElf(-item.getRegistElf());
        owner.addAddPotionPer(-item.getAddPotionPer());
        owner.addExpBonus(-item.getAddExp());
        owner.addAddEr(-item.getAddEr());

        owner.addCriticalPer(-item.getCriticalPer());
        owner.addBowCriticalPer(-item.getBowCriticalPer());

        for (L1ArmorSet armorSet : ArmorSetTable.getInstance().getAllSet()) {
            if (armorSet.isPartOfSet(itemId) && currentArmorSet.contains(armorSet) && !armorSet.isValid(owner)) {
                armorSet.cancelEffect(owner);
                currentArmorSet.remove(armorSet);
            }
        }

        if (itemId == 20284 || itemId == 120284) {
            owner.sendPackets(new S_Ability(5, false));
        }

        if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
            L1MagicUtils.stopInvisible(owner);
            owner.getSkillEffectTimerSet().setSkillEffect(STATUS_INVISI_OFF, 3000);
        }

        if (itemId == 20288) {
            owner.sendPackets(new S_Ability(1, false));
        }
        if (itemId == 20036) {
            owner.sendPackets(new S_Ability(3, false));
        }
        if (itemId == 20207) {
            owner.sendPackets(new S_SkillIconBlessOfEva(owner.getId(), 0));
        }

        if (L1CommonUtils.isArmor(item)) {
            if (item.getSkillIcon() > 0) {
                owner.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, item.getSkillIcon(), false));
            }
        }

        if (itemId == 55000055) {
            owner.setEquipServerRune(false);
        }

        item.stopEquipmentTimer();

        armors.remove(item);
    }

    public void set(L1ItemInstance item) {
        L1Item template = item.getItem();

        if (template.getType2() == 0) {
            return;
        }

        if (item.getAddHp() != 0) {
            owner.addMaxHp(item.getAddHp());
        }

        if (item.getAddMp() != 0) {
            owner.addMaxMp(item.getAddMp());
        }

        owner.getAbility().addAddedStr(item.getAddStr());
        owner.getAbility().addAddedCon(item.getAddCon());
        owner.getAbility().addAddedDex(item.getAddDex());
        owner.getAbility().addAddedInt(item.getAddInt());
        owner.getAbility().addAddedWis(item.getAddWis());

        if (template.getAddWis() != 0) {
            owner.getPcExpManager().resetMr();
        }

        owner.getAbility().addAddedCha(template.getAddCha());

        int addMr = 0;

        addMr += item.getMr();

        if (addMr != 0) {
            owner.getResistance().addMr(addMr);
            owner.sendPackets(new S_SPMR(owner));
        }

        if (item.getAddSp() != 0) {
            owner.getAbility().addSp(item.getAddSp());
            owner.sendPackets(new S_SPMR(owner));
        }

        if (template.isHasteItem()) {
            owner.addHasteItemEquipped(1);
            owner.removeHasteSkillEffect();

            if (owner.getMoveState().getMoveSpeed() != 1) {
                owner.getMoveState().setMoveSpeed(1);
                owner.sendPackets(new S_SkillHaste(owner.getId(), 1, -1));
                Broadcaster.broadcastPacket(owner, new S_SkillHaste(owner.getId(), 1, 0));
            }
        }

        if (template.getItemId() == 20383) {
            BravePotion.removeBraveStatus(owner, STATUS_BRAVE);
        }

        owner.getEquipSlot().setMagicHelm(item);

        if (template.getType2() == 1) {
            setWeapon(item);
        } else if (template.getType2() == 2) {
            setArmor(item);
            owner.sendPackets(new S_SPMR(owner));
        }
    }

    public void remove(L1ItemInstance itemInstance) {
        L1Item template = itemInstance.getItem();

        if (template.getType2() == 0) {
            return;
        }

        if (itemInstance.getAddHp() != 0) {
            owner.addMaxHp(-itemInstance.getAddHp());
        }
        if (itemInstance.getAddMp() != 0) {
            owner.addMaxMp(-itemInstance.getAddMp());
        }

        owner.getAbility().addAddedStr((byte) -itemInstance.getAddStr());
        owner.getAbility().addAddedCon((byte) -itemInstance.getAddCon());
        owner.getAbility().addAddedDex((byte) -itemInstance.getAddDex());
        owner.getAbility().addAddedInt((byte) -itemInstance.getAddInt());
        owner.getAbility().addAddedWis((byte) -itemInstance.getAddWis());

        if (template.getAddWis() != 0) {
            owner.getPcExpManager().resetMr();
        }

        owner.getAbility().addAddedCha((byte) -template.getAddCha());

        int addMr = 0;
        addMr -= itemInstance.getMr();

        if (addMr != 0) {
            owner.getResistance().addMr(addMr);
            owner.sendPackets(new S_SPMR(owner));
        }

        if (itemInstance.getAddSp() != 0) {
            owner.getAbility().addSp(-itemInstance.getAddSp());
            owner.sendPackets(new S_SPMR(owner));
        }

        if (template.isHasteItem()) {
            owner.addHasteItemEquipped(-1);
            if (owner.getHasteItemEquipped() == 0) {
                owner.getMoveState().setMoveSpeed(0);
                owner.sendPackets(new S_SkillHaste(owner.getId(), 0, 0));
                Broadcaster.broadcastPacket(owner, new S_SkillHaste(owner.getId(), 0, 0));
            }
        }

        owner.getEquipSlot().removeMagicHelm(owner.getId(), itemInstance);

        if (template.getType2() == 1) {
            removeWeapon(itemInstance);
        } else if (template.getType2() == 2) {
            removeArmor(itemInstance);
        }
    }

    public boolean isEquipedContain(Integer... itemIds) {
        for (Integer itemId : itemIds) {
            if (isEquiped(itemId)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEquiped(int itemId) {
        if (weapon != null && weapon.getItemId() == itemId) {
            return true;
        }

        for (L1ItemInstance item : armors) {
            if (item.isEquipped() && item.getItemId() == itemId) {
                return true;
            }
        }

        return false;
    }

    public L1ItemInstance getEquipedItem(int itemId) {
        if (weapon != null && weapon.getItemId() == itemId) {
            return weapon;
        }

        for (L1ItemInstance item : armors) {
            if (item.isEquipped() && item.getItemId() == itemId) {
                return item;
            }
        }

        return null;
    }

    public void setMagicHelm(L1ItemInstance item) {
        switch (item.getItemId()) {
            case 20008:
                owner.setSkillMastery(HASTE);
                owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20013:
                owner.setSkillMastery(PHYSICAL_ENCHANT_DEX);
                owner.setSkillMastery(HASTE);
                owner.sendPackets(new S_AddSkill(0, 0, 0, 2, 0, 4, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20014:
                owner.setSkillMastery(HEAL);
                owner.setSkillMastery(EXTRA_HEAL);
                owner.sendPackets(new S_AddSkill(1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20015:
                owner.setSkillMastery(ENCHANT_WEAPON);
                owner.setSkillMastery(DETECTION);
                owner.setSkillMastery(PHYSICAL_ENCHANT_STR);
                owner.setSkillMastery(EYE_OF_DRAGON);//용기사 디텍션
                owner.setSkillMastery(EYES_BREAK);//환술사 디텍션

                owner.sendPackets(new S_AddSkill(0, 24, 0, 0, 0, 2, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
            case 20023:
                owner.setSkillMastery(GREATER_HASTE);
                owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                break;
        }
    }

    public void removeMagicHelm(int objectId, L1ItemInstance item) {
        switch (item.getItemId()) {
            case 20008:
                helmHasteRemove(objectId);
                break;
            case 20013:
                if (!SkillsTable.getInstance().spellCheck(objectId, PHYSICAL_ENCHANT_DEX)) {
                    owner.removeSkillMastery(PHYSICAL_ENCHANT_DEX);
                    owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                helmHasteRemove(objectId);
                break;
            case 20014:
                if (!SkillsTable.getInstance().spellCheck(objectId, HEAL)) {
                    owner.removeSkillMastery(HEAL);
                    owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                if (!SkillsTable.getInstance().spellCheck(objectId, EXTRA_HEAL)) {
                    owner.removeSkillMastery(EXTRA_HEAL);
                    owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
            case 20015:
                if (!SkillsTable.getInstance().spellCheck(objectId, ENCHANT_WEAPON)) {
                    owner.removeSkillMastery(ENCHANT_WEAPON);
                    owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }

                removeDetection(objectId, DETECTION);

                if (!SkillsTable.getInstance().spellCheck(objectId, PHYSICAL_ENCHANT_STR)) {
                    owner.removeSkillMastery(PHYSICAL_ENCHANT_STR);
                    owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }

                removeDetection(objectId, EYE_OF_DRAGON);
                removeDetection(objectId, EYES_BREAK);
                break;
            case 20023:
                if (!SkillsTable.getInstance().spellCheck(objectId, GREATER_HASTE)) {
                    owner.removeSkillMastery(GREATER_HASTE);
                    owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
                break;
        }
    }

    private void removeDetection(int objectId, int eyesBreak) {
        if (!SkillsTable.getInstance().spellCheck(objectId, eyesBreak)) {
            owner.removeSkillMastery(eyesBreak);
            owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
    }

    private void helmHasteRemove(int objectId) {
        if (!SkillsTable.getInstance().spellCheck(objectId, HASTE)) {
            owner.removeSkillMastery(HASTE);
            owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
    }

    public L1ItemInstance getWeapon() {
        return weapon;
    }

    private void setWeapon(L1ItemInstance item) {
        int polyId = owner.getGfxId().getTempCharGfx();

        owner.setWeapon(item);
        owner.setDamageCheck(new L1DamageCheck());

        //데스
        if (polyId == 6137
                || polyId == 6142
                || polyId == 6147
                || polyId == 6152
                || polyId == 6157
                || polyId == 9205
                || polyId == 9206
                || polyId == 6775
                || polyId == 3784
                || polyId == 11232
                || polyId == 11234
                || polyId == 11236
                || polyId == 11375
                || polyId == 11623
                || polyId == 11631
                || polyId == 11653
                || polyId == 11735 || polyId == 12229 || polyId == 12230 || polyId == 12231 || polyId == 12232 || polyId == 6276) {
            if (item.getItem().getType1() == 24) {
                owner.setCurrentWeapon(50);
                owner.sendPackets(new S_SkillIconGFX(polyId, true));
            } else {
                owner.setCurrentWeapon(item.getItem().getType1());
            }

            if (item.getItemId() == 333) {
                L1PolyMorph.doPoly(owner, 12542, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
            }
        } else {
            owner.setCurrentWeapon(item.getItem().getType1());
        }

        item.startEquipmentTimer(owner);

        this.weapon = item;

        if (item.getItemId() >= 11011 && item.getItemId() <= 11013) {
            L1PolyMorph.doPoly(owner, 8768, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
        }

        if (item.getDurability() > 0) {
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.DURABILITY, 1));
        }

        if (owner.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DANCING_BLADES)) {
            if (owner.getWeapon() == null || owner.getWeapon().getItem().getType1() != 4 && owner.getWeapon().getItem().getType1() != 46) {
                owner.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.DANCING_BLADES);
                owner.getMoveState().setBraveSpeed(0);
                owner.sendPackets(new S_ServerMessage(2168));
                owner.sendPackets(new S_SkillBrave(owner.getId(), 0, 0));
                Broadcaster.broadcastPacket(owner, new S_SkillBrave(owner.getId(), 0, 0));
            }
        }

        if (item.getItemId() == 532) {
            L1PolyMorph.doPoly(owner, 12232, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
            owner.getResistance().addFire(25);
            owner.sendPackets(new S_ServerMessage(149, "$데스나이트의 불검:진"));//18621
        }

        if (item.getDmgUp() > 0) {
            owner.addDmgUp(item.getDmgUp());
        }

        if (item.getBowDmgUp() > 0) {
            owner.addBowDmgUp(item.getBowDmgUp());
        }

        if (item.getHitUp() > 0) {
            owner.addHitUp(item.getHitUp());
        }

        if (item.getBowHitup() > 0) {
            owner.addBowHitup(item.getBowHitup());
        }

        if (item.getAddDmgUp() > 0) {
            owner.addDmgUp(item.getAddDmgUp());
            owner.addBowDmgUp(item.getAddDmgUp());
        }

        if (item.getAddStunHit() > 0) {
            owner.addAddStunHit(item.getAddStunHit());
        }

        if (item.getSkillIcon() > 0) {
            owner.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, item.getSkillIcon(), true));
        }

        owner.getInventory().initArrow();

        int optionGrade = item.getOptionGrade();
        if (optionGrade == L1Options.무기옵션_추가대미지1) {
            owner.addDmgUp(1);
            owner.addBowDmgUp(1);
        } else if (optionGrade == L1Options.무기옵션_추가대미지2) {
            owner.addDmgUp(2);
            owner.addBowDmgUp(2);
        } else if (optionGrade == L1Options.무기옵션_추가대미지3) {
            owner.addDmgUp(3);
            owner.addBowDmgUp(3);
        } else if (optionGrade == L1Options.무기옵션_명중1) {
            owner.addHitUp(1);
            owner.addBowHitup(1);
        } else if (optionGrade == L1Options.무기옵션_명중2) {
            owner.addHitUp(2);
            owner.addBowHitup(2);
        } else if (optionGrade == L1Options.무기옵션_명중3) {
            owner.addHitUp(3);
            owner.addBowHitup(3);
        } else if (optionGrade == L1Options.무기옵션_sp1) {
            owner.getAbility().addSp(1);
        } else if (optionGrade == L1Options.무기옵션_sp2) {
            owner.getAbility().addSp(2);
        } else if (optionGrade == L1Options.무기옵션_sp3) {
            owner.getAbility().addSp(3);
        }

    }

    public List<L1ItemInstance> getEquipedItems() {
        List<L1ItemInstance> result = new ArrayList<>(armors);
        if (weapon != null)
            result.add(weapon);

        return result;
    }

    public boolean isEquiedHasteItem() {
        List<L1ItemInstance> items = getEquipedItems();

        for (L1ItemInstance item : items) {
            if (item.getItem().isHasteItem()) {
                return true;
            }
        }

        return false;
    }

    public List<L1ItemInstance> getArmors() {
        return armors;
    }

}
