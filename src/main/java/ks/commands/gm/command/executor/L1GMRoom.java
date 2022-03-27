package ks.commands.gm.command.executor;

import ks.commands.gm.GMCommandsUtils;
import ks.core.datatables.MapsTable;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;

public class L1GMRoom implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1GMRoom();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int target = -1;

            try {
                target = Integer.parseInt(arg);
            } catch (NumberFormatException ignored) {
            }

            if (target == 0) {
                L1Teleport.teleport(pc, 32799, 33112, (short) 6202, 5, false); // 영자방
            } else if (target == 1) {
                L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, false); // 영자방
            } else if (target == 2) {
                L1Teleport.teleport(pc, 33052, 32339, (short) 4, 5, false); // 요정숲
            } else if (target == 3) {
                L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false); // 판도라
            } else if (target == 4) {
                L1Teleport.teleport(pc, 34055, 32290, (short) 4, 5, false); // 오렌
            } else if (target == 5) {
                L1Teleport.teleport(pc, 33429, 32814, (short) 4, 5, false); // 기란
            } else if (target == 6) {
                L1Teleport.teleport(pc, 33047, 32761, (short) 4, 5, false); // 켄말
            } else if (target == 7) {
                L1Teleport.teleport(pc, 32612, 33191, (short) 4, 5, false); // 윈다우드
            } else if (target == 8) {
                L1Teleport.teleport(pc, 33611, 33253, (short) 4, 5, false); // 하이네
            } else if (target == 9) {
                L1Teleport.teleport(pc, 33082, 33390, (short) 4, 5, false); // 은말
            } else if (target == 10) {
                L1Teleport.teleport(pc, 32572, 32944, (short) 0, 5, false); // 말섬
            } else if (target == 11) {
                L1Teleport.teleport(pc, 33964, 33254, (short) 4, 5, false); // 아덴
            } else if (target == 12) {
                L1Teleport.teleport(pc, 32635, 32818, (short) 303, 5, false); // 몽섬
            } else if (target == 13) {
                L1Teleport.teleport(pc, 32828, 32848, (short) 70, 5, false); // 잊섬
            } else if (target == 14) {
                L1Teleport.teleport(pc, 32736, 32787, (short) 15, 5, false); // 켄성
            } else if (target == 15) {
                L1Teleport.teleport(pc, 32735, 32788, (short) 29, 5, false); // 윈성
            } else if (target == 16) {
                L1Teleport.teleport(pc, 32730, 32802, (short) 52, 5, false); // 기란
            } else if (target == 17) {
                L1Teleport.teleport(pc, 32572, 32826, (short) 64, 5, false); // 하이네성
            } else if (target == 18) {
                L1Teleport.teleport(pc, 32895, 32533, (short) 300, 5, false); // 아덴성
            } else if (target == 19) {
                L1Teleport.teleport(pc, 33167, 32775, (short) 4, 5, false); // 켄성
            } else if (target == 20) {
                L1Teleport.teleport(pc, 32674, 33408, (short) 4, 5, false); // 윈성
            } else if (target == 21) {
                L1Teleport.teleport(pc, 33630, 32677, (short) 4, 5, false); // 기란
            } else if (target == 22) {
                L1Teleport.teleport(pc, 33524, 33394, (short) 4, 5, false); // 하이네
            } else if (target == 23) {
                L1Teleport.teleport(pc, 32424, 33068, (short) 440, 5, false); // 해적섬
            } else if (target == 24) {
                L1Teleport.teleport(pc, 32800, 32868, (short) 1001, 5, false); // 베헤모스
            } else if (target == 25) {
                L1Teleport.teleport(pc, 32800, 32856, (short) 1000, 5, false); // 실베리아
            } else if (target == 26) {
                L1Teleport.teleport(pc, 32630, 32903, (short) 780, 5, false); // 테베사막
            } else if (target == 27) {
                L1Teleport.teleport(pc, 32743, 32799, (short) 781, 5, false); // 테베
            } else if (target == 28) {
                L1Teleport.teleport(pc, 32735, 32830, (short) 782, 5, false); // 테베
            } else if (target == 29) {
                L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, false); // 감옥
            } else if (target == 30) {
                L1Teleport.teleport(pc, 32760, 32870, (short) 610, 5, false); // 벗꽃마을
            } else if (target == 31) {
                L1Teleport.teleport(pc, 32645, 32904, (short) 5153, 5, false);  //배틀존
            } else if (target == 32) {
                L1Teleport.teleport(pc, 32723, 32800, (short) 5167, 5, false);  //악영
            } else if (target == 33) {
                L1Teleport.teleport(pc, 32699, 32770, (short) 666, 5, false);  //지옥
            } else if (target == 34) {
                L1Teleport.teleport(pc, 32799, 33112, (short) 6202, 5, false);  //후원
            } else {
                L1Location loc = GMCommandsUtils.ROOMS.get(arg.toLowerCase());

                if (loc == null) {
                    showInfo(pc);
                    return;
                }

                L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);

                String name = MapsTable.getInstance().getMapName(loc.getMapId());

                pc.sendPackets(new S_SystemMessage("운영자 귀환(" + name + ")으로 이동 했습니다."));

                return;
            }

            pc.sendPackets(new S_SystemMessage("운영자 귀환(" + target + ")번으로 이동 했습니다."));
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(".귀환 [장소명]을 입력 해주세요.(장소명은 GMCommands.xml을 참조)"));
        }
    }

    public void showInfo(L1PcInstance pc) {
        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
        pc.sendPackets(new S_SystemMessage("  GM 귀환 명령어 List..."));
        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
        pc.sendPackets(new S_SystemMessage("  0.GM1 1.GM2 2.요숲 3.판도라 4.오렌 5.기란 6.켄말"));
        pc.sendPackets(new S_SystemMessage("  7.윈말 8.하이네 9.은말 10.말섬 11.아덴 12.몽섬"));
        pc.sendPackets(new S_SystemMessage("  13.잊섬 14.켄성 15.윈성 16.기란성 17.하이네성"));
        pc.sendPackets(new S_SystemMessage("  18.아덴성 19.켄성수탑 20.윈성수탑 21.기란수탑"));
        pc.sendPackets(new S_SystemMessage("  22.하이네수탑 23.해적섬 24.베헤모스 25.실베리아"));
        pc.sendPackets(new S_SystemMessage("  26.테베사막 27.피라미드내부 28.오리시스제단"));
        pc.sendPackets(new S_SystemMessage("  29.감옥 30.벗꽃마을 31.배틀존 32.악영 33.지옥 34.후원"));

        int i = 0;

        StringBuilder sb = new StringBuilder();

        for (String name : GMCommandsUtils.ROOMS.keySet()) {
            sb.append("  ").append(name).append("  ");

            if (i % 3 == 0 && i > 0) {
                sb.append("\r\n");
            }

            i++;
        }

        pc.sendPackets(sb.toString());

        pc.sendPackets(new S_SystemMessage("\\fY＊---------------------------------------------------＊"));
    }
}
