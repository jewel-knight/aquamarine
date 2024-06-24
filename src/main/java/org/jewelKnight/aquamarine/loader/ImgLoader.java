package com.impactcn.aquamarine.loader;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author impactCn
 * @date 2023/10/1 16:08
 */
public class ImgLoader extends ResourcesLoader {

    private static final String IMG = "img";

    private final static Map<String, String> IMG_MAP = new ConcurrentHashMap<>();

    static {
        new ImgLoader().loadFiles();

    }


    @Override
    void loadFiles() {
        File[] files = getFiles(IMG);
        if (files == null) {
            throw new RuntimeException("img 文件夹不存在");
        }
        recursiveFile(files, IMG_MAP, STR);


    }

    public static String getVal(String key) {
        return IMG_MAP.get(key);
    }


}
