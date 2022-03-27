package ks.core.datatables.item;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.constants.L1EtcTypes;
import ks.constants.L1ItemId;
import ks.core.ObjectIdFactory;
import ks.core.datatables.item.mapper.ArmorMapper;
import ks.core.datatables.item.mapper.EtcItemMapper;
import ks.core.datatables.item.mapper.WeaponMapper;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.MagicDollItemInstance;
import ks.model.item.*;
import ks.model.item.function.*;
import ks.model.item.function.enchant.*;
import ks.model.item.function.item.Armor;
import ks.model.item.function.item.Arrow;
import ks.model.item.function.item.Sting;
import ks.model.item.function.item.Weapon;
import ks.model.item.function.orim.L1OrimScrollEnchantItem;
import ks.model.item.function.potion.*;
import ks.model.item.function.spellbook.SpellBook;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemTable {
    private static final Logger logger = LogManager.getLogger(ItemTable.class);

    public final Map<Integer, L1EtcItem> etcItems = new LinkedHashMap<>();
    public final Map<Integer, L1Armor> armors = new LinkedHashMap<>();
    public final Map<Integer, L1Weapon> weapons = new LinkedHashMap<>();

    private final Map<Integer, L1Item> allTemplates = new LinkedHashMap<>();

    public static ItemTable getInstance() {
        return LineageAppContext.getBean(ItemTable.class);
    }

    @LogTime
    public void load() {
        loadAllEtcItem();
        loadAllWeapon();
        loadAllArmor();

        buildFastLookupTable();
    }

    public void loadAllEtcItem() {
        etcItems.clear();

        List<L1EtcItem> list = selectEtcList();

        for (L1EtcItem item : list) {
            etcItems.put(item.getItemId(), item);
        }
    }

    public List<L1EtcItem> selectEtcList() {
        return SqlUtils.query("select * from etcitem", new EtcItemMapper());
    }

    public void loadAllWeapon() {
        weapons.clear();

        List<L1Weapon> list = selectWeaponList();

        for (L1Weapon item : list) {
            weapons.put(item.getItemId(), item);
        }
    }

    public List<L1Weapon> selectWeaponList() {
        return SqlUtils.query("select * from weapon", new WeaponMapper());
    }

    private void loadAllArmor() {
        armors.clear();

        List<L1Armor> list = selectArmorList();

        for (L1Armor item : list) {
            armors.put(item.getItemId(), item);
        }
    }

    public List<L1Armor> selectArmorList() {
        return SqlUtils.query("select * from armor", new ArmorMapper());
    }

    private void putTemplate(L1Item item) {
        int key = item.getItemId();

        if (allTemplates.containsKey(key)) {
            L1Item oldItem = allTemplates.get(key);

            logger.error("아이디가 겹치는 아이템이 있습니다 : " + oldItem.getName() + " - " + item.getName());

            return;
        }

        allTemplates.put(key, item);
    }

    private void buildFastLookupTable() {
        allTemplates.clear();

        Collection<L1EtcItem> etcItems = this.etcItems.values();
        for (L1EtcItem item : etcItems) {
            putTemplate(item);
        }

        Collection<L1Weapon> weapons = this.weapons.values();

        for (L1Weapon item : weapons) {
            putTemplate(item);
        }

        Collection<L1Armor> armors = this.armors.values();

        for (L1Armor item : armors) {
            putTemplate(item);
        }
    }

    public L1Item getTemplate(int id) {
        return allTemplates.get(id);
    }

    public L1ItemInstance functionItem(L1Item temp) {
        L1ItemInstance item = null;

        if (temp.getType2() == 1) {
            item = new Weapon(temp);
            item.setWorking(true);
        } else if (temp.getType2() == 2) {
            item = new Armor(temp);
            item.setWorking(true);
        } else if (temp.getType2() == 0) {
            switch (temp.getType()) {
                case L1EtcTypes.etc_arrow:
                    item = new Arrow(temp);
                    item.setWorking(true);
                    break;
                case L1EtcTypes.etc_wand:
                    switch (temp.getItemId()) {
                        case 40006:
                        case 140006:
                        case 5000121:
                            item = new MobSpawnWand(temp);
                            item.setWorking(true);
                            break;
                        case 5000683: // 9월20일gmwand.java 캐릭정보 검사막대
                        case 5000684: // 캐릭장비 검사막대
                        case 5000685: // 캐릭계정 검사막대
                        case 5000686: // 채팅캐릭 지정막대
                            item = new GMWand(temp);
                            item.setWorking(true);
                            break;
                        case 46160:
                        case 6000067:
                            item = new FieldObject(temp);
                            item.setWorking(true);
                            break;
                        case 40007:
                        case 140007:
                            item = new ThunderWand(temp);
                            item.setWorking(true);
                            break;
                        case 46091:
                            item = new FireWand(temp);
                            item.setWorking(true);
                            break;
                        case 40008:
                        case 140008:
                        case 45464://픽시의 변신막대
                            item = new PolyWand(temp);
                            item.setWorking(true);
                            break;
                        case 41401:
                            item = new FurnitureItem(temp);
                            item.setWorking(true);
                            break;
                    }
                    break;
                case L1EtcTypes.etc_light:
                    item = new Light(temp);
                    item.setWorking(true);
                    break;
                case L1EtcTypes.etc_gem:
                    if (temp.getItemId() >= 40931 && temp.getItemId() <= 40958) {
                        item = new Choiceitem(temp);
                        item.setWorking(true);
                    }
                    break;
                case L1EtcTypes.etc_firecracker:
                    item = new Firecracker(temp);
                    item.setWorking(true);
                    break;
                case L1EtcTypes.etc_potion:
                    switch (temp.getItemId()) {
                        case 40010:
                        case 40011:
                        case 40012:
                        case 40019:
                        case 40020:
                        case 40021:
                        case 40022:
                        case 40023:
                        case 40024:
                        case 60001302:
                        case 404081:
                        case 40026:
                        case 40027:
                        case 40028:
                        case 40029:
                        case 40043:
                        case 50006:
                        case 40058:
                        case 40071:
                        case 40734:
                        case 40930:
                        case 41141:
                        case 41298:
                        case 41299:
                        case 41300:
                        case 41337:
                        case 41403:
                        case 70171:
                        case 140010:
                        case 140011:
                        case 140012:
                        case 240010:
                        case 410003:
                        case 435000:
                            item = new HealingPotion(temp);
                            item.setWorking(true);
                            break;
                        case 40015:
                        case 40736:
                        case 41142:
                        case 140015:
                        case 50017:
                        case 404082:
                            item = new BluePotion(temp);
                            item.setWorking(true);
                            break;
                        case 40033:
                        case 40034:
                        case 40035:
                        case 40036:
                        case 40037:
                        case 40038:
                        case 55700://픽시의엘릭서
                            item = new Elixir(temp);
                            item.setWorking(true);
                            break;
                        case 40013:
                        case 40018:
                        case 50018:// 복지속도물약
                        case 40030:
                        case 40039:
                        case 40040:
                        case 41261:
                        case 41262:
                        case 41268:
                        case 41269:
                        case 41271:
                        case 41272:
                        case 41273:
                        case 41338:
                        case 140013:
                        case 140018:
                            item = new GreenPotion(temp);
                            item.setWorking(true);
                            break;
                        case 40014:
                        case 40031:
                        case 40068:
                        case 41415:
                        case 60001428:
                        case 50014:// 복지용기물약
                        case 140014:
                        case 140068:
                        case 50015:// 복지집중물약
                        case 430006:
                            item = new BravePotion(temp);
                            item.setWorking(true);
                            break;
                        case 40032:
                        case 50019:// 복지호흡물약
                        case 40041:
                        case 41344:
                            item = new BlessOfEvaPotion(temp);
                            item.setWorking(true);
                            break;
                        case 40016:
                        case 140016:
                        case 50016:// 복지지혜물약
                            item = new WisdomPotion(temp);
                            item.setWorking(true);
                            break;
                        case 40025:
                            item = new BlindPotion(temp);
                            item.setWorking(true);
                            break;
                    }
                    break;
                case L1EtcTypes.etc_food:
                    item = new Food(temp);
                    item.setWorking(true);
                    break;
                case L1EtcTypes.etc_scroll:
                    switch (temp.getItemId()) {
                        case 60001212:
                            item = new ItemDragonArmorChange(temp);
                            item.setWorking(true);

                            break;
                        case 6000112:
                        case 6000113:
                        case 6000114:
                            item = new ItemChangeScroll(temp);
                            item.setWorking(true);
                            break;
                        case 6000110:
                            item = new RevivalScroll(temp);
                            item.setWorking(true);
                            break;
                        case 40098:
                        case 40126:
                            item = new DefiniteScroll(temp);
                            item.setWorking(true);
                            break;
                        case 7760://파이어스톰 막대
                        case 7761:
                        case 40090:
                        case 40091:
                        case 40092:
                        case 40093:
                        case 40094:
                            item = new BlankScroll(temp);
                            item.setWorking(true);
                            break;
                        case 40079:
                        case 40081:
                        case 40117:
                        case 40086:
                        case 40095:
                        case 40099:
                        case 40100:
                        case 40124:
                        case 40521:
                        case 40863:
                        case 41159:
                        case 140100:
                        case 240100:
                        case 6000070:
                            item = new TeleportScroll(temp);
                            item.setWorking(true);
                            break;
                        case 714:
                        case 40074:
                        case 40078:
                        case 40127:
                        case 40129:
                        case 60001320:
                        case 60001321:
                        case 60001322:
                        case 140074:
                        case 140129:
                        case 240074:
                        case 437006:
                        case 540341:
                        case 437027:
                        case 41563: //오림 축복
                        case 41564: //오림 노멀
                        case 5000149: //스냅퍼강화주문서
                        case 60001236:
                        case L1ItemId.TEST_ENCHANT_ARMOR:
                        case L1ItemId.HALLOWEEN_2011_ACCESSORY_ENCHANT_SCROLL://할로윈이벤트
                            item = new EnchantArmor(temp);
                            item.setWorking(true);
                            break;
                        case 430014:
                            item = new EnchantAcc(temp);
                            item.setWorking(true);
                            break;
                        case L1ItemId.FIRE_ENCHANT_WEAPON_SCROLL:
                        case L1ItemId.WATER_ENCHANT_WEAPON_SCROLL:
                        case L1ItemId.WIND_ENCHANT_WEAPON_SCROLL:
                        case L1ItemId.EARTH_ENCHANT_WEAPON_SCROLL:
                            item = new EnchantAttr(temp);
                            item.setWorking(true);
                            break;
                        case 60001226:
                        case 60001227:
                        case 60001340:
                        case 60001341:
                        case 60001342:
                        case 60001343:
                        case 60001371:
                            item = new L1OrimScrollEnchantItem(temp);
                            item.setWorking(true);
                            break;
                        case 60001149:
                        case 40077:
                        case 40087:
                        case 5101:
                        case 40128:
                        case 40130:
                        case 140087:
                        case 140130:
                        case 240087:
                        case 437007:
                        case 540342:
                        case 600006:
                        case 600007:
                        case 600008:
                        case 600009:
                        case L1ItemId.TEST_ENCHANT_WEAPON:
                            item = new EnchantWeapon(temp);
                            item.setWorking(true);
                            break;
                        case 40088:
                        case 40096:
                        case 140088:
                        case 6000101:
                        case 50022:
                        case 60001213:
                            item = new PolyScroll(temp);
                            item.setWorking(true);
                            break;
                        case 40089:
                        case 140089:
                            item = new ResurrectionScroll(temp);
                            item.setWorking(true);
                            break;
                        case 50020:
                        case 50021:
                        case 401262:
                        case 401261:
                        case 60001256:
                            item = new SealScroll(temp);
                            item.setWorking(true);
                            break;
                        case 430015:
                        case 430016:
                        case 430017:
                        case 430018:
                        case 430019:
                        case 430020:
                        case 430021:
                        case 435016:
                        case 435017:
                        case 51200:
                        case 51201:
                        case 430055://데스 변신막대
                            item = new PolyItem(temp);
                            item.setWorking(true);
                            break;
                        case 60001239:
                        case 60001240:
                            item = new OptionEnchant(temp);
                            item.setWorking(true);

                            break;
                    }
                    break;
                case L1EtcTypes.etc_questitem:
                    switch (temp.getItemId()) {
                        case 41342:
                            item = new GreenPotion(temp);
                            item.setWorking(true);
                            break;
                        case 40660:
                            item = new EnchantWeapon(temp);
                            item.setWorking(true);
                            break;
                        case 41048:
                        case 41049:
                        case 41050:
                        case 41051:
                        case 41052:
                        case 41053:
                        case 41054:
                        case 41055:
                        case 41056:
                        case 41057:
                        case 490503:
                            item = new Choiceitem(temp);
                            item.setWorking(true);
                            break;
                    }
                    break;
                case L1EtcTypes.etc_spellbook:
                    item = new SpellBook(temp);
                    item.setWorking(true);
                    break;
                case L1EtcTypes.etc_other:
                    switch (temp.getItemId()) {
                        case 30282:
                        case 560025:
                        case 560026:
                        case 560027:
                        case 560028:
                        case 6000041:
                            item = new TelBookItem(temp);
                            item.setWorking(true);
                            break;
                        case L1ItemId.용해제:
                            item = new Resolvent(temp);
                            item.setWorking(true);
                            break;
                        case 41255:
                        case 41256:
                        case 41257:
                        case 41258:
                        case 41259:
                            item = new MakeCooking(temp);
                            item.setWorking(true);
                            break;
                        case 430506:
                        case 430520:
                            item = new Fishing(temp);
                            item.setWorking(true);
                            break;
                        case L1ItemId.DOLL_타락:
                        case L1ItemId.DOLL_서큐:
                        case L1ItemId.DOLL_늑대인간:
                        case L1ItemId.DOLL_얼음여왕:
                        case L1ItemId.DOLL_커츠:
                        case L1ItemId.DOLL_시댄서:
                        case L1ItemId.DOLL_에티:
                        case L1ItemId.DOLL_스파:
                        case L1ItemId.DOLL_코카:
                        case L1ItemId.DOLL_라미아:
                        case L1ItemId.DOLL_허수아비:
                        case L1ItemId.DOLL_에틴:
                        case L1ItemId.DOLL_데스:
                        case L1ItemId.DOLL_자이언트:
                        case L1ItemId.DOLL_파푸:
                        case L1ItemId.DOLL_린드:
                        case L1ItemId.DOLL_발라:
                        case L1ItemId.DOLL_안타:
                        case L1ItemId.DOLL_데몬:
                        case L1ItemId.DOLL_초보:
                        case L1ItemId.DOLL_돌골렘:
                        case L1ItemId.DOLL_버그베어:
                        case L1ItemId.DOLL_목각:
                        case L1ItemId.DOLL_크러스트시안:
                        case L1ItemId.DOLL_장로:
                        case L1ItemId.DOLL_눈사람:
                        case L1ItemId.DOLL_인어:
                        case L1ItemId.DOLL_라바골렘:
                        case L1ItemId.DOLL_다이아몬드골렘:
                        case L1ItemId.DOLL_킹버그베어:
                        case L1ItemId.DOLL_드레이크:
                        case L1ItemId.DOLL_서큐버스퀸:
                        case L1ItemId.DOLL_흑장로:
                        case L1ItemId.DOLL_축서큐버스퀸:
                        case L1ItemId.DOLL_축흑장로:
                        case L1ItemId.DOLL_축자이언트:
                        case L1ItemId.DOLL_축드레이크:
                        case L1ItemId.DOLL_축킹버그베어:
                        case L1ItemId.DOLL_축다이아몬드골렘:

                        case L1ItemId.DOLL_리치:
                        case L1ItemId.DOLL_사이클롭스:
                        case L1ItemId.DOLL_나이트발드:
                        case L1ItemId.DOLL_시어:
                        case L1ItemId.DOLL_아이리스:
                        case L1ItemId.DOLL_뱀파이어:
                        case L1ItemId.DOLL_머미로드:
                        case L1ItemId.DOLL_축리치:
                        case L1ItemId.DOLL_축사이클롭스:
                        case L1ItemId.DOLL_축나이트발드:
                        case L1ItemId.DOLL_축시어:
                        case L1ItemId.DOLL_축아이리스:
                        case L1ItemId.DOLL_축뱀파이어:
                        case L1ItemId.DOLL_축머미로드:

                        case L1ItemId.DOLL_바란카:
                        case L1ItemId.DOLL_바포메트:
                        case L1ItemId.DOLL_축바란카:
                        case L1ItemId.DOLL_축타락:
                        case L1ItemId.DOLL_축바포메트:
                        case L1ItemId.DOLL_축얼음여왕:
                        case L1ItemId.DOLL_축커츠:

                        case L1ItemId.DOLL_축데스:
                        case L1ItemId.DOLL_축데몬:
                        case L1ItemId.DOLL_축안타:
                        case L1ItemId.DOLL_축파푸:
                        case L1ItemId.DOLL_축발라:
                        case L1ItemId.DOLL_축린드:

                            item = new MagicDollItemInstance(temp);
                            item.setWorking(true);
                            break;
                        case 41383:
                        case 41384:
                        case 41385:
                        case 41386:
                        case 41387:
                        case 41388:
                        case 41389:
                        case 41390:
                        case 41391:
                        case 41392:
                        case 41393:
                        case 41394:
                        case 41395:
                        case 41396:
                        case 41397:
                        case 41398:
                        case 41399:
                        case 41400:
                            item = new FurnitureItem(temp);
                            item.setWorking(true);
                            break;
                    }
                    break;
                case L1EtcTypes.etc_material:
                    switch (temp.getItemId()) {
                        case 40506:
                        case 140506:
                            item = new HealingPotion(temp);
                            item.setWorking(true);
                            break;
                        case 40410:
                            item = new PolyWand(temp);
                            item.setWorking(true);
                            break;
                        case 40412:
                            item = new MobSpawnWand(temp);
                            item.setWorking(true);
                            break;
                        case 41143:
                        case 41144:
                        case 41145:
                        case 41154:
                        case 41155:
                        case 41156:
                        case 41157:
                            item = new PolyItem(temp);
                            item.setWorking(true);
                            break;
                    }
                    break;
                case L1EtcTypes.etc_sting:
                    item = new Sting(temp);
                    item.setWorking(true);
                    break;
                case L1EtcTypes.etc_treasurebox:
                    item = new TreasureBox(temp);
                    item.setWorking(true);
                    break;
                default:
            }

            switch (temp.getItemId()) {
                case 437010:
                case 437012:
                case 437059:
                    item = new DragonGem(temp);
                    item.setWorking(true);
                    break;
                case 40317:
                    item = new RepairItem(temp);
                    item.setWorking(true);
                    break;
                case 40507:
                case 40017:
                case 6000085:
                    item = new CurePotion(temp);
                    item.setWorking(true);
                    break;
            }
        }

        switch (temp.getItemId()) {
            case 6000039:
            case 6000050:
            case 6000056:
                item = new ItemTeleportCharm(temp);
                item.setWorking(true);
                break;
            case 6000042:
            case 6000100:
                item = new ItemMonsterDropCheck();
                item.setWorking(true);
                break;
            case 6000043:
            case 6000044:
            case 6000045:
            case 60001251:
                item = new ItemSealScroll();
                item.setWorking(true);
                break;
            case 6000052:
            case 5000145:
                item = new ItemEnchantRingAndEarRing(temp);
                item.setWorking(true);
                break;
            case 6000055:
                item = new ItemDollScroll();
                item.setWorking(true);
                break;
            case 6000062:
                item = new ItemHpMpResetScroll();
                item.setWorking(true);
                break;
            case 6000065:
            case 6000066:
                item = new ItemPotion();
                item.setWorking(true);
                break;
            case 6000069:
                item = new ItemCharacterTrade();
                item.setWorking(true);
                break;
        }

        if (item != null)
            return item;
        else
            return new L1ItemInstance(temp);
    }

    public L1ItemInstance createItem(int itemId) {
        L1Item temp = getTemplate(itemId);

        if (temp == null) {
            return null;
        }

        L1ItemInstance item = functionItem(temp);

        item.setId(ObjectIdFactory.getInstance().nextId());
        item.setItem(temp);
        item.setBless(temp.getBless());

        L1World.getInstance().storeObject(item);

        return item;
    }

    public L1Item findItem(int itemId) {
        return allTemplates.get(itemId);
    }

    public int findItemIdByNameWithoutSpace(String name) {
        int itemid = 0;

        for (L1Item item : allTemplates.values()) {
            if (item != null && item.getName().replace(" ", "").equals(name)) {
                itemid = item.getItemId();
                break;
            }
        }

        return itemid;
    }

    public Collection<L1Item> getAllItems() {
        return allTemplates.values();
    }
}
