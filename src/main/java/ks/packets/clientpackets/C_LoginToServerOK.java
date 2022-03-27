package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_LoginToServerOK extends ClientBasePacket {
    public C_LoginToServerOK(byte[] decrypt, L1Client client) {
        super(decrypt);
        int type = readC();
        int button = readC();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (type == 255) {
            if (button == 95 || button == 127) {
                pc.setShowWorldChat(true);
                pc.setCanWhisper(true);
            } else if (button == 91 || button == 123) {
                pc.setShowWorldChat(true);
                pc.setCanWhisper(false);
            } else if (button == 94 || button == 126) {
                pc.setShowWorldChat(false);
                pc.setCanWhisper(true);
            } else if (button == 90 || button == 122) {
                pc.setShowWorldChat(false);
                pc.setCanWhisper(false);
            }

        } else if (type == 0) {
            if (button == 0) {
                pc.setShowWorldChat(false);
            } else if (button == 1) {
                pc.setShowWorldChat(true);
            }

        } else if (type == 2) {
            if (button == 0) {
                pc.setCanWhisper(false);
            } else if (button == 1) {
                pc.setCanWhisper(true);
            }

        } else if (type == 6) {
            if (button == 0) {
                pc.setShowTradeChat(false);
            } else if (button == 1) {
                pc.setShowTradeChat(true);
            }
        }
    }
}