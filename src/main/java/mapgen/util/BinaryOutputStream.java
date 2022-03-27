package mapgen.util;

import java.io.IOException;
import java.io.OutputStream;

public class BinaryOutputStream extends OutputStream {
    private final OutputStream _os;

    public BinaryOutputStream(OutputStream os) {
        _os = os;
    }

    @Override
    public void write(int b) throws IOException {
        _os.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        _os.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        _os.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        _os.flush();
    }

    @Override
    public void close() throws IOException {
        _os.close();
    }

    public void writeByte(int b) throws IOException {
        _os.write(b);
    }

    public void writeShort(int s) throws IOException {
        _os.write(s & 0x00FF);
        _os.write((s >> 8) & 0x00FF);
    }

    public void writeInt(int i) throws IOException {
        writeShort(i & 0x0000FFFF);
        writeShort((i >> 16) & 0x0000FFFF);
    }
}
