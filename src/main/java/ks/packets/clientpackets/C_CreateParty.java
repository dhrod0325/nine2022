package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;

public class C_CreateParty extends ClientBasePacket {
    public C_CreateParty(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        int type = readC();
        if (type == 0 || type == 1) {// 0.일반 1.분배
            int targetId = readD();
            L1Object temp = L1World.getInstance().findObject(targetId);
            if (temp instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) temp;

                if (pc.getId() == targetPc.getId())
                    return;

                if (targetPc.isInParty()) {
                    pc.sendPackets(new S_ServerMessage(415));
                    return;
                }

                if (pc.isInParty()) {
                    if (pc.getParty().isLeader(pc)) {
                        targetPc.setPartyID(pc.getId());
                        targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(416));
                    }
                } else {
                    targetPc.setPartyID(pc.getId());
                    switch (type) {
                        case 0:
                            targetPc.sendPackets(new S_Message_YN(953, pc.getName()));
                            break;
                        case 1:
                            targetPc.sendPackets(new S_Message_YN(954, pc.getName()));
                            break;
                    }
                }
            }
        } else if (type == 2) { // 채팅 파티
            String name = readS();
            L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
            if (!pc.isGm() && ((name.compareTo("메티스") == 0) || (name.compareTo("미소피아") == 0))) { //운영자 채팅초대
                pc.sendPackets(new S_SystemMessage(targetPc + "님은 접속중이 아닙니다."));
                return;
            }
            if (targetPc == null) {
                pc.sendPackets(new S_ServerMessage(109));
                return;
            }
            if (pc.getId() == targetPc.getId())
                return;

            if (targetPc.isInChatParty()) {
                pc.sendPackets(new S_ServerMessage(415));
                return;
            }

            if (pc.isInChatParty()) {
                if (pc.getChatParty().isLeader(pc)) {
                    targetPc.setPartyID(pc.getId());
                    targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
                } else {
                    pc.sendPackets(new S_ServerMessage(416));
                }
            } else {
                targetPc.setPartyID(pc.getId());
                targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
            }
        } else if (type == 3) {
            int targetId = readD();
            L1Object temp = L1World.getInstance().findObject(targetId);
            if (temp instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) temp;
                if (pc.getId() == targetPc.getId()) {
                    return;
                }

                if (pc.isInParty()) {
                    if (targetPc.isInParty()) {
                        if (pc.getParty().isLeader(pc)) {
                            if (pc.getLocation().getTileLineDistance(targetPc.getLocation()) < 16) {
                                pc.getParty().passLeader(targetPc);
                            } else {
                                pc.sendPackets(new S_ServerMessage(1695));
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(1697));
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(1696));
                    }
                }
            }
        }
    }
}

