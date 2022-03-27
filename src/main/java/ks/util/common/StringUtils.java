package ks.util.common;

public class StringUtils {
    public static boolean isDisitAlpha(String str) {
        boolean check = true;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)) // 숫자가 아니라면
                    && Character.isLetterOrDigit(str.charAt(i)) // 특수문자라면
                    && !Character.isUpperCase(str.charAt(i)) // 대문자가 아니라면
                    && !Character.isLowerCase(str.charAt(i))) { // 소문자가 아니라면
                check = false;
                break;
            }
        }

        return check;
    }

    public static byte[] leftPad(byte[] data, int size, byte pad) {
        if (size <= data.length) {
            return data;
        }

        byte[] newData = new byte[size];

        for (int i = 0; i < size; i++) {
            newData[i] = pad;
        }

        for (int i = 0; i < data.length; i++) {
            newData[size - i - 1] = data[data.length - i - 1];
        }

        return newData;
    }
}
