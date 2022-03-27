package ks.commands.gm.command.executor;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1UserCalc implements L1CommandExecutor {
    private static int calcUser = 0;

    private L1UserCalc() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1UserCalc();
    }

    public static int getCalcUser() {
        return calcUser;
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        String msg = null;

        try {
            StringTokenizer tok = new StringTokenizer(arg);
            String type = tok.nextToken();
            int count = Integer.parseInt(tok.nextToken());

            if (type.equalsIgnoreCase("+")) {
                calcUser += count;
                msg = "뻥튀기 : " + count + "명 추가 / 현재 뻥튀기 : " + calcUser + "명";
            } else if (type.equalsIgnoreCase("-")) {
                int temp = calcUser - count;
                if (temp < 0) {
                    pc.sendPackets(new S_SystemMessage("뻥튀기가 -가 될수는 없습니다. 현재 뻥튀기 : " + calcUser));
                } else {
                    calcUser = temp;
                    msg = "뻥튀기 : " + count + "명 감소 / 현재 뻥튀기 : " + calcUser + "명";
                }
            }
        } catch (Exception e) {
            msg = cmdName + " [+,-] [COUNT] 입력";
        } finally {
            if (msg != null) {
                pc.sendPackets(new S_SystemMessage(msg));
            }
        }
    }
}
