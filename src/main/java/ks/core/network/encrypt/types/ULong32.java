package ks.core.network.encrypt.types;

public class ULong32 {
    public long fromArray(char[] buff) {
        return fromLong64(((buff[3] & 0xFF) << 24) | ((buff[2] & 0xFF) << 16) | ((buff[1] & 0xFF) << 8) | (buff[0] & 0xFF));
    }

    public long fromLong64(long l) {
        return (((l << 32) >>> 32));
    }

    public long fromInt32(int i) {
        return ((((long) i << 32) >>> 32));
    }

    public long add(long l1, long l2) {
        return fromInt32((int) l1 + (int) l2);
    }
}
