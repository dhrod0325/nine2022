package ks.commands.gm.command.executor;

import ks.app.LineageAppContext;
import ks.model.Broadcaster;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Burf implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1Burf();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int sprid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            Burfskill spr = new Burfskill(pc, sprid, count);

            LineageAppContext.skillTaskScheduler().execute(spr);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [castgfx] 라고 입력해 주세요. "));
        }
    }

    static class Burfskill implements Runnable {
        private final L1PcInstance pc;
        private final int sprid;
        private final int count;

        public Burfskill(L1PcInstance pc, int sprid, int count) {
            this.pc = pc;
            this.sprid = sprid;
            this.count = count;
        }

        public void run() {
            for (int i = 0; i < count; i++) {
                try {
                    Thread.sleep(500);
                    int num = sprid + i;
                    pc.sendPackets(new S_SystemMessage("스킬번호: " + num + ""));
                    pc.sendPackets(new S_SkillSound(pc.getId(), sprid + i));
                    Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), sprid + i));
                } catch (Exception exception) {
                    break;
                }
            }
        }
    }
}
