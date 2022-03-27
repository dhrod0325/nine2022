package mapgen;

import java.io.*;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "./data/mapgen/config.properties.xml";
    private static final Properties _prop = new Properties();

    public static String MAP_DIR = null;
    public static String OUTPUT_DIR = null;
    public static boolean OUTPUT_V1MAPS = false;
    public static boolean OUTPUT_V2MAPS = false;
    public static boolean OUTPUT_MAPINFO = false;
    public static boolean OUTPUT_MAPIDS = false;
    public static String ZONE_FILE = null;
    public static boolean OUTPUT_TILEVALUES = false;

    private static void loadFromStream(InputStream is) throws IOException {
        _prop.loadFromXML(is);

        MAP_DIR = _prop.getProperty("LineageMapDirectory");
        OUTPUT_DIR = _prop.getProperty("OutputDirectory");
        OUTPUT_V1MAPS = _prop.getProperty("OutputV1Maps", "yes")
                .equalsIgnoreCase("yes");
        OUTPUT_V2MAPS = _prop.getProperty("OutputV2Maps", "no")
                .equalsIgnoreCase("yes");
        OUTPUT_MAPINFO = _prop.getProperty("OutputV1MapInfo", "yes")
                .equalsIgnoreCase("yes");
        OUTPUT_MAPIDS = _prop.getProperty("OutputMapIds", "yes")
                .equalsIgnoreCase("yes");
        ZONE_FILE = _prop.getProperty("ZoneFilePath");
        OUTPUT_TILEVALUES = _prop.getProperty("OutputTileValue", "yes")
                .equalsIgnoreCase("yes");
    }

    public static void load(String props) throws IOException {
        InputStream is = new ByteArrayInputStream(props.getBytes());
        loadFromStream(is);
        is.close();
    }

    public static void load() throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(new File(
                CONFIG_FILE)));
        loadFromStream(is);
        is.close();
    }
}
