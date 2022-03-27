package ks.model.skill.magic.skills;

import ks.core.datatables.npc.NpcTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.L1Npc;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ChatPacket;
import ks.util.common.random.RandomUtils;

import java.util.Collection;

import static ks.constants.L1SkillId.SUMMON_LESSER_ELEMENTAL;

public class L1SkillSummonElemental extends L1SkillAdapter {

    public L1SkillSummonElemental(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            int attr = pc.getElfAttr();

            if (attr != 0) {
                if (pc.getMap().isRecallPets() || pc.isGm()) {
                    int petCost = 0;

                    Collection<L1NpcInstance> petList = pc.getPetList().values();

                    for (L1NpcInstance pet : petList) {
                        petCost += pet.getPetCost();
                    }

                    if (petCost == 0) {
                        int summonId = 0;
                        int[] summons;

                        if (skillId == SUMMON_LESSER_ELEMENTAL) {
                            summons = new int[]{45306, 45303, 45304, 45305};
                        } else {
                            summons = new int[]{81053, 81050, 81051, 81052};
                        }

                        int npcAttr = 1;

                        for (int i = 0; i < summons.length; i++) {
                            if (npcAttr == attr) {
                                summonId = summons[i];
                                i = summons.length;
                            }

                            npcAttr *= 2;
                        }
                        if (summonId == 0) {
                            int k3 = RandomUtils.nextInt(4);
                            summonId = summons[k3];
                        }

                        L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonId);
                        L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                        summon.setPetCost(pc.getAbility().getTotalCha() + 7);
                    }
                } else {
                    pc.sendPackets(new S_ChatPacket(pc, "아무일도 일어나지 않았습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                }
            } else {
                pc.sendPackets(new S_ChatPacket(pc, "아무일도 일어나지 않았습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            }
        }
    }
}
