package mapgen.writer;

import mapgen.L1Map;
import mapgen.util.BinaryOutputStream;
import mapgen.util.StreamUtils;

import java.io.*;
import java.util.zip.DeflaterOutputStream;

public class V2MapWriter implements MapWriter {
    private byte[] buildHeader(L1Map map) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BinaryOutputStream out = new BinaryOutputStream(bos);

        try {
            out.writeInt(map.getNum());
            out.writeInt(map.getXLoc());
            out.writeInt(map.getYLoc());
            out.writeInt(map.getXSize() * 64);
            out.writeInt(map.getYSize() * 64);
        } catch (IOException e) {
            new Error();
        } finally {
            StreamUtils.close(out, bos);
        }

        return bos.toByteArray();
    }

    @Override
    public void writeMap(L1Map map, File dir) {
        dir = new File(dir, "v2maps");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        DeflaterOutputStream dos = null;
        try {
            fos = new FileOutputStream(new File(dir, map.getNum() + ".md"));
            dos = new DeflaterOutputStream(fos);
            bos = new BufferedOutputStream(dos);

            writeMap(map, bos);

            bos.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            StreamUtils.close(bos, dos, fos);
        }
    }

    @Override
    public void writeMap(L1Map map, OutputStream out) throws IOException {
        out.write(buildHeader(map));
        int tiles[] = map.getRawTile();
        for (int i = 0; i < tiles.length; i++) {
            out.write(tiles[i]);
        }
    }

}
