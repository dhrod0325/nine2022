package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_HPUpdate;
import ks.packets.serverpackets.S_MPUpdate;
import ks.packets.serverpackets.S_SPMR;

import static ks.constants.L1SkillId.*;

public class L1SkillCashScroll extends L1SkillAdapter {
    public L1SkillCashScroll(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        switch (skillId) {
            case STATUS_CASHSCROLL1: {
                L1PcInstance pc = (L1PcInstance) cha;

                pc.addHpr(4 * type);
                pc.addMaxHp(50 * type);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));

                if (pc.isInParty()) {
                    pc.getParty().updateMiniHP(pc);
                }
            }
            break;
            case STATUS_CASHSCROLL2: {
                L1PcInstance pc = (L1PcInstance) cha;

                pc.addMpr(4 * type);
                pc.addMaxMp(40 * type);
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            }
            break;
            case STATUS_CASHSCROLL3: {
                L1PcInstance pc = (L1PcInstance) cha;

                pc.addDmgUp(3 * type);
                pc.addHitUp(3 * type);
                pc.addBowHitup(3 * type);
                pc.addBowDmgUp(3 * type);
                pc.getAbility().addSp(3 * type);
                pc.sendPackets(new S_SPMR(pc));
            }
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
