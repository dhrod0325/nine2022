package ks.model;

import ks.constants.L1SkillIcon;
import ks.constants.L1SkillId;
import ks.core.datatables.ArmorSetTable;
import ks.core.datatables.PolyTable;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.log.L1LogUtils;

import java.util.HashMap;
import java.util.Map;

public class L1PolyMorph {
    public static final int MORPH_BY_ITEMMAGIC = 1;
    public static final int MORPH_BY_GM = 2;
    public static final int MORPH_BY_NPC = 4;
    public static final int MORPH_BY_LOGIN = 0;
    private static final int DAGGER_EQUIP = 1;
    private static final int SWORD_EQUIP = 2;
    private static final int TWOHANDSWORD_EQUIP = 4;
    private static final int AXE_EQUIP = 8;
    private static final int SPEAR_EQUIP = 16;
    private static final int STAFF_EQUIP = 32;
    private static final int EDORYU_EQUIP = 64;
    private static final int CLAW_EQUIP = 128;
    private static final int BOW_EQUIP = 256;
    private static final int KIRINGKU_EQUIP = 512;
    private static final int CHAINSWORD_EQUIP = 1024;
    private static final int TWOHANDKIRINGKU_EQUIP = 512;
    // armor equip bit
    private static final int HELM_EQUIP = 1;
    private static final int AMULET_EQUIP = 2;
    private static final int EARRING_EQUIP = 4;
    private static final int TSHIRT_EQUIP = 8;
    private static final int ARMOR_EQUIP = 16;
    private static final int CLOAK_EQUIP = 32;
    private static final int BELT_EQUIP = 64;
    private static final int SHIELD_EQUIP = 128;
    private static final int GARDER_EQUIP = 128;
    private static final int GLOVE_EQUIP = 256;
    private static final int RING_EQUIP = 512;
    private static final int BOOTS_EQUIP = 1024;
    private static final int GUARDER_EQUIP = 2048;
    private static final Map<Integer, Integer> weaponFlgMap = new HashMap<>();
    private static final Map<Integer, Integer> armorFlgMap = new HashMap<>();

    static {
        weaponFlgMap.put(1, SWORD_EQUIP);
        weaponFlgMap.put(2, DAGGER_EQUIP);
        weaponFlgMap.put(3, TWOHANDSWORD_EQUIP);
        weaponFlgMap.put(4, BOW_EQUIP);
        weaponFlgMap.put(5, SPEAR_EQUIP);
        weaponFlgMap.put(6, AXE_EQUIP);
        weaponFlgMap.put(7, STAFF_EQUIP);
        weaponFlgMap.put(8, BOW_EQUIP);
        weaponFlgMap.put(9, BOW_EQUIP);
        weaponFlgMap.put(10, BOW_EQUIP);
        weaponFlgMap.put(11, CLAW_EQUIP);
        weaponFlgMap.put(12, EDORYU_EQUIP);
        weaponFlgMap.put(13, BOW_EQUIP);
        weaponFlgMap.put(14, SPEAR_EQUIP);
        weaponFlgMap.put(15, AXE_EQUIP);
        weaponFlgMap.put(16, STAFF_EQUIP);
        weaponFlgMap.put(17, KIRINGKU_EQUIP);
        weaponFlgMap.put(18, CHAINSWORD_EQUIP);
        weaponFlgMap.put(19, TWOHANDKIRINGKU_EQUIP);
    }

    static {
        armorFlgMap.put(1, HELM_EQUIP);
        armorFlgMap.put(2, ARMOR_EQUIP);
        armorFlgMap.put(3, TSHIRT_EQUIP);
        armorFlgMap.put(4, CLOAK_EQUIP);
        armorFlgMap.put(5, GLOVE_EQUIP);
        armorFlgMap.put(6, BOOTS_EQUIP);
        armorFlgMap.put(7, SHIELD_EQUIP);
        armorFlgMap.put(8, GARDER_EQUIP);
        armorFlgMap.put(9, AMULET_EQUIP);
        armorFlgMap.put(10, RING_EQUIP);
        armorFlgMap.put(11, BELT_EQUIP);
        armorFlgMap.put(12, EARRING_EQUIP);
        armorFlgMap.put(13, GUARDER_EQUIP);
    }

    private final int id;

    private final String name;

    private final int polyId;

    private final int minLevel;

    private final int weaponEquipFlg;

    private final int armorEquipFlg;

    private final boolean canUseSkill;

    private final int causeFlg;

    public L1PolyMorph(int id, String name, int polyId, int minLevel,
                       int weaponEquipFlg, int armorEquipFlg, boolean canUseSkill,
                       int causeFlg) {
        this.id = id;
        this.name = name;
        this.polyId = polyId;
        this.minLevel = minLevel;
        this.weaponEquipFlg = weaponEquipFlg;
        this.armorEquipFlg = armorEquipFlg;
        this.canUseSkill = canUseSkill;
        this.causeFlg = causeFlg;
    }

    public static void handleCommands(L1PcInstance pc, String s) {
        if (pc == null || pc.isDead()) {
            return;
        }

        L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);

        if (poly != null || s.equals("none")) {
            if (s.equals("none")) {

                pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.SHAPE_CHANGE);
                pc.sendPackets(new S_CloseList(pc.getId()));
            } else if (poly.getMinLevel() == 100) {
                pc.sendPackets(new S_CloseList(pc.getId()));
            } else if (pc.getLevel() >= poly.getMinLevel() || pc.isGm()) { // 변신이벤트
                doPoly(pc, poly.getPolyId(), 7200, MORPH_BY_ITEMMAGIC);
                pc.sendPackets(new S_CloseList(pc.getId()));
            } else {
                pc.sendPackets(new S_ServerMessage(181));
            }
        }
    }

    public static void doPolyByItemMagic(L1PcInstance pc, String s, int time) {
        if (pc == null || pc.isDead()) {
            return;
        }

        L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);

        if (poly != null) {
            doPoly(pc, poly.getPolyId(), time, MORPH_BY_ITEMMAGIC);
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    public static void doPoly(L1Character cha, int polyId, int timeSecs, int cause) {
        if (cha == null || cha.isDead()) {
            return;
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (pc.getMapId() == L1Map.MAP_FISHING) {
                pc.sendPackets(new S_ServerMessage(1170));
                return;
            }

            if (!isMatchCause(polyId, cause)) {
                pc.sendPackets(new S_ServerMessage(181));
                return;
            }

            for (L1ItemInstance armor : pc.getEquipSlot().getArmors()) {
                if (armor == null) {
                    continue;
                }

                for (int itemId = 421000; itemId <= 421023; itemId++) {
                    if (armor.getItemId() == itemId && armor.isEquipped()) {
                        L1ArmorSets set = ArmorSetTable.getInstance().findOne(itemId);
                        if (polyId != set.getPolyId()) {
                            return;
                        }
                    }
                }
            }

            pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
            pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);

            if (pc.getGfxId().getTempCharGfx() != polyId) {
                L1ItemInstance weapon = pc.getWeapon();
                boolean weaponTakeoff = (weapon != null && !isEquipAbleWeapon(polyId, weapon.getItem().getType()));

                pc.getGfxId().setTempCharGfx(polyId);
                pc.sendPackets(new S_ChangeShape(pc.getId(), polyId, weaponTakeoff));

                if (!pc.isGmInvis() && !pc.isInvisible()) {
                    Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), polyId));
                }

                pc.getInventory().takeoffEquip(polyId);
                weapon = pc.getWeapon();

                if (weapon != null) {
                    pc.sendPackets(new S_CharVisualUpdate(pc));
                    Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
                }
            }

            L1LogUtils.gmLog(pc, "POLY ID : {} , TIME : {}", polyId, timeSecs);

            pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.변신, timeSecs));
        } else if (cha instanceof L1MonsterInstance) {
            L1MonsterInstance mob = (L1MonsterInstance) cha;

            mob.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);
            mob.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SHAPE_CHANGE, timeSecs * 1000);

            if (mob.getGfxId().getTempCharGfx() != polyId) {
                mob.getGfxId().setTempCharGfx(polyId);
                Broadcaster.broadcastPacket(mob, new S_ChangeShape(mob.getId(), polyId));
            }
        }
    }

    public static void undoPoly(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            pc.getGfxId().setTempCharGfx(pc.getClassId());
            pc.sendPackets(new S_ChangeShape(pc.getId(), pc.getGfxId().getTempCharGfx()));
            Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), pc.getGfxId().getTempCharGfx()));

            pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.SHAPE_CHANGE);

            L1ItemInstance weapon = pc.getWeapon();

            if (weapon != null) {
                pc.sendPackets(new S_CharVisualUpdate(pc));
                Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
            }

        } else if (cha instanceof L1MonsterInstance) {
            L1MonsterInstance mob = (L1MonsterInstance) cha;
            mob.getGfxId().setTempCharGfx(mob.getTemplate().getGfxid());
            Broadcaster.broadcastPacket(mob, new S_ChangeShape(mob.getId(), mob.getTemplate().getGfxid()));
        }
    }

    public static boolean isEquipAbleWeapon(int polyId, int weaponType) {
        L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);

        if (poly == null) {
            return true;
        }

        Integer flg = weaponFlgMap.get(weaponType);

        if (flg != null) {
            return 0 != (poly.getWeaponEquipFlg() & flg);
        }

        return true;
    }

    public static boolean isEquipableArmor(int polyId, int armorType) {
        L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
        if (poly == null) {
            return true;
        }

        Integer flg = armorFlgMap.get(armorType);
        if (flg != null) {
            return 0 != (poly.getArmorEquipFlg() & flg);
        }
        return true;
    }

    // 지정한 polyId가 무엇에 의해 변신해, 그것이 변신 당할까?
    public static boolean isMatchCause(int polyId, int cause) {
        L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);

        if (poly == null) {
            return true;
        }

        if (cause == MORPH_BY_LOGIN) {
            return true;
        }

        return 0 != (poly.getCauseFlg() & cause);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPolyId() {
        return polyId;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getWeaponEquipFlg() {
        return weaponEquipFlg;
    }

    public int getArmorEquipFlg() {
        return armorEquipFlg;
    }

    public boolean canUseSkill() {
        return canUseSkill;
    }

    public int getCauseFlg() {
        return causeFlg;
    }
}
