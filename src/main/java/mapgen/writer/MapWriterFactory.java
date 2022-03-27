package mapgen.writer;

import mapgen.Config;

import java.util.ArrayList;
import java.util.List;

public class MapWriterFactory {
    public static List<MapWriter> newWriters() {
        List<MapWriter> result = new ArrayList<MapWriter>();
        if (Config.OUTPUT_V1MAPS) {
            result.add(new V1MapWriter());
        }
        if (Config.OUTPUT_V2MAPS) {
            result.add(new V2MapWriter());
        }
        return result;
    }
}
