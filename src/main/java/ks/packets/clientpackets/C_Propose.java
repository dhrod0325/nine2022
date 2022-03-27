package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1FaceUtils;

public class C_Propose extends ClientBasePacket {
    public C_Propose(byte[] data, L1Client clientthread) {
        super(data);
        int c = readC();

        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }
        if (c == 0) { // /propose(/프로포즈)
            L1PcInstance target = L1FaceUtils.faceToFace(pc);

            if (target != null) {
                if (pc.getPartnerId() > 0) {
                    pc.sendPackets(new S_ServerMessage(657)); // \f1당신은 벌써
                    // 결혼했습니다.
                    return;
                }
                if (target.getPartnerId() > 0) {
                    pc.sendPackets(new S_ServerMessage(658)); // \f1 그 상대는 벌써
                    // 결혼했습니다.
                    return;
                }
                if (pc.getSex() == target.getSex()) {
                    pc.sendPackets(new S_ServerMessage(661)); // \f1결혼상대는 이성이
                    // 아니면 안됩니다.
                    return;
                }
                if (!pc.getInventory().checkItem(40903)
                        || !pc.getInventory().checkItem(40904)
                        || !pc.getInventory().checkItem(40905)
                        || !pc.getInventory().checkItem(40906)
                        || !pc.getInventory().checkItem(40907)
                        || !pc.getInventory().checkItem(40908)) {
                    pc.sendPackets(new S_ServerMessage(659));
                }
                if (!target.getInventory().checkItem(40903)
                        || !target.getInventory().checkItem(40904)
                        || !target.getInventory().checkItem(40905)
                        || !target.getInventory().checkItem(40906)
                        || !target.getInventory().checkItem(40907)
                        || !target.getInventory().checkItem(40908)) {
                    pc.sendPackets(new S_ServerMessage(660));
                }
                if (pc.getX() >= 33974 && pc.getX() <= 33976
                        && pc.getY() >= 33362 && pc.getY() <= 33365
                        && pc.getMapId() == 4 && target.getX() >= 33974
                        && target.getX() <= 33976 && target.getY() >= 33362
                        && target.getY() <= 33365 && target.getMapId() == 4) {
                    target.setTempID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
                    target.sendPackets(new S_Message_YN(654, pc.getName()));
                }
            }
        } else if (c == 1) { // /divorce(/이혼)
            if (pc.getPartnerId() == 0) {
                pc.sendPackets(new S_ServerMessage(662)); // \f1당신은 결혼하지
                // 않았습니다.
                return;
            }
            pc.sendPackets(new S_Message_YN(653, "")); // 이혼을 하면(자) 링은 사라져
            // 버립니다. 이혼을 바랍니까? (Y/N)
        }
    }
}
