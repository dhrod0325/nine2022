package ks.core.network.encrypt.types;

public class UChar8 {
    public char[] fromArray(long[] buff) {
        char[] charBuff = new char[buff.length * 4];
        return getChars(buff, charBuff);
    }

    public char[] fromArray(long[] buff, char[] charBuff) {
        return getChars(buff, charBuff);
    }

    private char[] getChars(long[] buff, char[] charBuff) {
        for (int i = 0; i < buff.length; ++i) {
            charBuff[(i * 4)] = (char) (buff[i] & 0xFF);
            charBuff[(i * 4) + 1] = (char) ((buff[i] >> 8) & 0xFF);
            charBuff[(i * 4) + 2] = (char) ((buff[i] >> 16) & 0xFF);
            charBuff[(i * 4) + 3] = (char) ((buff[i] >> 24) & 0xFF);
        }

        return charBuff;
    }

    public char[] fromArray(byte[] buff) {
        char[] charBuff = new char[buff.length];

        for (int i = 0; i < buff.length; i++) {
            charBuff[i] = (char) (buff[i] & 0xFF);
        }

        return charBuff;
    }

    public char[] fromArray(byte[] buff, char[] data, int size) {
        for (int i = 0; i < size; i++) {
            data[i] = (char) (buff[i] & 0xFF);
        }

        return data;
    }

    public char[] fromULong32(long l) {
        char[] charBuff = new char[4];

        charBuff[0] = (char) (l & 0xFF);
        charBuff[1] = (char) ((l >> 8) & 0xFF);
        charBuff[2] = (char) ((l >> 16) & 0xFF);
        charBuff[3] = (char) ((l >> 24) & 0xFF);

        return charBuff;
    }
}
