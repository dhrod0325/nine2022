package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_TrueTarget;

public class L1SkillTrueTarget extends L1SkillAdapter {
    public L1SkillTrueTarget(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        int targetId = request.getTargetId();
        int targetX = request.getTargetX();
        int targetY = request.getTargetY();

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pri = (L1PcInstance) skillUseCharacter;
            pri.sendPackets(new S_TrueTarget(targetId, targetX, targetY));

            for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(targetCharacter)) {
                if (pri.getClanId() == pc.getClanId()) {
                    pc.sendPackets(new S_TrueTarget(targetId, targetX, targetY));
                }
            }

            if (targetCharacter instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) targetCharacter;
                targetPc.setTrueTargetLeaderId(skillUseCharacter.getId());

                if (pri.getClanId() == targetPc.getClanId()) {
                    targetPc.sendPackets(new S_TrueTarget(targetId, targetX, targetY));
                }
            }
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.setTrueTargetLeaderId(0);
        }
    }
}
