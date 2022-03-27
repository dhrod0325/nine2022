package ks.packets.clientpackets;

import ks.commands.gm.command.executor.L1UserCalc;
import ks.core.network.L1Client;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.S_WhoAmount;
import ks.packets.serverpackets.S_WhoCharInfo;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1CommonUtils;

public class C_Who extends ClientBasePacket {
    public C_Who(byte[] decrypt, L1Client client) {
        super(decrypt);
        String s = readS();

        L1PcInstance find = L1World.getInstance().getPlayer(s);
        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1RobotInstance robot = L1World.getInstance().getRobot(s);

        if (robot != null) {
            String msg = L1CommonUtils.getWhoCharInfo(robot);
            pc.sendPackets(new S_SystemMessage(msg));
            return;
        }

        if (find != null && (pc.isGm() || !find.isGm())) {
            pc.sendPackets(new S_WhoCharInfo(find));
        } else {
            int addUser = L1World.getInstance().getAllPlayers().size();
            int calcUser = L1UserCalc.getCalcUser();

            addUser += calcUser;

            String amount = String.valueOf(addUser + L1World.getInstance().getRobotPlayers().size());

            pc.sendPackets(new S_WhoAmount(amount));
        }
    }
}
