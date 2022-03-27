package ks.core.network;

import ks.core.network.encrypt.L1Encryption;
import ks.core.network.opcode.L1Opcodes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1Packet {
    private final Logger logger = LogManager.getLogger();

    private final L1Encryption encryption = new L1Encryption(L1Opcodes.SEED);

    private final byte[] packet = new byte[1024 * 2];
    private int packetIdx = 0;

    public byte[] getPacket() {
        return packet;
    }

    public int getPacketIdx() {
        return packetIdx;
    }

    public void addPacketIdx(int idx) {
        packetIdx += idx;
    }

    public byte[] encrypt(byte[] data) {
        try {
            char[] data1 = encryption.getUChar8().fromArray(data);
            data1 = encryption.encrypt(data1);
            return encryption.getUByte8().fromArray(data1);
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return null;
    }

    public byte[] decrypt(byte[] data) {
        try {
            int length = packetSize(data) - 2;

            byte[] temp = new byte[length];
            char[] incoming = new char[length];

            System.arraycopy(data, 2, temp, 0, length);

            incoming = encryption.getUChar8().fromArray(temp, incoming, length);
            incoming = encryption.decrypt(incoming, length);

            return encryption.getUByte8().fromArray(incoming, temp);
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return data;
    }

    public int packetSize() {
        return packetSize(packet);
    }

    private int packetSize(byte[] data) {
        int length = data[0] & 0xff;
        length |= data[1] << 8 & 0xff00;

        return length;
    }
}
