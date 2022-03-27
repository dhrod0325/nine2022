package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.robot.is.L1RobotInstance;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1AutoPc implements L1CommandExecutor {
    private static final L1AutoPc instance = new L1AutoPc();

    private L1AutoPc() {
    }

    public static L1CommandExecutor getInstance() {
        return instance;
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            int type = Integer.parseInt(tok.nextToken());
            String pcName = tok.nextToken();

            switch (type) {
                case 0:
                    for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
                        if (robot.getName().equalsIgnoreCase(pcName)) {
                            robot.save();
                            robot.logout();
                            break;
                        }
                    }
                    break;
                case 1:
                    for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
                        robot.logout();
                    }
                    break;
                case 2:
                    for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
                        if (robot.getHighLevel() == 45 || robot.getHighLevel() == 25 || robot.getHighLevel() == 1) {
                            robot.logout();
                        }
                    }
                    break;
                case 3:
                    for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
                        if (robot.getHighLevel() == 56) {
                            robot.logout();
                        }
                    }

                    break;
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [0:끔/1:전체끔/2:마을끔/3:사냥끔] [로봇아이디] 라고 입력해 주세요. "));
        }
    }
}
