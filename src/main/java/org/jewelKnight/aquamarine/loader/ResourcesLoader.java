package com.impactcn.aquamarine.loader;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author impactCn
 * @date 2023/10/1 12:41
 */
public abstract class ResourcesLoader {

    /**
     * URL 类型
     */
    public static final byte URL = 1;

    /**
     * String 类型
     */
    public static final byte STR = 2;

    /**
     * 加载文件
     */
    abstract void loadFiles();

    /**
     * 获取文件夹下的所有文件
     * @param name
     * @return
     */
    public File[] getFiles(String name) {
        try {
            ClassLoader loader = ResourcesLoader.class.getClassLoader();

            Enumeration<URL> enumeration = loader.getResources(name);
            URL url = enumeration.nextElement();
            File folder = new File(url.getFile());
            return folder.listFiles();
        } catch (IOException e) {
            System.err.println("该资源不存在:" + name);
        }
        return null;
    }

    /**
     * 递归获取文件夹下的所有文件
     * @param files
     * @param map
     */
    @SuppressWarnings("unchecked")
    public void recursiveFile(File[] files, Map map, byte type) {

        for (File file : files) {
            if (file == null) {
                return;
            }
            if (file.isDirectory()) {
                recursiveFile(file.listFiles(), map, type);
            } else {
                switch (type) {
                    case URL:
                        try {
                            map.put(file.getName(), file.toURI().toURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case STR: {
                        map.put(file.getName(), file.toURI().toString());
                        break;

                    }
                    default:
                        break;
                }
            }

        }

    }




}
