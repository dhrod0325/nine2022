package ks.packets.clientpackets;

import ks.constants.L1SkillId;
import ks.core.datatables.pet.PetTypeTable;
import ks.core.network.L1Client;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ItemName;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class C_GiveItem extends ClientBasePacket {
    private final Logger logger = LogManager.getLogger();

    private final static String[] receivableImpls = new String[]{"L1Npc", "L1Monster", "L1Guardian", "L1Teleporter", "L1Guard"};

    public C_GiveItem(byte[] data, L1Client client) {
        super(data);
        int targetId = readD();

        readH();
        readH();

        int itemId = readD();
        int count = readD();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }

        pc.saveInventory();

        L1Object object = L1World.getInstance().findObject(targetId);
        L1NpcInstance target = (L1NpcInstance) object;
        L1Inventory targetInv = target.getInventory();
        L1Inventory inv = pc.getInventory();
        L1ItemInstance item = inv.getItem(itemId);

        if (item == null) {
            return;
        }

        if (L1CommonUtils.isTwoLogin(pc))
            return;

        if (!isNpcItemReceivable(target.getTemplate())) {
            if (!(item.getItem().getItemId() == 40499) || !(item.getItem().getItemId() == 40507)) {
                return;
            }
        }

        if (item.getItemDelay().isDelay()) {
            return;
        }

        if (!item.getItem().isTradeAble()) {
            pc.sendPackets(item.getName() + "은 땅에 버리거나 교환할수 없습니다");
            return;
        }

        if (item.isEquipped()) {
            pc.sendPackets(new S_ServerMessage(141));
            return;
        }

        if (item.getBless() >= 128) {// 봉인
            pc.sendPackets(new S_ServerMessage(141));
            return;
        }

        if (itemId != item.getId()) {
            pc.disconnect(pc.getName() + " error1");
            return;
        }

        if (!item.isStackable() && count != 1) {
            pc.disconnect(pc.getName() + " error2");
            return;
        }

        if (item.getCount() <= 0 || count <= 0) {
            pc.disconnect(pc.getName() + " error3");
            return;
        }

        if (count >= item.getCount()) {
            count = item.getCount();
        }

        if (item.getItem().getItemId() == 423012 || item.getItem().getItemId() == 423013) { // 10주년티
            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
            return;
        }

        if (!item.getItem().isTradeAble() || item.getItemId() == 40308 || item.getItemId() == 41159) {
            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
            return;
        }

        for (Object petObject : pc.getPetList().values()) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                if (item.getId() == pet.getItemObjId()) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }
            }
        }

        if (!pc.isGm() && targetInv.checkAddItem(item, count) != L1Inventory.OK) {
            pc.sendPackets(new S_ServerMessage(942));
            return;
        }

        if (item.isStackable()) {
            item.getItemDelay().start(2000);
        }

        L1CommonUtils.clearMagicItem(pc, item);

        L1PetType petType = PetTypeTable.getInstance().get(target.getTemplate().getNpcId());

        if (petType != null) {
            if ((petType.getBaseNpcId() == 46046 || petType.getItemIdForTaming() != 0) && item.getItem().isUseHighPet()) {
                return;
            }
        }

        item = inv.tradeItem(item, count, targetInv);
        target.onGetItem(item);
        target.getLight().turnOnOffLight();
        pc.getLight().turnOnOffLight();
        pc.saveInventory();

        if (petType == null || target.isDead()) {
            return;
        }

        if (item.getItemId() == petType.getItemIdForTaming() && (item.getItemId() == 490026 && target.getTemplate().getNpcId() == 45711) || (item.getItemId() == 490027 && target.getTemplate().getNpcId() == 45313)) {
            if (item.getItemId() >= 490024 && item.getItemId() <= 490027) {
                int value = RandomUtils.nextInt(100) + 1;
                value += item.getItemId() == 490026 || item.getItemId() == 490027 ? 20 : 0;

                if (value > 90) {
                    tamePet(pc, target);
                }
            }
        }

        if (item.getItemId() == petType.getItemIdForTaming()) {
            tamePet(pc, target);
        }

        if (item.getItemId() == 40070 && petType.canEvolve() && petType.getItemIdForTaming() == 40060) {
            evolvePet(pc, target);
        }
        if (item.getItemId() == 27 && petType.canEvolve() && petType.getItemIdForTaming() == 40407) {//테이밍 뼈조각
            evolvePet(pc, target);
        }
        if (item.getItemId() == 49 && petType.canEvolve() && petType.getItemIdForTaming() == 2) {
            evolvePet(pc, target);
        }
        if (item.getItemId() == 58 && petType.canEvolve() && petType.getItemIdForTaming() == 3) {
            evolvePet(pc, target);
        }
        if (item.getItemId() == 213 || item.getItemId() == 217 || item.getItemId() == 7704 || item.getItemId() == 221 || item.getItemId() == 7705 || item.getItemId() == 7706 && petType.canEvolve() && petType.getItemIdForTaming() == 4) {
            evolvePet(pc, target);
        }

        if (item.getItemId() == 40070
                && petType.canEvolve()
                && (petType.getItemIdForTaming() == 40057
                || petType.getItemIdForTaming() == 490024
                || petType.getItemIdForTaming() == 490025
                || petType.getItemIdForTaming() == 490026
                || petType.getItemIdForTaming() == 490027)) {
            evolvePet(pc, target);
        }

        if (item.getItemId() == 41310 && petType.canEvolve() && petType.getItemIdForTaming() == 0) {
            evolvePet(pc, target);
        }

        if ((item.getItem().getMaterial() == 4 || item.getItemId() == 40060) && item.getItem().getType() == 7) {// 동물성 음식류
            petfoodgive(target, item);
        }

        Object oo = L1World.getInstance().findObject(targetId);

        if (oo instanceof L1PetInstance) {
            L1PetInstance pets = (L1PetInstance) oo;

            if (item.getItem().isUseHighPet() && petType.getItemIdForTaming() == 0) {
                if (item.getItemId() >= 427100 && item.getItemId() <= 427109) {
                    pets.usePetWeapon(item);
                } else if (item.getItemId() >= 427000 && item.getItemId() <= 427007) {
                    pets.usePetArmor(item);
                }
            }
        }
    }

    private boolean isNpcItemReceivable(L1Npc npc) {
        for (String impl : receivableImpls) {
            if (npc.getImpl().equals(impl)) {
                return true;
            }
        }
        return false;
    }

    private void tamePet(L1PcInstance pc, L1NpcInstance target) {
        if (target instanceof L1PetInstance || target instanceof L1SummonInstance) {
            return;
        }

        int petCost = 0;

        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (L1NpcInstance pet : petList) {
            petCost += pet.getPetCost();
        }

        int charisma = pc.getAbility().getTotalCha();

        charisma = petCharisma(pc, petCost, charisma);

        L1PcInventory inv = pc.getInventory();
        String npcName = target.getTemplate().getName();

        if (charisma >= 6) {
            if (inv.getSize() < 180) {
                if (isTamePet(target)) {
                    L1ItemInstance petamu = inv.storeItem(40314, 1);

                    if (petamu != null) {
                        new L1PetInstance(target, pc, petamu.getId());

                        pc.sendPackets(new S_ItemName(petamu));
                        pc.sendPackets(new S_SystemMessage(npcName + "의 목걸이를 얻었습니다."));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(324)); // 길들이는데 실패했습니다.
                }
            } else {
                pc.sendPackets(new S_ServerMessage(263));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(319));
        }
    }

    static int petCharisma(L1PcInstance pc, int petCost, int charisma) {
        if (pc.isCrown()) { // 군주
            charisma += 6;
        } else if (pc.isElf()) { // 요정
            charisma += 12;
        } else if (pc.isWizard()) { // 마법사
            charisma += 6;
        } else if (pc.isDarkElf()) { // 다크엘프
            charisma += 6;
        } else if (pc.isDragonKnight()) { // 용기사
            charisma += 6;
        } else if (pc.isIllusionist()) { // 환술사
            charisma += 6;
        }

        charisma -= petCost;
        return charisma;
    }

    private void evolvePet(L1PcInstance pc, L1NpcInstance target) {
        if (!(target instanceof L1PetInstance)) {
            return;
        }

        L1PcInventory inv = pc.getInventory();
        L1PetInstance pet = (L1PetInstance) target;
        L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
        String npcname = target.getTemplate().getName();

        if (pet.getLevel() >= 30 && pc == pet.getMaster() && petamu != null) {
            L1ItemInstance highpetamu = inv.storeItem(40316, 1);
            if (highpetamu != null) {
                pet.evolvePet(highpetamu.getId()); // 진화시킨다
                pc.sendPackets(new S_ItemName(highpetamu));
                inv.removeItem(petamu, 1);
                pc.sendPackets(new S_SystemMessage(npcname + "의 진화에 성공 하였습니다."));
            }
        } else {
            pc.sendPackets(new S_SystemMessage(npcname + "의 진화 조건이 맞지 않습니다."));
        }
    }

    private boolean isTamePet(L1NpcInstance npc) {
        boolean isSuccess = false;

        int npcId = npc.getTemplate().getNpcId();

        switch (npcId) {
            case 45313: // 호랑이
            case 46044: // 아기판다곰
            case 46042: // 아기캥거루
            case 45044: // 라쿤
            case 45711: // 아기진돗개
            case 45040: // 곰
            case 45049: // 열혈토끼
            case 45048: // 여우
            case 45039: // 고양이
            case 45042: // 도베르만
            case 45053: // 허스키
                isSuccess = true;
                break;
            default:
                if (npc.getMaxHp() / 2 > npc.getCurrentHp()) {
                    isSuccess = true;
                }
                break;
        }

        return isSuccess;
    }

    private void petfoodgive(L1NpcInstance target, L1ItemInstance item) {
        if (!(target instanceof L1PetInstance)) {
            return;
        }

        L1PetInstance pet = (L1PetInstance) target;
        L1Inventory inv = target.getInventory();

        if ((target.getNpcId() == 46042 || target.getNpcId() == 46043) && item.getItemId() == 41423) {// 캥거루 먹이
            pet.setFood(0);
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
        } else if ((target.getNpcId() == 46044 || target.getNpcId() == 46045) && item.getItemId() == 41424) {
            // 판다곰 먹이
            inv.removeItem(item, 1);
            pet.setFood(0);
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
        } else if ((target.getNpcId() == 45049 || target.getNpcId() == 45695) && item.getItemId() == 40060) {
            inv.removeItem(item, 1);
            pet.setFood(0);
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
        } else {
            inv.removeItem(item, 1);
            pet.setFood(0);
            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
        }
    }
}
