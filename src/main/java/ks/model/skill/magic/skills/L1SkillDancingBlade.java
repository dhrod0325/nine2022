package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillBrave;
import ks.packets.serverpackets.S_SkillIconAura;

public class L1SkillDancingBlade extends L1SkillAdapter {
    public L1SkillDancingBlade(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();
        int duration = request.getDuration();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance w = pc.getEquipSlot().getWeapon();

            if (w != null) {
                if (w.getItem().getType1() == 4 || w.getItem().getType1() == 46) {
                    pc.getMoveState().setBraveSpeed(1);
                    sendIcon(pc, duration);
                }
            }
        }
    }

    @Override
    public void stopSkill(L1Character cha) {
        cha.getMoveState().setBraveSpeed(0);
        cha.sendPackets(new S_SkillBrave(cha.getId(), 0, 0));
        cha.sendPackets(new S_SkillIconAura(154, 0));
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_SkillBrave(cha.getId(), 1, duration));
        cha.sendPackets(new S_SkillIconAura(154, duration));
    }
}
