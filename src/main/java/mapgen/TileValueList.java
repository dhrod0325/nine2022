package mapgen;

import mapgen.util.BinaryUtils;

import java.util.Set;
import java.util.TreeSet;

public class TileValueList {
    private final TreeSet<Integer> _tiles = new TreeSet<Integer>();

    public void add(int value) {
        _tiles.add(value);
    }

    public void addAll(Set<Integer> s) {
        _tiles.addAll(s);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int v : _tiles) {
            result.append(v);
            result.append(" ");
            result.append(BinaryUtils.byteToBinaryString((byte) v));
            result.append("\r\n");
        }

        return result.toString();
    }
}
