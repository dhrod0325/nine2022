package ks.util;

import ks.app.config.prop.CodeConfig;
import ks.constants.*;
import ks.core.auth.AuthorizationUtils;
import ks.core.datatables.LetterTable;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.hunt.HuntPrice;
import ks.core.datatables.hunt.HuntPriceTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.datatables.pet.PetTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.instance.*;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.model.skill.utils.L1SkillUtils;
import ks.model.types.Point;
import ks.model.warehouse.Warehouse;
import ks.packets.serverpackets.*;
import ks.system.boss.L1BossSpawnManager;
import ks.system.event.TimePickupEvent;
import ks.system.infinityWar.InfinityWarService;
import ks.util.common.NumberUtils;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ks.constants.L1SkillId.*;
import static ks.util.L1ClassUtils.*;

public class L1CommonUtils {

    private static final int STATUS_INVISIBLE = 2;
    private static final int STATUS_PC = 4;
    private static final int STATUS_FREEZE = 8;
    private static final int STATUS_BRAVE = 16;
    private static final int STATUS_ELF_BRAVE = 32;
    private static final int STATUS_FAST_MOVE_ABLE = 64;

    public static Logger logger = LogManager.getLogger();

    public static int changeGfx(int gfxId) {
        int gfx = gfxId;

        if (L1Opcodes.SERVER_VERSION == 3.1) {
            if (gfxId == L1ClassUtils.CLASSID_PRINCE) {
                gfx = CodeConfig.SPR_PRINCE;
            } else if (gfxId == L1ClassUtils.CLASSID_PRINCESS) {
                gfx = CodeConfig.SPR_PRINCESS;
            } else if (gfxId == L1ClassUtils.CLASSID_KNIGHT_MALE) {
                gfx = CodeConfig.SPR_KNIGHT;
            } else if (gfxId == L1ClassUtils.CLASSID_KNIGHT_FEMALE) {
                gfx = CodeConfig.SPR_KNIGHT_FEMALE;
            }
        }

        return gfx;
    }

    public static void statusBuff(L1PcInstance pc, L1NpcInstance npc) {
        int gn4 = RandomUtils.nextInt(4) + 1;

        if (gn4 == 1) {
            L1SkillUtils.skillByGm(pc, L1SkillId.STATUS_LUCK_A);
            pc.sendPackets(new S_NpcChatPacket(npc, "자네는 운이 정말 좋군.... 최고의 날이야 오늘같은 날은 로또라도 뽑아보라구~", 0));
        } else if (gn4 == 2) {
            L1SkillUtils.skillByGm(pc, L1SkillId.STATUS_LUCK_B);
            pc.sendPackets(new S_NpcChatPacket(npc, "이정도 운세면 좋은편이야.... 그래도 조심 또 조심하라구~", 0));
        } else if (gn4 == 3) {
            L1SkillUtils.skillByGm(pc, L1SkillId.STATUS_LUCK_C);
            pc.sendPackets(new S_NpcChatPacket(npc, "평범한 운세구만.... 서버 홍보 많이 해줄거지?? 안해준다면 버프를 주지 않을꺼다.", 0));
        } else if (gn4 == 4) {
            L1SkillUtils.skillByGm(pc, L1SkillId.STATUS_LUCK_D);
            pc.sendPackets(new S_NpcChatPacket(npc, "솔직히 오늘같은날은 집에서 안나오는게 좋을거 같아....", 0));
        }
    }

    public static L1Character maxHateCharacter(Map<L1Character, Integer> hateMap) {
        L1Character maxHateCha = null;
        int hate = Integer.MIN_VALUE;

        for (L1Character e : hateMap.keySet()) {
            int value = hateMap.get(e);

            if (hate < value) {
                maxHateCha = e;
                hate = value;
            }
        }

        return maxHateCha;
    }

    public static String numberFormat(int number) {
        try {
            NumberFormat nf = NumberFormat.getInstance();

            return nf.format(number);
        } catch (Exception e) {
            return Integer.toString(number);
        }
    }

    public static String dateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        return sdf.format(date.getTime());
    }

    public static boolean isNear(int x, int y, int targetX, int targetY, int cellSize) {
        int diffLocX = Math.abs(x - targetX);
        int diffLocY = Math.abs(y - targetY);

        return diffLocX > cellSize || diffLocY > cellSize;
    }

    public static void deleteGroundItems() {
        Collection<L1ItemInstance> items = L1World.getInstance().getAllItem();

        for (L1ItemInstance item : new ArrayList<>(items)) {
            if (item == null)
                continue;

            if (item.getX() == 0 && item.getY() == 0) {
                continue;
            }

            if (item.getItem().getItemId() == 40515) {
                continue;
            }

            if (item.getMapId() == 88
                    || item.getMapId() == 98
                    || item.getMapId() == 91
                    || item.getMapId() == 92
                    || item.getMapId() == 95
                    || item.getMapId() == TimePickupEvent.MAP_ID) {
                continue;
            }

            if (InfinityWarService.getInstance().infinityWarMapIds().contains((int) item.getMapId())) {
                continue;
            }

            if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item.getMapId())) {
                continue;
            }

            if (L1CastleLocation.isInCastleInner(item.getMapId())) {
                continue;
            }

            if (item.getX() == 0 && item.getY() == 0) {
                continue;
            }

            if (item.getOwnerTime() > 0) {
                continue;
            }

            L1Inventory groundInventory = L1World.getInstance().getInventory(item.getX(), item.getY(), item.getMapId());

            int itemId = item.getItem().getItemId();

            if (itemId == 40314 || itemId == 40316) {
                PetTable.getInstance().deletePet(item.getId());
            } else if (itemId >= 49016 && itemId <= 49025) {
                LetterTable lettertable = new LetterTable();
                lettertable.deleteLetter(item.getId());
            }

            groundInventory.deleteItem(item);

            L1World.getInstance().removeVisibleObject(item);
            L1World.getInstance().removeObject(item);

            L1LogUtils.debugLog("[월드아이템삭제] : {} ", L1LogUtils.logItemName(item));
        }

        String msg = "월드맵상의 아이템이 삭제되었습니다.";

        L1World.getInstance().broadcastServerMessage(msg);
        logger.info(msg);
    }

    public static boolean isTwoLogin(L1PcInstance c) {
        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        int count = 0;

        for (L1PcInstance target : players) {
            if (c.getId() == target.getId()) {
                count++;

                if (count > 1) {
                    c.disconnect();
                    target.disconnect();
                    return true;
                }
            } else if (c.getId() != target.getId()) {
                if (c.getAccountName().equalsIgnoreCase(target.getAccountName())) {
                    c.disconnect();
                    target.disconnect();
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isNotAvailablePcWeight(L1PcInstance pc, L1ItemInstance item, int count) {
        if (pc.getInventory().checkAddItem(item, count) != L1Inventory.OK) {
            pc.sendPackets(new S_ServerMessage(270));
            return true;
        }
        return false;
    }

    public static boolean isOverMaxAdena(L1PcInstance pc, int sellPrice, int count) {
        if (sellPrice * count > 2000000000 || sellPrice * count < 0) {
            pc.sendPackets(new S_ServerMessage(904, "2000000000"));
            return true;
        }

        if (count < 0) {
            return true;
        }

        return sellPrice < 0;
    }

    public static boolean isNotAvailableTrade(L1PcInstance pc, L1Character targetPc, int itemObjectId, L1ItemInstance item, int count) {
        boolean result = true;

        if (item == null) {
            result = false;
        } else {
            if ((itemObjectId != item.getId()) || (!item.isStackable() && count != 1))
                result = false;

            if (count <= 0 || item.getCount() <= 0 || item.getCount() < count)
                result = false;

            if (count > 2000000000 || item.getCount() > 2000000000)
                result = false;
        }

        if (!result) {
            pc.disconnect();
            if (targetPc instanceof L1PcInstance) {
                ((L1PcInstance) targetPc).disconnect();
            }
        }

        return !result;
    }

    public static boolean isNotAvailableTrade(L1PcInstance pc, int objectId, L1ItemInstance item, int count) {
        boolean result = true;

        if (item == null) {
            result = false;
        } else {
            if (objectId != item.getId()) {
                result = false;
            }

            if (!item.isStackable() && count != 1) {
                result = false;
            }

            if (item.getCount() <= 0 || item.getCount() > 2000000000) {
                result = false;
            }

            if (item.getItem().getItemId() == 40308) {
                if (count > CodeConfig.MAX_TRADE_PRICE) {
                    pc.sendPackets(new S_SystemMessage("아데나는 " + NumberFormat.getInstance().format(CodeConfig.MAX_TRADE_PRICE) + " 이상 한번에 이용 할 수 없습니다."));
                    result = false;
                }
            }
        }

        if (count <= 0 || count > 2000000000) {
            result = false;
        }

        if (!result) {
            pc.disconnect();
        }

        return !result;
    }

    public static void doHunt(L1PcInstance pc, L1PcInstance target) {
        try {
            if (target == null) {
                pc.sendPackets(new S_SystemMessage("상대방이 접속해 있지 않습니다"));
                return;
            }

            HuntPrice huntPrice = HuntPriceTable.getInstance().findByLevel(target.getLevel());

            if (huntPrice == null) {
                target.sendPackets("오류가 발생했습니다. 관리자에게 문의하세요");
                return;
            }

            int price = huntPrice.getPrice();

            if (target.getHuntCount() == 1) {
                pc.sendPackets(new S_SystemMessage("이미 수배 되어있습니다"));
                return;
            }

            if (!(pc.getInventory().checkItem(L1ItemId.ADENA, price))) {
                pc.sendPackets(new S_SystemMessage(NumberFormat.getInstance().format(price) + "만 아데나가 필요합니다"));
                return;
            }

            target.huntBuff();
            target.setHuntCount(1);
            target.setHuntPrice(price);
            target.setReasonToHunt("");

            pc.getInventory().consumeItem(L1ItemId.ADENA, price);

            String msg = "\\fW[수배] " + target.getName() + " : 추가대미지 +3, SP +3, 리덕션+1";
            L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(msg));

            L1SkillUtils.skillByLogin(target, L1SkillId.STATUS_HUNT);

            pc.save();
            target.save();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public static boolean isBlessScroll(int itemId) {
        return itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
                || itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
                || itemId == 140129
                || itemId == 60001321
                || itemId == 140130;
    }

    public static void polyAction(L1Character skillUseCharacter, L1Character target) {
        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            if (pc.getMapId() == 63 || pc.getMapId() == 552
                    || pc.getMapId() == 555 || pc.getMapId() == 557
                    || pc.getMapId() == 558 || pc.getMapId() == 779) {
                pc.sendPackets(new S_ServerMessage(563));
                pc.tell();
            } else {
                if (pc.getMap().isSafetyZone(pc.getLocation())) {
                    pc.sendPackets(new S_SystemMessage("마을안에서는 사용 할 수 없습니다."));
                    return;
                }

                if (target != null) {
                    boolean isSameClan = false;

                    if (target instanceof L1MonsterInstance) {
                        return;
                    }

                    if (target instanceof L1PcInstance) {
                        L1PcInstance targetPc = (L1PcInstance) target;

                        if (targetPc.getClanId() != 0 && pc.getClanId() == targetPc.getClanId()) {
                            isSameClan = true;
                        }
                    }

                    if (pc.getId() != target.getId() && !isSameClan) {
                        L1Skills skill = SkillsTable.getInstance().getTemplate(SHAPE_CHANGE);
                        int probability = pc.getAbility().getTotalInt() * 4 + skill.getProbabilityValue() - target.getResistance().getMr();

                        int rnd = RandomUtils.nextInt(100) + 1;

                        if (rnd > probability) {
                            pc.sendPackets("마법이 실패했습니다");
                            return;
                        }
                    }

                    int[] polyArray = {29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862,
                            3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376, 2377, 2378,
                            3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875,
                            3876};

                    int pid = RandomUtils.nextInt(polyArray.length);
                    int polyId = polyArray[pid];

                    if (target instanceof L1PcInstance) {
                        L1PcInstance targetPc = (L1PcInstance) target;

                        if (targetPc.getInventory().checkEquipped(20281)) {
                            targetPc.sendPackets(new S_ShowPolyList(targetPc.getId(), targetPc));

                            if (!targetPc.isShapeChange()) {
                                targetPc.setShapeChange(true);
                            }
                        } else {
                            L1Skills skillTemp = SkillsTable.getInstance().getTemplate(SHAPE_CHANGE);
                            L1PolyMorph.doPoly(targetPc, polyId, skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                        }

                        if (pc.getId() != targetPc.getId()) {
                            targetPc.sendPackets(new S_ServerMessage(241, pc.getName()));
                        }
                    }

                    pc.cancelAbsoluteBarrier();
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }
        }
    }

    public static boolean isAlphaNumeric(String s) {
        if (StringUtils.isEmpty(s)) {
            return false;
        }

        char[] ac = s.toCharArray();

        for (char idx : ac) {
            if (!Character.isLetterOrDigit(idx)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isInvalidName(String name) {
        int numOfNameBytes;

        try {
            numOfNameBytes = name.getBytes("EUC-KR").length;
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
            return false;
        }

        if (isAlphaNumeric(name)) {
            return false;
        }

        if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
            return false;
        }

        return !L1BadNameUtils.getInstance().isBadName(name);
    }

    public static boolean isInValidName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == 'ㄱ' || name.charAt(i) == 'ㄲ' || name.charAt(i) == 'ㄴ' || name.charAt(i) == 'ㄷ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㄸ' || name.charAt(i) == 'ㄹ' || name.charAt(i) == 'ㅁ' || name.charAt(i) == 'ㅂ' ||    //한문자(char)단위로 비교
                    name.charAt(i) == 'ㅃ' || name.charAt(i) == 'ㅅ' || name.charAt(i) == 'ㅆ' || name.charAt(i) == 'ㅇ' ||    //한문자(char)단위로 비교
                    name.charAt(i) == 'ㅈ' || name.charAt(i) == 'ㅉ' || name.charAt(i) == 'ㅊ' || name.charAt(i) == 'ㅋ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㅌ' || name.charAt(i) == 'ㅍ' || name.charAt(i) == 'ㅎ' || name.charAt(i) == 'ㅛ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㅕ' || name.charAt(i) == 'ㅑ' || name.charAt(i) == 'ㅐ' || name.charAt(i) == 'ㅔ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㅗ' || name.charAt(i) == 'ㅓ' || name.charAt(i) == 'ㅏ' || name.charAt(i) == 'ㅣ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㅠ' || name.charAt(i) == 'ㅜ' || name.charAt(i) == 'ㅡ' || name.charAt(i) == 'ㅒ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㅖ' || name.charAt(i) == 'ㅢ' || name.charAt(i) == 'ㅟ' || name.charAt(i) == 'ㅝ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == 'ㅞ' || name.charAt(i) == 'ㅙ' || name.charAt(i) == 'ㅚ' || name.charAt(i) == 'ㅘ' ||    //한문자(char)단위로 비교.
                    name.charAt(i) == '씹' || name.charAt(i) == '좃' || name.charAt(i) == '좆' || name.charAt(i) == 'ㅤ') {
                return true;
            }
        }

        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLetterOrDigit(name.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static String getWhoCharInfo(L1PcInstance pc) {
        String lawfulness;

        int lawful = pc.getLawful();
        float win = pc.getKillCount();
        float lose = pc.getDeathCount();

        String title = "";
        String name = pc.getName();
        String hunt = "";
        String clan = "";

        if (!StringUtils.isEmpty(pc.getTitle())) {
            title = pc.getTitle() + " ";
        }

        if (pc.getClanId() > 0) {
            clan = "[" + pc.getClanName() + "]";
        }

        if (pc.getHuntCount() > 0) {
            hunt += "[수배중]";
        }

        float total = win + lose;
        float winner = ((win * 100) / (total));
        winner = Float.isNaN(winner) ? 0 : winner;

        if (lawful < 0) {
            lawfulness = "[CHAOTIC]";
        } else if (lawful < 500) {
            lawfulness = "[NEUTRAL]";
        } else {
            lawfulness = "[LAWFUL]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(title).append(name).append(" ").append(lawfulness).append(" ").append(hunt).append(" ").append(clan)
                .append("\n\r")
                .append("\\fVKILL:").append((int) win).append("\\fY DEATH:").append((int) lose).append("\\fR 승률:").append((int) winner).append("%").append(" \\fW[PVP]");

        return sb.toString();
    }

    public static boolean isValidSellItem(L1PcInstance pc, int objectId, L1ItemInstance item, int count) {
        if (isNotAvailableTrade(pc, objectId, item, count)) {
            return false;
        }

        if (count >= item.getCount())
            count = item.getCount();

        if (count <= 0) {
            pc.disconnect();
            return false;
        }

        if (objectId != item.getId()) {
            pc.disconnect();
            return false;
        }

        if (!item.isStackable() && count != 1) {
            pc.disconnect();
            return false;
        }

        if (count > item.getCount()) {
            count = item.getCount();
        }

        if (item.getCount() < count) {
            pc.disconnect();
            return false;
        }

        if (count < 1 || item.getCount() <= 0) {
            pc.kick();
            return false;
        }

        if (item.getBless() >= 128) {
            pc.sendPackets(new S_SystemMessage("해당 아이템은 판매 할 수 없습니다."));
            return false;
        }

        if (!item.getItem().isToBeSavedAtOnce()) {
            pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
        }

        if (item.getCount() > 2000000000) {
            return false;
        }

        L1CommonUtils.clearMagicItem(pc, item);

        return count <= 2000000000;
    }

    public static boolean isValidShopOpen(L1PcInstance pc) {
        if (pc == null) return false;
        if (pc.isTeleport() || pc.isDead()) return false;

        if (pc.isInvisible()) {
            pc.sendPackets(new S_ServerMessage(755));
            return false;
        }

        if (pc.getMapId() != L1Map.MAP_USER_SHOP) {
            pc.sendPackets(new S_SystemMessage("개인상점은  시장에서만  열수 있습니다."));
            return false;
        }

        if (pc.getGfxId().getTempCharGfx() != pc.getClassId() && pc.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.SHAPE_CHANGE) >= 0) {
            pc.sendPackets(new S_SystemMessage("변신을 해제 해 주시기 바랍니다."));
            return false;
        }

        if (pc.getLevel() <= 50) {
            pc.sendPackets(new S_SystemMessage("개인상점은 레벨 50이하는 열수 없습니다."));
            return false;
        }

        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (L1PcInstance target : players) {
            if (target.getId() != pc.getId() && target.getAccountName().equalsIgnoreCase(pc.getAccountName())) {
                pc.sendPackets(new S_SystemMessage("무인상점은 한개의 캐릭터만 가능합니다."));
                return false;
            }
        }

        List<L1PcInstance> pcList = L1World.getInstance().getVisiblePlayer(pc, 1);

        if (pcList.size() >= 1) {
            pc.sendPackets(new S_SystemMessage("상점을 열려면 다른상점과 1칸 떨어져서 개설 해야합니다."));
            return false;
        }

        return true;
    }

    public static L1PcInstance getAttackerToPc(L1Character attacker) {
        L1PcInstance result = null;

        if (attacker instanceof L1PcInstance) {
            result = (L1PcInstance) attacker;
        } else if (attacker instanceof L1PetInstance) {
            result = (L1PcInstance) ((L1PetInstance) attacker).getMaster();
        } else if (attacker instanceof L1SummonInstance) {
            result = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();
        }

        return result;
    }

    public static List<L1PcInstance> getInnerPlayers(Integer... mapId) {
        List<L1PcInstance> result = new ArrayList<>();
        Collection<L1PcInstance> list = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : list) {
            if (NumberUtils.contains(pc.getMapId(), mapId)) {
                result.add(pc);
            }
        }

        return result;
    }

    public static boolean isRindArmor(int itemId) {
        return itemId >= 420108 && itemId <= 420111;
    }

    public static boolean isAntaArmor(int itemId) {
        return itemId >= 420100 && itemId <= 420103;
    }

    public static boolean isValaArmor(int itemId) {
        return itemId >= 420112 && itemId <= 420115;
    }

    public static boolean isPapooArmor(int itemId) {
        return itemId >= 420104 && itemId <= 420107;
    }

    public static boolean isDragonArmor(int itemId) {
        return isRindArmor(itemId) || isAntaArmor(itemId) || isValaArmor(itemId) || isPapooArmor(itemId);
    }

    public static <T> List<T> getPagingList(L1PcInstance pc, List<T> searchItems) {
        pc.getPagination().setTotalRecordCount(searchItems.size());

        List<T> nItems;

        L1Pagination pagination = pc.getPagination();

        int recordCountPerPage = pagination.getRecordCountPerPage();

        if (searchItems.size() >= recordCountPerPage) {
            int start = (pagination.getCurrentPageNo() - 1) * recordCountPerPage;
            int end = Math.min(start + recordCountPerPage, searchItems.size());

            try {
                nItems = searchItems.subList(start, end);
            } catch (IllegalArgumentException e) {
                pc.getPagination().setCurrentPageNo(1);
                nItems = searchItems.subList(0, recordCountPerPage);
            }
        } else {
            nItems = new ArrayList<>(searchItems);
        }

        pc.getPagination().setSearchList(nItems);

        return nItems;
    }

    public static L1BringStonePer calcBringStonePer(int level, int wis) {
        if (wis > 25) {
            wis = 25;
        }

        int dark = (int) (10 + (level * 0.8) + (wis - 6) * 1.2);

        int brave = dark / 2;
        int wise = brave / 4;
        int kayser = wise / 2;

        return new L1BringStonePer(dark, brave, wise, kayser);
    }

    public static int randomClassId() {
        int[] MALE_LIST = new int[]{CLASSID_KNIGHT_MALE, CLASSID_DARK_ELF_MALE, CLASSID_ELF_MALE};
        int[] FEMALE_LIST = new int[]{CLASSID_KNIGHT_FEMALE, CLASSID_DARK_ELF_FEMALE, CLASSID_ELF_FEMALE};

        int sex = RandomUtils.nextInt(1);
        int type = RandomUtils.nextInt(MALE_LIST.length);
        int clazz = 0;

        switch (sex) {
            case 0:
                clazz = MALE_LIST[type];
                break;
            case 1:
                clazz = FEMALE_LIST[type];
                break;
        }

        return clazz;
    }

    public static L1ItemInstance createDummyItemInstance(L1Item item) {
        L1ItemInstance result = ItemTable.getInstance().functionItem(item);
        result.setItem(item);
        result.setIdentified(true);

        return result;
    }

    //판매하려는 아이템이 소환중인가
    public static boolean isUsingDoll(L1ItemInstance targetItem, L1PcInventory inv) {
        if (targetItem instanceof MagicDollItemInstance) {
            if (inv.getOwner().getCurrentDoll() != null) {
                return targetItem.equals(inv.getOwner().getCurrentDollItem());
            }
        }

        return false;
    }

    public static Integer getAllRankingByName(String charName) {
        String sql = "SELECT\n" +
                "	RANKING \n" +
                "FROM\n" +
                "	( SELECT char_name, dense_rank() over ( ORDER BY exp DESC ) RANKING FROM characters WHERE AccessLevel = 0) T \n" +
                "WHERE\n" +
                "	CHAR_NAME = ?";

        Integer rank = Integer.MAX_VALUE;

        try {
            rank = SqlUtils.selectInteger(sql, charName);
        } catch (Exception ignored) {
        }

        return rank;
    }

    public static Integer getRankingByName(int type, String charName) {
        String sql = "SELECT\n" +
                "	RANKING \n" +
                "FROM\n" +
                "	( SELECT char_name, dense_rank() over ( ORDER BY exp DESC ) RANKING FROM characters WHERE type = ? AND AccessLevel = 0) T \n" +
                "WHERE\n" +
                "	CHAR_NAME = ?";

        Integer rank = Integer.MAX_VALUE;

        try {
            rank = SqlUtils.selectInteger(sql, type, charName);
        } catch (Exception ignored) {
        }

        return rank;
    }

    public static void deleteSpell(L1PcInstance pc) {
        int charId = pc.getId();
        SqlUtils.update("DELETE FROM character_skills WHERE char_obj_id=?", charId);
    }

    public static void statInit(L1PcInstance pc) {
        try {
            if (pc.getSkillEffectTimerSet().hasSkillEffect(ADVANCE_SPIRIT)) {
                pc.getSkillEffectTimerSet().removeSkillEffect(ADVANCE_SPIRIT);
            }

            L1SkillUse l1skilluse = new L1SkillUse(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0);
            l1skilluse.run();

            takeoffItems(pc);

            pc.setReturnStat(pc.getExp());
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
            pc.sendPackets(new S_OwnCharStatus2(pc));
            pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));

            pc.setStatReturnCheck(true);

            try {
                pc.save();
            } catch (Exception e) {
                logger.error("스텟초기화 오류", e);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public static boolean isInValidItemInInventory(L1PcInstance pc, int objectId, int count) {
        try {
            L1ItemInstance item = pc.getInventory().getItem(objectId);

            if (item == null) {
                return true;
            }

            int dbCount = SqlUtils.selectInteger("SELECT count from character_items where id=?", objectId);

            String msg = "창고 맡기기 버그시도 - 아이템명 : " + item.getName() + ", 수량 : " + count + ", 캐릭명 : " + pc.getName();

            if (count < 0) {
                return true;
            }

            if (dbCount <= 0) {
                L1LogUtils.bugLog(msg);
                sendMessageToAllGm(msg);
                pc.disconnect();
                return true;
            }

            if (count > dbCount) {
                if (!item.getName().contains("체력 회복제")) {
                    L1LogUtils.bugLog(msg);
                    sendMessageToAllGm(msg);
                    pc.disconnect();
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return false;
    }

    public static boolean isValidItemInWareHouse(L1PcInstance pc, int objectId, String tableName, int count) {
        try {
            String msg = "창고 찾기 버그시도 - 캐릭명 : " + pc.getName() + ", 수량 : " + count + ",";

            int dbCount = SqlUtils.selectInteger("SELECT count from " + tableName + " where id=?", objectId);

            if (dbCount <= 0) {
                L1LogUtils.bugLog(msg);
                sendMessageToAllGm(msg);
                pc.disconnect();
                return false;
            }

            if (count > dbCount) {
                L1LogUtils.bugLog(msg);
                sendMessageToAllGm(msg);
                pc.disconnect();
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return true;
    }

    public static boolean isValidItemInClanWareHouse(L1PcInstance pc, int objectId, int count) {
        return isValidItemInWareHouse(pc, objectId, "clan_warehouse", count);
    }

    public static void sendPoisonStatus(L1PcInstance pc, int poisonId) {
        if (poisonId != 0) {
            pc.sendPackets(new S_Poison(pc.getId(), poisonId));
            Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), poisonId));
        }
    }

    public static boolean isNotExpRestoreAble(L1PcInstance pc) {
        int diffLevel = pc.getHighLevel() - pc.getLevel();

        if (diffLevel > 1) {
            pc.sendPackets("경험치 복구를 이용할 수 없습니다");
            return true;
        }

        return false;
    }

    public static void locationEffect(L1PcInstance pc, int effectId) {
        pc.sendPackets(new S_EffectLocation(pc.getX(), pc.getY(), effectId));
        Broadcaster.broadcastPacket(pc, new S_EffectLocation(pc.getX(), pc.getY(), effectId));
    }

    public static void locationEffect(L1Character cha, int x, int y, int effectId) {
        cha.sendPackets(new S_EffectLocation(x, y, effectId));
        Broadcaster.broadcastPacket(cha, new S_EffectLocation(x, y, effectId));
    }

    public static void safeMode(L1PcInstance pc, boolean show) {
        if (L1Opcodes.SERVER_VERSION == 3.1) {
            if (show) {
                pc.sendPackets(new S_IconFromEffectList(pc.getId(), 278));
            } else {
                pc.sendPackets(new S_IconFromEffectList(pc.getId(), 279));
            }
        } else if (L1Opcodes.SERVER_VERSION == 3.8) {
            new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 290, show);
        }
    }

    public static void changeItem(L1PcInstance pc, L1ItemInstance targetItem, List<Integer> changed, int bless) {
        changed.removeIf(o -> o == targetItem.getItemId());

        if (changed.isEmpty()) {
            pc.sendPackets("아무일도 일어나지 않았습니다");
            return;
        }

        Integer newItemId = changed.get(RandomUtils.nextInt(changed.size()));

        L1ItemInstance newItem = ItemTable.getInstance().createItem(newItemId);

        if (bless != -1) {
            newItem.setBless(bless);
        }
        newItem.setCount(1);
        newItem.setEnchantLevel(targetItem.getEnchantLevel());
        newItem.setIdentified(true);
        newItem.setAttrEnchantLevel(targetItem.getAttrEnchantLevel());

        if (pc.getInventory().checkAddItem(newItem, 1) != L1Inventory.OK) {
            pc.sendPackets("인벤토리를 확인하세요");
            return;
        }

        pc.sendPackets("변경 전 : " + targetItem.getViewName());
        pc.sendPackets("변경 후 : " + newItem.getViewName());

        L1CommonUtils.locationEffect(pc, 7321);

        pc.getInventory().storeItem(newItem);
        pc.getInventory().removeItem(targetItem, 1);
    }

    public static String getAttrNameKr(int attrEnchantLevel) {
        StringBuilder name = new StringBuilder();

        if (attrEnchantLevel > 0) {
            int enchant = 0;

            if (attrEnchantLevel <= 5) {
                name.append("화령");
                enchant = attrEnchantLevel;
            } else if (attrEnchantLevel <= 10) {
                name.append("수령");
                enchant = attrEnchantLevel - 5;
            } else if (attrEnchantLevel <= 15) {
                name.append("풍령");
                enchant = attrEnchantLevel - 10;
            } else if (attrEnchantLevel <= 20) {
                name.append("지령");
                enchant = attrEnchantLevel - 15;
            }

            name.append(":").append(enchant).append("단");
        }

        return name.toString();
    }

    public static void useDragonPerl(L1PcInstance pc) {
        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGON_PERL)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_DRAGON_PERL);
        }

        pc.cancelAbsoluteBarrier();// 앱솔해제(팩에 이 메소드없으면 무시)

        int time = 600 * 1000;
        int stime = ((time / 1000) / 4) - 2;

        pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_DRAGON_PERL, time);

        pc.sendPackets(new S_PacketBox(L1PacketBoxType.DRAGONPERL, 8, stime));
        pc.sendPackets(new S_DragonPerl(pc.getId(), 8));
        Broadcaster.broadcastPacket(pc, new S_DragonPerl(pc.getId(), 8));

        L1CommonUtils.locationEffect(pc, 7954);

        pc.setDragonPerlSpeed(1);
    }

    public static void checkItemAndMessage(L1PcInstance pc, int checkItemId) {
        checkItemAndMessage(pc, checkItemId, 1);
    }

    public static void checkItemAndMessage(L1PcInstance pc, int checkItemId, int count) {
        if (!pc.getInventory().checkItem(checkItemId, count)) {
            L1Item item = ItemTable.getInstance().findItem(checkItemId);

            int requiredCount = count;

            List<L1ItemInstance> invItemList = pc.getInventory().findItemsId(checkItemId);

            if (!invItemList.isEmpty()) {
                for (L1ItemInstance is : invItemList) {
                    requiredCount -= is.getCount();
                }
            }

            pc.sendPackets(item.getName() + "(" + requiredCount + ")개를 소지하고 있지 않습니다");
        }
    }

    public static void checkItemAndEnchantLevelAndMessage(L1PcInstance pc, int checkItemId, int count, int enchantLevel) {
        int requiredCount = pc.getInventory().makeCheckEnchantAndNotEqCount(checkItemId, enchantLevel) - count;

        if (requiredCount < 0) {
            L1Item item = ItemTable.getInstance().findItem(checkItemId);

            if (item != null) {
                pc.sendPackets("+" + enchantLevel + " " + item.getName() + "(" + -requiredCount + ")개를 소지하고 있지 않습니다");
            }
        }
    }

    public static void enchantFailEffect(L1PcInstance pc) {
        int gfxId = SkillsTable.getInstance().getTemplate(L1SkillId.ENCHANT_FAIL).getCastGfx();
        pc.sendPackets(new S_SkillSound(pc.getId(), gfxId));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxId));
    }

    public static boolean isLastabardMap(int mapId) {
        return NumberUtils.contains(mapId,
                450, 451, 452, 453, 454, 455,
                456, 457, 460, 461, 462, 463, 464, 465,
                466, 467, 468, 470, 471, 472, 473, 474,
                475, 476, 477, 478, 490, 491, 492, 493,
                494, 495, 496, 530, 531, 532, 533, 534,
                536, 780, 781, 782);
    }

    public static void guide(L1PcInstance pc) {
        pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_support"));
    }

    public static void guidePotion(L1PcInstance pc) {
        String p0 = pc.getAutoPotion().getAutoPotionNumString();
        String p1 = pc.getAutoPotion().getAutoPotionPercent() + "";

        if (!pc.getAutoPotion().isAutoPotion()) {
            p0 = "꺼짐";
        }

        String p2 = pc.isAutoDragonDiamond() ? "켜짐" : "꺼짐";
        String p3 = pc.isAutoDragonPerl() ? "켜짐" : "꺼짐";
        String p4 = pc.isMarkShow() ? "켜짐" : "꺼짐";

        pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_support1", p0, p1, p2, p3, p4));
    }

    public static L1DollInstance getDoll(L1PcInstance pc, int itemObjectId) {
        for (L1DollInstance doll : pc.getDollList().values()) {
            if (doll == null)
                continue;

            if (doll.getItemObjId() == itemObjectId) {
                return doll;
            }
        }

        return null;
    }

    public static boolean isArmor(L1ItemInstance item) {
        int type = item.getItem().getType();
        int type2 = item.getItem().getType2();

        return type == 2 && type2 == 2;
    }

    public static void createOldItemChange(L1PcInstance pc, int oldItemId, int oldEnchant, int oldItemCount, int newItemId, int newEnchantLevel) {
        createOldItemChange(pc, oldItemId, oldEnchant, oldItemCount, newItemId, newEnchantLevel, 1);
    }

    public static void createOldItemChange(L1PcInstance pc, int oldItemId, int oldEnchant, int oldItemCount, int newItemId, int newEnchantLevel, int bless) {
        L1CommonUtils.checkItemAndEnchantLevelAndMessage(pc, oldItemId, oldItemCount, oldEnchant);

        if (pc.getInventory().checkItem(oldItemId, oldItemCount, oldEnchant)) {
            L1ItemInstance newItem = ItemTable.getInstance().createItem(newItemId);
            newItem.setIdentified(true);
            newItem.setCount(1);
            newItem.setBless(bless);
            newItem.setEnchantLevel(newEnchantLevel);

            if (pc.getInventory().checkAddItem(newItem, newItem.getCount()) != L1Inventory.OK) {
                pc.sendPackets("소지하고 있는 아이템이 너무 많습니다");
                return;
            }

            int deleteCount = 0;

            List<L1ItemInstance> list = pc.getInventory().getItems(oldItemId, oldItemCount, oldEnchant);

            for (L1ItemInstance is : new ArrayList<>(list)) {
                int count = pc.getInventory().removeItem(is, 1);
                deleteCount += count;
            }

            if (deleteCount != oldItemCount) {
                pc.sendPackets("오류가 발생했습니다 운영자에게 문의하세요");
                return;
            }

            pc.getInventory().storeItem(newItem);
            pc.sendPackets(newItem.getViewName() + "를 획득하였습니다");
        }
    }

    public static String nowStr() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[" + format.format(new Date()) + "] : ";
    }

    public static String getClassNameByType(int type) {
        switch (type) {
            case 0:
                return "군주";
            case 1:
                return "기사";
            case 2:
                return "요정";
            case 3:
                return "법사";
            case 4:
                return "다크엘프";
        }

        return "";
    }

    public static void clearMagicItem(L1PcInstance pc, L1ItemInstance item) {
        if (item.getAcByMagic() > 0) {
            item.removeArmorEnchant(pc);
        }

        if (item.getDmgByMagic() > 0) {
            item.removeWeaponEnchant(pc);
        }

        if (item.getHolyDmgByMagic() > 0) {
            item.removeWeaponEnchant(pc);
        }

        if (item.getHitByMagic() > 0) {
            item.removeWeaponEnchant(pc);
        }
    }

    public static void sendStandByMsg(L1PcInstance pc) {
        pc.sendGreenMessageAndSystemMessage(L1Msg.STAND_BY_MSG);
    }

    public static boolean isStandByServer() {
        return CodeConfig.STANDBY_SERVER;
    }

    public static boolean isStandByServer(L1PcInstance pc) {
        if (pc.isGm()) {
            return false;
        }

        return isStandByServer();
    }

    public static int calcHeading(int myx, int myy, int tx, int ty) {
        int newheading = 0;

        if (tx > myx && ty > myy) {
            newheading = 3;
        }
        if (tx < myx && ty < myy) {
            newheading = 7;
        }
        if (tx > myx && ty == myy) {
            newheading = 2;
        }
        if (tx < myx && ty == myy) {
            newheading = 6;
        }
        if (tx == myx && ty > myy) {
            newheading = 4;
        }
        if (tx < myx && ty > myy) {
            newheading = 5;
        }
        if (tx > myx && ty < myy) {
            newheading = 1;
        }
        return newheading;
    }

    public static void returnSelectCharacters(L1Client client) {
        try {
            client.sendPacket(new S_PacketBox(L1PacketBoxType.LOGOUT));

            if (AuthorizationUtils.getInstance().isAlreadyLoginAccount(client.getAccountName())) {
                return;
            }

            if (client.getActiveChar() != null) {
                L1PcInstance pc = client.getActiveChar();

                if (pc == null) {
                    return;
                }

                pc.setWorld(false);
                pc.save();
                pc.saveInventory();

                if (pc.isDead()) {
                    return;
                }

                pc.quitGame();
                pc.logout();

                client.setActiveChar(null);
            } else {
                client.disconnectNow();
                logger.debug("연결이 종료되었습니다 계정 : {}", client.getAccountName());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void createNewItem(L1PcInstance pc, int item_id, int count) {
        L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
        item.setCount(count);

        if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
            pc.getInventory().storeItem(item);
        } else {
            L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
        }

        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
    }

    public static void unEquipTurbun(L1PcInstance pc) {
        for (int itemId = 421000; itemId <= 421023; itemId++) {
            if (pc.getInventory().checkEquipped(itemId)) {
                L1ItemInstance item = pc.getInventory().findEquippedItemId(itemId);
                pc.getInventory().setEquipped(item, false);
            }
        }
    }

    public static boolean itemUseAbleCheck(L1PcInstance pc, L1ItemInstance item) {
        return pc.isCrown() && item.getItem().isUseRoyal()
                || pc.isKnight() && item.getItem().isUseKnight()
                || pc.isElf() && item.getItem().isUseElf()
                || pc.isWizard() && item.getItem().isUseMage()
                || pc.isDarkElf() && item.getItem().isUseDarkElf()
                || pc.isDragonKnight() && item.getItem().isUseDragonKnight()
                || pc.isIllusionist() && item.getItem().isUseBlackWizard();
    }

    public static int checkSt(L1ItemInstance targetItem) {
        int st = 0;
        if (targetItem.isIdentified()) st += 1;
        if (!targetItem.getItem().isTradeAble()) st += 2;
        if (!targetItem.getItem().isDeleteAble()) st += 4;
        if (targetItem.getItem().getSafeEnchant() < 0) st += 8;
        if (targetItem.getBless() >= 128) {
            st = 32;
            if (targetItem.isIdentified()) {
                st += 15;
            } else {
                st += 14;
            }
        }

        return st;
    }

    public static int getStatus(L1PcInstance pc) {
        int status = STATUS_PC;

        if (pc.isInvisible() || pc.isGmInvis()) {
            status |= STATUS_INVISIBLE;
        }

        if (pc.isBrave()) {
            status |= STATUS_BRAVE;
        }

        if (pc.isElfBrave()) {
            status |= STATUS_BRAVE;
            status |= STATUS_ELF_BRAVE;
        }

        if (pc.isFastMovable()) {
            status |= STATUS_FAST_MOVE_ABLE;
        }

        if (pc.isParalyzed()) {
            status |= STATUS_FREEZE;
        }

        return status;
    }

    public static boolean isDragonT(int itemId) {
        return NumberUtils.contains(itemId, 55000095, 55000096, 55000097, 55000098, 155000095, 155000096, 155000097, 155000098);
    }

    public static boolean isFantasyFood(int itemId) {
        return NumberUtils.contains(itemId,
                436017,
                436019,
                436023,
                436024,
                436021,
                436022,
                436020,
                436018,
                49059,
                49064, 49060, 49057, 49062, 49058, 49061, 49063, 41287, 41292, 41291, 41285, 41290, 41289, 41286, 41288
        );
    }

    public static int getDollStep(int itemId) {
        Map<String, List<Integer>> dollMap = L1DollItemId.dollMap;

        for (String key : dollMap.keySet()) {
            List<Integer> dollList = dollMap.get(key);

            if (key.contains("Normal") || key.contains("Bless")) {
                continue;
            }

            if (dollList.contains(itemId)) {
                return Integer.parseInt(key.replace("dollList", ""));
            }
        }

        return 0;
    }

    public static String getGradeByColor(int grade) {
        if (grade == 10) {
            return "fT";
        } else if (grade == 20) {
            return "fU";
        } else if (grade == 30) {
            return "fY";
        } else if (grade == 40) {
            return "fR";
        }

        return null;
    }

    public static boolean isGiranVillage(int x, int y, int map) {
        if (map != 4) {
            return false;
        }

        return x >= 33332 && x <= 33502 && y >= 32632 && y <= 32895;
    }

    public static L1ItemInstance findItemByObjectId(int objectId, List<L1ItemInstance> list) {
        for (L1ItemInstance is : list) {
            if (objectId == is.getId()) {
                return is;
            }
        }

        return null;
    }

    public static boolean isShortDistance(L1Character attacker, L1Character target) {
        if (attacker == null)
            return false;

        int targetX = target.getX();
        int targetY = target.getY();

        boolean isLongRange = (attacker.getLocation().getTileLineDistance(new Point(targetX, targetY)) > 1);

        if (isLongRange) {
            return false;
        }

        if (attacker instanceof L1MonsterInstance) {
            L1MonsterInstance m = (L1MonsterInstance) attacker;
            int bowActId = m.getTemplate().getBowActId();
            return bowActId <= 0;
        }
        return true;
    }

    public static int attrEnchantLevelValue(int itemId, int attrLevel) {
        int result = 0;

        if (itemId == L1ItemId.FIRE_ENCHANT_WEAPON_SCROLL) {
            if (attrLevel == 0) {
                result = 1;
            } else if (attrLevel == 1) {
                result = 2;
            } else if (attrLevel == 2) {
                result = 3;
            } else if (attrLevel == 3) {
                result = 4;
            } else if (attrLevel == 4) {
                result = 5;
            }
        } else if (itemId == L1ItemId.WATER_ENCHANT_WEAPON_SCROLL) {
            if (attrLevel == 0) {
                result = 6;
            } else if (attrLevel == 6) {
                result = 7;
            } else if (attrLevel == 7) {
                result = 8;
            } else if (attrLevel == 8) {
                result = 9;
            } else if (attrLevel == 9) {
                result = 10;
            }
        } else if (itemId == L1ItemId.WIND_ENCHANT_WEAPON_SCROLL) {
            if (attrLevel == 0) {
                result = 11;
            } else if (attrLevel == 11) {
                result = 12;
            } else if (attrLevel == 12) {
                result = 13;
            } else if (attrLevel == 13) {
                result = 14;
            } else if (attrLevel == 14) {
                result = 15;
            }
        } else if (itemId == L1ItemId.EARTH_ENCHANT_WEAPON_SCROLL) {
            if (attrLevel == 0) {
                result = 16;
            } else if (attrLevel == 16) {
                result = 17;
            } else if (attrLevel == 17) {
                result = 18;
            } else if (attrLevel == 18) {
                result = 19;
            } else if (attrLevel == 19) {
                result = 20;
            }
        }

        return result;
    }

    public static boolean hasNotAdena(L1PcInstance pc) {
        if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
            pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분하지 않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            return true;
        }
        return false;
    }

    public static boolean isNotAvailableClan(L1PcInstance pc, L1Clan clan) {
        if (pc.getClanId() == 0 || clan == null) {
            pc.sendPackets(new S_ServerMessage(208));
            return true;
        }
        return false;
    }

    public static boolean isNotAbleWhCount(Warehouse warehouse, L1PcInstance pc, L1ItemInstance item, int count) {
        if (warehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
            pc.sendPackets(new S_ServerMessage(75));
            return true;
        }

        return false;
    }

    public static boolean checkPetList(L1PcInstance pc, L1ItemInstance item) {
        Collection<L1DollInstance> dollList = pc.getDollList().values();

        for (L1DollInstance doll : dollList) {
            if (item.getId() == doll.getItemObjId()) {
                pc.sendPackets(new S_ServerMessage(1181)); //
                return false;
            }
        }

        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (Object petObject : petList) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                if (item.getId() == pet.getItemObjId()) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotAbleWareHouse(L1PcInstance pc, L1ItemInstance item) {
        if (!item.getItem().isWarehouse()) {
            return true;
        }

        if (item.getItem().getItemId() == 423012 || item.getItem().getItemId() == 423013) { // 10주년티
            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
            return true;
        }

        if (item.getItemId() == 433008) {
            return true;
        }

        if (item.getEndTime() != null) {
            return true;
        }

        return item.getBless() >= 128;
    }

    public static void npcCheckTarget(L1NpcInstance npc) {
        L1Character target = npc.getTarget();

        if (target == null
                || (Math.abs(npc.getX() - npc.getHomeX())) > 20
                || (Math.abs(npc.getY() - npc.getHomeY())) > 20
                || target.getMapId() != npc.getMapId()
                || target.getCurrentHp() <= 0
                || target.isDead()
                || (target.isInvisible() && !npc.getTemplate().isAgrocoi() && !npc.getHateList().containsKey(target))) {

            if (target != null) {
                npc.targetClear();
                npc.teleport(npc.getHomeX(), npc.getHomeY(), npc.getSpawn().getHeading());
            }

            if (!npc.getHateList().isEmpty()) {
                npc.setTarget(npc.getHateList().getMaxHateCharacter());
                npc.checkTarget();
            }
        }
    }

    public static L1PcInstance findWorldUserOrOffLineUserByName(String name) {
        L1PcInstance pc = L1World.getInstance().getPlayer(name);

        if (pc == null) {
            pc = CharacterTable.getInstance().restoreCharacter(name);
        }

        return pc;
    }

    public static void sendMessageToAllGm(String msg) {
        Collection<L1PcInstance> list = L1World.getInstance().getGmPlayers();

        for (L1PcInstance pc : list) {
            if (pc.isGm()) {
                pc.sendPackets("\\fW" + msg);
            }
        }
    }

    public static void takeoffItems(L1PcInstance pc) {
        if (pc.getWeapon() != null)
            pc.getInventory().setEquipped(pc.getWeapon(), false, false, false);

        pc.getInventory().takeoffEquip(945);
        pc.sendPackets(new S_CharVisualUpdate(pc));

        for (L1ItemInstance armor : pc.getInventory().getItems()) {
            if (armor != null && armor.isEquipped()) {
                pc.getInventory().setEquipped(armor, false, false, false);
            }
        }
    }

    public static void removeSpawnByMapId(int mapId) {
        Collection<L1MonsterInstance> monsterList = L1World.getInstance().getAllMonsters();

        for (L1MonsterInstance m : monsterList) {
            if (m.getMapId() != mapId) {
                continue;
            }

            if (L1BossSpawnManager.getInstance().isSpawned(m)) {
                continue;
            }

            m.setRespawn(false);
            m.deleteMe();
        }
    }
}
