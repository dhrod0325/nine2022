package ks.model.skill.magic.skills;

import ks.core.datatables.npc.NpcTable;
import ks.model.L1Character;
import ks.model.L1Npc;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_ServerMessage;

import java.util.Collection;

public class L1SkillSummonMonster extends L1SkillAdapter {
    public L1SkillSummonMonster(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            int level = pc.getLevel();
            int[] summons;
            if (pc.getMap().isRecallPets() || pc.isGm()) {
                if (pc.getInventory().checkEquipped(20284) || pc.getInventory().checkEquipped(120284)) {
                    L1SkillUtils.summonMonster(pc, request.getTargetX(), request.getTargetY());
                } else {
                    summons = new int[]{81083, 81084, 81085, 81086, 81087, 81088, 81089};

                    int summonId = 0;
                    int summonCost = 6;
                    int levelRange = 32;

                    for (int i = 0; i < summons.length; i++) {
                        if (level < levelRange || i == summons.length - 1) {
                            summonId = summons[i];
                            break;
                        }

                        levelRange += 4;
                    }

                    int petCost = 0;

                    Collection<L1NpcInstance> petList = pc.getPetList().values();

                    for (L1NpcInstance pet : petList) {
                        petCost += pet.getPetCost();
                    }

                    int charisma = pc.getAbility().getTotalCha() + 6 - petCost;
                    int summonCount = charisma / summonCost;

                    L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonId);

                    for (int i = 0; i < summonCount; i++) {
                        L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                        summon.setPetCost(summonCost);
                    }
                }
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        }
    }
}
