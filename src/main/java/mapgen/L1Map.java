package mapgen;

import java.util.ArrayList;
import java.util.TreeSet;

public class L1Map {
    private final ArrayList<L1MapPart> _maps;
    private final int _xLoc;
    private final int _yLoc;
    private final int _xSize;
    private final int _ySize;
    private final int _num;

    private int[] _tiles = null;

    public L1Map(ArrayList<L1MapPart> maps, int num, int xLoc, int yLoc, int xSize, int ySize) {
        _maps = maps;
        _num = num;
        _xLoc = xLoc;
        _yLoc = yLoc;
        _xSize = xSize;
        _ySize = ySize;

    }

    private int getOffset(int xOff, int yOff, int x, int y) {
        return (_xSize * 64 * 2) * (yOff * 64 + y) + (xOff * 64 * 2) + x;
    }

    public void build() {
        _tiles = new int[_xSize * _ySize * 64 * 64 * 2];

        for (L1MapPart part : _maps) {
            part.load();
            int xOff = part.getXOff();
            int yOff = part.getYOff();

            for (int y = 0; y < 64; y++) {
                int[] row = part.getRow(y);

                for (int x = 0; x < 64 * 2; x++) {
                    _tiles[getOffset(xOff, yOff, x, y)] = row[x];
                }
            }
        }
    }

    public TreeSet<Integer> getTileValueSet() {
        TreeSet<Integer> result = new TreeSet<>();
        for (int v : _tiles) {
            result.add(v);
        }
        return result;
    }

    public int getTile(int idx) {
        return _tiles[idx];
    }

    public int[] getRawTile() {
        return _tiles;
    }

    public int getXLoc() {
        return _xLoc;
    }

    public int getYLoc() {
        return _yLoc;
    }

    public int getXSize() {
        return _xSize;
    }

    public int getYSize() {
        return _ySize;
    }

    public int getNum() {
        return _num;
    }
}