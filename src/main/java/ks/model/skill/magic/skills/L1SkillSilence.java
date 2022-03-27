package ks.model.skill.magic.skills;

import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.COUNTER_MAGIC;

public class L1SkillSilence extends L1SkillAdapter {
    public L1SkillSilence(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        L1Character targetCharacter = request.getTargetCharacter();
        L1Character skillUseCharacter = request.getSkillUseCharacter();

        if (targetCharacter.getId() == skillUseCharacter.getId()) {
            return;
        }

        run(targetCharacter);
    }

    @Override
    public void preSkill(L1SkillRequest request) {
        super.preSkill(request);

        run(request.getSkillUseCharacter());
    }

    private void run(L1Character targetCharacter) {
        int pcTime = getSkill().getBuffDuration();

        if (targetCharacter.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
            targetCharacter.getSkillEffectTimerSet().removeSkillEffect(COUNTER_MAGIC);
            Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 10702));
            targetCharacter.sendPackets(new S_SkillSound(targetCharacter.getId(), 10702));

            pcTime /= 2;
        }

        logger.debug("pcTime : {}, name : {}", pcTime, targetCharacter.getName());

        targetCharacter.getSkillEffectTimerSet().setSkillEffect(L1SkillId.AREA_OF_SILENCE, pcTime * 1000);
        targetCharacter.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_CHATBAN, pcTime));
        targetCharacter.sendPackets(new S_SkillSound(targetCharacter.getId(), 2241));
        Broadcaster.broadcastPacket(targetCharacter, new S_SkillSound(targetCharacter.getId(), 2241));

        setRunSkillState(STATUS_CONTINUE);
    }
}
