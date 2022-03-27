package mapgen.util;

public class StringUtils {
    public static String join(Object[] arry, String with) {
        StringBuffer buf = new StringBuffer();
        for (Object s : arry) {
            if (buf.length() > 0) {
                buf.append(with);
            }
            buf.append(s);
        }
        return buf.toString();
    }

    public static String join(int[] arry, String with) {
        StringBuffer buf = new StringBuffer();
        for (Object s : arry) {
            if (buf.length() > 0) {
                buf.append(with);
            }
            buf.append(s);
        }
        return buf.toString();
    }
}