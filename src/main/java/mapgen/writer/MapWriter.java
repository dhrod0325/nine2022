package mapgen.writer;

import mapgen.L1Map;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface MapWriter {
    void writeMap(L1Map map, File dir);

    void writeMap(L1Map map, OutputStream out) throws IOException;
}
