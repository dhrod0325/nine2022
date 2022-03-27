package ks.model.skill.magic.skills;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.magic.L1MagicRun;
import ks.model.attack.magic.impl.action.vo.L1MagicActionVo;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

public class L1SkillManaDrain extends L1SkillAdapter {
    public L1SkillManaDrain(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character skillUseCharacter = request.getSkillUseCharacter();
        L1Character cha = request.getTargetCharacter();

        int totalInt = skillUseCharacter.getAbility().getTotalInt();
        int drainMana = RandomUtils.nextInt(totalInt / 2, totalInt);

        drainMana -= drainMana * L1MagicUtils.reduceDamageByMr(cha.getResistance().getEffectedMrBySkill());

        if (cha.getCurrentMp() < drainMana) {
            drainMana = cha.getCurrentMp();
        }

        L1MagicRun magic = new L1MagicRun(skillUseCharacter, cha);
        magic.commit(new L1MagicActionVo(0, drainMana));

        skillUseCharacter.sendPackets(new S_SkillSound(skillUseCharacter.getId(), 2171));
        Broadcaster.broadcastPacket(skillUseCharacter, new S_SkillSound(skillUseCharacter.getId(), 2171));
    }
}
