package ks.commands.gm.command.executor;

import ks.core.datatables.SkillsTable;
import ks.model.Broadcaster;
import ks.model.L1Skills;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_AddSkill;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1AddSkill implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1AddSkill();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);

            L1PcInstance target = null;

            if (st.hasMoreTokens()) {
                String userName = st.nextToken();
                target = L1World.getInstance().getPlayer(userName);
            } else {
                target = pc;
            }

            if (target == null) {
                return;
            }

            int objectId = target.getId();

            target.sendPackets(new S_SkillSound(objectId, '\343'));
            Broadcaster.broadcastPacket(target, new S_SkillSound(objectId, '\343'));

            if (target.isCrown()) {
                target.sendPackets(new S_AddSkill(
                        255,
                        255,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0, 0, 0, 0,
                        255,
                        255, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0));

                for (int cnt = 1; cnt <= 16; cnt++) {
                    skillMaster(target, cnt);
                }

                for (int cnt = 113; cnt <= 120; cnt++) {
                    skillMaster(target, cnt);
                }
            } else if (target.isKnight()) {
                target.sendPackets(new S_AddSkill(255, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 192, 7, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0));

                for (int cnt = 1; cnt <= 8; cnt++) {
                    skillMaster(target, cnt);
                }

                for (int cnt = 87; cnt <= 91; cnt++) {
                    skillMaster(target, cnt);
                }
            } else if (target.isElf()) {
                target.sendPackets(new S_AddSkill(255, 255, 127, 255, 255, 255, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 127, 3, 255, 255, 255, 255,
                        0, 0, 0, 0, 0, 0));

                for (int cnt = 1; cnt <= 48; cnt++) {
                    skillMaster(target, cnt);
                }
                for (int cnt = 129; cnt <= 176; cnt++) {
                    skillMaster(target, cnt);
                }
            } else if (target.isWizard()) {
                target.sendPackets(new S_AddSkill(255, 255, 127, 255, 255, 255,
                        255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0));
                for (int cnt = 1; cnt <= 80; cnt++) {
                    skillMaster(target, cnt);
                }
            } else if (target.isDarkElf()) {
                target.sendPackets(new S_AddSkill(
                        255, 255,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0,
                        255, 127,
                        0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0,
                        0, 0, 0));

                for (int cnt = 1; cnt <= 16; cnt++) {
                    skillMaster(target, cnt);
                }

                for (int cnt = 97; cnt <= 111; cnt++) {
                    skillMaster(target, cnt);
                }
            } else if (pc.isDragonKnight()) {
                target.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255,
                        255, 255, 0, 0, 0));

                for (int cnt = 177; cnt <= 200; cnt++) {
                    skillMaster(target, cnt);
                }
            } else if (pc.isIllusionist()) {
                target.sendPackets(new S_AddSkill(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0,
                        0, 0,
                        0, 0,
                        0, 0, 0, 0, 0, 0,
                        0, 0, 0,
                        255, 255, 255));

                for (int cnt = 201; cnt <= 224; cnt++) {
                    skillMaster(target, cnt);
                }
            }

            target.sendPackets("모든 스킬을 배웠습니다.");
            pc.sendPackets(target.getName() + "이 모든 스킬을 배웠습니다");
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 커멘드 에러"));
        }
    }

    private void skillMaster(L1PcInstance target, int cnt) {
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(cnt);
        String skillName = l1skills.getName();
        int skillId = l1skills.getSkillId();
        SkillsTable.getInstance().spellMastery(target.getId(), skillId, skillName, 0, 0); // DB에 등록
    }
}
