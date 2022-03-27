package ks.packets.clientpackets;

import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pet.PetTable;
import ks.core.network.L1Client;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

import java.util.Collection;

public class C_SelectList extends ClientBasePacket {
    public C_SelectList(byte[] data, L1Client clientthread) {
        super(data);

        int itemObjectId = readD();
        int npcObjectId = readD();

        L1PcInstance pc = clientthread.getActiveChar();

        if (pc == null) {
            return;
        }

        if (npcObjectId != 0) {
            L1Object obj = L1World.getInstance().findObject(npcObjectId);
            if (obj != null) {
                if (obj instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    int difflocx = Math.abs(pc.getX() - npc.getX());
                    int difflocy = Math.abs(pc.getY() - npc.getY());

                    if (difflocx > 3 || difflocy > 3) {
                        return;
                    }
                }
            }

            L1PcInventory pcInventory = pc.getInventory();
            L1ItemInstance item = pcInventory.getItem(itemObjectId);

            int cost = item.getDurability() * 200;

            if (!pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
                return;
            }

            item.setDurability(0);
            pcInventory.updateItem(item, L1PcInventory.COL_DURABILITY);
        } else {
            int petCost = 0;
            Collection<L1NpcInstance> petList = pc.getPetList().values();

            for (L1NpcInstance pet : petList) {
                petCost += pet.getPetCost();
            }

            int charisma = pc.getAbility().getTotalCha();

            if (pc.isCrown())
                charisma += 6;
            else if (pc.isElf())
                charisma += 12;
            else if (pc.isWizard())
                charisma += 6;
            else if (pc.isDarkElf())
                charisma += 6;
            else if (pc.isDragonKnight())
                charisma += 6;
            else if (pc.isIllusionist())
                charisma += 6;

            int petCount = (charisma - petCost) / 6;

            if (petCount <= 0) {
                pc.sendPackets(new S_ServerMessage(489));
                return;
            }

            L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);

            if (l1pet != null) {
                L1Npc npcTemp = NpcTable.getInstance().getTemplate(l1pet.getNpcId());
                L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
                pet.setPetCost(6);
                pet.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
            }
        }
    }
}
