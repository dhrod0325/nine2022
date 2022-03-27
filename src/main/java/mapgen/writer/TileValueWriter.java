package mapgen.writer;

import mapgen.TileValueList;
import mapgen.util.StreamUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TileValueWriter {
    public static void writeTileValues(File f, TileValueList valueList) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);

            bw.write(valueList.toString());
            bw.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            StreamUtils.close(bw, fw);
        }
    }
}
