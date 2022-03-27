package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ElfIcon;
import ks.packets.serverpackets.S_SPMR;

public class L1SkillClearMind extends L1SkillAdapter {
    public L1SkillClearMind(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        cha.getAbility().addAddedWis(3 * type);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.getPcExpManager().resetMr();
            pc.sendPackets(new S_SPMR(pc));
        }
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character target = request.getTargetCharacter();

        statUp(target, 1);

        target.sendPackets(new S_ElfIcon(0, request.getDuration() / 16, 0, 0));
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        statUp(targetCharacter, -1);
        targetCharacter.sendPackets(new S_ElfIcon(0, 0, 0, 0));
    }
}
