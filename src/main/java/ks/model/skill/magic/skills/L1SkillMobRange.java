package ks.model.skill.magic.skills;

import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillMobRange extends L1SkillAdapter {
    public L1SkillMobRange(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character targetCharacter = request.getTargetCharacter();

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        if (skill.getCastGfx2() > 0) {
            Broadcaster.broadcastPacket(skillUseCharacter, new S_SkillSound(targetCharacter.getId(), skill.getCastGfx2()));
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        super.stopSkill(targetCharacter);

        switch (skillId) {
            case L1SkillId.MOB_RANGE_ERASE_MAGIC: {
                targetCharacter.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_AURA));
                break;
            }
        }
    }
}
