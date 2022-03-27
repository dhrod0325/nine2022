package mapgen;


import mapgen.util.BinaryInputStream;
import mapgen.util.FileUtils;
import mapgen.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class L1MapPart {
    private final File _file;
    private final int _xOff;
    private final int _yOff;
    private final int[] _tiles = new int[64 * 64 * 2];

    public L1MapPart(File file, int xOff, int yOff) {
        _file = file;
        _xOff = xOff;
        _yOff = yOff;
    }

    private void loadS32(BinaryInputStream bin) throws IOException {
        StreamUtils.forceSkip(bin, 0x8000);

        int numOfOptData = bin.readShort();
        StreamUtils.forceSkip(bin, numOfOptData * 6);

        for (int i = 0; i < 0x2000; i++) {
            _tiles[i] = bin.readShort();
        }
    }

    private void loadSeg(BinaryInputStream bin) throws IOException {
        StreamUtils.forceSkip(bin, 0x4000);

        int numOfOptData = bin.readShort();
        StreamUtils.forceSkip(bin, numOfOptData * 4L);

        for (int i = 0; i < 0x2000; i++) {
            _tiles[i] = bin.readByte();
        }
    }

    public void load() {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BinaryInputStream bin = null;
        try {
            fis = new FileInputStream(_file);
            bis = new BufferedInputStream(fis);
            bin = new BinaryInputStream(bis);

            String ext = FileUtils.getExtension(_file).toLowerCase();
            if (ext.equals("seg")) {
                loadSeg(bin);
            } else if (ext.equals("s32")) {
                loadS32(bin);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            StreamUtils.close(bin, bis, fis);
        }
    }

    public int[] getRow(int r) {
        int[] row = new int[64 * 2];

        System.arraycopy(_tiles, 64 * 2 * r, row, 0, 64 * 2);

        return row;
    }

    public int getXOff() {
        return _xOff;
    }

    public int getYOff() {
        return _yOff;
    }
}