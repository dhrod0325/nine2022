package ks.commands.gm.command.executor;

import ks.core.datatables.SkillsTable;
import ks.model.L1Skills;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Buff implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1Buff();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);

            Collection<L1PcInstance> players;

            String target = tok.nextToken();

            if (target.equals("나")) {
                players = new ArrayList<>();
                players.add(pc);
                target = tok.nextToken();
            } else if (target.equals("전체")) {
                players = L1World.getInstance().getAllPlayers();
                target = tok.nextToken();
            } else {
                players = L1World.getInstance().getVisiblePlayer(pc);
            }

            int skillId = Integer.parseInt(target);

            int time = 0;

            if (tok.hasMoreTokens()) {
                time = Integer.parseInt(tok.nextToken());
            }

            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

            if (skill.getTarget().equals("buff")) {
                for (L1PcInstance tg : players) {
                    new L1SkillUse(pc, skillId, tg.getId(), tg.getX(), tg.getY(), time, L1SkillUse.TYPE_SPELL_SC).run();
                }
            } else if (skill.getTarget().equals("none")) {
                for (L1PcInstance tg : players) {
                    new L1SkillUse(tg, skillId, tg.getId(), tg.getX(), tg.getY(), time, L1SkillUse.TYPE_GM_BUFF).run();
                }
            } else {
                pc.sendPackets(new S_SystemMessage("버프계의 스킬이 아닙니다. "));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [전체, 나] [스킬아이디] [시간] 라고 입력해 주세요. "));
        }
    }
}
