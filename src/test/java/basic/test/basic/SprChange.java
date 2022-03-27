package basic.test.basic;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SprChange {
    public static void main(String[] args) throws IOException {
        String str = IOUtils.toString(new FileInputStream("source/sprChange.txt"), StandardCharsets.UTF_8);

        str = str.replace("_", " ");
        str = str.replace(".", " ");
        str = str.replace("(", " ");
        str = str.replace(")", " ");
        str = str.replace(":", " ");

        str = str.replace(",", " ");

        StringBuilder sb = new StringBuilder();

        for (String s : str.split("\r")) {
            if (s.contains("RunL") || s.contains("RunR"))
                continue;

            sb.append(s);
        }

        str = sb.toString();

        str = str.replaceAll("[a-zA-Z]", "");
        str = str.replace("    ", " ");
        str = str.replace("   ", " ");
        str = str.replace("  ", " ");
        str = str.replace("  ", " ");


        System.out.println(str);
    }
}
