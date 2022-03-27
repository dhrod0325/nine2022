package mapgen;

import mapgen.util.FileUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapFileName {
    private final int _x;
    private final int _y;
    private final String _name;

    public MapFileName(File fileName) {
        this(fileName.getName());
    }

    public MapFileName(String name) {
        Pattern p = Pattern.compile("([0-9a-f]{4})([0-9a-f]{4})\\.(seg|s32)",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        if (!m.matches())
            throw new IllegalArgumentException();
        _name = name;
        _x = Integer.parseInt(m.group(1), 16);
        _y = Integer.parseInt(m.group(2), 16);
    }

    public String getNameWithoutExtension() {
        return FileUtils.getNameWithoutExtension(_name);
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }
}
