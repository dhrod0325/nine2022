package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_EffectLocation;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillFouSlayer extends L1SkillAdapter {
    public L1SkillFouSlayer(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            L1ItemInstance weapon = skillUseCharacter.getWeapon();

            if (weapon != null) {
                for (int i = 0; i < 3; i++) {
                    targetCharacter.onAction(pc);
                }

                pc.sendPackets(new S_EffectLocation(targetCharacter.getX(), targetCharacter.getY(), 6509));
                Broadcaster.broadcastPacket(pc, new S_EffectLocation(targetCharacter.getX(), targetCharacter.getY(), 6509));

                pc.sendPackets(new S_SkillSound(pc.getId(), 7020));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7020));
            }
        }
    }
}
