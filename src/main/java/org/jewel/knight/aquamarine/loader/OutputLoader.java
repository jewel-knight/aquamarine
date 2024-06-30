package org.jewel.knight.aquamarine.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author impactCn
 * @date 2023/11/13 0:21
 */
public class OutputLoader extends ResourcesLoader {

    private static final String OUTPUT = "output";

    private final static Map<String, String> OUTPUT_MAP = new ConcurrentHashMap<>();

    static {
        new OutputLoader().loadFiles();
    }

    @Override
    void loadFiles() {
        File[] files = getFiles(OUTPUT);
        if (files == null) {
            throw new RuntimeException("output 文件夹不存在");
        }
        try {
            for (File file : files) {
                if (file.isFile()) {
                    OutputLoader.OUTPUT_MAP.put(file.getName(), Files.readString(file.toPath()));
                }
            }
        } catch (IOException ignored) {

        }
    }

    public static String getVal(String key) {
        return OUTPUT_MAP.get(key);
    }
}
