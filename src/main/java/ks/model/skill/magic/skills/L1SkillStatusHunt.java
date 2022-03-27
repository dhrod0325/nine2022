package ks.model.skill.magic.skills;

import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.*;

public class L1SkillStatusHunt extends L1SkillAdapter {
    public L1SkillStatusHunt(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.addDmgUp(3 * type);
        cha.addBowDmgUp(3 * type);
        cha.getAbility().addSp(3 * type);
        cha.getAbility().addAddedReduction(type);

        cha.sendPackets(new S_ChangeName(cha));
        Broadcaster.broadcastPacket(cha, new S_ChangeName(cha));

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            pc.getPetList().forEach((integer, pet) -> {
                if (pet instanceof L1SummonInstance) {
                    pc.sendPackets(new S_SummonPack((L1SummonInstance) pet, pc));
                }

                if (pet instanceof L1PetInstance) {
                    pc.sendPackets(new S_PetPack((L1PetInstance) pet, pc));
                }
            });
        }
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        statUp(targetCharacter, 1);

        if (L1Opcodes.SERVER_VERSION == 3.1) {
            targetCharacter.sendPackets(new S_IconFromEffectList(targetCharacter.getId(), 255));
        } else if (L1Opcodes.SERVER_VERSION == 3.8) {
            targetCharacter.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 473, true));
        }

        targetCharacter.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_HUNT, 0);
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);

        if (L1Opcodes.SERVER_VERSION == 3.1) {
            cha.sendPackets(new S_IconFromEffectList(cha.getId(), 262));
        } else if (L1Opcodes.SERVER_VERSION == 3.8) {
            cha.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, 473, false));
        }
    }
}
