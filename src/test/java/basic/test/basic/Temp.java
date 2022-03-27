package basic.test.basic;

import basic.test.BaseTest;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Temp extends BaseTest {
    public static void main(String[] args) throws IOException {
//        List<String> list = Files.readAllLines(Paths.get("source/text/zone3-k.tbl"));
//        for (String s : list) {
//            System.out.println(s);
//        }

        File f = new File("source/text/zone4-k.tbl");
        String s = IOUtils.toString(new FileInputStream(f), "x-windows-949");

        //s = s.replace("\"", "");

        List<String> r = new ArrayList<>();

        for (String k : s.split("\r\n")) {
            String z = k.substring(1);

            String name = z.substring(0, z.indexOf("\""));

            String et = k.replace("\"" + name + "\"", "").trim();
            String[] o = et.split(" ");

            String a1 = o[0];
            String a2 = o[1];
            String a3 = o[2];
            String a4 = o[3];
            String a5 = o[4];
            String a6 = o[5];

            String sql = "insert into map_zone (" +
                    "name,\n" +
                    "mapType,\n" +
                    "mapId,\n" +
                    "x1,\n" +
                    "y1,\n" +
                    "x2,\n" +
                    "y2,\n" +
                    "data1,\n" +
                    "data2,\n" +
                    "data3\n) values (?,?,?,?,?,?,?,?,?,?)";

            jdbcTemplate.update(sql, name, a1, a2, a3, a4, a5, a6, "", "", "");
        }

        System.exit(0);
    }
}
