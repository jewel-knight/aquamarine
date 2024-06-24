package com.impactcn.aquamarine.loader;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author impactCn
 * @date 2023/9/30 17:31
 */
public class CssLoader extends ResourcesLoader {

    private static final String CSS = "css";

    private static final Map<String, String> CSS_MAP = new HashMap<>();

    private static final List<String> IGNORES = new ArrayList<>();

    private static final List<String> FILTER = new ArrayList<>();


    static {
        new CssLoader().loadFiles();

        IGNORES.add("github-markdown-dark.css");
        IGNORES.add("setting.css");
        for (String ignore : IGNORES) {
            FILTER.add(CSS_MAP.get(ignore));
        }
    }
    @Override
    void loadFiles() {
        File[] files = getFiles(CSS);
        if (files == null) {
            throw new RuntimeException("css 文件夹不存在");
        }

        recursiveFile(files, CSS_MAP, STR);

    }



    public static List<String> getAll() {

        return CSS_MAP.values().stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return !FILTER.contains(s);
            }
        }).toList();
    }

    public static String getVal(String key) {
        return CSS_MAP.get(key);
    }

    public static List<String> getVal(List<String> keys) {
        List<String> values = new ArrayList<>();
        for (String key : keys) {
            values.add(CSS_MAP.get(key));
        }
        return values;
    }


}
