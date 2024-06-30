package org.jewel.knight.aquamarine.controller;

import org.jewel.knight.aquamarine.loader.CssLoader;
import org.jewel.knight.aquamarine.loader.FxmlLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2024/1/9 21:39
 */
@Component
public class SettingController implements Initializable {

    private final static String FXML = ".fxml";

    private final static Stage SETTING_STAGE = new Stage();

    private double xOffset = 0;
    private double yOffset = 0;

    public VBox settingContainer;
    public Button closeBtn;
    public Button cancelBtn;

    public TreeView<String> settingTree;

    public HBox settingContentContainer;
    public VBox settingProperty;

    public HBox titleBar;

    @Autowired
    private StageManager stageManager;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 设置模态对话框
        SETTING_STAGE.initStyle(StageStyle.UNDECORATED);
        SETTING_STAGE.initModality(Modality.APPLICATION_MODAL);
        SETTING_STAGE.initOwner(stageManager.getMainStage());
        SETTING_STAGE.setTitle("Setting");
        Scene scene = new Scene(settingContainer, 1000, 730);
        scene.getStylesheets().addAll(CssLoader.getVal(List.of("setting.css", "titleBar.css", "common.css")));
        SETTING_STAGE.setScene(scene);

        setCloseAction();
        setCancelAction();
        initSettingTree();
        setFlexibleMove();
    }

    public void initSettingTree() {
        TreeItem<String> root = new TreeItem<>(null);

        TreeItem<String> appearance = new TreeItem<>("Appearance");
        TreeItem<String> keymap = new TreeItem<>("Keymap");
        TreeItem<String> backupSync = new TreeItem<>("Backup Sync");
        TreeItem<String> chatGPT = new TreeItem<>("ChatGPT");
        root.getChildren().addAll(appearance, keymap, backupSync, chatGPT);
        settingTree.setRoot(root);
        settingTree.setShowRoot(false);

        setSettingItem();
    }

    public void show() {
        SETTING_STAGE.showAndWait();
    }

    private void setFlexibleMove() {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        titleBar.setOnMouseDragged(event -> {
            SETTING_STAGE.setX(event.getScreenX() - xOffset);
            SETTING_STAGE.setY(event.getScreenY() - yOffset);
        });

    }

    private void setSettingItem() {
        settingTree.setOnMouseClicked(event -> {
            String value = convert(settingTree.getFocusModel().getFocusedItem().getValue());
            Parent parent = FxmlLoader.getParent(value, applicationContext);
            if (parent != null) {
                settingProperty.getChildren().clear();
                settingProperty.getChildren().add(parent);
            }
        });
    }

    private void setCancelAction() {
        cancelBtn.setOnAction(event -> {
            hide();
        });
    }

    private void setCloseAction() {
        closeBtn.setOnAction(event -> {
            hide();
        });
    }

    private void hide() {
        SETTING_STAGE.hide();
    }

    private String convert(String name) {
        name = name.toLowerCase() + FXML;
        return name;
    }

}
