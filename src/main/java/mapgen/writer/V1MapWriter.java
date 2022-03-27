package mapgen.writer;

import mapgen.L1Map;
import mapgen.util.StreamUtils;

import java.io.*;

public class V1MapWriter implements MapWriter {
    private int toV1Value(int tile1, int tile2) {
        int result = 0;
        if ((tile1 & 0x0c) == 0) { // 0b1100 == 0x0c
            result += 0; // NormalZone
        } else if ((tile1 & 0x04) == 0x04) { // 0b0100 == 0x04
            result += 16; // SafetyZone
        } else {
            result += 32; // CombatZone
        }
        if ((tile1 >>> 4) == 0) { //Upper 4 bits are 0
            if ((tile1 & 0x01) == 0) {
                result += 10; //Movable and arrow can pass through (uncertain)
            }
        } else {
            if (((tile1 >>> 4) & 0x01) == 0) {
                result += 8; // Arrow can pass
            }
            if ((tile1 & 0x01) == 0) {
                result += 2; // Movable
            }
        }

        if ((tile2 >>> 4) == 0) {
            if ((tile2 & 0x01) == 0) {
                result += 5; //Movable and arrow can pass through (uncertain)
            }
        } else {
            if (((tile2 >>> 4) & 0x01) == 0) {
                result += 4; // Arrow can Pass
            }
            if ((tile2 & 0x01) == 0) {
                result += 1; // Movable
            }
        }

        return result;
    }

    @Override
    public void writeMap(L1Map map, File dir) {
        dir = new File(dir, "maps");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(new File(dir, map.getNum() + ".txt"));
            bos = new BufferedOutputStream(fos);

            writeMap(map, bos);

            bos.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            StreamUtils.close(bos, fos);
        }
    }

    @Override
    public void writeMap(L1Map map, OutputStream out) throws IOException {
        int[][] m = optimization(map);

        for (int[] ints : m) {
            for (int x = 0; x < ints.length; x++) {
                String s = Integer.toString(ints[x]);
                out.write(s.getBytes());
                if (x < ints.length - 1) {
                    out.write(",".getBytes());
                }
            }
            out.write("\r\n".getBytes());
        }
    }

    private int[][] optimization(L1Map map) {
        int cell = 64;

        int xSize = map.getXSize();
        int ySize = map.getYSize();
        int[][] m = new int[ySize * cell][xSize * cell];

        boolean isMargin;

        for (int y = 0; y < ySize * cell; y++) {
            for (int x = 0; x < xSize * cell; x++) {
                int off = (y * (cell * 2 * xSize)) + x * 2;
                m[y][x] = toV1Value(map.getTile(off), map.getTile(off + 1));
            }
        }

        for (int y = 0; y * cell < m.length; y++) {
            for (int x = 0; x * cell < m[y].length; x++) {
                // 64 * 64 check cell or a vacant lot
                isMargin = true;
                for (int i = y * cell; i < (y + 1) * cell; i++) {
                    for (int j = x * cell; j < (x + 1) * cell; j++) {
                        if (m[i][j] != 15) {
                            isMargin = false;
                            break;
                        }
                    }
                    if (!isMargin) {
                        break;
                    }
                }

                if (isMargin) {
                    for (int i = y * cell; i < (y + 1) * cell; i++) {
                        for (int j = x * cell; j < (x + 1) * cell; j++) {
                            m[i][j] = 0;
                        }
                    }
                }
            }
        }

        return m;
    }
}
