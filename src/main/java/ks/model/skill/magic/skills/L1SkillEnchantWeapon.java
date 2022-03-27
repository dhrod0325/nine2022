package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;

public class L1SkillEnchantWeapon extends L1SkillAdapter {
    public L1SkillEnchantWeapon(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance item = pc.getInventory().getItem(request.getTargetId());

            if (item != null && item.getItem().getType2() == 1) {
                pc.sendPackets(new S_ServerMessage(161, String.valueOf(item.getLogName()).trim(), "$245", "$247"));
                item.setSkillWeaponEnchant(pc, skillId, request.getDuration() * 1000);
            }
        }
    }
}
