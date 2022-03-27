package ks.util.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private final static Logger logger = LogManager.getLogger();

    public static String getExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    public static String getNameWithoutExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return "";
    }

    public static List<String> readLineTextList(String fileName) {
        List<String> list = new ArrayList<>();

        try {
            list.addAll(Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return list;
    }
}
