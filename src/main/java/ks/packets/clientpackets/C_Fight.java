package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1FaceUtils;

public class C_Fight extends ClientBasePacket {
    public C_Fight(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        L1PcInstance target = L1FaceUtils.faceToFace(pc);
        if (target != null) {
            if (!target.isParalyzed()) {
                if (pc.getFightId() != 0) {
                    pc.sendPackets(new S_ServerMessage(633));
                    return;
                } else if (target.getFightId() != 0) {
                    target.sendPackets(new S_ServerMessage(634));
                    return;
                }
                pc.setFightId(target.getId());
                target.setFightId(pc.getId());
                target.sendPackets(new S_Message_YN(630, pc.getName()));
            }
        }
    }
}
