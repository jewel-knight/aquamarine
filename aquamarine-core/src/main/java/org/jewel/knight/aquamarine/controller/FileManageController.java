package org.jewel.knight.aquamarine.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jewel.knight.aquamarine.controller.event.FlexibleNodeEvent;
import org.jewel.knight.aquamarine.controller.event.Position;
import org.jewel.knight.aquamarine.event.FileEvent;
import org.jewel.knight.aquamarine.loader.FxmlLoader;
import org.jewel.knight.aquamarine.loader.ImgLoader;
import org.jewel.knight.aquamarine.service.FileService;
import org.jewel.knight.aquamarine.service.PromptService;
import org.jewel.knight.aquamarine.service.impl.FileServiceImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2023/12/17 17:35
 */
@Component
public class FileManageController implements Initializable {

    public static final Map<String, HBox> FILE_MAPPING = new HashMap<>();

    public static final Map<HBox, List<Node>> TAB_MAPPING = new HashMap<>();

    public static final Map<String, VBox> RAG = new HashMap<>(4);

    public HBox fileContainer;

    public HBox fileSettingContainer;

    public MenuItem newFileItem;

    public MenuItem deleteFileItem;

    public MenuItem renameFileItem;

    public TreeView<String> fileTree;

    @Value("${file.path}")
    private String path;

    @Autowired
    private FileService fileService;

    @Autowired
    private EditorController editorController;

    @Autowired
    private EditorTabController editorTabController;

    @Autowired
    private PopUpContextMenuController popUpContextMenuController;

    @Autowired
    private ContextMenuController contextMenuController;

    @Autowired
    private InputController inputController;

    @Autowired
    private ConfigurableApplicationContext applicationContext;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFileTree();
        listenFileTree();
    }


    private void initFileTree() {


        FlexibleNodeEvent flexibleNodeEvent = new FlexibleNodeEvent(fileTree);
        flexibleNodeEvent.resize(Position.RIGHT);

        // 点击文件的时候，打开输入容器、输出容器，编辑的title，
        // 同时读取文件内容，先清理，在输入到 input 组件内
        fileTree.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {

                String fileName = fileTree.getFocusModel().getFocusedItem().getValue();

                // 说明当前 tab 就是点击的 tab
                if (editorTabController.getCurrTabName() != null) {
                    String curr = editorTabController.getCurrTabName().replace(".md", "");
                    if (curr.equals(fileName)) {
                        return;
                    }
                }

                // 点击的是 Home
                if (fileName.equals("Home")) {
                    return;
                }

                editorController.setHide(true);

                // 复制容器
                Platform.runLater(() -> {
                    String title = fileName + FileServiceImpl.FILE_SUFFIX;
                    String key = path + "/" + title;
                    HBox tab;
                    // 首次添加
                    if (!FILE_MAPPING.containsKey(key)) {
                        //// todo 待优化，启动的时候就加载一次
                        tab = (HBox) FxmlLoader.getParent("editorTab.fxml", applicationContext);
                        VBox input = (VBox) FxmlLoader.getParent("input.fxml", applicationContext);
                        VBox output = (VBox) FxmlLoader.getParent("output.fxml", applicationContext);
                        assert input != null;
                        assert output != null;
                        assert tab != null;
                        input.setPrefWidth(450);
                        output.setPrefWidth(450);
                        HBox.setHgrow(input, Priority.ALWAYS);
                        HBox.setHgrow(output, Priority.ALWAYS);

                        List<Node> nodes = List.of(input, output);
                        // 设置 tab 的 title
                        editorTabController.setTitle(title);
                        // 由于每次都是添加操作，直接传当前 list 的大小进去，则表示是最后一个需要高亮
                        editorTabController.setTabStatus(tab, true);
                        editorTabController.setClickTab(tab, TAB_MAPPING);
                        editorTabController.setClickClose(tab, FILE_MAPPING, TAB_MAPPING);
                        editorTabController.addTab(tab);
                        FILE_MAPPING.put(key, tab);
                        TAB_MAPPING.put(tab, nodes);

                        inputController.replaceContent(fileName);


                    } else {
                        // 点击已经存在的 tab
                        tab = FILE_MAPPING.get(key);
                        editorTabController.setTabStatus(tab, false);

                    }
                    editorController.addInputAndOutput(TAB_MAPPING.get(tab));
                });
            }
        });

        HBox createFile = (HBox) newFileItem.getGraphic();
        initFileItem(createFile, "New", "Ctrl+Insert", null);

        newFileItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popUpContextMenuController.addCreateFilePopUp("New MarkDown", fileTree, path);
            }
        });

        HBox deleteFile = (HBox) deleteFileItem.getGraphic();
        initFileItem(deleteFile, "Delete...", "Delete", null);

        deleteFileItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileName = fileTree.getFocusModel().getFocusedItem().getValue();
                fileService.deleteFile(path, fileName);
                reloadFile();
            }
        });

        HBox renameFile = (HBox) renameFileItem.getGraphic();
        initFileItem(renameFile, "Rename", "Alt+Shift+R", null);


        renameFileItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileName = fileTree.getFocusModel().getFocusedItem().getValue();
                popUpContextMenuController.addRenameFilePopUp("Rename MarkDown", fileTree, path, fileName);
            }
        });



    }

    private void initFileItem(HBox hBox, String leftText, String rightText, String imageUrl) {
        contextMenuController.setMenuHover(hBox);
        contextMenuController.setMenu(hBox, leftText, rightText, imageUrl);
    }

//    @EventListener
//    public void listen(FileEvent fileEvent) {
////        Platform.runLater(() -> {
//        System.err.println(fileEvent);
//        fileTree.getRoot().getChildren().clear();
//        listenFileTree();
////        });
//    }

    public void reloadFile() {
        fileTree.getRoot().getChildren().clear();
        listenFileTree();
    }

    private void listenFileTree() {
        Pair<String, File[]> files = fileService.getFiles(path);
        TreeItem<String> rootNode = new TreeItem<>("Home");
        ImageView imageView = new ImageView(new Image(ImgLoader.getVal("folders-line.png")));
        rootNode.setGraphic(imageView);
        rootNode.setExpanded(true);


        for (File file : files.getValue()) {
            TreeItem<String> treeItem = new TreeItem<>(file.getName().split("\\.")[0]);
            ImageView oneImg = new ImageView(new Image(ImgLoader.getVal("markdown.png")));
            treeItem.setGraphic(oneImg);
            rootNode.getChildren().add(treeItem);
        }

        fileTree.setRoot(rootNode);

    }

}
