package ks.commands.gm.command.executor;

import ks.app.config.prop.CodeConfig;
import ks.model.Broadcaster;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Lawful;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1Status implements L1CommandExecutor {
    // private static Logger _log = LogManager.getLogger(L1Status.class.getName());

    private L1Status() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Status();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String char_name = st.nextToken();
            String param = st.nextToken();
            int value = Integer.parseInt(st.nextToken());

            L1PcInstance target = null;
            target = L1World.getInstance().getPlayer(char_name);

            if (target == null) {
                pc.sendPackets(new S_ServerMessage(73, char_name)); // \f1%0은
                // 게임을 하고 있지
                // 않습니다.
                return;
            }

            // -- not use DB --
            if (param.equalsIgnoreCase("방어")) {
                target.getAC().addAc((byte) (value - target.getAC().getAc()));
            } else if (param.equalsIgnoreCase("마방")) {
                target.getResistance().addMr((short) (value - target.getResistance().getMr()));
            } else if (param.equalsIgnoreCase("공성")) {
                target.addHitUp((short) (value - target.getHitUp()));
            } else if (param.equalsIgnoreCase("대미지")) {
                target.addDmgUp((short) (value - target.getDmgUp()));
                // -- use DB --
            } else {
                if (param.equalsIgnoreCase("피")) {
                    target.addBaseMaxHp((short) (value - target.getBaseMaxHp()));
                    target.setCurrentHp(target.getMaxHp());
                } else if (param.equalsIgnoreCase("엠피")) {
                    target.addBaseMaxMp((short) (value - target.getBaseMaxMp()));
                    target.setCurrentMp(target.getMaxMp());
                } else if (param.equalsIgnoreCase("성향")) {
                    target.setLawful(value);
                    target.sendPackets(new S_Lawful(target.getId(), target.getLawful()));
                    Broadcaster.broadcastPacket(target, new S_Lawful(target.getId(), target.getLawful()));
                } else if (param.equalsIgnoreCase("우호도")) {
                    target.setKarma(value);
                } else if (param.equalsIgnoreCase("지엠")) {
                    if (value == CodeConfig.GM_CODE || value == 1) {
                        target.setAccessLevel((short) value);
                        target.sendPackets(new S_SystemMessage("RESTART 하면 GM권한이 생깁니다."));
                    } else if (value == 0 && target.getAccessLevel() == 1) {
                        target.setAccessLevel((short) value);
                        target.sendPackets(new S_SystemMessage("RESTART 하면 권한이 사라집니다."));
                    } else {
                        target.sendPackets(new S_SystemMessage("GM번호가 일치하지 않습니다."));
                    }
                } else if (param.equalsIgnoreCase("힘")) {
                    target.getAbility().setStr((byte) value);
                } else if (param.equalsIgnoreCase("콘")) {
                    target.getAbility().setCon((byte) value);
                } else if (param.equalsIgnoreCase("덱스")) {
                    target.getAbility().setDex((byte) value);
                } else if (param.equalsIgnoreCase("인트")) {
                    target.getAbility().setInt((byte) value);
                } else if (param.equalsIgnoreCase("위즈")) {
                    target.getAbility().setWis((byte) value);
                } else if (param.equalsIgnoreCase("카리")) {
                    target.getAbility().setCha((byte) value);
                } else {
                    pc.sendPackets(new S_SystemMessage("스테이터스 " + param + " (은)는 불명합니다. "));
                    return;
                }
                target.save(); // DB에 캐릭터 정보를 기입한다
            }
            target.sendPackets(new S_OwnCharStatus(target));
            pc.sendPackets(new S_SystemMessage(target.getName() + "의 " + param + "(을)를 " + value + "로 변경했습니다. "));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명] [스텟] [변경치]를 입력해 주세요. "));
            pc.sendPackets(new S_SystemMessage("[피][엠피][성향][우호도][지엠][힘][콘][덱스][인트][위즈][카리]"));
        }
    }
}
