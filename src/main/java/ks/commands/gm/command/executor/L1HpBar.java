package ks.commands.gm.command.executor;

import ks.constants.L1SkillId;
import ks.model.L1Object;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_HPMeter;
import ks.packets.serverpackets.S_SystemMessage;

public class L1HpBar implements L1CommandExecutor {
    private L1HpBar() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1HpBar();
    }

    public static boolean isHpBarTarget(L1Object obj) {
        if (obj instanceof L1MonsterInstance) {
            return true;
        }

        if (obj instanceof L1PcInstance) {
            return true;
        }

        if (obj instanceof L1SummonInstance) {
            return true;
        }

        return obj instanceof L1PetInstance;
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        if (arg.equalsIgnoreCase("켬")) {
            pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_GM_HPBAR, 0);
        } else if (arg.equalsIgnoreCase("끔")) {
            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_GM_HPBAR);

            for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
                if (isHpBarTarget(obj)) {
                    pc.sendPackets(new S_HPMeter(obj.getId(), 0xFF));
                }
            }
        } else {
            pc.sendPackets(new S_SystemMessage(cmdName + " [켬,끔] 라고 입력해 주세요. "));
        }
    }
}