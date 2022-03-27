package mapgen;

import java.io.File;
import java.util.Comparator;

public class MapDirComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
    }
}