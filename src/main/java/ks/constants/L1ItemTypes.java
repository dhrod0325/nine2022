package ks.constants;

import java.util.HashMap;
import java.util.Map;

public class L1ItemTypes {
    public static final Map<String, Integer> armorTypes = new HashMap<>();
    public static final Map<String, Integer> weaponTypes = new HashMap<>();
    public static final Map<String, Integer> weaponId = new HashMap<>();
    public static final Map<String, Integer> materialTypes = new HashMap<>();
    public static final Map<String, Integer> etcItemTypes = new HashMap<>();
    public static final Map<String, Integer> useTypes = new HashMap<>();

    static {
        L1ItemTypes.etcItemTypes.put("arrow", 0);
        L1ItemTypes.etcItemTypes.put("wand", 1);
        L1ItemTypes.etcItemTypes.put("light", 2);
        L1ItemTypes.etcItemTypes.put("gem", 3);
        L1ItemTypes.etcItemTypes.put("totem", 4);
        L1ItemTypes.etcItemTypes.put("firecracker", 5);
        L1ItemTypes.etcItemTypes.put("potion", 6);
        L1ItemTypes.etcItemTypes.put("food", 7);
        L1ItemTypes.etcItemTypes.put("scroll", 8);
        L1ItemTypes.etcItemTypes.put("questitem", 9);
        L1ItemTypes.etcItemTypes.put("spellbook", 10);
        L1ItemTypes.etcItemTypes.put("petitem", 11);
        L1ItemTypes.etcItemTypes.put("other", 12);
        L1ItemTypes.etcItemTypes.put("material", 13);
        L1ItemTypes.etcItemTypes.put("event", 14);
        L1ItemTypes.etcItemTypes.put("sting", 15);
        L1ItemTypes.etcItemTypes.put("treasure_box", 16);

        L1ItemTypes.useTypes.put("none", -1); // 사용 불가능
        L1ItemTypes.useTypes.put("normal", 0);
        L1ItemTypes.useTypes.put("weapon", 1);
        L1ItemTypes.useTypes.put("armor", 2);
        L1ItemTypes.useTypes.put("spell_long", 5); // 지면 / 오브젝트 선택(원거리)
        L1ItemTypes.useTypes.put("ntele", 6);
        L1ItemTypes.useTypes.put("identify", 7);
        L1ItemTypes.useTypes.put("res", 8);
        L1ItemTypes.useTypes.put("letter", 12);
        L1ItemTypes.useTypes.put("letter_w", 13);
        L1ItemTypes.useTypes.put("choice", 14);
        L1ItemTypes.useTypes.put("instrument", 15);
        L1ItemTypes.useTypes.put("sosc", 16);
        L1ItemTypes.useTypes.put("spell_short", 17); // 지면 / 오브젝트 선택(근거리)
        L1ItemTypes.useTypes.put("T", 18);
        L1ItemTypes.useTypes.put("cloak", 19);
        L1ItemTypes.useTypes.put("glove", 20);
        L1ItemTypes.useTypes.put("boots", 21);
        L1ItemTypes.useTypes.put("helm", 22);
        L1ItemTypes.useTypes.put("ring", 23);
        L1ItemTypes.useTypes.put("amulet", 24);
        L1ItemTypes.useTypes.put("shield", 25);
        L1ItemTypes.useTypes.put("garder", 25);
        L1ItemTypes.useTypes.put("dai", 26);
        L1ItemTypes.useTypes.put("zel", 27);
        L1ItemTypes.useTypes.put("blank", 28);
        L1ItemTypes.useTypes.put("btele", 29);
        L1ItemTypes.useTypes.put("spell_buff", 30); // 오브젝트 선택(원거리)
        L1ItemTypes.useTypes.put("ccard", 31);
        L1ItemTypes.useTypes.put("ccard_w", 32);
        L1ItemTypes.useTypes.put("vcard", 33);
        L1ItemTypes.useTypes.put("vcard_w", 34);
        L1ItemTypes.useTypes.put("wcard", 35);
        L1ItemTypes.useTypes.put("wcard_w", 36);
        L1ItemTypes.useTypes.put("belt", 37);
        L1ItemTypes.useTypes.put("earring", 40);
        L1ItemTypes.useTypes.put("fishing_rod", 42);
        L1ItemTypes.useTypes.put("rune", 44);//룬왼쪽칸
        L1ItemTypes.useTypes.put("acczel", 46);
        L1ItemTypes.useTypes.put("rune2", 74);//룬왼쪽칸
        L1ItemTypes.useTypes.put("spawn", 100);

        L1ItemTypes.armorTypes.put("none", 0);
        L1ItemTypes.armorTypes.put("helm", 1);
        L1ItemTypes.armorTypes.put("armor", 2);
        L1ItemTypes.armorTypes.put("T", 3);
        L1ItemTypes.armorTypes.put("cloak", 4);
        L1ItemTypes.armorTypes.put("glove", 5);
        L1ItemTypes.armorTypes.put("boots", 6);
        L1ItemTypes.armorTypes.put("shield", 7);
        L1ItemTypes.armorTypes.put("amulet", 8);
        L1ItemTypes.armorTypes.put("ring", 9);
        L1ItemTypes.armorTypes.put("belt", 10);
        L1ItemTypes.armorTypes.put("ring2", 11);
        L1ItemTypes.armorTypes.put("earring", 12);
        L1ItemTypes.armorTypes.put("garder", 13);
        L1ItemTypes.armorTypes.put("rune", 14);
        L1ItemTypes.armorTypes.put("rune2", 28);

        L1ItemTypes.weaponTypes.put("sword", 1);
        L1ItemTypes.weaponTypes.put("dagger", 2);
        L1ItemTypes.weaponTypes.put("tohandsword", 3);
        L1ItemTypes.weaponTypes.put("bow", 4);
        L1ItemTypes.weaponTypes.put("spear", 5);
        L1ItemTypes.weaponTypes.put("blunt", 6);
        L1ItemTypes.weaponTypes.put("staff", 7);
        L1ItemTypes.weaponTypes.put("throwingknife", 8);
        L1ItemTypes.weaponTypes.put("arrow", 9);
        L1ItemTypes.weaponTypes.put("gauntlet", 10);
        L1ItemTypes.weaponTypes.put("claw", 11);
        L1ItemTypes.weaponTypes.put("edoryu", 12);
        L1ItemTypes.weaponTypes.put("singlebow", 13);
        L1ItemTypes.weaponTypes.put("singlespear", 14);
        L1ItemTypes.weaponTypes.put("tohandblunt", 15);
        L1ItemTypes.weaponTypes.put("tohandstaff", 16);
        L1ItemTypes.weaponTypes.put("keyringku", 17);
        L1ItemTypes.weaponTypes.put("chainsword", 18);
        L1ItemTypes.weaponTypes.put("twohandkeyringku", 19);

        L1ItemTypes.weaponId.put("sword", 4);
        L1ItemTypes.weaponId.put("dagger", 46);
        L1ItemTypes.weaponId.put("tohandsword", 50);
        L1ItemTypes.weaponId.put("bow", 20);
        L1ItemTypes.weaponId.put("blunt", 11);
        L1ItemTypes.weaponId.put("spear", 24);
        L1ItemTypes.weaponId.put("staff", 40);
        L1ItemTypes.weaponId.put("throwingknife", 2922);
        L1ItemTypes.weaponId.put("arrow", 66);
        L1ItemTypes.weaponId.put("gauntlet", 62);
        L1ItemTypes.weaponId.put("claw", 58);
        L1ItemTypes.weaponId.put("keyringku", 58);
        L1ItemTypes.weaponId.put("edoryu", 54);
        L1ItemTypes.weaponId.put("singlebow", 20);
        L1ItemTypes.weaponId.put("singlespear", 24);
        L1ItemTypes.weaponId.put("chainsword", 24);
        L1ItemTypes.weaponId.put("tohandblunt", 11);
        L1ItemTypes.weaponId.put("tohandstaff", 40);
        L1ItemTypes.weaponId.put("twohandkeyringku", 58);

        L1ItemTypes.materialTypes.put("none", 0);
        L1ItemTypes.materialTypes.put("liquid", 1);
        L1ItemTypes.materialTypes.put("web", 2);
        L1ItemTypes.materialTypes.put("vegetation", 3);
        L1ItemTypes.materialTypes.put("animalmatter", 4);
        L1ItemTypes.materialTypes.put("paper", 5);
        L1ItemTypes.materialTypes.put("cloth", 6);
        L1ItemTypes.materialTypes.put("leather", 7);
        L1ItemTypes.materialTypes.put("wood", 8);
        L1ItemTypes.materialTypes.put("bone", 9);
        L1ItemTypes.materialTypes.put("dragonscale", 10);
        L1ItemTypes.materialTypes.put("iron", 11);
        L1ItemTypes.materialTypes.put("steel", 12);
        L1ItemTypes.materialTypes.put("copper", 13);
        L1ItemTypes.materialTypes.put("silver", 14);
        L1ItemTypes.materialTypes.put("gold", 15);
        L1ItemTypes.materialTypes.put("platinum", 16);
        L1ItemTypes.materialTypes.put("mithril", 17);
        L1ItemTypes.materialTypes.put("blackmithril", 18);
        L1ItemTypes.materialTypes.put("glass", 19);
        L1ItemTypes.materialTypes.put("gemstone", 20);
        L1ItemTypes.materialTypes.put("mineral", 21);
        L1ItemTypes.materialTypes.put("oriharukon", 22);
    }

    public static String findWeaponTypeStringByType(int type) {
        return findTypeStringByType(weaponTypes, type);
    }

    public static String findArmorTypeStringByType(int type) {
        return findTypeStringByType(armorTypes, type);
    }

    private static String findTypeStringByType(Map<String, Integer> types, int type) {
        for (String a : types.keySet()) {
            int s = types.get(a);

            if (s == type) {
                return a;
            }
        }

        return null;
    }
}
