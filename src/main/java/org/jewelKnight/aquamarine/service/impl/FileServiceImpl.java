package com.impactcn.aquamarine.service.impl;

import com.impactcn.aquamarine.service.FileService;
import javafx.util.Pair;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author impactCn
 * @date 2023/10/3 20:17
 */
@Service
public class FileServiceImpl implements FileService {

    public static final String MD = "md";

    public static final String DIR = "dir";

    public static final String FILE_SUFFIX = ".md";

    @Override
    public void createFile(String currPath, String type, String name) {

        switch (type) {
            case MD: {
                File file = new File(currPath + "/" + name + FILE_SUFFIX);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            case DIR: {

                Path path = Paths.get(currPath + name);
                try {
                    Files.createDirectory(path);
                } catch (IOException e) {
                    // 失败
                    throw new RuntimeException(e);
                }
            }
        }

    }


    @Override
    public void deleteFile(String currPath, String name) {
        File file = new File(currPath + "/" +name + FILE_SUFFIX);
        file.delete();
    }

    @Override
    public void rename(String currPath, String name, String newName) {
        File file = new File(currPath + "/" +name + FILE_SUFFIX);


        String newPath = file.getParentFile().getPath() + "/" +  newName +  FILE_SUFFIX;

        file.renameTo(new File(newPath));
    }

    @Override
    public Pair<String, File[]> getFiles(String path) {
        File root = new File(path);
        String name = root.getName();
        return new Pair<>(name, root.listFiles());
    }

    @Override
    public String getContent(String currPath, String name) {
        try {
            if (!name.contains(FILE_SUFFIX)) {
                return Files.readString(Paths.get(currPath + "/" + name + FILE_SUFFIX));
            } else {
                return Files.readString(Paths.get(currPath + "/" + name));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean detect(String currPath, String name) {
        //// todo 根据文件判断
        File file = new File(currPath + name);
        Tika tika = new Tika();
        try {
            String mimeType = tika.detect(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void save(String name, String content) {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(name))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
