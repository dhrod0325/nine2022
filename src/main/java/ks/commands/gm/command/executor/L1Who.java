package ks.commands.gm.command.executor;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.S_WhoAmount;
import ks.system.robot.is.L1RobotInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class L1Who implements L1CommandExecutor {
    private L1Who() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Who();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int noUserCount = L1UserCalc.getCalcUser();

            WhoResult whoResult = getWhoResult();

            List<String> robotList = whoResult.robotList;
            List<String> gmList = whoResult.gmList;
            List<String> playList = whoResult.playList;

            pc.sendPackets(new S_WhoAmount(gmList.size() + robotList.size() + ""));
            pc.sendPackets(new S_SystemMessage("로봇 : " + robotList.size()));
            pc.sendPackets(new S_SystemMessage("뻥튀기 : " + noUserCount));

            if (arg.equalsIgnoreCase("전체")) {
                if (gmList.size() > 0) {
                    pc.sendPackets(new S_SystemMessage("-- 운영자 (" + gmList.size() + "명)"));
                    pc.sendPackets(new S_SystemMessage(String.join(",", gmList)));
                }

                if (playList.size() > 0) {
                    pc.sendPackets(new S_SystemMessage("-- 플레이어 (" + playList.size() + "명)"));
                    pc.sendPackets(new S_SystemMessage(String.join(",", playList)));
                }

                if (robotList.size() > 0) {
                    pc.sendPackets(new S_SystemMessage("-- 로봇유저 (" + robotList.size() + "명)"));
                    pc.sendPackets(new S_SystemMessage(String.join(",", robotList)));
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".누구 [전체] 라고 입력해 주세요. "));
        }
    }

    public static WhoResult getWhoResult() {
        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        List<String> robotList = new ArrayList<>();
        List<String> gmList = new ArrayList<>();
        List<String> playList = new ArrayList<>();

        for (L1PcInstance player : players) {
            if (player.isGm()) {
                gmList.add(player.getName());
                continue;
            }

            playList.add(player.getName());
        }

        for (L1RobotInstance robot : L1World.getInstance().getRobotPlayers()) {
            robotList.add(robot.getName());
        }

        return new WhoResult(robotList, gmList, playList);
    }

    public static class WhoResult {
        public List<String> robotList;
        public List<String> gmList;
        public List<String> playList;

        public WhoResult(List<String> robotList, List<String> gmList, List<String> playList) {
            this.robotList = robotList;
            this.gmList = gmList;
            this.playList = playList;
        }
    }
}
