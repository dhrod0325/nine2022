package ks.core.network.encrypt.types;

public class UByte8 {

    public byte[] fromArray(char[] buff) {
        byte[] byteBuff = new byte[buff.length];

        for (int i = 0; i < buff.length; ++i) {
            byteBuff[i] = (byte) (buff[i] & 0xFF);
        }

        return byteBuff;
    }

    public byte[] fromArray(char[] buff, byte[] data) {
        for (int i = 0; i < buff.length; ++i) {
            data[i] = (byte) (buff[i] & 0xFF);
        }

        return data;
    }
}
