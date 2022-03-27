package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;

public class L1SkillBlessWeapon extends L1SkillAdapter {
    public L1SkillBlessWeapon(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (pc.getWeapon() == null || pc.isLongAttack()) {
                setRunSkillState(STATUS_RETURN);
                return;
            }

            pc.sendPackets(new S_ServerMessage(161, String.valueOf(pc.getWeapon().getLogName()).trim(), "$245", "$247"));
            pc.getWeapon().setSkillWeaponEnchant(pc, skillId, request.getDuration() * 1000);
        }
    }
}
