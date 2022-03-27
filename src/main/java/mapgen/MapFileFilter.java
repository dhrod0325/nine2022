package mapgen;

import java.io.File;
import java.io.FileFilter;

public class MapFileFilter implements FileFilter {
    public boolean accept(File file) {
        if (!file.canRead())
            return false;

        try {
            new MapFileName(file);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}