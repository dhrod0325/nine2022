package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_HPUpdate;
import ks.packets.serverpackets.S_MPUpdate;
import ks.packets.serverpackets.S_SPMR;

public class L1SkillBuffCray extends L1SkillAdapter {
    public L1SkillBuffCray(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
    }

    private void statUp(L1Character cha, int type) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addHitUp(5 * type);
            pc.addDmgUp(type);
            pc.addBowHitup(5 * type);
            pc.addBowDmgUp(type);

            pc.addMaxHp(100 * type);
            pc.addMaxMp(50 * type);
            pc.addHpr(3 * type);
            pc.addMpr(3 * type);

            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            pc.sendPackets(new S_SPMR(pc));
        }
    }
}
