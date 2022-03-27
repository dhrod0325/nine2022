package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_HPUpdate;
import ks.packets.serverpackets.S_MPUpdate;

public class L1SkillAdvanceSpirit extends L1SkillAdapter {

    public L1SkillAdvanceSpirit(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.setAdvenHp(pc.getBaseMaxHp() / 5);
            pc.setAdvenMp(pc.getBaseMaxMp() / 5);
            pc.addMaxHp(pc.getAdvenHp());
            pc.addMaxMp(pc.getAdvenMp());
            
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));

            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }
        }
    }

    @Override
    public void stopSkill(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.addMaxHp(-pc.getAdvenHp());
            pc.addMaxMp(-pc.getAdvenMp());
            pc.setAdvenHp(0);
            pc.setAdvenMp(0);

            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));

            if (pc.isInParty()) {
                pc.getParty().updateMiniHP(pc);
            }

            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
    }
}
