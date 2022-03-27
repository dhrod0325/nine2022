package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_HPUpdate;
import ks.packets.serverpackets.S_MPUpdate;
import ks.packets.serverpackets.S_SPMR;

import static ks.constants.L1SkillId.*;

public class L1SkillLuck extends L1SkillAdapter {
    public L1SkillLuck(int skillId) {
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

            switch (skillId) {
                case STATUS_LUCK_A: {// 운세버프 (매우 좋은)
                    pc.addDmgUp(2 * type);
                    pc.addHitUp(2 * type);
                    pc.addBowHitup(2 * type);
                    pc.addBowDmgUp(2 * type);
                    pc.getAbility().addSp(2 * type);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.addHpr(3 * type);
                    pc.addMaxHp(50 * type);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));

                    if (pc.isInParty()) {
                        pc.getParty().updateMiniHP(pc);
                    }

                    pc.addMpr(3 * type);
                    pc.addMaxMp(30 * type);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                    pc.sendPackets("쪽지버프(A) : HP +50, MP +30, 근거리 명중 +2,원거리 명중 +2,근거리 대미지 +2,원거리 대미지 +2,대미지리덕션 +3,SP +2");
                    break;
                }
                case STATUS_LUCK_B: {// 운세버프 (좋은)
                    buffStatusC(type, pc);
                    pc.addHitUp(2 * type);
                    pc.addBowHitup(2 * type);

                    pc.getAbility().addSp(type);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets("쪽지버프(B) : HP +50, MP +30, 근거리 명중 +2,원거리 명중 +2,대미지리덕션 +2,SP +1");
                    break;
                }
                case STATUS_LUCK_C: {// 운세버프 (보통)
                    buffStatusC(type, pc);
                    pc.getAC().addAc(-2 * type);
                    pc.sendPackets("쪽지버프(C) : HP +50,MP +30,AC -2");
                    break;
                }

                case STATUS_LUCK_D: {// 운세버프 (나쁜)
                    pc.getAC().addAc(-1 * type);
                    pc.sendPackets("쪽지버프(D) : AC -1");
                    break;
                }
            }
        }
    }

    private void buffStatusC(int type, L1PcInstance pc) {
        pc.addMaxHp(50 * type);
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        if (pc.isInParty()) {
            pc.getParty().updateMiniHP(pc);
        }
        pc.addMaxMp(30 * type);
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    }
}
