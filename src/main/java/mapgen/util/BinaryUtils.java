package mapgen.util;

public class BinaryUtils {
    public static int getBit(int i, int bit) {
        return ((i >>> (bit - 1)) & 0x01) == 0x01 ? 1 : 0;
    }

    public static String byteToBinaryString(byte i) {
        StringBuilder result = new StringBuilder();
        result.append(getBit(i, 8));
        result.append(getBit(i, 7));
        result.append(getBit(i, 6));
        result.append(getBit(i, 5));
        result.append(" ");
        result.append(getBit(i, 4));
        result.append(getBit(i, 3));
        result.append(getBit(i, 2));
        result.append(getBit(i, 1));
        return result.toString();
    }

}
