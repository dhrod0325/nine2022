package ks.model.skill.magic.skills;

import ks.model.L1BringStonePer;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class L1SkillBringStone extends L1SkillAdapter {

    public L1SkillBringStone(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            L1ItemInstance item = pc.getInventory().getItem(request.getTargetId());

            if (item != null) {
                L1BringStonePer per = L1CommonUtils.calcBringStonePer(pc.getLevel(), pc.getAbility().getTotalWis());
                int dark = per.getDark();
                int brave = per.getBrave();
                int wise = per.getWise();
                int kaiser = per.getKayser();
                int chance = RandomUtils.nextInt(100) + 1;

                //흑마석
                if (item.getItem().getItemId() == 40320) {
                    pc.getInventory().removeItem(item, 1);
                    if (dark >= chance) {
                        pc.getInventory().storeItem(40321, 1);
                        pc.sendPackets(new S_ServerMessage(403, "$2475"));
                    } else {
                        pc.sendPackets(new S_ServerMessage(280));
                    }
                }
                //흑요석
                else if (item.getItem().getItemId() == 40321) {
                    pc.getInventory().removeItem(item, 1);
                    if (brave >= chance) {
                        pc.getInventory().storeItem(40322, 1);
                        pc.sendPackets(new S_ServerMessage(403, "$2476"));
                    } else {
                        pc.sendPackets(new S_ServerMessage(280));
                    }
                }
                //강암석
                else if (item.getItem().getItemId() == 40322) {
                    pc.getInventory().removeItem(item, 1);
                    if (wise >= chance) {
                        pc.getInventory().storeItem(40323, 1);
                        pc.sendPackets(new S_ServerMessage(403, "$2477"));
                    } else {
                        pc.sendPackets(new S_ServerMessage(280));
                    }
                }
                //현암석
                else if (item.getItem().getItemId() == 40323) {
                    pc.getInventory().removeItem(item, 1);
                    if (kaiser >= chance) {
                        pc.getInventory().storeItem(40324, 1);
                        pc.sendPackets(new S_ServerMessage(403, "$2478"));
                    } else {
                        pc.sendPackets(new S_ServerMessage(280));
                    }
                }
            }
        }
    }
}
