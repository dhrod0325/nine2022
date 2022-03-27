package ks.packets.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;

public abstract class ServerBasePacket {
    protected final Logger logger = LogManager.getLogger();

    protected ByteArrayOutputStream bao = new ByteArrayOutputStream();

    public void writeD(int value) {
        bao.write(value & 0xff);
        bao.write(value >> 8 & 0xff);
        bao.write(value >> 16 & 0xff);
        bao.write(value >> 24 & 0xff);
    }

    public void writeH(int value) {
        bao.write(value & 0xff);
        bao.write(value >> 8 & 0xff);
    }

    public void writeC(int value) {
        bao.write(value & 0xff);
    }

    public void writeS(String text) {
        try {
            if (text != null) {
                bao.write(text.getBytes("EUC-KR"));
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        bao.write(0);
    }

    public void writeSS(String text) {
        try {
            if (StringUtils.isEmpty(text)) {
                return;
            }

            byte[] data = text.getBytes("EUC-KR");

            for (int i = 0; i < data.length; ) {
                if ((data[i] & 0xff) >= 0x7F) {
                    bao.write(data[i + 1]);
                    bao.write(data[i]);
                    i += 2;
                } else {
                    bao.write(data[i]);
                    bao.write(0);
                    i += 1;
                }
            }

        } catch (Exception e) {
            logger.error("오류", e);
        }

        bao.write(0);
        bao.write(0);
    }

    public void writeByte(byte[] bytes) {
        try {
            if (bytes != null) {
                bao.write(bytes);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public int getLength() {
        return bao.size() + 2;
    }

    public byte[] toBytes() {
        return bao.toByteArray();
    }

    public byte[] getBytes() {
        try {
            if (bao != null) {
                int padding = bao.size() % 8;

                if (padding != 0) {
                    for (int i = padding; i < 8; i++) {
                        writeC(0x00);
                    }
                }

                return bao.toByteArray();
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return new byte[]{};
    }

    public byte[] getContent() {
        return getBytes();
    }

    public String getType() {
        return "[S] " + this.getClass().getSimpleName();
    }

    public void close() {
        try {
            if (bao != null) {
                bao.reset();
                bao.close();
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        bao = null;
    }
}
