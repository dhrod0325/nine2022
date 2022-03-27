package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SkillIconWindShackle;
import ks.packets.serverpackets.S_SkillSound;

public class L1SkillWindShackle extends L1SkillAdapter {
    public L1SkillWindShackle(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();
        int duration = request.getDuration();

        targetCharacter.sendPackets(new S_SkillIconWindShackle(targetCharacter.getId(), duration));
        targetCharacter.sendPackets(new S_PacketBox(180, 52, true));

        targetCharacter.sendPackets(new S_SkillSound(targetCharacter.getId(), 1799));
        Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 1799));
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        targetCharacter.sendPackets(new S_SkillIconWindShackle(targetCharacter.getId(), 0));
        targetCharacter.sendPackets(new S_PacketBox(180, 52, false));
    }

    @Override
    public void sendIcon(L1Character targetCharacter, int duration) {
        targetCharacter.sendPackets(new S_SkillIconWindShackle(targetCharacter.getId(), duration));
    }
}
