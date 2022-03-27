package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.S_Weather;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1ChangeWeather implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1ChangeWeather();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            int weather = Integer.parseInt(tok.nextToken());
            L1World.getInstance().setWeather(weather);
            L1World.getInstance().broadcastPacketToAll(new S_Weather(weather));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 0~3(눈), 16~19(비)라고 입력 해주세요."));
        }
    }
}
