package org.jewel.knight.aquamarine.loader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author impactCn
 * @date 2023/10/1 12:40
 */
public class FxmlLoader extends ResourcesLoader {

    private static final String FXML = "fxml";
    private final static Map<String, URL> FXML_MAP = new ConcurrentHashMap<>();

    static {
        new FxmlLoader().loadFiles();

    }


    @Override
    void loadFiles() {
        File[] files = getFiles(FXML);
        if (files == null) {
            throw new RuntimeException("fxml 文件夹不存在");
        }
        recursiveFile(files, FXML_MAP, URL);


    }

    public static Parent getParent(String key, ApplicationContext applicationContext) {
        FXMLLoader fxmlLoader = new FXMLLoader(getVal(key));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(key + " 加载失败");
        }
        return null;
    }


    public static URL getVal(String key) {
        return FXML_MAP.get(key);
    }


}
