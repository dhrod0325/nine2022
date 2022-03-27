package mapgen;

import mapgen.util.StreamUtils;
import mapgen.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MapInfoBuilder {
    ArrayList<String> _infos = new ArrayList<String>();

    public void add(int no, int xStart, int xEnd, int yStart, int yEnd) {
        String s = "Update `mapids` set startX='" + xStart + "', endX='" + xEnd + "', startY='" + yStart + "', endY='" + yEnd + "' Where mapid='" + no + "';";
        _infos.add(s);
    }

    public void save(File f) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);

            bw.write(this.toString());

            bw.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            StreamUtils.close(bw, fw);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(StringUtils.join(_infos.toArray(), "\r\n"));
        return result.toString();
    }
}