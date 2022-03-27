package ks.commands.gm.command.executor;

import ks.model.L1PolyMorph;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1StatusUtils;

import java.util.StringTokenizer;

import static ks.constants.L1SkillId.*;

@SuppressWarnings("unused")
public class L1AllBuff implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1AllBuff();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        Integer[] allBuffSkill = {
                DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR,
                BLESS_WEAPON, BERSERKERS, IMMUNE_TO_HARM,
                ADVANCE_SPIRIT, REDUCTION_ARMOR, BOUNCE_ATTACK,
                SOLID_CARRIAGE, ENCHANT_VENOM, BURNING_SPIRIT,
                VENOM_RESIST, DOUBLE_BRAKE, UNCANNY_DODGE,
                DRESS_EVASION, RESIST_MAGIC, CLEAR_MIND,
                ELEMENTAL_PROTECTION, AQUA_PROTECTER, BURNING_WEAPON,
                IRON_SKIN, EXOTIC_VITALIZE, WATER_LIFE,
                ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE
        };

        try {
            String name = pc.getName();

            StringTokenizer st = new StringTokenizer(arg);

            if (st.hasMoreTokens()) {
                name = st.nextToken();
            }

            L1PcInstance target = L1World.getInstance().getPlayer(name);

            if (target == null) {
                pc.sendPackets(new S_ServerMessage(73, name));
                return;
            }

            L1StatusUtils.haste(target, 3600 * 1000);
            L1StatusUtils.brave(target, 3600 * 1000);

            L1PolyMorph.doPoly(target, 5641, 7200, L1PolyMorph.MORPH_BY_GM);

            L1SkillUtils.skillByGm(target, allBuffSkill);

            pc.sendPackets(new S_SystemMessage(name + " 에게 올버프를 줬습니다. "));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".올버프 [캐릭터명]으로 입력해 주세요. "));
        }
    }
}
