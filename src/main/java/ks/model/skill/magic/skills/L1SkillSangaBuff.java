package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SPMR;

public class L1SkillSangaBuff extends L1SkillAdapter {

    public L1SkillSangaBuff(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addHitUp(5 * type);
            pc.addDmgUp(5 * type);
            pc.addBowHitup(5 * type);
            pc.addBowDmgUp(5 * type);
            pc.getAbility().addAddedStr(2 * type);
            pc.getAbility().addAddedDex(2 * type);
            pc.getAbility().addAddedInt(2 * type);
            pc.getAbility().addAddedCon(2 * type);
            pc.getAbility().addAddedWis(2 * type);
            pc.getAbility().addSp(type);
            pc.sendPackets(new S_SPMR(pc));
        }
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
}
