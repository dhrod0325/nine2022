package mapgen;

import java.io.File;
import java.io.FileFilter;

public class MapDirectoryFilter implements FileFilter {
    public boolean accept(File file) {
        try {
            Integer.parseInt(file.getName());
        } catch (NumberFormatException e) {
            return false;
        }
        return file.isDirectory();
    }
}