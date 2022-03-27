package ks.core.network.encrypt;

import ks.core.network.encrypt.types.UByte8;
import ks.core.network.encrypt.types.UChar8;
import ks.core.network.encrypt.types.ULong32;

public class L1Encryption {
    private final L1Blowfish blowfish = new L1Blowfish();

    private final UByte8 ub8 = new UByte8();

    private final UChar8 uc8 = new UChar8();

    private final ULong32 ul32 = new ULong32();

    private final long[] encodeKey = {0, 0};
    private final long[] decodeKey = {0, 0};

    private char[] dac1 = new char[400];
    private char[] eac1 = new char[5000];

    public L1Encryption(long key) {
        initKeys(key);
    }

    public UByte8 getUByte8() {
        return ub8;
    }

    public UChar8 getUChar8() {
        return uc8;
    }

    public void initKeys(long seed) {
        long[] keys = {seed, 0x930FD7E2L};

        blowfish.initKeys(keys);

        encodeKey[0] = decodeKey[0] = keys[0];
        encodeKey[1] = decodeKey[1] = keys[1];
    }

    public char[] encrypt(char[] ac) {
        long l = ul32.fromArray(ac);
        eac1 = uc8.fromArray(encodeKey, eac1);
        ac[0] ^= eac1[0];
        for (int j = 1; j < ac.length; j++) {
            ac[j] ^= ac[j - 1] ^ eac1[j & 7];
        }
        ac[3] = (char) (ac[3] ^ eac1[2]);
        ac[2] = (char) (ac[2] ^ ac[3] ^ eac1[3]);
        ac[1] = (char) (ac[1] ^ ac[2] ^ eac1[4]);
        ac[0] = (char) (ac[0] ^ ac[1] ^ eac1[5]);

        encodeKey[0] ^= l;
        encodeKey[1] = ul32.add(encodeKey[1], 0x287effc3L);

        return ac;
    }

    public char[] decrypt(char[] ac, int size) {
        dac1 = uc8.fromArray(decodeKey, dac1);
        char c = ac[3];
        ac[3] ^= dac1[2];
        char c1 = ac[2];
        ac[2] ^= c ^ dac1[3];
        char c2 = ac[1];
        ac[1] ^= c1 ^ dac1[4];
        char c3 = (char) (ac[0] ^ c2 ^ dac1[5]);
        ac[0] = (char) (c3 ^ dac1[0]);
        for (int j = 1; j < size; j++) {
            char c4 = ac[j];
            ac[j] ^= dac1[j & 7] ^ c3;
            c3 = c4;
        }

        long l = ul32.fromArray(ac);

        decodeKey[0] ^= l;
        decodeKey[1] = ul32.add(decodeKey[1], 0x287effc3L);

        return ac;
    }
}