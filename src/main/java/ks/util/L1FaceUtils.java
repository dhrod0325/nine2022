package ks.util;

import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

import java.util.List;

public class L1FaceUtils {
    public static L1PcInstance faceToFace(L1PcInstance pc) {
        int pcX = pc.getX();
        int pcY = pc.getY();
        int pcHeading = pc.getHeading();

        List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(pc, 1);

        if (players.size() == 0) {
            pc.sendPackets(new S_ServerMessage(93));
            return null;
        }
        for (L1PcInstance target : players) {
            int targetX = target.getX();
            int targetY = target.getY();
            int targetHeading = target.getHeading();
            if (pcHeading == 0 && pcX == targetX && pcY == (targetY + 1)) {
                if (targetHeading == 4) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 1 && pcX == (targetX - 1) && pcY == (targetY + 1)) {
                if (targetHeading == 5) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 2 && pcX == (targetX - 1) && pcY == targetY) {
                if (targetHeading == 6) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 3 && pcX == (targetX - 1) && pcY == (targetY - 1)) {
                if (targetHeading == 7) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 4 && pcX == targetX && pcY == (targetY - 1)) {
                if (targetHeading == 0) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 5 && pcX == (targetX + 1) && pcY == (targetY - 1)) {
                if (targetHeading == 1) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 6 && pcX == (targetX + 1) && pcY == targetY) {
                if (targetHeading == 2) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            } else if (pcHeading == 7 && pcX == (targetX + 1) && pcY == (targetY + 1)) {
                if (targetHeading == 3) {
                    return target;
                } else {
                    pc.sendPackets(new S_ServerMessage(91, target.getName()));
                    return null;
                }
            }
        }

        pc.sendPackets(new S_ServerMessage(93));

        return null;
    }
}
