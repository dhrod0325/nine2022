package ks.commands.gm.command.executor;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1SkillSound implements L1CommandExecutor {
    private static final Logger logger = LogManager.getLogger(L1SkillSound.class);

    public static L1CommandExecutor getInstance() {
        return new L1SkillSound();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int sprid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            SkillSound spr = new SkillSound(pc, sprid, count);

            LineageAppContext.skillTaskScheduler().execute(spr);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [sprId, 갯수] 라고 입력해 주세요. "));
        }
    }

    static class SkillSound implements Runnable {
        private final int sprId;
        private final int count;
        private final L1PcInstance pc;

        public SkillSound(L1PcInstance pc, int sprId, int count) {
            this.pc = pc;
            this.sprId = sprId;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                try {

                    Thread.sleep(500);

                    int num = sprId + i;
                    pc.sendPackets(new S_SystemMessage("사운드번호: " + num + ""));
                    pc.sendPackets(new S_SkillSound(pc.getId(), sprId + i));
                } catch (Exception exception) {
                    break;
                }
            }
        }
    }
}
