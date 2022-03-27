package ks.core.network.util;

import ks.core.datatables.IpTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.StringTokenizer;

public class L1NetworkUtils {
    private static final Logger logger = LogManager.getLogger(L1NetworkUtils.class);

    public static boolean isPortAttack(String remoteAddr) {
        try {
            if (StringUtils.isEmpty(remoteAddr)) {
                return false;
            }

            StringTokenizer st = new StringTokenizer(remoteAddr.substring(1), ":");
            String remoteIp = st.nextToken();
            IpTable table = IpTable.getInstance();

            String check = st.nextToken();

            if (check.startsWith("0")) {
                table.insert(remoteIp);
                logger.warn("O 포트 공격 자동 차단: " + check);
                return true;
            } else if (check.startsWith("null")) {
                table.insert(remoteIp);
                logger.warn("NULL 포트 공격 자동 차단: " + check);
                return true;
            } else if (check.isEmpty()) {
                table.insert(remoteIp);
                logger.warn("Empty 포트 공격 자동 차단: " + check);
                return true;
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return false;
    }

    public static byte[] buffer(byte[] data, int length) {
        byte[] size = new byte[2];

        size[0] |= length & 0xff;
        size[1] |= length >> 8 & 0xff;

        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(size);
        buffer.put(data);
        buffer.flip();

        return buffer.array();
    }
}
