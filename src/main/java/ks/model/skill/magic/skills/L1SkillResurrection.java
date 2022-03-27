package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1TowerInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;

import static ks.constants.L1SkillId.RESURRECTION;

public class L1SkillResurrection extends L1SkillAdapter {
    public L1SkillResurrection(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;

            if (skillUseCharacter.getId() != pc.getId()) {
                if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
                    for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(pc, 0)) {
                        if (!visiblePc.isDead()) {
                            skillUseCharacter.sendPackets(new S_ServerMessage(592));
                            setRunSkillState(STATUS_RETURN);
                            return;
                        }
                    }
                }

                if (pc.getCurrentHp() == 0 && pc.isDead()) {
                    if (pc.isUseResurrection()) {
                        pc.setGres(request.getSkillId() != RESURRECTION);
                        pc.setTempID(skillUseCharacter.getId());
                        pc.sendPackets(new S_Message_YN(322, ""));
                    }
                }
            }
        } else if (targetCharacter instanceof L1NpcInstance) {
            if (!(targetCharacter instanceof L1TowerInstance)) {
                if (targetCharacter instanceof L1PetInstance) {
                    if (targetCharacter.isOverlapLocation()) {
                        setRunSkillState(STATUS_RETURN);
                        return;
                    }

                    if (targetCharacter.getCurrentHp() == 0 && targetCharacter.isDead()) {
                        targetCharacter.resurrect(targetCharacter.getMaxHp() / 4);
                    }
                }
            }
        }
    }
}
