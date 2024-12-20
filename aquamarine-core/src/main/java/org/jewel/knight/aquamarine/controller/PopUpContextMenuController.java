package org.jewel.knight.aquamarine.controller;

import javafx.scene.input.KeyEvent;
import org.jewel.knight.aquamarine.service.FileService;
import org.jewel.knight.aquamarine.service.impl.FileServiceImpl;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author impactCn
 * @date 2023/10/5 0:56
 */
@Component
public class PopUpContextMenuController implements Initializable {

    @FXML
    private VBox popUpContainer;

    @FXML
    private Label popUpTitle;
    @FXML
    private TextField popUpInput;

    private double xOffset = 0;
    private double yOffset = 0;

    @Autowired
    private StageManager stageManager;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileManageController fileManageController;

    private static final Popup FLOATING_POPUP = new Popup();

    private static final double POPUP_WIDTH = 390;

    private static final double POPUP_HEIGHT = 70;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FLOATING_POPUP.setWidth(POPUP_WIDTH);
        FLOATING_POPUP.setHeight(POPUP_HEIGHT);
        FLOATING_POPUP.setAutoHide(true);
        FLOATING_POPUP.getContent().add(popUpContainer);


        popUpContainer.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FLOATING_POPUP.setX(mouseEvent.getScreenX() - xOffset);
                FLOATING_POPUP.setY(mouseEvent.getScreenY() - yOffset);
            }
        });

        popUpContainer.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });


    }




    /**
     * 创建文件的 popUp
     * @param title
     * @param node
     * @param path
     */
    public void addCreateFilePopUp(String title, Node node, String path) {

        popUpInput.setText("");
        popUpTitle.setText(title);
        Pair<Double, Double> centerPoint = stageManager.getCenterPoint();
        FLOATING_POPUP.show(node,  centerPoint.getKey() - POPUP_WIDTH / 2, centerPoint.getValue() - POPUP_HEIGHT / 2);

        // 按 enter 确定
        popUpInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals(KeyCode.ENTER.getName())) {

                fileService.createFile(path, FileServiceImpl.MD, popUpInput.getText());

                // 关闭 popup
                FLOATING_POPUP.hide();
                fileManageController.reloadFile();

            }
        });

    }

    public void addRenameFilePopUp(String title, Node node, String path, String name) {
        popUpInput.setText(name);
        // #214283
        popUpInput.selectAll();

//        popUpInput.setAccessibleText(name);
        popUpTitle.setText(title);
        Pair<Double, Double> centerPoint = stageManager.getCenterPoint();
        FLOATING_POPUP.show(node, centerPoint.getKey() - POPUP_WIDTH / 2, centerPoint.getValue() - POPUP_HEIGHT / 2);

        popUpInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().getName().equals(KeyCode.ENTER.getName())) {

                fileService.rename(path, name, popUpInput.getText());

                // 关闭 popup
                FLOATING_POPUP.hide();
                fileManageController.reloadFile();
            }
        });
    }



}
