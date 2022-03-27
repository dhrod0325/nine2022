package ks.packets.serverpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;

public class S_ChatPacket extends ServerBasePacket {
    public S_ChatPacket(String targetName, String chat, int opcode) {
        writeC(opcode);
        writeC(9);
        writeS("-> (" + targetName + ") " + chat);
        writeC(13);
        writeS("\\fW[******] " + chat);
    }

    // 매니저용 귓말
    public S_ChatPacket(String from, String chat) {
        writeC(L1Opcodes.S_OPCODE_WHISPERCHAT);
        writeS(from);
        writeS(chat);
    }

    public S_ChatPacket(L1PcInstance pc, String chat, int opcode, int type) {
        writeC(opcode);
        switch (type) {
            case 0:
                writeC(type);
                writeD(pc.getId());
                writeS(pc.getName() + ": " + chat);
                break;
            case 2:
                writeC(type);
                if (pc.isInvisible()) {
                    writeD(0);
                } else {
                    writeD(pc.getId());
                }
                writeS("<" + pc.getName() + "> " + chat);
                writeH(pc.getX());
                writeH(pc.getY());
                break;
            //전체채팅
            case 3:
                if (pc.getName().equalsIgnoreCase("메티스") || pc.getName().equalsIgnoreCase("미소피아")
                        || pc.getName().equalsIgnoreCase("카시오페아")) {
                    writeC(type);
                    writeS("\\fY[" + pc.getName() + "]: " + chat);

                    if (CodeConfig.IS_GM_CHAT)//영자채팅 관련
                        L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "[******] " + chat));
                } else if (pc.getAccessLevel() == 1) {
                    writeC(type);
                    writeS("[" + pc.getName() + "] " + chat);
                } else {
                    writeC(type);
                    writeS("[" + pc.getName() + "] " + chat);
                }
                break;
            case 4:
                writeC(type);
                if (pc.getAge() == 0) {
                    writeS("{" + pc.getName() + "} " + chat);
                } else {
                    writeS("{" + pc.getName() + "(" + pc.getAge() + ")" + "} " + chat);
                }
                break;
            case 9:
                writeC(type);
                writeS("-> (" + pc.getName() + ") " + chat);
                break;
            case 11:
                writeC(type);
                writeS("(" + pc.getName() + ") " + chat);
                break;
            case 12:
                if (pc.isGm()) {
                    writeC(15);//색지정
                    writeS(chat);
                } else if (pc.getAccessLevel() == 1) {
                    writeC(type);
                    writeS("[" + pc.getName() + "] " + chat);//좀비채팅
                } else {
                    writeC(type);
                    writeS("[" + pc.getName() + "] " + chat);
                }
                break;
            case 13:
                writeC(15);
                writeD(pc.getId());
                writeS("\\fW{{" + pc.getName() + "}} " + chat);
                break;
            case 14:
                writeC(type);
                writeD(pc.getId());
                writeS("(" + pc.getName() + ") " + chat);
                break;
            case 15:
                writeC(type);
                writeS("[" + pc.getName() + "] " + chat);
                break;
            case 16:
                writeS(pc.getName());
                writeS(chat);
                break;
            case 17:
                writeC(type);
                writeS("{" + pc.getName() + "} " + chat);
                break;
            case 18: // PVP, 채팅퀴즈 , 타임이벤트
                writeC(3);
                writeS(chat);
                break;
            case 19: //이름제거
                writeC(11); //(주황색)
                writeS(chat);
                break;
            case 20: //이름제거
                writeC(4); //(연초록색)
                writeS(chat);
                break;
            case 21: //이름제거
                writeC(13); //(보라색)
                writeS(chat);
                break;
            case 22: //이름제거
                writeC(0); //(연갈색)오토방지색
                writeS(chat);
                break;
            case 23: //이름제거
                writeC(18); //(보라색)
                writeS(chat);
                break;
            case 24: //이름제거
                writeC(15); //(노랭색 장사)
                writeS(chat);
                break;
            default:
                break;
        }
    }
}
