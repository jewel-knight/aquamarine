package com.impactcn.aquamarine.service;

import javafx.util.Pair;

import java.io.File;

/**
 * @author impactCn
 * @date 2023/10/3 20:00
 */
public interface FileService {

    /**
     * 创建 md 文件
     */
    void createFile(String currPath, String type, String name);


    /**
     * 删除文件 or 文件夹
     */
    void deleteFile(String currPath, String name);

    void rename(String filePath, String name, String newName);

    Pair<String, File[]> getFiles(String path);

    String getContent(String currPath, String name);

    boolean detect(String currPath, String name);

    void save(String name, String content);

}
