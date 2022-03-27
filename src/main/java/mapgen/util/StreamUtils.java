package mapgen.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    public static void close(Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public static void forceSkip(InputStream is, long n) throws IOException {
        while (0 < n) {
            long i = is.skip(n);
            if (i == 0) {
                throw new IOException("faild force skip");
            }
            n -= i;
        }
    }
}
