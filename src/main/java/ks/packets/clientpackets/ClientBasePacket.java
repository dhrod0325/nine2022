package ks.packets.clientpackets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ClientBasePacket {
    protected final Logger logger = LogManager.getLogger(getClass());

    private final byte[] data;

    private int off = 1;

    public ClientBasePacket(byte[] data) {
        this.data = data;
    }

    public int readD() {
        int i = data[off++] & 0xff;
        i |= data[off++] << 8 & 0xff00;
        i |= data[off++] << 16 & 0xff0000;
        i |= data[off++] << 24 & 0xff000000;
        return i;
    }

    public int readC() {
        return data[off++] & 0xff;
    }

    public int readH() {
        try {
            int i = data[off++] & 0xff;
            i |= data[off++] << 8 & 0xff00;

            return i;
        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }

        return 0;
    }

    public int readCH() {
        int i = data[off++] & 0xff;
        i |= data[off++] << 8 & 0xff00;
        i |= data[off++] << 16 & 0xff0000;
        return i;
    }

    public String readS() {
        try {
            String s = new String(data, off, data.length - off, "EUC-KR");
            s = s.substring(0, s.indexOf('\0'));
            off += s.getBytes("EUC-KR").length + 1;
            return s;
        } catch (Exception e) {
            logger.trace(e);
            logger.error("오류", e);
        }

        return null;
    }

    public String readS2() {
        try {
            String s = new String(data, off, data.length - off);
            s = s.substring(0, s.indexOf('\0'));
            off += s.getBytes().length + 1;
            return s;
        } catch (Exception e) {
            logger.trace(e);
            logger.error("오류", e);
        }

        return null;
    }

    public String readSS() {
        try {
            int start = off;
            int loc = 0;

            while (readH() != 0) {
                loc += 2;
            }

            StringBuilder sb = new StringBuilder();

            do {
                if ((data[start] & 0xff) >= 127 || (data[start + 1] & 0xff) >= 127) {
                    byte[] t = new byte[2];
                    t[0] = data[start + 1];
                    t[1] = data[start];
                    sb.append(new String(t, 0, 2, "EUC-KR"));
                } else {
                    sb.append(new String(data, start, 1, "EUC-KR"));
                }
                start += 2;
                loc -= 2;
            } while (0 < loc);

            return sb.toString();
        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }

        return null;
    }

    public byte[] readByte() {
        byte[] result = new byte[data.length - off];

        try {
            System.arraycopy(data, off, result, 0, data.length - off);
            off = data.length;
        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }

        return result;
    }

    public String getType() {
        return "[C] " + this.getClass().getSimpleName();
    }
}
