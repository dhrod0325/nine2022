package ks.core.reader;

import ks.app.config.prop.CodeConfig;
import ks.model.map.L1Map;

import java.io.IOException;
import java.util.Map;

public abstract class MapReader {
    public static MapReader getDefaultReader() {
        if (CodeConfig.CACHE_MAP_FILES) {
            return new CachedMapReader();
        }

        return new TextMapReader();
    }

    public abstract Map<Integer, L1Map> read() throws IOException;

    public abstract L1Map read(int id) throws IOException;
}
